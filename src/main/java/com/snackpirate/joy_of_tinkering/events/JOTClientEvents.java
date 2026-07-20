package com.snackpirate.joy_of_tinkering.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.entity.ModifiableBulletRenderer;
import com.snackpirate.joy_of_tinkering.items.JOTHeadType;
import com.snackpirate.joy_of_tinkering.items.ModifiableGunItem;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
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
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
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
		private static final List<ItemStack> bulletsLeft = new ArrayList<>();
		private static int magLeft = 0;
		private static final int SLOT_BACKGROUND_SIZE = 22;
		@SubscribeEvent
		static void equipmentChange(ToolEquipmentChangeEvent event) {
//			JoyOfTinkering.LOGGER.info("equip change");
			if (event.getEntity() != Minecraft.getInstance().player) {
				return;
			}
			EquipmentChangeContext context = event.getContext();
			if (Config.CLIENT.renderItemFrame.get()) {
				if (context.getChangedSlot() == EquipmentSlot.MAINHAND) {
					bulletsRight.clear();
					IToolStackView tool = context.getToolInSlot(EquipmentSlot.MAINHAND);
					if (tool != null) {
						ListTag ammoList = (ListTag) tool.getPersistentData().get(ModifiableGunItem.GUN_AMMO);
						if (ammoList != null) {
							int size = ammoList.size();
							for (int i = 0; i < size; i++) {
								bulletsRight.add(ItemStack.of((CompoundTag) ammoList.get(i)));
							}
						}
						magRight = (int)Math.floor(tool.getStats().get(JOTToolStats.MAX_AMMO));
					}
				}
				else if (context.getChangedSlot() == EquipmentSlot.OFFHAND) {
					bulletsLeft.clear();
					IToolStackView tool = context.getToolInSlot(EquipmentSlot.OFFHAND);
					if (tool != null) {
						ListTag ammoList = (ListTag) tool.getPersistentData().get(ModifiableGunItem.GUN_AMMO);
						if (ammoList != null) {
							int size = ammoList.size();
							for (int i = 0; i < size; i++) {
								bulletsLeft.add(ItemStack.of((CompoundTag) ammoList.get(i)));
							}
						}
						magLeft = (int)Math.floor(tool.getStats().get(JOTToolStats.MAX_AMMO));
					}
				}
			}
		}
		@SubscribeEvent
		public static void renderSlots(RenderGuiOverlayEvent.Post event) {
			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			if (mc.options.hideGui || (mc.screen != null && mc.screen.isPauseScreen()) && event.getOverlay() != VanillaGuiOverlay.HOTBAR.type() || player == null || player != mc.getCameraEntity()) {
				return;
			}
			boolean renderBullets = Config.CLIENT.renderItemFrame.get() && (!bulletsRight.isEmpty() || !bulletsLeft.isEmpty());
			if (!renderBullets) return;

			MultiPlayerGameMode playerController = mc.gameMode;
			if (playerController != null && playerController.getPlayerMode() != GameType.SPECTATOR) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();

				int scaledWidth = mc.getWindow().getGuiScaledWidth();
				int scaledHeight = mc.getWindow().getGuiScaledHeight();
				GuiGraphics graphics = event.getGuiGraphics();
				float partialTicks = event.getPartialTick();
				if (!bulletsRight.isEmpty()) {
					// determine how many items need to be rendered
//					int columns = Config.CLIENT.itemsPerRow.get();
					int count = bulletsRight.size();
					// need to split items over multiple lines potentially
//					int rows = count / columns;
//					int inLastRow = count % columns;
//					// if we have an exact number, means we should have full in last row
//					if (inLastRow == 0) {
//						inLastRow = columns;
//					} else {
//						// we have an incomplete row that was not counted
//						rows++;
//					}
					// determine placement of the items
					Orientation2D location = Orientation2D.BOTTOM_RIGHT;
					Orientation2D.Orientation1D xOrientation = location.getX();
					Orientation2D.Orientation1D yOrientation = location.getY();
					int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2);
					int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);
					// if the map and item frame are at the same spot, offset item frame below

					// draw backgrounds
