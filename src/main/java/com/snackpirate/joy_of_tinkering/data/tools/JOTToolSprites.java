package com.snackpirate.joy_of_tinkering.data.tools;

import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.GunBarrelMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.items.tools.PropellantMaterialStats;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider.ARMOR_PLATING;

public class JOTToolSprites extends AbstractPartSpriteProvider {
	public JOTToolSprites(String modID) {
		super(modID);
	}

	@Override
	public @NotNull String getName() {
		return "Tinkers' Additions Tool Sprites";
	}

	@Override
	protected void addAllSpites() {
		buildTool("armor/strider/boots").disallowAnimated()
				.addBreakablePart("metal", PlatingMaterialStats.BOOTS.getId());

		addTexture("tinker_armor/strider/metal_armor", ARMOR_PLATING).disallowAnimated();

		buildTool("armor/rockpuncher/chestplate").disallowAnimated()
				.addBreakablePart("plating", PlatingMaterialStats.CHESTPLATE.getId())
				.addHead("fists");

		addTexture("tinker_armor/rockpuncher/plating_armor", ARMOR_PLATING).disallowAnimated();
		addTexture("tinker_armor/rockpuncher/fists_armor", HeadMaterialStats.ID).disallowAnimated();

		addTexture("item/tool/bullet/casing", JOTToolStats.Statless.BULLET_CASING.getIdentifier());
		addTexture("item/tool/bullet/head", StatlessMaterialStats.ARROW_HEAD.getIdentifier());
		addTexture("item/tool/bullet/propellant", PropellantMaterialStats.ID);
		addPart("gun_barrel", GunBarrelMaterialStats.ID);
		addPart("firing_mechanism", FiringMechanismMaterialStats.ID);

		buildTool("revolver")
				.addPart("body", GunBarrelMaterialStats.ID)
				.addPart("cylinder", FiringMechanismMaterialStats.ID)
				.addGrip("grip");


		buildTool("rifle")
				.addPart("barrel", GunBarrelMaterialStats.ID)
				.addPart("trim", GunBarrelMaterialStats.ID)
				.addPart("receiver", FiringMechanismMaterialStats.ID)
				.addGrip("stock")
				.withLarge();
		buildTool("crested_helmet")
				.addPart("plating", PlatingMaterialStats.HELMET.getId())
				.addPart("blade", HeadMaterialStats.ID)
				.disallowAnimated();
		addTexture("item/tool/crested_helmet/held/plating", PlatingMaterialStats.HELMET.getId());
		addTexture("item/tool/crested_helmet/held/blade", HeadMaterialStats.ID);

		buildTool("decimator")
				.withLarge()
				.addHead("head")
				.addPart("grip", FiringMechanismMaterialStats.ID)
				.addHead("body")
				.addHandle("handle");
	}
}
