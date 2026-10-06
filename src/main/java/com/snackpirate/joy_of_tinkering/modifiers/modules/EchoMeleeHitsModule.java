package com.snackpirate.joy_of_tinkering.modifiers.modules;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
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
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public record EchoMeleeHitsModule(LevelingValue chance, LevelingValue damage, LevelingInt maxTargets) implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter {

	public static final RecordLoadable<EchoMeleeHitsModule> LOADER = RecordLoadable.create(
			LevelingValue.LOADABLE.requiredField("chance", EchoMeleeHitsModule::chance),
			LevelingValue.LOADABLE.requiredField("damage", EchoMeleeHitsModule::damage),
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
		if (context.getLivingTarget() == null || context.getPlayerAttacker() == null || context.isExtraAttack()) {
			JoyOfTinkering.LOGGER.info("null check");
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
		for (int i = 0; i < Math.min(maxTargets.compute(modifier.getLevel()), entities.size()); i++) {
			JoyOfTinkering.LOGGER.info("target {}: {}", i, entities.get(i).getType());
			if (!context.getLevel().isClientSide()) {
				((ServerLevel) context.getLevel()).sendParticles(new VibrationParticleOption(new EntityPositionSource(entities.get(i), entities.get(i).getBbHeight() / 2f), 5), target.getX(), target.getY() + (target.getBbHeight()/2d), target.getZ(), 1, 0, 0, 0, 0);
			}
			int finalI = i;
			DamageSource pSource = context.getAttacker().damageSources().playerAttack(context.getPlayerAttacker());
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					entities.get(finalI).hurt(pSource, damageDealt * damage.compute(modifier.getLevel()));
				}
			}, 250);
		}
	}

	@Override
	public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {

	}
}
