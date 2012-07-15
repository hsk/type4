package type4

object memAlloc {
  var m:Map[String,String] = null
  def apply(ls:List[Any]):List[Any] = ls.map {
    case (n:String,ls:List[Any])=>
      counter = 0
      m = Map()
      val ll = ls.map(g)
      val size = ((15-counter)/16)*16
      (n,("subq","$"+size,"%rsp")::ll)
  }
 
  def g(l:Any):Any = l match {
    case ("movl", a, b) => ("movl", adr(a), adr(b))
    case ("addl", a, b, c) => ("addl", adr(a), adr(b),adr(c))
    case ("call", a, b:List[Any]) => ("call", a, b.map(adr))
    case ("ret", a) => ("ret", adr(a))
  }

  var counter = 0 
  def adr(a:Any):Any = a match {
    case a:String if(m.contains(a))=> m(a)
    case a:String if(a.substring(0,1)=="%" || a.substring(0,1)=="$") => a 
    case a:String => counter -= 4; val n = counter + "(%rbp)"; m = m + (a -> n); n
    case a => a
  }

  def main(argv:Array[String]) {
    val prgs = List(
      ("_main",List(
        ("movl", "$1", "a"),
        ("call", "_printInt",List("a"))
      ))
    )
    val l = memAlloc(prgs)
    println("l="+l)
    emit("m.s",l)
    exec("gcc -m64 -o m m.s src/lib.c") match {
      case 0 => exec("./m")
      case _ =>
    }
    
  }

}

