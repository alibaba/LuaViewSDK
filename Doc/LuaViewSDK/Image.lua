--- Image 
-- 放图片的View
--
-- Image的父类是[View](./../ui容器/View.html)
-- @classmodUIItem Image


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
-- 
function image()
end


--- 开始帧动画
-- @tparam array images 帧图片数组
-- @number duration 动画时间
-- @int repeatCount 重复次数
-- @usage 
-- scrW, scrH = System.screenSize();
-- window.backgroundColor(0x777777,1);
-- w = scrW/3;
-- X0 = scrW/10;
-- Y0 = scrH/5;
-- imageView = Image();
-- imageView.frame(X0, Y0, w, w);
-- imageView.backgroundColor(0xff0000,1);
-- names = {};
-- for i=1,16 do
-- 	names[i] = "img" .. i .. ".tiff"
-- end
-- imageView.image("img16.tiff");
-- imageView.startAnimationImages( names, 3.0, 1000);
function startAnimationImages()
end

--- 停止帧动画
-- @usage 
-- imageView.stopAnimatingImages();
function stopAnimationImages()
end

--- 是否正在播放帧动画
-- @treturn bool 是否正在播放帧动画
function isAnimationImages()
end

-- {"render",  render},
-- {"renderSystemApi",  renderSystemApi},
-- {"resizeImage",  resizeImage},


