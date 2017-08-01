package net.katsstuff.puppeteermod.entity.dolltype

import net.katsstuff.puppeteermod.item.{ItemDoll, ShapedRecipeBuilder}
import net.katsstuff.puppeteermod.items.PuppeteerItems
import net.katsstuff.puppeteermod.lib.{LibDollName, LibMod}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation

class DollTypeBare extends DollTypeAutoRegister(LibDollName.Bare) with DollTypeDefault {

  override def texture: ResourceLocation = new ResourceLocation(LibMod.Id, "textures/dolls/bare.png")
  override def recipe: IRecipe =
    ShapedRecipeBuilder
      .withPattern(" W ", "WHW", " W ")
      .where('W').mapsTo(Blocks.WOOL)
      .where('H').mapsTo(PuppeteerItems.DollCore)
      .returns(ItemDoll.createStack(this))

  override def heldItem: ItemStack = ItemStack.EMPTY
}
