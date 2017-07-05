package net.katsstuff.puppeteermod.entity.dolltype;

import net.katsstuff.puppeteermod.lib.LibDollName;
import net.katsstuff.puppeteermod.lib.LibModJ;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public class PuppeteerDolls {

	@ObjectHolder(LibDollName.Bare)
	public static final DollType Bare = DollTypeDummy$.MODULE$;
}
