package app.excuseme.network;

import java.net.URLEncoder;

import com.google.gson.Gson;
import app.excuseme.model.*;
import app.excuseme.util.*;

public class RequestMusic {
	
	public static PlayListInfo[]  getTotalPlayList()throws Exception
	{
		Gson gson = new Gson();
		PlayListInfo[] playListInfos=gson.fromJson(OnlineDataGetter.doGet(Constants.PLAYLIST_URL), PlayListInfo[].class);
		return playListInfos;
	}
	public static MusicInfo[] getRandomMusicList(PlayListInfo list,int num)throws Exception
	{	
		Gson gson = new Gson();
		MusicInfo[] musicInfo = gson.fromJson(OnlineDataGetter.doGet(Constants.PLAYLIST_URL+list.getKey()+"/?num="+num), MusicInfo[].class);	
		return musicInfo;
	}
	
	public static MusicInfo[] searchMusic(String title)throws Exception
	{
		Gson gson = new Gson();
		title = URLEncoder.encode(title, "utf-8");	//导出jar包后搜索时可能会因为歌名是中文编码问题导致找不到该歌曲，先进行url编码
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(OnlineDataGetter.doGet(Constants.MUSIC_URL+"?title="+title), MusicInfo[].class);
		return musicInfo;
	}
	public static MusicInfo[] searchMusic(String title,int kbps)throws Exception
	{
		Gson gson = new Gson();
		MusicInfo[] musicInfo;
		title = URLEncoder.encode(title, "utf-8");
		musicInfo=gson.fromJson(OnlineDataGetter.doGet(Constants.MUSIC_URL+"?title="+title+"&kbps="+kbps), MusicInfo[].class);
		return musicInfo;
	}
	
	public static MusicInfo[] searchMusic(int start,int end)throws Exception
	{
		Gson gson = new Gson();
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(OnlineDataGetter.doGet(Constants.MUSIC_URL+"?start="+start+"&end="+end), MusicInfo[].class);
		return musicInfo;
	}
	public static MusicInfo[] searchMusic(int start)throws Exception
	{
		Gson gson = new Gson();
		MusicInfo[] musicInfo;
		musicInfo=gson.fromJson(OnlineDataGetter.doGet(Constants.MUSIC_URL+"?start="+start), MusicInfo[].class);
		return musicInfo;
	}
	
	/*public static void main(String[] args)throws Exception
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
		
	}*/
}
