package app.excuseme.util;

import javafx.concurrent.Task;

public abstract class ImportMusicTask<V> extends Task<V> {
	
	public void updateProgress(int progress, int maxProgress){
		//必须加入(long)否则调用的是当前的updateProgress，会陷入死循环
		updateProgress((long)progress, (long)maxProgress);
	}
	
}
