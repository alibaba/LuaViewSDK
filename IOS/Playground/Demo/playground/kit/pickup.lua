--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/23
-- Time: 10:03
-- To change this template use File | Settings | File Templates.
--

require("kit.common")
require("kit.platform")

Pickup = {}

function Pickup:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function Pickup:getInstance()
    if (self.instance == nil) then
        self.instance = self:new()
        print("tuoli", "确保一个虚拟机只有一个xmlParser")
        print("tuoli", "require kit.slaxdom start")
        self.xmlParser = require("kit.slaxdom")
        print("tuoli", "require kit.slaxdom end")
    end

    return self.instance
end

function Pickup:render(xmlFile)
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
function Pickup:flexOrNot()
    for _k, _ in pairs(self.objs) do
        local isFlex = false
        if (_k.attr["flexCss"] ~= nil) then
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
    if (not Platform.isAndroid) then
        for _, _view in pairs(self.flxLayoutObjs) do
            _view:flxLayout(true)
        end
    end
end

function Pickup:isViewElement(element)
    local view
    if (element.name == "View") then
        view = View()
    elseif (element.name == "Label") then
        view = Label()
    elseif (element.name == "Button") then
        view = Button()
    elseif (element.name == "Image") then
        view = Image()
    elseif (element.name == "HScrollView") then
        view = HScrollView()
    elseif (element.name == "WebView") then
        view = WebView()
    elseif (element.name == "TextField") then
        view = TextField()
    elseif (element.name == "CollectionView") then
        view = CollectionView()
    elseif (element.name == "RefreshCollectionView") then
        view = RefreshCollectionView()
    elseif (element.name == "LoadingIndicator") then
        view = LoadingIndicator()
    elseif (element.name == "PagerView") then
        view = PagerView()
    elseif (element.name == "PagerIndicator") then
        view = PagerIndicator()
    else
        --        print("tuoli error", element.name .. " is not view")
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
function Pickup:split(str, delimiter)
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

function Pickup:parseElement(element, parent, identifierObjs)
    if (element.type == "comment") then
        return
    end

    local isContains = self:isViewElement(element)
    if (isContains == true and element.attr ~= nil) then
        for _, _v in ipairs(element.attr) do
            if (_v.name == "frame") then
                _v.value = _v.value:gsub("CONTENT_WIDTH", Platform.contentWidth)
                _v.value = _v.value:gsub("CONTENT_HEIGHT", Platform.contentHeight)
                if (parent and (parent.name == "View" or parent.name == "HScrollView")) then
                    self.objs[parent]:addView(self.objs[element])
                end
                local paramFun = Platform:loadString("return " .. _v.value)
                self.objs[element]:frame(paramFun())
            elseif (_v.name == "backgroundColor") then
                self.objs[element]:backgroundColor(tonumber(_v.value))
            elseif (_v.name == "color") then
                if (element.name == "LoadingIndicator") then
                    self.objs[element]:color(tonumber(_v.value))
                end
            elseif (_v.name == "id") then
                -- 考虑到性能问题,不采用遍历的方式来取得开发者所关注的对象。而是使用字典的形式来获取。
                identifierObjs[_v.value] = self.objs[element]
            elseif (_v.name == "flexCss") then
                self.objs[element]:flexCss(_v.value)
                if (not Platform.isAndroid) then
                    if (element.name == "View" and parent and (parent.name ~= "View" or (parent.name == "View" and parent.attr["flexCss"] == nil))) then
                        table.insert(self.flxLayoutObjs, self.objs[element])
                    end
                end
            elseif (_v.name == "title") then
                self.objs[element]:title(_v.value)
            elseif (_v.name == "titleColor") then
                self.objs[element]:titleColor(tonumber(_v.value))
            elseif (_v.name == "image") then
                if (element.name == "Button") then
                    local params = self:split(_v.value, ",")
                    self.objs[element]:image(params[1], params[2])
                elseif (element.name == "Image") then
                    self.objs[element]:image(_v.value)
                end
            elseif (_v.name == "cornerRadius") then
                self.objs[element]:cornerRadius(tonumber(_v.value))
            elseif (_v.name == "borderColor") then
                self.objs[element]:borderColor(tonumber(_v.value))
            elseif (_v.name == "borderWidth") then
                self.objs[element]:borderWidth(tonumber(_v.value))
            elseif (_v.name == "textColor") then
                if (not Platform.isAndroid and string.len(_v.value) == 10) then
                    local alphaStr = string.sub(_v.value, 3, 4)
                    local alpha = tonumber(alphaStr)/tonumber("0xFF")
                    self.objs[element]:textColor(tonumber(_v.value), alpha)
                else
                    self.objs[element]:textColor(tonumber(_v.value))
                end
            elseif (_v.name == "fontSize") then
                self.objs[element]:fontSize(tonumber(_v.value))
            elseif (_v.name == "lineCount") then
                self.objs[element]:lineCount(tonumber(_v.value))
            elseif (_v.name == "alpha") then
                self.objs[element]:alpha(tonumber(_v.value))
            elseif (_v.name == "text") then
                self.objs[element]:text(_v.value)
            elseif (_v.name == "textAlign") then
                local paramFun = Platform:loadString("return " .. _v.value)
                self.objs[element]:textAlign(paramFun())
            elseif (_v.name == "scaleType") then
                local paramFun = Platform:loadString("return " .. _v.value)
                self.objs[element]:scaleType(paramFun())
            elseif (_v.name == "ellipsize") then
                if (Platform.isAndroid) then
                    local paramFun = Platform:loadString("return " .. _v.value)
                    self.objs[element]:ellipsize(paramFun())
                end
            elseif (_v.name == "hint") then
                self.objs[element]:hint(_v.value)
            elseif (_v.name == "miniSpacing") then
                self.objs[element]:miniSpacing(tonumber(_v.value))
            elseif (_v.name == "autoScroll") then
                self.objs[element]:autoScroll(tonumber(_v.value))
            elseif (_v.name == "looping") then
                if (_v.value == "true") then
                    self.objs[element]:looping(true)
                else
                    self.objs[element]:looping(false)
                end
            elseif (_v.name == "selectedColor") then
                self.objs[element]:selectedColor(tonumber(_v.value))
            elseif (_v.name == "unselectedColor") then
                self.objs[element]:unselectedColor(tonumber(_v.value))
            elseif (_v.name == "showScrollIndicator") then
                if (_v.value == "true") then
                    self.objs[element]:showScrollIndicator(true)
                else
                    self.objs[element]:showScrollIndicator(false)
                end
            elseif (_v.name == "loadUrl") then
                self.objs[element]:loadUrl(_v.value)
            elseif (_v.name == "hide") then
                if (_v.value == "true") then
                    self.objs[element]:hide(true)
                else
                    self.objs[element]:hide(false)
                end
            elseif (_v.name == "contentSize") then
                if (not Platform.isAndroid) then
                    _v.value = _v.value:gsub("CONTENT_WIDTH", Platform.contentWidth)
                    _v.value = _v.value:gsub("CONTENT_HEIGHT", Platform.contentHeight)
                    local paramFun = Platform:loadString("return " .. _v.value)
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

return Pickup





