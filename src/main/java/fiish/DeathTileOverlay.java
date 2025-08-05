package fiish;

import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;

public class DeathTileOverlay extends Overlay
{
	private static final int DEATH_TIMER_SECONDS = 60;

	private final Client client;
	private final FiishIndicatorsConfig config;

	private final Map<WorldPoint, Instant> playerDeaths = new HashMap<>();
	private final Map<WorldPoint, Instant> npcDeaths = new HashMap<>();
	private final Set<Player> recentlyDyingPlayers = new HashSet<>();

	@Inject
	public DeathTileOverlay(Client client, FiishIndicatorsConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}


	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!config.showDeathTimers()) return;
	
		for (Player player : client.getPlayers())
		{
			if (player == null || player.getName() == null)
				continue;
	
			if (player.getAnimation() == AnimationID.DEATH)
			{
				WorldPoint location = player.getWorldLocation();
				
					if (!playerDeaths.containsKey(location))
					{
						System.out.println("🔥 Saw death animation for: " + player.getName() + " at " + location);
						playerDeaths.put(location, Instant.now());
					}
			}
		}
	}
	


	public void markPlayerDeath(WorldPoint location)
	{
		playerDeaths.put(location, Instant.now());
	}

	public void markNpcDeath(WorldPoint location)
	{
		npcDeaths.put(location, Instant.now());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Instant now = Instant.now();

		if (config.showDeathTimers())
		{
			for (Iterator<Map.Entry<WorldPoint, Instant>> it = playerDeaths.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry<WorldPoint, Instant> entry = it.next();
				int secondsLeft = DEATH_TIMER_SECONDS - (int)(now.getEpochSecond() - entry.getValue().getEpochSecond());
				if (secondsLeft <= 0) { it.remove(); continue; }

				LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
				if (lp != null)
				{
					Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, "P: " + secondsLeft + "s", 0);
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
			for (Iterator<Map.Entry<WorldPoint, Instant>> it = npcDeaths.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry<WorldPoint, Instant> entry = it.next();
				int secondsLeft = DEATH_TIMER_SECONDS - (int)(now.getEpochSecond() - entry.getValue().getEpochSecond());
				if (secondsLeft <= 0) { it.remove(); continue; }

				LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
				if (lp != null)
				{
					Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, "N: " + secondsLeft + "s", 0);
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
}
