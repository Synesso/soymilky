package soymilky

import soymilky.Config.conf
import soymilky.StoryStore._
import soymilky.rally._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Farnsworth extends App {

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

  allStories.onSuccess{case s => store(s)}
  newStories.onSuccess{case p => println(s"New: $p")}

  Await.ready(newStories, 1 minute)

}
