package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.entity.ModifiableBullet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;

public class JOTEntities {
	public static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(JoyOfTinkering.MOD_ID);

	public static final RegistryObject<EntityType<ModifiableBullet>> bullet = ENTITIES.register("bullet", () -> EntityType.Builder.<ModifiableBullet>of(ModifiableBullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
}
