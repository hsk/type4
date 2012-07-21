package type4

object typing {

  def main(args:Array[String]) {

    val ast =List(
      ("_main",List(("void","void"),"void"),
       List(
          ("call","_printInt",List(("call","_add",List(1,2,3)))),
          ("call","_printFloat",List(("call","_addf",List(1.1f,0.1f,0.2f)))))),
      ("_add",List(("a","int"),("b","int"),("c","int"),"int"),
       List(("ret",("add",("add","a","b"),"c")))),
      ("_addf",List(("a","float"),("b","float"),("c","int"),"float"),
       List(("ret",("add",("add","a","b"),"c")))))
    val tast = typing(ast)
    println(pp(tast))
  }

  def apply(e:List[Any]):List[Any] = {

    e match {
      case List()=>List()
      case (n,l:List[Any],x:List[Any])::xs => (n,l,tl(l,x))::apply(xs)
    }
  }

  def t(env:List[Any],e:Any):Any = {
    e match {
      case ("add", a, b) => ic(t(env,a), t(env,b)){
        case (a,b,"float")=>(("addf", a, b), "float")
        case (a,b,"int")=>  (("addl", a, b), "int")
      }
      case ("call", a, b:List[Any]) => ("call", a, tl(env,b))
      case ("ret", a) => ("ret", t(env,a))
      case (a, "int") => (t(env, a),"int")
      case (a, "float") => (t(env, a),"float")
      case a:Int => (a,"int")
      case a:Float => (a,"float")
      case a:String => find(env, a)
      case a => a
    }
  }
  def find(env:List[Any], a:String):Any = {
    env match {
      case List() => "error"
      case List(a) => "error"
      case (ee@(n,_))::xs => if(a==n) ee else find(xs, a)
    }
  }
  def tl(env:List[Any],e:List[Any]):List[Any] = e match {
    case List() => List()
    case x::xs => t(env, x)::tl(env, xs)
  }
  def ic(a:Any, b:Any)(f:(Any)=>Any):Any = (a,b) match {
    case ((a,"float"),(b,"float")) => f(a,b,"float")
    case ((a,"float"),(b,_)) => f(a,("cast_float", b),"float")
    case ((a,_),(b,"float")) => f(("cast_float",a),b, "float")
    case ((a,_),(b,_))=> f(a,b,"int")
  }
}

// 型の情報を乗っけて返すか、否か。それが問題のようだ。
// で乗っけない方針で行きたいみたいだ。
// となると今の形は避けたいと。
