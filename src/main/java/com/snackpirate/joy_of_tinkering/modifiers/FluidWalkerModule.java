package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.modifiers.hook.FluidCollisionModifierHook;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record FluidWalkerModule(IJsonPredicate<BlockState> fluidPredicate, LevelingInt velocityThreshold, float damageChance) implements ModifierModule, EquipmentChangeModifierHook, FluidCollisionModifierHook, ArmorWalkModifierHook {
	public static final List<ModuleHook<?>> HOOKS = HookProvider.<FluidWalkerModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, JOTModifierHooks.FLUID_COLLISION, ModifierHooks.BOOT_WALK);
	public static final RecordLoadable<FluidWalkerModule> LOADER = RecordLoadable.create(
			BlockPredicate.LOADER.requiredField("fluid_predicate", FluidWalkerModule::fluidPredicate),
			LevelingInt.LOADABLE.requiredField("velocity_threshold", FluidWalkerModule::velocityThreshold),
			FloatLoadable.FROM_ZERO.requiredField("damage_chance", FluidWalkerModule::damageChance),
			FluidWalkerModule::new
	);
	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}

	@Override
	public boolean shouldCollide(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, EquipmentSlot slot, Fluid fluid) {
		int speedValue = entity.isCrouching() ? 0 : entity.isSprinting() ? 2 : 1;
		return velocityThreshold.compute(modifier.getLevel()) <= speedValue && fluidPredicate.matches(fluid.defaultFluidState().createLegacyBlock());
	}


	@Override
	public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
		Level level = living.level();
		if (tool.isBroken() || !living.onGround() || level.isClientSide) {
			return;
		}
		if (fluidPredicate.matches(living.getBlockStateOn())) {
			living.level().playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.SLIME_JUMP_SMALL, SoundSource.PLAYERS, 0.4F, 1.7F / (level.getRandom().nextFloat() * 0.4F + 1.2F));
			// damage boots
			if (level.random.nextFloat() < damageChance) {
				ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
			}
		}
	}
}
