package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public enum TerraguardModule implements ModifierModule, KeybindInteractModifierHook, EquipmentChangeModifierHook {
	INSTANCE;

	public static final RecordLoadable<TerraguardModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TerraguardModule>defaultHooks(ModifierHooks.ARMOR_INTERACT, ModifierHooks.EQUIPMENT_CHANGE);

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
		if (!player.isShiftKeyDown()) {
			if (player.hasEffect(JOTEffects.TERRAGUARD.get()) && player.getEffect(JOTEffects.TERRAGUARD.get()).getDuration() > 1100) {
				player.playNotifySound(SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 2.0F, 1.0F);
				return false;
			}
			boolean hasClay = true;
			Level level = player.level();
			if (!player.isCreative()) {
				hasClay = false;
				Inventory inventory = player.getInventory();
				for (int i = 0; i < inventory.getContainerSize(); i++) {
					ItemStack stack = inventory.getItem(i);
					if (!stack.isEmpty() && stack.is(JOTItemTags.TERRAGUARD_CONSUMABLE)) {
						hasClay = true;
						if (!level.isClientSide) {
							stack.shrink(1);
							if (stack.isEmpty()) {
								inventory.setItem(i, ItemStack.EMPTY);
							}
						}
						break;
					}
				}
			}
			if (hasClay) {
				player.playNotifySound(SoundEvents.ROOTED_DIRT_BREAK, SoundSource.PLAYERS, 2.0F, 1.0F);
				player.playNotifySound(SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 2.0F, 1.0F);
				int currentDuration = player.hasEffect(JOTEffects.TERRAGUARD.get()) ? Math.min(player.getEffect(JOTEffects.TERRAGUARD.get()).getDuration(), 1100) : 0;
				JOTEffects.TERRAGUARD.get().apply(player, currentDuration+200, modifier.getLevel()-1, true);
				return true;
			}
		}
		return false;
	}
	@Override
	public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
		if (context.getChangedSlot() == EquipmentSlot.HEAD) {
			IToolStackView replacement = context.getReplacementTool();
			if (replacement == null || replacement.getModifierLevel(JOTModifierIds.terraguard) == 0) {
				// cure effects using the helmet
				context.getEntity().removeEffect(JOTEffects.TERRAGUARD.get());
			}
		}
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}
}
