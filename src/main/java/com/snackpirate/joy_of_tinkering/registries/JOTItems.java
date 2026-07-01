package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tools.JOTToolDefinitionProvider;
import com.snackpirate.joy_of_tinkering.items.*;
import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.GunBarrelMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.List;
import java.util.function.Consumer;

public class JOTItems {
	public static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(JoyOfTinkering.MOD_ID);

	public static final ItemObject<SlimeBucketItem> BUCKET_OF_SLIME = ITEMS.register("bucket_of_slime", () -> new SlimeBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET), () -> EntityType.SLIME));
	public static final ItemObject<SlimeBucketItem> BUCKET_OF_MAGMA_CUBE = ITEMS.register("bucket_of_magma_cube", () -> new SlimeBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET), () -> EntityType.MAGMA_CUBE));
	public static final ItemObject<SlimeBucketItem> BUCKET_OF_TERRACUBE = ITEMS.register("bucket_of_terracube", () -> new SlimeBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET), TinkerWorld.terracubeEntity));
	public static final ItemObject<SlimeBucketItem> BUCKET_OF_SKYSLIME = ITEMS.register("bucket_of_skyslime", () -> new SlimeBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET), TinkerWorld.skySlimeEntity));
	public static final ItemObject<SlimeBucketItem> BUCKET_OF_ENDERSLIME = ITEMS.register("bucket_of_enderslime", () -> new SlimeBucketItem(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET), TinkerWorld.enderSlimeEntity));

	public static final ItemObject<ModifiableHeadItem> CRESTED_HELMET = ITEMS.register("crested_helmet", () -> new ModifiableHeadItem(new Item.Properties().stacksTo(1), JOTToolDefinitionProvider.CRESTED_HELMET));
	public static final ItemObject<MultilayerArmorItem> ROCKPUNCHERS = ITEMS.register("rockpunchers", () -> new MultilayerArmorItem(ModifiableArmorMaterial.create(JoyOfTinkering.id("rockpuncher"), SoundEvents.ARMOR_EQUIP_CHAIN), ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
	public static final ItemObject<MultilayerArmorItem> LAVA_LOAFERS = ITEMS.register("lava_loafers", () -> new MultilayerArmorItem(ModifiableArmorMaterial.create(JoyOfTinkering.id("strider"), SoundEvents.ARMOR_EQUIP_LEATHER), ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

	public static final ItemObject<ToolPartItem> BULLET_CASING = ITEMS.register("bullet_casing", () -> new ToolPartItem(new Item.Properties().stacksTo(64), JOTToolStats.Statless.BULLET_CASING.getIdentifier()));
	public static final ItemObject<ToolPartItem> GUN_BARREL = ITEMS.register("gun_barrel", () -> new ToolPartItem(new Item.Properties().stacksTo(64), GunBarrelMaterialStats.ID));
	public static final ItemObject<ToolPartItem> FIRING_MECHANISM = ITEMS.register("firing_mechanism", () -> new ToolPartItem(new Item.Properties().stacksTo(64), FiringMechanismMaterialStats.ID));

	public static final CastItemObject bulletCasingCast = ITEMS.registerCast("bullet_casing", new Item.Properties());
	public static final CastItemObject gunBarrelCast = ITEMS.registerCast("gun_barrel", new Item.Properties());
	public static final CastItemObject firingMechanismCast = ITEMS.registerCast("firing_mechanism", new Item.Properties());

	public static final ItemObject<ModifiableBulletItem> BULLET = ITEMS.register("bullet", () -> new ModifiableBulletItem(new Item.Properties().stacksTo(64), JOTToolDefinitionProvider.BULLET));
	public static final ItemObject<ModifiableGunItem> REVOLVER = ITEMS.register("revolver", () -> new ModifiableGunItem(new Item.Properties().stacksTo(1), JOTToolDefinitionProvider.REVOLVER));
	public static final ItemObject<ModifiableGunItem> RIFLE = ITEMS.register("rifle", () -> new ModifiableGunItem(new Item.Properties().stacksTo(1), JOTToolDefinitionProvider.RIFLE));

	public static final ItemObject<ModifiableGunItem> DECIMATOR = ITEMS.register("decimator", () -> new ModifiableDecimator(new Item.Properties().stacksTo(1), JOTToolDefinitionProvider.DECIMATOR));

	public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
		output.accept(JOTBlocks.slimebronze.getIngot());
		output.accept(JOTBlocks.slimebronze.getNugget());
		output.accept(JOTBlocks.slimebronze);
		output.accept(JOTFluids.moltenSlimebronze.getBucket());
		output.accept(BUCKET_OF_SLIME);
		output.accept(BUCKET_OF_MAGMA_CUBE);
		output.accept(BUCKET_OF_TERRACUBE);
		output.accept(BUCKET_OF_SKYSLIME);
		output.accept(BUCKET_OF_ENDERSLIME);

		output.accept(ToolBuildHandler.buildItemFromMaterials(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.earthslime), MaterialRegistry.getMaterial(MaterialIds.earthslime))));
		output.accept(ToolBuildHandler.buildItemFromMaterials(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.magma), MaterialRegistry.getMaterial(MaterialIds.magma))));
		output.accept(ToolBuildHandler.buildItemFromMaterials(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.clay), MaterialRegistry.getMaterial(MaterialIds.clay))));
		output.accept(ToolBuildHandler.buildItemFromMaterials(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.skyslime), MaterialRegistry.getMaterial(MaterialIds.skyslime))));
		output.accept(ToolBuildHandler.buildItemFromMaterials(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), MaterialNBT.of(MaterialRegistry.getMaterial(MaterialIds.enderslime), MaterialRegistry.getMaterial(MaterialIds.enderslime))));

		ToolBuildHandler.addVariants(output::accept, JOTItems.REVOLVER.get(), "");
		ToolBuildHandler.addVariants(output::accept, JOTItems.RIFLE.get(), "");
