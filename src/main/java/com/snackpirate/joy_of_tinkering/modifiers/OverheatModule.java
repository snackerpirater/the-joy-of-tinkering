package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;
import java.util.List;

public record OverheatModule(LevelingValue time) implements ModifierModule, ProjectileLaunchModifierHook.NoShooter, ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter {
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OverheatModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN, ModifierHooks.PROJECTILE_HIT);
	public static final RecordLoadable<OverheatModule> LOADER = RecordLoadable.create(
			LevelingValue.LOADABLE.requiredField("seconds", OverheatModule::time),
			OverheatModule::new);

	@Override
	public RecordLoadable<OverheatModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}


	/** Sets the target on fire */
	private void setFire(ModifierNBT modifiers, ModifierEntry modifier, Entity target) {
		target.setSecondsOnFire(Math.round(time.compute(modifiers.getLevel(ModifierIds.fiery))) + Math.round(time.compute(modifier.getEffectiveLevel())));
	}

	@Override
	public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
		// vanilla hack: apply fire so the entity drops the proper items on instant kill
		if (context.getAttacker().getItemBySlot(context.getSlotType()).is(TinkerTags.Items.ARMOR)) return knockback;
		if (OverslimeModule.INSTANCE.getAmount(tool) >= 3) {
			LivingEntity target = context.getLivingTarget();
			if (target != null && !target.isOnFire()) {
				target.setRemainingFireTicks(1);
			}
		}
		return knockback;
	}

	@Override
	public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
		// conclusion of vanilla hack: we don't want the target on fire if we did not hit them
		if (context.getAttacker().getItemBySlot(context.getSlotType()).is(TinkerTags.Items.ARMOR)) return;
		LivingEntity target = context.getLivingTarget();
		if (target != null && target.getRemainingFireTicks() == 1) {
			target.clearFire();
		}
	}

	@Override
	public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
		if (context.getAttacker().getItemBySlot(context.getSlotType()).is(TinkerTags.Items.ARMOR)) return;
		if (OverslimeModule.INSTANCE.getAmount(tool) >= 3) {
			setFire(tool.getModifiers(), modifier, context.getTarget());
			OverslimeModule.INSTANCE.addAmount(tool, -3);
		}
	}

	@Override
	public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
		// this is mostly cosmetic as we handle hit time below
		if (OverslimeModule.INSTANCE.getAmount(tool) >= 3) {
			projectile.setSecondsOnFire(100);
			OverslimeModule.INSTANCE.addAmount(tool, -3);
			persistentData.putBoolean(JoyOfTinkering.id("is_overburn"), true);
		}
	}

	@Override
	public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @org.jetbrains.annotations.Nullable LivingEntity attacker, @org.jetbrains.annotations.Nullable LivingEntity target, boolean notBlocked) {
//		return ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target, notBlocked);
		if (persistentData.getBoolean(JoyOfTinkering.id("is_overburn"))) setFire(modifiers, modifier, hit.getEntity());
		return false;
	}

}
