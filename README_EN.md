# ExcuseMePlayer

A desktop music player produced by me and my teammates.

Here are some basic ideas about this software:

* UI based on javafx.

* Music server is http://doufm.info , which is deployed in xidian university. We can access it by campus network and listen music without network traffic fee.




**Feature (☑☒):**

☑ play the local music

☑ play random online music by categories from doufm

☑ search online music from doufm

☑ search local music from local playing list

☑ lyrics display



### **Doufm API usage**

https://github.com/DouFM/wang_fm/blob/master/doc/api.md

---

**My normal use**

1. Get Playlist by access 

   http://doufm.info/api/playlist/

   Then get a list of json data

   ```json
   {
           "key": "52f8ca1d1d41c851663fcba7", 
           "music_list": 3726, 
           "name": "\u534e\u8bed"
   }, 
   ```

2. Get a music from that playlist by access `"/api/list/" + key + "?num=1"`

    http://doufm.info/api/playlist/52f8ca1d1d41c851663fcba7/?num=1

   Parameter of `num=1` can get the number of random music from this playlist.

   Then get json data

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

3. Here the audio URL means the song audio data. 

   Then some others obvious information about the song.

   Key is a parameter that you can access that song from website by

    http://doufm.info/#key=537c59881d41c83a88b67032




---

### **About lyric**

The music lyric use the gecimi(歌词迷) api.

Official document http://doc.gecimi.com/en/latest/#indices-and-tables

However, there are something error in the api using.

At first, we can get the lyric information by `http://gecimi.com/api/lyric/:song` .

Then response the url of the lrc file by json.

Like this `http://s.gecimi.com/lrc/344/34435/3443588.lrc`

But you will get 404 not found if you access it.

Then I found the reason is the domain of `s.gecimi.com` ought to be `s.geci.me`

When using this api should look out that.

***Notice: ** I found that url bug have fixed. Which means we can use the original url `s.gecimi.com` (2017.6.25) 



---

### Final sample

![demo](/demoPicture/demo.png)

#### How to run?

At first, you will need a computer. (Of course...)

1. You should install java runtime in your computer. 
2. For the UI, the version of java need to be java8(include javafx).