package soymilky

import java.io.File

import soymilky.Config.conf
import soymilky.StoryStore._
import soymilky.Twitter._
import soymilky.rally._
import twitter4j.Status

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

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

  val storiesFile: Future[File] = for {
    all <- allStories
    storage <- store(all)
  } yield storage

  val statuses: Future[Map[String, Set[Status]]] = {
    newStories.flatMap{map =>
      val teamAndFutureStatuses = map.map{case (team, stories) => (team, tweet(team, stories))}
      Future.sequence(teamAndFutureStatuses.map{case (team, futureStatuses) =>
        futureStatuses.map{set => (team, set)}
      }).map(_.toMap)
    }
  }

  val finalValue = Future.sequence(Seq(storiesFile, statuses))
  finalValue.onFailure{case t: Throwable => throw t}
  Await.ready(finalValue, 1 minute)

}
