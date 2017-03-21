> Class

## AudioPlayer

音频播放器类，用于播放本地音频文件

| ID   | API      | 参数                              | 返回值        | 平台      | 备注           |
| ---- | -------- | ------------------------------- | ---------- | ------- | ------------ |
| 1    | play     | name: String<br/> times: Number | -          | -       | 播放（uri，重复次数） |
| 2    | pause    | -                               | -          | Android | 暂停播放         |
| 3    | resume   | -                               | -          | Android | 恢复播放         |
| 4    | stop     | -                               | -          | -       | 停止播放         |
| 5    | seekTo   | sec: Number                     | -          | Android | 到某个位置        |
| 6    | callback | v: LuaFunction                  | v          | Android | 回调           |
| 7    | playing  | -                               | v: Boolean | Android | 是否播放         |
| 8    | pausing  | -                               | v: Boolean | Android | 是否暂停         |
| 9    | looping  | -                               | v: Boolean | Android | 是否循环播放       |

*示例*

```lua
local player = AudioPlayer()
player:play('music.wav', 3)
```

---

> Class

## Vibrator

震动类

| ID   | API         | 参数                                  | 返回值        | 平台   | 备注        |
| ---- | ----------- | ----------------------------------- | ---------- | ---- | --------- |
| 1    | hasVibrator | -                                   | v: Boolean | -    | 是否有震动硬件   |
| 2    | vibrate     | mode: Number[], repeatTimes: Number | -          | -    | 震动(模式，次数) |
| 3    | cancel      | -                                   | -          | -    | 取消震动      |

*示例*

```lua
local vibrator = Vibrator()

vibrator:vibrate() -- 默认震动

vibrator:vibrate(2) -- 震动两次

vibrator:vibrate({1, 2, 1, 0.3, 0.2, 0.1, 0.01, 1.1}, 4) -- 特殊震动模式
```

