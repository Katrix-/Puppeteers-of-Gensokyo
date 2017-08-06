package net.katsstuff.puppeteermod.network

import io.netty.buffer.ByteBuf
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.network.scalachannel.{MessageConverter, ServerMessageHandler}
import net.minecraft.network.NetHandlerPlayServer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DollSneaking(dollId: Int, isPressed: Boolean)
object DollSneaking {
  implicit val converter: MessageConverter[DollSneaking] = new MessageConverter[DollSneaking] {
    override def toBytes(a: DollSneaking, buf: ByteBuf): Unit = {
      buf.writeInt(a.dollId)
      buf.writeBoolean(a.isPressed)
    }

    override def fromBytes(buf: ByteBuf): DollSneaking = DollSneaking(buf.readInt(), buf.readBoolean())
  }

  implicit val handler: ServerMessageHandler[DollSneaking, Unit] = new ServerMessageHandler[DollSneaking, Unit] {
    @SideOnly(Side.SERVER)
    override def handle(netHandler: NetHandlerPlayServer, a: DollSneaking): Option[Unit] = {
      scheduler.addScheduledTask(DollSneakingRunnable(netHandler, a))
      None
    }
  }
}

case class DollSneakingRunnable(netHandler: NetHandlerPlayServer, a: DollSneaking) extends Runnable {
  override def run(): Unit = {
    val world = netHandler.player.world
    Option(world.getEntityByID(a.dollId)).foreach {
      case doll: EntityDoll if PuppeteerMod.proxy.dollControlHandler.isControlling(netHandler.player, doll) => doll.setSneaking(a.isPressed)
      case _                                                                                                =>
    }
  }
}