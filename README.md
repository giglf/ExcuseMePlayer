# ExcuseMePlayer

一个和同学一起写的桌面音乐播放器

以下是一些基础想法

* 使用javafx编写UI界面
* 在线的音乐服务器为 http://doufm.info , 这是一个布置在西安电子科技大学校内的音乐服务器，校内师生使用可以达到免流量的效果。






**Feature (☑☒):**

☑ 本地歌曲播放

☑ 根据选择的电台播放在线歌曲

☑ 从doufm搜索歌曲

☑ 从当前播放目录搜索歌曲

☑ 相关歌词显示



### **Doufm API 使用**

https://github.com/DouFM/wang_fm/blob/master/doc/api.md

---

**My normal use**

1. 获取电台列表

   http://doufm.info/api/playlist/

   获得电台的相关Json数据

   ```json
   {
           "key": "52f8ca1d1d41c851663fcba7", 
           "music_list": 3726, 
           "name": "\u534e\u8bed"
   }, 
   ```

2. 通过访问 `"/api/list/" + key + "?num=1"`获取音乐，例如

    http://doufm.info/api/playlist/52f8ca1d1d41c851663fcba7/?num=1

   `num=1` 参数表明从曲库获取一首单曲

   获得返回Json数据

   ```json
   [
       {
           "album": "\u5149\u8292", 
           "artist": "\u5149\u826f", 
           "audio": "/api/fs/537c59881d41c83a88b67028/", 
           "company": "EMI", 
           "cover": "/api/fs/537c59881d41c83a88b67026/", 
           "kbps": "64", 
           "key": "537c59881d41c83a88b67032", 
           "public_time": "2002", 
           "title": "\u82e5\u65e0\u5176\u4e8b", 
           "upload_date": "Wed, 21 May 2014 15:45:12 -0000"
       }
   ]
   ```

3. audio的URL表示服务器储存该歌曲的路径

   可以通过该链接获取歌曲


---

### **关于歌词**

歌词获取通过 gecimi(歌词迷) api.

Official document http://doc.gecimi.com/en/latest/#indices-and-tables

但是api当中有些错误

首先，我们可以通过以下方式获取song的歌词列表 `http://gecimi.com/api/lyric/:song` .

返回是歌词的json列表

其中的链接如下 `http://s.gecimi.com/lrc/344/34435/3443588.lrc`

但是访问会得到404 not found

后来我发现`s.gecimi.com` 这个域名应该为 `s.geci.me`

所以在使用过程中应把这个域名替换以下

***Notice: ** 发现这个bug被修复了，现在可以通过原始的url来直接获取歌词了 `s.gecimi.com` (2017.6.25) 



---

### 界面样式

![demo](/demoPicture/demo.png)

![ubuntuDemo](/demoPicture/ubuntuDemo.JPG)

#### 怎么运行

1. 首先，你要拥有一台电脑（废话！）
2. 电脑能连上西电校园网
3. 然后，电脑上要安装有java的运行环境，因为UI、核心播放器是建立在Javafx之上的，所以需要使用包含Javafx的java8，在某些情况下可能jdk没有包含javafx，可以通过安装jfx解决
4. 然后通过直接运行jar包就可以运行了`java -jar ExcuseMePlayer.jar`



#### 使用说明

* 当启动的时候，默认处于无选择电台的在线播放音乐状态。
  * 如果点击FM无列表显示，说明网络出问题，访问不到doufm的服务器。
* 选择电台后，根据网络状况需要等待一段时间，软件会自动缓存歌曲到系统的tmp文件夹中，同时向歌词迷搜索是否存在相关音乐歌词，并且下载。
* 点击切换到本地音乐，首次需要导入本地音乐，选择包含音乐的文件夹
  * 如果此时没有导入，直接关闭，程序会退出
* 本地音乐导入过后，将会在当前目录生成library.xml的文件，用于储存本地音乐的信息，想要重新导入只需要把该文件删除即可。
* 每次退出会默认删除所有tmp目录下的缓存文件，鉴于javafx存在的一些bug，曾播放过的音频缓存文件无法解除占用，导致无法删除，此时可通过手动删除抑或等下次打开ExcuseMePlayer再关闭时删除。
* 如果下载过程网络出现问题而断开，程序会崩溃（未修复）
* 使用终端打开ExcuseMePlayer的话，当歌曲下载完成会有完成提示显示。