--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/10
-- Time: 10:10
-- To change this template use File | Settings | File Templates.
--

require("kit.common")

local Pica = {}

local _screenWidth, _screenHeight = System:screenSize()
local _isAndroid = System:android()


-- 减掉ActionBar和StatusBar的高度
if (_isAndroid) then
    _screenHeight = _screenHeight - 80      -- Android, 不同机型, 高度不定, 比较蛋疼
else
    _screenHeight = _screenHeight - 64      -- iOS, 稳定在这个值
end

--function Pica:new(o)
--    local AppMeta = {__index = self}
--    local instance = o or {}
--    instance.functions={}
--    setmetatable(instance, AppMeta)
--    return instance
--end

function Pica:parseXml(xml)
    if (g_xmlParser == nil) then
        print("tuoli", "确保一个虚拟机只有一个xmlParser")
        print("tuoli", "require kit.slaxdom start")
        g_xmlParser = require("kit.slaxdom")
        print("tuoli", "require kit.slaxdom end")
    end

    self.objs = {}
    self.identifierObjs = {}
    if (not _isAndroid) then
        self.flxLayoutObjs = {}
    end

--    print("tuoli xml", xml)
    if (type(xml) ~= "string") then
        xml = tostring(xml)
        print("tuoli xml", string.len(xml))
    end

    print("tuoli", "xml dom start")
    local root = g_xmlParser:dom(xml)
    print("tuoli", "xml dom end")
    print("tuoli", "parse element start")
    self:parseElement(root, nil)
    print("tuoli", "parse element end")
    print("tuoli", "flex start")
    self:flexOrNot()
    print("tuoli", "flex end")
end

--[[
--  遍历self.objs,找出有子节点的父节点,并决定是否需要对其子节点进行flex布局
 ]]
function Pica:flexOrNot()
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
    if (not _isAndroid) then
        for _, _view in pairs(self.flxLayoutObjs) do
            _view:flxLayout(true)
        end
    end
end

function Pica:isViewElement(element)
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
    elseif (element.name == "TextField") then
        view = TextField()
    elseif (element.name == "CollectionView") then
        view = CollectionView()
    elseif (element.name == "RefreshCollectionView") then
        view = RefreshCollectionView()
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

function Pica:parseElement(element, parent)
    if (element.type == "comment") then
        return
    end

    local isContains = self:isViewElement(element)
    if (isContains == true and element.attr ~= nil) then
        for _, _v in ipairs(element.attr) do
            if (_v.name == "frame") then
                _v.value = _v.value:gsub("SCREEN_WIDTH", _screenWidth)
                _v.value = _v.value:gsub("SCREEN_HEIGHT", _screenHeight)
                if (parent and parent.name == "View") then
                    self.objs[parent]:addView(self.objs[element])
                end
                local paramFun = Common:loadString("return " .. _v.value)
                self.objs[element]:frame(paramFun())
            elseif (_v.name == "backgroundColor") then
                self.objs[element]:backgroundColor(tonumber(_v.value))
            elseif (_v.name == "id") then
                -- 考虑到性能问题,不采用遍历的方式来取得开发者所关注的对象。而是使用字典的形式来获取。
                self.identifierObjs[_v.value] = self.objs[element]
            elseif (_v.name == "flexCss") then
                self.objs[element]:flexCss(_v.value)
                if (not _isAndroid) then
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
                self.objs[element]:textColor(tonumber(_v.value))
            elseif (_v.name == "fontSize") then
                self.objs[element]:fontSize(tonumber(_v.value))
            elseif (_v.name == "lineCount") then
                self.objs[element]:lineCount(tonumber(_v.value))
            elseif (_v.name == "alpha") then
                self.objs[element]:alpha(tonumber(_v.value))
            elseif (_v.name == "text") then
                self.objs[element]:text(_v.value)
            elseif (_v.name == "textAlign") then
                local paramFun = Common:loadString("return " .. _v.value)
                self.objs[element]:textAlign(paramFun())
            elseif (_v.name == "ellipsize") then
                if (_isAndroid) then
                    local paramFun = Common:loadString("return " .. _v.value)
                    self.objs[element]:ellipsize(paramFun())
                end
            else
                print("LuaError::Layout", "方法名不对: " .. _v.name)
            end
        end
    end

    if (element.kids ~= nil) then
        for _, kid in pairs(element.kids) do
            self:parseElement(kid, element);
        end
    end
end

function Pica:getViewByName( name )
    return self.identifierObjs[name]
end

return Pica



