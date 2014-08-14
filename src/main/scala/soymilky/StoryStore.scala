package soymilky

import java.io.{File, PrintWriter}

import soymilky.rally.Story

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.pickling._
import scala.pickling.json._

object StoryStore {

  val storiesFile = new File(".stories")

  def previousStoriesByTeam: Future[Map[String, Set[Story]]] = Future {
    if (storiesFile.createNewFile()) Map.empty
    else {
      try { Source.fromFile(storiesFile).mkString.unpickle[Map[String, Set[Story]]] }
      catch { case e: PicklingException => Map.empty }
    }
  }

  def store(storiesByTeam: Map[String, Set[Story]]): Future[File] = Future {
    val pw = new PrintWriter(storiesFile)
    try {
      pw.write(storiesByTeam.pickle.value)
      storiesFile
    } catch {
      case e: Exception => e.printStackTrace(); throw e;
    } finally {
      pw.close()
    }
  }

}
