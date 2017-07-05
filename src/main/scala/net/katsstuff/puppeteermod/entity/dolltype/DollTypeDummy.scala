package net.katsstuff.puppeteermod.entity.dolltype

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation

object DollTypeDummy extends DollTypeDefault {
  override def name:     String           = throw new IllegalStateException("Dummy doll in the wild")
  override def texture:  ResourceLocation = throw new IllegalStateException("Dummy doll in the wild")
  override def recipe:   IRecipe          = throw new IllegalStateException("Dummy doll in the wild")
  override def heldItem: ItemStack        = throw new IllegalStateException("Dummy doll in the wild")
}
