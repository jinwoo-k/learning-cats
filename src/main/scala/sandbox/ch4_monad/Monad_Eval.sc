import cats.Eval

val x = Eval.now {
  println("Computing X")
  math.random
}
// Computing X
// x: cats.Eval[Double] = Now(0.8724950064732552)

x.value // first access
// res9: Double = 0.8724950064732552

x.value // second access
// res10: Double = 0.8724950064732552

val y = Eval.always {
  println("Computing Y")
  math.random
}
// y: cats.Eval[Double] = cats.Always@5212e1f5

y.value // first access
// Computing Y
// res11: Double = 0.8795680260041828

y.value // second access
// Computing Y
// res12: Double = 0.5640213059400854


val z = Eval.later {
  println("Computing Z")
  math.random
}
// z: cats.Eval[Double] = cats.Later@33eda11

z.value // first access
// Computing Z
// res13: Double = 0.5813583535421343

z.value // second access
// res14: Double = 0.5813583535421343



val greeting = Eval.
  now { println("Step 1"); "Hello" }.
  map { str => println("Step 2"); s"$str world" }
// greeting: cats.Eval[String] = cats.Eval$$anon$8@3a67c76e

greeting.value
// Step 1
// Step 2
// res15: String = Hello world


val ans = for {
  a <- Eval.always { println("Calculating A"); 40 }
  b <- Eval.now { println("Calculating B"); 2 }
} yield {
  println("Adding A and B")
  a + b
}
// Calculating A
// ans: cats.Eval[Int] = cats.Eval$$anon$8@2d96144d

ans.value // first access
// Calculating B
// Adding A and B
// res16: Int = 42

ans.value // second access
// Calculating B
// Adding A and B
// res17: Int = 42


val saying = Eval.
  always { println("Step 1"); "The cat" }.
  map { str => println("Step 2"); s"$str sat on" }.
  memoize.
  map { str => println("Step 3"); s"$str the mat" }
// saying: cats.Eval[String] = cats.Eval$$anon$8@7a0389b5

saying.value // first access
// Step 1
// Step 2
// Step 3
// res18: String = The cat sat on the mat

saying.value // second access
// Step 3
// res19: String = The cat sat on the mat


def factorial(n: BigInt): Eval[BigInt] =
  if(n == 1) {
    Eval.now(n)
  } else {
    Eval.defer(factorial(n - 1).map(_ * n))
  }

factorial(50000).value


def foldRightEval[A, B]
(as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] =
  as match {
    case head :: tail =>
      Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
    case Nil =>
      acc
  }

def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
  foldRightEval(as, Eval.now(acc)) { (a, b) =>
    b.map(fn(a, _))
  }.value

foldRight((1 to 100000).toList, 0L)(_ + _)
// res22: Long = 5000050000