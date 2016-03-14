/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.log;

import com.taobao.luaview.util.LogUtil;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Upvaldesc;
import org.luaj.vm2.Varargs;

/**
 * Debug helper class to pretty-print lua bytecodes.
 *
 * @see Prototype
 * @see LuaClosure
 */
public class LuaPrint extends Lua {

    /**
     * opcode names
     */
    private static final String STRING_FOR_NULL = "null";
    public StringBuffer ps = new StringBuffer();

    public LuaPrint() {
    }

    /**
     * String names for each lua opcode value.
     */
    public static final String[] OPNAMES = {
            "MOVE",
            "LOADK",
            "LOADKX",
            "LOADBOOL",
            "LOADNIL",
            "GETUPVAL",
            "GETTABUP",
            "GETTABLE",
            "SETTABUP",
            "SETUPVAL",
            "SETTABLE",
            "NEWTABLE",
            "SELF",
            "ADD",
            "SUB",
            "MUL",
            "DIV",
            "MOD",
            "POW",
            "UNM",
            "NOT",
            "LEN",
            "CONCAT",
            "JMP",
            "EQ",
            "LT",
            "LE",
            "TEST",
            "TESTSET",
            "CALL",
            "TAILCALL",
            "RETURN",
            "FORLOOP",
            "FORPREP",
            "TFORCALL",
            "TFORLOOP",
            "SETLIST",
            "CLOSURE",
            "VARARG",
            "EXTRAARG",
            null,
    };


    void buildString(StringBuffer ps, final LuaString s) {
        ps.append('"');
        for (int i = 0, n = s.m_length; i < n; i++) {
            int c = s.m_bytes[s.m_offset + i];
            if (c >= ' ' && c <= '~' && c != '\"' && c != '\\')
                ps.append((char) c);
            else {
                switch (c) {
                    case '"':
                        ps.append("\\\"");
                        break;
                    case '\\':
                        ps.append("\\\\");
                        break;
                    case 0x0007: /* bell */
                        ps.append("\\a");
                        break;
                    case '\b': /* backspace */
                        ps.append("\\b");
                        break;
                    case '\f':  /* form feed */
                        ps.append("\\f");
                        break;
                    case '\t':  /* tab */
                        ps.append("\\t");
                        break;
                    case '\r': /* carriage return */
                        ps.append("\\r");
                        break;
                    case '\n': /* newline */
                        ps.append("\\n");
                        break;
                    case 0x000B: /* vertical tab */
                        ps.append("\\v");
                        break;
                    default:
                        ps.append('\\');
                        ps.append(Integer.toString(1000 + 0xff & c).substring(1));
                        break;
                }
            }
        }
        ps.append('"');
    }

    void buildValue(StringBuffer ps, LuaValue v) {
        switch (v.type()) {
            case LuaValue.TSTRING:
                buildString(ps, (LuaString) v);
                break;
            default:
                ps.append(v.tojstring());

        }
    }

    void buildConstant(StringBuffer ps, Prototype f, int i) {
        buildValue(ps, f.k[i]);
    }

    void buildUpvalue(StringBuffer ps, Upvaldesc u) {
        ps.append(u.idx + " ");
        buildValue(ps, u.name);
    }

    /**
     * Print the code in a prototype
     *
     * @param f the {@link Prototype}
     */
    void buildCode(Prototype f) {
        int[] code = f.code;
        int pc, n = code.length;
        for (pc = 0; pc < n; pc++) {
            buildOpCode(f, pc);
            ps.append("\n");
        }
    }

    /**
     * Print an opcode in a prototype
     *
     * @param f  the {@link Prototype}
     * @param pc the program counter to look up and print
     */
    public LuaPrint buildOpCode(Prototype f, int pc) {
        buildOpCode(ps, f, pc);
        return this;
    }

