package com.snackpirate.joy_of_tinkering.data.materials;

import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import static com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds.*;

public class JOTMaterialData extends AbstractMaterialDataProvider {

	public JOTMaterialData(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void addMaterials() {
//		addMaterial(slimebronze, 3, ORDER_GENERAL + 5, false);
		material(slimebronze).tier(3).sort(ORDER_GENERAL + 5).craftable(false);
//		addMaterial(shimmervine, 3, ORDER_RANGED + 6, true);
		material(shimmervine).tier(3).sort(ORDER_RANGED + 6).craftable();
//		addMaterial(glowBerryVine, 2, ORDER_GENERAL + 6, true);
//		material(glowBerryVine).tier(2).sort(ORDER_GENERAL + 6).craftable();
//		addMaterial(sugar, 1, ORDER_RANGED, true);
		material(sugar).tier(1).sort(ORDER_RANGED+6).craftable();
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Material Data";
	}

}
