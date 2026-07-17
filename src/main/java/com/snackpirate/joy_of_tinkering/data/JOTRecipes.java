package com.snackpirate.joy_of_tinkering.data;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.*;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import static com.snackpirate.joy_of_tinkering.registries.JOTModifierIds.*;

import java.util.Collections;
import java.util.function.Consumer;

public class JOTRecipes extends RecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper, ISmelteryRecipeHelper, ICommonRecipeHelper {

	public JOTRecipes(PackOutput pOutput) {
		super(pOutput);
	}


	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		String armorFolder = "tools/armor/";
		String upgradeFolder = "tools/modifiers/upgrade/";
		String abilityFolder = "tools/modifiers/ability/";
		String slotlessFolder = "tools/modifiers/slotless/";
		String defenseFolder = "tools/modifiers/defense/";
		String compatFolder = "tools/modifiers/compat/";
		String salvageFolder = "tools/modifiers/salvage/";
		String worktableFolder = "tools/modifiers/worktable/";
		String materialsFolder = "tools/materials/";
		String recyclingFolder = "tools/recycling/";
		String castingFolder = "smeltery/casting/";
		String alloyFolder = "smeltery/alloys/";
		String partsFolder = "tools/parts/";

		String upgradeSalvage = salvageFolder + "upgrade/";
		String abilitySalvage = salvageFolder + "ability/";
		String defenseSalvage = salvageFolder + "defense/";
		String compatSalvage = salvageFolder + "compat/";

		ModifierRecipeBuilder.modifier(wellRead)
				.addInput(Tags.Items.GLASS_PANES)
				.addInput(TinkerMaterials.steel.getIngotTag())
				.addInput(Tags.Items.GLASS_PANES)
				.setSlots(SlotType.UPGRADE, 1)
				.setTools(TinkerTags.Items.HELMETS)
				.saveSalvage(consumer, location(salvageFolder + "well_read"))
				.save(consumer, JoyOfTinkering.id(upgradeFolder + "well_read"));
		ModifierRecipeBuilder.modifier(aquambulant)
				.addInput(Items.TROPICAL_FISH)
				.addInput(Items.HEART_OF_THE_SEA)
				.addInput(Items.TROPICAL_FISH)
				.addInput(Items.KELP)
				.addInput(Items.KELP)
				.setSlots(SlotType.ABILITY, 1)
				.setMaxLevel(2)
				.setTools(TinkerTags.Items.BOOTS)
				.saveSalvage(consumer, location(salvageFolder + "aquambulant"))
				.save(consumer, JoyOfTinkering.id(abilityFolder + "aquambulant"));
		ModifierRecipeBuilder.modifier(delicate)
				.addInput(TinkerModifiers.silkyCloth)
				.addInput(ItemTags.WOOL)
				.addInput(TinkerModifiers.silkyCloth)
				.setMaxLevel(1)
				.setTools(TinkerTags.Items.HARVEST)
				.save(consumer, location(slotlessFolder + "delicate"));

		slimeskull(consumer, MaterialIds.earthslime,   JOTItems.BUCKET_OF_SLIME,      armorFolder);
		slimeskull(consumer, MaterialIds.skyslime,  JOTItems.BUCKET_OF_SKYSLIME,   armorFolder);
		slimeskull(consumer, MaterialIds.enderslime,  JOTItems.BUCKET_OF_ENDERSLIME, armorFolder);
		slimeskull(consumer, MaterialIds.magma, JOTItems.BUCKET_OF_MAGMA_CUBE, armorFolder);
		slimeskull(consumer, MaterialIds.clay, JOTItems.BUCKET_OF_TERRACUBE,  armorFolder);

		PartBuilderToolRecycleBuilder.tool(JOTItems.CRESTED_HELMET)
				.part(TinkerToolParts.plating.get(ArmorItem.Type.HELMET))
				.part(TinkerToolParts.broadAxeHead)
				.save(consumer, location(recyclingFolder + "crested_helmet"));
		PartBuilderToolRecycleBuilder.tool(JOTItems.ROCKPUNCHERS)
				.part(TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE))
				.part(TinkerToolParts.pickHead)
				.save(consumer, location(recyclingFolder + "rockpunchers"));
		PartBuilderToolRecycleBuilder.tool(JOTItems.LAVA_LOAFERS)
				.part(TinkerToolParts.plating.get(ArmorItem.Type.BOOTS))
				.save(consumer, location(recyclingFolder + "lava_loafers"));

		partRecipes(consumer, JOTItems.BULLET_CASING, JOTItems.bulletCasingCast, 1, partsFolder, castingFolder);
		partRecipes(consumer, JOTItems.GUN_BARREL, JOTItems.gunBarrelCast, 2, partsFolder, castingFolder);
		partRecipes(consumer, JOTItems.FIRING_MECHANISM, JOTItems.firingMechanismCast, 3, partsFolder, castingFolder);

