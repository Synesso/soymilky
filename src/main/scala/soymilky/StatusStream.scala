package soymilky

import twitter4j.{StatusListener, Status, StatusDeletionNotice, StallWarning}

trait StatusStream extends StatusListener {

  def onStatus(status: Status) { println(status.getText) }
  def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
  def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
  def onException(ex: Exception) { ex.printStackTrace() }
  def onScrubGeo(arg0: Long, arg1: Long) {}
  def onStallWarning(warning: StallWarning) {}

}
