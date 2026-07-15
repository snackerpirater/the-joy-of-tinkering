package com.snackpirate.joy_of_tinkering.data.materials;

import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.GunBarrelMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.items.tools.PropellantMaterialStats;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.stats.*;

import static com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds.*;
import java.util.List;

public class JOTMaterialStats extends AbstractMaterialStatsDataProvider {
	public static final List<MaterialVariantId> gunMetals = List.of(
			MaterialIds.roseGold, MaterialIds.copper,
			MaterialIds.oxidizedCopper, MaterialIds.iron,
			MaterialIds.oxidizedIron, MaterialIds.steel,
			MaterialIds.slimesteel, MaterialIds.amethystBronze,
			MaterialIds.cobalt, MaterialIds.cinderslime,
			MaterialIds.queensSlime, MaterialIds.hepatizon,
			MaterialIds.manyullyn, MaterialIds.knightmetal,
			MaterialIds.steeleaf, MaterialIds.fiery,
			MaterialIds.ancient, MaterialIds.invar,
			MaterialIds.pewter, MaterialIds.constantan,
			MaterialIds.electrum, MaterialIds.bronze,
			MaterialIds.aluminum, MaterialIds.silver,
			MaterialIds.lead, MaterialIds.knightslime);
	public static final List<MaterialVariantId> bulletMetals = List.of(
			MaterialIds.copper, MaterialIds.iron,
			MaterialIds.steel, MaterialIds.amethystBronze,
			MaterialIds.cobalt, MaterialIds.hepatizon,
			MaterialIds.manyullyn, MaterialIds.knightmetal,
			MaterialIds.steeleaf, MaterialIds.slimesteel,
			MaterialIds.silver, MaterialIds.electrum);
	public static final List<MaterialVariantId> bulletPropellants = List.of(
			MaterialIds.gunpowder, MaterialIds.blaze, MaterialIds.redstone, MaterialIds.ice,
			MaterialIds.prismarine, MaterialIds.bone, MaterialIds.glowstone, MaterialIds.magma
	);
	public JOTMaterialStats(PackOutput packOutput, AbstractMaterialDataProvider materials) {
		super(packOutput, materials);
	}