//		ToolBuildingRecipeBuilder.toolBuildingRecipe(JOTItems.BULLET.get()).addExtraRequirement(Ingredient.of(Items.GUNPOWDER)).addExtraMaterial(MaterialIds.gunpowder).outputSize(6).save(consumer, JoyOfTinkering.id(buildingFolder + "bullet"));
		ToolBuildingRecipeBuilder.toolBuildingRecipe(JOTItems.REVOLVER.get()).save(consumer, JoyOfTinkering.id(buildingFolder + "revolver"));
		ToolBuildingRecipeBuilder.toolBuildingRecipe(JOTItems.RIFLE.get()).save(consumer, JoyOfTinkering.id(buildingFolder + "rifle"));

		bulletRecipe(Items.GUNPOWDER, MaterialIds.gunpowder, consumer);
		bulletRecipe(Items.BLAZE_POWDER, MaterialIds.blaze, consumer);
//		bulletRecipe(Items.REDSTONE, MaterialIds.redstone, consumer);
		bulletRecipe(Items.SUGAR, JOTMaterialIds.sugar, consumer);
		bulletRecipe(JOTItems.powderSnowBottle, MaterialIds.ice, consumer);
		bulletRecipe(Items.BONE_MEAL, MaterialIds.bone,  consumer);
		bulletRecipe(Items.GLOWSTONE_DUST, MaterialIds.glowstone, consumer);
		bulletRecipe(Items.PRISMARINE_CRYSTALS, MaterialIds.prismarine, consumer);
		bulletRecipe(Items.MAGMA_CREAM, MaterialIds.magma, consumer);

		ModifierRecipeBuilder.modifier(bulkBandolier)
				.addInput(Items.LEATHER)
				.addInput(Items.CHAIN)
				.addInput(Items.LEATHER)
				.addInput(Items.CHAIN)
				.addInput(Items.CHAIN)
				.setSlots(SlotType.ABILITY, 1)
				.setTools(JOTItemTags.MOD_GUNS)
				.saveSalvage(consumer, prefix(bulkBandolier, abilitySalvage))
				.save(consumer, prefix(bulkBandolier, abilityFolder));
		ModifierRecipeBuilder.modifier(trickBandolier)
				.addInput(TinkerModifiers.silkyCloth)
				.addInput(Items.CHAIN)
				.addInput(TinkerModifiers.silkyCloth)
				.addInput(Items.CHAIN)
				.addInput(Items.CHAIN)
				.setSlots(SlotType.ABILITY, 1)
				.saveSalvage(consumer, prefix(trickBandolier, abilitySalvage))
				.setTools(IntersectionIngredient.of(Ingredient.of(JOTItemTags.MOD_GUNS), Ingredient.of(TinkerTags.Items.INTERACTABLE)))
				.save(consumer, prefix(trickBandolier, abilityFolder));
		ModifierRecipeBuilder.modifier(extended)
				.addInput(Items.CHAIN)
				.addInput(Items.GUNPOWDER)
				.addInput(TinkerMaterials.steel.getIngotTag())
				.setSlots(SlotType.UPGRADE, 1)
				.setMaxLevel(4)
				.setTools(Ingredient.of(JOTItems.RIFLE))
				.saveSalvage(consumer, prefix(extended, upgradeSalvage))
				.save(consumer, prefix(extended, upgradeFolder));

		metalMaterialRecipe(consumer, JOTMaterialIds.slimebronze, materialsFolder, "slimebronze", false);
		materialMeltingCasting(consumer, JOTMaterialIds.slimebronze, JOTFluids.moltenSlimebronze, materialsFolder);
		metal(consumer, JOTFluids.moltenSlimebronze).metal();

		AlloyRecipeBuilder.alloy(JOTFluids.moltenSlimebronze, FluidValues.INGOT * 2)
				.addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
				.addInput(TinkerFluids.earthSlime.ingredient(FluidValues.SLIMEBALL))
				.addInput(TinkerFluids.moltenClay.ingredient(FluidValues.BRICK))
				.save(consumer, prefix(JOTFluids.moltenSlimebronze, alloyFolder));
		metalCrafting(consumer, JOTBlocks.slimebronze, "crafting/materials");

