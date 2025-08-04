package fiish;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import javax.annotation.Nullable;
import java.awt.*;

@ConfigGroup("fiishtile")
public interface FiishTileIndicatorConfig extends Config
{
	@ConfigItem(
		keyName = "highlightPlayer",
		name = "Highlight Player Tile",
		description = "Highlight the tile under the player"
	)
	default boolean highlightPlayer() { return true; }

	@ConfigItem(
		keyName = "highlightColor",
		name = "Player Tile Color",
		description = "Color of the player tile highlight"
	)
	@Nullable
	Color highlightColor();

	@ConfigItem(
		keyName = "flashPlayerTile",
		name = "Flash Player Tile",
		description = "Flashes the player's tile"
	)
	default boolean flashPlayerTile() { return true; }

	@ConfigItem(
		keyName = "flashEnemyTiles",
		name = "Flash Enemy Tiles",
		description = "Flashes enemy tiles"
	)
	default boolean flashEnemyTiles() { return true; }

	@ConfigItem(
		keyName = "enemyMinLevelRange1",
		name = "Enemy Range 1 Min Level",
		description = "Minimum combat level for range 1"
	)
	default int enemyMinLevelRange1() { return 3; }

	@ConfigItem(
		keyName = "enemyMaxLevelRange1",
		name = "Enemy Range 1 Max Level",
		description = "Maximum combat level for range 1"
	)
	default int enemyMaxLevelRange1() { return 70; }

	@ConfigItem(
		keyName = "enemyColorRange1",
		name = "Enemy Range 1 Color",
		description = "Highlight color for enemies in range 1"
	)
	@Nullable
	Color enemyColorRange1();

	@ConfigItem(
		keyName = "enemyMinLevelRange2",
		name = "Enemy Range 2 Min Level",
		description = "Minimum combat level for range 2"
	)
	default int enemyMinLevelRange2() { return 71; }

	@ConfigItem(
		keyName = "enemyMaxLevelRange2",
		name = "Enemy Range 2 Max Level",
		description = "Maximum combat level for range 2"
	)
	default int enemyMaxLevelRange2() { return 126; }

	@ConfigItem(
		keyName = "enemyColorRange2",
		name = "Enemy Range 2 Color",
		description = "Highlight color for enemies in range 2"
	)
	@Nullable
	Color enemyColorRange2();

	@ConfigItem(
		keyName = "showDeathTimers",
		name = "Show Death Timers",
		description = "Display a countdown timer on player death tiles"
	)
	default boolean showDeathTimers() { return true; }

	@ConfigItem(
	keyName = "trackSelfDeath",
	name = "Track Your Own Death",
	description = "Show timer when you die"
)
default boolean trackSelfDeath() { return false; }
}
