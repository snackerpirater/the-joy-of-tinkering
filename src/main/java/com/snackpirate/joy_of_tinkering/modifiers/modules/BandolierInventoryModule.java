package com.snackpirate.joy_of_tinkering.modifiers.modules;

import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BandolierInventoryModule extends InventoryModule {
	protected BandolierInventoryModule(@Nullable ResourceLocation key, LevelingInt slots, LevelingInt slotLimit, IJsonPredicate<Item> filter, @Nullable Pattern pattern, ModifierCondition<IToolContext> condition, IntRange validationLevel) {
		super(key, slots, slotLimit, filter, pattern, condition, validationLevel);
	}
	public static final RecordLoadable<BandolierInventoryModule> LOADER = RecordLoadable.create(KEY_FIELD, SLOTS_FIELD, LIMIT_FIELD, PATTERN_FIELD, ModifierCondition.CONTEXT_FIELD, VALIDATION_FIELD, BandolierInventoryModule::new);

	private BandolierInventoryModule(@javax.annotation.Nullable ResourceLocation key, LevelingInt slots, LevelingInt slotLimit, @javax.annotation.Nullable Pattern pattern, ModifierCondition<IToolContext> condition, IntRange validationLevel) {
		super(key, slots, slotLimit, ItemPredicate.ANY, pattern, condition, validationLevel);
	}

	@Override
	public RecordLoadable<BandolierInventoryModule> getLoader() {
		return LOADER;
	}

	@Override
	public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
		if (condition().matches(tool, modifier)) {
			return stack.is(JOTItems.BULLET.get())
					|| stack.is(JOTItemTags.JUNKSHOT_AMMO) && tool.getModifierLevel(JOTModifierIds.junkshot) > 0;
		}
		return false;
	}


	/* Builder */

	/** Creates a new builder instance */
	public static BandolierInventoryModule.Builder builder() {
		return new BandolierInventoryModule.Builder();
	}

	public static class Builder extends InventoryModule.Builder {
		private Builder() {}

		@Deprecated(forRemoval = true)
		@Override
		public InventoryModule.Builder filter(IJsonPredicate<Item> filter) {
			throw new IllegalStateException("Cannot set filter on QuiverInventoryModule");
		}

		@Override
		public InventoryModule slots(int base, int perLevel) {
			return new BandolierInventoryModule(key, new LevelingInt(base, perLevel), slotLimit, pattern, condition, validationLevel);
		}
	}
}
