package com.snackpirate.joy_of_tinkering.events;

import be.florens.expandability.api.forge.LivingFluidCollisionEvent;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tools.JOTToolDefinitionProvider;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierHooks;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.events.teleport.ReturningTeleportEvent;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.List;

@Mod.EventBusSubscriber(modid = JoyOfTinkering.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JOTServerEvents {
	@SubscribeEvent
	static void bucketPickup(PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getEntity().isHolding(Items.BUCKET) && event.getTarget() instanceof Slime slime && slime.isTiny()) {
			Item bucketItem = switch (slime.getType().getDescriptionId()) {
				case "entity.minecraft.slime" -> JOTItems.BUCKET_OF_SLIME.asItem();
				case "entity.minecraft.magma_cube" -> JOTItems.BUCKET_OF_MAGMA_CUBE.asItem();
				case "entity.tconstruct.terracube" -> JOTItems.BUCKET_OF_TERRACUBE.asItem();
				case "entity.tconstruct.sky_slime" -> JOTItems.BUCKET_OF_SKYSLIME.asItem();
				case "entity.tconstruct.ender_slime" -> JOTItems.BUCKET_OF_ENDERSLIME.asItem();
				default -> Items.BUCKET;
			};
			if (!bucketItem.equals(Items.BUCKET)) {
				event.getLevel().playSound(event.getEntity(), event.getPos(), SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1, 1);
				event.getTarget().remove(Entity.RemovalReason.DISCARDED);

				if (!event.getEntity().level().isClientSide()) {
					if (event.getEntity().getAbilities().instabuild) {
						event.getEntity().addItem(new ItemStack(bucketItem));
					} else {
						InteractionHand hand = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).is(Items.BUCKET) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
						event.getEntity().setItemInHand(hand, new ItemStack(bucketItem));
						//if there's no cooldown, the right click picks up the slime then immediately dumps it back out
						event.getEntity().getCooldowns().addCooldown(bucketItem, 1);
					}
				}
				event.setCanceled(true);
			}
		}
	}

    @SubscribeEvent
    static void livingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		//magmadaptive lavaproofing
        if (entity.hasEffect(JOTEffects.MAGMADAPTATION.get()) && event.getSource().equals(event.getEntity().damageSources().lava())) {
            event.setCanceled(true);
        }
		float originalDamage = event.getAmount();
		DamageSource source = event.getSource();
		//overheat modifier
		if (source.is(TinkerTags.DamageTypes.FIRE_PROTECTION)) {
			int level = TinkerEffect.getLevel(entity, JOTEffects.overheat);
//			JoyOfTinkering.LOGGER.info("overheat level {}", level);
			if (level > 0) {
				originalDamage *= (1 + (level / 5f)); //level 1 = 20%, 2 = 40%, etc
			}
		}
		event.setAmount(originalDamage);
    }

	@SubscribeEvent
	static void magmadaptiveLavaSwimming(PlayerSwimEvent event) {
		if (event.getEntity().hasEffect(JOTEffects.MAGMADAPTATION.get()) && event.getEntity().isInLava()) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent
	static void handleFluidCollisionHook(LivingFluidCollisionEvent event) {
		if (!event.getEntity().getItemBySlot(EquipmentSlot.FEET).is(TinkerTags.Items.BOOTS)) return;
		ToolStack tool = ToolStack.from(event.getEntity().getItemBySlot(EquipmentSlot.FEET));
		for (ModifierEntry entry: tool.getModifierList()) {
			if (entry.getHook(JOTModifierHooks.FLUID_COLLISION).shouldCollide(tool, entry, event.getEntity(), EquipmentSlot.FEET, event.getFluidState().getType())) event.setResult(Event.Result.ALLOW);
		}
	}
	@SubscribeEvent
	static void handleHarvestCheckHook(PlayerEvent.HarvestCheck event) {
		if (!event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(TinkerTags.Items.CHESTPLATES)) return;
		ToolStack tool = ToolStack.from(event.getEntity().getItemBySlot(EquipmentSlot.CHEST));
		for (ModifierEntry entry: tool.getModifierList()) {
			if (entry.getHook(JOTModifierHooks.HARVEST_CHECK).canHarvest(tool, entry, event.getEntity(), EquipmentSlot.CHEST, event)) event.setCanHarvest(true);
		}
	}
	@SubscribeEvent
//			(priority = EventPriority.LOWEST) //runs after tinkers because of chestplate haste
	static void breakSpeedOnChestplates(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		// tool break speed hook
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (!player.hasItemInSlot(EquipmentSlot.MAINHAND) && !player.hasItemInSlot(EquipmentSlot.OFFHAND)) {
			ToolStack tool = ToolStack.from(stack);
			if (tool.getModifierLevel(JOTModifierIds.fistMining) < 1) return;
			if (!tool.isBroken()) {
				List<ModifierEntry> modifiers = tool.getModifierList();
				if (!modifiers.isEmpty()) {
					// build context
					BreakSpeedContext context = new BreakSpeedContext.Event(
							event,
							BlockSideHitListener.getSideHit(event.getEntity()),
							IsEffectiveToolHook.isEffective(tool, event.getState()),
							BreakSpeedContext.getMiningModifier(event.getEntity())
					);

					// run each modifier hook
					float speed = event.getNewSpeed();
					for (ModifierEntry entry : tool.getModifierList()) {
						speed = entry.getHook(ModifierHooks.BREAK_SPEED).modifyBreakSpeed(tool, entry, context, speed);
						// if any modifier cancels mining, stop right here
						if (speed < 0 || event.isCanceled()) {
							break;
						}
					}
					// update the speed
					event.setNewSpeed(speed);
				}
			}
		}

		// armor haste shouldn't affect armor mining, if that makes sense
//		double armorMultiplier = player.getAttributeValue(TinkerAttributes.MINING_SPEED_MULTIPLIER.get()) + player.getAttributeValue(TinkerAttributes.MINING_SPEED_MULTIPLIER.get());
//		if (armorMultiplier >= 0) {
//			event.setNewSpeed((float) (event.getNewSpeed() * armorMultiplier));
//		}
	}
	@SubscribeEvent
	static void blockBreakChestplate(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		// tool break speed hook
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.is(TinkerTags.Items.HARVEST)) {
			ToolStack.from(stack);
			// modifiers using additive boosts may want info on the original boosts provided
			ToolHarvestLogic.mineBlock(stack, event.getPlayer().level(), event.getState(), event.getPos(), event.getPlayer());
		}
	}

	@SubscribeEvent
	static void knockfrontKnockback(LivingKnockBackEvent event) {
		if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(TinkerTags.Items.HELMETS) && ToolStack.from(event.getEntity().getItemBySlot(EquipmentSlot.HEAD)).getModifierLevel(JOTModifierIds.skyward) > 0) {
//			event.setRatioX(-event.getRatioX());
//			event.setRatioZ(-event.getRatioZ());
			event.getEntity().push(0, event.getStrength(), 0);
			event.setStrength(0f);
		}
	}
	@SubscribeEvent
	static void getGunAmmo(LivingGetProjectileEvent event) {
		if (event.getProjectileWeaponItemStack().is(JOTItemTags.MOD_GUNS)) {
//			JoyOfTinkering.LOGGER.info("ammo: {}", event.getProjectileItemStack());
			//if the player is in creative and does not have a bullet in the inventory
			if (event.getEntity() instanceof Player p && p.getAbilities().instabuild && !event.getProjectileItemStack().is(JOTItems.BULLET.get())) {
				ToolStack bullets = ToolStack.createTool(JOTItems.BULLET.get(), JOTToolDefinitionProvider.BULLET, MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.shulker), MaterialRegistry.getMaterial(MaterialIds.copper)));
				ItemStack stack = bullets.createStack();
				stack.setCount(1);
				event.setProjectileItemStack(stack);
			}
		}
	}
//	@SuppressWarnings({"deprecation", "OverrideOnly"})
//	@SubscribeEvent(priority = EventPriority.LOW)
//	static void spawnLavaLoafers(MobSpawnEvent.FinalizeSpawn event) {
//		Mob mob = event.getEntity();
//		EntityType<?> type = mob.getType();
//		ServerLevelAccessor level = event.getLevel();
//		if ((type == EntityType.PIGLIN || type == EntityType.ZOMBIFIED_PIGLIN) && level.getRandom().nextFloat() < 0.075f) {
//			event.setCanceled(true);
//			RandomSource random = level.getRandom();
//			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
//			mob.setItemSlot(EquipmentSlot.FEET, ToolBuildHandler.buildItemRandomMaterials(TAItems.LAVA_LOAFERS.get(), random));
//		}
//	}
}