	@Override
	protected void addMaterialStats() {
		addMaterialStats(MaterialIds.earthslime, StatlessMaterialStats.REPAIR_KIT, new SkullStats(120));
		addMaterialStats(MaterialIds.skyslime, StatlessMaterialStats.REPAIR_KIT, new SkullStats(90));
		addMaterialStats(MaterialIds.enderslime, StatlessMaterialStats.REPAIR_KIT, new SkullStats(120));
		addMaterialStats(MaterialIds.magma, StatlessMaterialStats.REPAIR_KIT, new SkullStats(100));
		addMaterialStats(MaterialIds.clay, StatlessMaterialStats.REPAIR_KIT, new SkullStats(150));
		//gun materials w/o bullet casings
		addMaterialStats(MaterialIds.roseGold,
				new GunBarrelMaterialStats(175, -0.25f, 0.2f),
				new FiringMechanismMaterialStats(5, 0.15f, 1.75f));
		addMaterialStats(MaterialIds.cinderslime,
				new GunBarrelMaterialStats(1221, 0, 0.2f),
				new FiringMechanismMaterialStats(8, -0.2f, 2.75f));
		addMaterialStats(MaterialIds.queensSlime,
				new GunBarrelMaterialStats(1650, -0.15f, 0.1f),
				new FiringMechanismMaterialStats(8, 0, 2.5f));
		addMaterialStats(MaterialIds.ancient,
				new GunBarrelMaterialStats(745, 0.1f, 0.1f));
		//gun materials w/ bullet casings
		addMaterialStats(MaterialIds.slimesteel,
				new GunBarrelMaterialStats(1040, -0.05f, 0.15f),
				new FiringMechanismMaterialStats(6, -0.05f, 2.25f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.copper,
				new GunBarrelMaterialStats(210, 0.05f, 0),
				new FiringMechanismMaterialStats(4, -0.1f, 1.5f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.iron,
				new GunBarrelMaterialStats(250, 0.1f, 0),
				new FiringMechanismMaterialStats(4, -0.2f, 1.75f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.steel,
				new GunBarrelMaterialStats(775, 0.15f, -0.1f),
				new FiringMechanismMaterialStats(6, -0.3f, 2.5f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.amethystBronze,
				new GunBarrelMaterialStats(720, 0.15f, -0.1f),
				new FiringMechanismMaterialStats(8, -0.25f, 2f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.cobalt,
				new GunBarrelMaterialStats(800, 0.05f, 0.05f),
				new FiringMechanismMaterialStats(6, 0.05f, 2f),
				JOTToolStats.Statless.BULLET_CASING);

		addMaterialStats(MaterialIds.hepatizon,
				new GunBarrelMaterialStats(975, -0.05f, 0.15f),
				new FiringMechanismMaterialStats(8, 0.15f, 2.25f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.manyullyn,
				new GunBarrelMaterialStats(1150, 0.20f, 0.1f),
				new FiringMechanismMaterialStats(6, -0.15f, 3f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.knightmetal,
				new GunBarrelMaterialStats(512, 0.05f, 0.1f),
				new FiringMechanismMaterialStats(8, 0.2f, 2.75f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.steeleaf,
				new GunBarrelMaterialStats(200, 0, 0.15f),
				new FiringMechanismMaterialStats(5, 0, 2f),
				JOTToolStats.Statless.BULLET_CASING);
		addMaterialStats(MaterialIds.fiery,
				new GunBarrelMaterialStats(1024, 0.2f, 0.05f),
				new FiringMechanismMaterialStats(8, -0.25f, 2.75f));
		gunFromBowStats(MaterialIds.knightslime,
				new LimbMaterialStats(1047, 0, 0.15f, -0.15f),
				new GripMaterialStats(-0.05f, 0.1f, 3.25f), 8, 2.5f);

		gunFromBowStats(MaterialIds.invar,
				new LimbMaterialStats(630, -0.15f, -0.1f, 0.2f),
				new GripMaterialStats(0, 0.05f, 2.5f), 6, 1.75f);
		gunFromBowStats(MaterialIds.pewter,
				new LimbMaterialStats(316, 0.1f, -0.05f, -0.2f),
				new GripMaterialStats(-0.2f, 0.15f, 3.0f), 6, 2.25f);
		gunFromBowStats(MaterialIds.constantan,
				new LimbMaterialStats(675, 0.2f, -0.05f, -0.25f),
				new GripMaterialStats(-0.05f, 0.1f, 1.75f), 7, 1.75f);
		gunFromBowStats(MaterialIds.bronze,
				new LimbMaterialStats(760, -0.2f, 0.15f, -0.2f),
				new GripMaterialStats(0.1f, 0f, 2.25f), 6, 2.25f);
		gunFromBowStats(MaterialIds.electrum,
				new LimbMaterialStats(225, -0.25f, 0.1f, 0.15f),
				new GripMaterialStats(-0.35f, 0.2f, 1.5f), 5, 2f);
		addMaterialStats(MaterialIds.electrum, JOTToolStats.Statless.BULLET_CASING);

		gunFromBowStats(MaterialIds.aluminum,
				new LimbMaterialStats(225, 0.15f, -0.15f, -0.05f),
				new GripMaterialStats(-0.15f, 0.15f, 2f), 5, 2f);
		gunFromBowStats(MaterialIds.silver,
				new LimbMaterialStats(300, -0.05f, 0, 0.1f),
				new GripMaterialStats(-0.1f, -0.05f, 2.25f), 5, 2f);
		gunFromBowStats(MaterialIds.lead,
				new LimbMaterialStats(200, -0.3f, 0.15f, -0.05f),
				new GripMaterialStats(-0.1f, 0.1f, 1.75f), 6, 2.5f);
		addMaterialStats(MaterialIds.silver, JOTToolStats.Statless.BULLET_CASING);

		addMaterialStats(slimebronze,
				new HeadMaterialStats(480, 7.5f, Tiers.DIAMOND, 2.5f),
				new HandleMaterialStats(0f, -0.1f, 0.05f, 0.05f),
				StatlessMaterialStats.BINDING,
				new PlatingMaterialStats(PlatingMaterialStats.HELMET, 318, 2, 2, 0.1f),
				new PlatingMaterialStats(PlatingMaterialStats.CHESTPLATE, 458, 6, 2, 0.1f),
				new PlatingMaterialStats(PlatingMaterialStats.LEGGINGS, 430, 5, 2, 0.1f),
				new PlatingMaterialStats(PlatingMaterialStats.BOOTS, 374, 2, 2, 0.1f),
				new PlatingMaterialStats(PlatingMaterialStats.SHIELD, 514, 1, 2, 0.1f),
				StatlessMaterialStats.MAILLE,
				new LimbMaterialStats(480, -0.1f, 0.1f, -0.1f),
				new GripMaterialStats(-0.05f, -0.1f, 2.5f),
				new GunBarrelMaterialStats(1040, -0.05f, 0.15f),
				new FiringMechanismMaterialStats(6, -0.05f, 2.25f),
				JOTToolStats.Statless.BULLET_CASING);

		addMaterialStats(MaterialIds.gunpowder, new PropellantMaterialStats(3f));
		addMaterialStats(MaterialIds.blaze, new PropellantMaterialStats(2.5f));
		addMaterialStats(MaterialIds.redstone, new PropellantMaterialStats(2));
		addMaterialStats(MaterialIds.ice, new PropellantMaterialStats(1.5f));
		addMaterialStats(MaterialIds.bone, new PropellantMaterialStats(1.5f));
		addMaterialStats(MaterialIds.glowstone, new PropellantMaterialStats(2));
		addMaterialStats(MaterialIds.prismarine, new PropellantMaterialStats(2.5f));
		addMaterialStats(sugar, new PropellantMaterialStats(0f));
		addMaterialStats(MaterialIds.magma, new PropellantMaterialStats(3f));

		addMaterialStats(shimmervine, StatlessMaterialStats.BOWSTRING, StatlessMaterialStats.MAILLE, StatlessMaterialStats.BINDING);
	}

	public void gunFromBowStats(MaterialId id, LimbMaterialStats limb, GripMaterialStats grip, int maxAmmo, float power) {
		this.addMaterialStats(id,
				new GunBarrelMaterialStats(limb.durability(), limb.velocity(), (limb.accuracy() + grip.accuracy())/2f),
				new FiringMechanismMaterialStats(maxAmmo, limb.drawSpeed(), power));
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Material Stats";
	}
}
