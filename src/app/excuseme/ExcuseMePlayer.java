package app.excuseme;

import java.util.ArrayList;
import java.util.Timer;

import app.excuseme.model.MusicInfo;
import app.excuseme.util.Constants;
import app.excuseme.view.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExcuseMePlayer extends Application{

	private static Stage stage;
	private static MainController mainController;
	
	private static MediaPlayer mediaPlayer;
	private static Timer timer;
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
		
		//应用基本标签设置
		ExcuseMePlayer.stage = stage;
		ExcuseMePlayer.stage.setTitle(Constants.APP_TITLE);
		ExcuseMePlayer.stage.getIcons().add(new Image(this.getClass().getResource(Constants.IMAGE + "AppIcon.png").toString()));
		ExcuseMePlayer.stage.setOnCloseRequest(event ->{
			Platform.exit();
			System.exit(0);
		});
		
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Constants.FXML + "Main.fxml"));
		
		Scene scene = new Scene(loader.load());
		stage.setScene(scene);
//		stage.setMaximized(true);
		stage.show();
	}
	
	public static void seek(int seconds){
		if(mediaPlayer != null){
			mediaPlayer.seek(new Duration(seconds * 1000));
			// PlayerTODO mainController中updateTimeLabels未完成
		}
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
	
	public static MusicInfo getNowPlaying(){
		return nowPlaying;
	}
	
	public static Stage getStage(){
		return stage;
	}
	
	

}
