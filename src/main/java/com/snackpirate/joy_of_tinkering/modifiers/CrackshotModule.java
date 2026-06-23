package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.data.tags.JOTEntityTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ConditionalStatTooltip;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public record CrackshotModule(IJsonPredicate<LivingEntity> holder, LevelingValue powerBoost) implements ModifierModule, ProjectileHitModifierHook, ConditionalStatTooltip {
	private static final List<ModuleHook<?>> HOOKS = HookProvider.<CrackshotModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
	public static final RecordLoadable<CrackshotModule> LOADER = RecordLoadable.create(
			LivingEntityPredicate.LOADER.defaultField("entity", CrackshotModule::holder),
			LevelingValue.LOADABLE.requiredField("power_boost", CrackshotModule::powerBoost),
			CrackshotModule::new
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
	public IJsonPredicate<LivingEntity> holder() {
		return holder;
	}

	@Override
	public INumericToolStat<?> stat() {
		return ToolStats.PROJECTILE_DAMAGE;
	}

	@Override
	public boolean percent() {
		return true;
	}

	@Override
	public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
		return 1 + powerBoost.compute(entry.getLevel());
	}

	@Override
	public ModifierCondition<IToolStackView> condition() {
		return ModifierCondition.ANY_TOOL;
	}

	@Override
	public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
		if (target != null && attacker != null && holder.matches(attacker)) {
//			JoyOfTinkering.LOGGER.info("projectile hit {} at {}", target.getType().getDescriptionId(), projectile.position());
			if (!checkHeadshot(projectile, hit)) return false;
			if (projectile instanceof ProjectileWithPower bullet) {
				bullet.setPower(bullet.getPower() * (1 + powerBoost.compute(modifier.getLevel())));
			}
			else if (projectile instanceof AbstractArrow arrow) {
				arrow.setBaseDamage(arrow.getBaseDamage() * (1 + powerBoost.compute(modifier.getLevel())));
			}
			attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
		}
		return false;
	}
	private static boolean checkHeadshot(Projectile projectile, EntityHitResult hit) {
		Entity entity = hit.getEntity();
		AABB headHitbox = null;
		if (entity.getType().is(JOTEntityTags.WEAKPOINT_TOP)) {
//			double diff = hit.getLocation().y - entity.getEyeY();
//			double headHalf = Math.abs(entity.getBbHeight() - entity.getEyeHeight());
			return projectile.getPosition(1.5f).add(projectile.getDeltaMovement()).y() > entity.getBoundingBox().maxY - entity.getBoundingBox().getXsize();
//			return (diff < 0 && -diff <= headHalf || diff >= 0 && diff < headHalf + 0.1f);
		}
//		else if (entity.getType().is(JOTEntityTags.WEAKPOINT_SIDE)) {
//			headHitbox = entity.getRot()
//		}
		Vec3 vec = new Vec3(projectile.getX(), projectile.getY(), projectile.getZ());
//		JoyOfTinkering.LOGGER.info("checking {} in {}", vec, headHitbox);
		if (headHitbox == null) return false;
		return headHitbox.intersects(projectile.getBoundingBox());
	}
}
