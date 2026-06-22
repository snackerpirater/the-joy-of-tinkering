package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.modifiers.hook.HarvestCheckModifierHook;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierHooks;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestTiers;

import java.util.List;

public enum FistMiningModule implements ModifierModule, BreakSpeedModifierHook, DisplayNameModifierHook, HarvestCheckModifierHook, BlockHarvestModifierHook {
	INSTANCE;

	public static final RecordLoadable<FistMiningModule> LOADER = new SingletonLoader<>(INSTANCE);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FistMiningModule>defaultHooks(ModifierHooks.BREAK_SPEED, ModifierHooks.DISPLAY_NAME, JOTModifierHooks.HARVEST_CHECK, ModifierHooks.BLOCK_HARVEST);

	public static final String FORMAT = TConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}

	//mining speed boost when both hands are empty
	@Override
	public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
		if (event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
			event.setNewSpeed(getDestroySpeed(tool, event.getState()) + event.getNewSpeed());
		}
	}
	static float getDestroySpeed(IToolStackView tool, BlockState state) {
		if (tool.isBroken()) {
			return 0.3f;
		}
		float speed = IsEffectiveToolHook.isEffective(tool, state) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1;
		return Math.max(1, tool.getHook(ToolHooks.MINING_SPEED).modifyDestroySpeed(tool, state, speed));
	}

	//Since the chestplate doesn't want to display mining tier I'm gonna do this
	@Override
	public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
		return Component.translatable(FORMAT, name, HarvestTiers.getName(tool.getStats().get(ToolStats.HARVEST_TIER)));
	}

	@Override
	public boolean canHarvest(IToolStackView tool, ModifierEntry entry, LivingEntity entity, EquipmentSlot slot, PlayerEvent.HarvestCheck event) {
//		TinkerAdditions.LOGGER.info("fist mining harvest check");
		if (slot == EquipmentSlot.CHEST && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
			Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
			return isCorrectTierForDrops(tier, event.getTargetBlock());
		}
		return event.canHarvest();
	}
	//graciously borrowed from Artifacts
	public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
		if (!state.requiresCorrectToolForDrops()) {
			return true;
		}
		int i = tier.getLevel();
		if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
			return i >= 4;
		} else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
			return i >= 3;
		} else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
			return i >= 2;
		} else {
			return i >= 1;
		}
	}

	@Override
	public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
		JoyOfTinkering.LOGGER.info("block harvest finish");
	}
}
