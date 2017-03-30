--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

require("kit.util")
require("kit.sys")

Pica = {}

local _gsub = string.gsub
local _find = string.find

function Pica:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function Pica:getInstance()
    if (self.instance == nil) then
        self.instance = self:new()
        print("tuoli", "确保一个虚拟机只有一个xmlParser")
        print("tuoli", "require kit.slaxdom start")
        self.xmlParser = require("kit.slaxdom")
        print("tuoli", "require kit.slaxdom end")
    end

    return self.instance
end

function Pica:render(xmlFile)
    print("tuoli", "xml read start")
    local data = File:read(xmlFile)
    print("tuoli", "xml read end")

    if (type(data) ~= "string") then
        data = tostring(data)
        print("tuoli xml", string.len(data))
    end

    self.objs = {}
    self.flxLayoutObjs = {}

    local identifierObjs = {}

    print("tuoli", "xml dom start")
    local root = self.xmlParser:dom(data)
    print("tuoli", "xml dom end")
    print("tuoli", "parse element start")
    self:parseElement(root, nil, identifierObjs)
    print("tuoli", "parse element end")
    print("tuoli", "flex start")
    self:flexOrNot()
    print("tuoli", "flex end")

    return identifierObjs
end

--[[
--  遍历self.objs,找出有子节点的父节点,并决定是否需要对其子节点进行flex布局
 ]]
function Pica:flexOrNot()
    for _k, _ in pairs(self.objs) do
        local isFlex = false
        if (_k.attr["css"] ~= nil) then
            isFlex = true
        end

        -- 如果遍历出来的对象是一个flex根节点,则遍历它的孩子节点,并设置flexChildren
        if (isFlex == true and table.getn(_k.kids) > 0) then
            local children = {}
            for _, __v in pairs(_k.kids) do
                if (self.objs[__v]) then
                    table.insert(children, self.objs[__v])
                end
            end
            if (table.getn(children) > 0) then
                self.objs[_k]:flexChildren(children)
            end
        end
    end

    -- iOS能否在SDK层屏蔽掉flxLayout的调用
    if (not Sys.android) then
        for _, _view in pairs(self.flxLayoutObjs) do
            _view:flxLayout(true)
        end
    end
end

function Pica:isViewElement(element)
    local view
    if (element.name == "v") then
        view = View()
    elseif (element.name == "l") then
        view = Label()
        view:ellipsize(Ellipsize.END)   -- default setting
        view:textColor(0x000000)
    elseif (element.name == "btn") then
        view = Button()
    elseif (element.name == "img") then
        view = Image()
    elseif (element.name == "hscroll") then
        view = HScrollView()
    elseif (element.name == "web") then
        view = WebView()
    elseif (element.name == "field") then
        view = TextField()
    elseif (element.name == "list") then
        view = CollectionView()
        view:miniSpacing(0)
    elseif (element.name == "pull") then
        view = RefreshCollectionView()
        view:miniSpacing(0)
    elseif (element.name == "load") then
        view = LoadingIndicator()
    elseif (element.name == "page") then
        view = PagerView()
    elseif (element.name == "ind") then
        view = PagerIndicator()
    else
        view = nil
    end

    if (view ~= nil) then
        self.objs[element] = view
        return true
    else
        return false
    end
end

--[[
--  分割字符串,并去除字符串中的空格符
 ]]
function Pica:split(str, delimiter)
    if str==nil or str=='' or delimiter==nil then
        return nil
    end

    local result = {}
    for match in (str..delimiter):gmatch("(.-)"..delimiter) do
        local _match, _ = string.gsub(match, "^%s*(.-)%s*$", "%1")
        table.insert(result, _match)
    end
    return result
end

