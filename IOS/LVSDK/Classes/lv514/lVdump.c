/*
** $Id: lVdump.c,v 2.8.1.1 2007/12/27 13:02:25 roberto Exp $
** save precompiled [L u a] chunks
** See Copyright Notice in lV.h
*/

#include <stddef.h>

#define ldump_c
#define LV_CORE

#include "lV.h"

#include "lVobject.h"
#include "lVstate.h"
#include "lVundump.h"

typedef struct {
 lv_State* L;
 lv_Writer writer;
 void* data;
 int strip;
 int status;
} DumpState;

#define DumpMem(b,n,size,D)	DumpBlock(b,(n)*(size),D)
#define DumpVar(x,D)	 	DumpMem(&x,1,sizeof(x),D)

static void DumpBlock(const void* b, size_t size, DumpState* D)
{
 if (D->status==0)
 {
  lv_unlock(D->L);
  D->status=(*D->writer)(D->L,b,size,D->data);
  lv_lock(D->L);
 }
}

static void DumpChar(int y, DumpState* D)
{
 char x=(char)y;
 DumpVar(x,D);
}

static void DumpInt(int x, DumpState* D)
{
 DumpVar(x,D);
}

static void DumpNumber(lv_Number x, DumpState* D)
{
 DumpVar(x,D);
}

static void DumpVector(const void* b, int n, size_t size, DumpState* D)
{
 DumpInt(n,D);
 DumpMem(b,n,size,D);
}

static void DumpString(const TString* s, DumpState* D)
{
 if (s==NULL || getstr(s)==NULL)
 {
  size_t size=0;
  DumpVar(size,D);
 }
 else
 {
  size_t size=s->tsv.len+1;		/* include trailing '\0' */
  DumpVar(size,D);
  DumpBlock(getstr(s),size,D);
 }
}

#define DumpCode(f,D)	 DumpVector(f->code,f->sizecode,sizeof(Instruction),D)

static void DumpFunction(const Proto* f, const TString* p, DumpState* D);

static void DumpConstants(const Proto* f, DumpState* D)
{
 int i,n=f->sizek;
 DumpInt(n,D);
 for (i=0; i<n; i++)
 {
  const TValue* o=&f->k[i];
  DumpChar(ttype(o),D);
  switch (ttype(o))
  {
   case LV_TNIL:
	break;
   case LV_TBOOLEAN:
	DumpChar(bvalue(o),D);
	break;
   case LV_TNUMBER:
	DumpNumber(nvalue(o),D);
	break;
   case LV_TSTRING:
	DumpString(rawtsvalue(o),D);
	break;
   default:
	lv_assert(0);			/* cannot happen */
	break;
  }
 }
 n=f->sizep;
 DumpInt(n,D);
 for (i=0; i<n; i++) DumpFunction(f->p[i],f->source,D);
}

static void DumpDebug(const Proto* f, DumpState* D)
{
 int i,n;
 n= (D->strip) ? 0 : f->sizelineinfo;
 DumpVector(f->lineinfo,n,sizeof(int),D);
 n= (D->strip) ? 0 : f->sizelocvars;
 DumpInt(n,D);
 for (i=0; i<n; i++)
 {
  DumpString(f->locvars[i].varname,D);
  DumpInt(f->locvars[i].startpc,D);
  DumpInt(f->locvars[i].endpc,D);
 }
 n= (D->strip) ? 0 : f->sizeupvalues;
 DumpInt(n,D);
 for (i=0; i<n; i++) DumpString(f->upvalues[i],D);
}

static void DumpFunction(const Proto* f, const TString* p, DumpState* D)
{
 DumpString((f->source==p || D->strip) ? NULL : f->source,D);
 DumpInt(f->linedefined,D);
 DumpInt(f->lastlinedefined,D);
 DumpChar(f->nups,D);
 DumpChar(f->numparams,D);
 DumpChar(f->is_vararg,D);
 DumpChar(f->maxstacksize,D);
 DumpCode(f,D);
 DumpConstants(f,D);
 DumpDebug(f,D);
}

static void DumpHeader(DumpState* D)
{
 char h[LVC_HEADERSIZE];
 lvU_header(h);
 DumpBlock(h,LVC_HEADERSIZE,D);
}

/*
** dump [L u a] function as precompiled chunk
*/
int lvU_dump (lv_State* L, const Proto* f, lv_Writer w, void* data, int strip)
{
 DumpState D;
 D.L=L;
 D.writer=w;
 D.data=data;
 D.strip=strip;
 D.status=0;
 DumpHeader(&D);
 DumpFunction(f,NULL,&D);
 return D.status;
}
