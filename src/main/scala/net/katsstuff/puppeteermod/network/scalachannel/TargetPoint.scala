package net.katsstuff.puppeteermod.network.scalachannel

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.network.NetworkRegistry

case class TargetPoint(dimension: Int, x: Double, y: Double, z: Double, range: Double) {
  def toMinecraft: NetworkRegistry.TargetPoint = new NetworkRegistry.TargetPoint(dimension, x, y, z, range)
}
object TargetPoint {
  def around(player: EntityPlayer, range: Double): TargetPoint = TargetPoint(player.dimension, player.posX, player.posY, player.posZ, range)
}
