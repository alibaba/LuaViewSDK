/*
** $Id: lVzio.h,v 1.21.1.1 2007/12/27 13:02:25 roberto Exp $
** Buffered streams
** See Copyright Notice in lV.h
*/


#ifndef lzio_h
#define lzio_h

#include "lV.h"

#include "lVmem.h"


#define EOZ	(-1)			/* end of stream */

typedef struct Zio ZIO;

#define char2int(c)	cast(int, cast(unsigned char, (c)))

#define zgetc(z)  (((z)->n--)>0 ?  char2int(*(z)->p++) : lvZ_fill(z))

typedef struct Mbuffer {
  char *buffer;
  size_t n;
  size_t buffsize;
} Mbuffer;

#define lvZ_initbuffer(L, buff) ((buff)->buffer = NULL, (buff)->buffsize = 0)

#define lvZ_buffer(buff)	((buff)->buffer)
#define lvZ_sizebuffer(buff)	((buff)->buffsize)
#define lvZ_bufflen(buff)	((buff)->n)

#define lvZ_resetbuffer(buff) ((buff)->n = 0)


#define lvZ_resizebuffer(L, buff, size) \
	(lvM_reallocvector(L, (buff)->buffer, (buff)->buffsize, size, char), \
	(buff)->buffsize = size)

#define lvZ_freebuffer(L, buff)	lvZ_resizebuffer(L, buff, 0)


LVI_FUNC char *lvZ_openspace (lv_State *L, Mbuffer *buff, size_t n);
LVI_FUNC void lvZ_init (lv_State *L, ZIO *z, lv_Reader reader,
                                        void *data);
LVI_FUNC size_t lvZ_read (ZIO* z, void* b, size_t n);	/* read next n bytes */
LVI_FUNC int lvZ_lookahead (ZIO *z);



/* --------- Private Part ------------------ */

struct Zio {
  size_t n;			/* bytes still unread */
  const char *p;		/* current position in buffer */
  lv_Reader reader;
  void* data;			/* additional data */
  lv_State *L;			/* [L u a] state (for reader) */
};


LVI_FUNC int lvZ_fill (ZIO *z);

#endif
