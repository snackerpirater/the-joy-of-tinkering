package com.snackpirate.joy_of_tinkering.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.block.entity.BannerPattern;
import oshi.util.tuples.Pair;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BannerElytraTextureSupplier(ModifierId modifier) implements ArmorTextureSupplier {
    public static TrimElytraTextureSupplier INSTANCE = new TrimElytraTextureSupplier(TinkerModifiers.trim.getId());
    public static final RecordLoadable<BannerElytraTextureSupplier> LOADER = RecordLoadable.create(ModifierId.PARSER.defaultField("modifier", TinkerModifiers.banner.getId(), BannerElytraTextureSupplier::modifier), BannerElytraTextureSupplier::new);


    public BannerElytraTextureSupplier {}

//    public BannerElytraTextureSupplier(ModifierId modifier) {
//        this(modifier, TrimModule.patternKey(modifier), TrimModule.materialKey(modifier));
//    }

    @Override
    public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
        if (textureType == TextureType.WINGS) {
            int patternHash = ModifierUtil.getPersistentInt(stack, BannerModule.cacheKey(modifier),0);
            if (patternHash != 0) {
                Map<Integer,ArmorTexture> cache = WINGS_CACHE;
                ArmorTexture texture = cache.get(patternHash);

                if (texture != null) {
                    return texture;
                }
                TrimPattern pattern = access.registryOrThrow(Registries.TRIM_PATTERN).get(ResourceLocation.tryParse(patternId));
                TrimMaterial material = access.registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
                texture = ArmorTexture.EMPTY;
                if (pattern != null && material != null) {
                    ResourceLocation patternAsset = pattern.assetId();
                    texture = BannerElytraTexture.create(patternAsset.withPath("trims/models/armor/" + patternAsset.getPath() +  "_wings"), material);
                }
                cache.put(patternHash, texture);
                return texture;
            }
        }
        return ArmorTexture.EMPTY;
    }

    @Override
    public RecordLoadable<? extends GenericLoaderRegistry.IHaveLoader> getLoader() {
        return LOADER;
    }

    //map from banner hash to texture
    private static final Map<Integer,ArmorTexture> WINGS_CACHE = new HashMap<>();

    public static final ResourceManagerReloadListener CACHE_INVALIDATOR = manager -> {
        WINGS_CACHE.clear();
//        TrimArmorTextureSupplier.CACHE_INVALIDATOR.onResourceManagerReload(manager);
    };
    public static class BannerElytraTexture implements ArmorTexture {
        private static TextureAtlas elytraBannerAtlas = null;
        private final List<Pair<TextureAtlasSprite, Integer>> patterns;

        private static TextureAtlas getBannerAtlas() {
            if (elytraBannerAtlas == null) {
                elytraBannerAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.BANNER_SHEET);
            }
            return elytraBannerAtlas;
        }
        public BannerElytraTexture(List<Pair<TextureAtlasSprite, Integer>> list) {
            patterns = list;
        }
        /**
         * Creates the trim texture for the given root texture and material
         */
        private static ArmorTexture create(ListTag bannerTag) {
            // start by trying and finding the material specific sprite
//            ResourceLocation withMaterial = root.withSuffix('_' + material.assetName());
            TextureAtlasSprite sprite = getBannerAtlas().getSprite();
            if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
                return new TrimElytraTextureSupplier.TrimElytraTexture(sprite);
            }
            // failed to find the unique sprite, go for tinting the base
//            int color = -1;
//            TextColor textColor = material.description().getStyle().getColor();
//            if (textColor != null) {
//                color = textColor.getValue() | 0xFF000000;
//            }
            JoyOfTinkering.LOGGER.error("Missing material specific texture, defaulting to empty");
            return ArmorTexture.EMPTY;
        }

        private static ArrayList<Pair<Holder<BannerPattern>, Integer>> listFromTag(ListTag tag) {
            ArrayList<Pair<Holder<BannerPattern>, Integer>> list = new ArrayList<>();
            for (int i = 0; i < tag.size(); i++) {
                CompoundTag pair = tag.getCompound(i);
                Holder<BannerPattern> pattern = BannerPattern.byHash(pair.getString(BannerModule.KEY_PATTERN));
                int color = pair.getInt(BannerModule.KEY_COLOR);
                list.add(new Pair<>(pattern, color));
            }
            return list;
        }

        @Override
        public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
            // ignoring glint as odds are very low trim texture is the first one
            VertexConsumer buffer = patterns.wrap(bufferSource.getBuffer(
                    Sheets.bannerSheet()
            ));
            model.renderToBuffer(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}

