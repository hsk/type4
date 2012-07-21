.globl _main
_main:
	pushq	%rbp
	movq	%rsp, %rbp
subq $32, %rsp
movss literal1(%rip), %xmm0
movss %xmm0,-4(%rbp)
movss literal2(%rip), %xmm0
movss %xmm0,-8(%rbp)
movss literal3(%rip), %xmm0
movss %xmm0,-12(%rbp)
movl $3, -16(%rbp)
movl $2, -20(%rbp)
movl $1, -24(%rbp)
movl -16(%rbp), %edi
movl -20(%rbp), %esi
movl -24(%rbp), %edx
call _add
call _printInt
movss -4(%rbp), %xmm0
movss -8(%rbp), %xmm1
movss -12(%rbp), %xmm2
call _addf
call _printFloat
	leave
	ret
.literal4
.align 2
literal3:
.long 1066192077
.literal4
.align 2
literal2:
.long 1066192077
.literal4
.align 2
literal1:
.long 1092825907
.align 3
.globl _add
_add:
	pushq	%rbp
	movq	%rsp, %rbp
subq $32, %rsp
movl %edx, -4(%rbp)
movl %esi, -8(%rbp)
movl %edi, -12(%rbp)
movl -12(%rbp), %eax
addl -8(%rbp), %eax
movl %eax, -16(%rbp)
movl -16(%rbp), %eax
addl -4(%rbp), %eax
movl %eax, -20(%rbp)
movl -20(%rbp), %eax
leave
ret
	leave
	ret
.align 3
.globl _addf
_addf:
	pushq	%rbp
	movq	%rsp, %rbp
subq $32, %rsp
movss %xmm2, -4(%rbp)
movss %xmm1, -8(%rbp)
movss %xmm0, -12(%rbp)
movss -12(%rbp), %xmm0
addss -8(%rbp), %xmm0
movss %xmm0, -16(%rbp)
movss -16(%rbp), %xmm0
addss -4(%rbp), %xmm0
movss %xmm0, -20(%rbp)
movl -20(%rbp), %eax
leave
ret
	leave
	ret
.align 3
