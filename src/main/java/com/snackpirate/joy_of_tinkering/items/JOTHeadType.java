package com.snackpirate.joy_of_tinkering.items;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SkullBlock;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;
import java.util.function.Supplier;

public enum JOTHeadType implements SkullBlock.Type, StringRepresentable {
	SLIME(() -> EntityType.SLIME),
	SKYSLIME(TinkerWorld.skySlimeEntity::get),
	ENDERSLIME(TinkerWorld.enderSlimeEntity::get),
	MAGMA_CUBE(() -> EntityType.MAGMA_CUBE),
	TERRACUBE(TinkerWorld.terracubeEntity::get);

	private final Supplier<EntityType<?>> type;

	JOTHeadType(Supplier<EntityType<?>> type) {
		this.type = type;
	}

	public EntityType<?> getType() {
		return type.get();
	}
	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
