/*
** $Id: lViolib.c,v 2.73.1.3 2008/01/18 17:47:43 roberto Exp $
** Standard I/O (and system) library
** See Copyright Notice in lV.h
*/


#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define liolib_c
#define LV_LIB

#include "lV.h"

#include "lVauxlib.h"
#include "lVlib.h"



#define IO_INPUT	1
#define IO_OUTPUT	2


static const char *const fnames[] = {"input", "output"};


static int pushresult (lv_State *L, int i, const char *filename) {
  int en = errno;  /* calls to [L u a] API may change this value */
  if (i) {
    lv_pushboolean(L, 1);
    return 1;
  }
  else {
    lv_pushnil(L);
    if (filename)
      lv_pushfstring(L, "%s: %s", filename, strerror(en));
    else
      lv_pushfstring(L, "%s", strerror(en));
    lv_pushinteger(L, en);
    return 3;
  }
}


static void fileerror (lv_State *L, int arg, const char *filename) {
  lv_pushfstring(L, "%s: %s", filename, strerror(errno));
  lvL_argerror(L, arg, lv_tostring(L, -1));
}


#define tofilep(L)	((FILE **)lvL_checkudata(L, 1, LV_FILEHANDLE))


static int io_type (lv_State *L) {
  void *ud;
  lvL_checkany(L, 1);
  ud = lv_touserdata(L, 1);
  lv_getfield(L, LV_REGISTRYINDEX, LV_FILEHANDLE);
  if (ud == NULL || !lv_getmetatable(L, 1) || !lv_rawequal(L, -2, -1))
    lv_pushnil(L);  /* not a file */
  else if (*((FILE **)ud) == NULL)
    lv_pushliteral(L, "closed file");
  else
    lv_pushliteral(L, "file");
  return 1;
}


static FILE *tofile (lv_State *L) {
  FILE **f = tofilep(L);
  if (*f == NULL)
    lvL_error(L, "attempt to use a closed file");
  return *f;
}



/*
** When creating file handles, always creates a `closed' file handle
** before opening the actual file; so, if there is a memory error, the
** file is not left opened.
*/
static FILE **newfile (lv_State *L) {
  FILE **pf = (FILE **)lv_newuserdata(L, sizeof(FILE *));
  *pf = NULL;  /* file handle is currently `closed' */
  lvL_getmetatable(L, LV_FILEHANDLE);
  lv_setmetatable(L, -2);
  return pf;
}


/*
** function to (not) close the standard files stdin, stdout, and stderr
*/
static int io_noclose (lv_State *L) {
  lv_pushnil(L);
  lv_pushliteral(L, "cannot close standard file");
  return 2;
}


/*
** function to close 'popen' files
*/
static int io_pclose (lv_State *L) {
  FILE **p = tofilep(L);
  int ok = lv_pclose(L, *p);
  *p = NULL;
  return pushresult(L, ok, NULL);
}


/*
** function to close regular files
*/
static int io_fclose (lv_State *L) {
  FILE **p = tofilep(L);
  int ok = (fclose(*p) == 0);
  *p = NULL;
  return pushresult(L, ok, NULL);
}


static int aux_close (lv_State *L) {
  lv_getfenv(L, 1);
  lv_getfield(L, -1, "__close");
  return (lv_tocfunction(L, -1))(L);
}


static int io_close (lv_State *L) {
  if (lv_isnone(L, 1))
    lv_rawgeti(L, LV_ENVIRONINDEX, IO_OUTPUT);
  tofile(L);  /* make sure argument is a file */
  return aux_close(L);
}


static int io_gc (lv_State *L) {
  FILE *f = *tofilep(L);
  /* ignore closed files */
  if (f != NULL)
    aux_close(L);
  return 0;
}


static int io_tostring (lv_State *L) {
  FILE *f = *tofilep(L);
  if (f == NULL)
    lv_pushliteral(L, "file (closed)");
  else
    lv_pushfstring(L, "file (%p)", f);
  return 1;
}


