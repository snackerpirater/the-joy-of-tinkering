package com.snackpirate.joy_of_tinkering.data;

import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.loot.AbstractLootTableInjectionProvider;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.TinkerTools;

public class JOTLootTableInjection extends AbstractLootTableInjectionProvider {
	public JOTLootTableInjection(PackOutput output, String domain) {
		super(output, domain);
	}

	@Override
	protected void addTables() {
		IJsonPredicate<MaterialVariantId> includeInLoot = MaterialPredicate.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).inverted();
		RandomMaterial random = RandomMaterial.random().allowHidden().material(includeInLoot).build();
		AddToolDataFunction.Builder ancientToolData1 = AddToolDataFunction.builder().addMaterial(random);
		injectGameplay("piglin_bartering")
				.addToPool("main", LootItem.lootTableItem(JOTItems.LAVA_LOAFERS.get())
						.setWeight(4)
						.apply(ancientToolData1)
						.build());
		RandomMaterial randomHighTier = RandomMaterial.random().allowHidden().tier(3, 4).material(includeInLoot).build();
//		injectChest("bastion_treasure")
//				.addToPool("main", LootItem.lootTableItem(TAItems.LAVA_LOAFERS.get())
//						.setWeight(6) // about as often as both diamond swords
//						.apply(AddToolDataFunction.builder()
//								.addMaterial(randomHighTier)
//						)
//						.build());
		injectChest("ruined_portal")
				.addToPool("main", LootItem.lootTableItem(JOTItems.LAVA_LOAFERS.get())
						.setWeight(18)
						.apply(ancientToolData1)
						.apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.9f)))
						.build());

		AddToolDataFunction.Builder ancientToolData2 = AddToolDataFunction.builder().addMaterial(random).addMaterial(random);
		injectChest("abandoned_mineshaft")
				.addToPool("main", LootItem.lootTableItem(JOTItems.ROCKPUNCHERS)
						.setWeight(25) //wow the chances of getting a war pick are annoyingly low
						.apply(ancientToolData2)
						.build());
		inject("hero_of_the_toolsmith", "gameplay/hero_of_the_village/toolsmith_gift")
				.addToPool("main", LootItem.lootTableItem(JOTItems.ROCKPUNCHERS)
						.setWeight(1)
						.apply(ancientToolData2)
						.build());

		injectChest("pillager_outpost")
				.addToPool("main", LootItem.lootTableItem(JOTItems.CRESTED_HELMET)
						.apply(ancientToolData2)
						.build());
		injectChest("woodland_mansion")
				.addToPool("main", LootItem.lootTableItem(JOTItems.CRESTED_HELMET)
						.setWeight(8) // about as often as both diamond swords
						.apply(ancientToolData2)
						.build());
		inject("hero_of_the_weaponsmith", "gameplay/hero_of_the_village/weaponsmith_gift")
				.addToPool("main", LootItem.lootTableItem(JOTItems.CRESTED_HELMET)
						.setWeight(1) // makes it a 1 in 4 chance of a war pick
						.apply(ancientToolData2)
						.build());
		AddToolDataFunction.Builder ancientToolData4 = ancientToolData2.addMaterial(random).addMaterial(random);
		injectChest("nether_bridge")
				.addToPool("main", LootItem.lootTableItem(JOTItems.DECIMATOR).setWeight(4)
						.apply(ancientToolData4)
						.apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1f, 0.9f)))
						.build());
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Loot Table Injection Provider";
	}
}
