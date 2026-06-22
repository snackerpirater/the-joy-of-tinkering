package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.network.GunflingerLaunchPacket;
import com.snackpirate.joy_of_tinkering.network.JOTNetwork;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingAngleModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.tools.TinkerToolActions;

import java.util.List;

public enum GunflingerModule implements ModifierModule, ProjectileLaunchModifierHook {
	INSTANCE;

	public static final RecordLoadable<GunflingerModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<GunflingerModule>defaultHooks(ModifierHooks.PROJECTILE_LAUNCH);



	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}

	@Override
	public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {}

	@Override
	public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
		float multiplier = 1;
		float force = SlingForceModifierHook.modifySlingForce(tool, shooter, shooter, modifier, getPower(tool, shooter) * multiplier, multiplier);
		if (force > 0 && shooter instanceof Player player) {
			Vec3 vec = player.getLookAngle().normalize();
			float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
			RandomSource random = player.getRandom();

			// we fling the inverted player look vector
			Vec3 angle = SlingAngleModifierHook.modifySlingAngle(tool, shooter, shooter, modifier, force, multiplier, new Vec3(
					-(vec.x + random.nextGaussian() * inaccuracy),
					-(vec.y + random.nextGaussian() * inaccuracy) / 1.5f,
					-(vec.z + random.nextGaussian() * inaccuracy)
			));
			player.push(angle.x * force, angle.y * force, angle.z * force);
			JOTNetwork.getInstance().sendTo(new GunflingerLaunchPacket(angle.scale(force)), player);
//			JoyOfTinkering.LOGGER.info("gunflinger proj shoot {}", angle);
			SlimeBounceHandler.addBounceHandler(player);
			// modifier callbacks
			SlingLaunchModifierHook.afterSlingLaunch(tool, player, player, modifier, force, multiplier, angle);

			if (!player.level().isClientSide) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1, 1);
				player.causeFoodExhaustion(0.2F);
				player.getCooldowns().addCooldown(tool.getItem(), 7);
				ToolDamageUtil.damageAnimated(tool, 1, player);
			}
			// apply drill attack if the modifier is present
			if (ModifierUtil.canPerformAction(tool, TinkerToolActions.DRILL_ATTACK)) {
				player.startAutoSpinAttack(20);
			}
			projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.5));
		}
	}

	private float getPower(IToolStackView tool, LivingEntity living) {
		float power = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE);
		float velo = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
		return (0.5f*power) + (0.5f*velo);
	}

}
