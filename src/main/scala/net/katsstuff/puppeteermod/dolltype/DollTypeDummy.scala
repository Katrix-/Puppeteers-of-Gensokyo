package net.katsstuff.puppeteermod.dolltype

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

object DollTypeDummy extends DollTypeDefault {
  override def name:     String           = throw new IllegalStateException("Dummy doll in the wild")
  override def texture(doll: EntityDoll):  ResourceLocation = throw new IllegalStateException("Dummy doll in the wild")
  override def heldItem: ItemStack        = throw new IllegalStateException("Dummy doll in the wild")
}
