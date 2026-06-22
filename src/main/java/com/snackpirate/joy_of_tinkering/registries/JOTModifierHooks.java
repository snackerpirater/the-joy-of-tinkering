package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.modifiers.hook.FluidCollisionModifierHook;
import com.snackpirate.joy_of_tinkering.modifiers.hook.HarvestCheckModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

public class JOTModifierHooks {
	JOTModifierHooks() {}
	public static void init() {}
	public static final ModuleHook<FluidCollisionModifierHook> FLUID_COLLISION = ModifierHooks.register(JoyOfTinkering.id("fluid_collision"), FluidCollisionModifierHook.class, FluidCollisionModifierHook.AnyMerger::new, (tool, modifier, entity, slot, fluid) -> false);
	public static final ModuleHook<HarvestCheckModifierHook> HARVEST_CHECK = ModifierHooks.register(JoyOfTinkering.id("harvest_check"), HarvestCheckModifierHook.class, HarvestCheckModifierHook.AnyMerger::new, (a, b, c, d, e) -> false);
}
