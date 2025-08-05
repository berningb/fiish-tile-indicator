package fiish;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import javax.annotation.Nullable;
import java.awt.*;
@ConfigGroup("fiishtile")
public interface FiishTileIndicatorConfig extends Config
{
	// Player Tile Settings
	@ConfigSection(
		name = "Player Tile Settings",
		description = "Settings for your own player tile",
		position = 0
	)
	String playerSection = "playerSection";

	@ConfigItem(
		keyName = "highlightPlayer",
		name = "Highlight Player Tile",
		description = "Highlight the tile under your player",
		section = playerSection,
		position = 0
	)
	default boolean highlightPlayer() { return true; }

	@ConfigItem(
		keyName = "highlightColor",
		name = "Player Tile Color",
		description = "Color of the tile under you",
		section = playerSection,
		position = 1
	)
	@Nullable
	Color highlightColor();

	@ConfigItem(
		keyName = "flashPlayerTile",
		name = "Flash Player Tile",
		description = "Make your tile flash to the server tick rate",
		section = playerSection,
		position = 2
	)
	default boolean flashPlayerTile() { return true; }

	// Enemy Tile Settings
	@ConfigSection(
		name = "Enemy Tile Settings",
		description = "Settings for highlighting enemy tiles",
		position = 1
	)
	String enemySection = "enemySection";

	@ConfigItem(
		keyName = "highlightEnemies",
		name = "Highlight Enemy Tiles",
		description = "Enable enemy tile highlights",
		section = enemySection,
		position = 0
	)
	default boolean highlightEnemies() { return true; }

	@ConfigItem(
		keyName = "flashEnemyTiles",
		name = "Flash Enemy Tiles",
		description = "Make enemy tiles flash to the server tick rate",
		section = enemySection,
		position = 1
	)
	default boolean flashEnemyTiles() { return true; }

	@ConfigItem(
		keyName = "enemyMinLevelRange1",
		name = "Enemy Range 1 Min",
		description = "Min combat level for range 1",
		section = enemySection,
		position = 2
	)
	default int enemyMinLevelRange1() { return 3; }

	@ConfigItem(
		keyName = "enemyMaxLevelRange1",
		name = "Enemy Range 1 Max",
		description = "Max combat level for range 1",
		section = enemySection,
		position = 3
	)
	default int enemyMaxLevelRange1() { return 57; }

	@ConfigItem(
		keyName = "enemyColorRange1",
		name = "Enemy Range 1 Color",
		description = "Color for range 1 enemies",
		section = enemySection,
		position = 4
	)
	@Nullable
	Color enemyColorRange1();

	@ConfigItem(
		keyName = "enemyMinLevelRange2",
		name = "Enemy Range 2 Min",
		description = "Min combat level for range 2",
		section = enemySection,
		position = 5
	)
	default int enemyMinLevelRange2() { return 58; }

	@ConfigItem(
		keyName = "enemyMaxLevelRange2",
		name = "Enemy Range 2 Max",
		description = "Max combat level for range 2",
		section = enemySection,
		position = 6
	)
	default int enemyMaxLevelRange2() { return 126; }

	@ConfigItem(
		keyName = "enemyColorRange2",
		name = "Enemy Range 2 Color",
		description = "Color for range 2 enemies",
		section = enemySection,
		position = 7
	)
	@Nullable
	Color enemyColorRange2();

	// Death Timer Settings
	@ConfigSection(
		name = "Death Timer Settings",
		description = "Settings for displaying death timers",
		position = 2
	)
	String deathTimerSection = "deathTimerSection";

	@ConfigItem(
		keyName = "showDeathTimers",
		name = "Show Player Death Timers",
		description = "Show timer where players die",
		section = deathTimerSection,
		position = 0
	)
	default boolean showDeathTimers() { return true; }

	@ConfigItem(
		keyName = "playerDeathColor",
		name = "Player Death Timer Color",
		description = "Color of player death text",
		section = deathTimerSection,
		position = 1
	)
	@Nullable
	default Color playerDeathColor() { return Color.WHITE; }

	@ConfigItem(
		keyName = "showNpcDeathTimers",
		name = "Show NPC Death Timers",
		description = "Show timer where NPCs die",
		section = deathTimerSection,
		position = 2
	)
	default boolean showNpcDeathTimers() { return false; }

	@ConfigItem(
		keyName = "npcDeathColor",
		name = "NPC Death Timer Color",
		description = "Color of NPC death text",
		section = deathTimerSection,
		position = 3
	)
	@Nullable
	default Color npcDeathColor() { return new Color(255, 140, 0); }
}
