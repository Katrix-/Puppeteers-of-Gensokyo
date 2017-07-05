package net.katsstuff.puppeteermod

import java.util.UUID

import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.{DataParameter, DataSerializer}

class OptionSerializers[A](serializer: DataSerializer[A]) extends DataSerializer[Option[A]] {

  override def write(buf: PacketBuffer, value: Option[A]): Unit = value match {
    case Some(present) =>
      buf.writeBoolean(true)
      serializer.write(buf, present)
    case None => buf.writeBoolean(false)
  }
  override def read(buf: PacketBuffer): Option[A] =
    if (buf.readBoolean()) {
      Some(serializer.read(buf))
    } else None

  override def createKey(id: Int): DataParameter[Option[A]] = new DataParameter(id, this)
}

object UUIDSerializer extends DataSerializer[UUID] {

  override def createKey(id: Int):                    DataParameter[UUID] = new DataParameter(id, this)
  override def write(buf: PacketBuffer, value: UUID): Unit                = buf.writeUniqueId(value)
  override def read(buf: PacketBuffer):               UUID                = buf.readUniqueId()
}

object OptionUUIDSerializer extends OptionSerializers(UUIDSerializer)
