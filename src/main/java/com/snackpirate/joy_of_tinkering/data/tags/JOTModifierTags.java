package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.tags.ModifierTagProvider;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class JOTModifierTags extends AbstractModifierTagProvider {
	public JOTModifierTags(PackOutput packOutput, String modId, ExistingFileHelper existingFileHelper) {
		super(packOutput, modId, existingFileHelper);
	}

	public static final TagKey<Modifier> BULLET_SUPPLYING = ModifierManager.getTag(JoyOfTinkering.id("bullet_supplying"));
	@Override
	protected void addTags() {
		tag(TinkerTags.Modifiers.HELMET_UPGRADES).add(JOTModifierIds.wellRead);
		tag(TinkerTags.Modifiers.BOOT_ABILITIES).add(JOTModifierIds.aquambulant);
		tag(TinkerTags.Modifiers.GENERAL_SLOTLESS).add(JOTModifierIds.delicate);
		tag(TinkerTags.Modifiers.RANGED_ABILITIES).add(JOTModifierIds.bulkBandolier, JOTModifierIds.trickBandolier,
//				JOTModifierIds.junkshot,
				JOTModifierIds.burstFire);
		tag(TinkerTags.Modifiers.RANGED_UPGRADES).add(JOTModifierIds.extended, JOTModifierIds.southpaw, JOTModifierIds.crackshot);
		tag(TinkerTags.Modifiers.OVERSLIME_FRIEND).add(JOTModifierIds.overheat, JOTModifierIds.oversharing);
		tag(BULLET_SUPPLYING).add(JOTModifierIds.bulkBandolier, JOTModifierIds.trickBandolier);
	}

	@Override
	public String getName() {
		return "Tinkers' Additions Modifier Tags";
	}
}
