package sandbox

object CovariantAndContravariant {
  sealed trait Shape
  case class Circle(radius: Double) extends Shape

  def covariant(): Unit = {
    // Covariance means that the type F[B] is a subtype of the type F[A] if B is a subtype of A

    // List[+A] <- covariant
    val circles: List[Circle] = List(Circle(1))
    val shapes: List[Shape] = circles
    println(shapes)
  }

  def contravariant(): Unit = {
    // contravariance means that the type F[B] is a subtype of F[A] if A is a subtype of B

    trait JsonWriter[-A] {  // <- A is contravariant
      def write(value: A): Json
    }

    val shapeWriter: JsonWriter[Shape] = new JsonWriter[Shape] {
      override def write(value: Shape) = JsString(value.toString)
    }
    val circleWriter: JsonWriter[Circle] = shapeWriter


    def format[A](value: A, writer: JsonWriter[A]): Json =
      writer.write(value)

    println(format(Circle(1), circleWriter))
    println(format(Circle(1), shapeWriter))

//    println(format(Circle(1): Shape, circleWriter)) // type mismatch
//    println(format(Circle(1): Shape, shapeWriter))

  }

  def main(args: Array[String]): Unit = {
    contravariant()
  }

}
