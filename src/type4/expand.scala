package type4

object expand {

  def main(argv:Array[String]) {
    val s = 	List(
		("_aaa",List(("aa",("Ptr",List("int"))),"void"),List(
			("var",100,("s_1","int")),
			("call","_printInt",List(("ref","aa","s_1"))))),
		("_main",List(("void","void"),"void"),List(
			("var",100,("s_4","int")),
			("var",5,("s_3","int")),
			("var",101,("s_2","int")),
			("var",("call","_malloc",List("s_2")),("a",("Ptr",List("Int")))),
			("mov","s_3",("ref","a","s_4")),
			("call","_aaa",List("a")))))
    val e = expand(s)
    println("p="+pp(e))
    val m = memAlloc(e)
    println("m="+pp(m))
    emit("e.s", m)
    exec("gcc -m64 -o e e.s src/lib.c") match {
      case 0 => exec("./e")
      case _ =>
    }
  }
  var prgs = List[Any]()
  def get_ret_type(id:String,prgs:List[Any]):String = {
    def rc_type(a:List[Any]):String = a match {
      case List() => throw new Exception("error")
      case List(x) => x.asInstanceOf[String]
      case x::xs => rc_type(xs)
    }
    prgs match {
      case List() => throw new Exception("not found function "+id);
      case (n,a:List[Any],b)::xs => if(id==n) rc_type(a) else get_ret_type(id, xs)
    }
  }

  def apply(p:List[Any]):List[Any] = {
    prgs = p:::List(
      ("_printInt",List[Any]("int"),List()),
      ("_printFloat",List[Any]("float"),List()),
      ("_malloc",List[Any]("long"),List())
    )
    p.map {
      case (n,a:List[Any],b:List[Any]) =>
        val ll = b.foldLeft(argv(a, regs, xregs)){
          case (l,b)=>
            val (l2, id) = f(l,b)
            l2
        }
        (n,ll.reverse)
    }
  }

  def argv(as:List[Any], rs:List[Any], xrs:List[Any]):List[Any] = {
    (as, rs, xrs) match {
      case (List(), rs, xrs) => List()
      case (a::List(), rs,xrs) => List()
      case (a@(id,"void")::as, rs, xrs) => argv(as, rs, xrs)
      case ((a@(id,"int"))::as, r::rs, xrs) => ("var", r, a)::argv(as, rs, xrs)
      case ((a@(id,"float"))::as, rs, r::xrs) => ("var", r, a)::argv(as, rs, xrs)
      case ((a@(id,("Ptr",_)))::as, r::rs, xrs) => ("var", r, a)::argv(as, rs, xrs)
    }
  }

  val regs = List("%edi", "%esi", "%edx", "%ecx", "%r8d", "%r9d")
  val xregs = List("%xmm0", "%xmm1", "%xmm2", "%xmm3", "%xmm4", "%xmm5", "%xmm6", "%xmm7")
  def f(l:List[Any],e:Any):(List[Any],String) = e match {
    case ("add", a, b) =>
      val id = genid("ex_")
      val (la, a1) = f(l, a)
      val (lb, b1) = f(la, b)
      (("addl", a1, b1, id)::("var", "", (id, "int"))::lb,id)
    case ("ref", a, b) =>
      println("ref ***"+a+","+b)
      val id = genid("ex_")
      val (la, a1) = f(l, a)
      val (lb, b1) = f(la, b)
      (
        ("movl", ("ref", a1, id), id)::
        ("mull", id, "$4", id)::
        ("movl", b1, id)::
        ("var", "", (id, "int"))::lb,id)
    case ("addf", a, b) =>
      val id = genid("ex_")
      val (la, a1) = f(l, a)
      val (lb, b1) = f(la, b)
      (("addf", a1, b1, id)::("var", "", (id, "float"))::lb,id)
    case ("mov", a:String, id:String) => (("movl", a, id)::l, id)
    case ("var", a:Int, (id:String,t:String)) => (("var", ("$"+a),(id,t))::l, id)
    case ("var", a:Float, (id:String,t:String)) => (("var", a,(id,t))::l, id)
    case ("var", a, (id:String,t)) =>
      val (la, a1) = f(l, a)
      (("var", a1,(id,t))::la, id)
    case ("mov", a, id:String) =>
      val (l2, id1) = f(l, a)
      (("movl", id1, id)::l2, id)
    case ("mov", a, ("ref",id1:String,id2:String)) =>
      val id = genid("ex_");
      val (l2, id3) = f(l, a)
      // id1とid2のキャストが
      (("movl",id3, ("ref", id))::
       ("addq", id1, id, id)::
       ("mulq",id2, "$4", id)::
       ("var", "", (id, "long"))::l2,id3)
    case ("call", a:String, b:List[Any]) =>
      var (la,ids) = b.foldLeft((l,List[String]())){
        case ((l,ids),b)=>
          val (l2,id) = f(l, b)
          (l2,id::ids)
      }
      get_ret_type(a, prgs) match {
        case "int" => (("call", a, ids)::la, "%eax")
        case "float" => (("call", a, ids)::la, "%xmm0")
        case "long" => (("call", a, ids)::la, "%rax")
        case "void" => (("call", a, ids)::la, "%eax")
        case t => throw new Exception("error call type "+a+" "+t)
      }
    case ("ret", e) =>
      val (l2, id) = f(l, e)
      (("ret", id)::l2, id)
    case id:String => (l, id)
    case e => (e::l, null)
  }

}
