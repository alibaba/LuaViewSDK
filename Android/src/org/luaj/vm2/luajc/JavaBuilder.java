/*******************************************************************************
* Copyright (c) 2010 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package org.luaj.vm2.luajc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.AASTORE;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;
import org.luaj.vm2.Buffer;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class JavaBuilder {
	
	private static final String STR_VARARGS = Varargs.class.getName();
	private static final String STR_LUAVALUE = LuaValue.class.getName();
	private static final String STR_LUASTRING = LuaString.class.getName();
	private static final String STR_LUAINTEGER = LuaInteger.class.getName();
	private static final String STR_LUANUMBER = LuaNumber.class.getName();
	private static final String STR_LUABOOLEAN = LuaBoolean.class.getName();
	private static final String STR_LUATABLE = LuaTable.class.getName();
	private static final String STR_BUFFER = Buffer.class.getName();
	private static final String STR_STRING = String.class.getName();
	private static final String STR_JSEPLATFORM = "org.luaj.vm2.lib.jse.JsePlatform";

	private static final ObjectType TYPE_VARARGS = new ObjectType(STR_VARARGS);
	private static final ObjectType TYPE_LUAVALUE = new ObjectType(STR_LUAVALUE);
	private static final ObjectType TYPE_LUASTRING = new ObjectType(STR_LUASTRING);
	private static final ObjectType TYPE_LUAINTEGER = new ObjectType(STR_LUAINTEGER);
	private static final ObjectType TYPE_LUANUMBER = new ObjectType(STR_LUANUMBER);
	private static final ObjectType TYPE_LUABOOLEAN = new ObjectType(STR_LUABOOLEAN);
	private static final ObjectType TYPE_LUATABLE = new ObjectType(STR_LUATABLE);
	private static final ObjectType TYPE_BUFFER = new ObjectType(STR_BUFFER);
	private static final ObjectType TYPE_STRING = new ObjectType(STR_STRING);
	
	private static final ArrayType TYPE_LOCALUPVALUE = new ArrayType( TYPE_LUAVALUE, 1 );
	private static final ArrayType TYPE_CHARARRAY = new ArrayType( Type.CHAR, 1 );
	private static final ArrayType TYPE_STRINGARRAY = new ArrayType( TYPE_STRING, 1 );


	private static final String STR_FUNCV = VarArgFunction.class.getName();
	private static final String STR_FUNC0 = ZeroArgFunction.class.getName();
	private static final String STR_FUNC1 = OneArgFunction.class.getName();
	private static final String STR_FUNC2 = TwoArgFunction.class.getName();
	private static final String STR_FUNC3 = ThreeArgFunction.class.getName();

	// argument list types
	private static final Type[] ARG_TYPES_NONE = {};
	private static final Type[] ARG_TYPES_INT =  { Type.INT };
	private static final Type[] ARG_TYPES_DOUBLE = { Type.DOUBLE };
	private static final Type[] ARG_TYPES_STRING = { Type.STRING };
	private static final Type[] ARG_TYPES_CHARARRAY = { TYPE_CHARARRAY };
	private static final Type[] ARG_TYPES_INT_LUAVALUE = { Type.INT, TYPE_LUAVALUE };
	private static final Type[] ARG_TYPES_INT_VARARGS = { Type.INT, TYPE_VARARGS };
	private static final Type[] ARG_TYPES_LUAVALUE_VARARGS = { TYPE_LUAVALUE, TYPE_VARARGS };
	private static final Type[] ARG_TYPES_LUAVALUE_LUAVALUE_VARARGS = { TYPE_LUAVALUE, TYPE_LUAVALUE, TYPE_VARARGS };
	private static final Type[] ARG_TYPES_LUAVALUEARRAY = { new ArrayType( TYPE_LUAVALUE, 1 ) };
	private static final Type[] ARG_TYPES_LUAVALUEARRAY_VARARGS = { new ArrayType( TYPE_LUAVALUE, 1 ), TYPE_VARARGS };
	private static final Type[] ARG_TYPES_LUAVALUE_LUAVALUE_LUAVALUE = { TYPE_LUAVALUE, TYPE_LUAVALUE, TYPE_LUAVALUE };
	private static final Type[] ARG_TYPES_VARARGS = { TYPE_VARARGS };
	private static final Type[] ARG_TYPES_LUAVALUE_LUAVALUE = { TYPE_LUAVALUE, TYPE_LUAVALUE };
	private static final Type[] ARG_TYPES_INT_INT = { Type.INT, Type.INT };
	private static final Type[] ARG_TYPES_LUAVALUE = { TYPE_LUAVALUE };
	private static final Type[] ARG_TYPES_BUFFER = { TYPE_BUFFER };
	private static final Type[] ARG_TYPES_STRINGARRAY = { TYPE_STRINGARRAY };
	private static final Type[] ARG_TYPES_LUAVALUE_STRINGARRAY = { TYPE_LUAVALUE, TYPE_STRINGARRAY };

	// names, arg types for main prototype classes
	private static final String[]     SUPER_NAME_N   = { STR_FUNC0, STR_FUNC1, STR_FUNC2, STR_FUNC3, STR_FUNCV, };
	private static final ObjectType[] RETURN_TYPE_N  = { TYPE_LUAVALUE, TYPE_LUAVALUE, TYPE_LUAVALUE, TYPE_LUAVALUE, TYPE_VARARGS, };
	private static final Type[][]     ARG_TYPES_N    = { ARG_TYPES_NONE, ARG_TYPES_LUAVALUE, ARG_TYPES_LUAVALUE_LUAVALUE, ARG_TYPES_LUAVALUE_LUAVALUE_LUAVALUE, ARG_TYPES_VARARGS,  };
	private static final String[][]   ARG_NAMES_N    = { {}, {"arg"}, {"arg1","arg2"}, {"arg1","arg2","arg3"}, {"args"}, };
	private static final String[]     METH_NAME_N    = { "call", "call", "call", "call", "onInvoke", };
	
	
	
	// varable naming
	private static final String PREFIX_CONSTANT     = "k";
	private static final String PREFIX_UPVALUE      = "u";
	private static final String PREFIX_PLAIN_SLOT   = "s";
	private static final String PREFIX_UPVALUE_SLOT = "a";
	private static final String NAME_VARRESULT      = "v";
	
	// basic info
	private final ProtoInfo pi;
	private final Prototype p;
	private final String classname;
	
	// bcel variables
	private final ClassGen cg;
	private final ConstantPoolGen cp;
	private final InstructionFactory factory;
	
	// main instruction list for the main function of this class
	private final InstructionList init;
	private final InstructionList main;
	private final MethodGen mg;
	
	// the superclass arg count, 0-3 args, 4=varargs
	private int superclassType;
	private static int SUPERTYPE_VARARGS = 4;
	
	// storage for goto locations
	private final int[] targets;
	private final BranchInstruction[] branches;
	private final InstructionHandle[] branchDestHandles;
	private final InstructionHandle[] lastInstrHandles;
	private InstructionHandle beginningOfLuaInstruction;
	
	// hold vararg result
	private LocalVariableGen varresult = null;
	private int prev_line = -1;
	
	public JavaBuilder(ProtoInfo pi, String classname, String filename) {
		this.pi = pi;
		this.p = pi.prototype;
		this.classname = classname;
		
		// what class to inherit from
		superclassType = p.numparams;
		if ( p.is_vararg != 0 || superclassType >= SUPERTYPE_VARARGS )
			superclassType = SUPERTYPE_VARARGS;
		for ( int i=0, n=p.code.length; i<n; i++ ) {
			int inst = p.code[i];
			int o = Lua.GET_OPCODE(inst);
			if ( (o == Lua.OP_TAILCALL) ||
			     ((o == Lua.OP_RETURN) && (Lua.GETARG_B(inst) < 1 || Lua.GETARG_B(inst) > 2)) ) {
				superclassType = SUPERTYPE_VARARGS;
				break;
			}
		}
		
		// create class generator
		cg = new ClassGen(classname, SUPER_NAME_N[superclassType], filename,
				Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);
		cp = cg.getConstantPool(); // cg creates constant pool

		// main instruction lists
		factory = new InstructionFactory(cg);
		init = new InstructionList();
		main = new InstructionList();

		// create the fields
		for ( int i=0; i<p.upvalues.length; i++ ) {
			boolean isrw = pi.isReadWriteUpvalue( pi.upvals[i] ); 
			Type uptype = isrw? (Type) TYPE_LOCALUPVALUE: (Type) TYPE_LUAVALUE;
			FieldGen fg = new FieldGen(0, uptype, upvalueName(i), cp);
			cg.addField(fg.getField());
		}
		
		// create the method
		mg = new MethodGen( Constants.ACC_PUBLIC | Constants.ACC_FINAL, // access flags
				RETURN_TYPE_N[superclassType], // return type
				ARG_TYPES_N[superclassType], // argument types
				ARG_NAMES_N[superclassType], // arg names
				METH_NAME_N[superclassType], 
				STR_LUAVALUE, // method, defining class
				main, cp);
		
		// initialize the values in the slots
		initializeSlots();	

		// initialize branching
		int nc = p.code.length;
		targets = new int[nc];
		branches = new BranchInstruction[nc];
		branchDestHandles = new InstructionHandle[nc];
		lastInstrHandles = new InstructionHandle[nc];
	}
	
	public void initializeSlots() {
		int slot = 0;
		createUpvalues(-1, 0, p.maxstacksize);
		if ( superclassType == SUPERTYPE_VARARGS ) {
			for ( slot=0; slot<p.numparams; slot++ ) {
				if ( pi.isInitialValueUsed(slot) ) {
					append(new ALOAD(1));
					append(new PUSH(cp, slot+1));
					append(factory.createInvoke(STR_VARARGS, "arg", TYPE_LUAVALUE, ARG_TYPES_INT, Constants.INVOKEVIRTUAL));
					storeLocal(-1, slot);
				}
			}
			append(new ALOAD(1));
			append(new PUSH(cp, 1 + p.numparams));
			append(factory.createInvoke(STR_VARARGS, "subargs", TYPE_VARARGS, ARG_TYPES_INT, Constants.INVOKEVIRTUAL));
			append(new ASTORE(1));
		} else {
			// fixed arg function between 0 and 3 arguments
			for ( slot=0; slot<p.numparams; slot++ ) {
				this.plainSlotVars.put( Integer.valueOf(slot), Integer.valueOf(1+slot) );
				if ( pi.isUpvalueCreate(-1, slot) ) {
					append(new ALOAD(1+slot));
					storeLocal(-1, slot);
				}
			}
		}
		
		// nil parameters 
		// TODO: remove this for lua 5.2, not needed
		for ( ; slot<p.maxstacksize; slot++ ) {
			if ( pi.isInitialValueUsed(slot) ) {
				loadNil();
				storeLocal(-1, slot);
			}
		}		
	}
	
	public byte[] completeClass(boolean genmain) {

		// add class initializer 
		if ( ! init.isEmpty() ) {
			MethodGen mg = new MethodGen(Constants.ACC_STATIC, Type.VOID,
					ARG_TYPES_NONE, new String[] {}, "<clinit>", 
					cg.getClassName(), init, cg.getConstantPool());
			init.append(InstructionConstants.RETURN);
			mg.setMaxStack();
			cg.addMethod(mg.getMethod());
			init.dispose();
		}

		// add default constructor
		cg.addEmptyConstructor(Constants.ACC_PUBLIC);
		
		// gen method
		resolveBranches();
		mg.setMaxStack();
		cg.addMethod(mg.getMethod());
		main.dispose();

		// add initupvalue1(LuaValue env) to initialize environment for main chunk 
		if (p.upvalues.length == 1 && superclassType == SUPERTYPE_VARARGS) {
			MethodGen mg = new MethodGen( Constants.ACC_PUBLIC | Constants.ACC_FINAL, // access flags
					Type.VOID, // return type
					ARG_TYPES_LUAVALUE, // argument types
					new String[] { "env" }, // arg names
					"initupvalue1", 
					STR_LUAVALUE, // method, defining class
					main, cp);
			boolean isrw = pi.isReadWriteUpvalue( pi.upvals[0] ); 
			append(InstructionConstants.THIS);
			append(new ALOAD(1));
			if ( isrw ) {
				append(factory.createInvoke(classname, "newupl", TYPE_LOCALUPVALUE,  ARG_TYPES_LUAVALUE, Constants.INVOKESTATIC));
				append(factory.createFieldAccess(classname, upvalueName(0), TYPE_LOCALUPVALUE, Constants.PUTFIELD));
			} else {
				append(factory.createFieldAccess(classname, upvalueName(0), TYPE_LUAVALUE, Constants.PUTFIELD));
			}
			append(InstructionConstants.RETURN);
			mg.setMaxStack();
			cg.addMethod(mg.getMethod());
			main.dispose();
		}
		
		// add main function so class is invokable from the java command line 
		if (genmain) {
			MethodGen mg = new MethodGen( Constants.ACC_PUBLIC | Constants.ACC_STATIC, // access flags
					Type.VOID, // return type
					ARG_TYPES_STRINGARRAY, // argument types
					new String[] { "arg" }, // arg names
					"main", 
					classname, // method, defining class
					main, cp);
			append(factory.createNew(classname));
			append(InstructionConstants.DUP);
            append(factory.createInvoke(classname, Constants.CONSTRUCTOR_NAME, Type.VOID, ARG_TYPES_NONE, Constants.INVOKESPECIAL));
			append(new ALOAD(0));
			append(factory.createInvoke(STR_JSEPLATFORM, "luaMain", Type.VOID,  ARG_TYPES_LUAVALUE_STRINGARRAY, Constants.INVOKESTATIC));
			append(InstructionConstants.RETURN);
			mg.setMaxStack();
			cg.addMethod(mg.getMethod());
			main.dispose();
		}
		

		// convert to class bytes
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			cg.getJavaClass().dump(baos);
			return baos.toByteArray();
		} catch ( IOException ioe ) {
			throw new RuntimeException("JavaClass.dump() threw "+ioe);
		}
	}

	public void dup() {
		append(InstructionConstants.DUP);
	}

	public void pop() {
		append(InstructionConstants.POP);
	}

	public void loadNil() {
		append(factory.createFieldAccess(STR_LUAVALUE, "NIL", TYPE_LUAVALUE, Constants.GETSTATIC));
	}
	
	public void loadNone() {
		append(factory.createFieldAccess(STR_LUAVALUE, "NONE", TYPE_LUAVALUE, Constants.GETSTATIC));
	}

	public void loadBoolean(boolean b) {
		String field = (b? "TRUE": "FALSE");
		append(factory.createFieldAccess(STR_LUAVALUE, field, TYPE_LUABOOLEAN, Constants.GETSTATIC));
	}
	
	private Map<Integer,Integer> plainSlotVars = new HashMap<Integer,Integer>();
	private Map<Integer,Integer> upvalueSlotVars = new HashMap<Integer,Integer>();
	private Map<Integer,LocalVariableGen> localVarGenBySlot = new HashMap<Integer,LocalVariableGen>();
	private int findSlot( int slot, Map<Integer,Integer> map, String prefix, Type type ) {
		Integer islot = Integer.valueOf(slot);
		if ( map.containsKey(islot) )
			return ((Integer)map.get(islot)).intValue();
		String name = prefix+slot;
		LocalVariableGen local = mg.addLocalVariable(name, type, null, null);
		int index = local.getIndex();
		map.put(islot, Integer.valueOf(index));
		localVarGenBySlot.put(islot, local);
		return index;
	}
	private int findSlotIndex( int slot, boolean isupvalue ) {
		return isupvalue? 
				findSlot( slot, upvalueSlotVars, PREFIX_UPVALUE_SLOT, TYPE_LOCALUPVALUE ):
				findSlot( slot, plainSlotVars, PREFIX_PLAIN_SLOT, TYPE_LUAVALUE );
	}

	public void loadLocal(int pc, int slot) {
		boolean isupval = pi.isUpvalueRefer(pc, slot);
		int index = findSlotIndex( slot, isupval );
		append(new ALOAD(index));
		if (isupval) {
			append(new PUSH(cp, 0));
			append(InstructionConstants.AALOAD);
		}
	}

	public void storeLocal(int pc, int slot) {
		boolean isupval = pi.isUpvalueAssign(pc, slot);
		int index = findSlotIndex( slot, isupval );
		if (isupval) {
			boolean isupcreate = pi.isUpvalueCreate(pc, slot);
			if ( isupcreate ) {
				append(factory.createInvoke(classname, "newupe", TYPE_LOCALUPVALUE, ARG_TYPES_NONE, Constants.INVOKESTATIC));
				append(InstructionConstants.DUP);
				append(new ASTORE(index));
			} else {
				append(new ALOAD(index));
			}
			append(InstructionConstants.SWAP);
			append(new PUSH(cp, 0));
			append(InstructionConstants.SWAP);
			append(InstructionConstants.AASTORE);
		} else {
			append(new ASTORE(index));
		}
	}

	public void createUpvalues(int pc, int firstslot, int numslots) {
		for ( int i=0; i<numslots; i++ ) {
			int slot = firstslot + i;
			boolean isupcreate = pi.isUpvalueCreate(pc, slot);
			if ( isupcreate ) {
				int index = findSlotIndex( slot, true );
				append(factory.createInvoke(classname, "newupn", TYPE_LOCALUPVALUE, ARG_TYPES_NONE, Constants.INVOKESTATIC));
				append(new ASTORE(index));
			}
		}
	}

	public void convertToUpvalue(int pc, int slot) {
		boolean isupassign = pi.isUpvalueAssign(pc, slot);
		if ( isupassign ) {
			int index = findSlotIndex( slot, false );
			append(new ALOAD(index));
			append(factory.createInvoke(classname, "newupl", TYPE_LOCALUPVALUE,  ARG_TYPES_LUAVALUE, Constants.INVOKESTATIC));
			int upindex = findSlotIndex( slot, true );
			append(new ASTORE(upindex));
		}
	}
	
	private static String upvalueName(int upindex) {
		return PREFIX_UPVALUE+upindex;
	}
	
	public void loadUpvalue(int upindex) {
		boolean isrw = pi.isReadWriteUpvalue( pi.upvals[upindex] ); 
		append(InstructionConstants.THIS);
		if ( isrw ) {
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LOCALUPVALUE, Constants.GETFIELD));
			append(new PUSH(cp,0));
			append(InstructionConstants.AALOAD);
		} else {
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LUAVALUE, Constants.GETFIELD));
		}
	}

	public void storeUpvalue(int pc, int upindex, int slot) {
		boolean isrw = pi.isReadWriteUpvalue( pi.upvals[upindex] ); 
		append(InstructionConstants.THIS);
		if ( isrw ) {
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LOCALUPVALUE, Constants.GETFIELD));
			append(new PUSH(cp,0));
			loadLocal(pc, slot);
			append(InstructionConstants.AASTORE);
		} else {
			loadLocal(pc, slot);
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LUAVALUE, Constants.PUTFIELD));
		}
	}

	
	public void newTable( int b, int c ) {
		append(new PUSH(cp, b));
		append(new PUSH(cp, c));
		append(factory.createInvoke(STR_LUAVALUE, "tableOf", TYPE_LUATABLE, ARG_TYPES_INT_INT, Constants.INVOKESTATIC));
	}

	public void loadVarargs() {
		append(new ALOAD(1));
	}
	
	public void loadVarargs(int argindex) {
		loadVarargs();
		arg(argindex);
	}

	public void arg(int argindex) {
		if ( argindex == 1 ) {
			append(factory.createInvoke(STR_VARARGS, "arg1", TYPE_LUAVALUE, ARG_TYPES_NONE, Constants.INVOKEVIRTUAL));
		} else {
			append(new PUSH(cp, argindex));
			append(factory.createInvoke(STR_VARARGS, "arg", TYPE_LUAVALUE, ARG_TYPES_INT, Constants.INVOKEVIRTUAL));
		}
	}

	private int getVarresultIndex() {
		if ( varresult == null )
			varresult = mg.addLocalVariable(NAME_VARRESULT, TYPE_VARARGS, null, null);
		return varresult.getIndex();
	}
	
	public void loadVarresult() {
		append(new ALOAD(getVarresultIndex()));
	}
	
	public void storeVarresult() {
		append(new ASTORE(getVarresultIndex()));
	}

	public void subargs(int firstarg) {
		append(new PUSH(cp, firstarg));
		append(factory.createInvoke(STR_VARARGS, "subargs", TYPE_VARARGS, ARG_TYPES_INT, Constants.INVOKEVIRTUAL));
	}
	
	public void getTable() {
        append(factory.createInvoke(STR_LUAVALUE, "get", TYPE_LUAVALUE, ARG_TYPES_LUAVALUE, Constants.INVOKEVIRTUAL));
	}
	
	public void setTable() {
        append(factory.createInvoke(STR_LUAVALUE, "set", Type.VOID, ARG_TYPES_LUAVALUE_LUAVALUE, Constants.INVOKEVIRTUAL));
	}

	public void unaryop(int o) {
		String op;
		switch (o) {
			default:
			case Lua.OP_UNM: op = "neg"; break;
			case Lua.OP_NOT: op = "not"; break;
			case Lua.OP_LEN: op = "len"; break;
		}
        append(factory.createInvoke(STR_LUAVALUE, op, TYPE_LUAVALUE, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}
	
	public void binaryop(int o) {
		String op;
		switch (o) {
			default: 
			case Lua.OP_ADD: op = "add"; break;
			case Lua.OP_SUB: op = "sub"; break;
			case Lua.OP_MUL: op = "mul"; break;
			case Lua.OP_DIV: op = "div"; break;
			case Lua.OP_MOD: op = "mod"; break;
			case Lua.OP_POW: op = "pow"; break;
		}
        append(factory.createInvoke(STR_LUAVALUE, op, TYPE_LUAVALUE, ARG_TYPES_LUAVALUE, Constants.INVOKEVIRTUAL));
	}

	public void compareop(int o) {
		String op;
		switch (o) {
			default: 
			case Lua.OP_EQ: op = "eq_b"; break;
			case Lua.OP_LT: op = "lt_b"; break;
			case Lua.OP_LE: op = "lteq_b"; break;
		}
        append(factory.createInvoke(STR_LUAVALUE, op, Type.BOOLEAN, ARG_TYPES_LUAVALUE, Constants.INVOKEVIRTUAL));
	}

	public void areturn() {
		append(InstructionConstants.ARETURN);
	}
	
	public void toBoolean() {
        append(factory.createInvoke(STR_LUAVALUE, "toboolean", Type.BOOLEAN, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}

	public void tostring() {
        append(factory.createInvoke(STR_BUFFER, "tostring", TYPE_LUASTRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}

	public void isNil() {
        append(factory.createInvoke(STR_LUAVALUE, "isnil", Type.BOOLEAN, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}

	public void testForLoop() {
		append(factory.createInvoke(STR_LUAVALUE, "testfor_b", Type.BOOLEAN, ARG_TYPES_LUAVALUE_LUAVALUE, Constants.INVOKEVIRTUAL));
	}

	public void loadArrayArgs(int pc, int firstslot, int nargs) {
		append(new PUSH(cp, nargs));
		append(new ANEWARRAY(cp.addClass(STR_LUAVALUE)));
		for ( int i=0; i<nargs; i++ ) {
			append(InstructionConstants.DUP);
			append(new PUSH(cp, i));
			loadLocal(pc, firstslot++);
			append(new AASTORE());
		}	
	}
	
	public void newVarargs(int pc, int firstslot, int nargs) {
		switch ( nargs ) {
		case 0: loadNone(); 
			break;
		case 1: loadLocal(pc, firstslot); 
			break;
		case 2: loadLocal(pc, firstslot); loadLocal(pc, firstslot+1); 
			append(factory.createInvoke(STR_LUAVALUE, "varargsOf", TYPE_VARARGS, ARG_TYPES_LUAVALUE_VARARGS, Constants.INVOKESTATIC));
			break;
		case 3: loadLocal(pc, firstslot); loadLocal(pc, firstslot+1); loadLocal(pc, firstslot+2); 
			append(factory.createInvoke(STR_LUAVALUE, "varargsOf", TYPE_VARARGS, ARG_TYPES_LUAVALUE_LUAVALUE_VARARGS, Constants.INVOKESTATIC));
			break;
		default:
			loadArrayArgs(pc, firstslot, nargs);
			append(factory.createInvoke(STR_LUAVALUE, "varargsOf", TYPE_VARARGS, ARG_TYPES_LUAVALUEARRAY, Constants.INVOKESTATIC));
			break;
		}
	}

	public void newVarargsVarresult(int pc, int firstslot, int nslots) {
		loadArrayArgs(pc, firstslot, nslots );
		loadVarresult();
		append(factory.createInvoke(STR_LUAVALUE, "varargsOf", TYPE_VARARGS, ARG_TYPES_LUAVALUEARRAY_VARARGS, Constants.INVOKESTATIC));
	}
	
	public void call(int nargs) {
		switch ( nargs ) {
		case 0: append(factory.createInvoke(STR_LUAVALUE, "call", TYPE_LUAVALUE, ARG_TYPES_NONE, Constants.INVOKEVIRTUAL)); break;
		case 1: append(factory.createInvoke(STR_LUAVALUE, "call", TYPE_LUAVALUE, ARG_TYPES_LUAVALUE, Constants.INVOKEVIRTUAL)); break;
		case 2: append(factory.createInvoke(STR_LUAVALUE, "call", TYPE_LUAVALUE, ARG_TYPES_LUAVALUE_LUAVALUE, Constants.INVOKEVIRTUAL)); break;
		case 3: append(factory.createInvoke(STR_LUAVALUE, "call", TYPE_LUAVALUE, ARG_TYPES_LUAVALUE_LUAVALUE_LUAVALUE, Constants.INVOKEVIRTUAL)); break;
		default: throw new IllegalArgumentException("can't call with "+nargs+" args");
		}
	}

	public void newTailcallVarargs() {
		append(factory.createInvoke(STR_LUAVALUE, "tailcallOf", TYPE_VARARGS, ARG_TYPES_LUAVALUE_VARARGS, Constants.INVOKESTATIC));
	}
	
	public void invoke(int nargs) {
		switch ( nargs ) {
		case -1: append(factory.createInvoke(STR_LUAVALUE, "invoke", TYPE_VARARGS, ARG_TYPES_VARARGS, Constants.INVOKEVIRTUAL)); break;
		case 0: append(factory.createInvoke(STR_LUAVALUE, "invoke", TYPE_VARARGS, ARG_TYPES_NONE, Constants.INVOKEVIRTUAL)); break;
		case 1: append(factory.createInvoke(STR_LUAVALUE, "invoke", TYPE_VARARGS, ARG_TYPES_VARARGS, Constants.INVOKEVIRTUAL)); break;
		case 2: append(factory.createInvoke(STR_LUAVALUE, "invoke", TYPE_VARARGS, ARG_TYPES_LUAVALUE_VARARGS, Constants.INVOKEVIRTUAL)); break;
		case 3: append(factory.createInvoke(STR_LUAVALUE, "invoke", TYPE_VARARGS, ARG_TYPES_LUAVALUE_LUAVALUE_VARARGS, Constants.INVOKEVIRTUAL)); break;
		default: throw new IllegalArgumentException("can't invoke with "+nargs+" args");
		}
	}

	
	// ------------------------ closures ------------------------
	
	public void closureCreate(String protoname) {
		append(factory.createNew(new ObjectType(protoname)));
		append(InstructionConstants.DUP);
		append(factory.createInvoke(protoname, "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
	}

	public void closureInitUpvalueFromUpvalue(String protoname, int newup, int upindex) {
		boolean isrw = pi.isReadWriteUpvalue( pi.upvals[upindex] ); 
		Type uptype = isrw? (Type) TYPE_LOCALUPVALUE: (Type) TYPE_LUAVALUE;
		String srcname = upvalueName(upindex);
		String destname = upvalueName(newup);
		append(InstructionConstants.THIS);
		append(factory.createFieldAccess(classname, srcname, uptype, Constants.GETFIELD));
		append(factory.createFieldAccess(protoname, destname, uptype, Constants.PUTFIELD));
	}

	public void closureInitUpvalueFromLocal(String protoname, int newup, int pc, int srcslot) {
		boolean isrw = pi.isReadWriteUpvalue( pi.vars[srcslot][pc].upvalue ); 
		Type uptype = isrw? (Type) TYPE_LOCALUPVALUE: (Type) TYPE_LUAVALUE;
		String destname = upvalueName(newup);
		int index = findSlotIndex( srcslot, isrw );
		append(new ALOAD(index));
		append(factory.createFieldAccess(protoname, destname, uptype, Constants.PUTFIELD));
	}
	
	private Map<LuaValue,String> constants = new HashMap<LuaValue,String>();
	
	public void loadConstant(LuaValue value) {
		switch ( value.type() ) {
		case LuaValue.TNIL: 
			loadNil();
			break;
		case LuaValue.TBOOLEAN:
			loadBoolean( value.toboolean() );
			break;
		case LuaValue.TNUMBER:
		case LuaValue.TSTRING:
			String name = (String) constants.get(value);
			if ( name == null ) {
				name = value.type() == LuaValue.TNUMBER? 
						value.isinttype()? 
							createLuaIntegerField(value.checkint()):
							createLuaDoubleField(value.checkdouble()):
						createLuaStringField(value.checkstring());
				constants.put(value, name);
			}
			append(factory.createGetStatic(classname, name, TYPE_LUAVALUE));
			break;
		default:
			throw new IllegalArgumentException("bad constant type: "+value.type());
		}
	}

	private String createLuaIntegerField(int value) {
		String name = PREFIX_CONSTANT+constants.size();
		FieldGen fg = new FieldGen(Constants.ACC_STATIC | Constants.ACC_FINAL, 
				TYPE_LUAVALUE, name, cp);
		cg.addField(fg.getField());
		init.append(new PUSH(cp, value));
		init.append(factory.createInvoke(STR_LUAVALUE, "valueOf",
				TYPE_LUAINTEGER, ARG_TYPES_INT, Constants.INVOKESTATIC));
		init.append(factory.createPutStatic(classname, name, TYPE_LUAVALUE));
		return name;
	}
	
	private String createLuaDoubleField(double value) {
		String name = PREFIX_CONSTANT+constants.size();
		FieldGen fg = new FieldGen(Constants.ACC_STATIC | Constants.ACC_FINAL, 
				TYPE_LUAVALUE, name, cp);
		cg.addField(fg.getField());
		init.append(new PUSH(cp, value));
		init.append(factory.createInvoke(STR_LUAVALUE, "valueOf",
				TYPE_LUANUMBER, ARG_TYPES_DOUBLE, Constants.INVOKESTATIC));
		init.append(factory.createPutStatic(classname, name, TYPE_LUAVALUE));			
		return name;
	}

	private String createLuaStringField(LuaString value) {
		String name = PREFIX_CONSTANT+constants.size();
		FieldGen fg = new FieldGen(Constants.ACC_STATIC | Constants.ACC_FINAL, 
				TYPE_LUAVALUE, name, cp);
		cg.addField(fg.getField());
		LuaString ls = value.checkstring();
		if ( ls.isValidUtf8() ) {
			init.append(new PUSH(cp, value.tojstring()));
			init.append(factory.createInvoke(STR_LUASTRING, "valueOf",
					TYPE_LUASTRING, ARG_TYPES_STRING, Constants.INVOKESTATIC));
		} else {
			char[] c = new char[ls.m_length];
			for ( int j=0; j<ls.m_length; j++ ) 
				c[j] = (char) (0xff & (int) (ls.m_bytes[ls.m_offset+j]));
			init.append(new PUSH(cp, new String(c)));
			init.append(factory.createInvoke(STR_STRING, "toCharArray",
					TYPE_CHARARRAY, Type.NO_ARGS,
					Constants.INVOKEVIRTUAL));
			init.append(factory.createInvoke(STR_LUASTRING, "valueOf",
					TYPE_LUASTRING, ARG_TYPES_CHARARRAY,
					Constants.INVOKESTATIC));
		}
		init.append(factory.createPutStatic(classname, name, TYPE_LUAVALUE));			
		return name;
	}

	// --------------------- branching support -------------------------
	public static final int BRANCH_GOTO = 1;
	public static final int BRANCH_IFNE = 2;
	public static final int BRANCH_IFEQ = 3;
	
	public void addBranch( int pc, int branchType, int targetpc ) {
		switch ( branchType ) {
		default: 
		case BRANCH_GOTO: branches[pc]  = new GOTO(null); break;
		case BRANCH_IFNE:  branches[pc] = new IFNE(null); break;
		case BRANCH_IFEQ:  branches[pc] = new IFEQ(null); break;
		}
		targets[pc] = targetpc;
		append(branches[pc]);
	}


	private void append( Instruction i ) {
		conditionalSetBeginningOfLua( main.append(i) );
	}
	
	private void append( CompoundInstruction i ) {
		conditionalSetBeginningOfLua( main.append(i) );
	}
	
	private void append( BranchInstruction i ) {
		conditionalSetBeginningOfLua( main.append(i) );
	}
	
	private void conditionalSetBeginningOfLua(InstructionHandle ih) {
		if ( beginningOfLuaInstruction == null )
			beginningOfLuaInstruction = ih;
	}

	public void onEndOfLuaInstruction(int pc, int line) {
		branchDestHandles[pc] = beginningOfLuaInstruction;
		lastInstrHandles[pc] = main.getEnd();
		if (line != prev_line)
			mg.addLineNumber(beginningOfLuaInstruction, prev_line = line);
		beginningOfLuaInstruction = null;
	}
	
	public void setVarStartEnd(int slot, int start_pc, int end_pc, String name) {
		Integer islot = Integer.valueOf(slot);
		if (localVarGenBySlot.containsKey(islot)) {
			name = name.replaceAll("[^a-zA-Z0-9]", "_");
			LocalVariableGen l = (LocalVariableGen)localVarGenBySlot.get(islot);
			l.setEnd(lastInstrHandles[end_pc-1]);
			if (start_pc > 1)
				l.setStart(lastInstrHandles[start_pc-2]);
			l.setName(name);
		}
	}
	
	private void resolveBranches() {
		int nc = p.code.length; 
		for (int pc = 0; pc < nc; pc++) {
			if (branches[pc] != null) {
				int t=targets[pc];
				while ( t<branchDestHandles.length && branchDestHandles[t] == null )
					t++;
				if ( t>= branchDestHandles.length )
					 throw new IllegalArgumentException("no target at or after "+targets[pc]+" op="+Lua.GET_OPCODE(p.code[targets[pc]]));
				branches[pc].setTarget(branchDestHandles[t]);
			}
		}
	}
	
	public void setlistStack(int pc, int a0, int index0, int nvals) {
		for ( int i=0; i<nvals; i++ ) {
			dup();
			append(new PUSH(cp, index0+i));
			loadLocal( pc, a0+i );
	        append(factory.createInvoke(STR_LUAVALUE, "rawset", Type.VOID, ARG_TYPES_INT_LUAVALUE, Constants.INVOKEVIRTUAL));
    	}
	}

	public void setlistVarargs(int index0, int vresultbase) {
		append(new PUSH(cp, index0));
		loadVarresult();
		append(factory.createInvoke(STR_LUAVALUE, "rawsetlist", Type.VOID, ARG_TYPES_INT_VARARGS, Constants.INVOKEVIRTUAL));
	}

	public void concatvalue() {
        append(factory.createInvoke(STR_LUAVALUE, "concat", TYPE_LUAVALUE, ARG_TYPES_LUAVALUE, Constants.INVOKEVIRTUAL));
	}
	
	public void concatbuffer() {
        append(factory.createInvoke(STR_LUAVALUE, "concat", TYPE_BUFFER, ARG_TYPES_BUFFER, Constants.INVOKEVIRTUAL));
	}

	public void tobuffer() {
        append(factory.createInvoke(STR_LUAVALUE, "buffer", TYPE_BUFFER, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}

	public void tovalue() {
        append(factory.createInvoke(STR_BUFFER, "value", TYPE_LUAVALUE, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
	}

	public void closeUpvalue(int pc, int upindex) {
		// TODO: assign the upvalue location the value null;
		/*
		boolean isrw = pi.isReadWriteUpvalue( pi.upvals[upindex] ); 
		append(InstructionConstants.THIS);
		append(InstructionConstants.ACONST_NULL);
		if ( isrw ) {
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LUAVALUEARRAY, Constants.PUTFIELD));
		} else {
			append(factory.createFieldAccess(classname, upvalueName(upindex), TYPE_LUAVALUE, Constants.PUTFIELD));
		}
		*/
	}
}
