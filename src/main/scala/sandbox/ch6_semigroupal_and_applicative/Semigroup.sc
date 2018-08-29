import cats.Semigroupal
import cats.instances.option._

import scala.util.Try // for Semigroupal

Semigroupal[Option].product(Some(123), Some("abc"))
// res0: Option[(Int, String)] = Some((123,abc))

Semigroupal[Option].product(None, Some("abc"))
// res1: Option[(Nothing, String)] = None

Semigroupal[Option].product(Some(123), None)
// res2: Option[(Int, Nothing)] = None


import cats.instances.option._ // for Semigroupal

Semigroupal.tuple3(Option(1), Option(2), Option(3))
// res3: Option[(Int, Int, Int)] = Some((1,2,3))

Semigroupal.tuple3(Option(1), Option(2), Option.empty[Int])
// res4: Option[(Int, Int, Int)] = None

import cats.instances.option._ // for Semigroupal
import cats.syntax.apply._     // for tupled and mapN

(Option(123), Option("abc")).tupled
// res7: Option[(Int, String)] = Some((123,abc))

(Option(123), Option("abc"), Option(true)).tupled
// res8: Option[(Int, String, Boolean)] = Some((123,abc,true))



//case class Cat(name: String, born: Int, color: String)
//
//(
//  Option("Garfield"),
//  Option(1978),
//  Option("Orange & black")
//).mapN(Cat.apply)
//// res9: Option[Cat] = Some(Cat(Garfield,1978,Orange & black))



import cats.implicits._
import cats.syntax.semigroup._
import cats.Monoid
import cats.instances.boolean._ // for Monoid
import cats.instances.int._     // for Monoid
import cats.instances.list._    // for Monoid
import cats.instances.string._  // for Monoid
import cats.syntax.apply._      // for imapN

case class Cat(
                name: String,
                yearOfBirth: Int,
                favoriteFoods: List[String]
              )

val tupleToCat: (String, Int, List[String]) => Cat = (n, y, f) =>
  Cat(n, y, f)

val catToTuple: Cat => (String, Int, List[String]) =
  cat => (cat.name, cat.yearOfBirth, cat.favoriteFoods)

implicit val catMonoid: Monoid[Cat] = (
  Monoid[String],
  Monoid[Int],
  Monoid[List[String]]
).imapN(tupleToCat)(catToTuple)


import cats.syntax.semigroup._ // for |+|

val garfield   = Cat("Garfield", 1978, List("Lasagne"))
val heathcliff = Cat("Heathcliff", 1988, List("Junk Food"))

garfield |+| heathcliff
// res17: Cat = Cat(GarfieldHeathcliff,3966,List(Lasagne, Junk Food))

import cats.Semigroupal
import cats.instances.future._ // for Semigroupal
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

val futurePair = Semigroupal[Future].
  product(Future("Hello"), Future(123))

Await.result(futurePair, 1.second)
// res1: (String, Int) = (Hello,123)


import cats.implicits._
import cats.syntax.semigroup._
import cats.Monoid
import cats.instances.boolean._ // for Monoid
import cats.instances.int._     // for Monoid
import cats.instances.list._    // for Monoid
import cats.instances.string._  // for Monoid
import cats.syntax.apply._      // for imapN
import cats.syntax.apply._ // for mapN

case class Cat(
  name: String,
  yearOfBirth: Int,
  favoriteFoods: List[String]
)

val futureCat = (
  Future("Garfield"),
  Future(1978),
  Future(List("Lasagne"))
).mapN(Cat.apply)


Await.result(futureCat, 1.second)
// res4: Cat = Cat(Garfield,1978,List(Lasagne))


import cats.Semigroupal
import cats.instances.list._ // for Semigroupal

Semigroupal[List].product(List(1, 2), List(3, 4))
// res5: List[(Int, Int)] = List((1,3), (1,4), (2,3), (2,4))


import cats.instances.either._ // for Semigroupal

type ErrorOr[A] = Either[Vector[String], A]

Semigroupal[ErrorOr].product(
  Left(Vector("Error 1")),
  Left(Vector("Error 2"))
)
// res7: ErrorOr[(Nothing, Nothing)] = Left(Vector(Error 1))



