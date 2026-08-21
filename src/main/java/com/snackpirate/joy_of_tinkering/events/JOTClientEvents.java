package com.snackpirate.joy_of_tinkering.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.snackpirate.joy_of_tinkering.JOTConfig;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.data.tags.JOTModifierTags;
import com.snackpirate.joy_of_tinkering.entity.ModifiableBulletRenderer;
import com.snackpirate.joy_of_tinkering.items.JOTHeadType;
import com.snackpirate.joy_of_tinkering.items.ModifiableGunItem;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTEntities;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JoyOfTinkering.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JOTClientEvents {

	@SubscribeEvent
	static void registerRenderers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		Supplier<LayerDefinition> normalHead = Lazy.of(SkullModel::createMobHeadLayer);
		registerLayerDefinition(event, JOTHeadType.SLIME, normalHead);
		registerLayerDefinition(event, JOTHeadType.SKYSLIME, normalHead);
		registerLayerDefinition(event, JOTHeadType.ENDERSLIME, normalHead);
		registerLayerDefinition(event, JOTHeadType.MAGMA_CUBE, normalHead);
		registerLayerDefinition(event, JOTHeadType.TERRACUBE, normalHead);
	}

	@SubscribeEvent
	static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			registerHeadModel(JOTHeadType.SLIME, MaterialIds.earthslime, JoyOfTinkering.id("textures/entity/skull/slime.png"));
			registerHeadModel(JOTHeadType.SKYSLIME, MaterialIds.skyslime, JoyOfTinkering.id("textures/entity/skull/skyslime.png"));
			registerHeadModel(JOTHeadType.ENDERSLIME, MaterialIds.enderslime, JoyOfTinkering.id("textures/entity/skull/enderslime.png"));
			registerHeadModel(JOTHeadType.MAGMA_CUBE, MaterialIds.magma, JoyOfTinkering.id("textures/entity/skull/magma_cube.png"));
			registerHeadModel(JOTHeadType.TERRACUBE, MaterialIds.clay, JoyOfTinkering.id("textures/entity/skull/terracube.png"));
		});
	}
	@SubscribeEvent
	static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(JOTEntities.bullet.get(), ModifiableBulletRenderer::new);
	}

	@SubscribeEvent
	static void registerSkullModels(EntityRenderersEvent.CreateSkullModels event) {
		EntityModelSet modelSet = event.getEntityModelSet();
		HEAD_LAYERS.forEach((type, layer) -> event.registerSkullModel(type, new SkullModel(modelSet.bakeLayer(layer))));
	}

	private static void registerHeadModel(JOTHeadType skull, MaterialId materialId, ResourceLocation texture) {
		SkullBlockRenderer.SKIN_BY_TYPE.put(skull, texture);
		SlimeskullArmorModel.registerHeadModel(materialId, HEAD_LAYERS.get(skull), texture);
	}

	public static final Map<JOTHeadType, ModelLayerLocation> HEAD_LAYERS = Arrays.stream(JOTHeadType.values()).collect(
			Collectors.toMap(Function.identity(), type -> new ModelLayerLocation(JoyOfTinkering.id(type.getSerializedName() + "_head"), "main"), (a, b) -> a, () -> new EnumMap<>(JOTHeadType.class)));

	private static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event, JOTHeadType head, Supplier<LayerDefinition> supplier) {
		event.registerLayerDefinition(HEAD_LAYERS.get(head), supplier);
	}

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JoyOfTinkering.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Game {
        @SubscribeEvent
        static void magmadaptiveClearFog(ViewportEvent.RenderFog event) {
            FogType fogType = event.getCamera().getFluidInCamera();
            if (Minecraft.getInstance().player.hasEffect(JOTEffects.MAGMADAPTATION.get()) && fogType == FogType.LAVA) {
                event.setNearPlaneDistance(-8.0F);
                event.setFarPlaneDistance(50.0F);
                event.setCanceled(true);
            }
        }

		private static final List<ItemStack> bulletsRight = new ArrayList<>();
		private static int magRight = 0;
		private static int reserveRight = 0;
		private List<Vector2i> radialPosRight = new ArrayList<>();

		private static final List<ItemStack> bulletsLeft = new ArrayList<>();
		private static int magLeft = 0;
		private static int reserveLeft = 0;
		private List<Vector2i> radialPosLeft = new ArrayList<>();

		private static final int SLOT_BACKGROUND_SIZE = 22;
		@SubscribeEvent
		static void equipmentChange(ToolEquipmentChangeEvent event) {
			JoyOfTinkering.LOGGER.info("equip change");
			if (event.getEntity() != Minecraft.getInstance().player) {
				return;
			}
			EquipmentChangeContext context = event.getContext();
			if (JOTConfig.CLIENT.bulletDisplay.get() != JOTConfig.Client.BulletDisplayOption.NONE) {
				if (context.getChangedSlot() == EquipmentSlot.MAINHAND) {
					bulletsRight.clear();
					reserveRight = 0;
					magRight = 0;
					IToolStackView tool = context.getToolInSlot(EquipmentSlot.MAINHAND);
					if (tool != null && tool.hasTag(JOTItemTags.MOD_GUNS)) {
						ListTag ammoList = (ListTag) tool.getPersistentData().get(ModifiableGunItem.GUN_AMMO);
						if (ammoList != null) {
							int size = ammoList.size();
							for (int i = 0; i < size; i++) {
								bulletsRight.add(ItemStack.of((CompoundTag) ammoList.get(i)));
							}
						}
						magRight = (int)Math.floor(tool.getStats().get(JOTToolStats.MAX_AMMO));
						reserveRight = countReserveAmmo(tool);
					}
				}
				else if (context.getChangedSlot() == EquipmentSlot.OFFHAND) {
					bulletsLeft.clear();
					reserveLeft = 0;
					magLeft = 0;
					IToolStackView tool = context.getToolInSlot(EquipmentSlot.OFFHAND);
					if (tool != null && tool.hasTag(JOTItemTags.MOD_GUNS)) {
						ListTag ammoList = (ListTag) tool.getPersistentData().get(ModifiableGunItem.GUN_AMMO);
						if (ammoList != null) {
							int size = ammoList.size();
							for (int i = 0; i < size; i++) {
								bulletsLeft.add(ItemStack.of((CompoundTag) ammoList.get(i)));
							}
						}
						magLeft = (int)Math.floor(tool.getStats().get(JOTToolStats.MAX_AMMO));
						reserveLeft = countReserveAmmo(tool);
					}
				}
			}
		}

		private static void calcPos(List<Vector2i> list, int magSize) {
			float angleIncrement = (float) (2*Math.PI/magSize);
		}
		private static int countReserveAmmo(IToolStackView tool) {
			List<ItemStack> bulletStacks = new ArrayList<>();
			tool.getModifierList().forEach((entry) -> {
				if (ModifierManager.isInTag(entry.getId(), JOTModifierTags.BULLET_SUPPLYING)) entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, entry, bulletStacks);
			});
			return bulletStacks.stream().filter(stack -> stack.is(JOTItems.BULLET.get())).mapToInt(ItemStack::getCount).sum();
		}

		@SubscribeEvent
		public static void renderSlots(RenderGuiOverlayEvent.Post event) {
			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			if (mc.options.hideGui || (mc.screen != null && mc.screen.isPauseScreen()) && event.getOverlay() != VanillaGuiOverlay.HOTBAR.type() || player == null || player != mc.getCameraEntity()) {
				return;
			}

			if (JOTConfig.CLIENT.bulletDisplay.get() == JOTConfig.Client.BulletDisplayOption.NONE) return;

			MultiPlayerGameMode playerController = mc.gameMode;
			if (playerController != null && playerController.getPlayerMode() != GameType.SPECTATOR) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();

				int scaledWidth = mc.getWindow().getGuiScaledWidth();
				int scaledHeight = mc.getWindow().getGuiScaledHeight();
				GuiGraphics graphics = event.getGuiGraphics();
				float partialTicks = event.getPartialTick();
				if (JOTConfig.CLIENT.bulletDisplay.get() == JOTConfig.Client.BulletDisplayOption.RADIAL) {
					if (magRight != 0) {
						int count = bulletsRight.size();
						// determine placement of the items
						Orientation2D location = Orientation2D.BOTTOM_RIGHT;
						Orientation2D.Orientation1D xOrientation = location.getX();
						Orientation2D.Orientation1D yOrientation = location.getY();
						int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2);
						int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);
						if (!bulletsRight.isEmpty()) {
							float angleIncrement = (float) (2 * Math.PI / magRight);
							for (int i = 0; i < count; i++) {
//						mc.gui.renderSlot(graphics, (int) (xStart + (SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))), partialTicks, player, bulletsRight.get(i), i);
								graphics.renderItem(bulletsRight.get(i), (int) (xStart + (SLOT_BACKGROUND_SIZE * 1 * Mth.cos(angleIncrement * i - Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE * 1 * Mth.sin(angleIncrement * i - Mth.HALF_PI))));
							}
						}
						if (JOTConfig.CLIENT.showReserve.get() && reserveRight != 0) {
							graphics.drawString(Minecraft.getInstance().font, String.valueOf(reserveRight), xStart + 6, yStart + 4, 0xffffff);
							graphics.blit(xStart - 8, yStart, 0, 16, 16, mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(JoyOfTinkering.id("gui/gun/reserve_ammo")));
						}
					}
					if (magLeft != 0) {
						int count = bulletsLeft.size();
						Orientation2D location = Orientation2D.BOTTOM_LEFT;
						Orientation2D.Orientation1D xOrientation = location.getX();
						Orientation2D.Orientation1D yOrientation = location.getY();
						int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2) + SLOT_BACKGROUND_SIZE + 4;
						int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);
						if (!bulletsLeft.isEmpty()) {
							float angleIncrement = (float) (2 * Math.PI / magLeft);
							for (int i = 0; i < count; i++) {
//						mc.gui.renderSlot(graphics, (int) (xStart + (-SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))), partialTicks, player, bulletsLeft.get(i), i);
								graphics.renderItem(bulletsLeft.get(i), (int) (xStart + (-SLOT_BACKGROUND_SIZE * 1 * Mth.cos(angleIncrement * i - Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE * 1 * Mth.sin(angleIncrement * i - Mth.HALF_PI))));
							}
						}
						if (JOTConfig.CLIENT.showReserve.get() && reserveLeft != 0) {
							graphics.drawString(Minecraft.getInstance().font, String.valueOf(reserveLeft), xStart + 6, yStart + 4, 0xffffff);
							graphics.blit(xStart - 8, yStart, 0, 16, 16, mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(JoyOfTinkering.id("gui/gun/reserve_ammo")));
						}
					}
				} else if (JOTConfig.CLIENT.bulletDisplay.get() == JOTConfig.Client.BulletDisplayOption.NUMBERED) {
					if (magRight != 0) {
						int count = bulletsRight.size();
						// determine placement of the items
						Orientation2D location = Orientation2D.BOTTOM_RIGHT;
						Orientation2D.Orientation1D xOrientation = location.getX();
						Orientation2D.Orientation1D yOrientation = location.getY();
						int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2);
						int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);
