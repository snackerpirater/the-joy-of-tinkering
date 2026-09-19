package com.snackpirate.joy_of_tinkering.modifiers.modules;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.AreaOfEffectHighlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.BlockItemProviderModifierHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public record PlaceBlockFromToolInventoryModule(int toolDamage, int cooldown) implements ModifierModule, BlockItemProviderModifierHook, BlockInteractionModifierHook, AreaOfEffectHighlightModifierHook {
	private static final List<ModuleHook<?>> HOOKS = HookProvider.<PlaceBlockFromToolInventoryModule>defaultHooks(ModifierHooks.BLOCK_ITEM_PROVIDER, ModifierHooks.BLOCK_INTERACT, ModifierHooks.AOE_HIGHLIGHT);
	public  static final RecordLoadable<PlaceBlockFromToolInventoryModule> LOADER = RecordLoadable.create(
			IntLoadable.FROM_ONE.defaultField("tool_damage", 0, PlaceBlockFromToolInventoryModule::toolDamage),
			IntLoadable.FROM_ZERO.defaultField("cooldown_ticks", 0, PlaceBlockFromToolInventoryModule::cooldown),
			PlaceBlockFromToolInventoryModule::new);
	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}
	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}
	@Override
	public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
		if (!tool.isBroken() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
			Player player = context.getPlayer();
			if (player != null && player.isCrouching()) return InteractionResult.FAIL;
			if (!context.getLevel().isClientSide)
			{
				Level world = context.getLevel();
				Direction face = context.getClickedFace();
				BlockPos pos = context.getClickedPos().relative(face);
				int numTargets = 0;
//				if (TinkerCommons.glowBlock.get().addGlow(world, pos, face.getOpposite())) {
//					// damage the tool, showing animation if relevant
//					if (toolDamage > 0 && ToolDamageUtil.damage(tool, toolDamage, player, context.getItemInHand(), modifier.getId()) && player != null) {
//						player.broadcastBreakEvent(source.getSlot(context.getHand()));
//					}
//					world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
//				}
				{
					ItemStack item = this.getBlockItemStack(tool, modifier, context.getPlayer());
					if (!item.isEmpty()) {
						numTargets++;
						BlockPlaceContext placeContext = new BlockPlaceContext(world, player, context.getHand(), item, context.getHitResult());
						BlockPos clicked = placeContext.getClickedPos();
						BlockItem blockItem = (BlockItem) item.getItem();
						if (blockItem.place(placeContext).consumesAction()) {
							if (player != null && !player.isCreative()) this.consumeBlockItem(tool, modifier, item, player);
							if (player instanceof ServerPlayer serverPlayer) {
								BlockState placed = world.getBlockState(clicked);
								SoundType soundType = placed.getSoundType(world, clicked, player);
								serverPlayer.connection.send(new ClientboundSoundPacket(
										BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundType.getPlaceSound()),
										SoundSource.BLOCKS, clicked.getX(), clicked.getY(), clicked.getZ(), (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, TConstruct.RANDOM.nextLong()));
							}
						}
					}
				}
				for (BlockPos offset : tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, world.getBlockState(pos), AreaOfEffectIterator.AOEMatchType.TRANSFORM)) {
					BlockHitResult offsetHit = Util.offset(context.getHitResult(), offset);
//					JoyOfTinkering.LOGGER.info("place iterator at {}", offset);
					ItemStack item = this.getBlockItemStack(tool, modifier, player);
					if (!item.isEmpty()) {
						numTargets++;
						BlockPlaceContext placeContext = new BlockPlaceContext(world, player, context.getHand(), item, offsetHit);
						BlockPos clicked = placeContext.getClickedPos();
						BlockItem blockItem = (BlockItem) item.getItem();
						if (blockItem.place(placeContext).consumesAction()) {
							if (player != null && !player.isCreative()) this.consumeBlockItem(tool, modifier, item, player);
							if (player instanceof ServerPlayer serverPlayer) {
								BlockState placed = world.getBlockState(clicked);
								SoundType soundType = placed.getSoundType(world, clicked, player);
								serverPlayer.connection.send(new ClientboundSoundPacket(
										BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundType.getPlaceSound()),
										SoundSource.BLOCKS, clicked.getX(), clicked.getY(), clicked.getZ(), (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, TConstruct.RANDOM.nextLong()));
							}
						}
					}
				}

				if (numTargets != 0 && player != null) {
					if (cooldown != 0) player.getCooldowns().addCooldown(context.getItemInHand().getItem(), cooldown);
					if (toolDamage > 0 && ToolDamageUtil.damage(tool, toolDamage * numTargets, player, context.getItemInHand(), modifier.getId())) {
						player.broadcastBreakEvent(source.getSlot(context.getHand()));
					}
				}
//				world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
			}
			return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getBlockItemStack(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity) {
		ToolInventoryCapability.InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
//		JoyOfTinkering.LOGGER.info("got stack {}", inventory.findStack(tool, modifier, stack -> {
//			return stack.getItem() instanceof BlockItem;
//		}).stack());
		return inventory.findStack(tool, modifier, stack -> stack.getItem() instanceof BlockItem).stack();
	}

	@Override
	public boolean consumeBlockItem(IToolStackView tool, ModifierEntry modifier, ItemStack backingStack, @Nullable LivingEntity entity) {
		ToolInventoryCapability.InventoryModifierHook inventory = modifier.getHook(ToolInventoryCapability.HOOK);
		ToolInventoryCapability.StackMatch match = inventory.findStack(tool, modifier, stack -> stack.getItem() instanceof BlockItem);
		if (!match.isEmpty()) {
			ItemStack copy = match.stack().copy();
			copy.shrink(1);
			inventory.setStack(tool, modifier, match.slot(), copy);
			return true;
		}
		return false;
	}


	@Override
	public boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state) {
		return true;
	}
}
