import cats.syntax.functor._     // for map

val func1: Int => Double =
  (x: Int) => x.toDouble

val func2: Double => Double =
  (y: Double) => y * 2

//(func1 map func2)(1)     // composition using map > intellij does not compile this code!
// res7: Double = 2.0

(func1 andThen func2)(1) // composition using andThen
// res8: Double = 2.0

func2(func1(1))          // composition written out by hand
// res9: Double = 2.0

(func2 compose func1)(1)


import scala.language.higherKinds
import cats.Functor // type class
import cats.instances.list._   // for Functor
import cats.instances.option._ // for Functor

val list1 = List(1, 2, 3)  // list1: List[Int] = List(1, 2, 3)

val list2 = Functor[List].map(list1)(_ * 2)  // list2: List[Int] = List(2, 4, 6)

val option1 = Option(123)  // option1: Option[Int] = Some(123)

val option2 = Functor[Option].map(option1)(_.toString)  // option2: Option[String] = Some(123)


val func = (x: Int) => x + 1
// func: Int => Int = <function1>

val liftedFunc = Functor[Option].lift(func)
// liftedFunc: Option[Int] => Option[Int] = cats.Functor$$Lambda$12952/101985314@120e192a

liftedFunc(Option(1))
// res0: Option[Int] = Some(2)


def doMath[F[_]](start: F[Int])
                (implicit functor: Functor[F]): F[Int] =
  start.map(n => n + 1 * 2)

doMath(Option(20))  // res3: Option[Int] = Some(22)

doMath(List(1, 2, 3))  // res4: List[Int] = List(3, 4, 5)


final case class Box[A](value: A)

implicit val boxFunctor: Functor[Box] = new Functor[Box] {
  override def map[A, B](fa: Box[A])(f: A => B) =
    Box(f(fa.value))
}

val box = Box[Int](123)
box.map(value => value + 1)


import scala.concurrent.{Future, ExecutionContext}

implicit def futureFunctor
(implicit ec: ExecutionContext): Functor[Future] =
  new Functor[Future] {
    def map[A, B](value: Future[A])(func: A => B): Future[B] =
      value.map(func)
  }


// And then to this:
val customExeuctionContext = ExecutionContext.Implicits.global
val newFutureFunctor =
  Functor[Future](futureFunctor(customExeuctionContext))

trait Printable[A] { self =>
  def format(value: A): String

  def contramap[B](func: B => A): Printable[B] =
    new Printable[B] {
      def format(value: B): String =
        self.format(func(value))
    }
}

def format[A](value: A)(implicit p: Printable[A]): String =
  p.format(value)


implicit val stringPrintable: Printable[String] =
  new Printable[String] {
    def format(value: String): String =
      "\"" + value + "\""
  }

implicit val booleanPrintable: Printable[Boolean] =
  new Printable[Boolean] {
    def format(value: Boolean): String =
      if(value) "yes" else "no"
  }

format("hello")
format(true)

implicit def boxPrintable[A](implicit p: Printable[A]) =
  p.contramap[Box[A]](_.value)

format(new Box[String]("a"))

trait Codec[A] {
  def encode(value: A): String
  def decode(value: String): A

  def imap[B](dec: A => B, enc: B => A): Codec[B] = {
    val self = this
    new Codec[B] {
      def encode(value: B): String =
        self.encode(enc(value))

      def decode(value: String): B =
        dec(self.decode(value))
    }
  }
}
def encode[A](value: A)(implicit c: Codec[A]): String =
  c.encode(value)

def decode[A](value: String)(implicit c: Codec[A]): A =
  c.decode(value)

implicit val stringCodec: Codec[String] =
  new Codec[String] {
    def encode(value: String): String = value
    def decode(value: String): String = value
  }

implicit val intCodec: Codec[Int] =
  stringCodec.imap(_.toInt, _.toString)

implicit val booleanCodec: Codec[Boolean] =
  stringCodec.imap(_.toBoolean, _.toString)

encode("string")
encode(10)
decode[String]("10")
decode[Int]("10")
decode[Boolean]("true")



