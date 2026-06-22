package com.snackpirate.joy_of_tinkering.items.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public record FiringMechanismMaterialStats(float maxAmmo, float drawSpeed, float projPower) implements IMaterialStats {
	public static final MaterialStatsId ID = new MaterialStatsId(JoyOfTinkering.id("firing_mechanism"));
	public static final MaterialStatType<FiringMechanismMaterialStats> TYPE = new MaterialStatType<>(ID, new FiringMechanismMaterialStats(1, 0f, 1f), RecordLoadable.create(
			FloatLoadable.min(1).defaultField("max_ammo", 0f, true, FiringMechanismMaterialStats::maxAmmo),
			FloatLoadable.ANY.defaultField("draw_speed", 0f, true, FiringMechanismMaterialStats::drawSpeed),
			FloatLoadable.FROM_ZERO.defaultField("projectile_damage", 0f, true, FiringMechanismMaterialStats::projPower),
			FiringMechanismMaterialStats::new));

	static final String MAX_AMMO_PREFIX = IMaterialStats.makeTooltipKey(JoyOfTinkering.id("max_ammo"));
	static final String DRAW_SPEED_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("draw_speed"));
	static final String POWER_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("projectile_damage"));
	// tooltip descriptions
	private static final List<Component> DESCRIPTION = ImmutableList.of(JOTToolStats.MAX_AMMO.getDescription(), ToolStats.DRAW_SPEED.getDescription());

	@Override
	public MaterialStatType<?> getType() {
		return TYPE;
	}

	@Override
	public List<Component> getLocalizedInfo() {
		List<Component> info = Lists.newArrayList();
		info.add(IToolStat.formatColoredBonus(MAX_AMMO_PREFIX, this.maxAmmo));
		info.add(IToolStat.formatColoredBonus(DRAW_SPEED_PREFIX, this.drawSpeed));
		info.add(IToolStat.formatColoredBonus(POWER_PREFIX, this.projPower));
		return info;
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return DESCRIPTION;
	}

	@Override
	public void apply(ModifierStatsBuilder builder, float scale) {
		JOTToolStats.MAX_AMMO.update(builder, maxAmmo * scale);
		ToolStats.DRAW_SPEED.add(builder, drawSpeed * scale);
		ToolStats.PROJECTILE_DAMAGE.add(builder, projPower * scale);
	}
}
