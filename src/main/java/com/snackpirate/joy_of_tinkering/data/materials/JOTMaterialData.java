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
		addMaterial(slimebronze, 3, ORDER_GENERAL + 5, false);
		addMaterial(sugar, 1, ORDER_RANGED, true);
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Material Data";
	}

}
