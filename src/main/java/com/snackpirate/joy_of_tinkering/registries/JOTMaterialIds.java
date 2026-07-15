package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

public class JOTMaterialIds {
	public static final MaterialId slimebronze = createMaterial("slimebronze");
	public static final MaterialId sugar = createMaterial("sugar");
	public static final MaterialId shimmervine = createMaterial("shimmervine");
	private static MaterialId createMaterial(String name) {
		return new MaterialId(JoyOfTinkering.id(name));
	}

}
