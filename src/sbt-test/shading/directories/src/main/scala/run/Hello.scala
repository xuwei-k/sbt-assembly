package run

object Hello {
  def main(args: Array[String]): Unit = {
    val name = (new somepackage.SomeClass).getClass.getName
    println("Hello " + name)
  }
}
