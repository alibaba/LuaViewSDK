/*
** $Id: lVzio.c,v 1.31.1.1 2007/12/27 13:02:25 roberto Exp $
** a generic input stream interface
** See Copyright Notice in lV.h
*/


#include <string.h>

#define lzio_c
#define LV_CORE

#include "lV.h"

#include "lVlimits.h"
#include "lVmem.h"
#include "lVstate.h"
#include "lVzio.h"


int lvZ_fill (ZIO *z) {
  size_t size;
  lv_State *L = z->L;
  const char *buff;
  lv_unlock(L);
  buff = z->reader(L, z->data, &size);
  lv_lock(L);
  if (buff == NULL || size == 0) return EOZ;
  z->n = size - 1;
  z->p = buff;
  return char2int(*(z->p++));
}


int lvZ_lookahead (ZIO *z) {
  if (z->n == 0) {
    if (lvZ_fill(z) == EOZ)
      return EOZ;
    else {
      z->n++;  /* lvZ_fill removed first byte; put back it */
      z->p--;
    }
  }
  return char2int(*z->p);
}


void lvZ_init (lv_State *L, ZIO *z, lv_Reader reader, void *data) {
  z->L = L;
  z->reader = reader;
  z->data = data;
  z->n = 0;
  z->p = NULL;
}


/* --------------------------------------------------------------- read --- */
size_t lvZ_read (ZIO *z, void *b, size_t n) {
  while (n) {
    size_t m;
    if (lvZ_lookahead(z) == EOZ)
      return n;  /* return number of missing bytes */
    m = (n <= z->n) ? n : z->n;  /* min. between n and z->n */
    memcpy(b, z->p, m);
    z->n -= m;
    z->p += m;
    b = (char *)b + m;
    n -= m;
  }
  return 0;
}

/* ------------------------------------------------------------------------ */
char *lvZ_openspace (lv_State *L, Mbuffer *buff, size_t n) {
  if (n > buff->buffsize) {
    if (n < LV_MINBUFFER) n = LV_MINBUFFER;
    lvZ_resizebuffer(L, buff, n);
  }
  return buff->buffer;
}


