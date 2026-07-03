package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.items.ModifiableGunItem;
import net.minecraft.nbt.ListTag;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public enum FireOnCritModule implements ModifierModule, MeleeHitModifierHook {
	INSTANCE;

	public static final RecordLoadable<FireOnCritModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> HOOKS = HookProvider.<FireOnCritModule>defaultHooks(ModifierHooks.MELEE_HIT);

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
		if (context.isCritical() && context.getPlayerAttacker() != null) {
			context.getTarget().invulnerableTime = 0;
			ListTag ammo = (ListTag) tool.getPersistentData().get(ModifiableGunItem.GUN_AMMO);
			if (!ammo.isEmpty()) {
				ModifiableGunItem.fireGun(tool, context.getPlayerAttacker(), context.getHand(), ammo);
				ToolAttackUtil.spawnAttackParticle(TinkerTools.hammerAttackParticle.get(), context.getTarget(), 0.8f);
			}
		}
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}
}
