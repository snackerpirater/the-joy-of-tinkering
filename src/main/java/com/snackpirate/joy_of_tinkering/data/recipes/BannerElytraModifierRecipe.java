package com.snackpirate.joy_of_tinkering.data.recipes;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.display.RecipeSlot;
import slimeknights.tconstruct.library.recipe.display.RecipeSlots;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;
import slimeknights.tconstruct.tools.recipe.BannerModifierRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
* Most of this copied from BannerModifierRecipe
* */
public class BannerElytraModifierRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {
	private static final RecipeResult<LazyToolStack> NO_PATTERNS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "banner.clear.no_patterns"));
	public static final RecordLoadable<BannerElytraModifierRecipe> LOADER = RecordLoadable.create(
			ContextKey.ID.requiredField(),
			IngredientLoadable.ALLOW_EMPTY.defaultField("clear_input", Ingredient.EMPTY, false, r -> r.clearInput),
			BannerElytraModifierRecipe::new);
	private final Ingredient clearInput;
	private final ResourceLocation id;

	public BannerElytraModifierRecipe(ResourceLocation id, Ingredient clearInput) {
		this.clearInput = clearInput;
		this.id = id;
		ModifierRecipeLookup.addRecipeModifier(null, JOTModifierIds.elytraBanner);
	}


	@Override
	public boolean matches(ITinkerStationContainer inv, Level world) {
		// ensure this modifier can be applied
		//ModifierRequirements doesn't automatically exclude non-elytras so it's here i guess
		if (!inv.getTinkerableStack().is(TinkerTags.Items.CHESTPLATES)) {
			return false;
		}
		// slots must be only banner
		boolean found = false;
		boolean clear = false;
		for (int i = 0; i < inv.getInputCount(); i++) {
			ItemStack input = inv.getInput(i);
			if (!input.isEmpty()) {
				if (!input.isEmpty()) {
					if (clearInput.test(input)) {
						// multiple clears
						if (clear) return false;
						clear = true;
					} else if (input.getItem() instanceof BannerItem) {
						if (found) return false;
						found = true;
					} else {
						// non-banner input
						return false;
					}
				}
			}
		}
		return found;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return JOTRecipes.elytraBannerModifierSerializer.get();
	}


	@Override
	public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
		ToolStack tool = inv.getTinkerable().copy();

		ModDataNBT persistentData = tool.getPersistentData();
		ModifierId key = JOTModifierIds.elytraBanner.getId();

		// locate the banner
		ItemStack banner = ItemStack.EMPTY;
		DyeColor dye = DyeColor.BLACK;
		for (int i = 0; i < inv.getInputCount(); i++) {
			ItemStack stack = inv.getInput(i);
			if (!stack.isEmpty() && stack.getItem() instanceof BannerItem bannerItem) {
				banner = stack;
				dye = bannerItem.getColor();
				// only need 1
				break;
			}
		}

		// should never happen
		if (banner.isEmpty()) {
			return RecipeResult.pass();
		}
		// remove the dye if clear
		if (clearInput != Ingredient.EMPTY) {
			for (int i = 0; i < inv.getInputCount(); i++) {
				ItemStack stack = inv.getInput(i);
				if (!stack.isEmpty() && clearInput.test(stack)) {
					dye = null;
					break;
				}
			}
		}
		// get the banner data
		ListTag patterns = getBannerPatterns(banner);
		// disallow no patterns when going clear
		if (dye == null && patterns.isEmpty()) {
			return NO_PATTERNS;
		}

		// apply the pattern
		BannerModule.copyPatterns(tool.getPersistentData(), key, dye, patterns);

		// add the modifier if missing
		if (tool.getModifierLevel(key) == 0) {
			tool.addModifier(key, 1);
		}

		Component toolValidation = tool.tryValidate();
		if (toolValidation != null) {
			return RecipeResult.failure(toolValidation);
		}
		return ITinkerStationRecipe.success(tool, inv);
	}

