package type4

import java.lang.Float.floatToIntBits

object emit {

  def main(argv:Array[String]) {
    emit("e.s", List(
        ("_main", List(
            ("subq","$32","%rsp"),
            ("movf",0.2f,("-4(%rbp)","float")),
            ("movf",0.1f,("-8(%rbp)","float")),
            ("movf",1.1f,("-12(%rbp)","float")),
            ("movl","$3",("-16(%rbp)","int")),
            ("movl","$2",("-20(%rbp)","int")),
            ("movl","$1",("-24(%rbp)","int")),
            ("call","_add",List(("-16(%rbp)","int"), ("-20(%rbp)","int"), ("-24(%rbp)","int"))),
            ("call","_printInt",List("%eax")),
            ("call","_addf",List(("-4(%rbp)","float"),("-8(%rbp)","float"),("-12(%rbp)","float"))),
            ("call","_printFloat",List("%xmm0")))),
        ("_add", List(
            ("subq","$32","%rsp"),
            ("movl","%edx",("-4(%rbp)","int")),
            ("movl","%esi",("-8(%rbp)","int")),
            ("movl","%edi",("-12(%rbp)","int")),
            (),
            ("addl",("-12(%rbp)","int"),("-8(%rbp)","int"),("-16(%rbp)","int")),
            (),
            ("addl",("-16(%rbp)","int"),("-4(%rbp)","int"),("-20(%rbp)","int")),
            ("ret",("-20(%rbp)","int"))
          )),
        ("_addf", List(
            ("subq","$32","%rsp"),
            ("movf","%xmm2",("-4(%rbp)","float")),
            ("movf","%xmm1",("-8(%rbp)","float")),
            ("movf","%xmm0",("-12(%rbp)","float")),
            (),
            ("addf",("-12(%rbp)","float"),("-8(%rbp)","float"),("-16(%rbp)","float")),
            (),
            ("addf",("-16(%rbp)","float"),("-4(%rbp)","float"),("-20(%rbp)","float")),
            ("ret",("-20(%rbp)","float"))))))
    exec("gcc -m64 -o e e.s src/lib.c") match {
      case 0 => exec("./e")
      case _ =>
    }
  }

  def apply(filename:String, ls:List[Any]) {
    asm.open(filename)
    ls.foreach {
      case (name:String,body:List[Any]) =>
        literals = List[Any]()
        asm(".globl "+name)
        asm(name+":")
        asm("\tpushq\t%rbp")
        asm("\tmovq\t%rsp, %rbp")
        body.foreach {
          case ("movl",a,b) => asm("movl "+d(a)+", "+d(b))
          case ("movf",a:Float,b) => asm("movss "+d(a)+", %xmm0"); asm("movss %xmm0,"+d(b))
          case ("movf",a,b) => asm("movss "+d(a)+", "+d(b))
          case ("subq",a,b) => asm("subq "+d(a)+", "+d(b))
          case ("addl",a,b,c) =>
            asm("movl "+d(a)+", %eax")
            asm("addl "+d(b)+", %eax")
            asm("movl %eax, "+d(c))
          case ("addf",a,b,c) =>
            asm("movss "+d(a)+", %xmm0")
            asm("addss "+d(b)+", %xmm0")
            asm("movss %xmm0, "+d(c))
          case ("call", n, b:List[Any]) => prms(b, regs,xregs); asm("call "+n)
          case ("ret", a) =>
            asm("movl "+d(a)+", %eax")
            asm("leave")
            asm("ret")
          case () =>
        }
        asm("\tleave")
        asm("\tret")
        literals.foreach {
          case (l,a,"float")=> asm(".literal4");asm(".align 2"); asm(l+":"); asm(".long "+floatToIntBits(a.asInstanceOf[Float]))
        }
        asm(".align 3")
    }
    asm.close()
  }
  var counter = 0
  var literals = List[Any]()

  def d(a:Any):Any = {
    a match {
      case a:Float => counter+=1; val l = "literal"+counter; literals = (l,a,"float")::literals; l+"(%rip)"
      case (a,_) => a
      case a => a
    }
  }
  val regs = List("%edi", "%esi", "%edx")
  val xregs = List("%xmm0", "%xmm1", "%xmm2")
  def prms(ps:List[Any],rs:List[Any], xrs:List[Any]) {
    (ps,rs,xrs) match {
      case (List(),_,_) =>
      case ((p,"int")::ps,r::rs, xrs) =>
        asm("movl "+p+", "+d(r))
        prms(ps, rs, xrs)
      case ((p,"float")::ps,rs, r::xrs) =>
        asm("movss "+d(p)+", "+d(r))
        prms(ps, rs, xrs)
      case _ =>
    }
  }
}
