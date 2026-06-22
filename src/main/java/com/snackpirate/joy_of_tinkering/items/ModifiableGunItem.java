package com.snackpirate.joy_of_tinkering.items;

import com.snackpirate.joy_of_tinkering.JOTSounds;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.entity.ModifiableBullet;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableShurikenItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook.KEY_DRAWTIME;

public class ModifiableGunItem extends ModifiableLauncherItem {
	public static final ResourceLocation GUN_AMMO = JoyOfTinkering.id("gun_ammo");
	private static final String PROJECTILE_KEY = "item.minecraft.crossbow.projectile";
	private static final boolean storeDrawingItem = false;
	public ModifiableGunItem(Properties properties, ToolDefinition toolDefinition) {
		super(properties, toolDefinition);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return ModifierUtil.canPerformAction(ToolStack.from(pStack), ToolActions.SHIELD_BLOCK) ? UseAnim.BLOCK : UseAnim.BOW;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return (stack -> stack.is(JOTItems.BULLET.get()));
	}

	@Override
	public int getDefaultProjectileRange() {
		return 8;
	}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
		consumer.accept(GunArmPose.INSTANCE);
    }
	static int getMaxAmmo(ToolStack gun, LivingEntity entity) {
		return (int) ConditionalStatModifierHook.getModifiedStat(gun, entity, JOTToolStats.MAX_AMMO);
	}
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack gun = player.getItemInHand(hand);

		ToolStack tool = ToolStack.from(gun);
		if (tool.isBroken()) {
			return InteractionResultHolder.fail(gun);
		}

		// yeah, its hardcoded, I cannot see a need to not hardcode this, request it if you need it
		boolean sinistral = hand == InteractionHand.MAIN_HAND && tool.getModifierLevel(JOTModifierIds.southpaw) > 0;

		// no ammo? not charged
		ModDataNBT persistentData = tool.getPersistentData();
		ListTag heldAmmo = (ListTag) persistentData.get(GUN_AMMO);
		if (heldAmmo == null || heldAmmo.isEmpty() || (heldAmmo.size() < getMaxAmmo(tool, player) && player.isCrouching())) { //can partial reload if crouching, provided there is space
			// do not charge if sneaking and we have sinistral, gives you a way to activate the offhand when the crossbow is not charged
			if (sinistral && !player.getOffhandItem().isEmpty() && player.isCrouching()) {
				return InteractionResultHolder.pass(gun);
			}

			// if we have ammo, start charging
			Predicate<ItemStack> ammoPredicate = getSupportedHeldProjectiles().or((stack) -> tool.getModifierLevel(JOTModifierIds.junkshot) > 0 && (stack.is(JOTItemTags.JUNKSHOT_AMMO)));

			ItemStack ammo = BowAmmoModifierHook.getAmmo(tool, gun, player, ammoPredicate);
			if (!ammo.isEmpty() || tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME)) {
				GeneralInteractionModifierHook.startDrawtime(tool, player, 1);
				if (!ammo.isEmpty()) {
					if (storeDrawingItem) {
						persistentData.put(KEY_DRAWBACK_AMMO, ammo.save(new CompoundTag()));
					} else {
						// boolean is enough to get detected by the property override, but won't bother the model
						persistentData.putBoolean(KEY_DRAWBACK_AMMO, true);
					}
				}
				player.startUsingItem(hand);
				if (!level.isClientSide) {
					level.playSound(null, player.getX(), player.getY(), player.getZ(), JOTSounds.REVOLVER_RELOAD_START.get(), SoundSource.PLAYERS, 0.75F, 1.35F);
				}
				return InteractionResultHolder.consume(gun);
			}
			// can also block without ammo
			if (tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME)) {
				player.startUsingItem(hand);
				return InteractionResultHolder.consume(gun);
			}
			return InteractionResultHolder.fail(gun);
		}

		// coming down here means we have ammo, try to use it

		// sinistral shoots on left click when in main hand, and lets us block instead of shooting if the offhand is empty
		if (sinistral) {
			ItemStack offhand = player.getOffhandItem();
			if (!offhand.isEmpty() && !offhand.is(Items.FIREWORK_ROCKET)) {
				return InteractionResultHolder.pass(gun);
			}
			// can block while filled with ammo
			if (ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK)) {
				player.startUsingItem(hand);
				return InteractionResultHolder.consume(gun);
			}
		}
		// ammo already loaded? time to fire
		fireGun(tool, player, hand, heldAmmo);
		for (int i = 0; i < tool.getModifierLevel(JOTModifierIds.burstFire); i++) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					ListTag ammo = (ListTag) persistentData.get(GUN_AMMO);
					if (!ammo.isEmpty()) fireGun(tool, player, hand, ammo);
				}
			}, 100L * (i+1));
		}
		return InteractionResultHolder.consume(gun);
	}

	/**
	 * Fires the crossbow
	 * @param tool       Tool instance
	 * @param player     Player firing
	 * @param hand       Hand fired from
	 * @param heldAmmo   Ammo used to fire, should be non-empty
	 */
	public static void fireGun(IToolStackView tool, Player player, InteractionHand hand, ListTag heldAmmo) {
		fireGun(tool, player, player.getAbilities().instabuild, hand, heldAmmo);
	}

	/**
	 * Fires the crossbow
	 * @param tool       Tool instance
	 * @param living     Entity firing
	 * @param creative   If true, was fired in creative
	 * @param hand       Hand fired from
	 * @param heldAmmo   Ammo used to fire, should be non-empty
	 */
	public static void fireGun(IToolStackView tool, LivingEntity living, boolean creative, InteractionHand hand, ListTag heldAmmo) {
//		JoyOfTinkering.LOGGER.info("fire gun");
		// ammo already loaded? time to fire
		Level level = living.level();
		if (!level.isClientSide) {
			// shoot the projectile
			int damage = 0;

			// don't need to calculate these multiple times
			float velocity = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY);
			float inaccuracy = ModifierUtil.getInaccuracy(tool, living);

			// the ammo has a stack size that may be greater than 1 (meaning multishot)
			// when creating the ammo stacks, we use split, so its getting smaller each time
			ItemStack ammo = ItemStack.of((CompoundTag) heldAmmo.get(0));
			float startAngle = getAngleStart(ammo.getCount());
			int primaryIndex = ammo.getCount() / 2;
			float burstFirePowerMult = 2f / (tool.getModifierLevel(JOTModifierIds.burstFire) + 2);
			for (int arrowIndex = 0; arrowIndex < ammo.getCount(); arrowIndex++) {
				// setup projectile
				//junkshot means you can shoot:
				//bullets
				//arrows
				//shurikens, throwing axes
				//fireworks

				// setup projectile
				AbstractArrow arrow = null;
				Projectile projectile;
				float speed;
				if (ammo.is(JOTItems.BULLET.get())) {
					ModifiableBullet bullet = new ModifiableBullet(level, living);
					bullet.setStack(ammo);
					float bulletPower = ConditionalStatModifierHook.getModifiedStat(ToolStack.from(ammo), living, ToolStats.PROJECTILE_DAMAGE);
					float gunPower = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE);
					float power = bulletPower != 0 && gunPower != 0 ? bulletPower + gunPower : 0;
					bullet.onCreate(ammo, living);
					bullet.setPower(power * burstFirePowerMult);
					speed = 2;
					projectile = bullet;
				}
				else if (ammo.is(Items.FIREWORK_ROCKET)) {
					projectile = new FireworkRocketEntity(level, ammo, living, living.getX(), living.getEyeY() - 0.15f, living.getZ(), true);
					speed = 1f;
					damage += 3;
				} else if (ammo.getItem() instanceof ModifiableShurikenItem && living instanceof Player player) {
					level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SHURIKEN_THROW.getSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
//					player.getCooldowns().addCooldown(ammo.getItem(), 10);
					speed = 1f;
					damage += 3;
					ThrownShuriken shuriken = new ThrownShuriken(level, player);
					IToolStackView stool = shuriken.onCreate(ammo, player);
					shuriken.setPower(shuriken.getPower() * burstFirePowerMult);
					projectile = shuriken;
				} else {
					ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem a ? a : (ArrowItem)Items.ARROW;
					arrow = arrowItem.createArrow(level, ammo, living);
					projectile = arrow;
					arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
					arrow.setShotFromCrossbow(true);
					speed = 1f;
					damage += 3;

					// vanilla arrows have a base damage of 2, cancel that out then add in our base damage to account for custom arrows with higher base damage
					float baseArrowDamage = (float)(arrow.getBaseDamage() - 2 + tool.getStats().get(ToolStats.PROJECTILE_DAMAGE));
					arrow.setBaseDamage(ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.PROJECTILE_DAMAGE, baseArrowDamage) * burstFirePowerMult / 1.5f);

					// fortunately, don't need to deal with vanilla infinity here, our infinity was dealt with during loading
					if (creative) {
						arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
					}
				}

				// setup projectile
				Vec3 upVector = living.getUpVector(1.0f);
				float angle = startAngle + (10 * arrowIndex);
				Vector3f targetVector = living.getViewVector(1.0f).toVector3f().rotate((new Quaternionf()).setAngleAxis(angle * Math.PI / 180F, upVector.x, upVector.y, upVector.z));
				projectile.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity * speed, inaccuracy);

				// add modifiers to the projectile, will let us use them on impact
				ModifierNBT modifiers = tool.getModifiers();
				EntityModifierCapability.getCapability(projectile).addModifiers(modifiers);

				// fetch the persistent data for the arrow as modifiers may want to store data
				ModDataNBT projectileData = PersistentDataCapability.getOrWarn(projectile);

				// let modifiers set properties
				for (ModifierEntry entry : EntityModifierCapability.getCapability(projectile).getModifiers()) {
//					JoyOfTinkering.LOGGER.info("running {}", entry.getId());
					entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, living, ammo, projectile, arrow, projectileData, arrowIndex == primaryIndex);
				}

				// finally, fire the projectile
				level.addFreshEntity(projectile);

				boolean isDecimator = tool.getItem().equals(JOTItems.DECIMATOR.asItem());
				SoundEvent fireSound = isDecimator ? JOTSounds.DECIMATOR_FIRE.get() : JOTSounds.REVOLVER_FIRE.get();
				level.playSound(null, living.getX(), living.getY(), living.getZ(), fireSound, SoundSource.PLAYERS, 0.35F + (isDecimator ? 0.2f : 0f), getRandomShotPitch(angle, living.getRandom()));
			}

			// clear the ammo, damage the bow
			((ListTag) tool.getPersistentData().get(GUN_AMMO)).remove(0);
			ToolDamageUtil.damageAnimated(tool, damage, living, hand);
			if (living instanceof Player p) {
				float drawSpeed = ConditionalStatModifierHook.getModifiedStat(tool, p, ToolStats.DRAW_SPEED);
				p.getCooldowns().addCooldown(JOTItems.REVOLVER.get(), (int) (20/drawSpeed));
				p.getCooldowns().addCooldown(JOTItems.RIFLE.get(), (int) (20/drawSpeed));
			}

			// stats
			if (living instanceof ServerPlayer serverPlayer) {
				serverPlayer.awardStat(Stats.ITEM_USED.get(tool.getItem()));
			}
		}
	}
	private static float getRandomShotPitch(float angle, RandomSource pRandom) {
		if (angle == 0) {
			return 1.0f;
		}
		return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + 0.53f + (angle / 10f);
	}

	@Override
	public void releaseUsing(ItemStack bow, Level level, LivingEntity living, int chargeRemaining) {
		ToolStack tool = ToolStack.from(bow);

		// call the stop using modifier hook
		int duration = getUseDuration(bow);
		for (ModifierEntry entry : tool.getModifiers()) {
			entry.getHook(ModifierHooks.TOOL_USING).beforeReleaseUsing(tool, entry, living, duration, chargeRemaining, ModifierEntry.EMPTY);
		}

		// any reason we shouldn't load?
		// specifically: broken, not fully charged, already have ammo
		ModDataNBT persistentData = tool.getPersistentData();
		if (tool.isBroken() || getUseDuration(bow) - chargeRemaining < persistentData.getInt(KEY_DRAWTIME)) {
			return;
		}

		// find ammo and store it on the bow
		Player player = living instanceof Player p ? p : null;
		List<ItemStack> ammoList = new ArrayList<>(List.of());
		ListTag ammoListNBT = Objects.requireNonNullElse((ListTag) tool.getPersistentData().get(GUN_AMMO), new ListTag());
//		JoyOfTinkering.LOGGER.info("release using, {} ammo", ConditionalStatModifierHook.getModifiedStat(tool, living, JOTToolStats.MAX_AMMO));
		//if we have two bullets, then load 3-6
		for (int i = ammoListNBT.size(); i < ConditionalStatModifierHook.getModifiedStat(tool, living, JOTToolStats.MAX_AMMO); i++) {
			Predicate<ItemStack> ammoPredicate = getSupportedHeldProjectiles().or((stack) -> tool.getModifierLevel(JOTModifierIds.junkshot) > 0 && (stack.is(JOTItemTags.JUNKSHOT_AMMO)));
			ItemStack ammo = BowAmmoModifierHook.consumeAmmo(tool, bow, living, player, ammoPredicate, 1); //loading one bullet at a time
//			JoyOfTinkering.LOGGER.info("ammo item {}", ammo.getDisplayName().getString());
			if (!ammo.isEmpty()) ammoList.add(ammo);
		}
		if (!ammoList.isEmpty()) {
//			JoyOfTinkering.LOGGER.info("ammo list not empty");
//			level.playSound(null, living.getX(), living.getY(), living.getZ(), JOTSounds.REVOLVER_RELOAD_END.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
			if (!level.isClientSide) {
				for (int i = 0; i < ammoList.size(); i++) {
					CompoundTag ammoNBT = ammoList.get(i).save(new CompoundTag()); //stack -> compoundtag
					ammoListNBT.add(ammoNBT);
				}
//				JoyOfTinkering.LOGGER.info(ammoListNBT.toString());
				persistentData.put(GUN_AMMO, ammoListNBT);
				// if the crossbow broke during loading, fire immediately
				if (tool.isBroken()) {
					fireGun(tool, living, player != null && player.getAbilities().instabuild, living.getUsedItemHand(), ammoListNBT);
				}
			}
		}
	}

	@Override
	public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
		TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
		if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
			builder.addDurability();
		}
		if (tool.hasTag(JOTItemTags.MOD_GUNS)) {
			builder.add(JOTToolStats.MAX_AMMO);
		}
		if (tool.hasTag(TinkerTags.Items.RANGED)) {
			builder.add(ToolStats.DRAW_SPEED);
			builder.add(ToolStats.VELOCITY);
			builder.add(ToolStats.PROJECTILE_DAMAGE);
			builder.add(ToolStats.ACCURACY);
		}
		if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
			builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
			builder.add(ToolStats.ATTACK_SPEED);
		}
		if (tool.hasTag(TinkerTags.Items.HARVEST)) {
			if (tool.hasTag(TinkerTags.Items.HARVEST_PRIMARY)) {
				builder.addTier();
			}
			builder.add(ToolStats.MINING_SPEED);
		}
		// slimestaffs and shields are holdable armor, so show armor stats
		if (tool.hasTag(TinkerTags.Items.ARMOR)) {
			builder.addOptional(ToolStats.ARMOR);
			builder.addOptional(ToolStats.ARMOR_TOUGHNESS);
			builder.addOptional(ToolStats.KNOCKBACK_RESISTANCE, 10f);
		}
		if (tool.getModifierLevel(TinkerModifiers.blocking.getId()) > 0 || tool.getModifierLevel(TinkerModifiers.parrying.getId()) > 0) {
			builder.add(ToolStats.BLOCK_AMOUNT);
			builder.add(ToolStats.BLOCK_ANGLE);
		}

		builder.addAllFreeSlots();
		for (ModifierEntry entry : tool.getModifierList()) {
			entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
		}
		TooltipUtil.addAttributes(this, tool, player, tooltips, TooltipUtil.SHOW_MELEE_ATTRIBUTES, EquipmentSlot.MAINHAND);
		// if we have ammo, render that in the tooltip
		ListTag heldAmmo = (ListTag) tool.getPersistentData().get(GUN_AMMO);
		if (heldAmmo != null && !heldAmmo.isEmpty()) {
			ItemStack heldStack = ItemStack.of(heldAmmo.getCompound(0));
			if (!heldStack.isEmpty()) {
				// basic info: item and count
				MutableComponent component = Component.translatable(PROJECTILE_KEY);
				int count = heldStack.getCount();
				if (count > 1) {
					component.append(" " + count + " ");
				} else {
					component.append(" ");
				}
				tooltips.add(component.append(heldStack.getDisplayName()));

				// copy the stack's tooltip if advanced
				if (tooltipFlag.isAdvanced() && player != null) {
					List<Component> nestedTooltip = new ArrayList<>();
					heldStack.getItem().appendHoverText(heldStack, player.level(), nestedTooltip, tooltipFlag);
					for (Component nested : nestedTooltip) {
						tooltips.add(Component.literal("  ").append(nested).withStyle(ChatFormatting.GRAY));
					}
				}
			}
		}
		return tooltips;
	}
	@Override
	public void onUseTick(Level level, LivingEntity living, ItemStack bow, int chargeRemaining) {
		// play the sound at the end of loading as an indicator its loaded, texture is another indicator
		int duration = getUseDuration(bow);
		if (!level.isClientSide) {
			if (duration - chargeRemaining == ModifierUtil.getPersistentInt(bow, KEY_DRAWTIME, -1)) {
				level.playSound(null, living.getX(), living.getY(), living.getZ(), JOTSounds.REVOLVER_RELOAD_END.get(), SoundSource.PLAYERS, 0.75F, 1.0F);
			}
		}
		ToolStack tool = ToolStack.from(bow);
		for (ModifierEntry entry : tool.getModifiers()) {
			entry.getHook(ModifierHooks.TOOL_USING).onUsingTick(tool, entry, living, duration, chargeRemaining, ModifierEntry.EMPTY);
		}
	}


}
