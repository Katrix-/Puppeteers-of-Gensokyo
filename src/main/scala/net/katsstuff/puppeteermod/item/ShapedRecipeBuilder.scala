package net.katsstuff.puppeteermod.item

import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.oredict.ShapedOreRecipe

case class ShapedRecipeBuilder(rows: Seq[String] = Seq.empty, mappings: Map[Char, Object] = Map.empty, mirror: Boolean = false) {

  def withPattern(row1: String): ShapedRecipeBuilder = copy(rows = Seq(row1))
  def withPattern(row1: String, row2: String): ShapedRecipeBuilder = copy(rows = Seq(row1, row2))
  def withPattern(row1: String, row2: String, row3: String): ShapedRecipeBuilder = copy(rows = Seq(row1, row2, row3))

  def where(char: Char): CharMapper = CharMapper(char, this)

  def isMirrored: ShapedRecipeBuilder = ShapedRecipeBuilder(mirror = true)

  def returns(item: Item): ShapedOreRecipe = returns(new ItemStack(item))
  def returns(block: Block): ShapedOreRecipe = returns(new ItemStack(block))
  def returns(result: ItemStack): ShapedOreRecipe = {
    val array = Array(Boolean.box(mirror)) ++ rows ++ mappings.flatMap(t => Seq(Char.box(t._1), t._2))

    new ShapedOreRecipe(result, array: _*)
  }
}
object ShapedRecipeBuilder extends ShapedRecipeBuilder()

case class CharMapper(char: Char, builder: ShapedRecipeBuilder) {
  def mapsTo(ore: String): ShapedRecipeBuilder = builder.copy(mappings = builder.mappings + ((char, ore)))
  def mapsTo(block: Block): ShapedRecipeBuilder = builder.copy(mappings = builder.mappings + ((char, block)))
  def mapsTo(item: Item): ShapedRecipeBuilder = builder.copy(mappings = builder.mappings + ((char, item)))
  def mapsTo(stack: ItemStack): ShapedRecipeBuilder = builder.copy(mappings = builder.mappings + ((char, stack)))
}
