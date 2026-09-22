package com.snackpirate.joy_of_tinkering.data.tools;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class JOTStationLayoutProvider extends AbstractStationSlotLayoutProvider {
	public JOTStationLayoutProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void addLayouts() {
		defineModifiable(JOTItems.BULLET)
				.sortIndex(SORT_AMMO)
				.addInputItem(TinkerToolParts.arrowHead, 48, 26)
				.addInputItem(JOTItems.BULLET_CASING, 12, 62)
				.addInputPattern(new Pattern(JoyOfTinkering.id("gunpowder")), 30, 44, Ingredient.of(JOTItemTags.PROPELLANTS))
				.build();

		defineModifiable(JOTItems.REVOLVER)
				.sortIndex(SORT_RANGED)
				.addInputItem(JOTItems.GUN_BARREL,  31, 22)
				.addInputItem(JOTItems.FIRING_MECHANISM,  51, 34)
				.addInputItem(TinkerToolParts.bowGrip, 22, 53)
				.build();

		defineModifiable(JOTItems.RIFLE)
				.sortIndex(SORT_RANGED + SORT_LARGE)
				.addInputItem(JOTItems.GUN_BARREL,  11, 12+15)
				.addInputItem(JOTItems.GUN_BARREL,  31, 22+15)
				.addInputItem(JOTItems.FIRING_MECHANISM,  51, 34+15)
				.addInputItem(TinkerToolParts.bowGrip, 31, 42+15)
				.build();
//		defineModifiable(JOTItems.morningStar)
//				.sortIndex(SORT_WEAPON)
//				.addInputItem(TinkerToolParts.smallBlade, 48, 26)
//				.addInputItem(TinkerToolParts.largePlate, 30, 44)
//				.addInputItem(TinkerToolParts.toolHandle, 12, 62)
//				.build();
		defineModifiable(JOTItems.greatmace)
				.sortIndex(SORT_WEAPON + SORT_LARGE)
//				.addInputItem(TinkerToolParts.smallBlade, 48, 26)
//				.addInputItem(TinkerToolParts.largePlate, 12, 62)
//				.addInputItem(TinkerToolParts.toolHandle, 30, 44)
				.addInputItem(TinkerToolParts.largePlate, 28, 26)
				.addInputItem(TinkerToolParts.largePlate, 48, 46)
				.addInputItem(TinkerToolParts.smallBlade, 48,26)
				.addInputItem(TinkerToolParts.toughHandle, 21,52)
				.build();
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Station Slot Layout Provider";
	}
}
