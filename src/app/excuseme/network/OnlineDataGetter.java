package app.excuseme.network;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import app.excuseme.model.LyricInfo;
import app.excuseme.model.MusicInfo;
import app.excuseme.util.CacheManager;
import app.excuseme.util.Constants;
import app.excuseme.util.HttpDownload;

public class OnlineDataGetter {

	
	public static Reader doGet(String url)throws Exception{
		URL localURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) localURL.openConnection();
		connection.setRequestProperty("User-Agent", Constants.APP_VERSION);
		InputStreamReader iReader=new InputStreamReader(connection.getInputStream());
		return iReader;
	}
	
	/**
	 * 传入歌曲信息，下载相应歌词，并记录歌词路径在歌曲类中
	 * @param musicInfo
	 * @return musicInfo with lyricPath
	 */
	public static MusicInfo lyricGetter(MusicInfo musicInfo){
		try{
			LyricInfo[] lyricInfos;
//			String title = musicInfo.getTitle();
//			String artist = musicInfo.getArtist();
			String title = URLEncoder.encode(musicInfo.getTitle(), "utf-8");
			String artist = URLEncoder.encode(musicInfo.getArtist(), "utf-8");
			title = title.replaceAll("\\+", "%20");
			artist = artist.replaceAll("\\+", "%20");
			lyricInfos = RequestLyrics.getAllLyricInfo(title, artist);
			if (lyricInfos.length == 0) {
				lyricInfos = RequestLyrics.getAllLyricInfo(title);
			}
			if(lyricInfos.length == 0){
				musicInfo.setLyricPath(null);
				return musicInfo;
			}
			//下载歌词
			HttpDownload downloader = new HttpDownload(lyricInfos[0].getLrc());
			downloader.setSaveLocation(CacheManager.getLyricsDir().toFile());
			downloader.download();
			while(!downloader.isDownloadFinished()){Thread.sleep(500);}
			musicInfo.setLyricPath(downloader.getSaveFile().getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return musicInfo;
	}
	
	/**
	 * 获取在线音乐播放列表
	 * @param musicList
	 * @return
	 */
	public static ArrayList<MusicInfo> musicGetter(MusicInfo[] musicList){
		
		ArrayList<MusicInfo> list = new ArrayList<>();
		HttpDownload[] downloader = new HttpDownload[musicList.length];
		for(int i=0;i<musicList.length;i++){
			try {
				downloader[i] = new HttpDownload("http://doufm.info" + musicList[i].getAudio());
				downloader[i].setSaveLocation(CacheManager.getSongsDir().toFile());
				downloader[i].setSavingFile("/" + musicList[i].hashCode() + ".mp3");
//				Thread.sleep(500);
				downloader[i].download();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < downloader.length; i++) {
				if(!downloader[i].isNetworkFine()) {return null;}
				while (!downloader[i].isDownloadFinished()) {Thread.sleep(1000);}
				AudioFile audioFile = AudioFileIO.read(downloader[i].getSaveFile());
				AudioHeader header = audioFile.getAudioHeader();
				musicList[i].setLocation(downloader[i].getSaveFile().getAbsolutePath());
				musicList[i].setLength(Integer.toString(header.getTrackLength()/2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.addAll(list, musicList);
		return list;
	}
	
}
