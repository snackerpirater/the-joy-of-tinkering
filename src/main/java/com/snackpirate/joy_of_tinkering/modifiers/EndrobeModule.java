package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Set;

public record EndrobeModule(Set<TooltipKey> keys) implements ModifierModule, KeybindInteractModifierHook {
	public static final RecordLoadable<EndrobeModule> LOADER =  RecordLoadable.create(TinkerLoadables.TOOLTIP_KEY.set().requiredField("on_key", EndrobeModule::keys), EndrobeModule::new);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<EndrobeModule>defaultHooks(ModifierHooks.ARMOR_INTERACT);

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}

	@Override
	public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
		if (keys.contains(keyModifier)) {
			Level level = player.level();
			if (level.isClientSide || player.getCooldowns().isOnCooldown(tool.getItem())) {
				return true;
			}
			// offhand must be able to go in the pants
			EquipmentSlot[] validSlots = {
					EquipmentSlot.CHEST,
					EquipmentSlot.LEGS,
					EquipmentSlot.FEET
			};
			boolean changedGear = false;
			for (int j = 0; j < 3; j++) {
				ItemStack gear = player.getItemBySlot(validSlots[j]);
				if (gear.isEmpty() || !ToolInventoryCapability.isBlacklisted(gear)) {
					ToolInventoryCapability.InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
					int slots = inventory.getSlots(tool, modifier) / 3;

					// new offhand is first slot
					ItemStack newGear = inventory.getStack(tool, modifier, j);
					player.setItemSlot(validSlots[j], newGear);
					int finalJ = j;
					if (newGear.is(TinkerTags.Items.ARMOR)) {
						ToolDamageUtil.directDamage(ToolStack.from(newGear), 2, player, newGear);
					} else {
						newGear.hurtAndBreak(2, player, (p_219739_) -> {
							p_219739_.broadcastBreakEvent(validSlots[finalJ]);
							net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, newGear, null);
						});
					}
					// shift all other slots back by 1;
					for (int i = 1; i < slots; i++) {
						//1, 2
						//0, 3
						inventory.setStack(tool, modifier, (i - 1) * 3 + j, inventory.getStack(tool, modifier, i * 3 + j));
					}
					// put old offhand in last slot
					//1, 2
					//0, 3
					inventory.setStack(tool, modifier, (slots - 1) * 3 + j, gear);

					// sound effect
					if (!newGear.isEmpty() || !gear.isEmpty()) {
						changedGear = true;
					}
				}
			}
			if (changedGear) {
				player.getCooldowns().addCooldown(tool.getItem(), 60);
				level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
				return true;
			}
		}
		return false;
	}
}