import cats.Monad

def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] = {
  for {
    xx <- x
    yy <- y
  } yield (xx ,yy)
}


import cats.Semigroupal
import cats.data.Validated
import cats.instances.list._ // for Monoid

type AllErrorsOr[A] = Validated[List[String], A]

Semigroupal[AllErrorsOr].product(
  Validated.valid(100),
  Validated.invalid(List("Error 1"))
)
// res1: AllErrorsOr[(Nothing, Nothing)] = Invalid(List(Error 1, Error 2))

Semigroupal[AllErrorsOr].product(
  Validated.invalid(List("Error 1")),
  Validated.valid(100)
)

Semigroupal[AllErrorsOr].product(
  Validated.valid(200),
  Validated.valid(100)
)



val v = Validated.Valid(123)
// v: cats.data.Validated.Valid[Int] = Valid(123)

val i = Validated.Invalid(List("Badness"))
// i: cats.data.Validated.Invalid[List[String]] = Invalid(List(Badness))


val v = Validated.valid[List[String], Int](123)
// v: cats.data.Validated[List[String],Int] = Valid(123)

val i = Validated.invalid[List[String], Int](List("Badness"))
// i: cats.data.Validated[List[String],Int] = Invalid(List(Badness))

import cats.syntax.validated._ // for valid and invalid

123.valid[List[String]]
// res2: cats.data.Validated[List[String],Int] = Valid(123)

List("Badness").invalid[Int]
// res3: cats.data.Validated[List[String],Int] = Invalid(List(Badness))


Validated.catchOnly[NumberFormatException]("foo".toInt)
// res7: cats.data.Validated[NumberFormatException,Int] = Invalid(java.lang.NumberFormatException: For input string: "foo")

Validated.catchNonFatal(sys.error("Badness"))
// res8: cats.data.Validated[Throwable,Nothing] = Invalid(java.lang.RuntimeException: Badness)

Validated.fromTry(scala.util.Try("foo".toInt))
// res9: cats.data.Validated[Throwable,Int] = Invalid(java.lang.NumberFormatException: For input string: "foo")

Validated.fromEither[String, Int](Left("Badness"))
// res10: cats.data.Validated[String,Int] = Invalid(Badness)

Validated.fromOption[String, Int](None, "Badness")
// res11: cats.data.Validated[String,Int] = Invalid(Badness)


Validated.catchNonFatal(throw new RuntimeException("ok"))



123.valid.map(_ * 100)
// res17: cats.data.Validated[Nothing,Int] = Valid(12300)

"?".invalid.leftMap(_.toString)
// res18: cats.data.Validated[String,Nothing] = Invalid(?)

123.valid[String].bimap(_ + "!", _ * 100)
// res19: cats.data.Validated[String,Int] = Valid(12300)

"?".invalid[Int].bimap(_ + "!", _ * 100)
// res20: cats.data.Validated[String,Int] = Invalid(?!)




import cats.syntax.either._ // for toValidated
// import cats.syntax.either._

"Badness".invalid[Int]
// res21: cats.data.Validated[String,Int] = Invalid(Badness)

"Badness".invalid[Int].toEither
// res22: Either[String,Int] = Left(Badness)

"Badness".invalid[Int].toEither.toValidated
// res23: cats.data.Validated[String,Int] = Invalid(Badness)


41.valid[String].withEither(_.flatMap(n => Right(n + 1)))
// res24: cats.data.Validated[String,Int] = Valid(42)


"!".invalid[Int].withEither(_.flatMap(n => Right(n + 1)))
// res31: cats.data.Validated[String,Int] = Invalid(!)

123.valid[String].ensure("Negative!")(_ > 0)
// res32: cats.data.Validated[String,Int] = Valid(123)

(-123).valid[String].ensure("Negative!")(_ > 0)
// res33: cats.data.Validated[String,Int] = Invalid(Negative!)


"fail".invalid[Int].getOrElse(0)
// res26: Int = 0

"fail".invalid[Int].fold(_ + "!!!", _.toString)
// res27: String = fail!!!








