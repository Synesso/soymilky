package soymilky

import soymilky.rally.Story

import scala.util.{Random => r, Try}

object Utterances {

  def phrase(team: String, story: Story) = {

    val dictionary: Map[String, String] = Map(
      "team" -> team,
      "id" -> story.FormattedID,
      "pointsOrNothing" -> story.PlanEstimate.map(_.toString).getOrElse(""),
      "pointsOrZero" -> story.PlanEstimate.map(_.toString).getOrElse("0"),
      "pointsOrAlthough" -> points(story)
    )

    resolveTokens(phrases(r.nextInt(phrases.size)), dictionary)
  }

  def resolveTokens(phrase: String, dict: Map[String, String]): Try[String] = {
    val tokens = tokenRegex.findAllMatchIn(phrase).toSeq
    val replacements = tokens.map(_.toString()) zip tokens.map(_.group(1))
    Try(replacements.foldLeft(phrase){case (acc, (_, after)) =>
      acc.replaceFirst("""\$\{""" + after + """\}""", dict(after))
    })
  }

  private val tokenRegex = """\$\{(.*?)\}""".r

  private val phrases: Array[String] = Array(
    "In the end, this will ${team} from a file"
  )

  private val phrases_old: Array[(String, Story) => String] = Array(
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
    (team: String, story: Story) => s"It came to me in a dream, and I forgot it in another dream. $team built ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"The thought of caressing $team makes the tapioca rise in my gullet. ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"Holy Zombie Jesus! $team just put ${story.FormattedID} to bed${points(story)}.",
    (team: String, story: Story) => s"Everyone, I have a dramatic announcement. We did all we could, but $team ended up killing ${story.FormattedID}${points(story)}.",
    (team: String, story: Story) => s"Ah, fine powdered $team's ${story.FormattedID} sprinkles directly into the trunks soothes the fire.",
    (team: String, story: Story) => s"Pine trees have been extinct for 800 years, $team. Gone the way of the poodle and ${story.FormattedID}${points(story)}."
  )

  private def points(story: Story, ifZero: String = ", although it had no points") = story.PlanEstimate
    .map(p => s" for $p point${if (p==1) "" else "s"}")
    .getOrElse(ifZero)

  private def randFloat = String.valueOf(r.nextFloat()).substring(0, 5)

}
