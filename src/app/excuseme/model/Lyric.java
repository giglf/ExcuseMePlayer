package app.excuseme.model;

import java.util.ArrayList;
import java.util.List;

public class Lyric {

	private String title;	//歌名
	private String artist;	//歌手
	
	//歌词项目
	private List<LyricItem> lyricitems = new ArrayList<LyricItem>();
	
	private int lines;		//歌词总行数
	private long totalTime;	//歌曲总时长
	
	private int info;	//扩展信息，记录状态和位置

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public List<LyricItem> getLyricitems() {
		return lyricitems;
	}

	public void setLyricitems(List<LyricItem> lyricitems) {
		this.lyricitems = lyricitems;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public int getInfo() {
		return info;
	}

	public void setInfo(int info) {
		this.info = info;
	}

	@Override
	public String toString() {
		StringBuffer items = new StringBuffer();
		lyricitems.forEach(n -> items.append("\r\n").append(n.getTime()).append(":").append(n.getContent()));
		return "[title=" + title + ": artist=" + artist
				+ ": lines=" + lines + ": totalTime=" + totalTime
				+ ": info=" + info + "]" + items.toString();
	}
	
	
}
