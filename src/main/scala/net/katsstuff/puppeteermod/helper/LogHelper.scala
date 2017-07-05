package net.katsstuff.puppeteermod.helper

import org.apache.logging.log4j.Level

import net.katsstuff.puppeteermod.lib.LibMod
import net.minecraftforge.fml.common.FMLLog

object LogHelper {

  private def log(level: Level, obj: Any):                       Unit = FMLLog.log(LibMod.Id, level, String.valueOf(obj))
  private def log(level: Level, obj: Any, throwable: Throwable): Unit = FMLLog.log(LibMod.Id, level, throwable, String.valueOf(obj))

  def all(obj: Any):   Unit = log(Level.ALL, obj)
  def debug(obj: Any): Unit = log(Level.DEBUG, obj)
  def info(obj: Any):  Unit = log(Level.INFO, obj)
  def off(obj: Any):   Unit = log(Level.OFF, obj)
  def trace(obj: Any): Unit = log(Level.TRACE, obj)
  def warn(obj: Any):  Unit = log(Level.WARN, obj)

  def error(obj: Any):                       Unit = log(Level.ERROR, obj)
  def error(obj: Any, throwable: Throwable): Unit = log(Level.ERROR, obj, throwable)

  def fatal(obj: Any):                       Unit = log(Level.FATAL, obj)
  def fatal(obj: Any, throwable: Throwable): Unit = log(Level.FATAL, obj, throwable)

}
