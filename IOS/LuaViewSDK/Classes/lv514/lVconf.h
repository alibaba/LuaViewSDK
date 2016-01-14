/*
** $Id: lVconf.h,v 1.82.1.7 2008/02/11 16:25:08 roberto Exp $
** Configuration file for [L u a]
** See Copyright Notice in lV.h
*/


#ifndef lconfig_h
#define lconfig_h

#include <limits.h>
#include <stddef.h>


/*
** ==================================================================
** Search for "@@" to find all configurable definitions.
** ===================================================================
*/


/*
@@ LV_ANSI controls the use of non-ansi features.
** CHANGE it (define it) if you want [L u a] to avoid the use of any
** non-ansi feature or library.
*/
#if defined(__STRICT_ANSI__)
#define LV_ANSI
#endif


#if !defined(LV_ANSI) && defined(_WIN32)
#define LV_WIN
#endif

#if defined(LV_USE_LINUX)
#define LV_USE_POSIX
#define LV_USE_DLOPEN		/* needs an extra library: -ldl */
#define LV_USE_READLINE	/* needs some extra libraries */
#endif

#if defined(LV_USE_MACOSX)
#define LV_USE_POSIX
#define LV_DL_DYLD		/* does not need extra library */
#endif



/*
@@ LV_USE_POSIX includes all functionallity listed as X/Open System
@* Interfaces Extension (XSI).
** CHANGE it (define it) if your system is XSI compatible.
*/
#if defined(LV_USE_POSIX)
#define LV_USE_MKSTEMP
#define LV_USE_ISATTY
#define LV_USE_POPEN
#define LV_USE_ULONGJMP
#endif


/*
@@ LV_PATH and LV_CPATH are the names of the environment variables that
@* [L u a] check to set its paths.
@@ LV_INIT is the name of the environment variable that [L u a]
@* checks for initialization code.
** CHANGE them if you want different names.
*/
#define LV_PATH        "LV_PATH"
#define LV_CPATH       "LV_CPATH"
#define LV_INIT	"LV_INIT"


/*
@@ LV_PATH_DEFAULT is the default path that [L u a] uses to look for
@* [L u a] libraries.
@@ LV_CPATH_DEFAULT is the default path that [L u a] uses to look for
@* C libraries.
** CHANGE them if your machine has a non-conventional directory
** hierarchy or if you want to install your libraries in
** non-conventional directories.
*/
#if defined(_WIN32)
/*
** In Windows, any exclamation mark ('!') in the path is replaced by the
** path of the directory of the executable file of the current process.
*/
#define LV_LDIR	"!\\lua\\"
#define LV_CDIR	"!\\"
#define LV_PATH_DEFAULT  \
		".\\?.lua;"  LV_LDIR"?.lua;"  LV_LDIR"?\\init.lua;" \
		             LV_CDIR"?.lua;"  LV_CDIR"?\\init.lua"
#define LV_CPATH_DEFAULT \
	".\\?.dll;"  LV_CDIR"?.dll;" LV_CDIR"loadall.dll"

#else
#define LV_ROOT	"/usr/local/"
#define LV_LDIR	LV_ROOT "share/lv/5.1/"
#define LV_CDIR	LV_ROOT "lib/lv/5.1/"
#define LV_PATH_DEFAULT  \
		"./?.lua;"  LV_LDIR"?.lua;"  LV_LDIR"?/init.lua;" \
		            LV_CDIR"?.lua;"  LV_CDIR"?/init.lua"
#define LV_CPATH_DEFAULT \
	"./?.so;"  LV_CDIR"?.so;" LV_CDIR"loadall.so"
#endif


/*
@@ LV_DIRSEP is the directory separator (for submodules).
** CHANGE it if your machine does not use "/" as the directory separator
** and is not Windows. (On Windows [L u a] automatically uses "\".)
*/
#if defined(_WIN32)
#define LV_DIRSEP	"\\"
#else
#define LV_DIRSEP	"/"
#endif


