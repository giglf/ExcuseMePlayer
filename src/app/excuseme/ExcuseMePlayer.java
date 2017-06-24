package app.excuseme;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import app.excuseme.model.MusicInfo;
import app.excuseme.model.MusicLibrary;
import app.excuseme.model.PlayListInfo;
import app.excuseme.network.OnlineDataGetter;
import app.excuseme.network.RequestMusic;
import app.excuseme.util.CacheManager;
import app.excuseme.util.Constants;
import app.excuseme.view.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExcuseMePlayer extends Application{

	private static Stage stage;
	private static MainController mainController;
	
	private static MediaPlayer mediaPlayer;
	private static Timer timer;
	private static int timerCounter;
	private static PlayListInfo currentOnlinePlayList;
	private static ArrayList<MusicInfo> nowPlayingList;
	private static int nowPlayingIndex;
	private static MusicInfo nowPlaying;
	
	private static boolean isNetworkType = true;
	private static boolean isMuted = false;
	private static boolean isLoopActive = false;
	private static boolean isShuffleActive = false;
	
	public static void main(String[] args) {
		Application.launch(ExcuseMePlayer.class);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		//获取当前jar文件所在路径
		File jarFile = null;
		try {
			jarFile = new File(ExcuseMePlayer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String jarFilePath = jarFile.getParentFile().getPath();	
		Constants.JAR = jarFilePath + "/";
		
		timer = new Timer();
		timerCounter = 0;
		
		//应用基本标签设置
		ExcuseMePlayer.stage = stage;
		ExcuseMePlayer.stage.setTitle(Constants.APP_TITLE);
		ExcuseMePlayer.stage.getIcons().add(new Image(this.getClass().getResource(Constants.IMAGE + "AppIcon.png").toString()));
		ExcuseMePlayer.stage.setOnCloseRequest(event ->{
			if(mediaPlayer!=null){
				mediaPlayer.stop();
				mediaPlayer.dispose();
				System.gc();
			}
			Platform.exit();
			System.exit(0);
		});
		
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Constants.FXML + "Main.fxml"));
		
		Scene scene = new Scene(loader.load());
		stage.setScene(scene);
		stage.setResizable(false);
//		stage.setMaximized(true);
		stage.show();
		
		mainController = loader.getController();
		
//		channelUpdateAndPlay();
//		nowPlayingList = MusicLibrary.getOnlineMusics(currentOnlinePlayList);
//		mainController.loadView();
//		PlayerTest();
	}
	
	//PlayerTODO testing 
	public static void PlayerTest(){
//		nowPlayingList = MusicLibrary.getOnlineMusics(currentOnlinePlayList);
		setNowPlaying(0);
		play();
	}
	
	
	//从当前播放列表搜索歌曲并播放
	public static void getLocalSearchMusicAndPlay(String songName){
		for(MusicInfo song : nowPlayingList){
			if(song.getTitle().equals(songName)){
				setNowPlaying(nowPlayingList.indexOf(song));
				play();
				return;
			}
		}
	}
	
	
	//获取在线搜索到的歌曲并且加载到列表播放
	public static void getOnlineSearchMusicAndPlay(String songName){
		try {
			MusicInfo[] music = RequestMusic.searchMusic(songName);
			if(music.length == 0){
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Excuse Me???");
				alert.setHeaderText("ERROR");
				alert.setContentText("搜不到这首歌");
				alert.showAndWait();
			}else{
				nowPlayingList = OnlineDataGetter.musicGetter(music);
				mainController.loadView();
				setNowPlaying(0);
				play();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void playingLocalMusic(){
		mainController.checkLocalLibraryXML();
		nowPlayingList = MusicLibrary.getLocalMusics();
		mainController.loadView();
		setNowPlaying(0);
		play();
	}
	
	public static boolean channelUpdateAndPlay(){
		nowPlayingList = MusicLibrary.getOnlineMusics(currentOnlinePlayList);
		if(nowPlayingList != null){
			mainController.loadView();
			setNowPlaying(0);
			play();
			return true;
		} else{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Excuse Me???");
			alert.setHeaderText("网络错误");
			alert.setContentText("不能获取在线歌曲……听本地的歌啦！");
			alert.showAndWait();
			return false;
		}
	}
	
	public static void setCurrentOnlinePlayList(PlayListInfo playList){
		currentOnlinePlayList = playList;
	}
	
	
	/**
	 * 设置目前正在播放的歌曲
	 * @param index 歌曲列表的序号
	 */
	public static void setNowPlaying(int index){
		nowPlayingIndex = index;
		nowPlaying = nowPlayingList.get(index);
		if(mediaPlayer != null){
			mediaPlayer.stop();
		}
		if(timer != null){
			timer.cancel();
		}
		timer = new Timer();
		timerCounter = 0;
		String path = nowPlaying.getLocation();
		Media media = new Media(Paths.get(path).toUri().toString());
		
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.dispose();
		}
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.volumeProperty().bind(mainController.getVolumeSilder().valueProperty().divide(200));
		mediaPlayer.setOnEndOfMedia(new SongSkipper());
		mediaPlayer.setMute(isMuted);
		mainController.initializeTimeSlider();
		mainController.initializeTimeLabels();
		//PlayerTODO nowPlaying
	}
	
	/**
	 * 定位到歌曲的某个点
	 * @param seconds
	 */
	public static void seek(int seconds){
		if(mediaPlayer != null){
			mediaPlayer.seek(new Duration(seconds * 1000));
			timerCounter = seconds * 4;
			mainController.updateTimeLabels();
		}
	}
	
	
	/**
	 * 开始播放选中的歌曲
	 */
	public static void play(){
		if(mediaPlayer != null && !isPlaying()){
			mediaPlayer.play();
			timer.scheduleAtFixedRate(new TimerUpdater(), 0, 250);
			mainController.updatePlayPauseIcon(true);
		}
	}
	
	/**
	 * 暂停正在播放的歌曲
	 */
	public static void pause(){
		if(isPlaying()){
			mediaPlayer.pause();
			timer.cancel();
			timer = new Timer();
			mainController.updatePlayPauseIcon(false);
		}
	}
	
	/**
	 * 切到下一首
	 */
	public static void skip(){
		if(nowPlayingIndex < nowPlayingList.size()-1){
			boolean isPlaying = isPlaying();
			mainController.updatePlayPauseIcon(isPlaying);
			setNowPlaying(nowPlayingIndex + 1);
			if(isPlaying){
				play();
			}
		} else if(isLoopActive){
			boolean isPlaying = isPlaying();
			mainController.updatePlayPauseIcon(isPlaying);
			nowPlayingIndex = 0;
			setNowPlaying(nowPlayingIndex);
			if(isPlaying){
				play();
			}
		} else{
			mainController.updatePlayPauseIcon(false);
			nowPlayingIndex = 0;
			setNowPlaying(nowPlayingIndex);
		}
	}
	
	/**
	 * 根据当前播放到的进度判断重新播放改歌曲还是切到上一首
	 */
	public static void back(){
		if(timerCounter > 20 || nowPlayingIndex == 0){
			mainController.initializeTimeSlider();
			seek(0);
		} else{
			boolean isPlaying = isPlaying();
			setNowPlaying(nowPlayingIndex - 1);
			if(isPlaying){
				play();
			}
		}
	}
	
	/**
	 * 触发随机播放
	 */
	public static void toggleShuffer(){
		isShuffleActive = !isShuffleActive;
		if(isShuffleActive){
			Collections.shuffle(nowPlayingList);
		}
		mainController.loadView();
		
	}
	
	/**
	 * 触发循环播放
	 */
	public static void toggleLoop(){
		isLoopActive = !isLoopActive;
	}
	
	public static boolean isLoopActive(){
		return isLoopActive;
	}
	
	public static void setIsNetworkType(boolean isNetwork){
		isNetworkType = isNetwork;
	}
	
	public static boolean isNetworkType(){
		return isNetworkType;
	}
	
	/**
	 * 返回播放器是否正在播放
	 * @return 是否正在播放
	 */
	public static boolean isPlaying(){
		return mediaPlayer!=null && MediaPlayer.Status.PLAYING.equals(mediaPlayer.getStatus());
	}
	
	/**
	 * 根据isMute设置播放器是否静音
	 * @param isMuted
	 */
	public static void mute(boolean isMuted){
		ExcuseMePlayer.isMuted = !isMuted;
		if(mediaPlayer != null){
			mediaPlayer.setMute(!isMuted);
		}
	}
	
	public static String getTimeRemaining(){
		long secondesPassed = timerCounter / 4;
		long totalSeconds = Integer.parseInt(getNowPlaying().getLength());
		long secondsRemaining = totalSeconds - secondesPassed;
		long minutes = secondsRemaining / 60;
		long seconds = secondsRemaining % 60;
		return Long.toString(minutes) + ":" + (seconds<10? "0" + seconds : Long.toString(seconds));
	}
	
	public static MusicInfo getNowPlaying(){
		return nowPlaying;
	}
	
	public static Stage getStage(){
		return stage;
	}
	
	public static ArrayList<MusicInfo> getPlayingList(){
		return nowPlayingList;
	}
	
	
	
//	------------------------------------------------------------------
	
	private static class SongSkipper implements Runnable{
		@Override
		public void run() {
			skip();
		}
	}
	
	private static class TimerUpdater extends TimerTask{
		private int length = Integer.parseInt(getNowPlaying().getLength()) * 4;
		
		@Override
		public void run() {
			Platform.runLater(() -> {
				if(timerCounter < length) {
					if(++timerCounter % 4 == 0){
						mainController.updateTimeLabels();
						//PlayerTODO secondsPlayed++
					}
					if(!mainController.isTimeSliderPressed()){
						mainController.updateTimeSlider();
					}
				}
			});
		}
	}

}
