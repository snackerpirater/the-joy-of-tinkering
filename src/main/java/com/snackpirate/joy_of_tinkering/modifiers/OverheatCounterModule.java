package com.snackpirate.joy_of_tinkering.modifiers;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

public record OverheatCounterModule(LevelingValue chance, LevelingValue constant, LevelingValue random, int durabilityUsage, IJsonPredicate<LivingEntity> defender, IJsonPredicate<LivingEntity> attacker, ModifierCondition<IToolStackView> condition) implements CounterModule {
	public static final RecordLoadable<OverheatCounterModule> LOADER = CounterModule.makeLoader("seconds", OverheatCounterModule::new);

	@Override
	public void applyEffect(IToolStackView tool, ModifierEntry modifier, float value, EquipmentContext context, Entity attacker, DamageSource source, float damageDealt) {
		if (OverslimeModule.INSTANCE.getAmount(tool) >= 2 && !attacker.fireImmune()) {
			setFire(value, attacker);
			OverslimeModule.INSTANCE.addAmount(tool, -1);
		}
	}
	private void setFire(float value, Entity target) {
		target.setSecondsOnFire((int) value);
	}
	@ApiStatus.Internal
	public OverheatCounterModule {}
	public static CounterModule.Builder<OverheatCounterModule> builder() {
		return new CounterModule.Builder<>(OverheatCounterModule::new);
	}
	@Override
	public RecordLoadable<? extends ModifierModule> getLoader() {
		return LOADER;
	}
}
