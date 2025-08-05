package fiish;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class EnemyTileOverlay extends Overlay
{
	private final Client client;
	private final FiishIndicatorsConfig config;
	private boolean shouldFlash = false;

	@Inject
	public EnemyTileOverlay(Client client, FiishIndicatorsConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

    public void setShouldFlash(boolean flash)
	{
		this.shouldFlash = flash;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.highlightEnemies()) return null;

		if (config.flashEnemyTiles())
			shouldFlash = !shouldFlash;

		if (!config.flashEnemyTiles() || shouldFlash)
		{
			Player local = client.getLocalPlayer();
			if (local == null) return null;

			for (Player other : client.getPlayers())
			{
				if (other == null || other == local || other.isFriend()) continue;

				int level = other.getCombatLevel();
				Color color = null;

				if (level >= config.enemyMinLevelRange2() && level <= config.enemyMaxLevelRange2())
					color = config.enemyColorRange2();
				else if (level >= config.enemyMinLevelRange1() && level <= config.enemyMaxLevelRange1())
					color = config.enemyColorRange1();

				if (color != null)
				{
					LocalPoint lp = other.getLocalLocation();
					Polygon poly = Perspective.getCanvasTilePoly(client, lp);
					if (poly != null)
					{
						graphics.setColor(color);
						graphics.setStroke(new BasicStroke(2));
						graphics.drawPolygon(poly);
					}
				}
			}
		}
		return null;
	}
}