/*
@@ LV_PATHSEP is the character that separates templates in a path.
@@ LV_PATH_MARK is the string that marks the substitution points in a
@* template.
@@ LV_EXECDIR in a Windows path is replaced by the executable's
@* directory.
@@ LV_IGMARK is a mark to ignore all before it when bulding the
@* lvopen_ function name.
** CHANGE them if for some reason your system cannot use those
** characters. (E.g., if one of those characters is a common character
** in file/directory names.) Probably you do not need to change them.
*/
#define LV_PATHSEP	";"
#define LV_PATH_MARK	"?"
#define LV_EXECDIR	"!"
#define LV_IGMARK	"-"


/*
@@ LV_INTEGER is the integral type used by lv_pushinteger/lv_tointeger.
** CHANGE that if ptrdiff_t is not adequate on your machine. (On most
** machines, ptrdiff_t gives a good choice between int or long.)
*/
#define LV_INTEGER	ptrdiff_t


/*
@@ LV_API is a mark for all core API functions.
@@ LVLIB_API is a mark for all standard library functions.
** CHANGE them if you need to define those functions in some special way.
** For instance, if you want to create one Windows DLL with the core and
** the libraries, you may want to use the following definition (define
** LV_BUILD_AS_DLL to get it).
*/
#if defined(LV_BUILD_AS_DLL)

#if defined(LV_CORE) || defined(LV_LIB)
#define LV_API __declspec(dllexport)
#else
#define LV_API __declspec(dllimport)
#endif

#else

#define LV_API		extern

#endif

/* more often than not the libs go together with the core */
#define LVLIB_API	LV_API


/*
@@ LVI_FUNC is a mark for all extern functions that are not to be
@* exported to outside modules.
@@ LVI_DATA is a mark for all extern (const) variables that are not to
@* be exported to outside modules.
** CHANGE them if you need to mark them in some special way. Elf/gcc
** (versions 3.2 and later) mark them as "hidden" to optimize access
** when [L u a] is compiled as a shared library.
*/
#if defined(lvall_c)
#define LVI_FUNC	static
#define LVI_DATA	/* empty */

#elif defined(__GNUC__) && ((__GNUC__*100 + __GNUC_MINOR__) >= 302) && \
      defined(__ELF__)
#define LVI_FUNC	__attribute__((visibility("hidden"))) extern
#define LVI_DATA	LVI_FUNC

#else
#define LVI_FUNC	extern
#define LVI_DATA	extern
#endif



/*
@@ LV_QL describes how error messages quote program elements.
** CHANGE it if you want a different appearance.
*/
#define LV_QL(x)	"'" x "'"
#define LV_QS		LV_QL("%s")


/*
@@ LV_IDSIZE gives the maximum size for the description of the source
@* of a function in debug information.
** CHANGE it if you want a different size.
*/
#define LV_IDSIZE	60


/*
** {==================================================================
** Stand-alone configuration
** ===================================================================
*/

#if defined(lv_c) || defined(lvall_c)

/*
@@ lv_stdin_is_tty detects whether the standard input is a 'tty' (that
@* is, whether we're running l u a interactively).
** CHANGE it if you have a better definition for non-POSIX/non-Windows
** systems.
*/
#if defined(LV_USE_ISATTY)
#include <unistd.h>
#define lv_stdin_is_tty()	isatty(0)
#elif defined(LV_WIN)
#include <io.h>
#include <stdio.h>
#define lv_stdin_is_tty()	_isatty(_fileno(stdin))
#else
#define lv_stdin_is_tty()	1  /* assume stdin is a tty */
#endif


/*
@@ LV_PROMPT is the default prompt used by stand-alone [L u a].
@@ LV_PROMPT2 is the default continuation prompt used by stand-alone [L u a].
** CHANGE them if you want different prompts. (You can also change the
** prompts dynamically, assigning to globals _PROMPT/_PROMPT2.)
*/
#define LV_PROMPT		"> "
#define LV_PROMPT2		">> "


/*
@@ LV_PROGNAME is the default name for the stand-alone [L u a] program.
** CHANGE it if your stand-alone interpreter has a different name and
** your system is not able to detect that name automatically.
*/
#define LV_PROGNAME		"l_u_a"


/*
@@ LV_MAXINPUT is the maximum length for an input line in the
@* stand-alone interpreter.
** CHANGE it if you need longer lines.
*/
#define LV_MAXINPUT	512


