package com.snackpirate.joy_of_tinkering.items.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public record GunBarrelMaterialStats(int durability, float velocity, float accuracy) implements IRepairableMaterialStats {
	public static final MaterialStatsId ID = new MaterialStatsId(JoyOfTinkering.id("barrel"));
	public static final MaterialStatType<GunBarrelMaterialStats> TYPE = new MaterialStatType<>(ID, new GunBarrelMaterialStats(1, 0, 0), RecordLoadable.create(
			IRepairableMaterialStats.DURABILITY_FIELD,
			FloatLoadable.ANY.defaultField("velocity", 0f, true, GunBarrelMaterialStats::velocity),
			FloatLoadable.ANY.defaultField("accuracy", 0f, true, GunBarrelMaterialStats::accuracy),
			GunBarrelMaterialStats::new
	));
	static final String ACCURACY_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("accuracy"));
	static final String VELOCITY_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("velocity"));
	// tooltip descriptions
	private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.VELOCITY.getDescription(), ToolStats.ATTACK_DAMAGE.getDescription());

	@Override
	public MaterialStatType<?> getType() {
		return TYPE;
	}

	@Override
	public List<Component> getLocalizedInfo() {
		List<Component> info = Lists.newArrayList();
		info.add(ToolStats.DURABILITY.formatValue(this.durability));
		info.add(IToolStat.formatColoredBonus(VELOCITY_PREFIX, this.velocity));
		info.add(IToolStat.formatColoredBonus(ACCURACY_PREFIX, this.accuracy));
		return info;
	}

	@Override
	public List<Component> getLocalizedDescriptions() {
		return DESCRIPTION;
	}

	@Override
	public void apply(ModifierStatsBuilder builder, float scale) {
		ToolStats.DURABILITY.update(builder, durability * scale);
		ToolStats.VELOCITY.add(builder, velocity * scale);
		ToolStats.ACCURACY.add(builder, accuracy * scale);
	}
}
