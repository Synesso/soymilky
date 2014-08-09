package soymilky

import rally._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import soymilky.Config.conf
import scala.collection.JavaConversions._

object Farnsworth extends App {

  val rally = new RallyClient
  val teams = conf.getStringList("rally.teams").toSeq
  val storiesByTeam: Future[Map[String, Seq[Story]]] = {
    val teamsToFutureStories: Map[String, Future[Seq[Story]]] = teams.map{team => (team, rally.acceptedBy(team))}.toMap
    Future.sequence(teamsToFutureStories.map{case (k,v) => v.map(k -> _)}).map(_.toMap)
  }

  storiesByTeam.foreach { println }

  Await.ready(storiesByTeam, 1 minute)

}
