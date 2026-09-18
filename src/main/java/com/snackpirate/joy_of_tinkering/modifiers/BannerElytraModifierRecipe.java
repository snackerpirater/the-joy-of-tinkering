package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.data.JOTRecipes;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
* Most of this copied from BannerModifierRecipe
* */
public class BannerElytraModifierRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {

	private final ResourceLocation id;
	public BannerElytraModifierRecipe(ResourceLocation id) {
		this.id = id;
		ModifierRecipeLookup.addRecipeModifier(null, JOTModifierIds.elytraBanner);
	}


	@Override
	public boolean matches(ITinkerStationContainer inv, Level world) {
		// ensure this modifier can be applied
		if (!inv.getTinkerableStack().is(TinkerTags.Items.CHESTPLATES) || inv.getTinkerable().getModifierLevel(ModifierIds.wings) < 1) {
			return false;
		}
		// slots must be only banner
		boolean found = false;
		for (int i = 0; i < inv.getInputCount(); i++) {
			ItemStack input = inv.getInput(i);
			if (!input.isEmpty()) {
				if (input.getItem() instanceof BannerItem) {
					if (found) {
						// multiple banners
						return false;
					}
					found = true;
				} else {
					// non-banner input
					return false;
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

		// get the banner data
		CompoundTag bannerData = BlockItem.getBlockEntityData(banner);
		ListTag patterns = new ListTag();
		if (bannerData != null) {
			patterns = bannerData.getList("Patterns", Tag.TAG_COMPOUND);
		}

		// apply the pattern
		BannerModule.copyPatterns(tool.getPersistentData(), key, dye, patterns);

		// add the modifier if missing
		if (tool.getModifierLevel(key) == 0) {
			tool.addModifier(key, 1);
		}
		return ITinkerStationRecipe.success(tool, inv);
	}
	@Nullable
	private List<IDisplayModifierRecipe> displayRecipes;
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
			if (!toolInputs.isEmpty()) {
				ResourceLocation id = getId();
				displayRecipes = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.BANNERS)
						.flatMap(item -> {
							if (item instanceof BannerItem banner) {
								return Stream.of(new DisplayRecipe(id, toolInputs, banner));
							}
							return Stream.empty();
						}).collect(Collectors.toList());
			} else {
				displayRecipes = List.of();
			}
		}
		return displayRecipes;
	}
	private static class DisplayRecipe implements IDisplayModifierRecipe {
		private static final IntRange LEVELS = new IntRange(1, 1);
		private final ModifierEntry RESULT = new ModifierEntry(JOTModifierIds.elytraBanner, 1);

		private final ResourceLocation recipeId;
		private final List<ItemStack> banner;
		private final List<ItemStack> toolWithoutModifier;
		private final List<ItemStack> toolWithModifier;
		private final Component variant;
		public DisplayRecipe(ResourceLocation recipeId, List<ItemStack> tools, BannerItem banner) {
			this.recipeId = recipeId;
			this.toolWithoutModifier = tools;
			this.banner = List.of(new ItemStack(banner));
			DyeColor dye = banner.getColor();
			this.variant = Component.translatable("color.minecraft." + dye.getSerializedName());

			ModifierId key = RESULT.getId();
			ListTag patterns = new ListTag();
			List<ModifierEntry> results = List.of(RESULT);
			toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, DEFAULT_TOOL_STACK_SIZE, results, data -> BannerModule.copyPatterns(data, key, dye, patterns))).toList();
		}

		@Override
		public ModifierEntry getDisplayResult() {
			return RESULT;
		}

		@Override
		public int getInputCount() {
			return 1;
		}

		@Override
		public List<ItemStack> getDisplayItems(int slot) {
			if (slot == 0) {
				return banner;
			}
			return List.of();
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
	}
}
