/*
 ** $Id: lVlex.c,v 2.20.1.1 2007/12/27 13:02:25 roberto Exp $
 ** Lexical Analyzer
 ** See Copyright Notice in lV.h
 */


#include <ctype.h>
#include <locale.h>
#include <string.h>

#define llex_c
#define LV_CORE

#include "lV.h"

#include "lVdo.h"
#include "lVlex.h"
#include "lVobject.h"
#include "lVparser.h"
#include "lVstate.h"
#include "lVstring.h"
#include "lVtable.h"
#include "lVzio.h"



#define next(ls) (ls->current = zgetc(ls->z))




#define currIsNewline(ls)	(ls->current == '\n' || ls->current == '\r')


/* ORDER RESERVED */
const char *const lvX_tokens [] = {
    "and", "break", "do", "else", "elseif",
    "end", "false", "for", "function", "if",
    "in", "local", "nil", "not", "or", "repeat",
    "return", "then", "true", "until", "while",
    "..", "...", "==", ">=", "<=", "~=",
    "<number>", "<name>", "<string>", "<eof>",
    NULL
};


#define save_and_next(ls) (save(ls, ls->current), next(ls))


static void save (LexState *ls, int c) {
    Mbuffer *b = ls->buff;
    if (b->n + 1 > b->buffsize) {
        size_t newsize;
        if (b->buffsize >= MAX_SIZET/2)
            lvX_lexerror(ls, "lexical element too long", 0);
        newsize = b->buffsize * 2;
        lvZ_resizebuffer(ls->L, b, newsize);
    }
    b->buffer[b->n++] = cast(char, c);
}


void lvX_init (lv_State *L) {
    int i;
    for (i=0; i<NUM_RESERVED; i++) {
        TString *ts = lvS_new(L, lvX_tokens[i]);
        lvS_fix(ts);  /* reserved words are never collected */
        lv_assert(strlen(lvX_tokens[i])+1 <= TOKEN_LEN);
        ts->tsv.reserved = cast_byte(i+1);  /* reserved word */
    }
}


#define MAXSRC          80


const char *lvX_token2str (LexState *ls, int token) {
    if (token < FIRST_RESERVED) {
        lv_assert(token == cast(unsigned char, token));
        return (iscntrl(token)) ? lvO_pushfstring(ls->L, "char(%d)", token) :
        lvO_pushfstring(ls->L, "%c", token);
    }
    else
        return lvX_tokens[token-FIRST_RESERVED];
}


static const char *txtToken (LexState *ls, int token) {
    switch (token) {
        case TK_NAME:
        case TK_STRING:
        case TK_NUMBER:
            save(ls, '\0');
            return lvZ_buffer(ls->buff);
        default:
            return lvX_token2str(ls, token);
    }
}


void lvX_lexerror (LexState *ls, const char *msg, int token) {
    char buff[MAXSRC];
    lvO_chunkid(buff, getstr(ls->source), MAXSRC);
    msg = lvO_pushfstring(ls->L, "%s:%d: %s", buff, ls->linenumber, msg);
    if (token)
        lvO_pushfstring(ls->L, "%s near " LV_QS, msg, txtToken(ls, token));
    lvD_throw(ls->L, LV_ERRSYNTAX);
}


void lvX_syntaxerror (LexState *ls, const char *msg) {
    lvX_lexerror(ls, msg, ls->t.token);
}


TString *lvX_newstring (LexState *ls, const char *str, size_t l) {
    lv_State *L = ls->L;
    TString *ts = lvS_newlstr(L, str, l);
    TValue *o = lvH_setstr(L, ls->fs->h, ts);  /* entry for `str' */
    if (ttisnil(o))
        setbvalue(o, 1);  /* make sure `str' will not be collected */
    return ts;
}


static void inclinenumber (LexState *ls) {
    int old = ls->current;
    lv_assert(currIsNewline(ls));
    next(ls);  /* skip `\n' or `\r' */
    if (currIsNewline(ls) && ls->current != old)
        next(ls);  /* skip `\n\r' or `\r\n' */
    if (++ls->linenumber >= MAX_INT)
        lvX_syntaxerror(ls, "chunk has too many lines");
}


