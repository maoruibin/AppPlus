# AppPlus #
![icon](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png "")
### 介绍 ###
AppPlus是一个可以用于传送Apk文件，提取APK文件等的工具软件。<br>
目前已开源，涉及到了一些[知识点](#jump)。欢迎学习交流。

### 下载 ###

[fir下载](http://fir.im/appplus)<br>
<br>
![扫码下载](/art/download.png "扫码下载")


### 开发初衷 ###

* <b>优雅体面的提取APK</b><br><br>在工作过程中，有时需要反编译一些软件的APK，此时这些软件就存在于自己的手机，但是无法直接获得从手机中获取APK，后来在软件市场找到了一些相关的APK提取工具软件，
如APK提取器等等的软件，但是无一例外，它们的操作体验都太差，加上丑陋的广告，让我一点都忍不了。所以我觉得自己有必要让这个看似简单的事情，变得更加优雅。<br><br>
* <b>快速打开应用详情</b><br><br>在开发过程中，经常需要去应用详情界面中执行清除数据的操作。但是一般的ROM（MIUI做的不错，可以直接在最近任务列表，长按对应APP的图标，就可以打开应用详情）都只能去设置界面，然后找到对应的应用，点击item，打开详情，点击清除数据，整个过程稍微复杂。所以如果有一个app，可以显示最近打开的应用列表，并且支持打开应用详情最好不过了。<br><br>
* <b>快捷分享应用APK文件</b><br><br>这个功能跟茄子快传、快牙提供的功能是重合的，但是它们正常工作的基础是双方手机必须同时安装客户端，其实不该这么霸道的。App+可以单向传送APK文件到对方手机，尽管速度慢一点。<br><br>
* <b>喜欢MaterialDesigner</b><br><br>不久前，Google公布了support:design包，很好的支持了MaterialDesigner的新元素,所以可以通过项目，学习使用最新的API。（有人说，可以在工作中就可以使用相关的API啊，但是无奈，公司的设计都是按照IOS的规范，MD根本没影）

### 截图 ###

![index](/art/index.png "")
![send](/art/send.jpg "")

### <a name="jump">知识点</a> ###
* 官方support design库的使用
* PackageManager的使用，以及在获取最近运行任务时，对Android5.0的适配
* 主题颜色切换(主要原理是通过切换事先定义好的Theme，然后重新启动Activity)
* 友盟统计(包括事件统计以及自动更新)
* 多渠道打包
* Log管理(动态设置在debug模式下输出log，在release下屏蔽所有log)

### 依赖库 ###
* [materialpreference](https://github.com/jenzz/Android-MaterialPreference) -- 材料样式的设置界面
* [systembartint](https://github.com/jgilfelt/SystemBarTint) -- 沉浸式效果
* [AndroidProcesses](https://github.com/jaredrummler/AndroidProcesses)
* [recyclerview-animators](https://github.com/wasabeef/recyclerview-animators)

### 更新日志 ###
Changelog is available [here](/doc/Changelog.md)

### 关于我 ###
Android开发者，爱折腾，爱篮球
<br>QQ:1252768410
<br>[个人博客](https://www.gudong.name/)
<br>[微博](http://weibo.com/u/1874136301)

### License ###

   The MIT License (MIT)

    Copyright (c) 2015 Maoruibin

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.