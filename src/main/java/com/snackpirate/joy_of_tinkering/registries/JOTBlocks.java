package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.shared.block.SlimesteelBlock;

import java.util.function.Function;

public class JOTBlocks {
	protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(JoyOfTinkering.MOD_ID);

	protected static final Item.Properties ITEM_PROPS = new Item.Properties();
	protected static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, ITEM_PROPS);
	protected static BlockBehaviour.Properties metalBuilder(MapColor color) {
		return builder(color, SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0f);
	}  protected static BlockBehaviour.Properties builder(SoundType soundType) {
		return Block.Properties.of().sound(soundType);
	}

	protected static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
		return builder(soundType).mapColor(color);
	}
	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}

	public static final MetalItemObject slimebronze = BLOCKS.registerMetal("slimebronze", () -> new SlimesteelBlock(metalBuilder(MapColor.COLOR_ORANGE).noOcclusion()), TOOLTIP_BLOCK_ITEM, ITEM_PROPS);
}