void lvX_setinput (lv_State *L, LexState *ls, ZIO *z, TString *source) {
    ls->decpoint = '.';
    ls->L = L;
    ls->lookahead.token = TK_EOS;  /* no look-ahead token */
    ls->lookahead2.token = TK_EOS;  /* no look-ahead token */
    ls->z = z;
    ls->fs = NULL;
    ls->linenumber = 1;
    ls->lastline = 1;
    ls->source = source;
    lvZ_resizebuffer(ls->L, ls->buff, LV_MINBUFFER);  /* initialize buffer */
    next(ls);  /* read first char */
}



/*
 ** =======================================================
 ** LEXICAL ANALYZER
 ** =======================================================
 */



static int check_next (LexState *ls, const char *set) {
    if (!strchr(set, ls->current))
        return 0;
    save_and_next(ls);
    return 1;
}


static void buffreplace (LexState *ls, char from, char to) {
    size_t n = lvZ_bufflen(ls->buff);
    char *p = lvZ_buffer(ls->buff);
    while (n--)
        if (p[n] == from) p[n] = to;
}


static void trydecpoint (LexState *ls, SemInfo *seminfo) {
    /* format error: try to update decimal point separator */
    struct lconv *cv = localeconv();
    char old = ls->decpoint;
    ls->decpoint = (cv ? cv->decimal_point[0] : '.');
    buffreplace(ls, old, ls->decpoint);  /* try updated decimal separator */
    if (!lvO_str2d(lvZ_buffer(ls->buff), &seminfo->r)) {
        /* format error with correct decimal point: no more options */
        buffreplace(ls, ls->decpoint, '.');  /* undo change (for error message) */
        lvX_lexerror(ls, "malformed number", TK_NUMBER);
    }
}


/* LV_NUMBER */
static void read_numeral (LexState *ls, SemInfo *seminfo) {
    lv_assert(isdigit(ls->current));
    do {
        save_and_next(ls);
    } while (isdigit(ls->current) || ls->current == '.');
    if (check_next(ls, "Ee"))  /* `E'? */
        check_next(ls, "+-");  /* optional exponent sign */
    while (isalnum(ls->current) || ls->current == '_')
        save_and_next(ls);
    save(ls, '\0');
    buffreplace(ls, '.', ls->decpoint);  /* follow locale for decimal point */
    if (!lvO_str2d(lvZ_buffer(ls->buff), &seminfo->r))  /* format error? */
        trydecpoint(ls, seminfo); /* try to update decimal point separator */
}


static int skip_sep (LexState *ls) {
    int count = 0;
    int s = ls->current;
    lv_assert(s == '[' || s == ']');
    save_and_next(ls);
    while (ls->current == '=') {
        save_and_next(ls);
        count++;
    }
    return (ls->current == s) ? count : (-count) - 1;
}


static void read_long_string (LexState *ls, SemInfo *seminfo, int sep) {
    int cont = 0;
    (void)(cont);  /* avoid warnings when `cont' is not used */
    save_and_next(ls);  /* skip 2nd `[' */
    if (currIsNewline(ls))  /* string starts with a newline? */
        inclinenumber(ls);  /* skip it */
    for (;;) {
        switch (ls->current) {
            case EOZ:
                lvX_lexerror(ls, (seminfo) ? "unfinished long string" :
                             "unfinished long comment", TK_EOS);
                break;  /* to avoid warnings */
#if defined(LV_COMPAT_LSTR)
            case '[': {
                if (skip_sep(ls) == sep) {
                    save_and_next(ls);  /* skip 2nd `[' */
                    cont++;
#if LV_COMPAT_LSTR == 1
                    if (sep == 0)
                        lvX_lexerror(ls, "nesting of [[...]] is deprecated", '[');
#endif
                }
                break;
            }
#endif
            case ']': {
                if (skip_sep(ls) == sep) {
                    save_and_next(ls);  /* skip 2nd `]' */
#if defined(LV_COMPAT_LSTR) && LV_COMPAT_LSTR == 2
                    cont--;
                    if (sep == 0 && cont >= 0) break;
#endif
                    goto endloop;
                }
                break;
            }
            case '\n':
            case '\r': {
                save(ls, '\n');
                inclinenumber(ls);
                if (!seminfo) lvZ_resetbuffer(ls->buff);  /* avoid wasting space */
                break;
            }
            default: {
                if (seminfo) save_and_next(ls);
                else next(ls);
            }
        }
    } endloop:
    if (seminfo)
        seminfo->ts = lvX_newstring(ls, lvZ_buffer(ls->buff) + (2 + sep),
                                    lvZ_bufflen(ls->buff) - 2*(2 + sep));
}


