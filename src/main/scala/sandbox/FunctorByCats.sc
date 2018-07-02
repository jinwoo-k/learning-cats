//import cats.Show
//import cats.functor.Contravariant
//import cats.instances.string._
//
//val showString = Show[String]
//
//import cats.syntax.contravariant._
//showString.contramap[Symbol](_.name).show('dave)  // by syntex
//
//val showSymbol = Contravariant[Show].
//  contramap(showString)((sym: Symbol) => s"'${sym.name}")
//showSymbol.show('dave) // by instance

//
//import cats.Monoid
//import cats.instances.string._ // for Monoid
//import cats.instances.symbol._ // for Monoid
//import cats.syntax.invariant._ // for imap
//import cats.syntax.semigroup._ // for |+|
//
//
//implicit val symbolMonoid: Monoid[Symbol] =
//  Monoid[String].imap(Symbol.apply)(_.name)
//
//Monoid[Symbol].empty
//// res5: Symbol = '
//
//'a |+| 'few |+| 'words
//// res6: Symbol = 'afewwords


import cats.Functor
import cats.instances.function._ // for Functor
import cats.syntax.functor._     // for map

val func1 = (x: Int)    => x.toDouble
val func2 = (y: Double) => y * 2

val func3 = func1.map(func2)