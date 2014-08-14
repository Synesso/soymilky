package soymilky

import soymilky.Config.conf
import soymilky.rally.Story
import soymilky.twitter.Farnsworth._
import twitter4j.{Status, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Twitter {

  // has side effect of actually tweeting
  def tweet(team: String, stories: Set[Story]): Future[Set[Status]] = Future {
    println(s"Tweeting for $team: $stories")
    val message = for (story <- stories) yield phrase(team, story)
    message.map(twitter.updateStatus)
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