//						Matrix4f pose = new Matrix4f().scale(1.1f);
//						GL33.glScaled(1.1f, 1.1f, 1.1f);
						graphics.pose().pushPose();
						String loaded = String.valueOf(count);
						Minecraft.getInstance().font.drawInBatch(loaded, (float) (xStart-5) / 1.5f, (float)(yStart) / 1.5f, 0xffffff, true, graphics.pose().last().pose().scale(1.5f), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0x000000, 0);
//						graphics.drawString(Minecraft.getInstance().font, String.valueOf(count), xStart, yStart + 4, 0xff0000);
						if (!bulletsRight.isEmpty()) {
							graphics.renderItem(bulletsRight.get(0), (int) ((xStart - 27) / 1.5f), (int) ((yStart - 6) / 1.5f));
						}
						graphics.pose().popPose();
//						GL33.glPopMatrix();
						graphics.drawString(Minecraft.getInstance().font, "/ " + magRight, xStart + 6 + ((loaded.length()-1)*8), yStart + 4, 0xffffff);
						if (bulletsRight.size() > 1) {
							graphics.renderItem(bulletsRight.get(1), (int) ((xStart - 25)), (int) ((yStart + 11)));
						}

						if (JOTConfig.CLIENT.showReserve.get() && reserveRight != 0) {
							graphics.drawString(Minecraft.getInstance().font, String.valueOf(reserveRight), xStart + 6, yStart + 15, 0xffffff);
							graphics.blit(xStart - 8, yStart+11, 0, 16, 16, mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(JoyOfTinkering.id("gui/gun/reserve_ammo")));
						}
					}
					if (magLeft != 0) {
						int count = bulletsLeft.size();
						Orientation2D location = Orientation2D.BOTTOM_LEFT;
						Orientation2D.Orientation1D xOrientation = location.getX();

						Orientation2D.Orientation1D yOrientation = location.getY();
						int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2) + SLOT_BACKGROUND_SIZE + 8;
						int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);

						graphics.pose().pushPose();
						String loaded = String.valueOf(count);
						Minecraft.getInstance().font.drawInBatch(loaded, (float) (xStart-5) / 1.5f, (float)(yStart) / 1.5f, 0xffffff, true, graphics.pose().last().pose().scale(1.5f), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0x000000, 0);
//						graphics.drawString(Minecraft.getInstance().font, String.valueOf(count), xStart, yStart + 4, 0xff0000);
						if (!bulletsLeft.isEmpty()) {
							graphics.renderItem(bulletsLeft.get(0), (int) ((xStart - 27) / 1.5f), (int) ((yStart - 6) / 1.5f));
						}
						graphics.pose().popPose();
//						GL33.glPopMatrix();
						graphics.drawString(Minecraft.getInstance().font, "/ " + magLeft, xStart + 6 + ((loaded.length()-1)*8), yStart + 4, 0xffffff);
						if (bulletsLeft.size() > 1) {
							graphics.renderItem(bulletsLeft.get(1), (xStart - 25), (yStart + 11));
						}

						if (JOTConfig.CLIENT.showReserve.get() && reserveLeft != 0) {
							graphics.drawString(Minecraft.getInstance().font, String.valueOf(reserveLeft), xStart + 6, yStart + 15, 0xffffff);
							graphics.blit(xStart - 8, yStart+11, 0, 16, 16, mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(JoyOfTinkering.id("gui/gun/reserve_ammo")));
						}
					}
				}
			}
		}
    }
}
