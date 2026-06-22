package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;

public class JOTModifierTags extends AbstractModifierTagProvider {
	public JOTModifierTags(PackOutput packOutput, String modId, ExistingFileHelper existingFileHelper) {
		super(packOutput, modId, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(TinkerTags.Modifiers.HELMET_UPGRADES).add(JOTModifierIds.wellRead);
		tag(TinkerTags.Modifiers.BOOT_ABILITIES).add(JOTModifierIds.aquambulant);
		tag(TinkerTags.Modifiers.GENERAL_SLOTLESS).add(JOTModifierIds.delicate);
		tag(TinkerTags.Modifiers.RANGED_ABILITIES).add(JOTModifierIds.bulkBandolier, JOTModifierIds.trickBandolier, JOTModifierIds.junkshot, JOTModifierIds.burstFire);
		tag(TinkerTags.Modifiers.RANGED_UPGRADES).add(JOTModifierIds.extended, JOTModifierIds.southpaw, JOTModifierIds.crackshot);
		tag(TinkerTags.Modifiers.OVERSLIME_FRIEND).add(JOTModifierIds.overheat, JOTModifierIds.oversharing);
	}

	@Override
	public String getName() {
		return "Joy Of Tinkering Modifier Tags";
	}
}
