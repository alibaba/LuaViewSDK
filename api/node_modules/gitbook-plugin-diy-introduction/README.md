Gitbook 定制侧边栏Introduction文案插件
==============

在 book.json 中加入:

```
{
    plugins: ["diy-introduction"],
    pluginsConfig: {
        "introduction-text": "封面"
    }
}
```

禁用:

```
{
    plugins: ["-diy-introduction"]
}
```