package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingAngleModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.modules.interaction.sling.SlingModule;

import java.util.List;

import static slimeknights.tconstruct.tools.modules.interaction.sling.SlingKnockbackModule.IS_BONKING;
//mostly copied from Bonking, but the melee damage works from helmet here
public record HeadbuttModule(LevelingValue forceMultiplier, float drawtimeMultiplier, float damageMultiplier, IJsonPredicate<LivingEntity> target, ModifierCondition<IToolStackView> condition) implements SlingModule, MeleeHitModifierHook, MeleeDamageModifierHook, DisplayNameModifierHook {
	private static final float RANGE = 2.5F;
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<HeadbuttModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.ARMOR_INTERACT, ModifierHooks.MELEE_HIT, ModifierHooks.MELEE_DAMAGE, ModifierHooks.DISPLAY_NAME);
	public static final RecordLoadable<HeadbuttModule> LOADER = RecordLoadable.create(
			FORCE_FIELD, DRAWTIME_FIELD,
			FloatLoadable.FROM_ZERO.requiredField("damage_multiplier", HeadbuttModule::damageMultiplier),
			TARGET_FIELD, ModifierCondition.TOOL_FIELD,
			HeadbuttModule::new);
	public static final String FORMAT = "modifier.joy_of_tinkering.plus_modifier.type_format";

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}

	@Override
	public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
		if (tool.getPersistentData().getBoolean(IS_BONKING)) {
			return 0;
		}
		return knockback;
	}
	@Override
	public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
		return Component.translatable(FORMAT, name, Util.COMMA_FORMAT.format(tool.getStats().get(ToolStats.ATTACK_DAMAGE) * damageMultiplier)).withStyle(style -> style.withColor(ResourceColorManager.getTextColor(entry.getModifier().getTranslationKey())));
	}

	@Override
	public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
		if (damageMultiplier != 1 && tool.getPersistentData().getBoolean(IS_BONKING)) {
			damage += ConditionalStatModifierHook.getModifiedStat(tool, context.getAttacker(), ToolStats.ATTACK_DAMAGE);
			damage -= (float) context.getAttacker().getAttributeValue(Attributes.ATTACK_DAMAGE);
//			JoyOfTinkering.LOGGER.info("damage {}", damage);
			damage *= damageMultiplier;
//			JoyOfTinkering.LOGGER.info("dage mult'd {}", damage);
		}
		return damage;
	}

	@Override
	public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
		// override to add in attack speed consideration
//		JoyOfTinkering.LOGGER.info("tool use");
		if (!tool.isBroken() && source == InteractionSource.ARMOR && condition.matches(tool, modifier)) {
//			JoyOfTinkering.LOGGER.info("tool use conditio");
			// apply use item speed always
			float speed = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED);
			// for melee weapons, also multiply in attack speed
			if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
				speed *= tool.getStats().get(ToolStats.ATTACK_SPEED);
			}
			tool.getPersistentData().putInt(GeneralInteractionModifierHook.KEY_DRAWTIME, (int)Math.ceil(20f * drawtimeMultiplier / speed));
			GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void sling(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int chargeTime, ModifierEntry activeModifier) {
		Level level = entity.level();
		if (!level.isClientSide && entity instanceof Player player) {
			float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
			if (charge > 0) {
				Vec3 start = player.getEyePosition(1F);
				Vec3 look = player.getLookAngle();
				Vec3 direction = start.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);
				AABB bb = player.getBoundingBox().expandTowards(look.x * RANGE, look.y * RANGE, look.z * RANGE).expandTowards(1, 1, 1);

				EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, start, direction, bb, e -> e instanceof LivingEntity);
				if (hit != null) {
					LivingEntity target = (LivingEntity)hit.getEntity();
					if (this.target.matches(target)) {
						double targetDist = start.distanceToSqr(target.getEyePosition(1F));

						// cancel if there's a block in the way
						BlockHitResult mop = ModifiableItem.blockRayTrace(level, player, ClipContext.Fluid.NONE);
						if (mop.getType() != HitResult.Type.BLOCK || targetDist < mop.getBlockPos().distToCenterSqr(start)) {
							// melee tools also do damage as a treat
							boolean didBonk = false;
							if (damageMultiplier > 0 && ToolAttackUtil.isAttackable(entity, target) && EntityInteractionModifierHook.isMelee(tool)) {
								didBonk = true;
								ModDataNBT data = tool.getPersistentData();
								data.putBoolean(IS_BONKING, true);
								InteractionHand hand = player.getUsedItemHand();
								ToolAttackContext.Builder builder = ToolAttackContext.attacker(entity).target(target).slot(EquipmentSlot.HEAD, InteractionHand.OFF_HAND).cooldown(Math.min(1, charge)).extraAttack();
								if (hand == InteractionHand.MAIN_HAND) {
									builder.applyAttributes();
								} else {
									builder.toolAttributes(tool);
								}
								ToolAttackUtil.performAttack(tool, builder.build());
								data.remove(IS_BONKING);
							}

							// send it flying
							float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
							RandomSource random = player.getRandom();
							float multiplier = charge * this.forceMultiplier.compute(modifier);
							float force = SlingForceModifierHook.modifySlingForce(tool, entity, target, modifier, SlingModule.getPower(tool, player) * multiplier, multiplier);
							Vec3 angle = Vec3.ZERO;
							if (force > 0) {
								// skip the bonk if we lack force, but the attack is fine
								angle = SlingAngleModifierHook.modifySlingAngle(tool, entity, target, modifier, force, multiplier, new Vec3(
										(-look.x + random.nextGaussian() * inaccuracy),
										0,
										(-look.z + random.nextGaussian() * inaccuracy)
								));
								target.knockback(force, angle.x, angle.z);
								if (target instanceof ServerPlayer playerMP) {
									TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(target), playerMP);
								}
								didBonk = true;
							}

							// if we dealt damage or knockback, apply bonk effects
							if (didBonk) {
								// spawn the bonk particle
								ToolAttackUtil.spawnAttackParticle(TinkerTools.axeAttackParticle.get(), player, 0.6d);

								// modifier callbacks
								SlingLaunchModifierHook.afterSlingLaunch(tool, entity, target, modifier, force, multiplier, angle);

								// cooldowns and stuff
								level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, player.getSoundSource(), 1, 0.75f);
								player.causeFoodExhaustion(0.2F);
								player.getCooldowns().addCooldown(tool.getItem(), 3);
								ToolDamageUtil.damageAnimated(tool, 1, entity, entity.getUsedItemHand(), modifier.getId());
								return;
							}
						}
					}
				}
			}
			// play failure sound
			if (ModifierUtil.isActiveModifier(tool, modifier, activeModifier)) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 1f);
			}
		}
	}

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}
}
