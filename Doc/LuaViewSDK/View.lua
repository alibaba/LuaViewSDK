--- View 
-- 所有UI控件的基类
-- @classmod View

 --- summary.
 -- Description; this can extend over
 -- several lines

 -----------------
 -- This will also do.

--- 通过这个方法创建一个View对象
-- @treturn View view
 -- @usage local view = View();
function View()
end


--- 设置View的位置和大小
-- @number x x坐标
-- @number y y坐标
-- @number w 宽
-- @number h 高
-- @usage local view = View(); 
-- view.frame(10,10,100,100);
--
function frame()
end

--- 获取View的位置和大小
 -- @usage local x,y,w,h = view.frame();
 -- @treturn  number x
 -- @treturn  number y
 -- @treturn  number w
 -- @treturn  number h
function frame()
end

--- 设置View的背景色
-- @int color 颜色值
-- @number alpha 透明度
-- @usage local view = View(); 
-- view.backgroundColor(0xff0000 );
-- --或者
-- view.backgroundColor(0xff0000,0.5);
--
function backgroundColor()
end

--- 获取View的背景色
 -- @treturn  int color 颜色值(例如0xff0000)
 -- @treturn  number alpha 透明度(0~1)
 -- @usage local color, alpha = view.backgroundColor();
function backgroundColor()
end

--- 设置View的大小
-- @number w 宽
-- @number h 高
-- @usage local view = View(); 
-- view.size(100,100);
--
function size()
end

--- 获取View的大小
 -- @usage local w,h = view.size();
 -- @treturn  number w
 -- @treturn  number h
function size()
end

--- 设置View的位置
-- @number x x坐标
-- @number y y坐标
-- @usage local view = View(); 
-- view.origin(10,10);
--
function origin()
end

--- 获取View的位置
 -- @usage local x,y = view.orgrin();
 -- @treturn  number x
 -- @treturn  number y
function orgrin()
end

--- 设置View的位置
-- @number x x坐标
-- @number y y坐标
-- @usage local view = View(); 
-- view.xy(10,10);
--
function xy()
end

--- 获取View的位置
 -- @usage local x,y = view.xy();
 -- @treturn  number x
 -- @treturn  number y
function xy()
end

--- 设置View的中心点位置
 -- @number x 中心坐标x
 -- @number y 中心坐标y
 -- @usage local view = View(); 
 -- view.center(100,100);
function center()
end

--- 获取View的中心点位置
 -- @treturn x x中心坐标
 -- @treturn y y中心坐标
 -- @usage local x,y = view.center();
 --
function center()
end

--- 设置View的x坐标
-- @number x x坐标
-- @usage local view = View(); 
-- view.x(10);
--
function x()
end

--- 获取View的x坐标
 -- @usage local x= view.x();
 -- @treturn  number x
function x()
end

--- 设置View的y坐标
-- @number y y坐标
-- @usage local view = View(); 
-- view.y(10);
--
function y()
end

--- 获取View的y坐标
 -- @usage local y = view.y();
 -- @treturn  number y
function y()
end

--- 设置View的y坐标
-- @number y y坐标
-- @usage local view = View(); 
-- view.top(10);
--
function top()
end

--- 获取View的y坐标
 -- @usage local y = view.top();
 -- @treturn  number y
 --
function top()
end

--- 设置View的x位置
-- @number x x坐标
-- @usage local view = View(); 
-- view.left(10);
--
function left()
end

--- 获取View的x坐标
 -- @usage local x= view.left();
 -- @treturn  number x
function left()
end

--- 设置View的bottom坐标
-- @number bottom bottom坐标
-- @usage local view = View(); 
-- view.bottom(10);
--
function bottom()
end

--- 获取View的bottom坐标
 -- @usage local botom = view.bottom();
 -- @treturn  number bottom
function bottom()
end

--- 设置View的right位置
-- @number right right坐标
-- @usage local view = View(); 
-- view.right(10);
--
function rigth()
end

--- 获取View的right坐标
 -- @usage local right = view.right();
 -- @treturn  number right
function rigth()
end

--- 设置View的宽
-- @number w 宽
-- @usage local view = View(); 
-- view.width(100);
--
function width()
end

--- 获取View的宽
 -- @usage local w = view.width();
 -- @treturn  number w
function width()
end

--- 设置View的高
-- @number h 高
-- @usage local view = View(); 
-- view.height(100);
--
function height()
end

