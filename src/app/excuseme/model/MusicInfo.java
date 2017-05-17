package app.excuseme.model;

/**
 * 
 * 封装的单首歌曲的信息</br>
 * 包含歌曲基本属性的getter和setter</br>
 * <p><b><pre>
 * album			专辑</br>
 * artist			艺术家</br>
 * audio			音频URL</br>
 * company		唱片公司</br>
 * cover			专辑封面URL</br>
 * kbps			码率</br>
 * key			歌曲key</br>
 * public_time		出版年份</br>
 * title			歌曲名</br>
 * upload_date		上传时间</br></br>
 * 
 * location		歌曲本地储存路径</br>
 * length			歌曲时长</br>
 * </pre></b></p>
 * 
 */
public final class MusicInfo {

	//在线歌曲包含属性
	private String album;			//专辑
	private String artist;			//艺术家
	private String audio;			//音频URL
	private String company;			//唱片公司
	private String cover;			//专辑封面URL
	private String kbps;			//码率
	private String key;				//歌曲key
	private String public_time;		//出版年份
	private String title;			//歌曲名
	private String upload_date;		//上传时间
	
	//添加属性
	private String location;		//歌曲本地储存路径
	private String length;			//歌曲时长
	
	public MusicInfo(){}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAudio() {
		return audio;
	}
	public void setAudio(String audio) {
		this.audio = audio;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getKbps() {
		return kbps;
	}
	public void setKbps(String kbps) {
		this.kbps = kbps;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPublicTime() {
		return public_time;
	}
	public void setPublicTime(String public_time) {
		this.public_time = public_time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUploadDate() {
		return upload_date;
	}
	public void setUploadDate(String upload_date) {
		this.upload_date = upload_date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	//判断改歌曲是否在线歌曲
	public boolean isOnline(){
		return audio != null;
	}
	
	
	
}
