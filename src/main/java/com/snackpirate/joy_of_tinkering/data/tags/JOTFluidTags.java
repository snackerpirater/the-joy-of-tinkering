package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.registries.JOTFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

public class JOTFluidTags extends FluidTagsProvider {
	public JOTFluidTags(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(p_255941_, p_256600_, modId, existingFileHelper);
	}
	private void fluidTag(FlowingFluidObject<?> fluid) {
		tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
		TagKey<Fluid> tag = fluid.getCommonTag();
		if (tag != null) {
			tag(tag).addTag(fluid.getLocalTag());
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	protected void addTags(HolderLookup.@NotNull Provider pProvider) {
		fluidTag(JOTFluids.moltenSlimebronze);
		this.tag(TinkerTags.Fluids.METAL_TOOLTIPS).addTags(JOTFluids.moltenSlimebronze.getTag());
	}
}
