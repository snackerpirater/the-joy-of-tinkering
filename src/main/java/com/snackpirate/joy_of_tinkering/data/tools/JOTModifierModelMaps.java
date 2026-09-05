package com.snackpirate.joy_of_tinkering.data.tools;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.data.AbstractModifierModelMapProvider;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class JOTModifierModelMaps extends AbstractModifierModelMapProvider {
	public JOTModifierModelMaps(PackOutput output, String modId) {
		super(output, modId);
	}

	@Override
	protected void addModels() {
		tool(JOTItems.BULLET).smashing(JoyOfTinkering.id("bullet/modifiers/tconstruct_smashing_full")).tipped(JoyOfTinkering.id("bullet/modifiers/tconstruct_tipped"), null);

		ModifierId dyed = TinkerModifiers.dyed.getId();
		tool(JOTItems.ROCKPUNCHERS).armor().basic("armor/chestplate/modifiers").trim(ArmorItem.Type.CHESTPLATE).modifier(dyed, new DyedModifierModel(toolMaterial("armor/rockpuncher/chestplate/fists"), null));
		tool(JOTItems.LAVA_LOAFERS).armor().basic("armor/boots/modifiers").trim(ArmorItem.Type.BOOTS).modifier(dyed, new DyedModifierModel(toolMaterial("armor/strider/boots/skin_dyed"), null));

	}

	@Override
	public @NotNull String getName() {
		return "Joy Of Tinkering Modifier Model Map Provider";
	}
}
