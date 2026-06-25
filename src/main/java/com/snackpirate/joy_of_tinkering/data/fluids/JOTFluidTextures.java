package com.snackpirate.joy_of_tinkering.data.fluids;

import com.mojang.blaze3d.shaders.FogShape;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTFluids;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.tconstruct.fluids.data.FluidBucketModelProvider;

public class JOTFluidTextures extends AbstractFluidTextureProvider {
	public JOTFluidTextures(PackOutput packOutput) {
		super(packOutput, JoyOfTinkering.MOD_ID);
	}

	@Override
	public void addTextures() {
		texture(JOTFluids.moltenSlimebronze).root(JoyOfTinkering.id("fluid/molten_slimebronze/")).still().flowing().color(0xffffffff).overlay().camera().cameraOpacity(0.3f).fog(FogShape.SPHERE, 0.25f, 2).calculateFogColor(true);
	}

	@Override
	public @NotNull String getName() {
		return "Joy Of Tinkering Fluid Texture Provider";
	}
	public static class BucketModels extends FluidBucketModelProvider {
		public BucketModels(PackOutput packOutput) {
			super(packOutput, JoyOfTinkering.MOD_ID);
		}
	}
}
