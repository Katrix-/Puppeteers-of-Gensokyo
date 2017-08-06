package net.katsstuff.puppeteermod.helper

import java.util

import com.google.common.{base => guava}

object JavaHelper {

  implicit class RichOptional[A](val optional: util.Optional[A]) extends AnyVal {
    def asScala: Option[A] = if(optional.isPresent) Some(optional.get()) else None
  }

  implicit class RichGuavaOptional[A](val optional: guava.Optional[A]) extends AnyVal {
    def asScala: Option[A] = if(optional.isPresent) Some(optional.get()) else None
  }

  implicit class RichOption[A](val option: Option[A]) extends AnyVal {
    def asJava: util.Optional[A] = option.fold(util.Optional.empty[A])(util.Optional.of)
    def asGuava: guava.Optional[A] = option.fold(guava.Optional.absent[A])(guava.Optional.of)
  }

}
