package net.katsstuff.puppeteermod.client

import net.katsstuff.puppeteermod.entity.EntityDoll
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor
import net.minecraft.entity.Entity
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{ItemArmor, ItemStack}
import net.minecraft.util.ResourceLocation

class LayerDollArmor(renderer: RendererDoll) extends LayerBipedArmor(renderer) {

  override def initArmor(): Unit = {
    modelArmor = renderer.activeDoll.modelArmor
    modelLeggings = renderer.activeDoll.modelLeggings
  }

  override def getArmorResource(entity: Entity, stack: ItemStack, slot: EntityEquipmentSlot, `type`: String): ResourceLocation = {
    entity match {
      case doll: EntityDoll if !stack.isEmpty =>
        stack.getItem match {
          case armor: ItemArmor =>
            val folder = doll.dollType.textureArmorFolder

            //Taken from LayerBipedArmor
            var texture = armor.getArmorMaterial.getName
            val idx     = texture.indexOf(':')

            if (idx != -1) {
              texture = texture.substring(idx + 1)
            }
            val resource =
              s"${folder.getResourceDomain}:${folder.getResourcePath}/${texture}_layer_${if (slot == EntityEquipmentSlot.LEGS) 2 else 1}.png"
            new ResourceLocation(resource)
          case _ => super.getArmorResource(entity, stack, slot, `type`)
        }
      case _ => super.getArmorResource(entity, stack, slot, `type`)
    }
  }
}
