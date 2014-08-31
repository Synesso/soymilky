package soymilky

import java.io.{FileNotFoundException, File}

import org.specs2.Specification
import Utterances._

import scala.io.Source

class UtterancesSpec extends Specification { def is = s2"""

  The resolveTokens method should
    resolve empty string $rt1
    resolve string with no tokens $rt2
    not be confused by half a token $rt3
    not be confused by a double dollared token $rt4
    resolve as failure any attempt to use a double opened token $rt5
    not be confused by a double closed token $rt6
    resolve a token $rt7
    resolve multiple tokens $rt8
    resolve multiple instances of the same token $rt9
    resolve a token at the start $rt10
    resolve a token at the end $rt11
    resolve as failure when token is not found $rt12

  The phrases method should
    return lines from source in an array, eventually $p1
    ignore empty lines $p2
    trim lines $p3


"""

  def rt1 = resolveTokens("")(Map.empty) must beSuccessfulTry("")
  def rt2 = resolveTokens("there are no tokens")(Map.empty) must beSuccessfulTry("there are no tokens")
  def rt3 = resolveTokens("there ${are no tokens")(Map.empty) must beSuccessfulTry("there ${are no tokens")
  def rt4 = resolveTokens("there is $${this} token")(Map("this" -> "one")) must beSuccessfulTry("there is $one token")
  def rt5 = resolveTokens("there is ${{this} token")(Map("{this" -> "one")) must beFailedTry
  def rt6 = resolveTokens("there is ${this}} token")(Map("this" -> "one")) must beSuccessfulTry("there is one} token")
  def rt7 = resolveTokens("there is ${this} token")(Map("this" -> "one")) must beSuccessfulTry("there is one token")
  def rt8 = resolveTokens("see ${this} and ${that} tokens")(Map("this" -> "one", "that" -> "two")) must beSuccessfulTry("see one and two tokens")
  def rt9 = resolveTokens("see ${this}, ${this} and ${this}")(Map("this" -> "that")) must beSuccessfulTry("see that, that and that")
  def rt10 = resolveTokens("${this} is the start")(Map("this" -> "that")) must beSuccessfulTry("that is the start")
  def rt11 = resolveTokens("this is the ${this}")(Map("this" -> "end")) must beSuccessfulTry("this is the end")
  def rt12 = resolveTokens("what is ${this}?")(Map.empty) must beFailedTry

  def p1 = phrases(Source.fromString("one\ntwo\nthree")) must beEqualTo(Array("one", "two", "three")).await
  def p2 = phrases(Source.fromString("one\n\n  \n\t\ntwhree")) must beEqualTo(Array("one", "twhree")).await
  def p3 = phrases(Source.fromString(" one \n\ttw0\t\nthr ee")) must beEqualTo(Array("one", "tw0", "thr ee")).await

}