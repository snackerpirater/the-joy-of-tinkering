package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EndrobeInventoryModule extends InventoryModule {

	public static final RecordLoadable<EndrobeInventoryModule> LOADER = RecordLoadable.create(KEY_FIELD, SLOTS_FIELD, LIMIT_FIELD, FILTER_FIELD, PATTERN_FIELD, ModifierCondition.CONTEXT_FIELD, VALIDATION_FIELD, EndrobeInventoryModule::new);

	protected EndrobeInventoryModule(@Nullable ResourceLocation key, LevelingInt slots, LevelingInt slotLimit, IJsonPredicate<Item> filter, @Nullable Pattern pattern, ModifierCondition<IToolContext> condition, IntRange validationLevel) {
		super(key, slots, slotLimit, filter, pattern, condition, validationLevel);
	}

	TagKey<Item>[] armorTags = new TagKey[]{
			Tags.Items.ARMORS_CHESTPLATES,
			Tags.Items.ARMORS_LEGGINGS,
			Tags.Items.ARMORS_BOOTS
	};
	String[] patterns = {
		"chestplate",
		"leggings",
		"boots"
	};

	@Override
	public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
		//if slot = 0, slot%3 = 0 -> chestplate
		return stack.is(armorTags[slot%3]);
	}

	@Nullable
	@Override
	public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
		return !hasStack ? new Pattern(JoyOfTinkering.id(patterns[slot%3])) : null;
	}

	@Override
	public RecordLoadable<EndrobeInventoryModule> getLoader() {
		return LOADER;
	}
	public static EndrobeInventoryModule.Builder builder() {
		return new EndrobeInventoryModule.Builder();
	}

	public static class Builder extends InventoryModule.Builder {
		private Builder() {}

		@Override
		public InventoryModule slots(int base, int perLevel) {
			return new EndrobeInventoryModule(key, new LevelingInt(base, perLevel), slotLimit, filter, pattern, condition, validationLevel);
		}
	}
}
