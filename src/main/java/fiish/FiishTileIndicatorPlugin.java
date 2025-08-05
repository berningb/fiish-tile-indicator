package fiish;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(name = "Fiish's Tile Indicator")
public class FiishTileIndicatorPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private FiishTileIndicatorConfig config;
	@Inject private OverlayManager overlayManager;

	private PlayerTileOverlay overlay;
	private boolean shouldFlash = false;

	private final Map<WorldPoint, Instant> deaths = new HashMap<>();
	private final Map<WorldPoint, Instant> npcDeaths = new HashMap<>();
	private final Map<Player, Integer> lastKnownHp = new HashMap<>();

	@Override
	protected void startUp()
	{
		overlay = new PlayerTileOverlay();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlay = null;
		deaths.clear();
		npcDeaths.clear();
		lastKnownHp.clear();
	}

	@Provides
	FiishTileIndicatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FiishTileIndicatorConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		shouldFlash = !shouldFlash;
		lastKnownHp.clear();

		for (Player player : client.getPlayers())
		{
			if (player != null)
			{
				lastKnownHp.put(player, player.getHealthRatio());
			}
		}
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned event)
	{
		Player deadPlayer = event.getPlayer();
		if (!config.showDeathTimers()) return;

		Integer hp = lastKnownHp.get(deadPlayer);
		if (hp == null || hp != 0)
			return;

		deaths.put(deadPlayer.getWorldLocation(), Instant.now());
	}

	@Subscribe
	public void onNpcDespawned(net.runelite.api.events.NpcDespawned event)
	{
		if (!config.showNpcDeathTimers()) return;

		NPC npc = event.getNpc();
		if (npc == null || npc.getName() == null || npc.getHealthRatio() > 0) return;

		npcDeaths.put(npc.getWorldLocation(), Instant.now());
	}

	private class PlayerTileOverlay extends OverlayPanel
	{
		public PlayerTileOverlay()
		{
			setPosition(OverlayPosition.DYNAMIC);
			setPriority(OverlayPriority.LOW);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			Player player = client.getLocalPlayer();
			if (player == null) return null;

			if (config.highlightPlayer() && (!config.flashPlayerTile() || shouldFlash))
			{
				drawCorners(graphics, player.getLocalLocation(), config.highlightColor());
			}

			if (config.highlightEnemies() && (!config.flashEnemyTiles() || shouldFlash))
			{
				for (Player other : client.getPlayers())
				{
					if (other == null || other.isFriend() || other == player) continue;
					int lvl = other.getCombatLevel();
					Color color = null;

					if (lvl >= config.enemyMinLevelRange2() && lvl <= config.enemyMaxLevelRange2())
						color = config.enemyColorRange2();
					else if (lvl >= config.enemyMinLevelRange1() && lvl <= config.enemyMaxLevelRange1())
						color = config.enemyColorRange1();

					if (color != null)
					{
						drawCorners(graphics, other.getLocalLocation(), color);
					}
				}
			}

			if (config.showDeathTimers())
			{
				Instant now = Instant.now();
				for (Map.Entry<WorldPoint, Instant> entry : deaths.entrySet())
				{
					int secondsLeft = 60 - (int)(now.getEpochSecond() - entry.getValue().getEpochSecond());
					if (secondsLeft <= 0) continue;

					LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
					if (lp != null)
					{
						net.runelite.api.Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, "P: " + secondsLeft + "s", 0);
						if (textPoint != null)
						{
							graphics.setColor(config.playerDeathColor() != null ? config.playerDeathColor() : Color.WHITE);
							graphics.drawString("P: " + secondsLeft + "s", textPoint.getX(), textPoint.getY());
						}
					}
				}
			}

			if (config.showNpcDeathTimers())
			{
				Instant now = Instant.now();
				for (Map.Entry<WorldPoint, Instant> entry : npcDeaths.entrySet())
				{
					int secondsLeft = 60 - (int)(now.getEpochSecond() - entry.getValue().getEpochSecond());
					if (secondsLeft <= 0) continue;

					LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
					if (lp != null)
					{
						net.runelite.api.Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, "N: " + secondsLeft + "s", 0);
						if (textPoint != null)
						{
							graphics.setColor(config.npcDeathColor() != null ? config.npcDeathColor() : Color.ORANGE);
							graphics.drawString("N: " + secondsLeft + "s", textPoint.getX(), textPoint.getY());
						}
					}
				}
			}

			return null;
		}

		private void drawCorners(Graphics2D graphics, LocalPoint point, Color color)
		{
			Polygon poly = Perspective.getCanvasTilePoly(client, point);
			if (poly == null) return;

			int[] x = poly.xpoints;
			int[] y = poly.ypoints;
			int len = 8;

			graphics.setColor(color != null ? color : Color.CYAN);
			graphics.setStroke(new BasicStroke(2));

			for (int i = 0; i < 4; i++)
			{
				int x0 = x[i];
				int y0 = y[i];
				int xPrev = x[(i + 3) % 4];
				int yPrev = y[(i + 3) % 4];
				int xNext = x[(i + 1) % 4];
				int yNext = y[(i + 1) % 4];

				double dx1 = xNext - x0;
				double dy1 = yNext - y0;
				double mag1 = Math.hypot(dx1, dy1);
				dx1 = dx1 / mag1 * len;
				dy1 = dy1 / mag1 * len;

				double dx2 = xPrev - x0;
				double dy2 = yPrev - y0;
				double mag2 = Math.hypot(dx2, dy2);
				dx2 = dx2 / mag2 * len;
				dy2 = dy2 / mag2 * len;

				graphics.drawLine(x0, y0, (int)(x0 + dx1), (int)(y0 + dy1));
				graphics.drawLine(x0, y0, (int)(x0 + dx2), (int)(y0 + dy2));
			}
		}
	}
}
