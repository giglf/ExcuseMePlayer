package app.excuseme.util;


/**
 * 储存全局常量的类</br>
 * 会根据需要随时更新
 */
public final class Constants {

	
	//doufm api url属性常量
	public static final String BASE_URL = "http://doufm.info";
    public static final String PLAYLIST_URL = BASE_URL + "/api/playlist/";
    public static final String MUSIC_IN_PLAYLIST_URL = BASE_URL + "/api/playlist/";
//    public static final String LOGIN_URL = BASE_URL + "/api/app_auth/";
    public static final String MUSIC_URL = BASE_URL + "/api/music/";
//    public static final String CHANNEL_URL = BASE_URL + "/api/channel/";
//    public static final String LOGOUT_URL = BASE_URL + "/api/user/logout/";
//    public static final String USER_PROFILE_URL = BASE_URL + "/api/user/profile/";
//    public static final String USER_MUSIC_URL = BASE_URL + "/api/user/music/";
//    public static final String USER_HISTORY_URL = BASE_URL + "/api/user/history/";
	
    
    //APP属性常量
    public static final String APP_VERSION = "ExcuseMePlayer:develop";
    public static final String APP_TITLE = "Excuse Me???";
    
    
    //资源路径
    public static final String IMAGE = "/app/excuseme/util/image/";
    public static final String FXML = "/app/excuseme/view/";
    public static String JAR;
}
