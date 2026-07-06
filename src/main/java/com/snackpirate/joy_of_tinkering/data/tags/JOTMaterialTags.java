package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;

public class JOTMaterialTags extends AbstractMaterialTagProvider {
	public JOTMaterialTags(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
		super(packOutput, JoyOfTinkering.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(TinkerTags.Materials.MELEE).add(JOTMaterialIds.slimebronze);
		tag(TinkerTags.Materials.BALANCED).add(JOTMaterialIds.slimebronze);
	}

	@Override
	public String getName() {
		return "Joy Of Tinkering Material Tags";
	}
}
