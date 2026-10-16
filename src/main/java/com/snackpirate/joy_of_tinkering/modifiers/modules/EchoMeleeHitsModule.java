package com.snackpirate.joy_of_tinkering.modifiers.modules;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public record EchoMeleeHitsModule(LevelingValue chance, LevelingValue damage, LevelingInt maxTargets) implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter {

	public static final RecordLoadable<EchoMeleeHitsModule> LOADER = RecordLoadable.create(
			LevelingValue.LOADABLE.requiredField("chance", EchoMeleeHitsModule::chance),
			LevelingValue.LOADABLE.requiredField("damage_mult", EchoMeleeHitsModule::damage),
			LevelingInt.LOADABLE.requiredField("max_targets", EchoMeleeHitsModule::maxTargets),
			EchoMeleeHitsModule::new);

	public static final List<ModuleHook<?>> HOOKS = HookProvider.<EchoMeleeHitsModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
		if (context.getLivingTarget() == null || context.isExtraAttack() || context.getAttacker().getRandom().nextFloat() > chance.compute(modifier.getLevel())) {
//			JoyOfTinkering.LOGGER.info("null check");
			return;
		}

		LivingEntity target = context.getLivingTarget();
		List<LivingEntity> entities = context.getLevel().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3, 2, 3));
		entities.remove(target);
		entities.remove(context.getAttacker());
		entities.sort((entity1, entity2) -> { //sort by closest
			double comp = entity1.distanceToSqr(target) - entity2.distanceToSqr(target);
			if (comp < 0) return -1;
			else if (comp > 0) return 1;
			else return 0;
		});
		//less damage and knockback
		ToolAttackContext modifiedContext = ToolAttackContext.attacker(context.getAttacker(), context.getPlayerAttacker())
				.hand(context.getHand())
				.slot(context.getSlotType(), context.getHand())
				.projectile(context.getProjectile())
				.target(context.getTarget(), context.getLivingTarget())
				.baseDamage(context.getBaseDamage() * damage.compute(modifier.getLevel()))
				.baseKnockback(context.getBaseKnockback() / 2)
				.cooldown(context.getCooldown())
				.extraAttack().sound(context.getSound()).build();
		for (int i = 0, targetsHit = 0; i < entities.size() && targetsHit < maxTargets.compute(modifier.getLevel()); i++) {
			if (tool.isBroken()) {
				break;
			}
			LivingEntity targetI = entities.get(i);
//			JoyOfTinkering.LOGGER.info("target {}: {}", i, targetI.getType());
			if (!targetI.hasEffect(JOTEffects.echoing.get())) continue;
			if (!context.getLevel().isClientSide()) ((ServerLevel) context.getLevel()).sendParticles(new VibrationParticleOption(new EntityPositionSource(targetI, targetI.getBbHeight() / 2f), 5), target.getX(), target.getY() + (target.getBbHeight()/2d), target.getZ(), 1, 0, 0, 0, 0);
			//			DamageSource pSource = context.getAttacker().damageSources().playerAttack(context.getPlayerAttacker());
			if (!context.getAttacker().isAlliedTo(targetI) && ToolAttackUtil.isAttackable(context.getAttacker(), targetI)
					&& !(targetI instanceof ArmorStand stand && stand.isMarker())) {
				targetsHit++;
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						ToolAttackUtil.performAttack(tool, modifiedContext.withAOETarget(targetI, targetI));
					}
				}, 250);
			}
		}
	}

	@Override
	public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
		if (context.getLivingTarget() == null || context.isExtraAttack() || context.getAttacker().getRandom().nextFloat() > chance.compute(modifier.getLevel())) {
//			JoyOfTinkering.LOGGER.info("null check");
			return;
		}

		LivingEntity target = context.getLivingTarget();
		List<LivingEntity> entities = context.getLevel().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3, 2, 3));
		entities.remove(target);
		entities.remove(context.getAttacker());
		entities.sort((entity1, entity2) -> { //sort by closest
			double comp = entity1.distanceToSqr(target) - entity2.distanceToSqr(target);
			if (comp < 0) return -1;
			else if (comp > 0) return 1;
			else return 0;
		});
		//less damage and knockback
		ToolAttackContext modifiedContext = ToolAttackContext.attacker(context.getAttacker(), context.getPlayerAttacker())
				.hand(context.getHand())
				.slot(context.getSlotType(), context.getHand())
				.projectile(context.getProjectile())
				.target(context.getTarget(), context.getLivingTarget())
				.baseDamage(context.getBaseDamage() * damage.compute(modifier.getLevel()))
				.baseKnockback(context.getBaseKnockback() / 2)
				.cooldown(context.getCooldown())
				.extraAttack().sound(context.getSound()).build();
		for (int i = 0, targetsHit = 0; i < entities.size() && targetsHit < maxTargets.compute(modifier.getLevel()); i++) {
			if (tool.isBroken()) {
				break;
			}
			LivingEntity targetI = entities.get(i);
//			JoyOfTinkering.LOGGER.info("target {}: {}", i, targetI.getType());
			if (!targetI.hasEffect(JOTEffects.echoing.get())) continue;
			if (!context.getLevel().isClientSide()) ((ServerLevel) context.getLevel()).sendParticles(new VibrationParticleOption(new EntityPositionSource(targetI, targetI.getBbHeight() / 2f), 5), target.getX(), target.getY() + (target.getBbHeight()/2d), target.getZ(), 1, 0, 0, 0, 0);
			//			DamageSource pSource = context.getAttacker().damageSources().playerAttack(context.getPlayerAttacker());
			if (!context.getAttacker().isAlliedTo(targetI) && ToolAttackUtil.isAttackable(context.getAttacker(), targetI)
					&& !(targetI instanceof ArmorStand stand && stand.isMarker())) {
				targetsHit++;
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						ToolAttackUtil.performAttack(tool, modifiedContext.withAOETarget(targetI, targetI));
					}
				}, 250);
			}
		}
	}
}
