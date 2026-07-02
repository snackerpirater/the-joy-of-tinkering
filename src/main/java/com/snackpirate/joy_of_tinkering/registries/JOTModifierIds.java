package com.snackpirate.joy_of_tinkering.registries;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class JOTModifierIds {
	//upgrades
	public static final ModifierId wellRead = modId("well_read");
	//defense

	//abilities
	public static final ModifierId aquambulant = modId("aquambulant");
	//slotless
	public static final ModifierId delicate = modId("delicate"); //limits break speed to NOT instamine
	//traits
	public static final ModifierId surfaceStrider = modId("surface_strider");
	public static final ModifierId fistMining = modId("fist_mining");

	public static final ModifierId terraguard = modId("terraguard");
	public static final ModifierId terracubeDisguise = modId("terracube_disguise");

	public static final ModifierId oversharing = modId("oversharing");
	public static final ModifierId slimeDisguise = modId("slime_disguise");

    public static final ModifierId magmadaptive = modId("magmadaptive");
    public static final ModifierId magmaCubeDisguise = modId("magma_cube_disguise");

	public static final ModifierId endrobe = modId("endrobe");
	public static final ModifierId enderslimeDisguise = modId("enderslime_disguise");

	public static final ModifierId skyward = modId("skyward");
	public static final ModifierId skyslimeDisguise = modId("skyslime_disguise");

	public static final ModifierId surplus = modId("surplus");
	public static final ModifierId gunflinger = modId("gunflinger");
	public static final ModifierId recurrentReload = modId("recurrent_reload");
	public static final ModifierId scarce = modId("scarce");

	//tool inventory for bullets
	public static final ModifierId bulkBandolier = modId("bulk_bandolier");
	//tool inventory for bullets, lets you choose which to load
	public static final ModifierId trickBandolier = modId("trick_bandolier");
	//hit projectile = ricochet bullet into multiple enemies
	public static final ModifierId ricoshot = modId("ricoshot");
	//+1 max ammo
	public static final ModifierId extended = modId("extended");
	//fires (level+1) round bursts, ignores iFrames
	public static final ModifierId burstFire = modId("burst_fire");
	//bullets go through blocks
	public static final ModifierId blockPierce = modId("block_pierce");
	//extra damage to weakpoints
	public static final ModifierId crackshot = modId("crackshot");
	//shoot any ammo/throwable from gun
	public static final ModifierId junkshot = modId("junkshot");

	public static final ModifierId overheat = modId("overheat");

	public static final ModifierId headbutt = modId("headbutt");

	public static final ModifierId firework = modId("firework");
	//sinistral for guns
	public static final ModifierId southpaw = modId("southpaw");

	public static final ModifierId kaboom = modId("kaboom");

	public static ModifierId modId(String id) {
		return new ModifierId(JoyOfTinkering.MOD_ID, id);
	}
}
