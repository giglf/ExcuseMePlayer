import java.awt.List;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.jar.JarEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sun.javafx.iio.gif.GIFImageLoaderFactory;
//import com.sun.net.ssl.HttpURLConnection;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import sun.net.www.protocol.http.HttpURLConnection;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.JSONFunctions;
import sun.management.Agent;
import app.excuseme.model.*;
import app.excuseme.util.*;
public class RequestMusic {
	static Gson gson=new Gson();
	public Reader doGet(String url)throws Exception
	{
		
			
		URL localURL=new URL(url);
		URLConnection connection=(HttpURLConnection) localURL.openConnection();
		InputStreamReader iReader=new InputStreamReader(connection.getInputStream());	
		return iReader;
	}
	public PlayListInfo[]  getTotalPlayList()throws Exception
	{
		
		PlayListInfo[] playListInfos=gson.fromJson(doGet(Constants.PLAYLIST_URL), PlayListInfo[].class);
	
		return playListInfos;
	}
	public MusicInfo[] getRandomMusicList(PlayListInfo list,int num)throws Exception//list 转成string再转成jsonobject？
	{
		
		
		MusicInfo[] musicInfo = gson.fromJson(doGet(Constants.PLAYLIST_URL+list.getKey()+"/?num="+num), MusicInfo[].class);
		
		return musicInfo;
	}
	
	public MusicInfo[] searchMusic(String title)throws Exception
	{
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(doGet(Constants.MUSIC_URL+"?title="+title), MusicInfo[].class);
		return musicInfo;
	}
	public MusicInfo[] searchMusic(String title,int kbps)throws Exception
	{
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(doGet(Constants.MUSIC_URL+"?title="+title+"&kbps="+kbps), MusicInfo[].class);
		return musicInfo;
	}
	public MusicInfo[] searchMusic(int start,int end)throws Exception
	{
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(doGet(Constants.MUSIC_URL+"?start="+start+"&end="+end), MusicInfo[].class);
		return musicInfo;
	}
	public MusicInfo[] searchMusic(int start)throws Exception
	{
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(doGet(Constants.MUSIC_URL+"?start="+start), MusicInfo[].class);
		return musicInfo;
	}
	public static void main(String[] args)throws Exception
	{
		RequestMusic requestMusic =new RequestMusic();
		PlayListInfo[] playListInfos;
		MusicInfo[] musicInfos;
		playListInfos=requestMusic.getTotalPlayList();
		musicInfos=requestMusic.getRandomMusicList(playListInfos[1],2);
		for (int i = 0; i < playListInfos.length; i++) {
			System.out.println(playListInfos[i].getMusiclist());
		}
		for (int i = 0; i < musicInfos.length; i++) {
		System.out.println(musicInfos[i]);
		}
		musicInfos=requestMusic.searchMusic(0, 2);
		for (int i = 0; i < musicInfos.length; i++) {
		System.out.println(musicInfos[i]);
	}
		
	}
}
