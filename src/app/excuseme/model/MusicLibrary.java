package app.excuseme.model;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.swing.internal.plaf.basic.resources.basic;

import app.excuseme.util.Constants;
import app.excuseme.util.ImportMusicTask;

public class MusicLibrary {
	//PlayerTODO 整个类咯
	
	//在线歌曲包含属性
	private static final String ALBUM = "album";			//专辑
	private static final String ARTIST = "artist";			//艺术家
	private static final String COMPANY = "company";			//唱片公司
	private static final String KBPS = "kbps";			//码率
	private static final String TITLE = "tilte";			//歌曲名
		
	//添加属性
	private static final String LOCATION = "location";		//歌曲本地储存路径
	private static final String LENGTH = "length";			//歌曲时长
	
	private static int maxProgress;
	private static ArrayList<MusicInfo> localMusics;
	private static ImportMusicTask<Boolean> importMusicTask;
	
	//音乐导入
	public static void importMusic(String path, ImportMusicTask<Boolean> task) throws Exception{
		maxProgress = 0;
		importMusicTask = task;
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element library = doc.createElement("library");				//整个文件开头
		Element musicLibrary = doc.createElement("musicLibrary");	//歌曲文件夹基本信息
		Element musics = doc.createElement("musics");				//歌曲信息列表
		
		doc.appendChild(library);
		library.appendChild(musicLibrary);
		library.appendChild(musics);
		
		Element musicPath = doc.createElement("path");		//歌曲路径
		Element fileNum = doc.createElement("fileNum");		//歌曲数量
		
		musicPath.setTextContent(path);
		
		File directory = new File(Paths.get(path).toUri());
		getMaxProgress(directory);
		importMusicTask.updateProgress(0, maxProgress);
		int i = writeXMLFile(directory, doc, musics, 0);
		String fileNumber = Integer.toString(i);
		fileNum.setTextContent(fileNumber);
		
		musicLibrary.appendChild(musicPath);
		musicLibrary.appendChild(fileNum);
		
		//文件输出
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		
		File xmlFile = new File(Constants.JAR + "library.xml");
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
		
		maxProgress = 0;
		importMusicTask = null;
	}
	
	//获取本地音乐
	public static ArrayList<MusicInfo> getLocalMusics(){
		if(localMusics==null){
			localMusics = new ArrayList<>();
			updatePlayList();
		}
		return localMusics;
	}
	
	public static boolean isSupportedFileType(File file){
		String filename = file.getName();
		String type = filename.substring(filename.lastIndexOf('.')).toLowerCase();
		
		switch(type){
			case "mp3":
			case "mp4":
			case "m4a":
			case "wav":
				return true;
			default:
				return false;
		}
	}
	
	//从xml中读取歌曲信息
	private static void updatePlayList(){
		
		try{
	
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty("javax.xml.stream.isCoalescing", true);
			FileInputStream inputStream = new FileInputStream(new File(Constants.JAR + "library.xml"));
			XMLStreamReader reader = factory.createXMLStreamReader(inputStream, "utf-8");
			
			String element = "";
			
			String title = null;
			String artist = null;
			String album = null;
			String company = null;
			String length = null;
			String kbps = null;
			String location = null;
			
			while(reader.hasNext()){
				reader.next();
				
				if(reader.isWhiteSpace()){
					continue;
				} else if (reader.isStartElement()){
					element = reader.getName().getLocalPart();
				} else if (reader.isCharacters()){
					String value = reader.getText();
					switch(element){
						case TITLE:
							title = value;
							break;
						case ARTIST:
							artist = value;
							break;
						case ALBUM:
							album = value;
							break;
						case COMPANY:
							company = value;
							break;
						case LENGTH:
							length = value;
							break;
						case KBPS:
							kbps = value;
							break;
						case LOCATION:
							location = value;
							break;
					}
				} else if(reader.isEndElement() && reader.getName().getLocalPart().equals("music")){
					MusicInfo musicAdd = new MusicInfo();
					musicAdd.setTitle(title);
					musicAdd.setArtist(artist);
					musicAdd.setAlbum(album);
					musicAdd.setCompany(company);
					musicAdd.setLength(length);
					musicAdd.setKbps(kbps);
					musicAdd.setLocation(location);
					localMusics.add(musicAdd);
					
					title = artist = album = company = length = kbps = location = null;
				} else if(reader.isEndElement() && reader.getName().getLocalPart().equals("musics")){
					reader.close();
					break;
				}
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//单首歌曲信息写入
	private static int writeXMLFile(File directory, Document doc, Element musics, int i){
		File[] files = directory.listFiles();
		
		for(File file : files){
			if(file.isFile() && isSupportedFileType(file)){
				try {
					AudioFile audioFile = AudioFileIO.read(file);
					Tag tag = audioFile.getTag();
					AudioHeader header = audioFile.getAudioHeader();
					
					Element music = doc.createElement("music");
					musics.appendChild(music);
					
					Element title = doc.createElement(TITLE);
					Element artist = doc.createElement(ARTIST);
					Element album = doc.createElement(ALBUM);
					Element company = doc.createElement(COMPANY);
					Element length = doc.createElement(LENGTH);
					Element kbps = doc.createElement(KBPS);
					Element location = doc.createElement(LOCATION);
					
					//歌名
					title.setTextContent(tag.getFirst(FieldKey.TITLE));
					//歌手
					String artistTitle = tag.getFirst(FieldKey.ALBUM_ARTIST);
					if(artistTitle==null || artistTitle.equals("") || artistTitle.equals("null")){
						artistTitle = tag.getFirst(FieldKey.ARTIST);
					}
					artist.setTextContent(
							(artistTitle==null || artistTitle.equals("null"))? "" : artistTitle
					);
					//专辑
					album.setTextContent(tag.getFirst(FieldKey.ALBUM));
					//发行公司？？
					company.setTextContent(tag.getFirst(FieldKey.PRODUCER));
					//歌曲长度
					length.setTextContent(Integer.toString(header.getTrackLength()));
					//码率
					kbps.setTextContent(tag.getFirst(FieldKey.RATING));
					//储存路径
					location.setTextContent(Paths.get(file.getAbsolutePath()).toString());
					
					music.appendChild(title);
					music.appendChild(artist);
					music.appendChild(album);
					music.appendChild(company);
					music.appendChild(length);
					music.appendChild(kbps);
					music.appendChild(location);
					
					i++;
					importMusicTask.updateProgress(i, maxProgress);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(file.isDirectory()){
				i = writeXMLFile(file, doc, musics, i);
			}
		}
		return i;
	}
	
	//获取可播放的歌曲总数
	private static void getMaxProgress(File directory){
		File[] files = directory.listFiles();
		
		for(File file : files){
			if(file.isFile() && isSupportedFileType(file)){
				maxProgress++;
			} else{
				getMaxProgress(file);
			}
		}
	}
	
}