--- 获取View的高
 -- @usage local h = view.height();
 -- @treturn  number h
function height()
end

--- 设置View的中心点x坐标
 -- @number x 中心坐标x
 -- @usage local view = View(); 
 -- view.centerX(100);
function centerX()
end

--- 获取View的中心点x坐标
 -- @treturn number x x中心坐标
 -- @usage local x = view.centerX();
 --
function centerX()
end
--- 设置View的中心点y坐标
 -- @number y 中心坐标y
 -- @usage local view = View(); 
 -- view.centerY(100);
function centerY()
end

--- 获取View的中心点y坐标
 -- @treturn number y y中心坐标
 -- @usage local y = view.centerX();
 --
function centerY()
end

--- 设置可见状态
 -- @bool hidden true: 隐藏   false:可见
 -- @usage local view = View(); 
 -- view.hidden(false);
function hidden()
end

--- 获取可见状态
 -- @treturn bool 是否隐藏
 -- @usage view.hidden();
function hidden()
end

--- 隐藏 view
 -- @usage view.hide();
function hide()
end

--- 是否隐藏 view
 -- @usage view.isHide();
function isHide()
end

--- 显示 view
 -- @usage view.hide();
function show()
end

--- 是否显示 view
 -- @usage view.hide();
function isShow()
end

--- 设置View enable控制是否接收触摸事件
 -- @bool enable true/false
 -- @usage local view = View(); 
 -- view.enabled(false);
function enabled();
end

--- 获取View enable状态
 -- @treturn bool true/false
 -- @usage local ret = view.userInteractionEnabled();
function enabled();
end


--- 设置alpha值
 -- @number alpha 透明度(0~1.0)
 -- @usage local view = View(); 
 -- view.alpha(0.5);
function alpha(); 
end 
--- 获取alpha值
 -- @treturn number alpha值
 -- @usage local alpha = view.alpha();
function alpha();
end

--- 设置边框圆角半径
 -- @number radius 半径
 -- @usage local view = View(); 
 -- view.cornerRadius(8);
function cornerRadius();
end

--- 获取边框圆角半径
 -- @treturn number radius 半径
 -- @usage local r = view.cornerRadius();
 -- --或者 view.cornerRadius(8);
function cornerRadius();
end

--- 设置边框粗细
 -- @number borderWidth 
 -- @usage local view = View(); 
 -- view.borderWidth(3);
function borderWidth(); 
end

--- 获取边框粗细
 -- @treturn number borderWidth 
 -- @usage local borderWidth = view.borderWidth();
function borderWidth(); 
end

--- 设置边框颜色
 -- @int color 颜色值
 -- @number alpha 透明度
 -- @usage local view = View(); 
 -- view.borderColor(0x0000ff); -- alpha省缺默认是1
 -- --或者 
 -- view.borderColor(0x0000ff,0.5);
function borderColor();
end

--- 获取边框颜色
 -- @treturn int color 颜色值
 -- @treturn number alpha 透明度
 -- @usage local color,alpha = view.borderColor();
function borderColor();
end


--- 设置 只对边框外部加阴影
 -- @bool shadowPath 只对边框外部加阴影
 -- @usage local view = View(); 
 -- view.shadowPath(true);
function shadowPath();
end

--- 设置边框是否裁剪
 -- @bool masksToBounds 是否边框剪切
 -- @usage local view = View(); 
 -- view.masksToBounds(true);
function masksToBounds(); --
end

--- 设置View阴影偏移位置
 -- @number x x偏移
 -- @number y y偏移
 -- @usage local view = View(); 
 -- view.shadowOffset(2,2);
function shadowOffset()
end

--- 设置View阴影高斯模糊半径
 -- @number r 模糊半径
 -- @usage local view = View(); 
 -- view.shadowRadius(3);
function shadowRadius()
end

--- 设置View阴影透明度
 -- @number alpha 透明度
 -- @usage local view = View(); 
 -- view.shadowOpacity(0.5);
function shadowOpacity()
end

--- 设置View阴影颜色
 -- @int color 颜色值
 -- @number alpha 透明度
 -- @usage local view = View(); 
 -- view.shadowColor(0xff0000, 0.5);
function shadowColor()
end

--- 适应View内容的大小
 -- @usage local label = Label(); 
 -- label.text("测试");
 -- label.adjustSize();
