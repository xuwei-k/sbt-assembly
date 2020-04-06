package to.be.shaded

import to.be.shaded.anobject.MyTrait

class Parametrized[T](t: T) {
  type MyType = T

  def myMethod = t.toString
}

object Parametrized {
  val inst = new Parametrized[anobject.MyTrait](
    new MyTrait {
      override def toString: String = "test_parametrized_class"
}
  )
}
