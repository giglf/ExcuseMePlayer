package app.excuseme.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple http downloader. Supported multithread.
 *
 */
public class HttpDownload {

	
	private int threadNum;					//线程数
	private int finishThreadNum;			//已经完成的线程数
	private URL url;						//下载的链接路径
	private HttpURLConnection connection;	//http链接
	private File currentPath;
	private File path;						//存放路径
	private File saveFile;					//表示下载的文件
	private String filename;				//表示保存的文件名
	
	private boolean isFinish;				//标记下载是否结束
	//用于计算总共耗时
	private long startTime;
	private long endTime;
	
	private int fileLength;					//文件大小
	
	public HttpDownload(String url, int threadNum) {
		try {
			this.url = new URL(url);
			this.threadNum = threadNum;
			this.finishThreadNum = 0;
			this.isFinish = false;
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
	
	public File getSaveFile(){
		return saveFile;
	}
	
	public void download(){
		startTime = System.currentTimeMillis();
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			//正常响应，获取文件大小，并划分各部分，启动下载线程
			if (connection.getResponseCode() == 200) {
				fileLength = connection.getContentLength();
				filename = filename==null? url.getFile().substring(url.getFile().lastIndexOf('/')) : filename;
				saveFile = new File(path.getAbsolutePath() + filename);
				saveFile.deleteOnExit();
				RandomAccessFile tmpRaf = new RandomAccessFile(saveFile, "rwd");
				tmpRaf.setLength(fileLength);
				tmpRaf.close();

				int size = fileLength / threadNum;

				for (int i = 0; i < threadNum; i++) {
					int start = i * size;
					int end = i * size + size - 1;

					if (i == threadNum - 1) {
						end = fileLength - 1;
					}

					DownloadThread dThread = new DownloadThread(i, start, end);
					dThread.start();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		private int threadId;
		private int start;
		private int end;
		
		public DownloadThread(int threadId, int start, int end) {
			this.threadId = threadId;
			this.start = start;
			this.end = end;
		}
		
		
		@Override
		public void run() {
			
			try{
				//用于存放线程下载进度，以便下载出错时重启能断点续传
				File progressFile = new File(this.hashCode() + threadId + ".txt");
				if(progressFile.exists()){
					
					FileInputStream fileInputStream = new FileInputStream(progressFile);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
				
					start += Integer.parseInt(reader.readLine());
					fileInputStream.close();
				}
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Range", "bytes=" + start + "-" + end);
				
				if(con.getResponseCode() == 206 || con.getResponseCode()==200){
					InputStream inputStream = con.getInputStream();
					
					byte[] buff = new byte[1024*1024*4]; //缓冲区大小设置为4M
					int len;
					int total = 0;
					
					RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rwd");
					randomAccessFile.seek(start);
					
					while((len = inputStream.read(buff)) > 0 ){
						randomAccessFile.write(buff, 0, len);
						total += len;
						
						RandomAccessFile progressRaf = new RandomAccessFile(progressFile, "rwd");
						progressRaf.write((total+"").getBytes());
						progressRaf.close();
					}
					
					System.out.println("thread " + threadId + ": Download finish.");
					randomAccessFile.close();
					
					finishThreadNum++;
					
					progressFile.delete();
					endTime = System.currentTimeMillis();
					isFinish = true;
					printConsumeTime();
				}
				
//				//下载完成，删除储存下载进度的文件
//				synchronized (url) {
//					if(finishThreadNum == threadNum){
//						for(int i=0;i<threadNum;i++){
//							File file = new File(this.hashCode() + i + ".txt");
//							file.delete();
//						}
//						finishThreadNum = 0;
//						endTime = System.currentTimeMillis();
//						isFinish = true;
//						printConsumeTime();
//					}
//				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
//	public static void main(String[] args) {
//		HttpDownload downloader = new HttpDownload("http://doufm.info/api/fs/53016fa81d41c805512c9ce0/", 10);
//		downloader.setSaveLocation(CacheManager.getSongsDir().toFile());
//		downloader.setSavingFile("/music.mp3");
//		downloader.download();
//		
//	}
	
	
}