static int io_open (lv_State *L) {
  const char *filename = lvL_checkstring(L, 1);
  const char *mode = lvL_optstring(L, 2, "r");
  FILE **pf = newfile(L);
  *pf = fopen(filename, mode);
  return (*pf == NULL) ? pushresult(L, 0, filename) : 1;
}


/*
** this function has a separated environment, which defines the
** correct __close for 'popen' files
*/
static int io_popen (lv_State *L) {
  const char *filename = lvL_checkstring(L, 1);
  const char *mode = lvL_optstring(L, 2, "r");
  FILE **pf = newfile(L);
  *pf = lv_popen(L, filename, mode);
  return (*pf == NULL) ? pushresult(L, 0, filename) : 1;
}


static int io_tmpfile (lv_State *L) {
  FILE **pf = newfile(L);
  *pf = tmpfile();
  return (*pf == NULL) ? pushresult(L, 0, NULL) : 1;
}


static FILE *getiofile (lv_State *L, int findex) {
  FILE *f;
  lv_rawgeti(L, LV_ENVIRONINDEX, findex);
  f = *(FILE **)lv_touserdata(L, -1);
  if (f == NULL)
    lvL_error(L, "standard %s file is closed", fnames[findex - 1]);
  return f;
}


static int g_iofile (lv_State *L, int f, const char *mode) {
  if (!lv_isnoneornil(L, 1)) {
    const char *filename = lv_tostring(L, 1);
    if (filename) {
      FILE **pf = newfile(L);
      *pf = fopen(filename, mode);
      if (*pf == NULL)
        fileerror(L, 1, filename);
    }
    else {
      tofile(L);  /* check that it's a valid file handle */
      lv_pushvalue(L, 1);
    }
    lv_rawseti(L, LV_ENVIRONINDEX, f);
  }
  /* return current value */
  lv_rawgeti(L, LV_ENVIRONINDEX, f);
  return 1;
}


static int io_input (lv_State *L) {
  return g_iofile(L, IO_INPUT, "r");
}


static int io_output (lv_State *L) {
  return g_iofile(L, IO_OUTPUT, "w");
}


static int io_readline (lv_State *L);


static void aux_lines (lv_State *L, int idx, int toclose) {
  lv_pushvalue(L, idx);
  lv_pushboolean(L, toclose);  /* close/not close file when finished */
  lv_pushcclosure(L, io_readline, 2);
}


static int f_lines (lv_State *L) {
  tofile(L);  /* check that it's a valid file handle */
  aux_lines(L, 1, 0);
  return 1;
}


static int io_lines (lv_State *L) {
  if (lv_isnoneornil(L, 1)) {  /* no arguments? */
    /* will iterate over default input */
    lv_rawgeti(L, LV_ENVIRONINDEX, IO_INPUT);
    return f_lines(L);
  }
  else {
    const char *filename = lvL_checkstring(L, 1);
    FILE **pf = newfile(L);
    *pf = fopen(filename, "r");
    if (*pf == NULL)
      fileerror(L, 1, filename);
    aux_lines(L, lv_gettop(L), 1);
    return 1;
  }
}


/*
** {======================================================
** READ
** =======================================================
*/


static int read_number (lv_State *L, FILE *f) {
  lv_Number d;
  if (fscanf(f, LV_NUMBER_SCAN, &d) == 1) {
    lv_pushnumber(L, d);
    return 1;
  }
  else return 0;  /* read fails */
}


static int test_eof (lv_State *L, FILE *f) {
  int c = getc(f);
  ungetc(c, f);
  lv_pushlstring(L, NULL, 0);
  return (c != EOF);
}


static int read_line (lv_State *L, FILE *f) {
  lvL_Buffer b;
  lvL_buffinit(L, &b);
  for (;;) {
    size_t l;
    char *p = lvL_prepbuffer(&b);
    if (fgets(p, LVL_BUFFERSIZE, f) == NULL) {  /* eof? */
      lvL_pushresult(&b);  /* close buffer */
      return (lv_objlen(L, -1) > 0);  /* check whether read something */
    }
    l = strlen(p);
    if (l == 0 || p[l-1] != '\n')
      lvL_addsize(&b, l);
    else {
      lvL_addsize(&b, l - 1);  /* do not include `eol' */
      lvL_pushresult(&b);  /* close buffer */
      return 1;  /* read at least an `eol' */
    }
  }
}


