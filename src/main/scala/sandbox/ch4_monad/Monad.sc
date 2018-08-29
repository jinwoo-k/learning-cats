//import scala.language.higherKinds
//
//trait Monad[F[_]] {
//  def pure[A](a: A): F[A]
//
//  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
//
//  def map[A, B](value: F[A])(func: A => B): F[B] =
//    flatMap(value)(a => pure(func(a)))
//}
//

import cats.Monad
import cats.instances.option._ // for Monad
import cats.instances.list._   // for Monad

val opt1 = Monad[Option].pure(3)
// opt1: Option[Int] = Some(3)

val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
// opt2: Option[Int] = Some(5)

val opt3 = Monad[Option].map(opt2)(a => 100 * a)
// opt3: Option[Int] = Some(500)

val list1 = Monad[List].pure(3)
// list1: List[Int] = List(3)

val list2 = Monad[List].
  flatMap(List(1, 2, 3))(a => List(a, a*10))
// list2: List[Int] = List(1, 10, 2, 20, 3, 30)

val list3 = Monad[List].map(list2)(a => a + 123)
// list3: List[Int] = List(124, 133, 125, 143, 126, 153)


import cats.instances.future._ // for Monad
import scala.concurrent._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

val fm = Monad[Future]
val future = fm.flatMap(fm.pure(1))(x => fm.pure(x + 2))
Await.result(future, 1.second)


import cats.instances.option._   // for Monad
import cats.instances.list._     // for Monad
import cats.syntax.applicative._ // for pure

1.pure[Option]
// res4: Option[Int] = Some(1)

1.pure[List]
// res5: List[Int] = List(1)


import cats.Monad
import cats.syntax.functor._ // for map
import cats.syntax.flatMap._ // for flatMap
import scala.language.higherKinds

//def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
//  a.flatMap(x => b.map(y => x*x + y*y))

import cats.instances.option._ // for Monad
import cats.instances.list._   // for Monad

sumSquare(Option(3), Option(4))
// res8: Option[Int] = Some(25)

sumSquare(List(1, 2, 3), List(4, 5))
// res9: List[Int] = List(17, 26, 20, 29, 25, 34)

def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
  for {
    x <- a
    y <- b
  } yield x*x + y*y

sumSquare(Option(3), Option(4))
// res10: Option[Int] = Some(25)

sumSquare(List(1, 2, 3), List(4, 5))
// res11: List[Int] = List(17, 26, 20, 29, 25, 34)