static void read_string (LexState *ls, int del, SemInfo *seminfo) {
    save_and_next(ls);
    while (ls->current != del) {
        switch (ls->current) {
            case EOZ:
                lvX_lexerror(ls, "unfinished string", TK_EOS);
                continue;  /* to avoid warnings */
            case '\n':
            case '\r':
                lvX_lexerror(ls, "unfinished string", TK_STRING);
                continue;  /* to avoid warnings */
            case '\\': {
                int c;
                next(ls);  /* do not save the `\' */
                switch (ls->current) {
                    case 'a': c = '\a'; break;
                    case 'b': c = '\b'; break;
                    case 'f': c = '\f'; break;
                    case 'n': c = '\n'; break;
                    case 'r': c = '\r'; break;
                    case 't': c = '\t'; break;
                    case 'v': c = '\v'; break;
                    case '\n':  /* go through */
                    case '\r': save(ls, '\n'); inclinenumber(ls); continue;
                    case EOZ: continue;  /* will raise an error next loop */
                    default: {
                        if (!isdigit(ls->current))
                            save_and_next(ls);  /* handles \\, \", \', and \? */
                        else {  /* \xxx */
                            int i = 0;
                            c = 0;
                            do {
                                c = 10*c + (ls->current-'0');
                                next(ls);
                            } while (++i<3 && isdigit(ls->current));
                            if (c > UCHAR_MAX)
                                lvX_lexerror(ls, "escape sequence too large", TK_STRING);
                            save(ls, c);
                        }
                        continue;
                    }
                }
                save(ls, c);
                next(ls);
                continue;
            }
            default:
                save_and_next(ls);
        }
    }
    save_and_next(ls);  /* skip delimiter */
    seminfo->ts = lvX_newstring(ls, lvZ_buffer(ls->buff) + 1,
                                lvZ_bufflen(ls->buff) - 2);
}

static int isChineseChar(int t ){
    if( t>=128 && t<255 ){
        return 1;
    }
    return 0;
}

