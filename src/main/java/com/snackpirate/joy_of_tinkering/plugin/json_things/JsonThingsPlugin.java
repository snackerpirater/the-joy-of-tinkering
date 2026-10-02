package com.snackpirate.joy_of_tinkering.plugin.json_things;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.items.ModifiableGunItem;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.plugin.jsonthings.item.IToolItemFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonThingsPlugin {

	public static void onConstruct() {
		JoyOfTinkering.LOGGER.info("JOT-JSON THINGS: onConstruct");
		ItemTypes.init();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			PluginClient.init();
		}
	}

	public static class ItemTypes {
		static final List<Item> GUN_ITEMS = new ArrayList<>();

		public static void init() {
			register("gun", data -> {
				boolean twoHanded = GsonHelper.getAsBoolean(data, "two_handed", false);
				return (IToolItemFactory<ModifiableGunItem>)(props, builder) -> add(GUN_ITEMS, new ModifiableGunItem(props.stacksTo(1).durability(-1), ToolDefinition.create(builder.getRegistryName()), twoHanded));
			});
		}

		private static <T extends Item> void register(String name, IItemSerializer<T> factory) {
			FlexItemType.register("joy_of_tinkering:" + name, factory);
		}

		private static <T> T add(List<? super T> list, T item) {
			list.add(item);
			return item;
		}
	}
}
