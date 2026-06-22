package com.snackpirate.joy_of_tinkering.network;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import slimeknights.mantle.network.NetworkWrapper;

public class JOTNetwork extends NetworkWrapper {
	private static JOTNetwork instance = null;

	public JOTNetwork(ResourceLocation channelName, String version) {
		super(channelName, version);
	}

	private JOTNetwork() {
		super(JoyOfTinkering.id("network"), "1");
	}

	public static JOTNetwork getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Attempt to call TANetwork getInstance before TANetwork is setup");
		}
		return instance;
	}

	public static void setup() {
		if (instance != null) {
			return;
		}
		instance = new JOTNetwork();
		instance.registerPacket(UpdateHelmetPagePacket.class, UpdateHelmetPagePacket::new, NetworkDirection.PLAY_TO_SERVER);
		instance.registerPacket(GunflingerLaunchPacket.class, GunflingerLaunchPacket::new, NetworkDirection.PLAY_TO_CLIENT);
	}
}
