package fiish;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import fiish.FiishTileIndicatorPlugin;

public class FiishTileIndicatorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FiishTileIndicatorPlugin.class);
		RuneLite.main(args);
	}
}