package sandbox

import cats.kernel.Eq

object Equality {
  final case class Cat(name: String, age: Int, color: String)

  def main(args: Array[String]): Unit = {
    val cat1 = Cat("Garfield",   38, "orange and black")
    val cat2 = Cat("Heathcliff", 33, "orange and black")

    val optionCat1 = Option(cat1)
    val optionCat2 = Option.empty[Cat]

    import cats.instances.int._
    import cats.instances.string._
    import cats.syntax.eq._

    implicit val catEq: Eq[Cat] = Eq.instance((cat1, cat2) => {
      cat1.name === cat2.name &&
      cat1.age === cat2.age &&
      cat1.color === cat2.color

    })

    import cats.instances.option._

    println(cat1 === cat2)  // false
    println(optionCat1 === Option.empty[Cat]) // false
    println(optionCat1 === optionCat2)  // false
    println(optionCat1 === optionCat1)  // true
//    println(optionCat1 === cat1)  // compile error
  }
}