//		ModifierRecipeBuilder.modifier(junkshot)
//				.addInput(TinkerMaterials.steel.getIngotTag(), 4)
//				.addInput(TinkerMaterials.cinderslime.getIngotTag(), 4)
//				.addInput(TinkerMaterials.steel.getIngotTag(), 4)
//				.addInput(Items.TNT, 4)
//				.addInput(Items.TNT, 4)
//				.setSlots(SlotType.ABILITY, 1)
//				.setTools(Ingredient.of(JOTItems.RIFLE))
//				.setMaxLevel(1)
//				.allowCrystal()
//				.saveSalvage(consumer, prefix(junkshot, abilitySalvage))
//				.save(consumer, prefix(junkshot, abilityFolder));

		ModifierRecipeBuilder.modifier(southpaw)
				.addInput(TinkerMaterials.steel.getIngotTag())
				.addInput(Items.COMPASS)
				.addInput(TinkerMaterials.steel.getIngotTag())
				.setMaxLevel(1).checkTraitLevel()
				.setSlots(SlotType.UPGRADE, 1)
				.setTools(IntersectionIngredient.of(Ingredient.of(JOTItems.REVOLVER), Ingredient.of(TinkerTags.Items.INTERACTABLE_LEFT))) // this is the same recipes as dual wielding, but crossbows do not interact on left
				.saveSalvage(consumer, prefix(southpaw, upgradeSalvage))
				.save(consumer, prefix(southpaw, upgradeFolder));

		ModifierRecipeBuilder.modifier(crackshot)
				.addInput(Items.TARGET)
				.addInput(Items.FLINT)
				.addInput(Items.GOLD_NUGGET)
				.setMaxLevel(5)
				.setSlots(SlotType.UPGRADE, 1)
				.setTools(Ingredient.of(JOTItems.REVOLVER))
				.saveSalvage(consumer, prefix(crackshot, upgradeSalvage))
				.save(consumer, prefix(crackshot, upgradeFolder));
		ModifierRecipeBuilder.modifier(burstFire)
				.addInput(Items.BLAZE_POWDER)
				.addInput(TinkerWorld.headItems.get(TinkerHeadType.BLAZE))
				.addInput(Items.BLAZE_POWDER)
				.addInput(SizedIngredient.of(MaterialIngredient.of(JOTItems.FIRING_MECHANISM)))
				.addInput(Items.FLINT)
				.setMaxLevel(5)
				.setSlots(SlotType.ABILITY, 1)
				.setTools(Ingredient.of(JOTItems.REVOLVER))
				.saveSalvage(consumer, prefix(burstFire, abilitySalvage))
				.save(consumer, prefix(burstFire, abilityFolder));
		materialMeltingComposite(consumer, MaterialIds.vine, JOTMaterialIds.shimmervine, TinkerFluids.moltenEmerald, FluidValues.GEM, materialsFolder);
		materialMeltingComposite(consumer, MaterialIds.twistingVine, JOTMaterialIds.twistedShimmervine, TinkerFluids.moltenEmerald, FluidValues.GEM, materialsFolder);
		materialMeltingComposite(consumer, MaterialIds.weepingVine, JOTMaterialIds.crimsonShimmervine, TinkerFluids.moltenEmerald, FluidValues.GEM, materialsFolder);

		ItemCastingRecipeBuilder.tableRecipe(JOTItems.powderSnowBottle)
				.setFluid(TinkerFluids.powderedSnow.ingredient(FluidValues.BOTTLE))
				.setCoolingTime(1)
				.setCast(Items.GLASS_BOTTLE, true)
				.save(consumer, location(castingFolder + "bottle"));
	}

	private static final String buildingFolder = "tools/building/";
	private static void bulletRecipe(ItemLike inputItem, MaterialVariantId material, Consumer<FinishedRecipe> consumer) {
		ToolBuildingRecipeBuilder.toolBuildingRecipe(JOTItems.BULLET.get()).addExtraRequirement(Ingredient.of(inputItem)).addExtraMaterial(material).outputSize(6).save(consumer, JoyOfTinkering.id(buildingFolder + "bullet_" + material.getId().getPath()));
	}
	public SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid) {
		return molten(consumer, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
	}

	private void slimeskull(Consumer<FinishedRecipe> consumer, MaterialId material, ItemLike skull, String folder) {
//		MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET))
//				.setCast(skull, ToolCastingRecipe.CastPurpose.FIRST_MATERIAL)
//				.addExtraMaterial(material)
//				.setItemCost(5)
//				.save(consumer, location(folder + "slime_skull/" + material.getPath()));
//		MaterialSwappingRecipeBuilder.tools(TinkerTags.Items.SWAPPABLE_SKULLS)
//				.index(0).material(material, skull).repairValue((int) (MaterialRecipe.INGOTS_PER_REPAIR * 2))
//				.save(consumer, location(folder + "slime_skull/swapping/" + material.getPath()));
		MaterialIdNBT nbt = new MaterialIdNBT(Collections.singletonList(material));
		ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromStack(nbt.updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET)))))
				.setCast(skull, true)
				.setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 5)
				.save(consumer, location(folder + "slime_skull/" + material.getPath()));
	}
	public static class FluidTransfer extends AbstractFluidContainerTransferProvider {

		public FluidTransfer(PackOutput packOutput) {
			super(packOutput, JoyOfTinkering.MOD_ID);
		}

		@Override
		protected void addTransfers() {
			addFillEmpty("powdered_snow_", JOTItems.powderSnowBottle, Items.GLASS_BOTTLE, TinkerFluids.powderedSnow, FluidValues.BOTTLE, false);
		}

		@Override
		public String getName() {
			return "Joy Of Tinkering Fluid Transfer Provider";
		}
		protected void addBottleFill(String name, ItemLike output, Fluid fluid) {
			addTransfer(name + "_fill", new FillFluidContainerTransfer(Ingredient.of(Items.GLASS_BOTTLE), ItemOutput.fromItem(output), FluidIngredient.of(fluid, FluidValues.BOTTLE)));
		}
	}

	@Override
	public String getModId() {
		return JoyOfTinkering.MOD_ID;
	}
}
