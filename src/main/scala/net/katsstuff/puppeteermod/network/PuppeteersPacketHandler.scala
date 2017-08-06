package net.katsstuff.puppeteermod.network

import net.katsstuff.puppeteermod.lib.LibMod
import net.katsstuff.puppeteermod.network.scalachannel.ScalaNetworkWrapper

object PuppeteersPacketHandler extends ScalaNetworkWrapper(LibMod.Id) {
  def load(): Unit = {
    registerMessages {
      for {
        _ <- init
        _ <- registerMessage[MountToDoll]
        _ <- registerMessage[DismountFromDoll]
        _ <- registerMessage[DollJumping]
        _ <- registerMessage[DollSneaking]
      } yield ()
    }
  }
}
