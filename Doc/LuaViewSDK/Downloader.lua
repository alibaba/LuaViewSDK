--- Downloader
-- 简单的文件读写
-- @classmod Downloader

--- 获取文件的绝对路径
-- @string fileName 文件名
-- @treturn string 绝对路径
function Downloader.pathOfResource()
end


--- 下载文件
-- @string url url资源 http://xxxx.xx/xxx
-- @string fileName 本地保存的文件名
-- @tparam function function(Data) 下载成功后回调
-- @usage
-- Download("http://g.tbcdn.cn/ju/lua/1.2.0/shake.js","shake.wav",^(data){
-- 	print( data );
-- 	player = AudioPlayer("shake.wav");
-- });
-- button = Button();
-- button.title("播放音效");
-- button.frame(0,100,w,40);
-- button.backgroundColor(0xff,1);
-- button.callback = ^(){
-- 	if( player ) then  
-- 		player.play();
-- 	end
-- }
function Downloader.download()
end

