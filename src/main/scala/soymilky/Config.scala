package soymilky

import com.typesafe.config.ConfigFactory

object Config {

  val conf = ConfigFactory.parseResources("account.config")

}
