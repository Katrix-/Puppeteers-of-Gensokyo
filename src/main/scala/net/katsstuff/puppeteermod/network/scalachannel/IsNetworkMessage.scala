package net.katsstuff.puppeteermod.network.scalachannel

trait IsNetworkMessage[A]
trait HasClientHandler[A] extends IsNetworkMessage[A]
trait HasServerHandler[A] extends IsNetworkMessage[A]
