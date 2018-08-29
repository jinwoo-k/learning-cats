import cats.Id
import cats.Monad


"Dave" : Id[String] // res3: cats.Id[String] = Dave

123 : Id[Int] // res4: cats.Id[Int] = 123

List(1, 2, 3) : Id[List[Int]] // res5: cats.Id[List[Int]] = List(1, 2, 3)


val a = Monad[Id].pure(3) // a: cats.Id[Int] = 3

val b = Monad[Id].flatMap(a)(_ + 1) // b: cats.Id[Int] = 4

import cats.syntax.functor._ // for map
import cats.syntax.flatMap._ // for flatMap

for {
  x <- a
  y <- b
} yield x + y
// res6: cats.Id[Int] = 7

val c = Monad[Id].pure("기나다라")
val d = Monad[Id].pure("마바사")

val k = for {
    x <- c
    y <- d
  } yield {
  s"$x$y"
}

k.mkString(",")

import cats.Id

def pure[A](value: A): Id[A] = value

def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)

def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)

