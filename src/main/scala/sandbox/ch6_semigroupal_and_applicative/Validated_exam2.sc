import cats.data.Validated
import cats.syntax.either._
import cats.syntax.validated._ // for valid and invalid
import cats.instances.list._ // for Semigroupal
import cats.syntax.apply._   // for mapN

type Fail[A] = Validated[List[String], A]

def getValue(fieldName: String)(map: Map[String, String]): Fail[String] =
  map.get(fieldName)
    .toRight(List(s"$fieldName 필드를 찾을 수 없습니다."))
    .toValidated

val getName = getValue("name")_

def parseInt(name: String)(data: String): Fail[Int] =
  Validated
    .catchOnly[NumberFormatException](data.toInt)
    .leftMap(_ => List(s"$name 필드는 숫자형태여야 합니다."))

def nonBlank(name: String)(data: String): Fail[String] =
  data.valid.ensure(List(s"$name 은 공백이 아니어야 합니다."))(!_.isEmpty)

def nonNegative(name: String)(data: Int): Fail[Int] =
  data.valid.ensure(List(s"$name 은 양수여야 합니다.."))(0 < _)


def readName(map: Map[String, String]): Fail[String] =
  getName(map)
    .withEither(_.flatMap(name => nonBlank("name")(name).toEither))

def readAge(map: Map[String, String]): Fail[Int] =
  getValue("age")(map).withEither {
    _.flatMap(age => parseInt("age")(age).toEither)
      .flatMap(age => nonNegative("age")(age).toEither)
  }

case class User(name: String, age: Int)



def readUser(map: Map[String, String]): Fail[User] = {
  (readName(map), readAge(map)).mapN(User.apply)
}

readUser(Map("name" -> "jason", "age" -> "34"))
readUser(Map("name" -> "", "age" -> "34"))
readUser(Map("age" -> "34"))
readUser(Map("name" -> "jason", "age" -> "-34"))

