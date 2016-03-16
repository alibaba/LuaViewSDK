--- AudioPlayer
-- 声音播放器
-- @classmodAudio AudioPlayer

--- 创建声音播放器
-- @string url 声音资源(支持本地文件和网络资源)
-- @treturn AudioPlayer player 播放器
-- @usage 
-- w,h = System.screenSize();
-- player = AudioPlayer("http://g.tbcdn.cn/ju/lua/1.2.0/shake.js");
-- button = Button();
-- button.title("播放音效");
-- button.frame(0,100,w,40);
-- button.backgroundColor(0xff,1);
-- button.callback( function()
-- 	if( player ) then
-- 		player.play();
-- 	end
-- end)
function AudioPlayer()
end


--- 开始播放
function play()
end

--- 停止播放
function stop()
end
