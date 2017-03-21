> Class

## Label

文本组件，非容器。继承自-> [BaseView](../baseview.html)

---

### *方法*

| ID   | API            | 参数                                       | 返回值        | 平台      | 备注       |
| ---- | -------------- | ---------------------------------------- | ---------- | ------- | -------- |
| 1    | text           | v: String/<a href="#styled_string">StyledString/<a href="#unicode">Unicode</a> | v          | -       | Label文本  |
| 2    | textColor      | color: Number                            | color      | -       | 文本颜色     |
| 3    | textSize       | size: Number                             | size       | -       | 文本字体大小   |
| 4    | fontSize       | size: Number                             | size       | -       | 文本字体大小   |
| 5    | fontName       | name: String                             | name       | -       | 文本字体     |
| 6    | font           | name: String<br/> size: Number           | name, size | -       | 文本字体&大小  |
| 7    | gravity        | v: <a href="#gravity">Gravity</a>        | v          | -       | 文本对齐方式   |
| 8    | textAlign      | v: <a href="#text_align">TextAlign</a>   | v          | -       | 文本对齐方式   |
| 9    | lines          | v: Number                                | v          | Android | 文字行数     |
| 10   | maxLines       | v: Number                                | v          | -       | 文本最大行数   |
| 11   | lineCount      | v: Number                                | v          | -       | 文本最大行数   |
| 12   | minLines       | v: Number                                | v          | Android | 文本最小行数   |
| 13   | ellipsize      | v: <a href="#ellipsize">Ellipsize</a>    | v          | -       | 文本省略方式   |
| 14   | adjustTextSize | -                                        | -          | Android | 字体大小适应宽度 |
| 15   | adjustFontSize | -                                        | -          | -       | 字体大小适应宽度 |