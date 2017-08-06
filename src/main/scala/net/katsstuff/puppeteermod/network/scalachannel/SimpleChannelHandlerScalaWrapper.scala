package net.katsstuff.puppeteermod.network.scalachannel

import scala.reflect.ClassTag

import org.apache.logging.log4j.Level

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.network.{FMLOutboundHandler, NetworkRegistry}
import net.minecraftforge.fml.relauncher.Side

class SimpleChannelHandlerScalaWrapper[A, Reply](handler: MessageHandler[A, Reply], side: Side)(implicit classTag: ClassTag[A])
    extends SimpleChannelInboundHandler[A](classTag.runtimeClass.asInstanceOf[Class[_ <: A]]) {

  @throws[Exception]
  override protected def channelRead0(ctx: ChannelHandlerContext, msg: A): Unit = {
    val iNetHandler = ctx.channel.attr(NetworkRegistry.NET_HANDLER).get
    val reply       = handler.handle(iNetHandler, msg)

    reply.foreach { rep =>
      ctx.channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.REPLY)
      ctx.writeAndFlush(rep).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
    }
  }

  @throws[Exception]
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    FMLLog.log(Level.ERROR, cause, "SimpleChannelHandlerScalaWrapper exception")
    super.exceptionCaught(ctx, cause)
  }
}
