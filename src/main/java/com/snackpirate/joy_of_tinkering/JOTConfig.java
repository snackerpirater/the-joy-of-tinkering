package com.snackpirate.joy_of_tinkering;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = JoyOfTinkering.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JOTConfig {
//	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
//
//	private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);
//
//	private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
//	public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");
//
//	// a list of strings that are treated as resource locations for items
//	private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);
//
//	static final ForgeConfigSpec SPEC = BUILDER.build();
//
//	public static boolean logDirtBlock;
//	public static int magicNumber;
//	public static String magicNumberIntroduction;
//	public static Set<Item> items;
//
//	private static boolean validateItemName(final Object obj) {
//		return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//	}
//
//	@SubscribeEvent
//	static void onLoad(final ModConfigEvent event) {
//		logDirtBlock = LOG_DIRT_BLOCK.get();
//		magicNumber = MAGIC_NUMBER.get();
//		magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//		// convert the list of strings into a set of items
//		items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
//	}
	public static class Client {

public final ForgeConfigSpec.EnumValue<BulletDisplayOption> bulletDisplay;
public final ForgeConfigSpec.BooleanValue showReserve;

		Client(ForgeConfigSpec.Builder builder) {
			bulletDisplay = builder.comment("How the bullets of the gun are displayed in the GUI. See mod page gallery for visual examples.").defineEnum("bulletDisplayStyle", BulletDisplayOption.NUMBERED);
			showReserve = builder.comment("Whether or not the reserve bullets (stored in the gun's inventory) are displayed in the GUI.").define("displayReserves", true);
//			builder.pop();
		}

		public enum BulletDisplayOption {
			RADIAL,
			NUMBERED,
			NONE
		}
	}


	public static final ForgeConfigSpec clientSpec;
	public static final Client CLIENT;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
	public static void init() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
	}

}
