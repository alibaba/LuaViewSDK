--
-- Created by IntelliJ IDEA.
-- User: tuoli
-- Date: 17/3/7
-- Time: 20:57
-- To change this template use File | Settings | File Templates.
--

if (Common == nil) then
    Common = {}

    --function Common:setFlex(view, flex)
    --    local flexCss = ""
    --    for k, v in pairs(flex) do
    --        if (k == "flexDirection") then
    --            flexCss = flexCss .. "flex-direction: " .. v .. ","
    --        elseif (k == "marginLeft") then
    --            flexCss = flexCss .. "margin-left: " .. v .. ","
    --        elseif (k == "marginRight") then
    --            flexCss = flexCss .. "margin-right: " .. v .. ","
    --        elseif (k == "marginTop") then
    --            flexCss = flexCss .. "margin-top: " .. v .. ","
    --        elseif (k == "marginBottom") then
    --            flexCss = flexCss .. "margin-bottom: " .. v .. ","
    --        else
    --            flexCss = flexCss .. k .. ": " .. v .. ","
    --        end
    --    end
    --
    --    view:flexCss(flexCss)
    --end
    --
    --function Common:renderView(view, style)
    --    for _k, _v in pairs(style) do
    --        if (_k == "flexCss" and type(_v) == "table") then
    --            Common:setFlex(view, _v)
    --        end
    --
    --        if (_k == "property" and type(_v) == "table") then
    --            for k, v in pairs(_v) do
    --                if (k == "frame") then
    --                    view:frame(v[1], v[2], v[3], v[4])
    --                end
    --            end
    --        end
    --    end
    --end
    --
    --function Common:renderLabel(view, style)
    --    for _k, _v in pairs(style) do
    --        if (_k == "flexCss" and type(_v) == "table") then
    --            Common:setFlex(view, _v)
    --        end
    --
    --        if (_k == "property" and type(_v) == "table") then
    --            for k, v in pairs(_v) do
    --                if (k == "frame") then
    --                    view:frame(v[1], v[2], v[3], v[4])
    --                else
    --                    view[k](view, v)
    --                end
    --            end
    --        end
    --    end
    --end
    --
    --function Common:renderButton(view, style)
    --    for _k, _v in pairs(style) do
    --        if (_k == "flexCss" and type(_v) == "table") then
    --            Common:setFlex(view, _v)
    --        end
    --
    --        if (_k == "property" and type(_v) == "table") then
    --            for k, v in pairs(_v) do
    --                if (k == "frame") then
    --                    view:frame(v[1], v[2], v[3], v[4])
    --                elseif (k == "image") then
    --                    view:image(v[1], v[2])
    --                else
    --                    view[k](view, v)
    --                end
    --            end
    --        end
    --    end
    --end

    function Common:printTable(t, exclusions)
        if (type(t) == "table") then
            local nests = 0
            if not exclusions then exclusions = {} end
            local recurse = function(t, recurse, exclusions)
                indent = function()
                    local dent = ""
                    for i = 1, nests do
                        dent = dent .. "    "
                    end
                    return dent
                end
                local excluded = function(key)
                    for k, v in pairs(exclusions) do
                        if v == key then
                            return true
                        end
                    end
                    return false
                end
                local isFirst = true
                for k, v in pairs(t) do
                    local dent = indent()
                    if isFirst then
                        print(dent .. "|")
                        isFirst = false
                    end
                    dent = indent()
                    if type(v) == "table" and not excluded(k) then
                        print(dent .. "|- " .. tostring(k) .. ": " .. type(v))
                        nests = nests + 1
                        recurse(v, recurse, exclusions)
                    elseif excluded(k) then
                        print(dent .. "|- " .. tostring(k) .. ": " .. type(v))
                    elseif type(v) == "userdata" or type(v) == "function" then
                        print(dent .. "|- " .. tostring(k) .. ": " .. type(v))
                    elseif type(v) == "string" then
                        print(dent .. "|- " .. tostring(k) .. ": " .. "\"" .. tostring(v) .. "\"")
                    else
                        print(dent .. "|- " .. tostring(k) .. ": " .. tostring(v))
                    end
                end
                nests = nests - 1
            end

            nests = 0

            if t then
                print("### START TABLE ###")
                for k, v in pairs(t) do
                    if type(v) == "table" then
                        print("|- " .. k .. ": " .. type(v))
                        nests = nests + 1
                        recurse(v, recurse, exclusions)
                    elseif type(v) == "userdata" or type(v) == "function" then
                        print("|- " .. tostring(k) .. ": " .. type(v))
                    elseif type(v) == "string" then
                        print("|- " .. tostring(k) .. ": " .. "\"" .. tostring(v) .. "\"")
                    else
                        print("|- " .. tostring(k) .. ": " .. tostring(v))
                    end
                end
                print("### END TABLE ###")
            else
                print("NIL TABLE")
            end
        else
            print(t)
        end
    end
end
