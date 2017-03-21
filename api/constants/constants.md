> Static Class & Constants

## Align

View布局常量，静态类

| ID   | 值        | 平台      | 备注     |
| ---- | -------- | ------- | ------ |
| 1    | LEFT     | -       | 左对齐    |
| 2    | TOP      | -       | 顶对齐    |
| 3    | RIGHT    | -       | 右对齐    |
| 4    | BOTTOM   | -       | 底对齐    |
| 5    | CENTER   | -       | 整体居中   |
| 6    | H_CENTER | -       | 水平居中   |
| 7    | V_CENTER | -       | 垂直居中   |
| 8    | START    | Android | 左or上对齐 |
| 9    | END      | Android | 右or下对齐 |


## TextAlign
文本布局常量，静态类

| ID   | 值      | 平台   | 备注     |
| ---- | ------ | ---- | ------ |
| 1    | LEFT   | -    | 左&垂直居中 |
| 2    | RIGHT  | -    | 整体居中   |
| 3    | CENTER | -    | 右&垂直居中 |

## FontWeight

字体大小常量，静态类

| ID   | 值      | 平台   | 备注   |
| ---- | ------ | ---- | ---- |
| 1    | NORMAL | -    | 正常   |
| 2    | BOLD   | -    | 粗体   |

## FontStyle

字体样式常量，静态类

| ID   | 值      | 平台   | 备注   |
| ---- | ------ | ---- | ---- |
| 1    | NORMAL | -    | 正常   |
| 2    | ITALIC | -    | 斜体   |
| 3    | BOLD   | -    | 粗体   |

## ScaleType

图片样式常量，静态类

| ID   | 值             | 平台   | 备注     |
| ---- | ------------- | ---- | ------ |
| 1    | FIT_XY        | -    | 左上铺满   |
| 2    | FIT_START     | -    | 左or上铺满 |
| 3    | FIT_END       | -    | 右or下铺满 |
| 4    | FIT_CENTER    | -    | 居中铺满   |
| 5    | CENTER        | -    | 居中     |
| 6    | CENTER_CROP   | -    | 居中裁剪   |
| 7    | CENTER_INSIDE | -    | 居中包含   |
| 8    | MATRIX        | -    | 矩阵     |

## Gravity
布局样式常量，静态类

| ID   | 值        | 平台      | 备注     |
| ---- | -------- | ------- | ------ |
| 1    | LEFT     | Android | 左对齐    |
| 2    | TOP      | Android | 上对齐    |
| 3    | RIGHT    | Android | 右对齐    |
| 4    | BOTTOM   | Android | 下对齐    |
| 5    | START    | Android | 左or上对齐 |
| 6    | END      | Android | 右or下对齐 |
| 7    | CENTER   | Android | 居中对齐   |
| 8    | H_CENTER | Android | 水平居中对齐 |
| 9    | V_CENTER | Android | 垂直居中对齐 |
| 10   | FILL     | Android | 铺满     |
| 11   | H_FILL   | Android | 水平铺满   |
| 12   | V_FILL   | Android | 垂直铺满   |

## Ellipsize

文本省略样式常量，静态类

| ID   | 值       | 平台   | 备注     |
| ---- | ------- | ---- | ------ |
| 1    | START   | -    | 起始位置省略 |
| 2    | MIDDLE  | -    | 中间位置省略 |
| 3    | END     | -    | 结束为止省略 |
| 4    | MARQUEE | -    | 跑马灯    |


## Interpolator
动画差值器常量，静态类

| ID   | 值                     | 平台      | 备注       |
| ---- | --------------------- | ------- | -------- |
| 1    | ACCELERATE_DECELERATE | -       | 先加速后减速插值 |
| 2    | ACCELERATE            | -       | 加速插值     |
| 3    | ANTICIPATE            | -       | 预期插值     |
| 4    | ANTICIPATE_OVERSHOOT  | -       | 预期弹性插值   |
| 5    | BOUNCE                | Android | 回弹插值     |
| 6    | CYCLE                 | Android | 环形插值     |
| 7    | DECELERATE            | -       | 减速插值     |
| 8    | LINEAR                | -       | 线性插值     |
| 9    | OVERSHOOT             | Android | 弹性插值     |

## ViewEffect
View特效常量，静态类

| ID   | 值        | 平台      | 备注      |
| ---- | -------- | ------- | ------- |
| 1    | NONE     | -       | 无特效     |
| 2    | CLICK    | Android | 点击水波纹特效 |
| 3    | PARALLAX | -       | 视差特效    |

## PaintStyle
Canvas画笔样式常量，静态类，参考 [Canvas](../ui/graphics.html)


| ID   | 值            | 平台   | 备注   |
| ---- | ------------ | ---- | ---- |
| 1    | FILL         | -    | 填充   |
| 2    | STROKE       | -    | 中空   |
| 3    | EOFILL       | IOS  | 中空   |
| 4    | FILLSTROKE   | IOS  | 中空   |
| 5    | EOFILLSTROKE | IOS  | 中空   |