/*
@@ lv_readline defines how to show a prompt and then read a line from
@* the standard input.
@@ lv_saveline defines how to "save" a read line in a "history".
@@ lv_freeline defines how to free a line read by lv_readline.
** CHANGE them if you want to improve this functionality (e.g., by using
** GNU readline and history facilities).
*/
#if defined(LV_USE_READLINE)
#include <stdio.h>
#include <readline/readline.h>
#include <readline/history.h>
#define lv_readline(L,b,p)	((void)L, ((b)=readline(p)) != NULL)
#define lv_saveline(L,idx) \
	if (lv_strlen(L,idx) > 0)  /* non-empty line? */ \
	  add_history(lv_tostring(L, idx));  /* add it to history */
#define lv_freeline(L,b)	((void)L, free(b))
#else
#define lv_readline(L,b,p)	\
	((void)L, fputs(p, stdout), fflush(stdout),  /* show prompt */ \
	fgets(b, LV_MAXINPUT, stdin) != NULL)  /* get line */
#define lv_saveline(L,idx)	{ (void)L; (void)idx; }
#define lv_freeline(L,b)	{ (void)L; (void)b; }
#endif

#endif

/* }================================================================== */


/*
@@ LVI_GCPAUSE defines the default pause between garbage-collector cycles
@* as a percentage.
** CHANGE it if you want the GC to run faster or slower (higher values
** mean larger pauses which mean slower collection.) You can also change
** this value dynamically.
*/
#define LVI_GCPAUSE	200  /* 200% (wait memory to double before next GC) */


/*
@@ LVI_GCMUL defines the default speed of garbage collection relative to
@* memory allocation as a percentage.
** CHANGE it if you want to change the granularity of the garbage
** collection. (Higher values mean coarser collections. 0 represents
** infinity, where each step performs a full collection.) You can also
** change this value dynamically.
*/
#define LVI_GCMUL	200 /* GC runs 'twice the speed' of memory allocation */



/*
@@ LV_COMPAT_GETN controls compatibility with old getn behavior.
** CHANGE it (define it) if you want exact compatibility with the
** behavior of setn/getn in [L u a] 5.0.
*/
#undef LV_COMPAT_GETN

/*
@@ LV_COMPAT_LOADLIB controls compatibility about global loadlib.
** CHANGE it to undefined as soon as you do not need a global 'loadlib'
** function (the function is still available as 'package.loadlib').
*/
#undef LV_COMPAT_LOADLIB

/*
@@ LV_COMPAT_VARARG controls compatibility with old vararg feature.
** CHANGE it to undefined as soon as your programs use only '...' to
** access vararg parameters (instead of the old 'arg' table).
*/
#define LV_COMPAT_VARARG

/*
@@ LV_COMPAT_MOD controls compatibility with old math.mod function.
** CHANGE it to undefined as soon as your programs use 'math.fmod' or
** the new '%' operator instead of 'math.mod'.
*/
#define LV_COMPAT_MOD

/*
@@ LV_COMPAT_LSTR controls compatibility with old long string nesting
@* facility.
** CHANGE it to 2 if you want the old behaviour, or undefine it to turn
** off the advisory error when nesting [[...]].
*/
#define LV_COMPAT_LSTR		1

/*
@@ LV_COMPAT_GFIND controls compatibility with old 'string.gfind' name.
** CHANGE it to undefined as soon as you rename 'string.gfind' to
** 'string.gmatch'.
*/
#define LV_COMPAT_GFIND

/*
@@ LV_COMPAT_OPENLIB controls compatibility with old 'lvL_openlib'
@* behavior.
** CHANGE it to undefined as soon as you replace to 'lvL_register'
** your uses of 'lvL_openlib'
*/
#define LV_COMPAT_OPENLIB



/*
@@ lvi_apicheck is the assert macro used by the [L u a]-C API.
** CHANGE lvi_apicheck if you want [L u a] to perform some checks in the
** parameters it gets from API calls. This may slow down the interpreter
** a bit, but may be quite useful when debugging C code that interfaces
** with [L u a]. A useful redefinition is to use assert.h.
*/
#if defined(LV_USE_APICHECK)
#include <assert.h>
#define lvi_apicheck(L,o)	{ (void)L; assert(o); }
#else
#define lvi_apicheck(L,o)	{ (void)L; }
#endif