//	@Override
//	public RecipeSerializer<?> getSerializer() {
//		return JOTRecipes.elytraBannerModifierSerializer.get();
//	}

	private static ListTag getBannerPatterns(ItemStack banner) {
		// get the banner data
		CompoundTag bannerData = BlockItem.getBlockEntityData(banner);
		ListTag patterns;
		if (bannerData != null) {
			patterns = bannerData.getList("Patterns", Tag.TAG_COMPOUND);
		} else {
			patterns = new ListTag();
		}
		return patterns;
	}

	@Nullable
	private List<IDisplayModifierRecipe> displayRecipes;

	private static CompoundTag createDisplayPatternTag(BannerPattern pattern, DyeColor color) {
		ListTag singlePattern = new ListTag();
		CompoundTag patternTag = new CompoundTag();
		patternTag.putString("Pattern", pattern.getHashname());
		patternTag.putInt("Color", color.getId());
		singlePattern.add(patternTag);

		// create NBT for the banner stacks
		CompoundTag blockEntityData = new CompoundTag();
		blockEntityData.put("Patterns", singlePattern);
		BlockEntity.addEntityType(blockEntityData, BlockEntityType.BANNER);
		CompoundTag stackTag = new CompoundTag();
		stackTag.put("BlockEntityTag", blockEntityData);
		return stackTag;
	}

	@Override
	public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
		if (displayRecipes == null) {
			List<ItemStack> toolInputs = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.CHESTPLATES)
					.map(item -> {
						ItemStack stack = IModifiableDisplay.getDisplayStack(item);
						if (stack.getMaxStackSize() > 1) {
							stack = stack.copyWithCount(Math.min(stack.getMaxStackSize(), DEFAULT_TOOL_STACK_SIZE));
						}
						return stack;
					}).toList();
			List<ItemStack> banners = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.BANNERS)
					.filter(item -> item instanceof BannerItem)
					.map(ItemStack::new).toList();
			if (!toolInputs.isEmpty()) {
				ResourceLocation id = getId();
				List<IDisplayModifierRecipe> recipes = new ArrayList<>(clearInput != Ingredient.EMPTY ? 2 : 1);
				recipes.add(new DisplayRecipe(id, toolInputs, banners, List.of()));

				if (clearInput != Ingredient.EMPTY) {
					// we want a pattern on it to make it more clear what it does
					// but put a white pattern on the black banner for visibility
					CompoundTag stackTag, stackTagBlack;
					BannerPattern cross = BuiltInRegistries.BANNER_PATTERN.get(BannerPatterns.CROSS);
					if (cross != null) {
						stackTag = createDisplayPatternTag(cross, DyeColor.BLACK);
						stackTagBlack = createDisplayPatternTag(cross, DyeColor.WHITE);
					} else {
						stackTag = null;
						stackTagBlack = null;
					}
					// add the recipe to the end of the stream
					List<ItemStack> bannersWithPattern = banners.stream().map(stack -> {
						stack = stack.copy();
						stack.setTag(stack.getItem() == Items.BLACK_BANNER ? stackTagBlack : stackTag);
						return stack;
					}).toList();
					recipes.add(new DisplayRecipe(id, toolInputs, bannersWithPattern, List.of(clearInput.getItems())));
				}
				displayRecipes = List.copyOf(recipes);
			}
		}
		return displayRecipes;
	}

	private static class DisplayRecipe implements IDisplayModifierRecipe {
		private static final IntRange LEVELS = new IntRange(1, 1);
		private final ModifierEntry RESULT = new ModifierEntry(JOTModifierIds.elytraBanner, 1);

		private final ResourceLocation recipeId;
		private final List<ItemStack> banner;
		private final List<ItemStack> clearInput;
		private final List<ItemStack> toolWithoutModifier;
		private final List<ItemStack> toolWithModifier;
		private final Component variant;

		public DisplayRecipe(ResourceLocation recipeId, List<ItemStack> tools, List<ItemStack> banners, List<ItemStack> clearInput) {
			this.recipeId = recipeId;
			this.toolWithoutModifier = tools;
			this.banner = banners;
			this.clearInput = clearInput;
			if (!clearInput.isEmpty()) {
				this.variant = TConstruct.makeTranslation("recipe", "banner.clear");
			} else {
				this.variant = TConstruct.makeTranslation("recipe", "banner.solid");
			}
			// build tools with modifier
			List<ModifierEntry> results = List.of(RESULT);
			ModifierId key = RESULT.getId();
			// apply a default pattern to the result tools for the sake of offbrand JEI. You will never see it in real JEI
			DyeColor defaultColor = clearInput.isEmpty() ? DyeColor.WHITE : null;
			ListTag defaultPatterns = getBannerPatterns(banners.get(0));
			toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, DEFAULT_TOOL_STACK_SIZE, results, data -> BannerModule.copyPatterns(data, key, defaultColor, defaultPatterns))).toList();
		}

		@Override
		public ModifierEntry getDisplayResult() {
			return RESULT;
		}

		@Override
		public int getInputCount() {
			return clearInput.isEmpty() ? 1 : 2;
		}

		@Override
		public List<ItemStack> getDisplayItems(int slot) {
			if (slot == 0) {
				return banner;
			}
			if (slot == 1) {
				return clearInput;
			}
			return List.of();
		}

		@Override
		public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) { if (slot == 0 && !focusOutput && !focus.isEmpty() && focus.getItem() instanceof BannerItem
				// skip using it if the example pattern lacks patterns, as that won't give a useful result
				&& (clearInput.isEmpty() || !getBannerPatterns(focus).isEmpty())) {
			return List.of(focus.copyWithCount(1));
		}
			return getDisplayItems(slot);
		}

		@Override
		public List<ItemStack> getToolWithoutModifier() {
			return toolWithoutModifier;
		}

		@Override
		public List<ItemStack> getToolWithModifier() {
			return toolWithModifier;
		}

		@Override
		public IntRange getLevel() {
			return LEVELS;
		}

		@Override
		public boolean isTool(ItemStack check) {
			return check.is(TinkerTags.Items.CHESTPLATES) && ModifierUtil.getModifierLevel(check, ModifierIds.wings) > 0;
		}

		@Override
		public @org.jetbrains.annotations.Nullable Component canApply(IToolStackView tool) {
			if (tool.getModifierLevel(ModifierIds.wings) < 1) return Component.translatable("modifier.joy_of_tinkering.elytra_banner.requirement");
			return null;
		}

		@Override
		public void applyModifier(ToolStack tool) {
			ModifierId modifier = JOTModifierIds.elytraBanner.getId();
			BannerModule.copyPatterns(tool.getPersistentData(), modifier, clearInput.isEmpty() ? DyeColor.WHITE : null, getBannerPatterns(banner.get(0)));

			// add the modifier if missing
			if (tool.getModifierLevel(modifier) == 0) {
				tool.addModifier(modifier, 1);
			}
		}

		@Override
		public boolean isSlotsDynamic() {
			return true;
		}

		@Override
		public boolean shouldDisplayValidate() {
			return false;
		}
		@Override
		public void onDisplayUpdate(RecipeSlot<ItemStack> toolSlot, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output) {
			// add banner to the currently displayed tool from the currently displayed banner
			ItemStack bannerStack = inputs.get(0);
			// apply banner to display tool
			ItemStack toolStack = toolSlot.get();
			if (!toolStack.isEmpty() && bannerStack.getItem() instanceof BannerItem banner2) {
				ListTag patterns = getBannerPatterns(bannerStack);
				// if clear, skip the color. Still better that we have a BannerItem though
				DyeColor dye = clearInput.isEmpty() ? banner2.getColor() : null;
				ToolStack tool = ToolStack.copyFrom(toolStack);
				ModifierId modifier = RESULT.getId();
				BannerModule.copyPatterns(tool.getPersistentData(), modifier, dye, patterns);

				// add the modifier if missing
				if (tool.getModifierLevel(modifier) == 0) {
					tool.addModifier(modifier, 1);
				}
				// build the display stack
				output.set(tool.copyStack(toolStack));
			}
		}

	}
}
