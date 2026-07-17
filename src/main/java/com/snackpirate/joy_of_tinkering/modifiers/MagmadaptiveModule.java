package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import com.snackpirate.joy_of_tinkering.registries.JOTEffects;
import com.snackpirate.joy_of_tinkering.registries.JOTItems;
import com.snackpirate.joy_of_tinkering.registries.JOTModifierIds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.List;
import java.util.Map;

public record MagmadaptiveModule(LevelingInt addedDuration, LevelingInt maxDuration) implements ModifierModule, KeybindInteractModifierHook, EquipmentChangeModifierHook {

    public static final RecordLoadable<MagmadaptiveModule> LOADER = RecordLoadable.create(
            LevelingInt.LOADABLE.requiredField("added_duration", MagmadaptiveModule::addedDuration),
            LevelingInt.LOADABLE.requiredField("max_duration", MagmadaptiveModule::maxDuration),
            MagmadaptiveModule::new);

    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MagmadaptiveModule>defaultHooks(ModifierHooks.ARMOR_INTERACT, ModifierHooks.EQUIPMENT_CHANGE);
    private static final Map<Item, Integer> MULTIPLIERS = Map.of(
            Items.MAGMA_CREAM, 1,
            Items.MAGMA_BLOCK, 2,
            TinkerFluids.magmaBottle.get(), 2
    );
    @Override
    public RecordLoadable<? extends ModifierModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }
    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
        if (!player.isShiftKeyDown()) {
            int modLevel = modifier.getLevel();
            if (player.hasEffect(JOTEffects.MAGMADAPTATION.get()) && player.getEffect(JOTEffects.MAGMADAPTATION.get()).getDuration() > maxDuration.compute(modLevel) - (addedDuration.compute(modLevel)/2)) {
                player.playNotifySound(SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 2.0F, 1.0F);
                return false;
            }
            boolean hasMagma = true;
            int multiplier = 1;
            Level level = player.level();
            if (!player.isCreative()) {
                hasMagma = false;
                Inventory inventory = player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty() && stack.is(JOTItemTags.magmadaptiveConsumable)) {
                        hasMagma = true;
                        if (!level.isClientSide) {
                            multiplier = MULTIPLIERS.getOrDefault(stack.getItem(), 1);
//                            JoyOfTinkering.LOGGER.info("item {} multiplier {}", stack.getItem().getDescriptionId(), multiplier);
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                if (stack.is(TinkerFluids.magmaBottle.get())) {
                                    player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
                                }
                                inventory.setItem(i, ItemStack.EMPTY);
                            }
                        }
                        break;
                    }
                }
            }
            if (hasMagma) {
                player.playNotifySound(SoundEvents.MAGMA_CUBE_SQUISH, SoundSource.PLAYERS, 2.0F, 1.0F);
                player.playNotifySound(SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 2.0F, 1.0F);
                int toAdd = addedDuration.compute(modLevel) * multiplier;
                int currentDuration = player.hasEffect(JOTEffects.MAGMADAPTATION.get()) ? Math.min(player.getEffect(JOTEffects.MAGMADAPTATION.get()).getDuration(), maxDuration.compute(modLevel) - (addedDuration.compute(modLevel)/2)) : 0;
                JOTEffects.MAGMADAPTATION.get().apply(player, currentDuration+toAdd, 0, true);
                return true;
            }
        }
        return false;
    }
    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getChangedSlot() == EquipmentSlot.HEAD) {
            IToolStackView replacement = context.getReplacementTool();
            if (replacement == null || replacement.getModifierLevel(JOTModifierIds.magmadaptive) == 0) {
                // cure effects using the helmet
                context.getEntity().removeEffect(JOTEffects.MAGMADAPTATION.get());
            }
        }
    }
}
