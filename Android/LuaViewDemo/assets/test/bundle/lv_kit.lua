-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

-- LuaView 相关的kit
-- Created by IntelliJ IDEA.
-- User: song
-- Date: 15/9/8
-- Time: 上午10:33
--


--1. Lua Table View by Elertan
function printTable(t, exclusions)
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
