--- Transform3D
-- 用于设置View的3D变换矩阵
-- @classmod Transform3D

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


--- 通过这个方法创建一个Transform3D对象
-- @treturn Transform3D 变换矩阵
-- @usage local t = Transform3D();
function Transform3D()
end


--- 设置矩阵为平移变换
-- @number translationX x轴平移
-- @number translationY y轴平移
-- @number translationZ y轴平移
-- @treturn Transform3D 矩阵自身
-- @usage local t = Transform3D(); 
-- t.translation( 10, 100, 50);
-- imageView.transform3D(t);
function translation()
end


--- 设置矩阵为缩放变换
-- @number scaleX x轴缩放
-- @number scaleY y轴缩放
-- @number scaleZ z轴缩放
-- @treturn Transform3D 矩阵自身
-- @usage local t = Transform3D(); 
-- t.scale( 3.14/3, 0, 1);
-- imageView.transform3D(t);
function scale()
end


--- 设置矩阵为旋转变换
-- @number roteX x轴旋转
-- @number roteY y轴旋转
-- @number roteZ z轴旋转
-- @treturn Transform3D 矩阵自身
-- @usage local t = Transform3D(); 
-- t.rotate( 3.14/3, 0, 1);
-- imageView.transform3D(t);
function rotate()
end


--- 检测矩阵是否是单位矩阵
-- @treturn bool 是否是单位矩阵
-- @usage local t = Transform3D(); 
-- t.rotate( 3.14/3, 0, 1);
-- imageView.transform3D(t);
function isIdentity()
end

--- 初始化矩阵为单位矩阵
-- @treturn Transform3D 矩阵自身
-- @usage local t = Transform3D(); 
-- t.reset();
function reset()
end


--- 矩阵赋值
-- @Transform3D transform 矩阵
-- @treturn Transform3D 矩阵自身
-- @usage local transform1 = Transform3D(); 
-- local transform2 = Transform3D(); 
-- transform1.set(transform2);
function set()
end


--- 矩阵相乘
-- @Transform3D transform 矩阵
-- @treturn Transform3D 矩阵自身
-- @usage local transform1 = Transform3D(); 
-- local transform2 = Transform3D(); 
-- transform1.concat(transform2);
function concat()
end

--- 比较矩阵是否相同
-- @Transform3D transform 矩阵
-- @treturn bool 是否相等
-- @usage local transform1 = Transform3D(); 
-- local transform2 = Transform3D(); 
-- if( transform1 == transform2) ){
--     --相同
-- } else {
--    -- 不相同
-- }
function __eq()
end

--- 矩阵相乘
-- @Transform3D transform 矩阵
-- @treturn Transform3D 新的矩阵
-- @usage local transform1 = Transform3D(); 
-- local transform2 = Transform3D(); 
-- local transform3 = transform1 * transform2;
function __mul()
end







