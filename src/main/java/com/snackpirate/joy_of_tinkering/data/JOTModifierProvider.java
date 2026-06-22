package com.snackpirate.joy_of_tinkering.data;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTBlockTags;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.modifiers.*;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.CraftCountModule;
import slimeknights.tconstruct.tools.modules.armor.FieryCounterModule;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Set;

import static com.snackpirate.joy_of_tinkering.registries.JOTModifierIds.*;

public class JOTModifierProvider extends AbstractModifierProvider {

	public static final ToolStackPredicate HAS_OVERSLIME = ToolStackPredicate.simple((tool) -> tool.getPersistentData().getInt(TinkerModifiers.overslime.getId()) > 3);

	public JOTModifierProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void addModifiers() {
		buildModifier(wellRead).priority(95)
				.addModule(new WellReadModule(Set.of(TooltipKey.NORMAL, TooltipKey.SHIFT)))
				.addModule(InventoryModule.builder().pattern(new Pattern(JoyOfTinkering.id("book"))).filter(ItemPredicate.tag(TinkerTags.Items.BOOKS)).slots(0, 1))
//				.addModule(InventoryMenuModule.SHIFT)
				.build();
		buildModifier(delicate)
				.addModule(new SingleInstaBreakModule(BlockPredicate.ANY))
				.priority(50) //after momentum and fist minging
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.build();
		buildModifier(aquambulant)
				.addModule(new FluidWalkerModule(BlockPredicate.set(Blocks.WATER), new LevelingInt(3, -1), 0.05f))
				.levelDisplay(ModifierLevelDisplay.DEFAULT)
				.build();
		buildModifier(surfaceStrider)
				.addModule(new FluidWalkerModule(BlockPredicate.tag(JOTBlockTags.SURFACE_STRIDER_FLUIDS), new LevelingInt(0,0), 0.1f))
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.build();
		buildModifier(fistMining)
				.addModule(FistMiningModule.INSTANCE)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.priority(71)
				.build();
		buildModifier(terraguard)
				.levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
				.addModule(TerraguardModule.INSTANCE)
				.build();
		buildModifier(terracubeDisguise)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new MobDisguiseModule(TinkerWorld.terracubeEntity.get()))
				.build();
		buildModifier(oversharing)
				.levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
				.addModule(OversharingModule.INSTANCE)
				.addModule(StatBoostModule.add(OverslimeModule.OVERSLIME_STAT).eachLevel(50))
				.build();
		buildModifier(slimeDisguise)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new MobDisguiseModule(EntityType.SLIME))
				.build();
        buildModifier(magmadaptive)
                .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
                .addModule(MagmadaptiveModule.INSTANCE)
				.build();
        buildModifier(magmaCubeDisguise)
                .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
                .addModule(new MobDisguiseModule(EntityType.MAGMA_CUBE))
				.build();
		buildModifier(endrobe)
				.levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
				.addModule(new EndrobeModule(Set.of(TooltipKey.NORMAL)))
				.addModule(EndrobeInventoryModule.builder().key(endrobe).slotsPerLevel(3))
				.addModule(InventoryMenuModule.SHIFT)
				.build();
		buildModifier(enderslimeDisguise)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new MobDisguiseModule(TinkerWorld.enderSlimeEntity.get()))
				.build();
		buildModifier(skyslimeDisguise)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new MobDisguiseModule(TinkerWorld.skySlimeEntity.get()))
				.build();
		buildModifier(surplus)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new CraftCountModule(LevelingValue.flat(1.34f)))
				.addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).flat(-0.5f))
				.build();
		buildModifier(scarce)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(new CraftCountModule(LevelingValue.flat(0.67f)))
				.addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).flat(0.5f))
				.build();
		buildModifier(gunflinger)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.addModule(GunflingerModule.INSTANCE)
				.build();

		buildModifier(bulkBandolier)
				.priority(60)
				.addModule(InventoryModule.builder().pattern(new Pattern(JoyOfTinkering.id("bullet"))).filter(ItemPredicate.set(JOTItems.BULLET.get())).slots(0, 3))
				.addModule(new BulkBandolierModule(true))
				.addModule(InventoryMenuModule.ANY)
				.build();
		buildModifier(trickBandolier)
				.priority(70)
				.addModule(InventoryModule.builder().pattern(new Pattern(JoyOfTinkering.id("tipped_bullet"))).filter(ItemPredicate.set(JOTItems.BULLET.get())).flatLimit(32).slotsPerLevel(3))
				.addModule(TrickBandolierModule.INSTANCE)
				.addModule(InventoryMenuModule.ANY)
				.build();

		buildModifier(extended)
				.addModule(StatBoostModule.add(JOTToolStats.MAX_AMMO).eachLevel(1))
				.build();
		buildModifier(overheat)
				.addModule(new OverheatModule(LevelingValue.eachLevel(5)))
				.addModule(MobEffectModule.builder(JOTEffects.overheat.get()).tool(ToolStackPredicate.and(HAS_OVERSLIME, ToolStackPredicate.context(ToolContextPredicate.fallback(ItemPredicate.tag(TinkerTags.Items.AMMO))).inverted())).time(RandomLevelingValue.perLevel(0, 100)).level(RandomLevelingValue.perLevel(0, 1)).build())
				.addModule(MobEffectModule.builder(JOTEffects.overheat.get()).tool(ToolStackPredicate.context(ToolContextPredicate.fallback(ItemPredicate.tag(TinkerTags.Items.AMMO)))).level(RandomLevelingValue.flat(2)).chance(LevelingValue.flat(1)).time(RandomLevelingValue.flat(100)).build())
				.addModule(FieryCounterModule.builder().tool(ToolStackPredicate.and(HAS_OVERSLIME, ToolStackPredicate.context(ToolContextPredicate.fallback(ItemPredicate.tag(TinkerTags.Items.ARMOR))))).chance(LevelingValue.flat(0.15f)).constant(LevelingValue.eachLevel(2.5f)).durabilityUsage(3).randomFlat(2).build())
				.build();
		buildModifier(headbutt)
//				.addModule(new HeadbuttModule(LevelingValue.eachLevel(0.5f), 0.9f, 1.5f, LivingEntityPredicate.ANY, ModifierCondition.ANY_TOOL))
				.levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
				.build();
		buildModifier(junkshot)
				.addModule(JunkshotModule.INSTANCE)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.build();
		buildModifier(southpaw)
				.addModule(SouthpawModule.INSTANCE)
				.levelDisplay(ModifierLevelDisplay.NO_LEVELS)
				.build();
		buildModifier(crackshot)
				.addModule(new CrackshotModule(LivingEntityPredicate.ANY, LevelingValue.eachLevel(0.1f)))
				.build();
		buildModifier(burstFire)
				.addModule(BurstfireModule.INSTANCE)
//				.addModule(StatBoostModule.multiplyAll(ToolStats.PROJECTILE_DAMAGE).eachLevel(-0.2f))
				.levelDisplay(new ModifierLevelDisplay.UniqueForLevels(5, true))
				.build();
	}

	@Override
	public String getName() {
		return "Joy Of Tinkering Modifier Provider";
	}
}
