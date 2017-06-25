package app.excuseme.model;

/**
 * 歌词项目信息 
 *
 */
public class LyricItem {

	private long time;
	private String content;
	
	public LyricItem() {}
	
	public LyricItem(long time, String content){
		this.setTime(time);
		this.setContent(content);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	
	
}
