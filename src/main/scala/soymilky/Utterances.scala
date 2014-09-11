package soymilky

import soymilky.Configuration._
import soymilky.rally.Story

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try, Random => r}

object Utterances {

  def phrase(team: String, story: Story): Future[String] = {

    implicit def tryAsFuture[T](attempt: Try[T]) = attempt match {
      case Success(v) => Future successful v
      case Failure(f) => Future failed f
    }

    implicit val dictionary: Map[String, String] = Map(
      "team" -> team,
      "team's" -> (if (team.endsWith("s")) s"$team'" else s"$team's"),
      "id" -> story.FormattedID,
      "points" -> story.PlanEstimate.map(_.toString).getOrElse("0"),
      "forPoints" -> points(story)
    )

    phrases().flatMap {ps => resolveTokens(ps(r.nextInt(ps.size)))}

  }

  def resolveTokens(phrase: String)(implicit dict: Map[String, String]): Try[String] = {
    val tokens = tokenRegex.findAllMatchIn(phrase).toSeq
    val replacements = tokens.map(_.toString()) zip tokens.map(_.group(1))
    Try(replacements.foldLeft(phrase){case (acc, (_, after)) =>
      acc.replaceFirst("""\$\{""" + after + """\}""", dict(after))
    })
  }

  def phrases(source: Source = Source.fromFile(conf.getString("twitter.utterances.file"))): Future[Array[String]] = Future {
    source.getLines().map(_.trim).filter(!_.isEmpty).toArray
  }

  private val tokenRegex = """\$\{(.*?)\}""".r

  private def points(story: Story, ifZero: String = ", although it had no points") = story.PlanEstimate
    .map(p => s" for $p point${if (p==1) "" else "s"}")
    .getOrElse(ifZero)

}
