package soymilky

import soymilky.Configuration.conf
import soymilky.rally.Story
import Utterances._
import twitter4j.{Status, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object Twitter {

  // has side effect of actually tweeting
  def tweet(team: String, stories: Set[Story]): Future[Set[Status]] = Future {
    // todo - must cater for statuses that are not successful. Retry on next run.
    val messages: Set[String] = stories.flatMap{phrase(team, _).toOption}
    messages.foreach(println)
    messages.map(twitter.updateStatus)
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
