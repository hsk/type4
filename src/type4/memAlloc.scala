package type4

object memAlloc {
  var m:Map[String,(String,String)] = null
  def apply(ls:List[Any]):List[Any] = ls.map {
    case (n:String,ls:List[Any])=>
      counter = 0
      m = Map()
      val ll = ls.map(g)
      val size = ((15-counter)/16)*16
      (n,("subq","$"+size,"%rsp")::ll)
  }
  def g(l:Any):Any = l match {
    case ("var", a, ("void", "void")) =>
    case ("var",  a, (b:String, ("Ptr",_))) => counter -= 8; val n = counter+"(%rbp)"; m = m + (b -> (n,"long"));   if(a!="")("movq", adr(a), adr(b))
    case ("var",  a, (b:String, "int")  ) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"int"));   if(a!="")("movl", adr(a), adr(b))
    case ("var",  a, (b:String, "float")) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"float")); if(a!="")("movf", adr(a), adr(b))
    case ("var",  a, (b:String, "long")) => counter -= 8; val n = counter+"(%rbp)"; m = m + (b -> (n,"long")); if(a!="")("movq", adr(a), adr(b))
    case ("movl", a, b) => ("movl", adr(a), adr(b))
    case ("ref",  a, b, c) => ("ref", adr(a), adr(b), adr(c))
    case ("addl", a, b, c) => ("addl", adr(a), adr(b),adr(c))
    case ("addq", a, b, c) => ("addq", adr(a), adr(b),adr(c))
    case ("mull", a, b, c) => ("mull", adr(a), adr(b),adr(c))
    case ("mulq", a, b, c) => ("mulq", adr(a), adr(b),adr(c))
    case ("addf", a, b, c) => ("addf", adr(a), adr(b),adr(c))
    case ("call", a, b:List[Any]) => ("call", a, b.map(adr))
    case ("ret", a) => ("ret", adr(a))
  }

  var counter = 0
  def adr(a:Any):Any = a match {
    case a:String if(m.contains(a))=> m(a)
    case a:String if(a.substring(0,1)=="%" || a.substring(0,1)=="$") => a
    case a:Float => a
    case a:String => counter -= 4; val n = counter + "(%rbp)"; m = m + (a -> (n,"int")); n
    case ("ref",a) => ("ref", adr(a))
    case ("ref",a,b) => ("ref", adr(a), adr(b))
    case a => a
  }

  def main(argv:Array[String]) {
    val e = List(
      ("_aaa",List(
          ("var","%edi",("aa",("Ptr",List("int")))),
          ("var","$100",("s_1","int")),
          ("var","",("ex_1","int")),
          ("ref","aa","s_1","ex_1"),
          ("call","_printInt",List("ex_1")))),
      ("_main",List(
          ("var","$100",("s_4","int")),
          ("var","$5",("s_3","int")),
          ("var","$101",("s_2","int")),
          ("var",("call","_malloc",List("s_2")),("a",("Ptr",List("Int")))),
          ("mov","s_3",("ref","a","s_4")),
          ("call","_aaa",List("a")))))



    val l = memAlloc(e)
    emit("e.s",l)
    exec("gcc -m64 -o e e.s src/lib.c") match {
      case 0 => exec("./e")
      case _ =>
    }

  }

}
