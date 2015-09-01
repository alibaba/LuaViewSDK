/*
** $Id: lVparser.h,v 1.57.1.1 2007/12/27 13:02:25 roberto Exp $
** [L u a] Parser
** See Copyright Notice in lV.h
*/

#ifndef lparser_h
#define lparser_h

#include "lVlimits.h"
#include "lVobject.h"
#include "lVzio.h"


/*
** Expression descriptor
*/

typedef enum {
  VVOID,	/* no value */
  VNIL,
  VTRUE,
  VFALSE,
  VK,		/* info = index of constant in `k' */
  VKNUM,	/* nval = numerical value */
  VLOCAL,	/* info = local register */
  VUPVAL,       /* info = index of upvalue in `upvalues' */
  VGLOBAL,	/* info = index of table; aux = index of global name in `k' */
  VINDEXED,	/* info = table register; aux = index register (or `k') */
  VJMP,		/* info = instruction pc */
  VRELOCABLE,	/* info = instruction pc */
  VNONRELOC,	/* info = result register */
  VCALL,	/* info = instruction pc */
  VVARARG	/* info = instruction pc */
} expkind;

typedef struct expdesc {
  expkind k;
  union {
    struct { int info, aux; } s;
    lv_Number nval;
  } u;
  int t;  /* patch list of `exit when true' */
  int f;  /* patch list of `exit when false' */
} expdesc;


typedef struct upvaldesc {
  lu_byte k;
  lu_byte info;
} upvaldesc;


struct BlockCnt;  /* defined in lVparser.c */


/* state needed to generate code for a given function */
typedef struct FuncState {
  Proto *f;  /* current function header */
  Table *h;  /* table to find (and reuse) elements in `k' */
  struct FuncState *prev;  /* enclosing function */
  struct LexState *ls;  /* lexical state */
  struct lv_State *L;  /* copy of the [L u a] state */
  struct BlockCnt *bl;  /* chain of current blocks */
  int pc;  /* next position to code (equivalent to `ncode') */
  int lasttarget;   /* `pc' of last `jump target' */
  int jpc;  /* list of pending jumps to `pc' */
  int freereg;  /* first free register */
  int nk;  /* number of elements in `k' */
  int np;  /* number of elements in `p' */
  short nlocvars;  /* number of elements in `locvars' */
  lu_byte nactvar;  /* number of active local variables */
  upvaldesc upvalues[LVI_MAXUPVALUES];  /* upvalues */
  unsigned short actvar[LVI_MAXVARS];  /* declared-variable stack */
} FuncState;


LVI_FUNC Proto *lvY_parser (lv_State *L, ZIO *z, Mbuffer *buff,
                                            const char *name);


#endif
