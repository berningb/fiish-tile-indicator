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

public class PlayerTileOverlay extends Overlay
{
	private final Client client;
	private final FiishIndicatorsConfig config;
	private boolean shouldFlash = false;

	@Inject
	public PlayerTileOverlay(Client client, FiishIndicatorsConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public void setShouldFlash(boolean flash)
	{
		this.shouldFlash = flash;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();
		if (player == null || !config.highlightPlayer()) return null;

		if (!config.flashPlayerTile() || shouldFlash)
		{
			LocalPoint lp = player.getLocalLocation();
			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly != null)
			{
				graphics.setColor(config.highlightColor() != null ? config.highlightColor() : Color.CYAN);
				graphics.setStroke(new BasicStroke(2));
				drawCorners(graphics, lp, config.highlightColor());
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
