
import cats.data.{Writer, WriterT}
import cats.instances.vector._ // for Monoid

Writer(Vector(
  "It was the best of times",
  "it was the worst of times"
), 1859)
// res0: cats.data.WriterT[cats.Id,scala.collection.immutable.Vector[String],Int] = WriterT((Vector(It was the best of times, it was the worst of times),1859))


import cats.instances.vector._   // for Monoid
import cats.syntax.applicative._ // for pure

type Logged[A] = Writer[Vector[String], A]

123.pure[Logged]
// res2: Logged[Int] = WriterT((Vector(),123))


import cats.syntax.writer._ // for tell

Vector("msg1", "msg2", "msg3").tell
// res3: cats.data.Writer[scala.collection.immutable.Vector[String],Unit] = WriterT((Vector(msg1, msg2, msg3),()))

import cats.syntax.writer._ // for writer

val a = Writer(Vector("msg1", "msg2", "msg3"), 123)
// a: cats.data.WriterT[cats.Id,scala.collection.immutable.Vector[String],Int] = WriterT((Vector(msg1, msg2, msg3),123))

val b = 123.writer(Vector("msg1", "msg2", "msg3"))
// b: cats.data.Writer[scala.collection.immutable.Vector[String],Int] = WriterT((Vector(msg1, msg2, msg3),123))


val aResult: Int = a.value
// aResult: Int = 123

val aLog: Vector[String] = a.written
// aLog: Vector[String] = Vector(msg1, msg2, msg3)

val (log, result) = b.run
// log: scala.collection.immutable.Vector[String] = Vector(msg1, msg2, msg3)
// result: Int = 123


val writer1 = for {
  a <- 10.pure[Logged]
  _ <- Vector("a", "b", "c").tell
  b <- 32.writer(Vector("x", "y", "z"))
} yield a + b
// writer1: cats.data.WriterT[cats.Id,Vector[String],Int] = WriterT((Vector(a, b, c, x, y, z),42))

writer1.run
// res4: cats.Id[(Vector[String], Int)] = (Vector(a, b, c, x, y, z),42)

val writer2 = writer1.mapWritten(_.map(_.toUpperCase))
writer2.run




val writer3 = writer1.bimap(
  log => log.map(_.toUpperCase),
  res => res * 100
)
writer3.run

val writer4 = writer1.mapBoth { (log, res) =>
  val log2 = log.map(_ + "!")
  val res2 = res * 1000
  (log2, res2)
}
writer4.run


val writer5 = writer1.reset
writer5.run

val writer6 = writer1.swap
writer6.run


def slowly[A](body: => A) =
try body finally Thread.sleep(100)

type Logged2[A] = Writer[Vector[String], A]

def factorial(n: Int): Logged2[Int] = {
  for {
    ans <- {
      if(n == 0)
        1.pure[Logged2]
      else
        slowly(factorial(n - 1).map(_ * n))
    }
    _ <- Vector(s"fact $n $ans").tell
  } yield ans
}

factorial(10).run

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

val Vector((logA, ansA), (logB, ansB)) =
  Await.result(Future.sequence(Vector(
    Future(factorial(3).run),
    Future(factorial(5).run)
  )), 5.seconds)

// logA: Vector[String] = Vector(fact 0 1, fact 1 1, fact 2 2, fact 3 6)
// ansA: Int = 6
// logB: Vector[String] = Vector(fact 0 1, fact 1 1, fact 2 2, fact 3 6, fact 4 24, fact 5 120)
// ansB: Int = 120


