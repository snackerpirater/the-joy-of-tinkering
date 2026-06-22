package com.snackpirate.joy_of_tinkering.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ModifiableHeadItem extends ModifiableItem implements Equipable {
	public ModifiableHeadItem(Properties properties, ToolDefinition toolDefinition) {
		super(properties, toolDefinition);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		CompoundTag nbt = stack.getTag();
		if (slot != getEquipmentSlot() || nbt == null) {
			return ImmutableMultimap.of();
		}
		return getAttributeModifiers(ToolStack.from(stack), slot);
	}


	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
		if (slot != getEquipmentSlot()) {
			return ImmutableMultimap.of();
		}

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		if (!tool.isBroken()) {
			// base stats
			StatsNBT statsNBT = tool.getStats();
			UUID uuid = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
			builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "tconstruct.armor.armor", statsNBT.get(ToolStats.ARMOR), AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "tconstruct.armor.toughness", statsNBT.get(ToolStats.ARMOR_TOUGHNESS), AttributeModifier.Operation.ADDITION));
			double knockbackResistance = statsNBT.get(ToolStats.KNOCKBACK_RESISTANCE);
			if (knockbackResistance != 0) {
				builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "tconstruct.armor.knockback_resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
			}
			// grab attributes from modifiers
			BiConsumer<Attribute,AttributeModifier> attributeConsumer = builder::put;
			for (ModifierEntry entry : tool.getModifierList()) {
				entry.getHook(ModifierHooks.ATTRIBUTES).addAttributes(tool, entry, slot, attributeConsumer);
			}
		}

		return builder.build();
	}
}
