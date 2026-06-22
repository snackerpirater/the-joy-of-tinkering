package com.snackpirate.joy_of_tinkering.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;

public class GunflingerLaunchPacket implements IThreadsafePacket {
	private final Vector3f vec;
	@Override
	public void handleThreadsafe(NetworkEvent.Context context) {
		Minecraft.getInstance().player.push(vec.x, vec.y, vec.z);
		SlimeBounceHandler.addBounceHandler(Minecraft.getInstance().player);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeVector3f(vec);
	}
	public GunflingerLaunchPacket(Vec3 vector) {
		vec = vector.toVector3f();
	}
	public GunflingerLaunchPacket(FriendlyByteBuf buf) {
		vec = buf.readVector3f();
	}
}
