package app.excuseme.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * 用于管理缓存文件
 *
 */
public class CacheManager {

	private static Path tmpFileDir = null;
	private static Path lyricsDir = null;
	private static Path songsDir = null;
	
	
	/**
	 * 获取储存缓存数据的临时文件夹
	 * @return 临时文件夹
	 */
	public static Path getInstance(){
		try{
			if(tmpFileDir==null){
				tmpFileDir = Files.createTempDirectory("ExcuseMePlayer");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpFileDir;
	}
	

	/**
	 * 获取储存歌词缓存的文件夹
	 * @return
	 */
	public static Path getLyricsDir(){
		if(lyricsDir==null){
			createLyricsDir();
		}
		return lyricsDir;
	}
	
	/**
	 * 获取储存歌曲缓存的文件夹
	 * @return
	 */
	public static Path getSongsDir(){
		if(songsDir==null){
			createSongsDir();
		}
		return songsDir;
	}
	
	private static void createSongsDir(){
		tmpFileDir = getInstance();
		File songsFile = new File(tmpFileDir.toString() + "/songs");
		songsFile.mkdirs();
		songsDir = songsFile.toPath();
	}
	
	private static void createLyricsDir(){
		tmpFileDir = getInstance();
		File lyricsFile = new File(tmpFileDir.toString() + "/lyrics");
		lyricsFile.mkdirs();
		lyricsDir = lyricsFile.toPath();
	}
	
	
	/**
	 * 缓存文件夹所有内容
	 */
	public static void deleteAll(){
		deleteFile(tmpFileDir.toFile());
	}
	
	private static void deleteFile(File file){
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				deleteFile(f);
			}
			file.delete();
		} else if (file.exists()){
			file.delete();
		}
	}
	
}
