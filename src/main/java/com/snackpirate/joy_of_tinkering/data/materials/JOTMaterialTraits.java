package com.snackpirate.joy_of_tinkering.data.materials;

import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.items.tools.PropellantMaterialStats;
import com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.stats.SkullStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds.*;

public class JOTMaterialTraits extends AbstractMaterialTraitDataProvider {
	public JOTMaterialTraits(PackOutput packOutput, AbstractMaterialDataProvider materials) {
		super(packOutput, materials);
	}

	@Override
	protected void addMaterialTraits() {
		addTraits(MaterialIds.clay, SkullStats.ID, JOTModifierIds.terraguard, JOTModifierIds.terracubeDisguise);
		addTraits(MaterialIds.earthslime, SkullStats.ID, JOTModifierIds.oversharing, JOTModifierIds.slimeDisguise);
        addTraits(MaterialIds.magma, SkullStats.ID, JOTModifierIds.magmadaptive, JOTModifierIds.magmaCubeDisguise);
		addTraits(MaterialIds.enderslime, SkullStats.ID, JOTModifierIds.endrobe, JOTModifierIds.enderslimeDisguise);
		addTraits(MaterialIds.skyslime, SkullStats.ID, new ModifierEntry(ModifierIds.springy, 2), new ModifierEntry(JOTModifierIds.skyslimeDisguise, 1));

		addTraits(MaterialIds.copper, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), JOTModifierIds.surplus);
		addTraits(MaterialIds.cobalt, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), JOTModifierIds.scarce);
		addTraits(MaterialIds.slimesteel, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), JOTModifierIds.gunflinger);
		addTraits(MaterialIds.silver, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), ModifierIds.holy);
		addTraits(MaterialIds.steel, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), ModifierIds.attractive);
		addDefaultTraits(slimebronze, JOTModifierIds.overheat, TinkerModifiers.overslime.getId());
		addTraits(slimebronze, JOTToolStats.Statless.BULLET_CASING.getIdentifier(), JOTModifierIds.overheat);

//		addTraits(MaterialIds.gunpowder, PropellantMaterialStats.ID);
		addTraits(MaterialIds.blaze, PropellantMaterialStats.ID, ModifierIds.fiery);
//		addTraits(MaterialIds.redstone, PropellantMaterialStats.ID, ModifierIds.supercharged);
		addTraits(MaterialIds.ice, PropellantMaterialStats.ID, ModifierIds.freezing);
		addTraits(MaterialIds.bone, PropellantMaterialStats.ID, ModifierIds.pierce);
		addTraits(MaterialIds.glowstone, PropellantMaterialStats.ID, ModifierIds.spectral);
		addTraits(MaterialIds.prismarine, PropellantMaterialStats.ID, ModifierIds.finsAmmo);
		addTraits(sugar, PropellantMaterialStats.ID, ModifierIds.soft);
		addTraits(MaterialIds.magma, PropellantMaterialStats.ID, ModifierIds.fuse);

		addDefaultTraits(shimmervine, JOTModifierIds.greed);
		addTraits(shimmervine, StatlessMaterialStats.MAILLE.getIdentifier(), ModifierIds.reinforced);
	}

	@Override
	public String getName() {
		return "Joy Of Tinkering Material Traits";
	}
}
