/*
** $Id: lVundump.h,v 1.37.1.1 2007/12/27 13:02:25 roberto Exp $
** load precompiled [L u a] chunks
** See Copyright Notice in lV.h
*/

#ifndef lundump_h
#define lundump_h

#include "lVobject.h"
#include "lVzio.h"

/* load one chunk; from lVundump.c */
LVI_FUNC Proto* lvU_undump (lv_State* L, ZIO* Z, Mbuffer* buff, const char* name);

/* make header; from lVundump.c */
LVI_FUNC void lvU_header (char* h);

/* dump one chunk; from lVdump.c */
LVI_FUNC int lvU_dump (lv_State* L, const Proto* f, lv_Writer w, void* data, int strip);

#ifdef lvc_c
/* print one chunk; from lVprint.c */
LVI_FUNC void lvU_print (const Proto* f, int full);
#endif

/* for header of binary files -- this is [L u a] 5.1 */
#define LVC_VERSION		0x51

/* for header of binary files -- this is the official format */
#define LVC_FORMAT		0

/* size of header of binary files */
#define LVC_HEADERSIZE		12

#endif
