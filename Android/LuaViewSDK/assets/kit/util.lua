--
-- Copyright 2017 Alibaba Group
-- License: MIT
-- Website: https://alibaba.github.io/LuaViewSDK
-- User: tuoli
-- Date: 17/3/30
--

if (util == nil) then
    util = {}
    function util:printTable(t, exclusions)
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