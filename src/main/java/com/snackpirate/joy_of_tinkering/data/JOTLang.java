package com.snackpirate.joy_of_tinkering.data;

import com.snackpirate.joy_of_tinkering.registries.*;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import static com.snackpirate.joy_of_tinkering.registries.JOTMaterialIds.*;

public class JOTLang extends LanguageProvider {
	public JOTLang(PackOutput output, String modid, String locale) {
		super(output, modid, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.joy_of_tinkering.joy_of_tinkering", "The Joy Of Tinkering");
		addBlock(JOTBlocks.slimebronze, "Block Of Slimebronze");
		addItem(JOTBlocks.slimebronze::getIngot, "Slimebronze Ingot");
		addItem(JOTBlocks.slimebronze::getNugget, "Slimebronze Nugget");
		addFluid(JOTFluids.moltenSlimebronze, "Molten Slimebronze", "");

		addModifier(JOTModifierIds.wellRead, "Well-Read", "Four-eyes!", "Interacting with the helmet allows reading the book stored inside");
		add("modifier.joy_of_tinkering.well_read.selected", "Well Read: Selected %s from slot %s");
		add("modifier.joy_of_tinkering.well_read.empty", "Well Read: No book found");
		addModifier(JOTModifierIds.aquambulant, "Aquambulant", "Reminiscent of a certain carpenter", "Allows walking on water, as long as you're moving fast enough.");
		addModifier(JOTModifierIds.delicate, "Delicate", "Isn't it?", "Limits the harvest speed of the tool to not instantly mine blocks.");

		addItem(JOTItems.BUCKET_OF_TERRACUBE, "Bucket of Terracube");
		add("material.tconstruct.clay.skull", "Terracube");
		add("material.tconstruct.clay.skull_flavor", "Yummy clay!");
		add("material.tconstruct.clay.skull_encyclopedia", "Allows consuming clay/clay blocks from the inventory to boost armor by +4 for an additional 5 seconds, for a maximum of 30 seconds");
		addModifier(JOTModifierIds.terraguard, "Terraguard", "Claymation!", "Consuming clay temporarily increases your defenses.");
		addEffect(JOTEffects.TERRAGUARD, "Terraguard");
		addModifier(JOTModifierIds.terracubeDisguise, "Terracube Disguise", "You haven't seen any Terracubes, and now they won't be seeing you either", "Makes you look more like a terracube to other terracubes");

		addItem(JOTItems.BUCKET_OF_SLIME, "Bucket of Slime");
		add("material.tconstruct.earthslime.skull", "Slime");
		add("material.tconstruct.earthslime.skull_flavor", "Sharing is caring");
		add("material.tconstruct.earthslime.skull_encyclopedia", "Every other second, one overslime is depleted from the helmet and redistributed to an equipped tool.");
		addModifier(JOTModifierIds.oversharing, "Oversharing", "OUR overslime", "Tool gives away its overslime to other tools!");
		addModifier(JOTModifierIds.slimeDisguise, "Slime Disguise", "Slime-spicious", "Makes you look more like a slime to other slimes");


		addItem(JOTItems.BUCKET_OF_MAGMA_CUBE, "Bucket of Magma Cube");
		add("material.tconstruct.magma.skull", "Magma Cube");
        add("material.tconstruct.magma.skull_flavor", "Improvise. Adapt. Overcome");
        add("material.tconstruct.magma.skull_encyclopedia", "Allows consuming Magma Cream from the inventory, which lets you freely swim through lava for an additional 10 seconds, for a maximum of 60 seconds");
        addModifier(JOTModifierIds.magmadaptive, "Magmadaptive", "Magma balls", "Lets you eat magma cream for the ability to swim in lava");
        addEffect(JOTEffects.MAGMADAPTATION, "Magmadaptation");
        addModifier(JOTModifierIds.magmaCubeDisguise, "Magma Cube Disguise", "The hottest disguise", "Makes you look more like a Magma Cube to other Magma Cubes");

		addItem(JOTItems.BUCKET_OF_SKYSLIME, "Bucket of Skyslime");
		add("material.tconstruct.skyslime.skull", "Skyslime");
		add("material.tconstruct.skyslime.skull_flavor", "!kcabkconK");
		add("material.tconstruct.skyslime.skull_encyclopedia", "Receiving knockback sends you upwards instead of sideways");
		addModifier(JOTModifierIds.skyward, "Skyward", "Weeeee!", "Increases launch force for all self-targeted sling modifiers");
		addModifier(JOTModifierIds.skyslimeDisguise, "Skyslime Disguise", "The sky is blue, and so are you", "Makes you look more like a Skyslime to other Skyslimes");

		addItem(JOTItems.BUCKET_OF_ENDERSLIME, "Bucket of Enderslime");
		add("material.tconstruct.enderslime.skull", "Enderslime");
		add("material.tconstruct.enderslime.skull_flavor", "Gives you greater control of your own teleportation");
		add("material.tconstruct.enderslime.skull_encyclopedia", "Whenever you teleport, always teleport in the direction you are looking. The range of this is limited by the distance of the original teleport.");
		addModifier(JOTModifierIds.endrobe, "Endrobe", "Wardrobe malfunction", "Interacting with the helmet allows swapping out your equipped armor");
		addModifier(JOTModifierIds.enderslimeDisguise, "Enderslime Disguise", "End 'er slime? I hardly know her!", "Makes you look more like an Enderslime to other Enderslimes");

		add("item.joy_of_tinkering.slime_bucket.tooltip", "Obtained by picking up a small %s with a Bucket.");

		addItem(JOTItems.LAVA_LOAFERS, "Lava Loafers");
		add(JOTItems.LAVA_LOAFERS.get().getDescriptionId() + ".description", "The Lava Loafers are a pair of unique boots found in nether-related areas, which imitate strider anatomy to allow the wearer to walk across lava.");
		addModifier(JOTModifierIds.surfaceStrider, "Surface Strider", "Not exactly walking on sunshine", "Allows walking on lava and other hot fluids");

		addItem(JOTItems.ROCKPUNCHERS, "Rockpunchers");
		add(JOTItems.ROCKPUNCHERS.get().getDescriptionId() + ".description", "The Rockpunchers are a unique chestplate fitted with sturdy gauntlets, which allow mining rock with bare hands.");
		addModifier(JOTModifierIds.fistMining, "Fist Mining", "Diggy diggy hole!", "Allows you to mine with empty hands");

		addItem(JOTItems.CRESTED_HELMET, "Crested Helmet");
		add(JOTItems.CRESTED_HELMET.get().getDescriptionId() + ".description", "The Crested Helmet is a fanciful piece of headgear sporting a large blade, allowing the wearer to execute a devastating headbutt attack against enemies.");
		addModifier(JOTModifierIds.headbutt, "Headbutt", "Parry this!", "Charges up a melee attack from the helmet");
		add("modifier.joy_of_tinkering.plus_modifier.type_format", "%s (+%s)");
		add("stat.joy_of_tinkering.bullet_casing", "Casing");
		add("tool_stat.joy_of_tinkering.extra.no_stats", "No stats");
		add("tool_stat.joy_of_tinkering.max_ammo", "Max Ammo: ");
		add("stat.joy_of_tinkering.barrel", "Barrel");
		add("stat.joy_of_tinkering.firing_mechanism", "Firing Mechanism");
		add("stat.joy_of_tinkering.propellant", "Propellant");

		addItem(JOTItems.BULLET_CASING, "Bullet Casing");
		add(JOTItems.bulletCasingCast.get().getDescriptionId(), "Bullet Casing Gold Cast");
		add(JOTItems.bulletCasingCast.getSand().getDescriptionId(), "Bullet Casing Sand Cast");
		add(JOTItems.bulletCasingCast.getRedSand().getDescriptionId(), "Bullet Casing Red Sand Cast");
		add("pattern.joy_of_tinkering.bullet_casing", "Bullet Casing");

		addItem(JOTItems.GUN_BARREL, "Gun Barrel");
		add(JOTItems.gunBarrelCast.get().getDescriptionId(), "Gun Barrel Gold Cast");
		add(JOTItems.gunBarrelCast.getSand().getDescriptionId(), "Gun Barrel Sand Cast");
		add(JOTItems.gunBarrelCast.getRedSand().getDescriptionId(), "Gun Barrel Red Sand Cast");
		add("pattern.joy_of_tinkering.gun_barrel", "Gun Barrel");

		addItem(JOTItems.FIRING_MECHANISM, "Firing Mechanism");
		add(JOTItems.firingMechanismCast.get().getDescriptionId(), "Firing Mechanism Gold Cast");
		add(JOTItems.firingMechanismCast.getSand().getDescriptionId(), "Firing Mechanism Sand Cast");
		add(JOTItems.firingMechanismCast.getRedSand().getDescriptionId(), "Firing Mechanism Red Sand Cast");
		add("pattern.joy_of_tinkering.firing_mechanism", "Firing Mechanism");

		add("pattern.joy_of_tinkering.gunpowder", "Propellant (Powder materials)");

		addItem(JOTItems.BULLET, "Bullet");
		add("item.joy_of_tinkering.bullet.description", "Bullets are single-use ammunition used for firearms such as the Revolver and Rifle");
		addItem(JOTItems.REVOLVER, "Revolver");
		add("item.joy_of_tinkering.revolver.description", "The Revolver is a small yet devastating firearm, able to load and fire multiple rounds in sequence.");

		addItem(JOTItems.RIFLE, "Rifle");
		add("item.joy_of_tinkering.rifle.description", "The Rifle is a large firearm, able to fire lower-power rounds at a higher quantity");


		addModifier(JOTModifierIds.surplus, "Surplus", "Quantity over quality", "Crafts more bullets at the cost of damage");
		addModifier(JOTModifierIds.scarce, "Scarce", "Quality over quantity", "Grants more damage at the cost of craft quantity");
		addModifier(JOTModifierIds.gunflinger, "Gunflinger", "Even higher noon", "Firing launches the player backwards");

		addModifier(JOTModifierIds.bulkBandolier, "Bulk Bandolier", "I need more bullet!", "Allows the gun to pull bullets from its own inventory when you run out of bullets, access by sneaking and attacking");
		addModifier(JOTModifierIds.trickBandolier, "Trick Bandolier", "Big cobalt on his hip", "Gives the gun three toggleable bullet slots. Select the slot by attacking, or access them by sneaking and attacking");
		add("modifier.joy_of_tinkering.trick_bandolier.disabled", "Trick Bandolier: Disabled");
		add("modifier.joy_of_tinkering.trick_bandolier.selected", "Trick Bandolier: Selected %s from slot %s");
		addModifier(JOTModifierIds.extended, "Extended", "It costs $400,000 to fire this weapon, for twelve seconds", "Increases the ammo capacity of the gun");

		addMaterial(slimebronze, "Slimebronze","Slime with a copper veneer", "Uses overslime to burn enemies");
		addModifier(JOTModifierIds.overheat, "Overheat", "Brazen!", "Consumes overslime on hit to burn and debuff enemies");

		addModifier(JOTModifierIds.junkshot, "Junkshot", "Random bullcrap go!", "Allows firing any ammo or throwable item from the gun, which consumes more durability");
		addMaterial(sugar, "Sugar", "Flavor text!", "Prevents the projectile from dealing damage");
		addModifier(JOTModifierIds.southpaw, "Southpaw", "Dual wielding!", "Makes the revolver fire on left click, and never reload when crouching");
		addModifier(JOTModifierIds.crackshot, "Crackshot", "Boom, headshot", "Projectiles deal greater damage when hitting the heads of enemies");
		add("modifier.joy_of_tinkering.crackshot.projectile_damage", "Headshot Damage");

		addModifier(JOTModifierIds.burstFire, "Burst Fire", "Ratatat!", "Allows the gun to fire in rapid bursts, at the cost of projectile damage");
		add("modifier.joy_of_tinkering.burst_fire.1", "2-Round Burst");
		add("modifier.joy_of_tinkering.burst_fire.2", "3-Round Burst");
		add("modifier.joy_of_tinkering.burst_fire.3", "4-Round Burst");
		add("modifier.joy_of_tinkering.burst_fire.4", "5-Round Burst");
		add("modifier.joy_of_tinkering.burst_fire.5", "6-Round Burst");

		add("death.attack.joy_of_tinkering.bullet", "%s was shot dead by %s");
	}

	public void addModifier(ModifierId modifier, String name, String flavour, String desc) {
		String id = modifier.getPath();
		add("modifier.joy_of_tinkering." + id, name);
		add("modifier.joy_of_tinkering." + id + ".flavor", flavour);
		add("modifier.joy_of_tinkering." + id + ".description", desc);
	}

	public void addFluid(FluidObject<?> fluid, String name, String effect) {
		add("fluid.joy_of_tinkering." + fluid.getId().getPath(), name);
		add("fluid.joy_of_tinkering." + fluid.getId().getPath() + ".fluid_effect", effect);
		add("fluid_type.joy_of_tinkering." + fluid.getId().getPath(), name);
		add("item.joy_of_tinkering." + fluid.getId().getPath() + "_bucket", name + " Bucket");
	}

	public void addMaterial(MaterialId material, String name, String flavour, String desc) {
		String id = material.getPath();
		add("material.joy_of_tinkering." + id, name);
		if (!flavour.isEmpty())
			add("material.joy_of_tinkering." + id + ".flavor", flavour);
		if (!desc.isEmpty())
			add("material.joy_of_tinkering." + id + ".encyclopedia", desc);
	}

}
