val either1: Either[String, Int] = Right(10)
val either2: Either[String, Int] = Right(32)

for {
  a <- either1.right
  b <- either2.right
} yield a + b


import cats.syntax.either._ // for asRight

val a = 3.asRight[String]
// a: Either[String,Int] = Right(3)

val b = 4.asRight[String]
// b: Either[String,Int] = Right(4)

for {
  x <- a
  y <- b
} yield x*x + y*y
// res4: scala.util.Either[String,Int] = Right(25)


val c = "abc".asRight[String]
val d = "def".asRight[String]
for {
  x <- c
  y <- d
} yield x + y


def countPositive(nums: List[Int]) =
  nums.foldLeft(Right(0): Either[String, Int]) { (accumulator, num) =>
    if(num > 0) {
      accumulator.map(_ + 1)
    } else {
      Left("Negative. Stopping!")
    }
  }

countPositive(List(1,2,3))

import cats.syntax.either._

"Error".asLeft[Int].getOrElse(0)
// res11: Int = 0

"Error".asLeft[Int].orElse(2.asRight[String])
// res12: Either[String,Int] = Right(2)


(-1).asRight[String].ensure("Must be non-negative!")(_ > 0)
// res13: Either[String,Int] = Left(Must be non-negative!)


"error".asLeft[Int].recover {
  case str: String => -1
}
// res14: Either[String,Int] = Right(-1)

"error".asLeft[Int].recoverWith {
  case str: String => Right(-1)
}
// res15: Either[String,Int] = Right(-1)


"foo".asLeft[Int].leftMap(_.reverse)
// res16: Either[String,Int] = Left(oof)

6.asRight[String].bimap(_.reverse, _ * 7)
// res17: Either[String,Int] = Right(42)

"bar".asLeft[Int].bimap(_.reverse, _ * 7)
// res18: Either[String,Int] = Left(rab)


123.asRight[String]
// res19: Either[String,Int] = Right(123)

123.asRight[String].swap
// res20: scala.util.Either[Int,String] = Left(123)





sealed trait LoginError extends Product with Serializable
final case class UserNotFound(username: String) extends LoginError
final case class PasswordIncorrect(username: String) extends LoginError
case object UnexpectedError extends LoginError


case class User(username: String, password: String)

type LoginResult = Either[LoginError, User]


// Choose error-handling behaviour based on type:
def handleError(error: LoginError): Unit =
  error match {
    case UserNotFound(u) =>
      println(s"User not found: $u")

    case PasswordIncorrect(u) =>
      println(s"Password incorrect: $u")

    case UnexpectedError =>
      println(s"Unexpected error")
  }

val result1: LoginResult = User("dave", "passw0rd").asRight
// result1: LoginResult = Right(User(dave,passw0rd))

val result2: LoginResult = UserNotFound("dave").asLeft
// result2: LoginResult = Left(UserNotFound(dave))

result1.fold(handleError, println)
// User(dave,passw0rd)

result2.fold(handleError, println)
// User not found: dave