function adjustSize()
end

--- 添加手势
 -- @Gesture gesture 手势
 -- @usage local g = SwipeGesture(^(gesture){
 --     if( gesture.state()==GestureState.END ) then
 --          print( "两个手势向左滑动" );
 --     end
 -- });
 -- g.touchCount(2); -- 两个手指才出发
 -- g.direction( GestureDirection.LEFT ); -- 手势方向
 -- window.addGesture(g);
function addGesture()
end


--- 移除手势
 -- @Gesture gesture 手势
 -- @usage view.addGesture( gesture );
function removeGesture()
end

---添加子类
 -- @View view view
 -- @usage local view = View(); 
 -- local button = Button();
 -- view.addSubview(button);
function addView(); 
end

---从父类中移除自身
 -- @usage button.removeFromSuper();
function removeFromSuper();
end

---移除所有子类
 -- @usage button.removeAllViews();
function removeAllViews();
end

--- 设置view 绕Z轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
 -- @usage local view = View(); 
 -- view.rotation(3.14);
 -- 
function rotation()
end

--- 获取view绕Z轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
function rotation()
end

--- 设置view 绕X轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
 -- @usage local view = View(); 
 -- view.rotationX(3.14 );
 -- 
function rotationX()
end

--- 获取view绕X轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
function rotationX()
end

--- 设置view 绕Y轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
 -- @usage local view = View(); 
 -- view.rotationY(3.14 );
 -- 
function rotationY()
end

--- 获取view绕Y轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
function rotationY()
end

--- 设置view 绕Z轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
 -- @usage local view = View(); 
 -- view.rotationY(3.14 );
 -- 
function rotationZ()
end

--- 获取view绕Z轴旋转弧度
 -- @number angle View逆时针绕中心点旋转的弧度
function rotationZ()
end

--- 设置view缩放
 -- @number scaleX x轴缩放大小
 -- @number scaleY y轴缩放大小
 -- @usage local view = View(); 
 -- view.scale(0.8, 0.8);
 -- 
function scale()
end

--- 获取view X轴 Y轴 缩放
 -- @treturn number scaleX x轴缩放大小
 -- @treturn number scaleY y轴缩放大小
function scale()
end

--- 设置view X轴缩放
 -- @number scaleX x轴缩放大小
 -- @usage local view = View(); 
 -- view.scaleX(0.8);
 -- 
function scaleX()
end

--- 获取view缩放
 -- @treturn number scaleX x轴缩放大小
function scaleX()
end

--- 设置view Y轴缩放
 -- @number scaleY y轴缩放大小
 -- @usage local view = View(); 
 -- view.scaleY(0.8);
 -- 
function scaleY()
end

--- 获取view缩放
 -- @treturn number scaleY y轴缩放大小
function scaleY()
end

--- 设置3D变换矩阵
 -- @Transform3D transform3D 变换矩阵
function transform3D()
end


--- 设置中心点位置
 -- @number x 数值区间: 0~1
 -- @number y 数值区间: 0~1
 -- @usage view.anchorPoint(0.5, 0.5);
function anchorPoint()
end

--- 获取中心点位置
 -- @treturn number x 数值区间: 0~1
 -- @treturn number y 数值区间: 0~1
function anchorPoint()
end

--- 设置回调事件处理方法
 -- @tparam function delegate 代理方法
 -- @usage view.callback( function()
 --     -- code
 -- end );
function callback()
end


--- 是否 Focus
 -- @treturn bool yes
function hasFocus()
end

--- focus
 -- @treturn bool yes
function requestFocus()
end

--- 取消 focus
 -- @treturn bool yes
function cancelFocus()
end




--- View水平方向左对齐
function alignLeft()
end

--- View水平方向右对齐
function alignRight()
end

--- View垂直方向上对齐
function alignTop()
end

--- View垂直方向下对齐
function alignBottom()
end


--- 设置View对齐方式
-- Align.LEFT Align.RIGHT .TOP .BOTTOM .H_CENTER .V_CENTER
 -- @usage view.anchorPoint(Align.LEFT,Align.V_CENTER);
 -- @Align align 水平对齐方式
 -- @Align align 垂直对齐方式
 --
function align()
end

--- 获取View对齐方式
-- Align.LEFT Align.RIGHT .TOP .BOTTOM .H_CENTER .V_CENTER
 -- @usage view.anchorPoint();
function align()
end


 