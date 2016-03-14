_DEBUG_FILE = "@debug.lua"

debug.bps = {
    max = 0,
    trace = false,
    last_cmd = "",
    next = false,
    cur_func = nil,
    trace_count = 0,
    var_tbl = nil,
}

function debug_log( log_str )
    print( "[调试器] " .. log_str );
end

function debug_print_var_0( name, value, level )
    local ret = "";
    local prefix = string:rep( "    ", level )
    local str = string:format( "%s%s = %s", prefix, name, tostring(value) )

    if type( value ) == "table" then
        if debug.var_tbl[value] then
            --已在临时表中的,只打印表地址
            -- print( str )
            ret = ret .. tostring(str) .. '\n';
            return ret;
        end

        --加到临时表中,以免表出现循环引用时,打印也产生死循环
        debug.var_tbl[value] = true
        --打印表中所有数据
        --print( string:format( "%s%s = {", prefix, name ) )
        ret = ret .. string:format( "%s%s = {", prefix, name ) .. '\n';
        for k, v in pairs( value ) do
            if type( k ) == "string" then
            else
                k = tostring(k);
            end
            --不打印 "_"开头的内部变量
            --if string:sub( k, 1, 1 ) ~= "_" then
            ret = ret .. debug_print_var_0( k, v, level + 1 )
            --end
        end
        -- print( prefix .. "}" )
        ret = ret .. prefix .. "}" .. '\n';
    elseif type( value ) == "string" then
        -- print( str )
        ret = ret .. tostring(str) .. '\n';
    else
        -- print( str )
        ret = ret .. tostring(str) .. '\n';
    end
    return ret;
end

