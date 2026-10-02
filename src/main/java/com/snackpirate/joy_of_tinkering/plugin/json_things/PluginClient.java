package com.snackpirate.joy_of_tinkering.plugin.json_things;

import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;

public class PluginClient {
	public static void init() {
		ItemColorHandler.register("joy_of_tinkering:gun", block -> ToolModel.COLOR_HANDLER);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(PluginClient::clientSetup);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			for (Item item : JsonThingsPlugin.ItemTypes.GUN_ITEMS) {
				TinkerItemProperties.registerToolProperties(item);
			}
		});
	}
}
