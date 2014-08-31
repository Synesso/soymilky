package soymilky

import soymilky.Configuration.conf
import soymilky.Utterances._
import soymilky.rally.Story
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Status, TwitterFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Twitter {

  // has side effect of actually tweeting
  def tweet(team: String, stories: Set[Story]): Future[Set[Status]] = {
    // todo - must cater for statuses that are not successful. Retry on next run.
    val messages: Future[Set[String]] = Future.sequence(stories.map{story =>
      phrase(team, story)
    })
    messages.map(_.map(twitter.updateStatus))
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
