package com.snackpirate.joy_of_tinkering.data.tools;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.items.tools.PropellantMaterialStats;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.display.FixedMaterialToolName;
import slimeknights.tconstruct.library.tools.definition.module.material.*;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.List;

public class JOTToolDefinitionProvider extends AbstractToolDefinitionDataProvider {
	public static final ToolDefinition REVOLVER = ToolDefinition.create(JOTItems.REVOLVER);
	public static final ToolDefinition RIFLE = ToolDefinition.create(JOTItems.RIFLE);
	public static final ToolDefinition BULLET = ToolDefinition.create(JOTItems.BULLET);
	public static final ToolDefinition CRESTED_HELMET = ToolDefinition.create(JOTItems.CRESTED_HELMET);
	public static final ToolDefinition DECIMATOR = ToolDefinition.create(JOTItems.DECIMATOR);

	public JOTToolDefinitionProvider(PackOutput packOutput, String modId) {
		super(packOutput, modId);
	}

	@Override
	protected void addToolDefinitions() {
		RandomMaterial tier1Material = RandomMaterial.random().tier(1).build();
		DefaultMaterialsModule defaultOneParts = DefaultMaterialsModule.builder().material(tier1Material).build();
		DefaultMaterialsModule defaultTwoParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material).build();
		DefaultMaterialsModule defaultThreeParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material).build();
		DefaultMaterialsModule defaultFourParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material, tier1Material).build();

		define(CRESTED_HELMET)
				.module(MaterialStatsModule.stats()
						.stat(PlatingMaterialStats.HELMET, 1f)
						.stat(HeadMaterialStats.ID, 1f)
						.build())
				.module(ToolSlotsModule.builder()
						.slots(SlotType.ABILITY, 1)
						.slots(SlotType.UPGRADE, 2)
						.slots(SlotType.DEFENSE, 2)
						.build())
				.module(ToolTraitsModule.builder()
						.trait(JOTModifierIds.headbutt).build())
				.module(new SetStatsModule(StatsNBT.builder()
						.set(ToolStats.ATTACK_DAMAGE, 3f).build()))
				.module(new MaterialTraitsModule(HeadMaterialStats.ID, 1), ToolHooks.REBALANCED_TRAIT)
				.module(new MultiplyStatsModule(MultiplierNBT.builder()
						.set(ToolStats.DURABILITY, 0.5f)
						.build()))
				.module(defaultTwoParts)
				.build();

		define(JoyOfTinkering.id("rockpuncher_chestplate"))
				.module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
				.module(MaterialStatsModule.stats()
						.stat(PlatingMaterialStats.CHESTPLATE, 0.75f)
						.stat(HeadMaterialStats.ID, 1f)
						.build())
				.module(ToolTraitsModule.builder()
						.trait(TinkerModifiers.ambidextrous.getId(), 1)
						.trait(JOTModifierIds.fistMining, 1)
						.build())
				.module(ToolSlotsModule.builder()
						.slots(SlotType.ABILITY, 1)
						.slots(SlotType.UPGRADE, 2)
						.slots(SlotType.DEFENSE, 2)
						.build())
				.module(new MultiplyStatsModule(MultiplierNBT.builder()
						.set(ToolStats.ATTACK_DAMAGE, 0.4f)
						.set(ToolStats.DURABILITY, 0.5f)
						.build()))
				.module(new MaterialTraitsModule(HeadMaterialStats.ID, 1), ToolHooks.REBALANCED_TRAIT)
				.module(new SetStatsModule(StatsNBT.builder().set(ToolStats.HARVEST_TIER, Tiers.STONE).build()))
				.module(defaultTwoParts)
				.build();
		define(JoyOfTinkering.id("rockpuncher_helmet")).build();
		define(JoyOfTinkering.id("rockpuncher_leggings")).build();
		define(JoyOfTinkering.id("rockpuncher_boots")).build();

		define(JoyOfTinkering.id("strider_boots"))
				.module(MaterialStatsModule.stats()
						.stat(PlatingMaterialStats.BOOTS, 0.75f)
						.build())
				.module(ToolTraitsModule.builder()
						.trait(JOTModifierIds.surfaceStrider, 1)
						.trait(ModifierIds.worldbound, 1)
						.build())
				.module(ToolSlotsModule.builder()
						.slots(SlotType.ABILITY, 1)
						.slots(SlotType.UPGRADE, 2)
						.slots(SlotType.DEFENSE, 1)
						.build())
				.module(defaultOneParts)
				.build();
		define(JoyOfTinkering.id("strider_chestplate")).build();
		define(JoyOfTinkering.id("strider_leggings")).build();
		define(JoyOfTinkering.id("strider_helmet")).build();

		RandomMaterial nonHiddenMaterial = RandomMaterial.random().build();
		define(BULLET)
