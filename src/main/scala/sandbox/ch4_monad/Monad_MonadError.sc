import cats.MonadError
import cats.instances.either._ // for MonadError

type ErrorOr[A] = Either[String, A]

//val monadError = MonadError[ErrorOr, String]
//
//val success = monadError.pure(42)
//// success: ErrorOr[Int] = Right(42)
//
//val failure = monadError.raiseError("Badness")
//// failure: ErrorOr[Nothing] = Left(Badness)
//
//
//monadError.handleError(failure) {
//  case "Badness" =>
//    monadError.pure("It's ok")
//
//  case other =>
//    monadError.raiseError("It's not ok")
//}
//// res2: ErrorOr[ErrorOr[String]] = Right(Right(It's ok))
//
//import cats.syntax.either._ // for asRight
//
//monadError.ensure(success)("Number too low!")(_ > 1000)
//// res3: ErrorOr[Int] = Left(Number too low!)

import cats.syntax.applicative._      // for pure
import cats.syntax.applicativeError._ // for raiseError etc
import cats.syntax.monadError._       // for ensure

val success = 42.pure[ErrorOr]
// success: ErrorOr[Int] = Right(42)

val failure = "Badness".raiseError[ErrorOr, Int]
// failure: ErrorOr[Int] = Left(Badness)

success.ensure("Number to low!")(_ > 1000)
// res4: Either[String,Int] = Left(Number to low!)


import scala.util.Try
import cats.instances.try_._ // for MonadError

val exn: Throwable =
  new RuntimeException("It's all gone wrong")


"".pure[Try]

exn.raiseError[Try, Int]
// res6: scala.util.Try[Int] = Failure(java.lang.RuntimeException: It's all gone wrong)
