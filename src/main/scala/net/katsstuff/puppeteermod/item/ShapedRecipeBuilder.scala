package net.katsstuff.puppeteermod.item

import scala.collection.mutable.ArrayBuffer

import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.ShapedOreRecipe

case class ShapedRecipeBuilder(
	output: ItemStack,
	mirrored: Boolean = false,
	row1: Option[String] = None,
	row2: Option[String] = None,
	row3: Option[String] = None,
	mappings: Map[Char, AnyRef] = Map()
) {

	def grid(row1: String): ShapedRecipeBuilder = copy(row1 = Some(row1))
	def grid(row1: String, row2: String): ShapedRecipeBuilder = copy(row1 = Some(row1), row2 = Some(row2))
	def grid(row1: String, row2: String, row3: String): ShapedRecipeBuilder = copy(row1 = Some(row1), row2 = Some(row2), row3 = Some(row3))

	def map(char: Char, stack: ItemStack): ShapedRecipeBuilder = copy(mappings = mappings + ((char, stack)))
	def map(char: Char, item: Item): ShapedRecipeBuilder = copy(mappings = mappings + ((char, new ItemStack(item))))
	def map(char: Char, block: Block): ShapedRecipeBuilder = copy(mappings = mappings + ((char, new ItemStack(block))))
	def map(char: Char, oreName: String): ShapedRecipeBuilder = copy(mappings = mappings + ((char, oreName)))

	def where(char: Char): RecipeMapping = RecipeMapping(char, this)

	def createRecipe: ShapedOreRecipe = {
		val rows = Seq(row1, row2, row3)

		if(rows.forall(_.isEmpty)) throw new IllegalArgumentException("All rows in recipe builder are empty")
		val usedRows = rows.flatten

		val allowedChars = mappings.keySet + ' '

		usedRows.foreach(row => {
			if(row.exists(!allowedChars.contains(_)))
				throw new IllegalArgumentException(s"The chars [${row.filter(!allowedChars.contains(_))}] are not mapped")
		})

		val usedMappings = mappings.flatMap{ case (char, obj) => Seq(Char.box(char), obj)}.toSeq

		//The ShapedOreRecipe constructor is very picky
		val input: ArrayBuffer[AnyRef] = ArrayBuffer(Boolean.box(mirrored))
		input ++= usedRows
		input ++= usedMappings

		new ShapedOreRecipe(output, input.toArray: _*)
	}

	def build(): Unit = GameRegistry.addRecipe(createRecipe)
}

case class RecipeMapping(char: Char, builder: ShapedRecipeBuilder) {

	def mapsTo(stack: ItemStack): ShapedRecipeBuilder = builder.map(char, stack)
	def mapsTo(item: Item): ShapedRecipeBuilder = builder.map(char, item)
	def mapsTo(block: Block): ShapedRecipeBuilder = builder.map(char, block)
	def mapsTo(oreName: String): ShapedRecipeBuilder = builder.map(char, oreName)
}
