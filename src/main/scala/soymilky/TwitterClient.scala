package soymilky

import twitter4j.TwitterStreamFactory

class TwitterClient(handle: String) extends TwitterConfig(handle) with StatusStream {

  val stream = new TwitterStreamFactory(config).getInstance()
  stream.addListener(this)
  stream.cleanUp()
  stream.shutdown()

  def start() = stream.sample()

  def stop() = {
    stream.cleanUp()
    stream.shutdown()
  }
}
