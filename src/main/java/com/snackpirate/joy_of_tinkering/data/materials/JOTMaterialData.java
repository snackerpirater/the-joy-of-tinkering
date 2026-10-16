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
		material(slimebronze).tier(3).sort(ORDER_GENERAL + 5).craftable(false);
		material(shimmervine).tier(3).sort(ORDER_RANGED + 6).craftable();
		material(sugar).tier(1).sort(ORDER_RANGED+6).craftable();
        material(reverberite).tier(4).sort(ORDER_WEAPON+7).craftable(false);
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Material Data";
	}

}
