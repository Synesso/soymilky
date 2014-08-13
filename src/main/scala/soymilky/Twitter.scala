package soymilky

import soymilky.Config.conf
import soymilky.rally.Story
import twitter4j.{StatusUpdate, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

object Twitter {

  def tweet(team: String, stories: Set[Story]): Unit = {
    for (story <- stories) {
      val id = story.FormattedID
      val points = story.PlanEstimate.map(p => s" for $p points").getOrElse(", but it had no points")
      twitter.updateStatus(s"$team delivered $id$points.")
    }
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
