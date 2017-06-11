package app.excuseme.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import app.excuseme.ExcuseMePlayer;
import app.excuseme.model.MusicInfo;
import app.excuseme.model.MusicLibrary;
import app.excuseme.util.ClippedTableCell;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

public class SongsController implements Initializable {

	@FXML private TableView<MusicInfo> tableView;
    @FXML private TableColumn<MusicInfo, Boolean> playingColumn;
    @FXML private TableColumn<MusicInfo, String> titleColumn;
    @FXML private TableColumn<MusicInfo, String> artistColumn;
    @FXML private TableColumn<MusicInfo, String> albumColumn;
    @FXML private TableColumn<MusicInfo, String> lengthColumn;
    
    private ScrollBar scrollBar;
    
    private MusicInfo selectedMusic;
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
    	
    	titleColumn.prefWidthProperty().bind(tableView.widthProperty());
        artistColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.30));
        albumColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.30));
        lengthColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.30));
      
        titleColumn.setCellFactory(x -> new ClippedTableCell<>());
        artistColumn.setCellFactory(x -> new ClippedTableCell<>());
        albumColumn.setCellFactory(x -> new ClippedTableCell<>());
        lengthColumn.setCellFactory(x -> new ClippedTableCell<>());
        
        titleColumn.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getTitle()));
        artistColumn.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getArtist()));
        albumColumn.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getAlbum()));
        lengthColumn.setCellValueFactory(itemData -> new ReadOnlyStringWrapper(itemData.getValue().getMinuteSecondLength()));
        
        titleColumn.setSortable(false);
        artistColumn.setSortable(false);
        albumColumn.setSortable(false);
        lengthColumn.setSortable(false);
        
        ArrayList<MusicInfo> songs = ExcuseMePlayer.getPlayingList()!=null? ExcuseMePlayer.getPlayingList() : MusicLibrary.getLocalMusics();
        ObservableList<MusicInfo> obSongs = FXCollections.observableArrayList(songs);
        
        tableView.setItems(obSongs);
        
        tableView.setRowFactory(x -> {
        	TableRow<MusicInfo> row = new TableRow<>();
        	
        	row.setOnMouseClicked(event -> {
        		TableViewSelectionModel<MusicInfo> sModel = tableView.getSelectionModel();
        		if(event.getClickCount() == 2 && !row.isEmpty()){
        			play();
        		}
        	});
        	
        	return row;
        });
        
        tableView.setOnKeyPressed(event -> {
        	if(event.getCode().equals(KeyCode.ENTER)){
        		play();
        	}
        });
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        	if(newSelection != null && tableView.getSelectionModel().getSelectedIndices().size() == 1){
        		selectedMusic = newSelection;
        	}
        });
        
        

	}
	
	public void play(){
		MusicInfo music = selectedMusic;
		ObservableList<MusicInfo> musicList = tableView.getItems();
		ExcuseMePlayer.setNowPlaying(musicList.indexOf(music));
		ExcuseMePlayer.play();
	}
	
	public MusicInfo getSelectedMusic(){
		return selectedMusic;
	}

}
