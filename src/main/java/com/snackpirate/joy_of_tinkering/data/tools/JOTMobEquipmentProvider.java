package com.snackpirate.joy_of_tinkering.data.tools;

import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMobEquipmentProvider;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class JOTMobEquipmentProvider extends AbstractMobEquipmentProvider {
	public JOTMobEquipmentProvider(PackOutput output, String modId) {
		super(output, modId);
	}

	@Override
	protected void addEquipment() {
		IJsonPredicate<MaterialVariantId> includeInLoot = MaterialPredicate.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).inverted();
		RandomMaterial tier2up = RandomMaterial.random().allowHidden().tier(2, 4).material(includeInLoot).build();
		equip(EntityType.PIGLIN).slot(EquipmentSlot.FEET).tool(JOTItems.LAVA_LOAFERS).chance(0.075f).material(tier2up).end();
		equip(EntityType.ZOMBIE_VILLAGER).slot(EquipmentSlot.CHEST).tool(JOTItems.ROCKPUNCHERS).chance(0.2f).material(tier2up).end();
		equip(EntityType.VINDICATOR).slot(EquipmentSlot.HEAD).tool(JOTItems.CRESTED_HELMET).chance(0.1f).material(tier2up).end();
		equip(EntityType.WITHER_SKELETON).slot(EquipmentSlot.MAINHAND).tool(JOTItems.DECIMATOR).chance(0.1f).material(tier2up).end();
	}

	@Override
	public String getName() {
		return "Tinkers Additions Mob Equipment Provider";
	}
}
