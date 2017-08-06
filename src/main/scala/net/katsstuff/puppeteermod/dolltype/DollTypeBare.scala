package net.katsstuff.puppeteermod.dolltype

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.lib.{LibDollName, LibMod}
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class DollTypeBare extends DollTypeAutoRegister(LibDollName.Bare) with DollTypeDefault {

  override def texture(doll: EntityDoll): ResourceLocation = new ResourceLocation(LibMod.Id, "textures/dolls/bare.png")
  override def heldItem: ItemStack = ItemStack.EMPTY
}