static int read_chars (lv_State *L, FILE *f, size_t n) {
  size_t rlen;  /* how much to read */
  size_t nr;  /* number of chars actually read */
  lvL_Buffer b;
  lvL_buffinit(L, &b);
  rlen = LVL_BUFFERSIZE;  /* try to read that much each time */
  do {
    char *p = lvL_prepbuffer(&b);
    if (rlen > n) rlen = n;  /* cannot read more than asked */
    nr = fread(p, sizeof(char), rlen, f);
    lvL_addsize(&b, nr);
    n -= nr;  /* still have to read `n' chars */
  } while (n > 0 && nr == rlen);  /* until end of count or eof */
  lvL_pushresult(&b);  /* close buffer */
  return (n == 0 || lv_objlen(L, -1) > 0);
}


static int g_read (lv_State *L, FILE *f, int first) {
  int nargs = lv_gettop(L) - 1;
  int success;
  int n;
  clearerr(f);
  if (nargs == 0) {  /* no arguments? */
    success = read_line(L, f);
    n = first+1;  /* to return 1 result */
  }
  else {  /* ensure stack space for all results and for auxlib's buffer */
    lvL_checkstack(L, nargs+LV_MINSTACK, "too many arguments");
    success = 1;
    for (n = first; nargs-- && success; n++) {
      if (lv_type(L, n) == LV_TNUMBER) {
        size_t l = (size_t)lv_tointeger(L, n);
        success = (l == 0) ? test_eof(L, f) : read_chars(L, f, l);
      }
      else {
        const char *p = lv_tostring(L, n);
        lvL_argcheck(L, p && p[0] == '*', n, "invalid option");
          if( p ) {
            switch (p[1]) {
              case 'n':  /* number */
                success = read_number(L, f);
                break;
              case 'l':  /* line */
                success = read_line(L, f);
                break;
              case 'a':  /* file */
                read_chars(L, f, ~((size_t)0));  /* read MAX_SIZE_T chars */
                success = 1; /* always success */
                break;
              default:
                return lvL_argerror(L, n, "invalid format");
            }
          }
      }
    }
  }
  if (ferror(f))
    return pushresult(L, 0, NULL);
  if (!success) {
    lv_pop(L, 1);  /* remove last result */
    lv_pushnil(L);  /* push nil instead */
  }
  return n - first;
}


static int io_read (lv_State *L) {
  return g_read(L, getiofile(L, IO_INPUT), 1);
}


static int f_read (lv_State *L) {
  return g_read(L, tofile(L), 2);
}


static int io_readline (lv_State *L) {
  FILE *f = *(FILE **)lv_touserdata(L, lv_upvalueindex(1));
  int sucess;
  if (f == NULL)  /* file is already closed? */
    lvL_error(L, "file is already closed");
  sucess = read_line(L, f);
  if (ferror(f))
    return lvL_error(L, "%s", strerror(errno));
  if (sucess) return 1;
  else {  /* EOF */
    if (lv_toboolean(L, lv_upvalueindex(2))) {  /* generator created file? */
      lv_settop(L, 0);
      lv_pushvalue(L, lv_upvalueindex(1));
      aux_close(L);  /* close it */
    }
    return 0;
  }
}

/* }====================================================== */


static int g_write (lv_State *L, FILE *f, int arg) {
  int nargs = lv_gettop(L) - 1;
  int status = 1;
  for (; nargs--; arg++) {
    if (lv_type(L, arg) == LV_TNUMBER) {
      /* optimization: could be done exactly as for strings */
      status = status &&
          fprintf(f, LV_NUMBER_FMT, lv_tonumber(L, arg)) > 0;
    }
    else {
      size_t l;
      const char *s = lvL_checklstring(L, arg, &l);
      status = status && (fwrite(s, sizeof(char), l, f) == l);
    }
  }
  return pushresult(L, status, NULL);
}


static int io_write (lv_State *L) {
  return g_write(L, getiofile(L, IO_OUTPUT), 1);
}


