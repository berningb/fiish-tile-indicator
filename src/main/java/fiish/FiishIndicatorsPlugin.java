package fiish;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Fiish's Indicators"
)
public class FiishIndicatorsPlugin extends Plugin
{
	@Inject private OverlayManager overlayManager;
	@Inject private FiishIndicatorsConfig config;
	@Inject private ClientThread clientThread;

	@Inject private PlayerTileOverlay playerTileOverlay;
	@Inject private EnemyTileOverlay enemyTileOverlay;
	@Inject private DeathTileOverlay deathTileOverlay;
	@Inject private EstimatedHPOverlay estimatedHPOverlay;

	private boolean shouldFlash = false;

	@Override
	protected void startUp()
	{
		overlayManager.add(playerTileOverlay);
		overlayManager.add(enemyTileOverlay);
		overlayManager.add(deathTileOverlay);
		overlayManager.add(estimatedHPOverlay);
		log.info("Fiish's Indicators started");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(playerTileOverlay);
		overlayManager.remove(enemyTileOverlay);
		overlayManager.remove(deathTileOverlay);
		overlayManager.remove(estimatedHPOverlay);
		log.info("Fiish's Indicators stopped");
	}

	@Provides
	FiishIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FiishIndicatorsConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		shouldFlash = !shouldFlash;
		playerTileOverlay.setShouldFlash(shouldFlash);
		enemyTileOverlay.setShouldFlash(shouldFlash);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (!config.showNpcDeathTimers()) return;
		if (event.getNpc().getHealthRatio() > 0) return;
		deathTileOverlay.markNpcDeath(event.getNpc().getWorldLocation());
	}
}
