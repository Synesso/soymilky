package soymilky

import org.specs2.Specification
import Utterances._

class UtterancesSpec extends Specification { def is = s2"""

  The resolveTokens method should
    resolve empty string $e1
    resolve string with no tokens $e2
    not be confused by half a token $e3
    not be confused by a double dollared token $e4
    resolve as failure any attempt to use a double opened token $e5
    not be confused by a double closed token $e6
    resolve a token $e7
    resolve multiple tokens $e8
    resolve multiple instances of the same token $e9
    resolve a token at the start $e10
    resolve a token at the end $e11
    resolve as failure when token is not found $e12


"""

  def e1 = resolveTokens("", Map.empty) must beSuccessfulTry("")
  def e2 = resolveTokens("there are no tokens", Map.empty) must beSuccessfulTry("there are no tokens")
  def e3 = resolveTokens("there ${are no tokens", Map.empty) must beSuccessfulTry("there ${are no tokens")
  def e4 = resolveTokens("there is $${this} token", Map("this" -> "one")) must beSuccessfulTry("there is $one token")
  def e5 = resolveTokens("there is ${{this} token", Map("{this" -> "one")) must beFailedTry
  def e6 = resolveTokens("there is ${this}} token", Map("this" -> "one")) must beSuccessfulTry("there is one} token")
  def e7 = resolveTokens("there is ${this} token", Map("this" -> "one")) must beSuccessfulTry("there is one token")
  def e8 = resolveTokens("see ${this} and ${that} tokens", Map("this" -> "one", "that" -> "two")) must beSuccessfulTry("see one and two tokens")
  def e9 = resolveTokens("see ${this}, ${this} and ${this}", Map("this" -> "that")) must beSuccessfulTry("see that, that and that")
  def e10 = resolveTokens("${this} is the start", Map("this" -> "that")) must beSuccessfulTry("that is the start")
  def e11 = resolveTokens("this is the ${this}", Map("this" -> "end")) must beSuccessfulTry("this is the end")
  def e12 = resolveTokens("what is ${this}?", Map.empty) must beFailedTry

}