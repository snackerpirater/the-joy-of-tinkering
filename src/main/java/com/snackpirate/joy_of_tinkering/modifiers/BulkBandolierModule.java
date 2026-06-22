package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Predicate;

public record BulkBandolierModule(boolean checkStandardArrows)  implements ModifierModule, BowAmmoModifierHook {
	public static final RecordLoadable<BulkBandolierModule> LOADER = RecordLoadable.create(
			BooleanLoadable.INSTANCE.defaultField("check_standard_arrows", true, BulkBandolierModule::checkStandardArrows),
			BulkBandolierModule::new);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BulkBandolierModule>defaultHooks(ModifierHooks.BOW_AMMO);
	private static final ResourceLocation LAST_SLOT = TConstruct.getResource("quiver_last_selected");

	@Override
	public RecordLoadable<BulkBandolierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}

	@Override
	public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
		// skip if we have standard ammo, this quiver holds backup arrows
		if (checkStandardArrows && !standardAmmo.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ToolInventoryCapability.StackMatch match = modifier.getHook(ToolInventoryCapability.HOOK).findStack(tool, modifier, ammoPredicate);
		if (!match.isEmpty()) {
			tool.getPersistentData().putInt(LAST_SLOT, match.slot());
			return match.stack();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
		// we assume no one else touched the quiver inventory, this is a good assumption, do not make it a bad assumption by modifying the quiver in other modifiers
		ammo.shrink(needed);
		modifier.getHook(ToolInventoryCapability.HOOK).setStack(tool, modifier, tool.getPersistentData().getInt(LAST_SLOT), ammo);
	}
}
