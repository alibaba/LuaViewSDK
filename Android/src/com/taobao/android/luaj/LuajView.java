package com.taobao.android.luaj;

import java.io.InputStream;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class LuajView extends View implements ResourceFinder {

	public final Globals globals;

	public LuajView(Context context) {
		super(context);
		this.globals = JsePlatform.standardGlobals();
		this.globals.finder = this;
		addTextView();
	}

	// Implement a finder that loads from the assets directory.
	public InputStream findResource(String name) {
		try {
			return getContext().getAssets().open(name);
		} catch (java.io.IOException ioe) {
			return null;
		}
	}

	public void addTextView() {
		LuaValue f = globals.get("createTextView");
		if (!f.isnil()) {
			f.call();
		}
	}
	
	public void draw(Canvas canvas) {
		LuaValue f = globals.get("draw");
		if (!f.isnil())
			try {
				f.call(CoerceJavaToLua.coerce(canvas));
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			super.draw(canvas);
	}

	public boolean f(int keyCode, KeyEvent event) {
		LuaValue f = globals.get("onKeyDown");
		if (!f.isnil())
			try {
				return f.call(CoerceJavaToLua.coerce(keyCode),
						CoerceJavaToLua.coerce(event)).toboolean();
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		else
			return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		LuaValue f = globals.get("onKeyUp");
		if (!f.isnil())
			try {
				return f.call(CoerceJavaToLua.coerce(keyCode),
						CoerceJavaToLua.coerce(event)).toboolean();
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		else
			return super.onKeyUp(keyCode, event);
	}

	public boolean onTouchEvent(MotionEvent event) {
		LuaValue f = globals.get("onTouchEvent");
		if (!f.isnil())
			try {
				return f.call(CoerceJavaToLua.coerce(event)).toboolean();
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		else
			return super.onTouchEvent(event);
	}

	public boolean onTrackballEvent(MotionEvent event) {
		LuaValue f = globals.get("onTrackballEvent");
		if (!f.isnil())
			try {
				return f.call(CoerceJavaToLua.coerce(event)).toboolean();
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		else
			return super.onTrackballEvent(event);
	}

	public void onWindowFocusChanged(boolean hasWindowFocus) {
		LuaValue f = globals.get("onWindowFocusChanged");
		if (!f.isnil())
			try {
				f.call(CoerceJavaToLua.coerce(hasWindowFocus));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void onWindowSystemUiVisibilityChanged(int visible) {
		LuaValue f = globals.get("onWindowSystemUiVisibilityChanged");
		if (!f.isnil())
			try {
				f.call(CoerceJavaToLua.coerce(visible));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
