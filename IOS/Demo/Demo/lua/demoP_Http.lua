
local http = Http();
http:post("http://www.taobao.com",
    {
        UserId = "23234234",
        Year = "2016",
        Moth = "3",
        Parts = { 
            { PartId = "48"},
            {PartId = "12"} 
        }
    },
    function ( response )
        local str = tostring( response:data() );
        print(str);
        local object = Json:toTable(str);

        if( callback ) then
            callback(object);
        end
    end
    )
