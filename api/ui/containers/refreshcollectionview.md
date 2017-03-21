> Class

## RefreshCollectionView

可刷新列表组件，容器。继承自 -> [CollectionView](collectionview.html)

---

### *方法*


| ID   | API             | 参数         | 返回值        | 平台   | 备注      |
| ---- | --------------- | ---------- | ---------- | ---- | ------- |
| 1    | refreshEnable   | v: Boolean | -          | -    | -       |
| 2    | initRefreshing  | -          | -          | iOS  | 初始化刷新组件 |
| 3    | isRefreshing    | -          | v: Boolean | -    | 是否正在刷新  |
| 4    | startRefreshing | -          | -          | -    | 开始刷新    |
| 5    | stopRefreshing  | -          | -          | -    | 停止刷新    |

### *示例*

```lua
-- RefreshCollectionView 例子
local cv = RefreshCollectionView {
    ...
    Callback = {-- 相比CollectionView，多了PullDown回调
        ...
        PullDown = function()
          -- 下拉刷新回调
        end
    }
  }
```