//					int lastRow = rows - 1;
//					for (int r = 0; r < lastRow; r++) {
//						for (int c = 0; c < columns; c++) {
//							graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
//						}
//					}
//					// last row will be aligned in the direction of x orientation (center, left, or right)
//					int lastRowOffset = xOrientation.align((columns - inLastRow) * 2) * SLOT_BACKGROUND_SIZE / 2;
//					for (int c = 0; c < inLastRow; c++) {
//						graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
//					}
//
//					// draw items
//					int i = 0;
//					xStart += 3;
//					yStart += 3; // offset from item start instead of frame start
//					for (int r = 0; r < lastRow; r++) {
//						for (int c = 0; c < columns; c++) {
//							mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, partialTicks, player, bulletsRight.get(i), i);
//							i++;
//						}
//					}
//					// align last row
//					for (int c = 0; c < inLastRow; c++) {
//						mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, partialTicks, player, bulletsRight.get(i), i);
//						i++;
//					}
					float angleIncrement = (float) (2*Math.PI/magRight);
					for (int i = 0; i < count; i++) {
//						mc.gui.renderSlot(graphics, (int) (xStart + (SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))), partialTicks, player, bulletsRight.get(i), i);
						graphics.renderItem(bulletsRight.get(i), (int) (xStart + (SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))));
					}
				}

				// determine how many items need to be rendered
				if (!bulletsLeft.isEmpty()) {
//					int columns = Config.CLIENT.itemsPerRow.get();
					int count = bulletsLeft.size();
					// need to split items over multiple lines potentially
//					int rows = count / columns;
//					int inLastRow = count % columns;
//					// if we have an exact number, means we should have full in last row
//					if (inLastRow == 0) {
//						inLastRow = columns;
//					} else {
//						// we have an incomplete row that was not counted
//						rows++;
//					}
					// determine placement of the items
					Orientation2D location = Orientation2D.BOTTOM_LEFT;
					Orientation2D.Orientation1D xOrientation = location.getX();
					Orientation2D.Orientation1D yOrientation = location.getY();
					int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * 2) + SLOT_BACKGROUND_SIZE+4;
					int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * 2);
					// if the map and item frame are at the same spot, offset item frame below

					// draw backgrounds
//					int lastRow = rows - 1;
//					for (int r = 0; r < lastRow; r++) {
//						for (int c = 0; c < columns; c++) {
//							graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
//						}
//					}
//					// last row will be aligned in the direction of x orientation (center, left, or right)
//					int lastRowOffset = xOrientation.align((columns - inLastRow) * 2) * SLOT_BACKGROUND_SIZE / 2;
//					for (int c = 0; c < inLastRow; c++) {
//						graphics.blit(Icons.ICONS, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
//					}

					// draw items
//					int i = 0;
//					xStart += 3;
//					yStart += 3; // offset from item start instead of frame start
//					for (int r = 0; r < lastRow; r++) {
//						for (int c = 0; c < columns; c++) {
//							mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, partialTicks, player, bulletsLeft.get(i), i);
//							i++;
//						}
//					}
//					// align last row
//					for (int c = 0; c < inLastRow; c++) {
//						mc.gui.renderSlot(graphics, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, partialTicks, player, bulletsLeft.get(i), i);
//						i++;
//					}
					float angleIncrement = (float) (2*Math.PI/magLeft);
					for (int i = 0; i < count; i++) {
//						mc.gui.renderSlot(graphics, (int) (xStart + (-SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))), partialTicks, player, bulletsLeft.get(i), i);
						graphics.renderItem(bulletsLeft.get(i), (int) (xStart + (-SLOT_BACKGROUND_SIZE*1*Mth.cos(angleIncrement*i-Mth.HALF_PI))), (int) (yStart + (SLOT_BACKGROUND_SIZE*1*Mth.sin(angleIncrement*i-Mth.HALF_PI))));
					}
				}
			}
		}
    }
}
