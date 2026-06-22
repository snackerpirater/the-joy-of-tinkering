package com.snackpirate.joy_of_tinkering.data.materials;

import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.GunBarrelMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import static com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds.*;

public class JOTMaterialSprites extends AbstractMaterialSpriteProvider {
	@Override
	public String getName() {
		return "Joy Of Tinkering Material Sprite Provider";
	}

	@Override
	protected void addAllMaterials() {

		buildMaterial(slimebronze).meleeHarvest().ranged().armor().maille().repairKit()
				.statType(JOTToolStats.Statless.BULLET_CASING)
				.statType(GunBarrelMaterialStats.ID)
				.statType(FiringMechanismMaterialStats.ID)
				.fallbacks("slime_metal", "metal")
				.transformer(new RecolorSpriteTransformer(GreyToColorMapping.builderFromBlack()
						.addARGB(63,  0xFF683603)
						.addARGB(102, 0xFF905408)
						.addARGB(140, 0xFFd45516)
						.addARGB(178, 0xFFff6313)
						.addARGB(216, 0xFFff8f3b)
						.addARGB(255, 0xFFffb480)
						.build()));
	}
	public static class RenderInfo extends AbstractMaterialRenderInfoProvider {

		public RenderInfo(PackOutput packOutput, @Nullable AbstractMaterialSpriteProvider materialSprites, @Nullable ExistingFileHelper existingFileHelper) {
			super(packOutput, materialSprites, existingFileHelper);
		}

		@Override
		protected void addMaterialRenderInfo() {
			buildRenderInfo(slimebronze).color(0xff6313).fallbacks("slime_metal", "metal");
			buildRenderInfo(sugar).color(0xffffff).fallbacks("crystal");
		}

		@Override
		public String getName() {
			return "Joy Of Tinkering Material Render Info Provider";
		}
	}
}
