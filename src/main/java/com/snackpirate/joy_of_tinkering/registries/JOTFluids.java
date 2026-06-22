package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

public class JOTFluids {
	public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(JoyOfTinkering.MOD_ID);

	public static final FlowingFluidObject<ForgeFlowingFluid> moltenSlimebronze = FLUIDS.register("molten_slimebronze").type(hot()).bucket().block(BurningLiquidBlock.createBurning(MapColor.COLOR_ORANGE, 12, 10, 2f)).flowing();

	public static void register(IEventBus eventBus) {
		FLUIDS.register(eventBus);
	}
	private static FluidType.Properties hot() {
		return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
	}

}
