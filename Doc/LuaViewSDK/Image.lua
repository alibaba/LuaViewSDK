--- Image 
-- 放图片的View
--
-- Image的父类是[View](./View.html)
-- @classmod Image


--- summary.
-- Description; this can extend over
-- several lines

-----------------
-- This will also do.


--  ---
-- -- 这是一个人的类，它有姓名和年龄两个属性
-- -- 在这个类中，我们规定了name和age的类型
-- -- @string name
-- -- @int age
-- -- @tparam person father
-- person = {
--     name = "",
--     age = 0,
--     father = nil
-- }


--- 通过这个方法创建一个Image对象
-- @treturn Image view
-- @usage local view = Image();
function Image()
end


--- 设置ImageView的图片url
-- @string url image url
-- @usage local view = Image(); 
-- view.image("https://gju2.alicdn.com/bao/uploaded/TB25bF4eFXXXXbzXpXXXXXXXXXX_!!0-0-juitemmedia.jpg_560x560Q90.jpg");
function image()
end


--- 开始帧动画
-- @tparam array images 帧图片数组
-- @number duration 动画时间
-- @int repeatCount 重复次数
-- @usage 
-- w,h = System.screenSize();
-- window.frame(0, 0, w, h);
-- window.enabled(true);
-- window.backgroundColor(0xffffff,1);
-- imageView = Image();
-- imageView.frame(0,30,100,100);
-- imageView.backgroundColor(0xff0000,1);
-- names = {};
-- for i=1,16 do
-- 	  names[i] = "" .. i .. ".tiff"
-- end
-- imageView.image("16.tiff");
-- imageView.startAnimating( names, 3.0, 1);
function startAnimation()
end

--- 停止帧动画
-- @usage 
-- imageView.stopAnimating();
function stopAnimation()
end

--- 是否正在播放帧动画
-- @treturn bool 是否正在播放帧动画
function isAnimating()
end

-- {"render",  render},
-- {"renderSystemApi",  renderSystemApi},
-- {"resizeImage",  resizeImage},


