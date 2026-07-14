package com.snackpirate.joy_of_tinkering.data.tags;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.registries.JOTBlocks;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static slimeknights.tconstruct.common.TinkerTags.Items.*;

public class JOTItemTags extends ItemTagsProvider {
	public JOTItemTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
	}
	public static TagKey<Item> TERRAGUARD_CONSUMABLE = ItemTags.create(JoyOfTinkering.id("terraguard_consumable"));
	public static TagKey<Item> magmadaptiveConsumable = ItemTags.create(JoyOfTinkering.id("magmadaptive_consumable"));
	public static TagKey<Item> MOD_GUNS = ItemTags.create(JoyOfTinkering.id("modifiable_guns"));
	public static TagKey<Item> JUNKSHOT_AMMO = ItemTags.create(JoyOfTinkering.id("junkshot_ammo"));
	public static TagKey<Item> PROPELLANTS = ItemTags.create(JoyOfTinkering.id("propellants"));

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(TERRAGUARD_CONSUMABLE).add(Items.CLAY, Items.CLAY_BALL);

		addToolTags(JOTItems.CRESTED_HELMET, MULTIPART_TOOL, DURABILITY, BONUS_SLOTS, HELMETS, WORN_ARMOR, ANCIENT_TOOLS, TRADER_TOOLS, MELEE);
		addToolTags(JOTItems.ROCKPUNCHERS, MULTIPART_TOOL,  DURABILITY, BONUS_SLOTS, CHESTPLATES, WORN_ARMOR, TRIM, ANCIENT_TOOLS, TRADER_TOOLS, DYEABLE);
		addToolTags(JOTItems.LAVA_LOAFERS, SINGLEPART_TOOL, DURABILITY, BONUS_SLOTS, BOOTS,       WORN_ARMOR, TRIM, ANCIENT_TOOLS, TRADER_TOOLS, DYEABLE);

		addToolTags(JOTItems.BULLET, MULTIPART_TOOL, AMMO, UNSALVAGABLE, UNSWAPPABLE, SINGLE_USE);
		addToolTags(JOTItems.REVOLVER, MULTIPART_TOOL, DURABILITY, BONUS_SLOTS, LAUNCHERS, MOD_GUNS, SMALL_RANGED, INTERACTABLE_LEFT, MELEE_WEAPON);
		addToolTags(JOTItems.RIFLE, MULTIPART_TOOL, DURABILITY, BONUS_SLOTS, LAUNCHERS, MOD_GUNS, BROAD_RANGED, INTERACTABLE_LEFT, MELEE_WEAPON);
		addToolTags(JOTItems.DECIMATOR, MULTIPART_TOOL, DURABILITY, BONUS_SLOTS, LAUNCHERS, MOD_GUNS, ANCIENT_TOOLS, MELEE_PRIMARY);

		IntrinsicTagAppender<Item> goldCasts = this.tag(TinkerTags.Items.GOLD_CASTS);
		IntrinsicTagAppender<Item> sandCasts = this.tag(TinkerTags.Items.SAND_CASTS);
		IntrinsicTagAppender<Item> redSandCasts = this.tag(TinkerTags.Items.RED_SAND_CASTS);
		IntrinsicTagAppender<Item> singleUseCasts = this.tag(TinkerTags.Items.SINGLE_USE_CASTS);
		IntrinsicTagAppender<Item> multiUseCasts = this.tag(TinkerTags.Items.MULTI_USE_CASTS);
		Consumer<CastItemObject> addCast = cast -> {
			// tag based on material
			goldCasts.add(cast.get());
			sandCasts.add(cast.getSand());
			redSandCasts.add(cast.getRedSand());
			// tag based on usage
			singleUseCasts.addTag(cast.getSingleUseTag());
			this.tag(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
			multiUseCasts.addTag(cast.getMultiUseTag());
			this.tag(cast.getMultiUseTag()).add(cast.get());
		};
		addCast.accept(JOTItems.bulletCasingCast);
		addCast.accept(JOTItems.gunBarrelCast);
		addCast.accept(JOTItems.firingMechanismCast);
		tag(TOOL_PARTS).add(JOTItems.GUN_BARREL.get(), JOTItems.FIRING_MECHANISM.get(), JOTItems.BULLET_CASING.get());
		addMetalTags(JOTBlocks.slimebronze);
		copy(TinkerTags.Blocks.ANVIL_METAL, ANVIL_METAL);

		tag(magmadaptiveConsumable).add(Items.MAGMA_CREAM, Items.MAGMA_BLOCK, TinkerFluids.magmaBottle.get());
		tag(JUNKSHOT_AMMO).add(Items.FIREWORK_ROCKET).addTag(THROWN_AMMO).addTag(ItemTags.ARROWS);
		tag(PROPELLANTS).add(Items.MAGMA_CREAM, Items.GUNPOWDER, Items.REDSTONE, Items.GLOWSTONE_DUST, Items.SUGAR, Items.BONE_MEAL, Items.POWDER_SNOW_BUCKET, Items.PRISMARINE_CRYSTALS, Items.BLAZE_POWDER);
	}

	@SafeVarargs
	private void addToolTags(ItemLike tool, TagKey<Item>... tags) {
		Item item = tool.asItem();
		for (TagKey<Item> tag : tags) {
			this.tag(tag).add(item);
		}
	}
	private void addMetalTags(MetalItemObject metal) {
		this.tag(metal.getIngotTag()).add(metal.getIngot());
		this.tag(Tags.Items.INGOTS).addTag(metal.getIngotTag());
		this.tag(metal.getNuggetTag()).add(metal.getNugget());
		this.tag(Tags.Items.NUGGETS).addTag(metal.getNuggetTag());
		this.copy(metal.getBlockTag(), metal.getBlockItemTag());
	}


}
