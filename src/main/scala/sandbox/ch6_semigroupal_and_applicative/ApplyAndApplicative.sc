import cats.Foldable
import cats.instances.list._ // for Foldable

val ints = List(1, 2, 3)

Foldable[List].foldLeft(ints, 0)(_ + _)
// res1: Int = 6

import cats.instances.option._ // for Foldable

val maybeInt = Option(123)

Foldable[Option].foldLeft(maybeInt, 10)(_ * _)
// res3: Int = 1230

import cats.instances.int._ // for Monoid

Foldable[List].combineAll(List(1, 2, 3))
// res12: Int = 6

Foldable[List].fold(List(1, 2, 3))


import cats.instances.string._ // for Monoid

Foldable[List].foldMap(List(1, 2, 3))(_.toString)
// res13: String = 123



import cats.syntax.foldable._ // for combineAll and foldMap

List(1, 2, 3).combineAll
// res16: Int = 6

List(1, 2, 3).foldMap(_.toString)
// res17: String = 123


import cats.instances.stream._
import cats.Eval
val eval = Stream(1,2,3,4).foldRight(Eval.now(0)) { case (a, b) => b.map(_ + a) }
eval.value
