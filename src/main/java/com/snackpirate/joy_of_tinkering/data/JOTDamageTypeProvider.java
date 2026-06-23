package com.snackpirate.joy_of_tinkering.data;

import com.snackpirate.joy_of_tinkering.registries.JOTDamageTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;

public class JOTDamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {
	@Override
	public void run(BootstapContext<DamageType> pContext) {
		pContext.register(JOTDamageTypes.BULLET, new DamageType("joy_of_tinkering.bullet", 0.1f));
	}
	public static void register(RegistrySetBuilder builder) {
		builder.add(Registries.DAMAGE_TYPE, new JOTDamageTypeProvider());
	}
}
