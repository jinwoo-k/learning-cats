
import cats.Monoid
import cats.instances.string._

Monoid[String].combine("Hi ", "there!")
Monoid[String].empty

import cats.Semigroup
Semigroup[String].combine("Hi ", "there!2")

import cats.instances.int._
Monoid[Int].combine(32, 10)

import cats.instances.option._
Monoid[Option[Int]].combine(Some(1), Some(10))
Monoid[Option[Int]].combine(Some(1), Option(10))
Monoid[Option[Int]].combine(Option(1), Option(10))

import cats.syntax.semigroup._
"Hi " |+| "there!!"
1 |+| 2
Option(1) |+| Some(2)

Option.empty[Int] |+| Option(2)

def add[A: Monoid](items: List[A]): A =
  items.foldLeft(Monoid[A].empty)(_ |+| _)

add(List(Option(1), Option(100)))
add(List(Option.empty[Int]))
add(List(Option(1), Some(2), Some(3)))


case class Order(totalCost: Double, quantity: Double)

implicit val monoidOrder: Monoid[Order] = new Monoid[Order] {
  override def empty = Order(0, 0)

  override def combine(x: Order, y: Order) = Order(x.totalCost + y.totalCost, x.quantity + y.quantity)
}

add(List(Order(1, 100), Order(5, 200)))

