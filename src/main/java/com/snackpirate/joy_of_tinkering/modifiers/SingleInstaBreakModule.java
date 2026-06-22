package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record SingleInstaBreakModule(IJsonPredicate<BlockState> predicate) implements ModifierModule, BreakSpeedModifierHook {

	public static final RecordLoadable<SingleInstaBreakModule> LOADER = RecordLoadable.create(BlockPredicate.LOADER.directField("predicate_type", SingleInstaBreakModule::predicate), SingleInstaBreakModule::new);
	private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SingleInstaBreakModule>defaultHooks(ModifierHooks.BREAK_SPEED);

	public static SingleInstaBreakModule tag(TagKey<Block> tag) {
		return new SingleInstaBreakModule(BlockPredicate.tag(tag));
	}

	@Override
	public RecordLoadable<SingleInstaBreakModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return DEFAULT_HOOKS;
	}


	@Override
	public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {}

	@Override
	public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
//		TinkerAdditions.LOGGER.info("modify break speed: {}, {}", context.state().getBlock().defaultDestroyTime() * 29.5f, speed);
		if (predicate.matches(context.state())) {
//			speed = Math.min(context.state().getBlock().defaultDestroyTime() * 29.5f, speed);
//			if (speed > context.state().getBlock().defaultDestroyTime() * 30f && context.player() instanceof ServerPlayer sp) {
//				ToolHarvestLogic.runBlockBreak(context.player().getItemInHand(InteractionHand.MAIN_HAND), tool, context.state(), context.pos(), context.sideHit(), sp, null);
//			}
			if (context.player().level().isClientSide() && speed > context.state().getBlock().defaultDestroyTime()*30) {
				Minecraft.getInstance().gameMode.destroyDelay = 5;
			}
		}
		return speed;
	}

}

