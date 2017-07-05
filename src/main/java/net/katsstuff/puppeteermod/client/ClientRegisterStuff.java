package net.katsstuff.puppeteermod.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientRegisterStuff {

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ClientProxy.registerModels(event);
	}
}