/*
@@ LVI_BITSINT defines the number of bits in an int.
** CHANGE here if [L u a] cannot automatically detect the number of bits of
** your machine. Probably you do not need to change this.
*/
/* avoid overflows in comparison */
#if INT_MAX-20 < 32760
#define LVI_BITSINT	16
#elif INT_MAX > 2147483640L
/* int has at least 32 bits */
#define LVI_BITSINT	32
#else
#error "you must define LV_BITSINT with number of bits in an integer"
#endif


/*
@@ LVI_UINT32 is an unsigned integer with at least 32 bits.
@@ LVI_INT32 is an signed integer with at least 32 bits.
@@ LVI_UMEM is an unsigned integer big enough to count the total
@* memory used by [L u a].
@@ LVI_MEM is a signed integer big enough to count the total memory
@* used by [L u a].
** CHANGE here if for some weird reason the default definitions are not
** good enough for your machine. (The definitions in the 'else'
** part always works, but may waste space on machines with 64-bit
** longs.) Probably you do not need to change this.
*/
#if LVI_BITSINT >= 32
#define LVI_UINT32	unsigned int
#define LVI_INT32	int
#define LVI_MAXINT32	INT_MAX
#define LVI_UMEM	size_t
#define LVI_MEM	ptrdiff_t
#else
/* 16-bit ints */
#define LVI_UINT32	unsigned long
#define LVI_INT32	long
#define LVI_MAXINT32	LONG_MAX
#define LVI_UMEM	unsigned long
#define LVI_MEM	long
#endif


/*
@@ LVI_MAXCALLS limits the number of nested calls.
** CHANGE it if you need really deep recursive calls. This limit is
** arbitrary; its only purpose is to stop infinite recursion before
** exhausting memory.
*/
#define LVI_MAXCALLS	20000


/*
@@ LVI_MAXCSTACK limits the number of [L u a] stack slots that a C function
@* can use.
** CHANGE it if you need lots of ([L u a]) stack space for your C
** functions. This limit is arbitrary; its only purpose is to stop C
** functions to consume unlimited stack space. (must be smaller than
** -LV_REGISTRYINDEX)
*/
#define LVI_MAXCSTACK	8000



/*
** {==================================================================
** CHANGE (to smaller values) the following definitions if your system
** has a small C stack. (Or you may want to change them to larger
** values if your system has a large C stack and these limits are
** too rigid for you.) Some of these constants control the size of
** stack-allocated arrays used by the compiler or the interpreter, while
** others limit the maximum number of recursive calls that the compiler
** or the interpreter can perform. Values too large may cause a C stack
** overflow for some forms of deep constructs.
** ===================================================================
*/


/*
@@ LVI_MAXCCALLS is the maximum depth for nested C calls (short) and
@* syntactical nested non-terminals in a program.
*/
#define LVI_MAXCCALLS		200


/*
@@ LVI_MAXVARS is the maximum number of local variables per function
@* (must be smaller than 250).
*/
#define LVI_MAXVARS		200


/*
@@ LVI_MAXUPVALUES is the maximum number of upvalues per function
@* (must be smaller than 250).
*/
#define LVI_MAXUPVALUES	60


/*
@@ LVL_BUFFERSIZE is the buffer size used by the lauxlib buffer system.
*/
#define LVL_BUFFERSIZE		BUFSIZ

/* }================================================================== */




/*
** {==================================================================
@@ LV_NUMBER is the type of numbers in [L u a].
** CHANGE the following definitions only if you want to build [L u a]
** with a number type different from double. You may also need to
** change lv_number2int & lv_number2integer.
** ===================================================================
*/

#define LV_NUMBER_DOUBLE
#define LV_NUMBER	double

/*
@@ LVI_UACNUMBER is the result of an 'usual argument conversion'
@* over a number.
*/
#define LVI_UACNUMBER	double


/*
@@ LV_NUMBER_SCAN is the format for reading numbers.
@@ LV_NUMBER_FMT is the format for writing numbers.
@@ lv_number2str converts a number to a string.
@@ LVI_MAXNUMBER2STR is maximum size of previous conversion.
@@ lv_str2number converts a string to a number.
*/
#define LV_NUMBER_SCAN		"%lf"
#define LV_NUMBER_FMT		"%.14g"
#define lv_number2str(s,n)	sprintf((s), LV_NUMBER_FMT, (n))
#define LVI_MAXNUMBER2STR	32 /* 16 digits, sign, point, and \0 */
#define lv_str2number(s,p)	strtod((s), (p))


