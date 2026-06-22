package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.network.JOTNetwork;
import com.snackpirate.joy_of_tinkering.network.UpdateHelmetPagePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.item.AbstractBookItem;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.modules.InventorySelectionModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public record WellReadModule(Set<TooltipKey> keys) implements ModifierModule, KeybindInteractModifierHook, ModifierRemovalHook, InventorySelectionModule {
	public static final RecordLoadable<WellReadModule> LOADER = RecordLoadable.create(TinkerLoadables.TOOLTIP_KEY.set().requiredField("on_key", WellReadModule::keys), WellReadModule::new);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<WellReadModule>defaultHooks(ModifierHooks.ARMOR_INTERACT);
	private static final ResourceLocation SELECTED_SLOT = JoyOfTinkering.id("well_read_selected");
	private static final String SELECTED = ("modifier.joy_of_tinkering.well_read.selected");
	private static final String EMPTY = ("modifier.joy_of_tinkering.well_read.empty");
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
			if (keyModifier == TooltipKey.SHIFT) {
				selectNext(tool, modifier, player, SELECTED_SLOT);
				return true;
			}
			if (!level.isClientSide) {
				return true;
			}
			ToolInventoryCapability.InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);

			ItemStack bookStack = inventory.getStack(tool, modifier, tool.getPersistentData().getInt(SELECTED_SLOT));
			if (bookStack.getItem() instanceof AbstractBookItem book && book.getBook(bookStack) instanceof BookData data) {
				String page = BookHelper.getCurrentSavedPage(bookStack);
				data.openGui(bookStack.getHoverName(), page, newPage -> {
					BookHelper.writeSavedPageToBook(bookStack, newPage);
					JOTNetwork.getInstance().sendToServer(new UpdateHelmetPagePacket(newPage, tool.getPersistentData().getInt(SELECTED_SLOT)));
				});
			}
			// shift all other slots back by 1;
			// put old offhand in last slot

			// sound effect
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
			return true;
		}
		return false;
	}


	@Nullable
	@Override
	public Component onRemoved(IToolStackView tool, Modifier modifier) {
		tool.getPersistentData().remove(SELECTED_SLOT);
		return null;
	}

	@Override
	public void onDisableSelection(IToolStackView tool, ModifierEntry modifier, Player player) {}

	@Override
	public void onInventorySelect(IToolStackView tool, ModifierEntry modifier, Player player, int newIndex, ItemStack stack) {
		player.displayClientMessage(Component.translatable(SELECTED, stack.getHoverName(), newIndex + 1), true);
	}

	@Override
	public boolean selectNext(IToolStackView tool, ModifierEntry modifier, Player player, ResourceLocation selectedSlot) {
		// first, find the new number
		ModDataNBT data = tool.getPersistentData();
		ToolInventoryCapability.InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
		int totalSlots = inventory.getSlots(tool, modifier);
		int current = data.getInt(selectedSlot);
		if (inventory.findStack(tool, modifier, stack -> stack.getItem() instanceof AbstractBookItem).isEmpty()) {
			player.displayClientMessage(Component.translatable(EMPTY), true);
			return false;
		}
		// support going 1 above max to disable the trick arrows
		int newSelected = (current + 1) % (totalSlots);
		// skip over empty slots; helps when you don't use the full space
		while (newSelected < totalSlots && inventory.getStack(tool, modifier, newSelected).isEmpty()) {
			newSelected = (newSelected + 1) % (totalSlots);
		}
		// display a message about what is now selected
		if (newSelected != current) {
			if (!player.level().isClientSide) {
				data.putInt(selectedSlot, newSelected);
				if (newSelected == totalSlots) {
					onDisableSelection(tool, modifier, player);
				} else {
					onInventorySelect(tool, modifier, player, newSelected, inventory.getStack(tool, modifier, newSelected));
				}
			}
			return true;
		}
		return false;
	}
}
