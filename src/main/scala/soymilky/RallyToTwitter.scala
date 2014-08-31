package soymilky

import java.io.File

import soymilky.Configuration._
import soymilky.StoryStore._
import soymilky.Twitter._
import soymilky.rally._
import twitter4j.Status

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object RallyToTwitter extends App {

  type FutureStories = Future[Map[String, Set[Story]]]

  val rally = new RallyClient
  val teams = conf.getStringList("rally.teams").toSet

  val storiesFromLastRun: FutureStories = previousStoriesByTeam

  val storiesFromRally: FutureStories = {
    val teamsToFutureStories: Map[String, Future[Set[Story]]] = teams.map{team => (team, rally.acceptedBy(team))}.toMap
    Future.sequence(teamsToFutureStories.map{case (k,v) => v.map(k -> _)}).map(_.toMap)
  }

  val allStories: FutureStories = for {
    justNow <- storiesFromRally
    fromBefore <- storiesFromLastRun
  } yield justNow ++ fromBefore.map{case (k,v) => (k, justNow.get(k).map(_ ++ v) getOrElse v)}

  val newStories: FutureStories = for {
    all <- allStories
    fromBefore <- storiesFromLastRun
  } yield all.map{ case (k, v) => (k, v -- fromBefore.getOrElse(k, Set.empty))}

  newStories.onSuccess{case m: Map[String, Set[Story]] =>
    println(s"New stories found: ${m.values.map(_.size).reduceLeft(_+_)}")
  }

  // if batch size is defined as n, we want the first n of newStories only, otherwise the entire thing
  val storiesToProcess: FutureStories = conf.getOptionalInt("batch.size").map{size: Int =>
    newStories.map{teamsToStories: Map[String, Set[Story]] =>
      teamsToStories.foldLeft((size, Map.empty[String, Set[Story]])) { case ((remaining, map), (team, stories)) =>
        val taking: Int = math.min(remaining, stories.size)
        (remaining - taking, map + (team -> stories.take(taking)))
      }._2
    }
  }.getOrElse(newStories)

  storiesToProcess.onSuccess{case m: Map[String, Set[Story]] =>
    println(s"Stories to be tweeted: ${m.values.map(_.size).reduceLeft(_+_)}")
  }

  // save to storage the union of storiesToProcess and storiesFromLastRun
  val storiesFile: Future[File] = for {
    fromBefore <- storiesFromLastRun
    processedNow <- storiesToProcess
    storage <- store(fromBefore.map{case (k,v) => (k, v ++ processedNow.getOrElse(k, Set.empty))} ++
      processedNow.filterKeys(!fromBefore.contains(_)))
  } yield storage

  // tweet all of the storiesToProcess and collect the statuses
  val statuses: Future[Map[String, Set[Status]]] = {
    storiesToProcess.flatMap{map =>
      val teamAndFutureStatuses = map.map{case (team, stories) => (team, tweet(team, stories))}
      Future.sequence(teamAndFutureStatuses.map{case (team, futureStatuses) =>
        futureStatuses.map{set => (team, set)}
      }).map(_.toMap)
    }
  }

  val finalValue = Future.sequence(Seq(storiesFile, statuses))
  statuses.onSuccess{case map => map.foreach{case (team, status) => status.map(_.getText).foreach(println)}}
  finalValue.onFailure{case t: Throwable => throw t}
  Await.ready(finalValue, 1 minute)

}
