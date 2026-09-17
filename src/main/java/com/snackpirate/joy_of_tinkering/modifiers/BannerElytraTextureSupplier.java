package com.snackpirate.joy_of_tinkering.modifiers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import oshi.util.tuples.Pair;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.client.TinkerRenderTypes;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record BannerElytraTextureSupplier(ModifierId modifier) implements ArmorTextureSupplier {
    public static BannerElytraTextureSupplier INSTANCE = new BannerElytraTextureSupplier(TinkerModifiers.banner.getId());
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
                texture = BannerElytraTexture.create(ToolStack.from(stack).getPersistentData().getList(BannerModule.patternKey(modifier), ListTag.TAG_COMPOUND));
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
//            TextureAtlasSprite sprite = getBannerAtlas().getSprite();
//            if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
//                return new TrimElytraTextureSupplier.TrimElytraTexture(sprite);
//            }
            ArrayList<Pair<TextureAtlasSprite, Integer>> patternList = new ArrayList<>();
            for (Tag tag: bannerTag) {
                CompoundTag compound = (CompoundTag) tag;
                Holder<BannerPattern> holder = BannerPattern.byHash(compound.getString(BannerModule.KEY_PATTERN));
                if (holder != null) {
                    holder.unwrapKey().ifPresent(id -> {
                        int color = compound.getInt(BannerModule.KEY_COLOR);
//                        JoyOfTinkering.LOGGER.info("id loc {}", id.location());
                        TextureAtlasSprite bannerSprite = getBannerAtlas().getSprite(JoyOfTinkering.id("elytra_banners/" + id.location().getPath()));
                        patternList.add(new Pair<>(bannerSprite, color));
                    });
                }
            }
            if (!patternList.isEmpty()) return new BannerElytraTexture(patternList);
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
        public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red1, float green1, float blue1, float alpha, boolean hasGlint) {
            // ignoring glint as odds are very low trim texture is the first one
            for (Pair<TextureAtlasSprite, Integer> pattern: patterns) {
                VertexConsumer buffer = pattern.getA().wrap(bufferSource.getBuffer(
                        BannerRenderType.ELYTRA_BANNER
                ));
                AbstractArmorModel.renderColored(model, matrices, buffer, packedLight, packedOverlay, pattern.getB(), red1, green1, blue1, alpha);
            }
        }
    }
    private static class BannerRenderType extends RenderType {

        public BannerRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }
        private static final RenderType ELYTRA_BANNER = RenderType.create("joy_of_tinkering:elytra_banner",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
                        .setTextureState(new TextureStateShard(Sheets.BANNER_SHEET, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true));
    }

}

