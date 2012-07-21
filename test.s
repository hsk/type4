	.text
.globl _aaa
_aaa:
LFB2:
	pushq	%rbp
LCFI0:
	movq	%rsp, %rbp
LCFI1:
	subq	$32, %rsp
LCFI2:
	movq	%rdi, -24(%rbp)
	movq	-24(%rbp), %rax
	addq	$400, %rax
	movl	(%rax), %eax
	movl	%eax, -4(%rbp)
	movl	-4(%rbp), %edi
	movl	$0, %eax
	call	_printInt
	leave
	ret
LFE2:
.globl _main
_main:
LFB3:
	pushq	%rbp
LCFI3:
	movq	%rsp, %rbp
LCFI4:
	subq	$16, %rsp
LCFI5:
	movl	$100, %edi
	call	_malloc
	movq	%rax, -8(%rbp)
	movq	-8(%rbp), %rax
	addq	$400, %rax
	movl	$5, (%rax)
	movq	-8(%rbp), %rdi
	call	_aaa
	movl	$0, %eax
	leave
	ret
LFE3:
	.section __TEXT,__eh_frame,coalesced,no_toc+strip_static_syms+live_support
EH_frame1:
	.set L$set$0,LECIE1-LSCIE1
	.long L$set$0
LSCIE1:
	.long	0x0
	.byte	0x1
	.ascii "zR\0"
	.byte	0x1
	.byte	0x78
	.byte	0x10
	.byte	0x1
	.byte	0x10
	.byte	0xc
	.byte	0x7
	.byte	0x8
	.byte	0x90
	.byte	0x1
	.align 3
LECIE1:
.globl _aaa.eh
_aaa.eh:
LSFDE1:
	.set L$set$1,LEFDE1-LASFDE1
	.long L$set$1
LASFDE1:
	.long	LASFDE1-EH_frame1
	.quad	LFB2-.
	.set L$set$2,LFE2-LFB2
	.quad L$set$2
	.byte	0x0
	.byte	0x4
	.set L$set$3,LCFI0-LFB2
	.long L$set$3
	.byte	0xe
	.byte	0x10
	.byte	0x86
	.byte	0x2
	.byte	0x4
	.set L$set$4,LCFI1-LCFI0
	.long L$set$4
	.byte	0xd
	.byte	0x6
	.align 3
LEFDE1:
.globl _main.eh
_main.eh:
LSFDE3:
	.set L$set$5,LEFDE3-LASFDE3
	.long L$set$5
LASFDE3:
	.long	LASFDE3-EH_frame1
	.quad	LFB3-.
	.set L$set$6,LFE3-LFB3
	.quad L$set$6
	.byte	0x0
	.byte	0x4
	.set L$set$7,LCFI3-LFB3
	.long L$set$7
	.byte	0xe
	.byte	0x10
	.byte	0x86
	.byte	0x2
	.byte	0x4
	.set L$set$8,LCFI4-LCFI3
	.long L$set$8
	.byte	0xd
	.byte	0x6
	.align 3
LEFDE3:
	.subsections_via_symbols