function debug_print_var( name, value, level )
    local s = debug_print_var_0( name, value, level );
    if( s ) then
        s = string:sub(s, 1, #s - 1);
        debug_log( s );
    else
        debug_log( s );
    end
end

function debug_print_expr( var )
    if ( var==nil ) then
        debug_log("debug_print_expr var==nil");
        return;
    end
    --清空临时变量表
    debug.var_tbl = {}

    local index = 1
    --找局部变量
    while true do
        local name, value = debug:getlocal( 4, index )
        if not name then 
            break 
        end
        index = index + 1

        if name == var then
            debug_print_var( var, value, 0 )
            return
        end
    end

    -- try upvalues
    local func = debug:getinfo(4,"f").func
    local index = 1
    while true do
        local name, value = debug:getupvalue(func, index)
        if not name then 
            break 
        end
        if name == var then 
            debug_print_var( var, value, 0 )
            return
        end
       index = index + 1
    end


    --找全局变量
    if _G[var] ~= nil then
        debug_print_var( var, _G[var], 0 )
        return
    end

    debug_log( var .. " is invalid" )
end


function debug_run_expr( s )
    loadJson(s)
end

function add_breakpoint( expr )
    local si = string:find( expr, ":" )
    if nil == si then
        debug_log( "add breakpoint error, expr (" .. expr .. ") invalid" )
        return
    end

    local line = string:sub( expr, si + 1 )
    local line = tonumber( line )
    local source = string:sub( expr, 1, si - 1 )

    --先查找有不有相同断点
    if ( ( debug.bps[line] ~= nil ) and ( debug.bps[line][source] ~= nil ) ) then
        debug_log( string:format( "breakpoint %s:%d existed", source, line ) )
        return
    end

    local tbl = {}
    tbl.source = source
    tbl.line = line
    tbl.active = true
    tbl.number = debug.bps.max + 1

    if debug.bps[line] == nil then
        debug.bps[line] = {}
    end

    debug.bps[line][source] = tbl
    debug.bps.max = debug.bps.max + 1
    debug_log( string:format( "加断点(%s:%d)", source, line ) )
end

function remove_breakpoint( expr )
    local si = string:find( expr, ":" )
    if nil == si then
        debug_log( "remove breakpoint error, expr (" .. expr .. ") invalid" )
        return
    end

    local line = string:sub( expr, si + 1 )
    local line = tonumber( line )
    local source = string:sub( expr, 1, si - 1 )

    --先查找有不有相同断点
    if( ( debug.bps[line] ~= nil ) and ( debug.bps[line][source] ~= nil ) )then
        debug.bps[line][source] = nil;
        debug_log( string:format( "删点断(%s:%d)", source, line ) )
        return
    else
        debug_log( string:format( "not found breakpoint %s:%d existed", source, line ) )
        return
    end
end

function debug_show_bp()
    for k, v in pairs( debug.bps ) do
        if type( v ) == "table" then
            for k1, v1 in pairs( v ) do
                local str = string:format( "bp num:%d  %s:%d  active:",
                v1.number,
                v1.source,
                v1.line )
                if v1.active then
                    str = str .. "enable"
                else
                    str = str .. "disable"
                end
                debug_log( str )
            end
        end
    end
end

function debug_del_bp( expr )
    local number = tonumber( expr )
    for k, v in pairs( debug.bps ) do
        if type( v ) == "table" then
            for k1, v1 in pairs( v ) do
                if v1.number == number then
                    debug.bps[k][k1] = nil
                    debug_log( "remove bp:" .. number .. " ok" )
                end
            end
        end
    end
end

function debug_enable_bp( expr )
    local number = tonumber( expr )
    for k, v in pairs( debug.bps ) do
        if type( v ) == "table" then
            for k1, v1 in pairs( v ) do
                if v1.number == number then
                    v1.active = true
                    debug_log( "enable bp:" .. number )
                end
            end
        end
    end
end

function debug_disable_bp( expr )
    local number = tonumber( expr )
    for k, v in pairs( debug.bps ) do
        if type( v ) == "table" then
            for k1, v1 in pairs( v ) do
                if v1.number == number then
                    v1.active = false
                    debug_log( "disable bp:" .. number )
                end
            end
        end
    end
end

function debug_help()
    print( "h             help info" )
    print( "c             continue" )
    print( "s             trace" )
    print( "n             next" )
    print( "p var         print variable" )
    print( "run expression      run expression code" )
    print( "b  src:line   add breakpoint" )
    print( "rb src:line   remove breakpoint" )
    print( "bl            list breakpoint" )
    print( "bt            print traceback" )
end

function debug_runing_execute( cmd )
    if( cmd==nil ) then 
    	return;
    end
    local c = cmd
    local expr = ""
    local si = string:find( cmd, " " )
    if si ~= nil then
        c = string:sub( cmd, 1, si - 1 )
        expr = string:sub( cmd, string:find( cmd, " %w" ) + 1 )
    end

    if c == "b" then
        add_breakpoint( expr )
    elseif c == "rb" then
        remove_breakpoint( expr )
    elseif c == "p" then
        debug_print_expr( expr )
    end
end

function debug_execute_cmd( env )
    --print( "(ldb) " )
    local cmd = debug:readCmd()
    if ( cmd ==nil ) then
    	debug:sleep(0.01);
    	return false;
    end
    --取上一次的命令,方便调试
    -- if cmd ~= "" then
    --     debug.bps.last_cmd = cmd
    -- else
    --     cmd = debug.bps.last_cmd
    -- end

    local c = cmd
    local expr = ""
    local si = string:find( cmd, " " )
    if si ~= nil then
        c = string:sub( cmd, 1, si - 1 )
        -- local index = string:find( cmd, " %w" );
        -- if ( index ) then
        --     expr = string:sub( cmd, index + 1 );
        -- end
        expr = string:sub(cmd, si + 1 );
    end
    if c=="close" then
        debug_close();
        return true;
    elseif c=="none" then
        return false;
    elseif c == "c" then
        debug.bps.trace = false
        return true
    elseif c == "s" then
        debug.bps.trace = true
        return true
    elseif c == "n" then
        debug.bps.trace = false
        debug.bps.next = true
        debug.bps.cur_func = env.func
        debug.bps.trace_count = debug:traceback_count()
        return true
    elseif c == "p" then
        debug_print_expr( expr )
    elseif c == "run" then
        debug_run_expr( expr )
    elseif c == "b" then
        add_breakpoint( expr )
    elseif c == "rb" then
        remove_breakpoint( expr )
    elseif c == "bl" then
        debug_show_bp()
    elseif c == "d" then
        debug_del_bp( expr )
    elseif c == "be" then
        debug_enable_bp( expr )
    elseif c == "bd" then
        debug_disable_bp( expr )
    elseif c == "bt" then
        print( debug:traceback("", 3) )
    elseif c == "h" then
        debug_help()
    else
        debug_log( "invalid cmd:" .. cmd )
    end
    return false
end

function debug_trace( event, line )

    local env = debug:getinfo( 2 )
    if not env then
        print("getinfo failed ", event, line)
        return
    end

    if env.source == _DEBUG_FILE then
        return
    end

    --判断是否在next调试
    if debug.bps.next  then
        local trace_count = debug:traceback_count()
        --函数返回了,调用栈数量就会比现在小
        if trace_count < debug.bps.trace_count then
            debug.bps.next = false
            debug.bps.trace = true
        elseif trace_count == debug.bps.trace_count then
            if debug.bps.cur_func == env.func then
	            debug.bps.next = false
	            debug.bps.trace = true
            end
        end
     end

    --判断是否有断点
    if( ( not debug.bps.trace ) and ( debug.bps[line] ~= nil ) ) then
        local tbl = debug.bps[line][env.source]
        if(  ( tbl ~= nil ) and tbl.active  )then
            --如果在next时,碰到断点了,就清除单步运行状态
            debug.bps.next = false
            debug.bps.trace = true
            -- debug_log( "碰到断点 " .. env.source .. " - " .. line )
        end
    end

    if debug.bps.trace then
        local src = debug:get_file_line( env.source, line )
        local funname = env.name or "unknow"
        --debug_log( string:format( "%s:%d(%s)  %s", env.source, line, funname, src ) )
        debug:runningLine( env.source, line );
        debug.bps.cur_file = env.source;
        debug.bps.cur_line = line
        while not debug_execute_cmd( env ) do
        end
        return;
    end

    -- local cmd = debug:readCmd()
    -- debug_runing_execute( cmd );
end

function begin_debug()
    debug.bps.trace = true
    debug:sethook( debug_trace, "l" )
end

--关闭debugger
function debug_close()
    debug.bps.trace = false
    debug.bps.next = false
    debug:sethook()
end

debug:printToServer(true);


begin_debug(); -- last line
