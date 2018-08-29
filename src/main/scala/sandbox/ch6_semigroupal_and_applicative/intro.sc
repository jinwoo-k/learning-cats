import cats.syntax.either._

import scala.concurrent.{Await, Future} // for catchOnly

def parseInt(str: String): Either[String, Int] =
  Either.catchOnly[NumberFormatException](str.toInt).
    leftMap(_ => s"Couldn't read $str")

for {
  a <- parseInt("a")
  b <- parseInt("b")
  c <- parseInt("c")
} yield (a + b + c)
// res1: scala.util.Either[String,Int] = Left(Couldn't read a)

