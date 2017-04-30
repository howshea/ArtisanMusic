# ArtisanMusic
![image](https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/ic_launcher.png)
> 模仿网易云的界面做了一个音乐播放器

## 功能
- 本地歌曲的扫描，删除，修改歌曲信息
- 使用自定义SurfaceView绘制的网易云的黑胶唱片
- 支持新建、编辑歌单，添加歌曲到歌单 
- 歌曲标记为喜欢
- 通知栏与界面同步交互
- 所有界面常驻一个播放底栏
- 播放列表自动定位到播放位置

### Version
`v 1.0 beta`

## 截图
<img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-194027.png" width=200>  <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-194017.png" width=200>   <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-192246.png" width=200>   <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-193209.png" width=200>

<img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-192520.png" width=200>  <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-205357.png" width=200>   <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-193007.png" width=200>   <img src="https://github.com/howshea/ArtisanMusic/raw/master/Screenshots/Screenshot_20161214-192644.png" width=200>

## 知识点
- **MVP**设计架构
- 自定义的MVP框架，解决presenter持有activity或者fragment引用造成的内存泄漏问题
- 自定义`SurfaceView`还原网易云的播放界面
- `Service`和`BroadcaseReceiver`控制所有的播放逻辑
- 歌曲表—中间表—歌单表 多对多的数据库设计
- `CursorWrapper`
- `BottomDialogFragment`的运用
- 播放背景高斯模糊算法
- 随机播放算法
- `EventBus`事件传递
- `RxJava`线程切换

## TODO
- [ ] 加入歌词控件
- [ ] 通过一些api联网自动加载歌词和封面
- [ ] 为本地歌曲加入**歌手**、**专辑**、**文件夹**的分类
- [ ] 添加**更换主题**、**定时播放**的功能
- [ ] ~~优化自定义`SurfaceView`的性能~~
- [ ] 把使用`SurfaceView`实现的唱片转盘换成`ViewGroup`实现


## 用到的开源项目
- [RxJava](https://github.com/ReactiveX/RxJava)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [Glide](https://github.com/bumptech/glide)
- [EventBus](https://github.com/greenrobot/EventBus)
- [Butterknife](https://github.com/JakeWharton/butterknife)
- [Logger](https://github.com/orhanobut/logger)

## 开源协议
[GPL v3 ](https://github.com/howshea/ArtisanMusic/raw/master/LICENSE)