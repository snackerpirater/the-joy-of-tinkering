package com.snackpirate.joy_of_tinkering;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JOTSounds {
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JoyOfTinkering.MOD_ID);
	public static void register(IEventBus eventBus) {
		SOUND_EVENTS.register(eventBus);
	}
	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(JoyOfTinkering.id(name)));
	}
	public static RegistryObject<SoundEvent> REVOLVER_FIRE = registerSoundEvent("revolver.fire");
	public static RegistryObject<SoundEvent> REVOLVER_RELOAD_START = registerSoundEvent("revolver.reload.start");
	public static RegistryObject<SoundEvent> REVOLVER_RELOAD_END = registerSoundEvent("revolver.reload.end");
	public static RegistryObject<SoundEvent> DECIMATOR_FIRE = registerSoundEvent("decimator.fire");

}
