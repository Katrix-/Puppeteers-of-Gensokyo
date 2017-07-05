package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.item.ShapedRecipeBuilder
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation

object DollTypeDummy extends DollTypeDefault {
  override def name:    String           = "Dummy"
  override def texture: ResourceLocation = new ResourceLocation(LibMod.Id, "textures/dolls/dummy.png")
  override def recipe: IRecipe =
    ShapedRecipeBuilder(PuppeteerItems.Doll.createStack(this))
      .grid(" W ", "WHW", " W ")
      .where('W')
      .mapsTo(Blocks.WOOL)
      .where('H')
      .mapsTo(PuppeteerItems.DollCore)
      .createRecipe
  override def heldItem: ItemStack = ItemStack.EMPTY
}
