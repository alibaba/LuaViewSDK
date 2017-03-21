> Class

## WebView

html组件，容器，用于在LuaView页面中嵌入HTML页面，提高页面的灵活性。继承自 -> [View](view.html)

**注**：webView组件在被销毁之前会占用大量的内存，需要小心使用

---

### *方法*

| ID   | API               | 参数         | 返回值        | 平台   | 备注       |
| ---- | ----------------- | ---------- | ---------- | ---- | -------- |
| 1    | loadUrl           | v: String  | -          | -    | 加载url    |
| 2    | canGoBack         | -          | v: Boolean | -    | 是否可以回退   |
| 3    | canGoForward      | -          | v: Boolean | -    | 是否可以前进   |
| 4    | goBack            | -          | -          | -    | 回退一页     |
| 5    | goForward         | -          | -          | -    | 前进一页     |
| 6    | reload            | -          | -          | -    | 重新加载     |
| 7    | title             | -          | v: String  | -    | 获取Title  |
| 8    | isLoading         | -          | v: Boolean | -    | 是否正在加载   |
| 9    | stopLoading       | -          | -          | -    | 停止加载     |
| 10   | url               | -          | v: String  | -    | 获取url    |
| 11   | pullRefreshEnable | v: Boolean | v          | -    | 是否可以下拉刷新 |

