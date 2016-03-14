package com.taobao.luaview.demo.ui;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVCustomPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * @author song
 * @date 15/11/3
 */
public class CustomError extends LVCustomPanel {

    public CustomError(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    @Override
    public void initPanel() {
        Button button = new Button(getContext());
        button.setText("Error");

        LayoutParams relativeLayout = LuaViewUtil.createRelativeLayoutParamsWW();
        relativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(button, relativeLayout);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callLuaCallback("Error");
            }
        });
    }
}
