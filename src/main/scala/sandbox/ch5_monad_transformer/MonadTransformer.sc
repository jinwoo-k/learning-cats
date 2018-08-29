import cats.data.OptionT

import scala.util.Try

type ListOption[A] = OptionT[List, A]

import cats.Monad
import cats.instances.list._     // for Monad
import cats.syntax.applicative._ // for pure

val result1: ListOption[Int] = OptionT(List(Option(10)))
// result1: ListOption[Int] = OptionT(List(Some(10)))

val result2: ListOption[Int] = 32.pure[ListOption]
// result2: ListOption[Int] = OptionT(List(Some(32)))


val result3 = result1.flatMap { x: Int =>
  result2.map { y: Int =>
    x + y
  }
}


val result4: ListOption[Int] = OptionT(List(Option(10), Option(11)))
val result5: ListOption[Int] = OptionT(List(Option(20), Option(21)))

val result6 = result4.flatMap { x: Int =>
  result5.map { y: Int =>
    x + y
  }
}

// Alias Either to a type constructor with one parameter:
type ErrorOr[A] = Either[String, A]

// Build our final monad stack using OptionT:
type ErrorOrOption[A] = OptionT[ErrorOr, A]


import cats.instances.either._ // for Monad

val a = 10.pure[ErrorOrOption]
// a: ErrorOrOption[Int] = OptionT(Right(Some(10)))

val b = 32.pure[ErrorOrOption]
// b: ErrorOrOption[Int] = OptionT(Right(Some(32)))

val c = a.flatMap(x => b.map(y => x + y))
// c: cats.data.OptionT[ErrorOr,Int] = OptionT(Right(Some(42)))

val d = OptionT(Left("fail"): ErrorOr[Option[Int]])
val e = a.flatMap(x => d.map(y => x + y))


import scala.concurrent.Future
import cats.data.{EitherT, OptionT}

type FutureEither[A] = EitherT[Future, String, A]

type FutureEitherOption[A] = OptionT[FutureEither, A]

import cats.instances.future._ // for Monad
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

val futureEitherOr: FutureEitherOption[Int] =
  for {
    a <- 10.pure[FutureEitherOption]
    b <- 32.pure[FutureEitherOption]
  } yield a + b

val res = Await.result(futureEitherOr.value.value, Duration.Inf)


// Create using apply:
val errorStack1 = OptionT[ErrorOr, Int](Right(Some(10)))
// errorStack1: cats.data.OptionT[ErrorOr,Int] = OptionT(Right(Some(10)))

// Create using pure:
val errorStack2 = 32.pure[ErrorOrOption]
// errorStack2: ErrorOrOption[Int] = OptionT(Right(Some(32)))
// Extracting the untransformed monad stack:

errorStack1.value
// res11: ErrorOr[Option[Int]] = Right(Some(10))

// Mapping over the Either in the stack:
errorStack2.value.map(_.getOrElse(-1))
// res13: scala.util.Either[String,Int] = Right(32)

futureEitherOr
// res14: FutureEitherOption[Int] = OptionT(EitherT(Future(Success(Right(Some(42))))))

val intermediate = futureEitherOr.value
// intermediate: FutureEither[Option[Int]] = EitherT(Future(Success(Right(Some(42)))))

val stack = intermediate.value
// stack: scala.concurrent.Future[Either[String,Option[Int]]] = Future(Success(Right(Some(42))))

Await.result(stack, 1.second)
// res15: Either[String,Option[Int]] = Right(Some(42))// Create using apply:



import cats.data.Writer

type Logged[A] = Writer[List[String], A]

// Methods generally return untransformed stacks:
def parseNumber(str: String): Logged[Option[Int]] =
  util.Try(str.toInt).toOption match {
    case Some(num) => Writer(List(s"Read $str"), Some(num))
    case None      => Writer(List(s"Failed on $str"), None)
  }

// Consumers use monad transformers locally to simplify composition:
def addAll(a: String, b: String, c: String): Logged[Option[Int]] = {
  import cats.data.OptionT

  val result = for {
    a <- OptionT(parseNumber(a))
    b <- OptionT(parseNumber(b))
    c <- OptionT(parseNumber(c))
  } yield a + b + c

  result.value
}

// This approach doesn't force OptionT on other users' code:
val result1 = addAll("1", "2", "3")
// result1: Logged[Option[Int]] = WriterT((List(Read 1, Read 2, Read 3),Some(6)))

val result2 = addAll("1", "a", "3")
// result2: Logged[Option[Int]] = WriterT((List(Read 1, Failed on a),None))import cats.data.Writer


val powerLevels = Map(
  "Jazz"      -> 6,
  "Bumblebee" -> 8,
  "Hot Rod"   -> 10
)

type Response[A] = EitherT[Future, String, A]

def getPowerLevel(autobot: String): Response[Int] = {
  powerLevels.get(autobot) match {
    case Some(level) => EitherT.right(Future(level))
    case None => EitherT.left(Future(s"$autobot unreachable"))
  }
}

def canSpecialMove(ally1: String, ally2: String): Response[Boolean] = {
  for {
    p1 <- getPowerLevel(ally1)
    p2 <- getPowerLevel(ally2)
  } yield 15 < p1 + p2
}


def tacticalReport(ally1: String, ally2: String): String = {
  canSpecialMove(ally1, ally2)
}