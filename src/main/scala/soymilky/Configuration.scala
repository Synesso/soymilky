package soymilky

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

object Configuration {

  val conf = ConfigFactory.parseResources("account.config")

  implicit class ConfigExtend(conf: Config) {
    def getOptionalInt(key: String): Option[Int] = Try(conf.getInt(key)).toOption
  }
}