/*
@@ The lvi_num* macros define the primitive operations over numbers.
*/
#if defined(LV_CORE)
#include <math.h>
#define lvi_numadd(a,b)	((a)+(b))
#define lvi_numsub(a,b)	((a)-(b))
#define lvi_nummul(a,b)	((a)*(b))
#define lvi_numdiv(a,b)	((a)/(b))
#define lvi_nummod(a,b)	((a) - floor((a)/(b))*(b))
#define lvi_numpow(a,b)	(pow(a,b))
#define lvi_numunm(a)		(-(a))
#define lvi_numeq(a,b)		((a)==(b))
#define lvi_numlt(a,b)		((a)<(b))
#define lvi_numle(a,b)		((a)<=(b))
#define lvi_numisnan(a)	(!lvi_numeq((a), (a)))
#endif


/*
@@ lv_number2int is a macro to convert lv_Number to int.
@@ lv_number2integer is a macro to convert lv_Number to lv_Integer.
** CHANGE them if you know a faster way to convert a lv_Number to
** int (with any rounding method and without throwing errors) in your
** system. In Pentium machines, a naive typecast from double to int
** in C is extremely slow, so any alternative is worth trying.
*/

/* On a Pentium, resort to a trick */
#if defined(LV_NUMBER_DOUBLE) && !defined(LV_ANSI) && !defined(__SSE2__) && \
    (defined(__i386) || defined (_M_IX86) || defined(__i386__))

/* On a Microsoft compiler, use assembler */
#if defined(_MSC_VER)

#define lv_number2int(i,d)   __asm fld d   __asm fistp i
#define lv_number2integer(i,n)		lv_number2int(i, n)

/* the next trick should work on any Pentium, but sometimes clashes
   with a DirectX idiosyncrasy */
#else

union lvi_Cast { double l_d; long l_l; };
#define lv_number2int(i,d) \
  { volatile union lvi_Cast u; u.l_d = (d) + 6755399441055744.0; (i) = u.l_l; }
#define lv_number2integer(i,n)		lv_number2int(i, n)

#endif


/* this option always works, but may be slow */
#else
#define lv_number2int(i,d)	((i)=(int)(d))
#define lv_number2integer(i,d)	((i)=(lv_Integer)(d))

#endif

/* }================================================================== */


/*
@@ LVI_USER_ALIGNMENT_T is a type that requires maximum alignment.
** CHANGE it if your system requires alignments larger than double. (For
** instance, if your system supports long doubles and they must be
** aligned in 16-byte boundaries, then you should add long double in the
** union.) Probably you do not need to change this.
*/
#define LVI_USER_ALIGNMENT_T	union { double u; void *s; long l; }


/*
@@ LVI_THROW/LVI_TRY define how [L u a] does exception handling.
** CHANGE them if you prefer to use longjmp/setjmp even with C++
** or if want/don't to use _longjmp/_setjmp instead of regular
** longjmp/setjmp. By default, [L u a] handles errors with exceptions when
** compiling as C++ code, with _longjmp/_setjmp when asked to use them,
** and with longjmp/setjmp otherwise.
*/
#if defined(__cplusplus)
/* C++ exceptions */
#define LVI_THROW(L,c)	throw(c)
#define LVI_TRY(L,c,a)	try { a } catch(...) \
	{ if ((c)->status == 0) (c)->status = -1; }
#define lvi_jmpbuf	int  /* dummy variable */

#elif defined(LV_USE_ULONGJMP)
/* in Unix, try _longjmp/_setjmp (more efficient) */
#define LVI_THROW(L,c)	_longjmp((c)->b, 1)
#define LVI_TRY(L,c,a)	if (_setjmp((c)->b) == 0) { a }
#define lvi_jmpbuf	jmp_buf

#else
/* default handling with long jumps */
#define LVI_THROW(L,c)	longjmp((c)->b, 1)
#define LVI_TRY(L,c,a)	if (setjmp((c)->b) == 0) { a }
#define lvi_jmpbuf	jmp_buf

#endif


/*
@@ LV_MAXCAPTURES is the maximum number of captures that a pattern
@* can do during pattern-matching.
** CHANGE it if you need more captures. This limit is arbitrary.
*/
#define LV_MAXCAPTURES		32


