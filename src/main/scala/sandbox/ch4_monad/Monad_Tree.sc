import cats.Monad

import scala.annotation.tailrec

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]

def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
  Branch(left, right)

def leaf[A](value: A): Tree[A] =
  Leaf(value)

implicit val treeMonad = new Monad[Tree] {
  override def pure[A](x: A): Tree[A] =
    Leaf(x)

  override def flatMap[A, B](fa: Tree[A])(f: A => Tree[B]): Tree[B] = {
    fa match {
      case Leaf(a) => f(a)
      case Branch(l, r) => Branch(flatMap(l)(f), flatMap(r)(f))
    }
  }

  override def tailRecM[A, B](a: A)(f: A => Tree[Either[A, B]]): Tree[B] = {
    f(a) match {
      case Leaf(e) => e match {
        case Left(l) => tailRecM(l)(f)
        case Right(b) => Leaf(b)
      }
      case Branch(l, r) =>
        Branch(
          flatMap(l) {
            case Left(ll) => tailRecM(ll)(f)
            case Right(lr) => pure(lr)
          },
          flatMap(r) {
            case Left(rl) => tailRecM(rl)(f)
            case Right(rr) => pure(rr)
          }
        )
    }
  }

  def tailRecMStackSafty[A, B](arg: A)(func: A => Tree[Either[A, B]]): Tree[B] = {
    @tailrec
    def loop(
        open: List[Tree[Either[A, B]]],
        closed: List[Tree[B]]): List[Tree[B]] =
      open match {
        case Branch(l, r) :: next =>
          l match {
            case Branch(_, _) =>
              loop(l :: r :: next, closed)
            case Leaf(Left(value)) =>
              loop(func(value) :: r :: next, closed)
            case Leaf(Right(value)) =>
              loop(r :: next, pure(value) :: closed)
          }

        case Leaf(Left(value)) :: next =>
          loop(func(value) :: next, closed)

        case Leaf(Right(value)) :: next =>
          closed match {
            case head :: tail =>
              loop(next, Branch(head, pure(value)) :: tail)
            case Nil =>
              loop(next, pure(value) :: closed)
          }
        case Nil =>
          closed
      }

    loop(List(func(arg)), Nil).head
  }
}

import cats.syntax.functor._ // for map
import cats.syntax.flatMap._ // for flatMap

val res = branch(leaf(100), leaf(200)).
  flatMap(x => branch(leaf(x - 1), leaf(x + 1)))
// res3: wrapper.Tree[Int] = Branch(Branch(Leaf(99),Leaf(101)),Branch(Leaf(199),Leaf(201)))

for {
  a <- branch(leaf(100), leaf(200))
  b <- branch(leaf(a - 10), leaf(a + 10))
  c <- branch(leaf(b - 1), leaf(b + 1))
} yield c


