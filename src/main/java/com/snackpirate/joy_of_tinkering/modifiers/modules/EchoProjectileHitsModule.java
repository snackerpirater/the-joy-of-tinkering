package com.snackpirate.joy_of_tinkering.modifiers.modules;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public record EchoProjectileHitsModule(LevelingValue chance, LevelingValue damageMult, LevelingInt maxTargets) implements ModifierModule, ProjectileHitModifierHook {

    public static final RecordLoadable<EchoProjectileHitsModule> LOADER = RecordLoadable.create(
            LevelingValue.LOADABLE.requiredField("chance", EchoProjectileHitsModule::chance),
            LevelingValue.LOADABLE.requiredField("damage_mult", EchoProjectileHitsModule::damageMult),
            LevelingInt.LOADABLE.requiredField("max_targets", EchoProjectileHitsModule::maxTargets),
            EchoProjectileHitsModule::new);

    public static final List<ModuleHook<?>> HOOKS = HookProvider.<EchoProjectileHitsModule>defaultHooks(ModifierHooks.PROJECTILE_HIT);

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
//        JoyOfTinkering.LOGGER.info("entity hit 1");
        if (target == null || target.getRandom().nextFloat() > chance.compute(modifier.getLevel())) {
//			JoyOfTinkering.LOGGER.info("null check");
            return false;
        }
//        JoyOfTinkering.LOGGER.info("entity hit 2");
        Level level = target.level();
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3, 2, 3));
        entities.remove(target);
        if (attacker!=null) entities.remove(attacker);
        entities.sort((entity1, entity2) -> { //sort by closest
            double comp = entity1.distanceToSqr(target) - entity2.distanceToSqr(target);
            if (comp < 0) return -1;
            else if (comp > 0) return 1;
            else return 0;
        });
        //less damage and knockback
        DamageSource source = projectile.damageSources().mobProjectile(projectile, attacker);
        float damage;
        if (projectile instanceof AbstractArrow arrow) damage = (float) arrow.getBaseDamage();
        else if (projectile instanceof ProjectileWithPower power) damage = power.getDamage();
        else damage = 5;
        damage*=damageMult.compute(modifier.getLevel());
        for (int i = 0, targetsHit = 0; i < entities.size() && targetsHit < maxTargets.compute(modifier.getLevel()); i++) {
//            JoyOfTinkering.LOGGER.info("entity hit 3");
            LivingEntity targetI = entities.get(i);
//			JoyOfTinkering.LOGGER.info("target {}: {}", i, targetI.getType());
            if (!targetI.hasEffect(JOTEffects.echoing.get())) continue;
//            JoyOfTinkering.LOGGER.info("entity hit 3.5");
            ((ServerLevel) level).sendParticles(new VibrationParticleOption(new EntityPositionSource(targetI, targetI.getBbHeight() / 2f), 5), target.getX(), target.getY() + (target.getBbHeight()/2d), target.getZ(), 1, 0, 0, 0, 0);
            //			DamageSource pSource = context.getAttacker().damageSources().playerAttack(context.getPlayerAttacker());
            if ((attacker == null || !attacker.isAlliedTo(targetI))
                    && !(targetI instanceof ArmorStand stand && stand.isMarker())) {
//                JoyOfTinkering.LOGGER.info("entity hit 4");
                targetsHit++;
                float finalDamage = damage;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        targetI.hurt(source, finalDamage);
                    }
                }, 250);
            }
        }
        return false;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        if (projectile.level().getRandom().nextFloat() > chance.compute(modifier.getLevel())) {
//			JoyOfTinkering.LOGGER.info("null check");
            return false;
        }

        Level level = projectile.level();
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(3, 2, 3));
//        entities.remove(target);
        if (owner!=null) entities.remove(owner);
        entities.sort((entity1, entity2) -> { //sort by closest
            double comp = entity1.distanceToSqr(projectile) - entity2.distanceToSqr(projectile);
            if (comp < 0) return -1;
            else if (comp > 0) return 1;
            else return 0;
        });
        //less damage and knockback
        DamageSource source = projectile.damageSources().mobProjectile(projectile, owner);
        float damage;
        if (projectile instanceof AbstractArrow arrow) damage = (float) arrow.getBaseDamage();
        else if (projectile instanceof ProjectileWithPower power) damage = power.getDamage();
        else damage = 5;
        damage*=damageMult.compute(modifier.getLevel());
        for (int i = 0, targetsHit = 0; i < entities.size() && targetsHit < maxTargets.compute(modifier.getLevel()); i++) {
            LivingEntity targetI = entities.get(i);
//			JoyOfTinkering.LOGGER.info("target {}: {}", i, targetI.getType());
            if (!targetI.hasEffect(JOTEffects.echoing.get())) continue;
            if (level.isClientSide()) ((ServerLevel) level).sendParticles(new VibrationParticleOption(new EntityPositionSource(targetI, targetI.getBbHeight() / 2f), 5), projectile.getX(), projectile.getY() + (projectile.getBbHeight()/2d), projectile.getZ(), 1, 0, 0, 0, 0);
            //			DamageSource pSource = context.getAttacker().damageSources().playerAttack(context.getPlayerAttacker());
            if ((owner == null || !owner.isAlliedTo(targetI))
                    && !(targetI instanceof ArmorStand stand && stand.isMarker())) {
                targetsHit++;
                float finalDamage = damage;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        targetI.hurt(source, finalDamage);
                    }
                }, 250);
            }
        }
        return false;
    }

    @Override
    public RecordLoadable<? extends ModifierModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return HOOKS;
    }
}
