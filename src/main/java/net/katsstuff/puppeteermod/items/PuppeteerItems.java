package net.katsstuff.puppeteermod.items;

import net.katsstuff.puppeteermod.item.ItemDoll;
import net.katsstuff.puppeteermod.lib.LibItemName;
import net.katsstuff.puppeteermod.lib.LibModJ;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public class PuppeteerItems {

	@ObjectHolder(LibItemName.Doll)
	public static final Item Doll = new Item();

	@ObjectHolder(LibItemName.DollCore)
	public static final Item DollCore = new Item();
}