    /**
     * Print an opcode in a prototype
     *
     * @param f  the {@link Prototype}
     * @param pc the program counter to look up and print
     */
    public LuaPrint buildOpCode(StringBuffer ps, Prototype f, int pc) {
        int[] code = f.code;
        int i = code[pc];
        int o = GET_OPCODE(i);
        int a = GETARG_A(i);
        int b = GETARG_B(i);
        int c = GETARG_C(i);
        int bx = GETARG_Bx(i);
        int sbx = GETARG_sBx(i);
        int line = getline(f, pc);
        ps.append("  " + (pc + 1) + "  ");
        if (line > 0)
            ps.append("[" + line + "]  ");
        else
            ps.append("[-]  ");
        ps.append(OPNAMES[o] + "  ");
        switch (getOpMode(o)) {
            case iABC:
                ps.append(a);
                if (getBMode(o) != OpArgN)
                    ps.append(" " + (ISK(b) ? (-1 - INDEXK(b)) : b));
                if (getCMode(o) != OpArgN)
                    ps.append(" " + (ISK(c) ? (-1 - INDEXK(c)) : c));
                break;
            case iABx:
                if (getBMode(o) == OpArgK) {
                    ps.append(a + " " + (-1 - bx));
                } else {
                    ps.append(a + " " + (bx));
                }
                break;
            case iAsBx:
                if (o == OP_JMP)
                    ps.append(sbx);
                else
                    ps.append(a + " " + sbx);
                break;
        }
        switch (o) {
            case OP_LOADK:
                ps.append("  ; ");
                buildConstant(ps, f, bx);
                break;
            case OP_GETUPVAL:
            case OP_SETUPVAL:
                ps.append("  ; ");
                buildUpvalue(ps, f.upvalues[b]);
                break;
            case OP_GETTABUP:
                ps.append("  ; ");
                buildUpvalue(ps, f.upvalues[b]);
                ps.append(" ");
                if (ISK(c))
                    buildConstant(ps, f, INDEXK(c));
                else
                    ps.append("-");
                break;
            case OP_SETTABUP:
                ps.append("  ; ");
                buildUpvalue(ps, f.upvalues[a]);
                ps.append(" ");
                if (ISK(b))
                    buildConstant(ps, f, INDEXK(b));
                else
                    ps.append("-");
                ps.append(" ");
                if (ISK(c))
                    buildConstant(ps, f, INDEXK(c));
                else
                    ps.append("-");
                break;
            case OP_GETTABLE:
            case OP_SELF:
                if (ISK(c)) {
                    ps.append("  ; ");
                    buildConstant(ps, f, INDEXK(c));
                }
                break;
            case OP_SETTABLE:
            case OP_ADD:
            case OP_SUB:
            case OP_MUL:
            case OP_DIV:
            case OP_POW:
            case OP_EQ:
            case OP_LT:
            case OP_LE:
                if (ISK(b) || ISK(c)) {
                    ps.append("  ; ");
                    if (ISK(b))
                        buildConstant(ps, f, INDEXK(b));
                    else
                        ps.append("-");
                    ps.append(" ");
                    if (ISK(c))
                        buildConstant(ps, f, INDEXK(c));
                    else
                        ps.append("-");
                }
                break;
            case OP_JMP:
            case OP_FORLOOP:
            case OP_FORPREP:
                ps.append("  ; to " + (sbx + pc + 2));
                break;
            case OP_CLOSURE:
                ps.append("  ; " + f.p[bx].getClass().getName());
                break;
            case OP_SETLIST:
                if (c == 0)
                    ps.append("  ; " + ((int) code[++pc]));
                else
                    ps.append("  ; " + ((int) c));
                break;
            case OP_VARARG:
                ps.append("  ; is_vararg=" + f.is_vararg);
                break;
            default:
                break;
        }
        return this;
    }

    int getline(Prototype f, int pc) {
        return pc > 0 && f.lineinfo != null && pc < f.lineinfo.length ? f.lineinfo[pc] : -1;
    }

    void buildHeader(Prototype f) {
        String s = String.valueOf(f.source);
        if (s.startsWith("@") || s.startsWith("="))
            s = s.substring(1);
        else if ("\033Lua".equals(s))
            s = "(bstring)";
        else
            s = "(string)";
        String a = (f.linedefined == 0) ? "main" : "function";
        ps.append("\n%" + a + " <" + s + ":" + f.linedefined + ","
                + f.lastlinedefined + "> (" + f.code.length + " instructions, "
                + f.code.length * 4 + " bytes at " + id(f) + ")\n");
        ps.append(f.numparams + " param, " + f.maxstacksize + " slot, "
                + f.upvalues.length + " upvalue, ");
        ps.append(f.locvars.length + " local, " + f.k.length
                + " constant, " + f.p.length + " function\n");
    }

