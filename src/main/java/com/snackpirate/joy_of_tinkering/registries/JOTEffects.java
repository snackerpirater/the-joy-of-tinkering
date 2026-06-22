package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.mantle.registration.deferred.EnumDeferredRegister;

public class JOTEffects {
	protected static final EnumDeferredRegister<MobEffect> MOB_EFFECTS = new EnumDeferredRegister<>(Registries.MOB_EFFECT, JoyOfTinkering.MOD_ID);
	public static void register(IEventBus event) {
		MOB_EFFECTS.register(event);
	}
	public static final RegistryObject<TinkerEffect> TERRAGUARD = MOB_EFFECTS.register("terraguard", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0xAFB9D6, true).addAttributeModifier(Attributes.ARMOR, "5de036ed-bee7-4965-9348-64c3ab5c8ba8", 4f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<TinkerEffect> MAGMADAPTATION = MOB_EFFECTS.register("magmadaptation", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0x652828, true));
	public static final RegistryObject<MobEffect> overheat = MOB_EFFECTS.register("overheat", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xff8f3b, true));

}
