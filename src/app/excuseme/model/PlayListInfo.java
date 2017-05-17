package app.excuseme.model;

/**
 * Doufm的api中包含了11个频道</br>
 * 此处对这些频道的信息进行封装</br>
 * 一个电台频道包含key, music_list, name三个属性，该类中包含相应的setter和getter</br>
 * <p><b><pre>
 * key			该电台频道的key</br>
 * music_list		该电台频道中的歌曲总数</br>
 * name			该电台频道的名称</br>
 * </pre></b></p>
 */
public final class PlayListInfo {

	private String key;				//该电台频道的key
	private String music_list;		//该电台频道中的歌曲总数
	private String name;			//该电台频道的名称
	
	
	public PlayListInfo(){}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getMusiclist() {
		return music_list;
	}
	public void setMusiclist(String music_list) {
		this.music_list = music_list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
