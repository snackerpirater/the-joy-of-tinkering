package com.snackpirate.joy_of_tinkering.items.tools;

import com.snackpirate.joy_of_tinkering.JoyOfTinkering;
import com.snackpirate.joy_of_tinkering.data.tags.JOTItemTags;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class JOTToolStats {
	public static final MaterialStatsId GUN = new MaterialStatsId(JoyOfTinkering.id("gun"));
	public static final FloatToolStat MAX_AMMO = ToolStats.register(new FloatToolStat(new ToolStatId(JoyOfTinkering.id("max_ammo")), 0xFF55FFFF, 0, 0, 20, JOTItemTags.MOD_GUNS));
	public enum Statless implements IMaterialStats {
		BULLET_CASING("bullet_casing");

		private static final List<Component> LOCALIZED = List.of(IMaterialStats.makeTooltip(JoyOfTinkering.id("extra.no_stats")));
		private static final List<Component> DESCRIPTION = List.of(Component.empty());
		private final MaterialStatType<Statless> type;

		Statless(String name) {
			this.type = MaterialStatType.singleton(new MaterialStatsId(JoyOfTinkering.id(name)), this);
		}

		@Override
		public MaterialStatType<?> getType() {
			return type;
		}

		@Override
		public List<Component> getLocalizedInfo() {
			return LOCALIZED;
		}

		@Override
		public List<Component> getLocalizedDescriptions() {
			return DESCRIPTION;
		}

		@Override
		public void apply(ModifierStatsBuilder builder, float scale) {}
	}
}
