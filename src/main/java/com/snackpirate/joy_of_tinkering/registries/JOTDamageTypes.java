package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class JOTDamageTypes {

	public static final ResourceKey<DamageType> BULLET = create("bullet");

	private static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, JoyOfTinkering.id(name));
	}
	public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
		return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
	}
	public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity entity) {
		return source(access, type, entity, entity);
	}
	public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type) {
		return source(access, type, null, null);
	}
}
