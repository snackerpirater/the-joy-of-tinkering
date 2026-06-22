package com.snackpirate.joy_of_tinkering.modifiers.hook;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface FluidCollisionModifierHook {
	boolean shouldCollide(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, EquipmentSlot slot, Fluid fluid);

	record AnyMerger(Collection<FluidCollisionModifierHook> modules) implements FluidCollisionModifierHook {
		@Override
		public boolean shouldCollide(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, EquipmentSlot slot, Fluid fluid) {
			for (FluidCollisionModifierHook hook : modules) {
				if (hook.shouldCollide(tool, modifier, entity, slot, fluid)) return true;
			}
			return false;
		}
	}
}
