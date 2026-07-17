package com.snackpirate.joy_of_tinkering.modifiers;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTEntityTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Arrays;
import java.util.List;

public record DropMobEquipmentModule(LevelingValue chance) implements ModifierModule, ProcessLootModifierHook {
	public static final RecordLoadable<DropMobEquipmentModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.requiredField("chance", DropMobEquipmentModule::chance), DropMobEquipmentModule::new);
	public static final List<ModuleHook<?>> HOOKS = HookProvider.<DropMobEquipmentModule>defaultHooks(ModifierHooks.PROCESS_LOOT);

//	@Override
//	public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
//		JoyOfTinkering.LOGGER.info("after hit");
//		if (context.getTarget() instanceof Mob mob && mob.isDeadOrDying() && !mob.getType().is(JOTEntityTags.GREED_IMMUNE)) {
//			for (EquipmentSlot slot: EquipmentSlot.values()) {
//				mob.setDropChance(slot, mob.getEquipmentDropChance(slot) + chance.compute(modifier.getLevel()));
//			}
//		}
//	}

	public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
//		JoyOfTinkering.LOGGER.info("before hit");
		if (context.getTarget() instanceof Mob mob && mob.getHealth() < damage && !mob.getType().is(JOTEntityTags.GREED_IMMUNE)) {
			for (EquipmentSlot slot: EquipmentSlot.values()) {
				mob.setDropChance(slot, mob.getEquipmentDropChance(slot) + chance.compute(modifier.getLevel()));
			}
		}
		return knockback;
	}

	public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
		if (target instanceof Mob mob && !mob.getType().is(JOTEntityTags.GREED_IMMUNE)) {
			for (EquipmentSlot slot: EquipmentSlot.values()) {
				mob.setDropChance(slot, mob.getEquipmentDropChance(slot) + chance.compute(modifier.getLevel()));
			}
		}
		return false;
	}

	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}

	@Override
	public List<ModuleHook<?>> getDefaultHooks() {
		return HOOKS;
	}

	@Override
	public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
//		JoyOfTinkering.LOGGER.info("greed 1");
		if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
			return;
		}
//		JoyOfTinkering.LOGGER.info("greed 2");
		// must have an entity
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (entity instanceof Mob mob && !mob.getType().is(JOTEntityTags.GREED_IMMUNE)) {
//			JoyOfTinkering.LOGGER.info("greed 3");
			for (EquipmentSlot slot: EquipmentSlot.values()) {
				if (!mob.getItemBySlot(slot).isEmpty() && mob.getEquipmentDropChance(slot) != 2) {
//					JoyOfTinkering.LOGGER.info("greed 4 slot {}", slot.getName());
					ItemStack item = mob.getItemBySlot(slot);
					generatedLoot.remove(item);
					float chanceToDrop = mob.getEquipmentDropChance(slot) + (0.01f*context.getLootingModifier()) + chance.compute(modifier.getLevel());
//					JoyOfTinkering.LOGGER.info("chance to drop {} + {} + {}", mob.getEquipmentDropChance(slot), (0.01f*context.getLootingModifier()), chance.compute(modifier.getLevel()));
					if (context.getRandom().nextFloat() < chanceToDrop) {
//						JoyOfTinkering.LOGGER.info("success");
						generatedLoot.add(item);
					}
				}
			}
		}
	}
}
