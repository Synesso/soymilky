package soymilky

import com.typesafe.config.ConfigFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterConfig(handle: String) {

  private val conf = ConfigFactory.parseResources("account.config")

  val config = new ConfigurationBuilder()
    .setOAuthConsumerKey(conf.getString(s"account.$handle.api.key"))
    .setOAuthConsumerSecret(conf.getString(s"account.$handle.api.secret"))
    .setOAuthAccessToken(conf.getString(s"account.$handle.access.token"))
    .setOAuthAccessTokenSecret(conf.getString(s"account.$handle.access.secret"))
    .build

}
