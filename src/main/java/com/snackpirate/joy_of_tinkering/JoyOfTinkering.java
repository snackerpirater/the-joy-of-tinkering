package com.snackpirate.joy_of_tinkering;

import com.mojang.logging.LogUtils;
import com.snackpirate.joy_of_tinkering.data.*;
import com.snackpirate.joy_of_tinkering.data.materials.*;
import com.snackpirate.joy_of_tinkering.data.JOTFluidTextures;
import com.snackpirate.joy_of_tinkering.data.tags.*;
import com.snackpirate.joy_of_tinkering.data.tools.*;
import com.snackpirate.joy_of_tinkering.items.tools.FiringMechanismMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.GunBarrelMaterialStats;
import com.snackpirate.joy_of_tinkering.items.tools.JOTToolStats;
import com.snackpirate.joy_of_tinkering.items.tools.PropellantMaterialStats;
import com.snackpirate.joy_of_tinkering.modifiers.*;
import com.snackpirate.joy_of_tinkering.network.JOTNetwork;
import com.snackpirate.joy_of_tinkering.registries.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(JoyOfTinkering.MOD_ID)
public class JoyOfTinkering {
	public static final String MOD_ID = "joy_of_tinkering";
	public static final Logger LOGGER = LogUtils.getLogger();

	protected static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

	public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.joy_of_tinkering.joy_of_tinkering"))
			.icon(() -> JOTItems.LAVA_LOAFERS.get().getRenderTool())
			.displayItems(JOTItems::addTabItems)
			.build());

	// Define mod id in a common place for everything to reference
	public static ResourceLocation id(String loc) {
		return ResourceLocation.tryBuild(MOD_ID, loc);
	}
	public JoyOfTinkering() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		JOTItems.ITEMS.register(modEventBus);
		JOTEffects.register(modEventBus);
		JOTEntities.ENTITIES.register(modEventBus);
		JOTSounds.register(modEventBus);
		JOTBlocks.register(modEventBus);
		JOTFluids.register(modEventBus);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::registerSerializers);
		CREATIVE_TABS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
		JOTNetwork.setup();
//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		JOTModifierHooks.init();
		MaterialRegistry.getInstance().registerStatType(GunBarrelMaterialStats.TYPE, MaterialRegistry.RANGED);
		MaterialRegistry.getInstance().registerStatType(FiringMechanismMaterialStats.TYPE, MaterialRegistry.RANGED);
		MaterialRegistry.getInstance().registerStatType(JOTToolStats.Statless.BULLET_CASING.getType(), MaterialRegistry.RANGED);
		MaterialRegistry.getInstance().registerStatType(PropellantMaterialStats.TYPE);
