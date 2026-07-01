package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.AttributeDeferredRegister;

public class JOTAttributes {
	public static final AttributeDeferredRegister ATTRIBUTES = new AttributeDeferredRegister(JoyOfTinkering.MOD_ID);

	public static void register(IEventBus bus) {
		ATTRIBUTES.register(bus);
	}

	public static final RegistryObject<Attribute> stickiness = ATTRIBUTES.register("stickiness", 0f, 0f, 1f, true);
}
