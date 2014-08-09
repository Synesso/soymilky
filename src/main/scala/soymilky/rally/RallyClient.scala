package soymilky.rally

import java.text.SimpleDateFormat
import java.util.Date

import dispatch.Defaults._
import dispatch._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import soymilky.Config.conf

class RallyClient {

  implicit val formats = DefaultFormats

  def acceptedBy(team: String): Future[Seq[Story]] = {
    val (user, pass) = (conf.getString("rally.user"), conf.getString("rally.pass"))
    val byRecordType: Seq[Future[Seq[Story]]] = Seq(story, defect).map{recordType =>
      val svc = url(urlFor(recordType, team)).as(user, pass)
      val json: Future[JValue] = Http(svc OK as.String).map(parse(_)).map{ _ \ "QueryResult" \ "Results" }
      json.map(_.children.map{result: JValue =>
        result.extract[Story]
      })
    }
    Future.sequence(byRecordType).map(_.flatten)
  }

  private def urlFor(recordType: String, team: String, date: Date = new Date) = {
    val day = new SimpleDateFormat("yyyy-MM-dd").format(date)
    s"https://rally1.rallydev.com/slm/webservice/v2.0/$recordType?" +
      s"query=((((Iteration.StartDate%20%3C=%20$day)%20AND%20(Iteration.EndDate%20%3E=%20$day))" +
      s"%20AND%20(Project.Name%20contains%20%22$team%22))%20AND%20(ScheduleState%20=%20Accepted))" +
      "&shallowFetch=FormattedId,PlanEstimate&pageSize=200"
  }

  private val story = "HierarchicalRequirement"
  private val defect = "defect"

}
