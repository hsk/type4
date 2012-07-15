package type4

object emit {

  def main(argv:Array[String]) {
    emit("emit.s", List(
      ("_main",List(
        ("movl", "$1", "%edi"),
        ("call", "_printInt",List())
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
        asm(".globl "+name)
        asm(name+":")
        asm("\tpushq\t%rbp")
        asm("\tmovq\t%rsp, %rbp")
        body.foreach {
          case ("movl",a,b) => asm("movl "+a+", "+b)
          case ("subq",a,b) => asm("subq "+a+", "+b)
          case ("addl",a,b,c) =>
            asm("movl "+a+", %eax")
            asm("addl "+b+", %eax")
            asm("movl %eax, "+c)
          case ("call", n, b:List[Any]) => prms(b, regs); asm("call "+n)
          case ("ret", a) =>
            asm("movl "+a+", %eax")
            asm("leave")
            asm("ret")
        }
        asm("\tleave")
        asm("\tret")
    }
    asm.close()
  }
  
  val regs = List("%edi","%esi", "%edx")
  def prms(ps:List[Any],rs:List[Any]) {
    (ps,rs) match {
      case (List(),_) =>
      case (p::ps,r::rs) =>
        asm("movl "+p+", "+r)
        prms(ps, rs)
      case _ =>
    }
  }
}
