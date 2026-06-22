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

public record PropellantMaterialStats(float projPower) implements IMaterialStats {
	public static final MaterialStatsId ID = new MaterialStatsId(JoyOfTinkering.id("propellant"));
	public static final MaterialStatType<PropellantMaterialStats> TYPE = new MaterialStatType<>(ID, new PropellantMaterialStats(0), RecordLoadable.create(
			FloatLoadable.min(0).defaultField("proj_power", 0f, true, PropellantMaterialStats::projPower), PropellantMaterialStats::new
	));
	static final String POWER_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("projectile_damage"));
	private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.PROJECTILE_DAMAGE.getDescription());

	@Override
	public MaterialStatType<?> getType() {
		return TYPE;
	}

	@Override
	public List<Component> getLocalizedInfo() {
		List<Component> info = Lists.newArrayList();
		info.add(IToolStat.formatColoredBonus(POWER_PREFIX, this.projPower));
		return info;
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return DESCRIPTION;
	}

	@Override
	public void apply(ModifierStatsBuilder builder, float scale) {
		ToolStats.PROJECTILE_DAMAGE.add(builder, projPower * scale);
	}
}
