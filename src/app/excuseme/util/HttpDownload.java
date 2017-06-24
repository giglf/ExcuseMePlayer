package app.excuseme.util;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple http downloader. Supported multithread.
 *
 */
public class HttpDownload {
	
	private URL url;						//下载的链接路径
	private File currentPath;
	private File path;						//存放路径
	private File saveFile;					//表示下载的文件
	private String filename;				//表示保存的文件名
	
	private boolean connectOK;
	private boolean isFinish;				//标记下载是否结束
	//用于计算总共耗时
	private long startTime;
	private long endTime;
	
	private int fileLength;					//文件大小
	
	public HttpDownload(String url) {
		try {
			this.url = new URL(url);
			this.isFinish = false;
			this.connectOK = false;
			currentPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			path = currentPath.getParentFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置存放路径，默认为客户端所在位置路径
	 * @param location
	 */
	public void setSaveLocation(File location){
		path = location;
	}
	
	public boolean isNetworkFine(){
		return connectOK;
	}
	
	public File getSaveFile(){
		return saveFile;
	}
	
	public void download(){
		filename = filename==null? url.getFile().substring(url.getFile().lastIndexOf('/')) : filename;
		saveFile = new File(path.getAbsolutePath() + filename);
		startTime = System.currentTimeMillis();
		DownloadThread dThread = new DownloadThread();
		dThread.start();
	}
	
	public boolean isDownloadFinished(){
		return isFinish;
	}
	
	public void setSavingFile(String name){
		filename = name;
	}
	
	//打印消耗时间
	private void printConsumeTime(){
		System.out.println("Total spend time: " + (endTime - startTime) + "ms");
	}
	
	/**
	 * 下载线程类
	 */
	private class DownloadThread extends Thread{  
		
		public DownloadThread() {}
		
		@Override
		public void run() {
			
			try{
				//用于存放线程下载进度，以便下载出错时重启能断点续传
//				File progressFile = new File(this.hashCode() + threadId + ".txt");
//				if(progressFile.exists()){
//					
//					FileInputStream fileInputStream = new FileInputStream(progressFile);
//					BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
//				
//					start += Integer.parseInt(reader.readLine());
//					fileInputStream.close();
//				}
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.setRequestProperty("User-Agent", Constants.APP_VERSION);
				//doufm服务器不支持该用法
//				con.setRequestProperty("Range", "bytes=" + start + "-" + end); 
				
				if(connection.getResponseCode() == 206 || connection.getResponseCode() == 200){
					connectOK = true;
					fileLength = connection.getContentLength();
//					saveFile.deleteOnExit();
					RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rwd");
					randomAccessFile.setLength(fileLength);
					randomAccessFile.seek(0);
					
					InputStream inputStream = connection.getInputStream();
					
					byte[] buff = new byte[1024*1024*4]; //缓冲区大小设置为4M
					int len;
					
					while((len = inputStream.read(buff)) > 0 ){
						randomAccessFile.write(buff, 0, len);
					}
					
					System.out.println("thread " + this.hashCode() + ": Download finish.");
					randomAccessFile.close();
					
					
//					progressFile.delete();
					endTime = System.currentTimeMillis();
					isFinish = true;
					printConsumeTime();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}	
	
}
