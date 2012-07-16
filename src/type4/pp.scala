package type4

object pp {
  def main(argv:Array[String]) {
    
  }
  def apply(e:Any):String = {
    e match {
      case a:Int => a+""
      case a:Float => a + "f"
      case a:String => "\"" + escape(a) + "\""
      case a:List[Any] => "List("+join(a)+")"
      case (a,b) => "("+pp(a)+","+pp(b)+")"
      case (a,b,c) => "("+pp(a)+","+pp(b)+","+pp(c)+")"
      case (a,b,c,d) => "("+pp(a)+","+pp(b)+","+pp(c)+","+pp(d)+")"
      case (a,b,c,d,e) => "("+pp(a)+","+pp(b)+","+pp(c)+","+pp(d)+","+pp(e)+")"
      case () => "()"
      case a => println("error [ " + a + " ]"); "[ERROR]"
    }
  }
  def escape(s:String):String = {
    s.replaceAll("\"", "\\\"")
     .replaceAll("\n","\\n")
     .replaceAll("\t","\\t")
     .replaceAll("\\\\","\\\\")
  }
  def join(l:List[Any]):String = {
    l match {
      case List() => ""
      case List(x) => pp(x)
      case x::xs => pp(x)+","+join(xs)
    }
  }
}