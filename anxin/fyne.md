Fyne 是一个使用 Go 语言编写的跨平台 GUI 库，它使用了现代的技术来实现简单易用的 UI 开发方案。以下是一个简单的 Fyne 示例程序：

```
goCopy codepackage main

import "fyne.io/fyne/v2/app"
import "fyne.io/fyne/v2/widget"

func main() {
    myApp := app.New()
    myWindow := myApp.NewWindow("Hello")

    hello := widget.NewLabel("Hello Fyne!")
    myWindow.SetContent(widget.NewVBox(
        hello,
    ))

    myWindow.ShowAndRun()
}
```

在这个示例程序中，我们首先导入了 Fyne 库的 app 和 widget 包。app 包用于创建应用程序实例，widget 包用于创建 UI 控件。

然后，我们使用 app.New() 函数创建一个新的应用程序实例，并使用 myApp.NewWindow() 函数创建一个新的窗口实例。

接着，我们使用 widget.NewLabel() 函数创建一个新的标签控件，并将其添加到一个垂直布局容器中。最后，我们将垂直布局容器设置为窗口的内容，并使用 myWindow.ShowAndRun() 方法显示窗口并启动应用程序。

这只是 Fyne 的一个简单示例，Fyne 还提供了丰富的 UI 控件和布局管理器，以及简单易用的事件处理和数据绑定机制，可以方便地创建复杂的跨平台 GUI 应用程序。你可以通过 Fyne 的官方文档来了解更多关于 Fyne 的知识和使用方法：https://fyne.io/。



设置中文字体：

```go
package ui

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/theme"
	"image/color"
	"strings"
)

type myTheme struct {
	regular, bold, italic, boldItalic, monospace fyne.Resource
}

func (t *myTheme) Color(name fyne.ThemeColorName, variant fyne.ThemeVariant) color.Color {
	return theme.DefaultTheme().Color(name, variant)
}

func (t *myTheme) Icon(name fyne.ThemeIconName) fyne.Resource {
	return theme.DefaultTheme().Icon(name)
}

func (m *myTheme) Font(style fyne.TextStyle) fyne.Resource {
	if style.Monospace {
		return m.monospace
	}
	if style.Bold {
		if style.Italic {
			return m.boldItalic
		}
		return m.bold
	}
	if style.Italic {
		return m.italic
	}
	return m.regular
}

func (m *myTheme) Size(name fyne.ThemeSizeName) float32 {
	return theme.DefaultTheme().Size(name)
}

func (t *myTheme) SetFonts(regularFontPath string, monoFontPath string) {
	t.regular = theme.TextFont()
	t.bold = theme.TextBoldFont()
	t.italic = theme.TextItalicFont()
	t.boldItalic = theme.TextBoldItalicFont()
	t.monospace = theme.TextMonospaceFont()

	if regularFontPath != "" {
		t.regular = loadCustomFont(regularFontPath, "Regular", t.regular)
		t.bold = loadCustomFont(regularFontPath, "Bold", t.bold)
		t.italic = loadCustomFont(regularFontPath, "Italic", t.italic)
		t.boldItalic = loadCustomFont(regularFontPath, "BoldItalic", t.boldItalic)
	}
	if monoFontPath != "" {
		t.monospace = loadCustomFont(monoFontPath, "Regular", t.monospace)
	} else {
		t.monospace = t.regular
	}
}

func loadCustomFont(env, variant string, fallback fyne.Resource) fyne.Resource {
	variantPath := strings.Replace(env, "Regular", variant, -1)

	res, err := fyne.LoadResourceFromPath(variantPath)
	if err != nil {
		fyne.LogError("Error loading specified font", err)
		return fallback
	}

	return res
}

```



然后在主程序中调用：

```go
//os.Setenv("FYNE_FONT", "C:\\Windows\\Fonts")
	t := &myTheme{}
	t.SetFonts("./assets/YaHei.Consolas.1.12.ttf", "")
```



所有程序的概括大概是：

```go
func ShowMain() {
	//os.Setenv("FYNE_FONT", "C:\\Windows\\Fonts")
	t := &myTheme{}
	t.SetFonts("./assets/YaHei.Consolas.1.12.ttf", "")

	a := app.NewWithID("tower-crane.free-sun.cn")
	a.SetIcon(theme.FyneLogo())
	a.Settings().SetTheme(t)
	makeTray(a)
	logLifecycle(a)
	w := a.NewWindow("Tower Crane  Anti-collision")
	topWindow = w

	//w.SetMainMenu(makeMenu(a, w))
	w.SetMaster() // 关闭界面后退出主程序
	w.SetContent(makeContent(a, w))
	w.Resize(fyne.NewSize(640, 460))
	//w.SetFullScreen(true)
	w.ShowAndRun()
}
```

