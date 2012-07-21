.globl _main
_main:
	pushq	%rbp
	movq	%rsp, %rbp
movl $1, %edi
call _printInt
movss literal1(%rip), %xmm0
movss %xmm0,%xmm0
call _printFloat
	leave
	ret
.literal4
.align 2
literal1:
.long 1066192077
.align 3
