package app.excuseme.network;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.excuseme.model.LyricInfo;

/**
 *
 * 从歌词迷api获取歌词
 *
 */
public class RequestLyrics {

	public static LyricInfo[] getAllLyricInfo(String title) throws Exception{
		Reader reader = OnlineDataGetter.doGet("http://gecimi.com/api/lyric/" + title);
		return parsingJson(reader);
	}
	
	public static LyricInfo[] getAllLyricInfo(String title, String artist) throws Exception{
		Reader reader = OnlineDataGetter.doGet("http://gecimi.com/api/lyric/" + title + "/" + artist);
		return parsingJson(reader);
	}

	private static LyricInfo[] parsingJson(Reader reader){
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(reader);
		JsonArray array = element.getAsJsonObject().get("result").getAsJsonArray();
		LyricInfo[] lyricInfos = gson.fromJson(array, LyricInfo[].class);
		return lyricInfos;
	}
	
//	public static void main(String[] args) {
//		try {
//			LyricInfo[] lyricInfos = getAllLyricInfo("海阔天空", "Beyond");
//			System.out.println(lyricInfos[0].getSong() + ": " + lyricInfos[0].getLrc());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
}
