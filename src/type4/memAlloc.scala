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
    case ("var", "", (b:String, "int")) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"int"));
    case ("var", a, (b:String, "int")) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"int")); ("movl", adr(a), adr(b))
    case ("var", "", (b:String, "float")) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"float"));
    case ("var", a, (b:String, "float")) => counter -= 4; val n = counter+"(%rbp)"; m = m + (b -> (n,"float")); ("movf", adr(a), adr(b))
    case ("movl", a, b) => ("movl", adr(a), adr(b))
    case ("addl", a, b, c) => ("addl", adr(a), adr(b),adr(c))
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
    case a => a
  }

  def main(argv:Array[String]) {
    val prgs = List(
    ("_main",List(
        ("var","$3",("s_3","int")),
        ("var","$2",("s_2","int")),
        ("var","$1",("s_1","int")),
        ("call","_add",List("s_3", "s_2", "s_1")),
        ("call","_printInt",List("%eax")))),
    ("_add",List(
        ("var","%edx",("c","int")),
        ("var","%esi",("b","int")),
        ("var","%edi",("a","int")),
        ("var","", ("ex_5","int")),
        ("addl","a","b","ex_5"),
        ("var","", ("ex_4","int")),
        ("addl","ex_5","c","ex_4"),
        ("ret","ex_4"))))

    val l = memAlloc(prgs)
    emit("m.s",l)
    exec("gcc -m64 -o m m.s src/lib.c") match {
      case 0 => exec("./m")
      case _ =>
    }

  }

}

