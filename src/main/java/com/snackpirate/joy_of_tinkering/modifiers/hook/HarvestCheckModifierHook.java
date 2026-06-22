package com.snackpirate.joy_of_tinkering.modifiers.hook;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface HarvestCheckModifierHook {
	boolean canHarvest(IToolStackView tool, ModifierEntry entry, LivingEntity entity, EquipmentSlot slot, final PlayerEvent.HarvestCheck event);

	record AnyMerger(Collection<HarvestCheckModifierHook> module) implements HarvestCheckModifierHook {
		@Override
		public boolean canHarvest(IToolStackView tool, ModifierEntry entry, LivingEntity entity, EquipmentSlot slot, PlayerEvent.HarvestCheck event) {
			for (HarvestCheckModifierHook hook: module) {
				if (hook.canHarvest(tool, entry, entity, slot, event)) return true;
			}
			return false;
		}
	}
}
