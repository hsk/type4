.globl _aaa
_aaa:
	pushq	%rbp
	movq	%rsp, %rbp
subq $16, %rsp
movq %rdi, -8(%rbp)
movl $100, -12(%rbp)
movl -12(%rbp), %eax
movl %eax,-16(%rbp)
movl -16(%rbp), %eax
movl $4, %edx
imull %edx, %eax
movl %eax, -16(%rbp)
movq -8(%rbp), %rax
movl -16(%rbp), %ecx
addq %rcx, %rax
movl (%rax), %eax
movl %eax,-16(%rbp)
movl -16(%rbp), %edi
call _printInt
	leave
	ret
.align 3
.globl _main
_main:
	pushq	%rbp
	movq	%rsp, %rbp
subq $32, %rsp
movl $100, -4(%rbp)
movl $5, -8(%rbp)
movl $101, -12(%rbp)
movl -12(%rbp), %edi
call _malloc
movq %rax, -20(%rbp)
movl -4(%rbp), %eax
imulq $4, %rax
movq %rax, -28(%rbp)
movq -20(%rbp), %rax
addq -28(%rbp), %rax
movq %rax, -28(%rbp)
movl -8(%rbp), %ecx
movq -28(%rbp), %rax
movl %ecx, (%rax)
movq -20(%rbp), %rdi
call _aaa
	leave
	ret
.align 3
