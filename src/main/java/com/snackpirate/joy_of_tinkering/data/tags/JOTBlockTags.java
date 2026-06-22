package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTBlocks;
import com.snackpirate.joy_of_tinkering.registries.JOTFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.world.TinkerWorld;

import static net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE;
import static net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL;
import static slimeknights.tconstruct.fluids.TinkerFluids.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class JOTBlockTags extends BlockTagsProvider {
	public static final TagKey<Block> SURFACE_STRIDER_FLUIDS = BlockTags.create(JoyOfTinkering.id("surface_strider_fluids"));
	public JOTBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(SURFACE_STRIDER_FLUIDS)
				.add(Blocks.LAVA)
				.add(searedStone.getBlock(),
					scorchedStone.getBlock(),
					moltenClay.getBlock(),
					moltenGlass.getBlock(),
					moltenPorcelain.getBlock(),
					moltenObsidian.getBlock(),
					moltenEnder.getBlock(),
					blazingBlood.getBlock(),
					moltenEmerald.getBlock(),
					moltenQuartz.getBlock(),
					moltenAmethyst.getBlock(),
					moltenDiamond.getBlock(),
					moltenDebris.getBlock(),
					moltenIron.getBlock(),
					moltenGold.getBlock(),
					moltenCopper.getBlock(),
					moltenCobalt.getBlock(),
					moltenSteel.getBlock(),
					moltenSlimesteel.getBlock(),
					moltenAmethystBronze.getBlock(),
					moltenRoseGold.getBlock(),
					moltenPigIron.getBlock(),
					moltenManyullyn.getBlock(),
					moltenHepatizon.getBlock(),
					moltenQueensSlime.getBlock(),
					moltenSoulsteel.getBlock(),
					moltenNetherite.getBlock(),
					moltenKnightslime.getBlock(),
					moltenTin.getBlock(),
					moltenAluminum.getBlock(),
					moltenLead.getBlock(),
					moltenSilver.getBlock(),
					moltenNickel.getBlock(),
					moltenZinc.getBlock(),
					moltenPlatinum.getBlock(),
					moltenTungsten.getBlock(),
					moltenOsmium.getBlock(),
					moltenUranium.getBlock(),
					moltenBronze.getBlock(),
					moltenBrass.getBlock(),
					moltenElectrum.getBlock(),
					moltenInvar.getBlock(),
					moltenConstantan.getBlock(),
					moltenPewter.getBlock(),
					moltenEnderium.getBlock(),
					moltenLumium.getBlock(),
					moltenSignalum.getBlock(),
					moltenRefinedObsidian.getBlock(),
					moltenRefinedGlowstone.getBlock(),
					moltenNicrosil.getBlock(),
					JOTFluids.moltenSlimebronze.getBlock());
		addMetalTags(JOTBlocks.slimebronze, true);

		this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(JOTBlocks.slimebronze.getBlockTag());
		this.tag(TinkerTags.Blocks.ANVIL_METAL).addTag(JOTBlocks.slimebronze.getBlockTag());
		tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, JOTBlocks.slimebronze);

	}
	@SafeVarargs
	private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, Supplier<? extends Block>... blocks) {
		tagBlocks(tag1, blocks);
		tagBlocks(tag2, blocks);
	}
	@SafeVarargs
	private void tagBlocks(TagKey<Block> tag, Supplier<? extends Block>... blocks) {
		IntrinsicTagAppender<Block> appender = this.tag(tag);
		for (Supplier<? extends Block> block : blocks) {
			appender.add(block.get());
		}
	}
	private void addMetalTags(MetalItemObject metal, boolean beacon) {
		this.tag(metal.getBlockTag()).add(metal.get());
		if (beacon) {
			this.tag(BlockTags.BEACON_BASE_BLOCKS).addTag(metal.getBlockTag());
		}
		this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(metal.getBlockTag());
	}

}
