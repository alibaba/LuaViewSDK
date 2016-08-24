require "kit/lv_kit"

dic = {
    ["spm-url"] = "a2141.7662935.click.4",
    spm = "a111.222.sdfsdf.123.sdfsd",
    title = "测试"
}

printTable(dic)

-- 分割字符串
local function slitString(szFullString, szSeparator, len)
    local len = len or string:len(szSeparator)
    local nFindStartIndex = 1
    local nSplitIndex = 1
    local nSplitArray = {}
    for i=1,100 do
        local nFindLastIndex = string:find(szFullString, szSeparator, nFindStartIndex)
        if not nFindLastIndex then
            nSplitArray[nSplitIndex] = string:sub(szFullString, nFindStartIndex, string:len(szFullString))
            return nSplitArray
        end
        nSplitArray[nSplitIndex] = string:sub(szFullString, nFindStartIndex, nFindLastIndex - 1)
        nFindStartIndex = nFindLastIndex + len
        nSplitIndex = nSplitIndex + 1
    end
    return nSplitArray
end

local function changeDic(dic, key)
    dic = dic or {}
    local spm_url = dic and dic[key]
    if(spm_url) then
       local spms =  slitString(spm_url, "%.", 1)
        if(spms and #spms >= 4) then
            if(spms[4]) then
                local firstChar = string:sub(spms[4], 1, 1)
                local hasD = #spms[4] >= 1 and firstChar == 'd'
                if(not hasD) then
                    spms[4] = "d" .. spms[4]
                    spm_url = table:concat(spms, ".")
                    dic[key] = spm_url
                end
            end

        end
    end
    return dic
end


dic = changeDic(dic, "spm-url")
dic = changeDic(dic, "spm")

print("changeDic:result")
printTable(dic)