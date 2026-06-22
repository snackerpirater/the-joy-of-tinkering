package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayList;
import java.util.List;

public enum OversharingModule implements ModifierModule, InventoryTickModifierHook {
	INSTANCE;

	public static final RecordLoadable<OversharingModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OversharingModule>defaultHooks(ModifierHooks.INVENTORY_TICK);

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}
	private static final List<EquipmentSlot> validSlots = List.of(EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
	@Override
	public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
		if (!world.isClientSide && holder.tickCount % 40 == 0 && holder.getUseItem() != stack && OverslimeModule.INSTANCE.getAmount(ToolStack.from(stack)) > 0 && isCorrectSlot) {
			ArrayList<ItemStack> validTools = new ArrayList<>();
			for (EquipmentSlot slot: validSlots) {
				ItemStack item = holder.getItemBySlot(slot);
				if (item.is(TinkerTags.Items.MODIFIABLE)) {
					ToolStack slotTool = ToolStack.from(item);
					if (OverslimeModule.INSTANCE.getAmount(slotTool) < OverslimeModule.INSTANCE.getCapacity(slotTool, modifier)) validTools.add(item);
				}
			}
			if (validTools.isEmpty()) return;
			ItemStack chosenTool = validTools.get(Modifier.RANDOM.nextInt(validTools.size()));
			ModifierEntry entry = tool.getModifier(TinkerModifiers.overslime.getId());
			OverslimeModule.INSTANCE.addAmount(tool, entry, -1);
			OverslimeModule.INSTANCE.addAmount(ToolStack.from(chosenTool), entry, modifier.getLevel());
		}
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}
}
