package sandbox

sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Double) extends Json
case object JsNull extends Json

// type class
// The "serialize to JSON" behaviour is encoded in this trait
trait JsonWriter[A] {
  def write(value: A): Json
}

final case class Person(name: String, email: String)

// type class instances
object JsonWriterInstances {
  implicit val stringWriter: JsonWriter[String] =
    new JsonWriter[String] {
      def write(value: String): Json =
        JsString(value)
    }

  implicit val personWriter: JsonWriter[Person] =
    new JsonWriter[Person] {
      def write(value: Person): Json =
        JsObject(Map(
          "name" -> JsString(value.name),
          "email" -> JsString(value.email)
        ))
    }
  implicit def optionWriter[A](implicit writer: JsonWriter[A]): JsonWriter[Option[A]] =
    new JsonWriter[Option[A]] {
      def write(option: Option[A]): Json =
        option match {
          case Some(aValue) => writer.write(aValue)
          case None         => JsNull
        }
    }
}

// interface object
object Json {
  def toJson[A](value: A)(implicit w: JsonWriter[A]): Json =
    w.write(value)
}

// interface syntex
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }
}



object TypeClass {
  final case class Cat(name: String, age: Int, color: String)

  def main(args: Array[String]): Unit = {

    // use type class interface object
    import JsonWriterInstances._
    Json.toJson(Person("Dave", "dave@example.com"))
    Json.toJson(Person("Dave", "dave@example.com"))(personWriter)

    // use type class interface syntex

//    import JsonWriterInstances._
    import JsonSyntax._
    Person("Dave", "dave@example.com").toJson
//    Person("Dave", "dave@example.com").toJson(personWriter)

    Option(Person("Dave", "dave@example.com")).toJson

    trait Printable[A] {
      def format(a: A): String
    }

    object PrintableInstances {
      implicit val stringPrinter: Printable[String] = new Printable[String] {
        override def format(a: String): String = a.toString
      }

      implicit val intPrinter: Printable[Int] = new Printable[Int] {
        override def format(a: Int): String = a.toString
      }

      implicit val catPrinter: Printable[Cat] = new Printable[Cat] {
        override def format(a: Cat): String = {
          s"${a.name} is a ${a.age} year-old ${a.color} cat."
        }
      }
    }

    object Printable {
      def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)
      def print[A](value: A)(implicit p: Printable[A]): Unit = println(p.format(value))
    }

    import PrintableInstances._
    Printable.print(1)

    val cat = Cat("The MyCat", 10, "red")
    Printable.print(cat)

    import cats._
    import cats.implicits._
    implicit val catShow: Show[Cat] = Show.show(cat =>
      s"${cat.name.show} is a ${cat.age.show} year-old ${cat.color.show} cat."
    )

    println(cat.show)
  }
}
