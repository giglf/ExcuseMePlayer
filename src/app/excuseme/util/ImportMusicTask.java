package app.excuseme.util;

import javafx.concurrent.Task;

public abstract class ImportMusicTask<V> extends Task<V> {
	
	public void updateProgress(int progress, int maxProgress){
		updateProgress(progress, maxProgress);
	}
	
}
