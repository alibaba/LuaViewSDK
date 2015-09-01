/*
** $Id: lVlib.h,v 1.36.1.1 2007/12/27 13:02:25 roberto Exp $
** [L u a] standard libraries
** See Copyright Notice in lV.h
*/


#ifndef lv_lib_h
#define lv_lib_h

#include "lV.h"


/* Key to file-handle type */
#define LV_FILEHANDLE		"FILE*"


#define LV_COLIBNAME	"coroutine"
LVLIB_API int (lvopen_base) (lv_State *L);

#define LV_TABLIBNAME	"table"
LVLIB_API int (lvopen_table) (lv_State *L);

#define LV_IOLIBNAME	"io"
LVLIB_API int (lvopen_io) (lv_State *L);

#define LV_OSLIBNAME	"os"
LVLIB_API int (lvopen_os) (lv_State *L);

#define LV_STRLIBNAME	"string"
LVLIB_API int (lvopen_string) (lv_State *L);

#define LV_MATHLIBNAME	"math"
LVLIB_API int (lvopen_math) (lv_State *L);

#define LV_DBLIBNAME	"debug"
LVLIB_API int (lvopen_debug) (lv_State *L);

#define LV_LOADLIBNAME	"package"
LVLIB_API int (lvopen_package) (lv_State *L);


/* open all previous libraries */
LVLIB_API void (lvL_openlibs) (lv_State *L); 



#ifndef lv_assert
#define lv_assert(x)	((void)0)
#endif


#endif
