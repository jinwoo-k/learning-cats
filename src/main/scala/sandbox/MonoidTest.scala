package sandbox


object MonoidTest {
//  implicit def setUnionMonoid[A]: Monoid[Set[A]] =
//    new Monoid[Set[A]] {
//      def combine(a: Set[A], b: Set[A]) = a union b
//      def empty = Set.empty[A]
//    }

  implicit def symDiffMonoid[A]: Monoid[Set[A]] =
    new Monoid[Set[A]] {
      def combine(a: Set[A], b: Set[A]): Set[A] =
        (a diff b) union (b diff a)
      def empty: Set[A] = Set.empty
    }

  implicit def setIntersectionSemigroup[A]: Semigroup[Set[A]] =
    new Semigroup[Set[A]] {
      def combine(a: Set[A], b: Set[A]) = {
        println(a.toString)
        println(b.toString)
        a intersect b
      }
    }

//  implicit val stringSemigroup: Semigroup[Int] = new Semigroup[Int] {
//    def combine(a: Int, b: Int) = a + b
//  }

//  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
//    def combine(a: Int, b: Int) = a + b
//    def empty = 0
//  }

  def main(args: Array[String]): Unit = {
    val intSetMonoid = Monoid[Set[Int]]
    println(intSetMonoid.combine(Set(1,2), Set(2,3)))

    val strSetMonoid = Monoid[Set[String]]
    println(strSetMonoid.combine(Set("a", "b"), Set("b", "c")))

    val strSetSemigroup = Semigroup[Set[String]]
    println(strSetSemigroup.combine(Set("a", "b"), Set("b", "c")))

  }
}

trait Semigroup[A] {
  def combine(x: A, y: A): A
}

object Semigroup {
  def apply[A](implicit semigroup: Semigroup[A]) = {
    semigroup
  }
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

object Monoid {
  def apply[A](implicit monoid: Monoid[A]) =
    monoid
}