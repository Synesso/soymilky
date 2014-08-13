package soymilky

import soymilky.Config.conf
import soymilky.rally.Story
import soymilky.twitter.Farnsworth._
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

object Twitter {

  def tweet(team: String, stories: Set[Story]): Unit = {
    val message = for (story <- stories) yield phrase(team, story)
    message.foreach(twitter.updateStatus)
    message.foreach(println)
  }

  private lazy val twitter = new TwitterFactory(config).getInstance

  private lazy val config = new ConfigurationBuilder()
    .setOAuthConsumerKey(conf.getString(s"twitter.api.key"))
    .setOAuthConsumerSecret(conf.getString(s"twitter.api.secret"))
    .setOAuthAccessToken(conf.getString(s"twitter.access.token"))
    .setOAuthAccessTokenSecret(conf.getString(s"twitter.access.secret"))
    .setUseSSL(true)
    .build

}
