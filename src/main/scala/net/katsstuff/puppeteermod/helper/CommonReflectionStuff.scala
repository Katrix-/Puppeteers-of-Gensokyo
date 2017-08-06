package net.katsstuff.puppeteermod.helper

import java.lang.reflect.Method

import net.minecraft.entity.Entity
import net.minecraftforge.fml.relauncher.ReflectionHelper

object CommonReflectionStuff {

  val setSize: Method = ReflectionHelper.findMethod(classOf[Entity], "setSize", "func_70105_a(FF)V", classOf[Float], classOf[Float])

}