// Some common setup code
//		LOGGER.info("HELLO FROM COMMON SETUP");
//		LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//		if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//		LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//
//		Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
	}

	// Add the example block item to the building blocks tab

	// You can use SubscribeEvent and let the Event Bus discover methods to call

	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent



	@SubscribeEvent
	public static void genData(GatherDataEvent event) {
		boolean server = event.includeServer();
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

		RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
		JOTDamageTypeProvider.register(registrySetBuilder);
		DatapackBuiltinEntriesProvider datapackRegistryProvider = new DatapackBuiltinEntriesProvider(output, provider, registrySetBuilder, Set.of(MOD_ID));
		gen.addProvider(server, datapackRegistryProvider);

		JOTMaterialData materials = new JOTMaterialData(output);
		gen.addProvider(server, new JOTModifierProvider(output));
		gen.addProvider(server, new JOTModifierTags(output, MOD_ID, helper));
		gen.addProvider(server, new JOTEntityTags(output, provider, MOD_ID, helper));
		JOTBlockTags blockTags = new JOTBlockTags(output, provider, MOD_ID, helper);
		gen.addProvider(server, blockTags);
		gen.addProvider(server, new JOTFluidTextures(output));
		gen.addProvider(server, new JOTFluidTextures.BucketModels(output));
		gen.addProvider(server, new JOTFluidTags(output, provider, JoyOfTinkering.MOD_ID, helper));
		gen.addProvider(server, new JOTRecipes(output));
		gen.addProvider(server, new JOTStationLayoutProvider(output));
		gen.addProvider(server, new JOTLootTableInjection(output, MOD_ID));
		gen.addProvider(server, new JOTLang(output, MOD_ID, "en_us"));
		gen.addProvider(server, materials);
		gen.addProvider(server, new JOTMaterialStats(output, materials));
		gen.addProvider(server, new JOTMaterialTraits(output, materials));
		JOTMaterialSprites materialSprites = new JOTMaterialSprites();
		JOTToolSprites toolSprites = new JOTToolSprites(MOD_ID);
		gen.addProvider(server, new JOTMaterialSprites.RenderInfo(output, materialSprites, helper));
		gen.addProvider(server, new MaterialPartTextureGenerator(output, helper, new TinkerPartSpriteProvider(), getOverride(), materialSprites));
		gen.addProvider(server, new JOTToolDefinitionProvider(output, MOD_ID));
		gen.addProvider(server, new MaterialPartTextureGenerator(output, helper, toolSprites, getOverride(), new TinkerMaterialSpriteProvider(), materialSprites));
		gen.addProvider(server, new GeneratorPartTextureJsonGenerator(output, MOD_ID, toolSprites));
		gen.addProvider(server, new GeneratorPartTextureJsonGenerator(output, TConstruct.MOD_ID, toolSprites));

		gen.addProvider(server, new JOTMobEquipmentProvider(output, MOD_ID));
		gen.addProvider(server, new JOTLootTableProvider(output));
//		gen.addProvider(server, new JOTModifierModelMaps(output, MOD_ID));

		gen.addProvider(server, new JOTItemTags(output, provider, blockTags.contentsGetter(), MOD_ID, helper));
//		gen.addProvider(server, new JOTDamageTypeTags(output, provider, MOD_ID, helper));
	}
	void registerSerializers(RegisterEvent event) {
		if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
			ModifierModule.LOADER.register(id("well_read"), WellReadModule.LOADER);
			ModifierModule.LOADER.register(id("terraguard"), TerraguardModule.LOADER);
			ModifierModule.LOADER.register(id("oversharing"), OversharingModule.LOADER);
            ModifierModule.LOADER.register(id("magmadaptive"), MagmadaptiveModule.LOADER);
			ModifierModule.LOADER.register(id("endrobe"), EndrobeModule.LOADER);
			ModifierModule.LOADER.register(id("endrobe_inventory"), EndrobeInventoryModule.LOADER);
			ModifierModule.LOADER.register(id("fluid_walker"), FluidWalkerModule.LOADER);
			ModifierModule.LOADER.register(id("fist_mining"), FistMiningModule.LOADER);
			ModifierModule.LOADER.register(id("single_insta_break"), SingleInstaBreakModule.LOADER);
			ModifierModule.LOADER.register(id("gunflinger"), GunflingerModule.LOADER);
			ModifierModule.LOADER.register(id("bulk_bandolier"), BulkBandolierModule.LOADER);
			ModifierModule.LOADER.register(id("trick_bandolier"), TrickBandolierModule.LOADER);
			ModifierModule.LOADER.register(id("overheat_melee"), OverheatModule.LOADER);
			ModifierModule.LOADER.register(id("overheat_counter"), OverheatCounterModule.LOADER);
//			ModifierModule.LOADER.register(id("headbutt_attack"), HeadbuttModule.LOADER);
			ModifierModule.LOADER.register(id("junkshot"), JunkshotModule.LOADER);
			ModifierModule.LOADER.register(id("southpaw"), SouthpawModule.LOADER);
			ModifierModule.LOADER.register(id("crackshot"), CrackshotModule.LOADER);
			ModifierModule.LOADER.register(id("burst_fire"), BurstfireModule.LOADER);

			ToolStackPredicate.LOADER.register(id("has_overslime"), JOTModifierProvider.HAS_OVERSLIME.getLoader());
		}
	}

	private static GeneratorPartTextureJsonGenerator.StatOverride getOverride() {
		GeneratorPartTextureJsonGenerator.StatOverride.Builder builder = new GeneratorPartTextureJsonGenerator.StatOverride.Builder();
//		builder.add(StatlessMaterialStats.REPAIR_KIT.getIdentifier(), MaterialIds.earthslime);
//		builder.add(StatlessMaterialStats.REPAIR_KIT.getIdentifier(), MaterialIds.skyslime);
////		builder.add(StatlessMaterialStats.REPAIR_KIT.getIdentifier(), MaterialIds.enderslime); already has repair kits
//		builder.add(StatlessMaterialStats.REPAIR_KIT.getIdentifier(), MaterialIds.magma);
//		builder.add(StatlessMaterialStats.REPAIR_KIT.getIdentifier(), MaterialIds.clay);
		JOTMaterialStats.gunMetals.forEach(material -> {
			builder.addVariant(GunBarrelMaterialStats.ID, material);
			builder.addVariant(FiringMechanismMaterialStats.ID, material);
		});
		JOTMaterialStats.bulletMetals.forEach(material -> {
			builder.addVariant(JOTToolStats.Statless.BULLET_CASING.getIdentifier(), material);
		});
		return builder.build();
	}
}
