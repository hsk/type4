package type4

object st2ast {
  def main(argv:Array[String]) {
    val st=(((("main",":",(("(","void",")"),"(","void",")")),"=",("{",((("printInt","(",("add","(",((1,",",2),",",3),")"),")"),";"),"@",("printFloat","(",("addf","(",((1.1f,",",0.1f),",",0.2f),")"),")")),"}")),"@",(("add",":",(("(",((("a",":","int"),",",("b",":","int")),",",("c",":","int")),")"),"(","int",")")),"=",("{",("return",(("a","+","b"),"+","c")),"}"))),"@",(("addf",":",(("(",((("a",":","float"),",",("b",":","float")),",",("c",":","float")),")"),"(","float",")")),"=",("{",("return",(("a",".+","b"),".+","c")),"}")))

    val ast = st2ast(st)
    println("ast="+pp(ast))
    val s = setmem(ast)
    val e = expand(s)
    val m = memAlloc(e)
    emit("e.s", m)
    exec("gcc -m64 -o e e.s src/lib.c") match {
      case 0 => exec("./e")
      case _ =>
    }
  }

  def apply(st:Any):List[Any] = st match {
    case (a,"@",b) => apply(a):::apply(b)
    case a => List(f(a))
  }

  def f(fn:Any):Any = fn match {
    case ((n,":",(("(",a,")"),"(",t,")")),"=",b) => ("_"+n, params(a):::List(t), bodys(b))
  }

  def params(e:Any):List[Any] = e match {
    case (a,",",b) => params(a):::params(b)
    case (a,":",b) => List(("_"+a,typ(b)))
    case "void" => List(("void","void"))
  }
  def typ(e:Any):Any = e match {
    case (b,"[",a,"]") => (b, typs(a))
    case a => a
  }
  def typs(e:Any):List[Any] = e match {
    case (a,",",b) => typs(a):::typs(b)
    case a => List(a)
  }
  def fargs(e:Any):List[Any] = e match {
    case (a,",",b) => fargs(a):::fargs(b)
    case a => List(exp(a))
  }

  def exp(e:Any):Any = e match {
    case ("{",b,"}") => bodys(b)
    case ("(",b,")") => exp(b)
    case ((a,":",b),"=",c) => ("var",exp(c),("_"+a,typ(b)))
    case (a,"[",b,"]") => ("ref", exp(a), exp(b))
    case (a,"(",b,")") => ("call","_"+a,fargs(b))
    case (a,"=",b) => ("mov", exp(b), exp(a))
    case (a,"+",b) => ("add",exp(a), exp(b))
    case (a,".+",b) => ("addf",exp(a), exp(b))
    case ("return", a) => ("ret", exp(a))
    case (a,";") => exp(a)
    case a:Int => a
    case a:String => "_"+a
    case a:Float => a
  }
  def bodys(e:Any):List[Any] = e match {
    case (a,"@",b) => bodys(a):::bodys(b)
    case a =>
      exp(a) match {
        case e:List[Any] => e
        case a => List(a)
      }
  }
}
