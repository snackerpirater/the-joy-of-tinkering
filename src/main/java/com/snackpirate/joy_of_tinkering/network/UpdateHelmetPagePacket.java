package com.snackpirate.joy_of_tinkering.network;

import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

//because mantle-provided logic only accounts for opening a book in the hand, inventory, and lectern
public class UpdateHelmetPagePacket implements IThreadsafePacket {
	private final int helmetSlot;
	private final String page;
	@Override
	public void handleThreadsafe(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null && this.page != null) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			ModifierEntry wellRead = ToolStack.from(helmet).getModifier(JOTModifierIds.wellRead);
			ItemStack book = wellRead.getHook(ToolInventoryCapability.HOOK).getStack(ToolStack.from(helmet), wellRead, this.helmetSlot);

			BookHelper.writeSavedPageToBook(book, page);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(helmetSlot);
		buf.writeUtf(page);
	}
	public UpdateHelmetPagePacket(FriendlyByteBuf buf) {
		this.helmetSlot = buf.readInt();
		this.page = buf.readUtf(100);
	}
	public UpdateHelmetPagePacket(String page, int slot) {
		this.helmetSlot = slot;
		this.page = page;
	}
}
