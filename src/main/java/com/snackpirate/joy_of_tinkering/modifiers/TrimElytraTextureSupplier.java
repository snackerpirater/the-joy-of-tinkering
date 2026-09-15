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
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.TrimModule;

import java.util.HashMap;
import java.util.Map;

public record TrimElytraTextureSupplier(ModifierId modifier, ResourceLocation patternKey, ResourceLocation materialKey) implements ArmorTextureSupplier {
	public static TrimElytraTextureSupplier INSTANCE = new TrimElytraTextureSupplier(TinkerModifiers.trim.getId());
	public static final RecordLoadable<TrimElytraTextureSupplier> LOADER = RecordLoadable.create(ModifierId.PARSER.defaultField("modifier", TinkerModifiers.trim.getId(), TrimElytraTextureSupplier::modifier), TrimElytraTextureSupplier::new);


	public TrimElytraTextureSupplier {}

	public TrimElytraTextureSupplier(ModifierId modifier) {
		this(modifier, TrimModule.patternKey(modifier), TrimModule.materialKey(modifier));
	}

	@Override
	public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
		if (textureType == TextureType.WINGS) {
			String patternId = ModifierUtil.getPersistentString(stack, patternKey);
			String materialId = ModifierUtil.getPersistentString(stack, materialKey);
			if (!patternId.isEmpty() && !materialId.isEmpty()) {
				String key = patternId + '#' + materialId;
				Map<String,ArmorTexture> cache = WINGS_CACHE;
				ArmorTexture texture = cache.get(key);
				if (texture != null) {
					return texture;
				}
				TrimPattern pattern = access.registryOrThrow(Registries.TRIM_PATTERN).get(ResourceLocation.tryParse(patternId));
				TrimMaterial material = access.registryOrThrow(Registries.TRIM_MATERIAL).get(ResourceLocation.tryParse(materialId));
				texture = ArmorTexture.EMPTY;
				if (pattern != null && material != null) {
					ResourceLocation patternAsset = pattern.assetId();
					texture = TrimElytraTextureSupplier.TrimElytraTexture.create(patternAsset.withPath("trims/models/armor/" + patternAsset.getPath() +  "_wings"), material);
				}
				cache.put(key, texture);
				return texture;
			}
		}
		return ArmorTexture.EMPTY;
	}

	@Override
	public RecordLoadable<? extends GenericLoaderRegistry.IHaveLoader> getLoader() {
		return LOADER;
	}

	private static final Map<String,ArmorTexture> WINGS_CACHE = new HashMap<>();
	public static final ResourceManagerReloadListener CACHE_INVALIDATOR = manager -> {
		WINGS_CACHE.clear();
		TrimArmorTextureSupplier.CACHE_INVALIDATOR.onResourceManagerReload(manager);
	};
	public static class TrimElytraTexture implements ArmorTexture {
		private static TextureAtlas elytraTrimAtlas = null;
		private final TextureAtlasSprite trimSprite;

		private static TextureAtlas getTrimAtlas() {
			if (elytraTrimAtlas == null) {
				elytraTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
			}
			return elytraTrimAtlas;
		}
		public TrimElytraTexture(TextureAtlasSprite sprite) {
			trimSprite = sprite;
		}
		/**
		 * Creates the trim texture for the given root texture and material
		 */
		private static ArmorTexture create(ResourceLocation root, TrimMaterial material) {
			// start by trying and finding the material specific sprite
			ResourceLocation withMaterial = root.withSuffix('_' + material.assetName());
			TextureAtlasSprite sprite = getTrimAtlas().getSprite(withMaterial);
			if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
				return new TrimElytraTexture(sprite);
			}
			// failed to find the unique sprite, go for tinting the base
			int color = -1;
			TextColor textColor = material.description().getStyle().getColor();
			if (textColor != null) {
				color = textColor.getValue() | 0xFF000000;
			}
			JoyOfTinkering.LOGGER.error("Missing material specific texture {}, defaulting to tinting base texture #{}", withMaterial, ColorLoadable.NO_ALPHA.getString(color));
			return new TintedArmorTexture(root.withPath("textures/" + root.getPath() + ".png"), color);
		}

		@Override
		public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
			// ignoring glint as odds are very low trim texture is the first one
			VertexConsumer buffer = trimSprite.wrap(bufferSource.getBuffer(
					Sheets.armorTrimsSheet()
			));
			model.renderToBuffer(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}
}