//				.module(PartStatsModule.parts()
//						.part(TinkerToolParts.arrowHead)
//						.part(JOTItems.BULLET_CASING)
//						.build())
				.module(MaterialStatsModule.stats()
						.stat(StatlessMaterialStats.ARROW_HEAD)
						.stat(JOTToolStats.Statless.BULLET_CASING)
						.stat(PropellantMaterialStats.ID)
						.build())
				.module(new PartsModule(List.of(TinkerToolParts.arrowHead.get(), JOTItems.BULLET_CASING.get())))
				.module(new SetStatsModule(StatsNBT.builder()
						.set(ToolStats.PROJECTILE_DAMAGE, 0f)
						.build()))
				.module(DefaultMaterialsModule.builder().material(nonHiddenMaterial, nonHiddenMaterial, nonHiddenMaterial).build())
				// display the arrow head, despite not being repairable
				.module(FixedMaterialToolName.FIRST)
				.build();
        //note to self: AnimationUtils#animateCrossbowHold
		define(REVOLVER)
				.module(new SetStatsModule(StatsNBT.builder()
						.set(ToolStats.PROJECTILE_DAMAGE, 0.5f)
						.set(ToolStats.ATTACK_DAMAGE, 0f)
						.set(ToolStats.ATTACK_SPEED, 1.0f)
						.set(ToolStats.VELOCITY, 0.8f)
						.build()))
				.module(new MultiplyStatsModule(MultiplierNBT.builder()
						.set(ToolStats.DURABILITY, 1.1f)
						.set(ToolStats.ACCURACY, 0.6f)
						.build()))
				.module(PartStatsModule.parts()
						.part(JOTItems.GUN_BARREL)
						.part(JOTItems.FIRING_MECHANISM)
						.part(TinkerToolParts.bowGrip)
						.build())
				.module(defaultThreeParts)
				.module(FixedMaterialToolName.FIRST)
				.smallToolStartingSlots()
				.build();

		define(RIFLE)
				.module(new SetStatsModule(StatsNBT.builder()
						.set(ToolStats.PROJECTILE_DAMAGE, 0.5f)
						.set(ToolStats.ATTACK_DAMAGE, 0f)
						.set(ToolStats.ATTACK_SPEED, 1.0f)
						.set(ToolStats.VELOCITY, 0.9f)
						.set(JOTToolStats.MAX_AMMO, 4)
						.build()))
				.module(new MultiplyStatsModule(MultiplierNBT.builder()
						.set(ToolStats.DURABILITY, 1.3f)
						.set(ToolStats.PROJECTILE_DAMAGE, 0.75f)
						.set(ToolStats.ACCURACY, 0.85f)
						.set(ToolStats.DRAW_SPEED, 0.75f)
						.build()))
				.module(PartStatsModule.parts()
						.part(JOTItems.GUN_BARREL, 0.75f)
						.part(JOTItems.GUN_BARREL, 0.75f)
						.part(JOTItems.FIRING_MECHANISM)
						.part(TinkerToolParts.bowGrip)
						.build())
				.module(defaultFourParts)
				.module(FixedMaterialToolName.FIRST)
				.largeToolStartingSlots()
				.build();
		define(DECIMATOR)
				.module(MaterialStatsModule.stats()
						.stat(HeadMaterialStats.ID, 0.35f)
						.stat(FiringMechanismMaterialStats.ID)
						.stat(HeadMaterialStats.ID, 0.35f)
						.stat(HandleMaterialStats.ID)
						.build())
				.module(ToolSlotsModule.builder()
						.slots(SlotType.ABILITY, 1)
						.slots(SlotType.UPGRADE, 2)
						.build())
				.module(new SetStatsModule(StatsNBT.builder()
						.set(ToolStats.PROJECTILE_DAMAGE, 0.5f)
						.set(ToolStats.VELOCITY, 0.8f)
						.set(ToolStats.ATTACK_DAMAGE, 3f)
						.set(ToolStats.ATTACK_SPEED, 0.75f).build()))
				.module(new MultiplyStatsModule(MultiplierNBT.builder()
						.set(ToolStats.ATTACK_DAMAGE, 1.35f)
						.set(ToolStats.MINING_SPEED, 0.4f)
						.set(ToolStats.DURABILITY, 2f).build()))
				.module(new MaterialTraitsModule(FiringMechanismMaterialStats.ID, 1), ToolHooks.REBALANCED_TRAIT)
				.build();
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Tool Definitions";
	}
}
