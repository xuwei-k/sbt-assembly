package scalasigannot

import fat.jar.Test
import shade.{MyType, Parametrized, anobject}

import scala.reflect.runtime.{universe => ru}

case object Main {

  def main(args: Array[String]): Unit = {

    val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
//    val packageMirror = runtimeMirror.staticModule("shade.package$")
//    val moduleMirror = runtimeMirror.reflectModule(packageMirror.asModule)
//    val instanceMirror = runtimeMirror.reflect(moduleMirror.instance)

    assert(new shade.Clazz().myMethod == "test_class")
    assert(shade.MyString == "test_packageobject1")
    assert(new Test().callPackageObject == "test_packageobject1")
    assert(shade.anobject.MyString == "test_object1")
    assert(new shade.anobject.MyTrait {}.MyString == "test_trait_in_object")
    assert(ru.typeOf[MyType[String]].toString == "shade.MyType[String]")
    assert(Parametrized.inst.myMethod == "test_parametrized_class")
    assert(ru.typeOf[Parametrized.inst.MyType].typeSymbol.fullName == "shade.anobject.MyTrait")
  }
}
