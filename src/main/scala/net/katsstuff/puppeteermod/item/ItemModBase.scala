package net.katsstuff.puppeteermod.item

import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry

class ItemModBase(name: String) extends Item {
  setRegistryName(name)
  setUnlocalizedName(name)
}