/*
@@ lv_tmpnam is the function that the OS library uses to create a
@* temporary name.
@@ LV_TMPNAMBUFSIZE is the maximum size of a name created by lv_tmpnam.
** CHANGE them if you have an alternative to tmpnam (which is considered
** insecure) or if you want the original tmpnam anyway.  By default, [L u a]
** uses tmpnam except when POSIX is available, where it uses mkstemp.
*/
#if defined(loslib_c) || defined(lvall_c)

#if defined(LV_USE_MKSTEMP)
#include <unistd.h>
#define LV_TMPNAMBUFSIZE	32
#define lv_tmpnam(b,e)	{ \
	strcpy(b, "/tmp/lv_XXXXXX"); \
	e = mkstemp(b); \
	if (e != -1) close(e); \
	e = (e == -1); }

#else
#define LV_TMPNAMBUFSIZE	L_tmpnam
#define lv_tmpnam(b,e)		{ e = (tmpnam(b) == NULL); }
#endif

#endif


/*
@@ lv_popen spawns a new process connected to the current one through
@* the file streams.
** CHANGE it if you have a way to implement it in your system.
*/
#if defined(LV_USE_POPEN)

#define lv_popen(L,c,m)	((void)L, fflush(NULL), popen(c,m))
#define lv_pclose(L,file)	((void)L, (pclose(file) != -1))

#elif defined(LV_WIN)

#define lv_popen(L,c,m)	((void)L, _popen(c,m))
#define lv_pclose(L,file)	((void)L, (_pclose(file) != -1))

#else

#define lv_popen(L,c,m)	((void)((void)c, m),  \
		lvL_error(L, LV_QL("popen") " not supported"), (FILE*)0)
#define lv_pclose(L,file)		((void)((void)L, file), 0)

#endif

/*
@@ LV_DL_* define which dynamic-library system [L u a] should use.
** CHANGE here if [L u a] has problems choosing the appropriate
** dynamic-library system for your platform (either Windows' DLL, Mac's
** dyld, or Unix's dlopen). If your system is some kind of Unix, there
** is a good chance that it has dlopen, so LV_DL_DLOPEN will work for
** it.  To use dlopen you also need to adapt the src/Makefile (probably
** adding -ldl to the linker options), so [L u a] does not select it
** automatically.  (When you change the makefile to add -ldl, you must
** also add -DL UA_USE_DLOPEN.)
** If you do not want any kind of dynamic library, undefine all these
** options.
** By default, _WIN32 gets LV_DL_DLL and MAC OS X gets LV_DL_DYLD.
*/
#if defined(LV_USE_DLOPEN)
#define LV_DL_DLOPEN
#endif

#if defined(LV_WIN)
#define LV_DL_DLL
#endif


/*
@@ LVI_EXTRASPACE allows you to add user-specific data in a lv_State
@* (the data goes just *before* the lv_State pointer).
** CHANGE (define) this if you really need that. This value must be
** a multiple of the maximum alignment required for your machine.
*/
#define LVI_EXTRASPACE		0


/*
@@ lvi_userstate* allow user-specific actions on threads.
** CHANGE them if you defined LVI_EXTRASPACE and need to do something
** extra when a thread is created/deleted/resumed/yielded.
*/
#define lvi_userstateopen(L)		((void)L)
#define lvi_userstateclose(L)		((void)L)
#define lvi_userstatethread(L,L1)	((void)L)
#define lvi_userstatefree(L)		((void)L)
#define lvi_userstateresume(L,n)	((void)L)
#define lvi_userstateyield(L,n)	((void)L)


/*
@@ LV_INTFRMLEN is the length modifier for integer conversions
@* in 'string.format'.
@@ LV_INTFRM_T is the integer type correspoding to the previous length
@* modifier.
** CHANGE them if your system supports long long or does not support long.
*/

#if defined(LV_USELONGLONG)

#define LV_INTFRMLEN		"ll"
#define LV_INTFRM_T		long long

#else

#define LV_INTFRMLEN		"l"
#define LV_INTFRM_T		long

#endif



/* =================================================================== */

/*
** Local configuration. You can use this space to add your redefinitions
** without modifying the main part of the file.
*/



#endif

