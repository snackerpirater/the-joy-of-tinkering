package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.items.ModifiableGunItem;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.snackpirate.joy_of_tinkering.items.ModifiableGunItem.GUN_AMMO;

public enum SouthpawModule implements ModifierModule, GeneralInteractionModifierHook, EntityInteractionModifierHook {
	INSTANCE;
	public static final RecordLoadable<SouthpawModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> HOOKS = HookProvider.<SouthpawModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.ENTITY_INTERACT);
	@Override
	public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
		if (source == InteractionSource.LEFT_CLICK && hand == InteractionHand.MAIN_HAND && !tool.isBroken()) {
			ListTag heldAmmo = ((ListTag) tool.getPersistentData().get(GUN_AMMO));
			if (!heldAmmo.isEmpty()) {
				ModifiableGunItem.fireGun(tool, player, hand, heldAmmo);
				for (int i = 0; i < tool.getModifierLevel(JOTModifierIds.burstFire); i++) {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							ModDataNBT persistentData = tool.getPersistentData();
							ListTag ammo = (ListTag) persistentData.get(GUN_AMMO);
							if (!ammo.isEmpty()) ModifiableGunItem.fireGun(tool, player, hand, ammo);
						}
					}, 100L * (i+1));
				}
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
		return onToolUse(tool, modifier, player, hand, source);
	}

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}
}
