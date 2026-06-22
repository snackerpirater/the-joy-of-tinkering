package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JOTEntityTags extends EntityTypeTagsProvider {

	public static final TagKey<EntityType<?>> WEAKPOINT_TOP = TagKey.create(Registries.ENTITY_TYPE, JoyOfTinkering.id("weakpoint/head_top"));
	public static final TagKey<EntityType<?>> WEAKPOINT_SIDE = TagKey.create(Registries.ENTITY_TYPE, JoyOfTinkering.id("weakpoint/head_side"));

	public JOTEntityTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider pProvider) {
		tag(WEAKPOINT_TOP).add(
				EntityType.PLAYER,
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.CREEPER,
				EntityType.ENDERMAN,
				EntityType.ARMOR_STAND,
				EntityType.HUSK,
				EntityType.STRAY,
				EntityType.STRAY,
				EntityType.BLAZE,
				EntityType.BAT,
				EntityType.EVOKER,
				EntityType.IRON_GOLEM,
				EntityType.SNOW_GOLEM,
				EntityType.PARROT,
				EntityType.PILLAGER,
				EntityType.STRIDER,
				EntityType.VEX,
				EntityType.VILLAGER,
				EntityType.VINDICATOR,
				EntityType.WANDERING_TRADER,
				EntityType.WARDEN,
				EntityType.WITCH,
				EntityType.WITHER_SKELETON,
				EntityType.ZOMBIE_VILLAGER
				).addOptional(Objects.requireNonNull(ResourceLocation.tryBuild("dummmmmmy", "target_dummy")));
		tag(WEAKPOINT_SIDE)
				.add(
						EntityType.AXOLOTL,
						EntityType.CAMEL,
						EntityType.CAT,
						EntityType.CAVE_SPIDER,
						EntityType.COD,
						EntityType.COW,
						EntityType.DOLPHIN,
						EntityType.DONKEY,
						EntityType.FOX,
						EntityType.GOAT,
						EntityType.HOGLIN,
						EntityType.HORSE,
						EntityType.LLAMA,
						EntityType.MOOSHROOM,
						EntityType.MULE,
						EntityType.OCELOT,
						EntityType.PANDA,
						EntityType.PHANTOM,
						EntityType.PIG,
						EntityType.POLAR_BEAR,
						EntityType.RABBIT,
						EntityType.RAVAGER,
						EntityType.SALMON,
						EntityType.SHEEP,
						EntityType.SILVERFISH,
						EntityType.SKELETON_HORSE,
						EntityType.SNIFFER,
						EntityType.SPIDER,
						EntityType.TRADER_LLAMA,
						EntityType.WOLF,
						EntityType.ZOGLIN,
						EntityType.ZOMBIE_HORSE
				);
	}
}
