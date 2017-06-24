package app.excuseme.network;

import java.util.ArrayList;
import java.util.Collections;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import app.excuseme.model.MusicInfo;
import app.excuseme.util.CacheManager;
import app.excuseme.util.HttpDownload;

public class OnlineDataGetter {

	
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
				downloader[i] = new HttpDownload("http://doufm.info" + musicList[i].getAudio(), 1);
				downloader[i].setSaveLocation(CacheManager.getSongsDir().toFile());
				downloader[i].setSavingFile("/" + musicList[i].getTitle() + ".mp3");
				downloader[i].download();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			for (int i = 0; i < downloader.length; i++) {
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
