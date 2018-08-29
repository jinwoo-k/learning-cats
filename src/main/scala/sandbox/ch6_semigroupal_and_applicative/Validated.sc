//import cats.syntax.either._ // for catchOnly
//import cats.data.Validated
//
//def getValue(fieldName: String)(map: Map[String, String]): Either[List[String], String] =
//  map.get(fieldName)
//    .toRight(List(s"$fieldName 필드를 찾을 수 없습니다."))
//
//val getName = getValue("name")_
//getName(Map())
//
//getName(Map("name" -> "jason"))
//
//def parseInt(name: String)(data: String): Either[List[String], Int] =
//  Validated
//    .catchOnly[NumberFormatException](data.toInt)
//    .leftMap(_ => List(s"$name 필드는 숫자형태여야 합니다."))
//    .toEither
//
//def nonBlank(name: String)(data: String): Either[List[String], String] =
//  Right(data).ensure(List(s"$name 은 공백이 아니어야 합니다."))(!_.isEmpty)
//
//def nonNegative(name: String)(data: Int): Either[List[String], Int] =
//  Right(data).ensure(List(s"$name 은 양수여야 합니다.."))(0 < _)
//
//
//def readName(map: Map[String, String]): Either[List[String], String] =
//  getName(map).flatMap(name => nonBlank("name")(name))
//
//def readAge(map: Map[String, String]): Either[List[String], Int] =
//  getValue("age")(map)
//    .flatMap(age => parseInt("age")(age))
//    .flatMap(age => nonNegative("age")(age))
//
//
//case class User(name: String, age: Int)
//
//def readUser(map: Map[String, String]): Either[List[String], User] = {
//  for {
//    name <- readName(map)
//    age <- readAge(map)
//  } yield User(name, age)
//}
//
//readUser(Map("name" -> "jason", "age" -> "34"))
//readUser(Map("name" -> "", "age" -> "34"))
//readUser(Map("age" -> "34"))
//readUser(Map("name" -> "jason", "age" -> "-34"))