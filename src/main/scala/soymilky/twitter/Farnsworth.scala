package soymilky.twitter

import soymilky.rally.Story
import scala.util.{Random => r}

object Farnsworth {

  def phrase(team: String, story: Story) = phrases(r.nextInt(phrases.size))(team, story)

  private val phrases: Array[(String, Story) => String] = Array(
    (team: String, story: Story) => s"Good news, everybody! Team $team just delivered ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"$team just finished ${story.FormattedID}${points(story)}. This will allow my starship to travel between galaxies in mere hours!",
    (team: String, story: Story) => s"Today is a special day, and I want you all to be alive. Team $team finished ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"Behold! $team's time traveling machine! Done with ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"Fetch my drinking teeth! $team uncorked ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"My god! Is it possible? $team brought forth ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"It's a miracle $team brought forth ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}${points(story)}. Just in time for the early bird dinner special.",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}${points(story)}. Huzzah!",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}${points(story)}. At this rate, by Tuesday it will be Thursday.",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}${points(story)}. Behold, my mutant atomic supermen!",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}. Dear Lord! That's good${points(story, s" for almost $randFloat points")}.",
    (team: String, story: Story) => s"$team finished ${story.FormattedID} (on the Mathematics of Quantum Neutrino Fields)${points(story)}.",
    (team: String, story: Story) => s"$team's ${story.FormattedID} appears to be no more than a piece of paper smeared with faeces.",
    (team: String, story: Story) => s"${story.FormattedID}${points(story)} by Team $team. This will help me with my wandering bladder.",
    (team: String, story: Story) => s"${story.FormattedID}${points(story)} by Team $team. It's a little experiment that might win me the Nobel Prize.",
    (team: String, story: Story) => s"$team tore the universe a new space hole with ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"Very well. If cop a feel of $team's ${story.FormattedID} I must, then cop a feel I shall${points(story)}.",
    (team: String, story: Story) => s"$team finished ${story.FormattedID}${points(story)}. Yet we still exist. Choke on that, causality!",
    (team: String, story: Story) => s"$team's skills paid the ${story.FormattedID} bills${points(story)}.",
    (team: String, story: Story) => s"$team's ${story.FormattedID} has left me sticky and naked${points(story)}.",
    (team: String, story: Story) => s"Well, this is uncomfortable and humiliating. $team pushed ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"$team, say farewell to ${story.FormattedID} before I jettison it into deep space${points(story)}.",
    (team: String, story: Story) => s"Hmm, ${story.FormattedID} seems larger than normal${points(story)}. Nice job $team",
    (team: String, story: Story) => s"It came to me in a dream, and I forgot it in another dream. $team built ${story.FormattedID}${points(story)}."
  )

  private def points(story: Story, ifZero: String = ", although it had no points") = story.PlanEstimate
    .map(p => s" for $p point${if (p==1) "" else "s"}")
    .getOrElse(ifZero)

  private def randFloat = String.valueOf(r.nextFloat()).substring(0, 5)

}