    void buildConstants(Prototype f) {
        int i, n = f.k.length;
        ps.append("constants (" + n + ") for " + id(f) + ":\n");
        for (i = 0; i < n; i++) {
            ps.append("  " + (i + 1) + "  ");
            buildValue(ps, f.k[i]);
            ps.append("\n");
        }
    }

    void buildLocals(Prototype f) {
        int i, n = f.locvars.length;
        ps.append("locals (" + n + ") for " + id(f) + ":\n");
        for (i = 0; i < n; i++) {
            ps.append("  " + i + "  " + f.locvars[i].varname + " " + (f.locvars[i].startpc + 1) + " " + (f.locvars[i].endpc + 1));
        }
    }

    void buildUpValues(Prototype f) {
        int i, n = f.upvalues.length;
        ps.append("upvalues (" + n + ") for " + id(f) + ":\n");
        for (i = 0; i < n; i++) {
            ps.append("  " + i + "  " + f.upvalues[i] + "\n");
        }
    }

    /**
     * Pretty-prints contents of a Prototype.
     *
     * @param prototype Prototype to print.
     */
    void print(Prototype prototype) {
        buildFunction(prototype, true);
    }

    /**
     * Pretty-prints contents of a Prototype in short or long form.
     *
     * @param prototype Prototype to print.
     * @param full      true to print all fields, false to print short form.
     */
    void buildFunction(Prototype prototype, boolean full) {
        int i, n = prototype.p.length;
        buildHeader(prototype);
        buildCode(prototype);
        if (full) {
            buildConstants(prototype);
            buildLocals(prototype);
            buildUpValues(prototype);
        }
        for (i = 0; i < n; i++)
            buildFunction(prototype.p[i], full);
    }

    void format(String s, int maxcols) {
        int n = s.length();
        if (n > maxcols)
            ps.append(s.substring(0, maxcols));
        else {
            ps.append(s);
            for (int i = maxcols - n; --i >= 0; )
                ps.append(' ');
        }
    }

    String id(Prototype f) {
        return "Proto";
    }

    void _assert(boolean b) {
        if (!b)
            throw new NullPointerException("_assert failed");
    }

    /**
     * Print the state of a {@link LuaClosure} that is being executed
     *
     * @param cl      the {@link LuaClosure}
     * @param pc      the program counter
     * @param stack   the stack of {@link LuaValue}
     * @param top     the top of the stack
     * @param varargs any {@link Varargs} value that may apply
     */
    public LuaPrint buildState(LuaClosure cl, int pc, LuaValue[] stack, int top, Varargs varargs) {
        // print opcode into buffer
        StringBuffer previous = ps;
        ps = new StringBuffer();
        buildOpCode(cl.p, pc);

        LogUtil.i(ps);

        ps = previous;
        format(ps.toString(), 50);

        // print stack
        ps.append('[');
        for (int i = 0; i < stack.length; i++) {
            LuaValue v = stack[i];
            if (v == null)
                ps.append(STRING_FOR_NULL);
            else switch (v.type()) {
                case LuaValue.TSTRING:
                    LuaString s = v.checkstring();
                    ps.append(s.length() < 48 ?
                            s.tojstring() :
                            s.substring(0, 32).tojstring() + "...+" + (s.length() - 32) + "b");
                    break;
                case LuaValue.TFUNCTION:
                    ps.append(v.tojstring());
                    break;
                case LuaValue.TUSERDATA:
                    Object o = v.touserdata();
                    if (o != null) {
                        String n = o.getClass().getName();
                        n = n.substring(n.lastIndexOf('.') + 1);
                        ps.append(n + ": " + Integer.toHexString(o.hashCode()));
                    } else {
                        ps.append(v.toString());
                    }
                    break;
                default:
                    ps.append(v.tojstring());
            }
            if (i + 1 == top)
                ps.append(']');
            ps.append(" | ");
        }
        ps.append(varargs);
        ps.append("\n");
        return this;
    }


    public void print() {
        LogUtil.i(ps);
    }


    @Override
    public String toString() {
        return ps != null ? ps.toString() : super.toString();
    }
}
