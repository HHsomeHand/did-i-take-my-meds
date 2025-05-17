修改 `com.github.hhsomehand.ui.theme` 包下的文件, 进行更改:

```text
.
|-- Color.kt 颜色文件
|-- Theme.kt 不需要动
`-- Type.kt 字体文件

0 directories, 5 files
```

如果想更方便地更改, 可以用: 

https://material-foundation.github.io/material-theme-builder/

下载了theme zip后, 解压出来, 直接覆盖原文件 

修改 `package`:

```
package com.github.hhsomehand.ui.theme
```



修改`theme.kt`, 去除动态主题色

```kotlin

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
//      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//          val context = LocalContext.current
//          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//      }
//
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}


```

就万事大吉了