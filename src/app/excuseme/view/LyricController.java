package app.excuseme.view;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.corba.se.spi.orb.StringPair;

import app.excuseme.model.Lyric;
import app.excuseme.model.LyricItem;
import app.excuseme.model.MusicInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LyricController implements Initializable{

	@FXML VBox lyricPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lyricPane.getChildren().clear();
	}
	
	public void loadLyric(MusicInfo musicInfo, double time){
		lyricPane.getChildren().clear();
		
		long nowTime = (long) (time*1000);
		Lyric lyric = load(musicInfo);
		if(lyric == null) return;
		
		List<LyricItem> list = lyric.getLyricitems();
		int index = 0;
		int size = list.size();
		
		//获取当前位置
		for(int i=size-1; i>=0; i--){
			LyricItem item = lyric.getLyricitems().get(i);
			long gap = nowTime - item.getTime();
			if(100 < gap){
				index = i;
				break;
			}
		}
		
		//前后共显示7行
		for(int i=0; i<13; i++){
			if(index - 6 + i < 0){
				continue;
			}
			if(index - 6 + i >= size){
				break;
			}
			
			LyricItem item = list.get(index-6+i);
			if(i < 6){
				getOtherText(item, i);
			} else if(i == 6){
				getCurrentText(item);
			} else{
				getOtherText(item, 12 - i);
			}
		}
		
	}
	
	
	private Lyric load(MusicInfo musicInfo){
		String path = musicInfo.getLyricPath();
		if(path == null || path == "") return null;
		
		Lyric lyric = new Lyric();
		try {
			File lyricFile = new File(path);
			FileInputStream inputStream = new FileInputStream(lyricFile);
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			
			String[] lyricArray = new String(data, "utf-8").split("\n");
			int lines = 0;
			List<LyricItem> listItems= new ArrayList<LyricItem>();
			for(int i=0; i<lyricArray.length; i++){
				String lyricStr = lyricArray[i].replace("\r", "");
				
				if(lyricStr.startsWith("[ti")){ //歌曲名
					lyricStr = lyricStr.replace("[ti:", "");
					lyricStr = lyricStr.replace("]", "");
					lyric.setTitle(lyricStr);
				} else if(lyricStr.startsWith("[ar")){
					lyricStr = lyricStr.replace("[ar:", "");
					lyricStr = lyricStr.replace("]", "");
					lyric.setArtist(lyricStr);
				} else if(lyricStr.startsWith("[al")){ //专辑名
				} else if(lyricStr.startsWith("[by")){ //歌词制作者
				} else if(lyricStr.startsWith("[offset")){ //补偿时间
				} else{
					LyricItem item = new LyricItem();
					lyricStr = lyricStr.replace("[", "");
					String[] infoArray = lyricStr.split("]");
					
					if(infoArray.length < 2) continue;
					else{
						//处理多时间戳的情况，正常情况下length=2
						for(int j=0;j<infoArray.length-1;j++){
							String[] timeStr = infoArray[j].split(":");
							if(timeStr.length < 2) continue;
							
							long time = (long) ((Double.parseDouble(timeStr[0])*60 + Double.parseDouble(timeStr[1]))*1000);
							String content = infoArray[infoArray.length-1];
							item.setTime(time);;
							item.setContent(content);
							listItems.add(item);
							lines++;
						}
					}
 				}
			}
			
			Collections.sort(listItems, (a, b) ->{
				if(a.getTime() > b.getTime()){
					return 1;
				} else if(a.getTime() < b.getTime()){
					return -1;
				}
				return 0;
			});
			
			lyric.setLyricitems(listItems);
			lyric.setLines(lines);
			lyric.setInfo(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lyric;
	}
	
	/**
	 * 设定并显示当前行歌词的样式
	 * @param item
	 */
	private void getCurrentText(LyricItem item){
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(8, 0, 8, 0));
		
		Text text = new Text(item.getContent());
		text.setFont(Font.font("Arial", 24));
		text.setFill(Color.WHITE);
		text.setStrokeWidth(0.2);
		text.setStroke(Color.BLUE);
		hBox.getChildren().add(text);
		lyricPane.getChildren().add(hBox);
	}
	
	/**
	 * 获取并显示其他行的歌词
	 * @param item
	 * @param i
	 */
	private void getOtherText(LyricItem item, int i){
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		hBox.setPadding(new Insets(i*0.8, 0, i*0.8, 0));
		
		Text text = new Text(item.getContent());
		text.setFont(Font.font("Arial", 15 + i));
		text.setOpacity(0.25*(i+1));
		text.setFill(Color.GRAY);
		text.setStrokeWidth(0.2);
		text.setStroke(Color.WHITE);
		hBox.getChildren().add(text);
		lyricPane.getChildren().add(hBox);
	}
	
}
