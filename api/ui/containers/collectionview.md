> Class

## CollectionView

基础列表组件，容器。继承自-> [View](view.html)

---

### *方法*


| ID   | API                 | 参数                                       | 返回值   | 平台      | 备注                             |
| ---- | ------------------- | ---------------------------------------- | ----- | ------- | ------------------------------ |
| 1    | reload              | section: Number<br/> row: Number<br/>    | -     | -       | Android支持参数                    |
| 5    | showScrollIndicator | v: Boolean                               | v     | -       | 是否显示滚动条信息                      |
| 6    | scrollToTop         | offset: Number<br/> animate: Boolean     | -     | -       | 滚动到顶部(offset间隔，animate是否动画)    |
| 7    | scrollToCell        | section: Number<br/> rowInSection: Number<br/> offset: Number<br/> animate: Boolean | -     | -       | 滚动到指定cell，offset间隔，animate是否动画 |
| 8    | miniSpacing         | space: Number                            | space | -       | cell间隙                         |
| 9    | lazyLoad            | v: Boolean                               | -     | Android | 是否懒加载Cell                      |

### *示例*

> CollectionView的基本使用示例

```lua
-- CollectionView 例子
local cv = CollectionView { --使用一个table来对CollectionView进行初始化
    Section = { 
        SectionCount = function()
            -- 返回页面区块的个数（不同区块的种类数）
            return sectionCount
        end,
        RowCount = function(section) -- RowCount函数用于返回指定section中的row count
            if(section == 1) then
                return rowCount1
            else
                return rowCount2
            end
        end
    },
    Cell = {
        Id = function(section, row)
            -- 返回每个区块对应的坑位ID，这个ID用于在之后进行对应cell的行为设置
            if (section == 1) then
                return "Label"
            elseif(section == 2) then
                return "ImageAndLabel"
            end
        end,
        Label = { -- ID为Label的cell的回调方法，Size为大小设置，Init为初始化方法，Layout为布局方法，Callback为点击回调方法
            Size = function(section, row)
                return w, cellHeight
            end,
            Init = function(cell, section, row)
                cell.title = Label()
            end,
            Layout = function(cell, section, row)
                cell.title:frame(0, 0, w - cellHeight, cellHeight)
                cell.title:text("测试" .. section .. "--" .. row .. "--" .. rowTitle)
                cell.title:backgroundColor(0xdcdcdc)
            end
        },
        ImageAndLabel = {
            Size = function(section, row)
                -- 返回Cell的宽、高
                return w, cellHeight
            end,
            Init = function(cell, section, row)
                -- Cell的初始化，一般在这里创建对应的Cell各个UI元素
                cell.icon = Image()
                cell.title = Label()
            end,
            Layout = function(cell, section, row)
                -- Cell的布局，一般在这里对Cell的各个UI元素进行布局（设置位置、内容）
                cell.icon:frame(20, 0, cellHeight, cellHeight)
                cell.icon:image(imageUrl1)

                cell.title:frame(20 + cellHeight, 0, w - cellHeight, cellHeight)
                cell.title:text("测试" .. section .. "--" .. row .. "--" .. rowTitle)
            end,
            Callback = function(section, row)
                -- 在这里处理Cell的点击事件，或者长按时间，默认处理的是点击事件，长按时间需要定义Callback为表                
            end
        }
    },
        Callback = {-- 整个CollectionView的事件回调
        Scrolling = function( firstVisibleSection, firstVisibleRow, visibleCellCount )
          -- 滚动中回调
        end,
        ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
          -- 滚动开始回调
        end,
        ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount )
          -- 滚动结束回调
        end
    }
    }
```



