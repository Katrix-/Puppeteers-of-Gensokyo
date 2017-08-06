package net.katsstuff.puppeteermod.network

import java.util.UUID

import io.netty.buffer.ByteBuf
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.client.ClientProxy
import net.katsstuff.puppeteermod.helper.CommonReflectionStuff
import net.katsstuff.puppeteermod.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DismountFromDoll(playerId: UUID)
object DismountFromDoll {
  implicit val converter: MessageConverter[DismountFromDoll] = new MessageConverter[DismountFromDoll] {
    override def toBytes(a: DismountFromDoll, buf: ByteBuf): Unit = {
      buf.writeLong(a.playerId.getMostSignificantBits)
      buf.writeLong(a.playerId.getLeastSignificantBits)
    }

    override def fromBytes(buf: ByteBuf): DismountFromDoll = DismountFromDoll(new UUID(buf.readLong(), buf.readLong()))
  }

  implicit val handler: ClientMessageHandler[DismountFromDoll, Unit] = new ClientMessageHandler[DismountFromDoll, Unit] {
    @SideOnly(Side.CLIENT)
    override def handle(netHandler: NetHandlerPlayClient, a: DismountFromDoll): Option[Unit] = {
      scheduler.addScheduledTask(DismountFromDollRunnable(a))
      None
    }
  }
}

case class DismountFromDollRunnable(a: DismountFromDoll) extends Runnable {
  override def run(): Unit = {
    val world         = Minecraft.getMinecraft.world
    val isLocalPlayer = Minecraft.getMinecraft.player.getUniqueID == a.playerId
    val player        = if (isLocalPlayer) Minecraft.getMinecraft.player else world.getPlayerEntityByUUID(a.playerId)

    CommonReflectionStuff.setSize.invoke(player, Float.box(0.6F), Float.box(1.8F))
    player.eyeHeight = player.getDefaultEyeHeight

    PuppeteerMod.proxy.asInstanceOf[ClientProxy].clientControlHandler.removeControlledDoll(player)
  }
}
