package com.snackpirate.joy_of_tinkering.items;

import com.snackpirate.joy_of_tinkering.JOTSounds;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.item.ModifiableItemClientExtension;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifiableDecimator extends ModifiableGunItem {

	private static final boolean storeDrawingItem = false;
	public ModifiableDecimator(Properties properties, ToolDefinition toolDefinition) {
		super(properties, toolDefinition);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		//don't want gun pose
		consumer.accept(ModifiableItemClientExtension.INSTANCE);
	}
	//loading but no firing
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

			ItemStack ammo = BowAmmoModifierHook.getAmmo(tool, ItemStack.EMPTY, player, ammoPredicate);
			if (!ammo.isEmpty() || tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME)) {
				GeneralInteractionModifierHook.startDrawing(tool, player, 1);
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
		return InteractionResultHolder.fail(gun);
	}

}
