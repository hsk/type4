package type4

import java.lang.Float.floatToIntBits

object emit {

  def main(argv:Array[String]) {
    emit("emit.s", List(
      ("_main",List(
        ("movl", "$1", "%edi"),
        ("call", "_printInt",List()),
        ("movf", 1.1f, "%xmm0"),
        ("call", "_printFloat",List())
      ))
    ))
    exec("gcc -m64 -o emit emit.s src/lib.c") match {
      case 0 => exec("./emit")
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
