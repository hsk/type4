package type4
object setmem {
  var ls:List[Any] = List()

  def apply(e:List[Any]):List[Any] = e.map {
    case (n:String, a:List[String], b:List[Any]) =>
      ls = List()
      val b2 = b.map(f)
      (n,a,ls:::b2)
  }
  def f(e:Any):Any = e match {
    case ("mov", a, b) => ("mov", f(a), f(b))
    case ("ret", a) => ("ret", a)
    case ("add", a, b) => ("add", f(a), f(b))
    case ("call", a, b:List[Any]) => ("call", a, b.map(f))
    case a:Int =>
      val id = genid("s_")
      ls = ("var",a,(id,"int"))::ls
      id
   case a:Float =>
      val id = genid("s_")
      ls = ("var",a,(id,"float"))::ls
      id
    case a => a
  }

  def main(argv:Array[String]) {
    val prg = List(
      ("_main",List(("void","void"), "void"),List(
        ("call","_printInt",List(("call","_add",List(1, 2, 3))))
      )),
      ("_add",List(("a","int"), ("b","int"), ("c","int"), "int"),List(
        ("ret",("add",("add","a","b"),"c"))
      )))

    val s = setmem(prg)
    println("s="+s)
    val e = expand(s)
    val m = memAlloc(e)
    emit("m.s", m)
    exec("gcc -m64 -o m m.s src/lib.c") match {
      case 0 => exec("./m")
      case _ =>
    }
  }
}
