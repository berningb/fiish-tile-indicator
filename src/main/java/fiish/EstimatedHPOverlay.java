package fiish;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class EstimatedHPOverlay extends Overlay
{
	private static final int MAX_FADE_MS = 1200;

	private final Client client;
	private final FiishIndicatorsConfig config;

	private final Map<Player, Integer> lastRatio = new HashMap<>();
	private final Map<Player, HealEvent> recentHeals = new HashMap<>();

	@Inject
	public EstimatedHPOverlay(Client client, FiishIndicatorsConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showEstimatedHp()) return null;

		Player local = client.getLocalPlayer();
		if (local == null) return null;

		for (Player player : client.getPlayers())
		{
			if (player == null || player == local || player.getHealthRatio() <= 0 || player.getHealthScale() <= 0)
				continue;

			int ratio = player.getHealthRatio();
			int scale = player.getHealthScale();
			int combatLevel = player.getCombatLevel();
            int assumedMaxHp = Math.min(99, (int)(combatLevel * 1.5));
            int estimatedHP = (int)((ratio / (double) scale) * assumedMaxHp);

			Point loc = player.getCanvasTextLocation(graphics, "", 0);
			if (loc == null) continue;

			int previous = lastRatio.getOrDefault(player, ratio);
			if (ratio > previous)
			{
				int healed = (int) ((ratio - previous) / (double) scale * 99);
				recentHeals.put(player, new HealEvent(healed, Instant.now()));
			}
			lastRatio.put(player, ratio);

			String text = estimatedHP + " HP";
			graphics.setFont(new Font("Arial", Font.BOLD, 12));
			FontMetrics fm = graphics.getFontMetrics();
			int x = loc.getX() - (fm.stringWidth(text) / 2);
			int y = loc.getY() - config.textYOffset();

			graphics.setColor(Color.BLACK);
			graphics.drawString(text, x + 1, y + 1);

			graphics.setColor(config.textColor());
			graphics.drawString(text, x, y);

			HealEvent heal = recentHeals.get(player);
			if (heal != null)
			{
				long elapsed = Instant.now().toEpochMilli() - heal.timestamp.toEpochMilli();
				if (elapsed > MAX_FADE_MS)
				{
					recentHeals.remove(player);
				}
				else
				{
					float alpha = 1f - (elapsed / (float) MAX_FADE_MS);
					String healText = "+" + heal.amount;

					graphics.setFont(new Font("Arial", Font.BOLD, 11));
					Color base = config.healTextColor();
					Color faded = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (alpha * 255));

					int hx = loc.getX() - (graphics.getFontMetrics().stringWidth(healText) / 2);
					int hy = loc.getY() - config.healYOffset() - (int)(elapsed / 60);

					graphics.setColor(faded);
					graphics.drawString(healText, hx, hy);
				}
			}
		}
		return null;
	}

	private static class HealEvent
	{
		final int amount;
		final Instant timestamp;

		HealEvent(int amount, Instant timestamp)
		{
			this.amount = amount;
			this.timestamp = timestamp;
		}
	}
}
