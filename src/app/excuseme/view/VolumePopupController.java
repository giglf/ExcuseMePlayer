package app.excuseme.view;

import java.net.URL;
import java.util.ResourceBundle;

import app.excuseme.ExcuseMePlayer;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class VolumePopupController implements Initializable{

	@FXML private Slider volumeSlider;
	@FXML private Region frontVolumeTrack;
	@FXML private Label volumeLabel;
	@FXML private Pane muteButton;
	@FXML private Pane mutedButton;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//宽度适配，与进度条slider同样原理
		frontVolumeTrack.prefWidthProperty().bind(volumeSlider.widthProperty().subtract(30).multiply(volumeSlider.valueProperty().divide(volumeSlider.maxProperty())));
		volumeSlider.valueProperty().addListener((x, y, newValue) -> {
			volumeLabel.setText(Integer.toString(newValue.intValue()));
		});
		
		volumeSlider.setOnMousePressed(x -> {
			if(mutedButton.isVisible()){
				muteClick();
			}
		});
		
	}
	
	
	@FXML
	private void muteClick(){
		
		//PseudoClass muted = PseudoClass.getPseudoClass("muted");
		boolean isMuted = mutedButton.isVisible();
		muteButton.setVisible(isMuted);
		mutedButton.setVisible(!isMuted);
		//volumeSlider.pseudoClassStateChanged(muted, !isMuted);
		//frontVolumeTrack.pseudoClassStateChanged(muted, !isMuted);
		//volumeLabel.pseudoClassStateChanged(muted, !isMuted);
		ExcuseMePlayer.mute(isMuted);
		
	}
	
	
	Slider getSlider(){
		return volumeSlider;
	}
	
	
	
}