function Pica:parseElement(element, parent, identifierObjs)
    if (element.type == "comment") then
        return
    end

    local isContains = self:isViewElement(element)
    if (isContains == true and element.attr ~= nil) then
        for _, _v in ipairs(element.attr) do
            if (_v.name == "frame") then
                if (parent and (parent.name == "v" or parent.name == "hscroll")) then
                    self.objs[parent]:addView(self.objs[element])
                end
                local paramFun = Sys:loadString("return " .. _v.value)
                self.objs[element]:frame(paramFun())
            elseif (_v.name == "bg") then
                self.objs[element]:backgroundColor(tonumber(_v.value))
            elseif (_v.name == "color") then
                self.objs[element]:color(tonumber(_v.value))
            elseif (_v.name == "id") then
                -- 考虑到性能问题,不采用数组的形式来存取开发者所关注的对象,而是使用字典的形式来存取。
                identifierObjs[_v.value] = self.objs[element]
            elseif (_v.name == "css") then
                print("tuoli ddddddd start")
                _v.value = _gsub(_v.value, "-","_")
                local paramFun = Sys:loadString("return " .. _v.value)
                local t = paramFun()
                local css = ""
                for _k, _v in pairs(t) do
                    _k = _gsub(_k, "_","-")
                    if (_find(_k, "margin")) then
                        css = css .. _k .. ":" .. _v*Sys.scale .. ","
                    else
                        css = css .. _k .. ":" .. _v .. ","
                    end
                end
                print("tuoli ddddddd end")
                self.objs[element]:flexCss(css)
                if (not Sys.android) then
                    if (element.name == "v" and parent and (parent.name ~= "v" or (parent.name == "v" and parent.attr["css"] == nil))) then
                        table.insert(self.flxLayoutObjs, self.objs[element])
                    end
                end
            elseif (_v.name == "title") then
                self.objs[element]:title(_v.value)
            elseif (_v.name == "titleColor") then
                self.objs[element]:titleColor(tonumber(_v.value))
            elseif (_v.name == "image") then
                if (element.name == "btn") then
                    local params = self:split(_v.value, ",")
                    self.objs[element]:image(params[1], params[2])
                elseif (element.name == "img") then
                    self.objs[element]:image(_v.value)
                end
            elseif (_v.name == "corner") then
                self.objs[element]:cornerRadius(tonumber(_v.value)*Sys.scale)
            elseif (_v.name == "borderColor") then
                self.objs[element]:borderColor(tonumber(_v.value))
            elseif (_v.name == "borderWidth") then
                self.objs[element]:borderWidth(tonumber(_v.value))
            elseif (_v.name == "textColor") then
                if (not Sys.android and string.len(_v.value) == 10) then
                    local alphaStr = string.sub(_v.value, 3, 4)
                    local alpha = tonumber(alphaStr)/tonumber("0xFF")
                    self.objs[element]:textColor(tonumber(_v.value), alpha)
                else
                    self.objs[element]:textColor(tonumber(_v.value))
                end
            elseif (_v.name == "fontSize") then
                self.objs[element]:fontSize(tonumber(_v.value)*Sys.scale)
            elseif (_v.name == "lineCount") then
                self.objs[element]:lineCount(tonumber(_v.value))
            elseif (_v.name == "alpha") then
                self.objs[element]:alpha(tonumber(_v.value))
            elseif (_v.name == "text") then
                self.objs[element]:text(_v.value)
            elseif (_v.name == "textAlign") then
                local paramFun = Sys:loadString("return " .. _v.value)
                self.objs[element]:textAlign(paramFun())
            elseif (_v.name == "scaleType") then
                local paramFun = Sys:loadString("return " .. _v.value)
                self.objs[element]:scaleType(paramFun())
            elseif (_v.name == "hint") then
                self.objs[element]:hint(_v.value)
            elseif (_v.name == "space") then
                self.objs[element]:miniSpacing(tonumber(_v.value))
            elseif (_v.name == "autoScroll") then
                self.objs[element]:autoScroll(tonumber(_v.value))
            elseif (_v.name == "looping") then
                if (_v.value == "true") then
                    self.objs[element]:looping(true)
                else
                    self.objs[element]:looping(false)
                end
            elseif (_v.name == "selected") then
                self.objs[element]:selectedColor(tonumber(_v.value))
            elseif (_v.name == "unselected") then
                self.objs[element]:unselectedColor(tonumber(_v.value))
            elseif (_v.name == "showScrollIndicator") then
                if (_v.value == "true") then
                    self.objs[element]:showScrollIndicator(true)
                else
                    self.objs[element]:showScrollIndicator(false)
                end
            elseif (_v.name == "url") then
                self.objs[element]:loadUrl(_v.value)
            elseif (_v.name == "hide") then
                if (_v.value == "true") then
                    self.objs[element]:hide(true)
                else
                    self.objs[element]:hide(false)
                end
            elseif (_v.name == "contentSize") then
                if (not Sys.android) then
                    local paramFun = Sys:loadString("return " .. _v.value)
                    self.objs[element]:contentSize(paramFun())
                end
            else
                print("LuaError::Layout", "方法名不对: " .. _v.name)
            end
        end
    end

    if (element.kids ~= nil) then
        for _, kid in pairs(element.kids) do
            self:parseElement(kid, element, identifierObjs);
        end
    end
end

return Pica





