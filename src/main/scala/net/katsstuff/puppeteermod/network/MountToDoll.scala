package net.katsstuff.puppeteermod.network

import java.util.UUID

import io.netty.buffer.ByteBuf
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.client.ClientProxy
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.helper.{CommonReflectionStuff, LogHelper}
import net.katsstuff.puppeteermod.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class MountToDoll(dollId: Int, playerId: UUID)
object MountToDoll {
  implicit val converter: MessageConverter[MountToDoll] = new MessageConverter[MountToDoll] {
    override def toBytes(a: MountToDoll, buf: ByteBuf): Unit = {
      buf.writeInt(a.dollId)
      buf.writeLong(a.playerId.getMostSignificantBits)
      buf.writeLong(a.playerId.getLeastSignificantBits)
    }

    override def fromBytes(buf: ByteBuf): MountToDoll = MountToDoll(buf.readInt(), new UUID(buf.readLong(), buf.readLong()))
  }

  implicit val handler: ClientMessageHandler[MountToDoll, Unit] = new ClientMessageHandler[MountToDoll, Unit] {
    @SideOnly(Side.CLIENT)
    override def handle(netHandler: NetHandlerPlayClient, a: MountToDoll): Option[Unit] = {
      scheduler.addScheduledTask(MountToDollRunnable(a))
      None
    }
  }
}

case class MountToDollRunnable(a: MountToDoll) extends Runnable {
  override def run(): Unit = {
    val world = Minecraft.getMinecraft.world
    val doll = world.getEntityByID(a.dollId)
    doll match {
      case doll: EntityDoll =>
        val isLocalPlayer = Minecraft.getMinecraft.player.getUniqueID == a.playerId
        val player = if (isLocalPlayer) Minecraft.getMinecraft.player else world.getPlayerEntityByUUID(a.playerId)

        CommonReflectionStuff.setSize.invoke(player, Float.box(doll.width), Float.box(doll.height))
        player.eyeHeight = doll.getEyeHeight

        PuppeteerMod.proxy.asInstanceOf[ClientProxy].clientControlHandler.addControlledDoll(player, doll)
      case _ => LogHelper.warn("Received non doll entity for doll mount packet")
    }
  }
}