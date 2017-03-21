> Class

## HScrollView

横向滑动组件，容器。继承自 [View](view.html)

---

### *方法*

| ID   | API            | 参数                                       | 返回值  | 平台      | 备注                       |
| ---- | -------------- | ---------------------------------------- | ---- | ------- | ------------------------ |
| 1    | offset         | x: Number<br/> y: Number<br/> smooth: Boolean | x, y | -       | 滚动到x,y，smooth表示是否平滑滚动    |
| 2    | scrollTo       | x: Number<br/> y: Number<br/> smooth: Boolean | -    | Android | 滚动到x,y，smooth表示是否平滑滚动    |
| 3    | offsetBy       | dx: Number<br/> dy: Number<br/> smooth: Boolean | x, y | Android | 滚动dx,dy，smooth表示是否平滑滚动   |
| 4    | scrollBy       | dx: Number<br/> dy: Number<br/> smooth: Boolean | -    | Android | 滚动dx,dy，smooth表示是否平滑滚动   |
| 5    | smoothScrollTo | x: Number<br/> y: Number                 | -    | Android | 平滑滚动到x,y                 |
| 6    | smoothScrollBy | x: Number<br/> y: Number                 | -    | Android | 滚动到x,y                   |
| 7    | pageScroll     | direction: Number                        | -    | Android | 滚动一页(direction>0右滚，否则左滚) |
| 8    | fullScroll     | direction: Number                        | -    | Android | 滚动到底(direction>0右滚，否则左滚) |
| 9    | contentSize    | -                                        | -    | iOS     | 内容区域大小                   |