//		ToolBuildHandler.addVariants(output::accept, JOTItems.BULLET.get(), "");
		addBulletVariants(output::accept);

		ToolBuildHandler.addVariants(output::accept, JOTItems.DECIMATOR.get(), "");
		ToolBuildHandler.addVariants(output::accept, JOTItems.CRESTED_HELMET.get(), "");
		ToolBuildHandler.addVariants(output::accept, JOTItems.ROCKPUNCHERS.get(), "");
		ToolBuildHandler.addVariants(output::accept, JOTItems.LAVA_LOAFERS.get(), "");

		BULLET_CASING.get().addVariants(output::accept, "");
		GUN_BARREL.get().addVariants(output::accept, "");
		FIRING_MECHANISM.get().addVariants(output::accept, "");
		output.accept(bulletCasingCast.get());
		output.accept(gunBarrelCast.get());
		output.accept(firingMechanismCast.get());
		output.accept(bulletCasingCast.getSand());
		output.accept(gunBarrelCast.getSand());
		output.accept(firingMechanismCast.getSand());
		output.accept(bulletCasingCast.getRedSand());
		output.accept(gunBarrelCast.getRedSand());
		output.accept(firingMechanismCast.getRedSand());
		ModifierRecipeLookup.getRecipeModifierList().forEach(modifier -> {
			if (!ModifierManager.isInTag(modifier.getId(), TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST) && modifier.getId().getNamespace().equals(JoyOfTinkering.MOD_ID)) {
				output.accept(ModifierCrystalItem.withModifier(modifier.getId()));
			}
		});
	}

	private static void addBulletVariants(Consumer<ItemStack> tab) {
		for (IMaterial material : MaterialRegistry.getInstance().getVisibleMaterials()) {
			// if we added it and we want a single material, we are done
			ItemStack tool = createSingleMaterial(JOTItems.BULLET.get(), MaterialVariant.of(material));
			if (!tool.isEmpty()) {
				tab.accept(tool);
			}
		}
	}

	public static ItemStack createSingleMaterial(IModifiable item, MaterialVariant material) {
		List<MaterialStatsId> required = ToolMaterialHook.stats(item.getToolDefinition());
		MaterialNBT.Builder materials = MaterialNBT.builder();
		boolean useMaterial = false;
		for (MaterialStatsId requirement : required) {
			// try to use requested material
			if (requirement.canUseMaterial(material.getId())) {
				materials.add(material);
				useMaterial = true;
			} else {
				// fallback to first that works
//				JoyOfTinkering.LOGGER.info("required stat {}", requirement.getPath());
				IMaterial fallback = switch (requirement.getPath()) {
					case "arrow_head" -> MaterialRegistry.getMaterial(MaterialIds.flint);
					case "propellant" -> MaterialRegistry.getMaterial(MaterialIds.gunpowder);
					default -> MaterialRegistry.getMaterial(MaterialIds.iron);
				};
				materials.add(fallback);
			}
		}
		// only report success if we actually used the material somewhere
		if (useMaterial) {
			return ToolBuildHandler.buildItemFromMaterials(item, materials.build());
		}
		return ItemStack.EMPTY;
	}

}
