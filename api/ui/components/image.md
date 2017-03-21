> Class

## Image

图片组件，非容器。 继承自-> [BaseView](../baseview.html)


| ID   | API                  | 参数                                       | 返回值  | 平台      | 备注                |
| ---- | -------------------- | ---------------------------------------- | ---- | ------- | ----------------- |
| 1    | image                | v: String, c: LuaFunction                | v    | -       | 设置图片url（本地、网络），回调 |
| 2    | contentMode          | type: <a href="#scale_type">ScaleType</a> | type | Android | 图片缩放模式            |
| 3    | scaleType            | type: <a href="#scale_type">ScaleType</a> | type | -       | 图片缩放模式            |
| 4    | startAnimationImages | images: String[]                         | -    | -       | 帧动画（本地图）          |
| 5    | stopAnimationImages  | -                                        | -    | -       | 停止播放帧动画           |
| 6    | isAnimationImages    | -                                        | -    | -       | 是否正在播放帧动画         |

