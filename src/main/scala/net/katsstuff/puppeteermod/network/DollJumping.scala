package net.katsstuff.puppeteermod.network

import io.netty.buffer.ByteBuf
import net.katsstuff.puppeteermod.PuppeteerMod
import net.katsstuff.puppeteermod.entity.EntityDoll
import net.katsstuff.puppeteermod.network.scalachannel.{MessageConverter, ServerMessageHandler}
import net.minecraft.network.NetHandlerPlayServer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DollJumping(dollId: Int, isPressed: Boolean)
object DollJumping {
  implicit val converter: MessageConverter[DollJumping] = new MessageConverter[DollJumping] {
    override def toBytes(a: DollJumping, buf: ByteBuf): Unit = {
      buf.writeInt(a.dollId)
      buf.writeBoolean(a.isPressed)
    }

    override def fromBytes(buf: ByteBuf): DollJumping = DollJumping(buf.readInt(), buf.readBoolean())
  }

  implicit val handler: ServerMessageHandler[DollJumping, Unit] = new ServerMessageHandler[DollJumping, Unit] {
    @SideOnly(Side.SERVER)
    override def handle(netHandler: NetHandlerPlayServer, a: DollJumping): Option[Unit] = {
      scheduler.addScheduledTask(DollJumpingRunnable(netHandler, a))
      None
    }
  }
}

case class DollJumpingRunnable(netHandler: NetHandlerPlayServer, a: DollJumping) extends Runnable {
  override def run(): Unit = {
    val world = netHandler.player.world
    Option(world.getEntityByID(a.dollId)).foreach {
      case doll: EntityDoll if PuppeteerMod.proxy.dollControlHandler.isControlling(netHandler.player, doll) =>
        if(a.isPressed) doll.getJumpHelper.setJumping()
        else doll.setJumping(false)

      case _                                                                                                =>
    }
  }
}