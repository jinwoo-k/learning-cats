import cats.data.State

val a = State[Int, String] { state =>
  (state, s"The state is $state")
}
// a: cats.data.State[Int,String] = cats.data.IndexedStateT@70142af6

val (state, result) = a.run(10).value
val state = a.runS(10).value
val result = a.runA(10).value

val step1 = State[Int, String] { num =>
  val ans = num + 1
  (ans, s"Result of step1: $ans")
}
// step1: cats.data.State[Int,String] = cats.data.IndexedStateT@376a962c

val step2 = State[Int, String] { num =>
  val ans = num * 2
  (ans, s"Result of step2: $ans")
}
// step2: cats.data.State[Int,String] = cats.data.IndexedStateT@6be37458

val both = for {
  a <- step1
  b <- step2
} yield (a, b)
// both: cats.data.IndexedStateT[cats.Eval,Int,Int,(String, String)] = cats.data.IndexedStateT@250c2c22

val (state, result) = both.run(20).value
// state: Int = 42
// result: (String, String) = (Result of step1: 21,Result of step2: 42)



val getDemo = State.get[Int]
// getDemo: cats.data.State[Int,Int] = cats.data.IndexedStateT@280446c5

getDemo.run(10).value
// res3: (Int, Int) = (10,10)

val setDemo = State.set[Int](30)
// setDemo: cats.data.State[Int,Unit] = cats.data.IndexedStateT@678380eb

setDemo.run(10).value
// res4: (Int, Unit) = (30,())

val pureDemo = State.pure[Int, String]("Result")
// pureDemo: cats.data.State[Int,String] = cats.data.IndexedStateT@2364f0fb

pureDemo.run(10).value
// res5: (Int, String) = (10,Result)

val inspectDemo = State.inspect[Int, String](_ + "!")
// inspectDemo: cats.data.State[Int,String] = cats.data.IndexedStateT@3502f4f3

inspectDemo.run(10).value
// res6: (Int, String) = (10,10!)

val modifyDemo = State.modify[Int](_ + 1)
// modifyDemo: cats.data.State[Int,Unit] = cats.data.IndexedStateT@6acdb6ef

modifyDemo.run(10).value
// res7: (Int, Unit) = (11,())val getDemo = State.get[Int]



import State._

val program: State[Int, (Int, Int, Int, Int)] = for {
  a <- get[Int]           // (a, a)
  _ <- set[Int](a + 1)    // (a + 1, Unit)
  b <- get[Int]           // (a + 1, a + 1)
  _ <- modify[Int](_ + 1) // (a + 1 + 1, Unit)
  c <- inspect[Int, Int](_ * 1000)  // (a + 1 + 1, (a + 1 + 1) * 1000)
  d <- get[Int]           // (a + 1 + 1, a + 1 + 1)
} yield (a, b, c, d)
// program: cats.data.State[Int,(Int, Int, Int)] = cats.data.IndexedStateT@3b51107e

val (state, result) = program.run(1).value
// state: Int = 3



import cats.syntax.applicative._
import cats.data.State

type CalcState[A] = State[List[Int], A]

def calc(n1: Int, n2: Int, op: String): Int = op match {
  case "+" => n1 + n2
  case "-" => n1 - n2
  case "*" => n1 * n2
  case "/" => n1 / n2
  case _ => 0
}

def evalOne(sym: String): CalcState[Int] = State[List[Int], Int] { oldStack =>
  if (sym.matches("^\\d+$")) {
    val num = sym.toInt
    (num :: oldStack, num)
  } else {
    val n1 :: n2 :: remain = oldStack
    val res = calc(n1, n2, sym)
    (res :: remain, res)
  }
}

def evalAll(input: List[String]): CalcState[Int] = {
  input.foldLeft(0.pure[CalcState]) { (calcState, str) =>
    calcState.flatMap(_ => evalOne(str))
  }
}

evalOne("42").runA(Nil).value
// res3: Int = 42

val program = evalAll(List("1", "2", "+", "3", "*"))
// program: CalcState[Int] = cats.data.IndexedStateT@2e788ab0

program.runA(Nil).value
// res6: Int = 9


val program = for {
  _   <- evalAll(List("1", "2", "+"))
  _   <- evalAll(List("3", "4", "+"))
  ans <- evalOne("*")
} yield ans
// program: cats.data.IndexedStateT[cats.Eval,List[Int],List[Int],Int] = cats.data.IndexedStateT@55072a57

program.runA(Nil).value
// res7: Int = 21

def evalInput(input: String): Int =
  evalAll(input.split(" ").toList).runA(Nil).value

evalInput("1 2 + 3 4 + *")
// res8: Int = 21
