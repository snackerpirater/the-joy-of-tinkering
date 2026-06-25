package com.snackpirate.joy_of_tinkering.data.fluids;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTFluids;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ExplosionFluidEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerEffects;

public class JOTFluidEffects extends AbstractFluidEffectProvider {
	public JOTFluidEffects(PackOutput packOutput) {
		super(packOutput, JoyOfTinkering.MOD_ID);
	}

	@Override
	protected void addFluids() {
		FluidMobEffect conductive = new FluidMobEffect(TinkerEffects.conductive.get(), 100, 1);
		addFluid(JOTFluids.moltenSlimebronze, FluidValues.NUGGET * 2)
				.addEffect(ExplosionFluidEffect.radius(1, 1).damage(LevelingValue.eachLevel(1.5f)).placeFire().ignoreBlocks().build())
				.addEntityEffect(new MobEffectFluidEffect(conductive, TimeAction.ADD))
				.addBlockEffect(new MobEffectCloudFluidEffect(conductive));
	}

	@Override
	public @NotNull String getName() {
		return "Joy Of Tinkering Fluid Effect Provider";
	}
}