static int llex (LexState *ls, SemInfo *seminfo) {
    lvZ_resetbuffer(ls->buff);
    for (;;) {
        switch (ls->current) {
            case '\n':
            case '\r': {
                inclinenumber(ls);
                continue;
            }
            case '/':{
                next(ls);
                if (ls->current != '/') return '/';
                /* else is a comment */
                next(ls);
                /* else short comment */
                while (!currIsNewline(ls) && ls->current != EOZ)
                    next(ls);
                continue;
            }
            case '-': {
                next(ls);
                if (ls->current != '-') return '-';
                /* else is a comment */
                next(ls);
                if (ls->current == '[') {
                    int sep = skip_sep(ls);
                    lvZ_resetbuffer(ls->buff);  /* `skip_sep' may dirty the buffer */
                    if (sep >= 0) {
                        read_long_string(ls, NULL, sep);  /* long comment */
                        lvZ_resetbuffer(ls->buff);
                        continue;
                    }
                }
                /* else short comment */
                while (!currIsNewline(ls) && ls->current != EOZ)
                    next(ls);
                continue;
            }
            case '[': {
                int sep = skip_sep(ls);
                if (sep >= 0) {
                    read_long_string(ls, seminfo, sep);
                    return TK_STRING;
                }
                else if (sep == -1) return '[';
                else lvX_lexerror(ls, "invalid long string delimiter", TK_STRING);
            }
            case '=': {
                next(ls);
                if (ls->current != '=') return '=';
                else { next(ls); return TK_EQ; }
            }
            case '<': {
                next(ls);
                if (ls->current != '=') return '<';
                else { next(ls); return TK_LE; }
            }
            case '>': {
                next(ls);
                if (ls->current != '=') return '>';
                else { next(ls); return TK_GE; }
            }
            case '!': {
                next(ls);
                if (ls->current != '=') return '!';
                else { next(ls); return TK_NE; }
            }
            case '~': {
                next(ls);
                if (ls->current != '=') return '~';
                else { next(ls); return TK_NE; }
            }
            case '"':
            case '\'': {
                read_string(ls, ls->current, seminfo);
                return TK_STRING;
            }
            case '.': {
                save_and_next(ls);
                if (check_next(ls, ".")) {
                    if (check_next(ls, "."))
                        return TK_DOTS;   /* ... */
                    else return TK_CONCAT;   /* .. */
                }
                else if (!isdigit(ls->current)) return '.';
                else {
                    read_numeral(ls, seminfo);
                    return TK_NUMBER;
                }
            }
            case EOZ: {
                return TK_EOS;
            }
            default: {
                if (isspace(ls->current)) {
                    lv_assert(!currIsNewline(ls));
                    next(ls);
                    continue;
                }
                else if (isdigit(ls->current)) {
                    read_numeral(ls, seminfo);
                    return TK_NUMBER;
                }
                else if (isalpha(ls->current) || ls->current == '_' || isChineseChar(ls->current)  ) {
                    /* identifier or reserved word */
                    TString *ts;
                    do {
                        save_and_next(ls);
                    } while (isalnum(ls->current) || ls->current == '_' || isChineseChar(ls->current) );
                    ts = lvX_newstring(ls, lvZ_buffer(ls->buff),
                                       lvZ_bufflen(ls->buff));
                    if (ts->tsv.reserved > 0)  /* reserved word? */
                        return ts->tsv.reserved - 1 + FIRST_RESERVED;
                    else {
                        seminfo->ts = ts;
                        return TK_NAME;
                    }
                }
                else {
                    int c = ls->current;
                    next(ls);
                    return c;  /* single-char tokens (+ - / ...) */
                }
            }
        }
    }
}

void printTString(Token* token){
//    if( token->token==TK_NAME ||token->token==TK_STRING ){
//        TString* s = token->seminfo.ts;
//        const char* chars = (const char*)(getstr(s));
//        
//        printf("%d [%s] ", token->token, chars );
//        int len = s->tsv.len;
//        for( int i=0;; i++){
//            if( chars[i]==0 ){
//                break;
//            }
//            printf("%d,",chars[i]);
//        }
//        printf("\n");
//        if( token->token == 61 ){
//            printf("");
//        }
//    }
}

void lvX_next (LexState *ls) {
    ls->lastline = ls->linenumber;
    if (ls->lookahead.token != TK_EOS) {  /* is there a look-ahead token? */
        ls->t = ls->lookahead;  /* use this one */
        ls->lookahead.token = TK_EOS;  /* and discharge it */
        
        ls->lookahead = ls->lookahead2;
        ls->lookahead2.token = TK_EOS;
        
        //Token* s = &ls->t;
        //printTString(s);
    }
    else {
        ls->t.token = llex(ls, &ls->t.seminfo);  /* read next token */
        //Token* s = &ls->t;
        //printTString(s);
    }
}


void lvX_lookahead (LexState *ls) {
    // lv_assert(ls->lookahead.token == TK_EOS);
    if( ls->lookahead.token == TK_EOS  ) {
        ls->lookahead.token = llex(ls, &ls->lookahead.seminfo);
    }
    if( ls->lookahead2.token == TK_EOS  ) {
        ls->lookahead2.token = llex(ls, &ls->lookahead2.seminfo);
    }
}

