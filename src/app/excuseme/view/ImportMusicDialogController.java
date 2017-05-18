package app.excuseme.view;

import app.excuseme.model.MusicLibrary;
import app.excuseme.util.ImportMusicTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ImportMusicDialogController {
	
	@FXML private Label label;
	@FXML private Button importMusicButton;
	@FXML private ProgressBar progressBar;
	
	private Stage dialogStage;
	private boolean musicImport = false;
	
	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}
	
	public boolean isMusicImport() {
		return musicImport;
	}
	
	@FXML
	private void handleImport(){
		try {
			
			DirectoryChooser directoryChooser = new DirectoryChooser();
			String musicDirectory = directoryChooser.showDialog(dialogStage).getPath();
			
			ImportMusicTask<Boolean> task = new ImportMusicTask<Boolean>() {
				
				@Override
				protected Boolean call() throws Exception {
					try{
						MusicLibrary.importMusic(musicDirectory, this);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			};
			
			task.setOnSucceeded((x) -> {
				musicImport = true;
				dialogStage.close();
			});
			
			task.updateProgress(0, 1);
			
			progressBar.progressProperty().bind(task.progressProperty());
			
			Thread thread = new Thread(task);
			thread.start();
			
			label.setText("一脸懵逼地疯狂为你导入歌曲...");
			
			importMusicButton.setVisible(false);
			progressBar.setVisible(true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
