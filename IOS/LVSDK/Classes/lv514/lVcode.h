/*
** $Id: lVcode.h,v 1.48.1.1 2007/12/27 13:02:25 roberto Exp $
** Code generator for [L u a]
** See Copyright Notice in lV.h
*/

#ifndef lcode_h
#define lcode_h

#include "lVlex.h"
#include "lVobject.h"
#include "lVopcodes.h"
#include "lVparser.h"


/*
** Marks the end of a patch list. It is an invalid value both as an absolute
** address, and as a list link (would link an element to itself).
*/
#define NO_JUMP (-1)


/*
** grep "ORDER OPR" if you change these enums
*/
typedef enum BinOpr {
  OPR_ADD, OPR_SUB, OPR_MUL, OPR_DIV, OPR_MOD, OPR_POW,
  OPR_CONCAT,
  OPR_NE, OPR_EQ,
  OPR_LT, OPR_LE, OPR_GT, OPR_GE,
  OPR_AND, OPR_OR,
  OPR_NOBINOPR
} BinOpr;


typedef enum UnOpr { OPR_MINUS, OPR_NOT, OPR_LEN, OPR_NOUNOPR } UnOpr;


#define getcode(fs,e)	((fs)->f->code[(e)->u.s.info])

#define lvK_codeAsBx(fs,o,A,sBx)	lvK_codeABx(fs,o,A,(sBx)+MAXARG_sBx)

#define lvK_setmultret(fs,e)	lvK_setreturns(fs, e, LV_MULTRET)

LVI_FUNC int lvK_codeABx (FuncState *fs, OpCode o, int A, unsigned int Bx);
LVI_FUNC int lvK_codeABC (FuncState *fs, OpCode o, int A, int B, int C);
LVI_FUNC void lvK_fixline (FuncState *fs, int line);
LVI_FUNC void lvK_nil (FuncState *fs, int from, int n);
LVI_FUNC void lvK_reserveregs (FuncState *fs, int n);
LVI_FUNC void lvK_checkstack (FuncState *fs, int n);
LVI_FUNC int lvK_stringK (FuncState *fs, TString *s);
LVI_FUNC int lvK_numberK (FuncState *fs, lv_Number r);
LVI_FUNC void lvK_dischargevars (FuncState *fs, expdesc *e);
LVI_FUNC int lvK_exp2anyreg (FuncState *fs, expdesc *e);
LVI_FUNC void lvK_exp2nextreg (FuncState *fs, expdesc *e);
LVI_FUNC void lvK_exp2val (FuncState *fs, expdesc *e);
LVI_FUNC int lvK_exp2RK (FuncState *fs, expdesc *e);
LVI_FUNC void lvK_self (FuncState *fs, expdesc *e, expdesc *key);
LVI_FUNC void lvK_indexed (FuncState *fs, expdesc *t, expdesc *k);
LVI_FUNC void lvK_goiftrue (FuncState *fs, expdesc *e);
LVI_FUNC void lvK_storevar (FuncState *fs, expdesc *var, expdesc *e);
LVI_FUNC void lvK_setreturns (FuncState *fs, expdesc *e, int nresults);
LVI_FUNC void lvK_setoneret (FuncState *fs, expdesc *e);
LVI_FUNC int lvK_jump (FuncState *fs);
LVI_FUNC void lvK_ret (FuncState *fs, int first, int nret);
LVI_FUNC void lvK_patchlist (FuncState *fs, int list, int target);
LVI_FUNC void lvK_patchtohere (FuncState *fs, int list);
LVI_FUNC void lvK_concat (FuncState *fs, int *l1, int l2);
LVI_FUNC int lvK_getlabel (FuncState *fs);
LVI_FUNC void lvK_prefix (FuncState *fs, UnOpr op, expdesc *v);
LVI_FUNC void lvK_infix (FuncState *fs, BinOpr op, expdesc *v);
LVI_FUNC void lvK_posfix (FuncState *fs, BinOpr op, expdesc *v1, expdesc *v2);
LVI_FUNC void lvK_setlist (FuncState *fs, int base, int nelems, int tostore);


#endif
