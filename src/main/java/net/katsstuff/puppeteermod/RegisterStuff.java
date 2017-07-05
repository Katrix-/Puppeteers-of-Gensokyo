package net.katsstuff.puppeteermod;

import net.katsstuff.puppeteermod.entity.dolltype.DollType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegisterStuff {

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		CommonProxy.registerItems(event);
	}

	@SubscribeEvent
	public static void registerDolls(RegistryEvent.Register<DollType> event) {
		CommonProxy.registerDolls(event);
	}
}