static int f_write (lv_State *L) {
  return g_write(L, tofile(L), 2);
}


static int f_seek (lv_State *L) {
  static const int mode[] = {SEEK_SET, SEEK_CUR, SEEK_END};
  static const char *const modenames[] = {"set", "cur", "end", NULL};
  FILE *f = tofile(L);
  int op = lvL_checkoption(L, 2, "cur", modenames);
  long offset = lvL_optlong(L, 3, 0);
  op = fseek(f, offset, mode[op]);
  if (op)
    return pushresult(L, 0, NULL);  /* error */
  else {
    lv_pushinteger(L, ftell(f));
    return 1;
  }
}


static int f_setvbuf (lv_State *L) {
  static const int mode[] = {_IONBF, _IOFBF, _IOLBF};
  static const char *const modenames[] = {"no", "full", "line", NULL};
  FILE *f = tofile(L);
  int op = lvL_checkoption(L, 2, NULL, modenames);
  lv_Integer sz = lvL_optinteger(L, 3, LVL_BUFFERSIZE);
  int res = setvbuf(f, NULL, mode[op], sz);
  return pushresult(L, res == 0, NULL);
}



static int io_flush (lv_State *L) {
  return pushresult(L, fflush(getiofile(L, IO_OUTPUT)) == 0, NULL);
}


static int f_flush (lv_State *L) {
  return pushresult(L, fflush(tofile(L)) == 0, NULL);
}


static const lvL_Reg iolib[] = {
  {"close", io_close},
  {"flush", io_flush},
  {"input", io_input},
  {"lines", io_lines},
  {"open", io_open},
  {"output", io_output},
  {"popen", io_popen},
  {"read", io_read},
  {"tmpfile", io_tmpfile},
  {"type", io_type},
  {"write", io_write},
  {NULL, NULL}
};


static const lvL_Reg flib[] = {
  {"close", io_close},
  {"flush", f_flush},
  {"lines", f_lines},
  {"read", f_read},
  {"seek", f_seek},
  {"setvbuf", f_setvbuf},
  {"write", f_write},
  {"__gc", io_gc},
  {"__tostring", io_tostring},
  {NULL, NULL}
};


static void createmeta (lv_State *L) {
  lvL_newmetatable(L, LV_FILEHANDLE);  /* create metatable for file handles */
  lv_pushvalue(L, -1);  /* push metatable */
  lv_setfield(L, -2, "__index");  /* metatable.__index = metatable */
  lvL_register(L, NULL, flib);  /* file methods */
}


static void createstdfile (lv_State *L, FILE *f, int k, const char *fname) {
  *newfile(L) = f;
  if (k > 0) {
    lv_pushvalue(L, -1);
    lv_rawseti(L, LV_ENVIRONINDEX, k);
  }
  lv_pushvalue(L, -2);  /* copy environment */
  lv_setfenv(L, -2);  /* set it */
  lv_setfield(L, -3, fname);
}


static void newfenv (lv_State *L, lv_CFunction cls) {
  lv_createtable(L, 0, 1);
  lv_pushcfunction(L, cls);
  lv_setfield(L, -2, "__close");
}


LVLIB_API int lvopen_io (lv_State *L) {
  createmeta(L);
  /* create (private) environment (with fields IO_INPUT, IO_OUTPUT, __close) */
  newfenv(L, io_fclose);
  lv_replace(L, LV_ENVIRONINDEX);
  /* open library */
  lvL_register(L, LV_IOLIBNAME, iolib);
  /* create (and set) default files */
  newfenv(L, io_noclose);  /* close function for default files */
  createstdfile(L, stdin, IO_INPUT, "stdin");
  createstdfile(L, stdout, IO_OUTPUT, "stdout");
  createstdfile(L, stderr, 0, "stderr");
  lv_pop(L, 1);  /* pop environment for default files */
  lv_getfield(L, -1, "popen");
  newfenv(L, io_pclose);  /* create environment for 'popen' */
  lv_setfenv(L, -2);  /* set fenv for 'popen' */
  lv_pop(L, 1);  /* pop 'popen' */
  return 1;
}

