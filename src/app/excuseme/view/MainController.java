package app.excuseme.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import app.excuseme.ExcuseMePlayer;
import app.excuseme.model.MusicInfo;
import app.excuseme.model.MusicLibrary;
import app.excuseme.util.Constants;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainController implements Initializable {

	private VolumePopupController volumePopupController;
	private Stage volumePopup;
	
	@FXML private Region frontSliderTrack;
	@FXML private Region backSliderTrack;
	@FXML private Slider timeSlider;
//	@FXML private Label timePassed;
	@FXML private Label timeRemaining;
	
	@FXML private HBox controlBox;
	@FXML private Pane backButton;
	@FXML private Pane playButton;
	@FXML private Pane pauseButton;
	@FXML private Pane skipButton;
	@FXML private Pane loopButton;
	@FXML private Pane shuffleButton;
	
	@FXML private TextField searchBox;
	@FXML private MenuButton channelSelect;
	@FXML private ToggleButton chooseOnlineType;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		controlBox.getChildren().remove(2);
		//画面适配，与宽值比绑定 -> width*(value/maxWidth)
		frontSliderTrack.prefWidthProperty().bind(timeSlider.widthProperty().multiply(timeSlider.valueProperty().divide(timeSlider.maxProperty())));
		
		createVolumePopup();
		
		loopButton.setOnMouseClicked(x -> {
			ExcuseMePlayer.toggleLoop();
		});
		
		

		//默认打开时处于在线听歌状态
		chooseOnlineType.setSelected(true); 
		
		timeSlider.setFocusTraversable(false);
		
		//进度条拖动时歌曲定位
		timeSlider.valueChangingProperty().addListener(
			(slider, wasChanging, isChanging) -> {
				if(wasChanging){
					int seconds = (int)Math.round(timeSlider.getValue() / 4.0);
					timeSlider.setValue(seconds * 4);
					ExcuseMePlayer.seek(seconds); 
				}
			}
		);
		
		//进度条点击时歌曲定位
		timeSlider.valueProperty().addListener(
			(slider, oldValue, newValue) -> {
				double previous = oldValue.doubleValue();
				double current = newValue.doubleValue();
				if(!timeSlider.isValueChanging() && current != previous+1 && !isTimeSliderPressed()){
					int seconds = (int)Math.round(current / 4.0);
					timeSlider.setValue(seconds * 4);
					ExcuseMePlayer.seek(seconds);
				}
			}
		);
		
		initializeTimeSlider();
		initializeTimeLabels();
		//PlayerTODO init
		
		
	}
	
	private void createVolumePopup() {
		
		try {
			Stage stage = ExcuseMePlayer.getStage();
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Constants.FXML + "VolumePopup.fxml"));
			HBox view = loader.load();
			volumePopupController = loader.getController();
			
			Stage popup = new Stage();
			popup.setScene(new Scene(view));
			popup.initStyle(StageStyle.UNDECORATED);
			popup.initOwner(stage);
			popup.setX(stage.getWidth() - 270);
			popup.setY(stage.getHeight() - 120);
			popup.focusedProperty().addListener((x, wasFocused, isFocused) -> {
				if(wasFocused && !isFocused){
					volumeHideAnimation.play();
				}
			});
			volumeHideAnimation.setOnFinished(x -> popup.hide());
			
			popup.show();
			popup.hide();
			volumePopup = popup;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initializeTimeSlider(){
		MusicInfo music = ExcuseMePlayer.getNowPlaying();
		if(music != null){
			timeSlider.setMin(0);
			timeSlider.setMax(Integer.parseInt(music.getLength()) * 4);
			timeSlider.setValue(0);
			timeSlider.setBlockIncrement(1);
		} else{
			timeSlider.setMin(0);
			timeSlider.setMax(1);
			timeSlider.setValue(0);
			timeSlider.setBlockIncrement(1);
		}
	}
	
	public void updateTimeSlider(){
		timeSlider.increment();
	}
	
	public void initializeTimeLabels(){
		MusicInfo music = ExcuseMePlayer.getNowPlaying();
		if(music != null){
			timeRemaining.setText(ExcuseMePlayer.getTimeRemaining());
		} else{
			timeRemaining.setText("");
		}
	}
	
	public void updateTimeLabels(){
		timeRemaining.setText(ExcuseMePlayer.getTimeRemaining());
	}
	
	
	/**
	 * 更新播放暂停按钮
	 * @param isPlaying
	 */
	public void updatePlayPauseIcon(boolean isPlaying){
		controlBox.getChildren().remove(1);
		if(isPlaying){
			controlBox.getChildren().add(1, pauseButton);
		}else {
			controlBox.getChildren().add(1, playButton);
		}
	}
	
	@FXML
	private void back(){
		ExcuseMePlayer.back();
	}
	
	@FXML
	private void playPause(){
		if(ExcuseMePlayer.isPlaying()){
			ExcuseMePlayer.pause();
		} else{
			ExcuseMePlayer.play();
		}
	}
	
	@FXML
	private void skip(){
		ExcuseMePlayer.skip();
	}
	
	@FXML
	public void volumeClick(){
		if(!volumePopup.isShowing()){
			Stage stage = ExcuseMePlayer.getStage();
			volumePopup.setX(stage.getX() + stage.getWidth() - 265);
			volumePopup.setY(stage.getY() + stage.getHeight() -115);
			volumePopup.show();
			volumeShowAnimation.play();
		}
	}
	
	
	@FXML
	public void networkSelectAction(){
		//当触发本地歌曲时，检测储存本地歌曲信息的XML是否存在
		if(!chooseOnlineType.isSelected()){
			checkLocalLibraryXML(); //PlayerTODO networkSelectAction
		}
	}
	
	public boolean isTimeSliderPressed(){
		return backSliderTrack.isPressed() || frontSliderTrack.isPressed();
	}
	
	public Slider getVolumeSilder(){ 
		return volumePopupController.getSlider();
	}
	
	//-------------------------------本地歌库存储函数------------------------------
	
	public void checkLocalLibraryXML(){
		//获取当前jar文件所在路径
		File jarFile = null;
		try {
			jarFile = new File(ExcuseMePlayer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String jarFilePath = jarFile.getParentFile().getPath();
		
		Constants.JAR = jarFilePath + "/";
		//用于储存本地歌曲的信息
		File libraryXML = new File(Constants.JAR + "library.xml");
		
		Path musicDirectory;
		if(libraryXML.exists()){
			musicDirectory = xmlMusicDirPathFinder();
			int musicNumcount = musicDirFileNumCounter(musicDirectory.toFile(), 0);
			int musicNumfind = xmlMusicDirFileNumFinder();
			// 对比本地目录跟xml中储存的是否有变化，来判断用不用update
			if(musicNumcount != musicNumfind){
				// PlayerTODO updateXML
			}
		} else if (!libraryXML.exists()){
			createLibraryXML();
		}
	}
	
	/**
	 * 新窗口创建library xml
	 */
	private static void createLibraryXML(){
		try{
			FXMLLoader loader = new FXMLLoader(ExcuseMePlayer.class.getResource(Constants.FXML + "ImportMusicDialog.fxml"));
			BorderPane importView = loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Configuration");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initStyle(StageStyle.UTILITY);//PlayerTODO 发布时改为UNDECORATED
			dialogStage.setResizable(false);
			dialogStage.initOwner(ExcuseMePlayer.getStage());
			
			dialogStage.setScene(new Scene(importView));
			
			ImportMusicDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			
			dialogStage.showAndWait();
			//如果导入失败，则关闭应用
			if(!controller.isMusicImport()){
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 从已有的xml文件中搜索本地music目录
	 * @return 曲库目录的路径
	 */
	private static Path xmlMusicDirPathFinder(){
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			//联合临近的数据？？
			factory.setProperty("javax.xml.stream.isCoalescing", true);
			FileInputStream inputStream = new FileInputStream(new File(Constants.JAR + "library.xml"));
			XMLStreamReader reader = factory.createXMLStreamReader(inputStream, "utf-8");
			
			String element = null;
			String path = null;
			
			while(reader.hasNext()){
				reader.next();
				if(reader.isWhiteSpace()){
					continue;
				} else if(reader.isStartElement()){
					element = reader.getName().getLocalPart();
				} else if(reader.isCharacters() && element.equals("path")){
					path = reader.getText();
					break;
				}
			}
			reader.close();
			return Paths.get(path);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 从xml文件中检索当前拥有歌曲数量
	 * @return 歌曲 
	 */
	private static int xmlMusicDirFileNumFinder(){
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			//联合临近的数据？？
			factory.setProperty("javax.xml.stream.isCoalescing", true);
			FileInputStream inputStream = new FileInputStream(new File(Constants.JAR + "library.xml"));
			XMLStreamReader reader = factory.createXMLStreamReader(inputStream, "utf-8");
			
			String element = null;
			String fileNum = null;
			
			while(reader.hasNext()){
				reader.next();
				if(reader.isWhiteSpace()){
					continue;
				} else if(reader.isStartElement()){
					element = reader.getName().getLocalPart();
				} else if(reader.isCharacters() && element.equals("fileNum")){
					fileNum = reader.getText();
					break;
				}
			}
			reader.close();
			return Integer.parseInt(fileNum);
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 计算目录中歌曲的数量
	 * @param musisDirecity 歌曲的路径
	 * @param i				当前初始搜索的歌曲数量
	 * @return				当前目录下包括子目录的歌曲数量
	 */
	private static int musicDirFileNumCounter(File musisDirecity, int i){
		File[] files = musisDirecity.listFiles();
		int num = i;
		for(File file : files){
			if(file.isDirectory()){
				num += musicDirFileNumCounter(file, i);
			} else if(file.isFile() && MusicLibrary.isSupportedFileType(file)){
				num++;
			}
		}
		return num;
	}
	
	//-------------------------------动画过渡----------------------------------
	private Animation volumeShowAnimation = new Transition() {
		{
			setCycleDuration(Duration.millis(250));
			setInterpolator(Interpolator.EASE_BOTH);
		}
		@Override
		protected void interpolate(double frac) {
			volumePopup.setOpacity(frac);
		}
	};
	
	private Animation volumeHideAnimation = new Transition() {
		{
			setCycleDuration(Duration.millis(250));
			setInterpolator(Interpolator.EASE_BOTH);
		}
		@Override
		protected void interpolate(double frac) {
			volumePopup.setOpacity(1.0 - frac);
		}
	};
	
	
}
