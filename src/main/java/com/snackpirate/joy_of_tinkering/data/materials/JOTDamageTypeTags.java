package com.snackpirate.joy_of_tinkering.data.materials;

import com.snackpirate.joy_of_tinkering.registries.JOTDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class JOTDamageTypeTags extends DamageTypeTagsProvider {
	public JOTDamageTypeTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(DamageTypeTags.IS_PROJECTILE).add(JOTDamageTypes.BULLET);
		tag(DamageTypeTags.BYPASSES_COOLDOWN).add(JOTDamageTypes.BULLET);
	}
}
