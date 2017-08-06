package net.katsstuff.puppeteermod.network.scalachannel

import io.netty.buffer.ByteBuf

trait MessageConverter[A] {

  def toBytes(a: A, buf: ByteBuf): Unit
  def fromBytes(buf: ByteBuf): A
}
