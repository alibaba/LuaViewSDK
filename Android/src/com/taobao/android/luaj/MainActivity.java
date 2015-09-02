package com.taobao.android.luaj;

import java.io.InputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements ResourceFinder, OnClickListener{
	public Globals globals;
	private TextView mLuaText;
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		globals = JsePlatform.standardGlobals();
//		globals.finder = this;
//		setContentView(R.layout.activity_main);
//		mLuaText = (TextView) findViewById(R.id.luaj_test);
//		mLuaText.setOnClickListener(this);
//		try {
//			globals.loadfile("activity.lua").call();
//		} catch ( Exception e ) {
//			e.printStackTrace();
//		}
//		LuaValue f = globals.get("setText");
//		if (!f.isnil()) {
//			mLuaText.setText(f.call().toString());
//		}
//	}

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LuajView view = new LuajView(this);
		setContentView(view);
		try {
			LuaValue activity = CoerceJavaToLua.coerce(this);
			LuaValue viewobj = CoerceJavaToLua.coerce(view);
			view.globals.loadfile("activity.lua").call(activity, viewobj);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@Override
	public InputStream findResource(String name) {
		try {
			return getAssets().open(name);
		} catch (java.io.IOException ioe) {
			return null;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		LuaValue f = globals.get("onClick");
		if (!f.isnil()) {
			f.call(CoerceJavaToLua.coerce(arg0));
		}
	}
}
