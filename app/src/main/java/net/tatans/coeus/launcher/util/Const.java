package net.tatans.coeus.launcher.util;

import net.tatans.coeus.launcher.R;

import android.net.Uri;

/**
 * 规定把所有常量都放到这里
 *
 * @author Yuliang
 * @time 2015/3/25
 */
public class Const {
	/* 天气数据接口*/
	public static final String WEATHER_API_URL = "http://wthrcdn.etouch.cn/WeatherApi";
	/* 新闻接口*/
	public static String NEWS_URL = "http://115.29.11.17:8091/news/rest/v1.0/find/findOrderByTime.do";
	/* 音乐接口 */
	public static String MUSIC_URL = "http://115.29.11.17:8092/iapetus-music/rest/v1.0/getmusic";
	/* 笑话接口 */
	public static String JOKE_URL = "http://api.laifudao.com/open/xiaohua.json";

	public static final String[] RADIO_URL = {
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1209/curpage/1/pagesize/1000",//北京
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1208/curpage/1/pagesize/1000",//上海
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1216/curpage/1/pagesize/1000",//天津
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1219/curpage/1/pagesize/1000",//重庆
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1215/curpage/1/pagesize/1000",//广东
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1212/curpage/1/pagesize/1000",//浙江
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1210/curpage/1/pagesize/1000",//江苏
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1213/curpage/1/pagesize/1000",//湖南
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1211/curpage/1/pagesize/1000",//四川
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1217/curpage/1/pagesize/1000",//山西
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1218/curpage/1/pagesize/1000",//河南
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1220/curpage/1/pagesize/1000",//湖北
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1221/curpage/1/pagesize/1000",//黑龙江
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1214/curpage/1/pagesize/1000",//辽宁
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1222/curpage/1/pagesize/1000",//河北
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1223/curpage/1/pagesize/1000",//山东
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1224/curpage/1/pagesize/1000",//安徽
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1226/curpage/1/pagesize/1000",//福建
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1227/curpage/1/pagesize/1000",//广西
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1228/curpage/1/pagesize/1000",//贵州
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1237/curpage/1/pagesize/1000",//云南
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1229/curpage/1/pagesize/1000",//江西
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1230/curpage/1/pagesize/1000",//吉林
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1225/curpage/1/pagesize/1000",//甘肃
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1241/curpage/1/pagesize/1000",//陕西
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1231/curpage/1/pagesize/1000",//宁夏
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1235/curpage/1/pagesize/1000",//内蒙古
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1234/curpage/1/pagesize/1000",//海南
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1233/curpage/1/pagesize/1000",//西藏
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1232/curpage/1/pagesize/1000",//青海
			"http://api2.qingting.fm/v6/media/categories/5/channels/order/0/attr/1236/curpage/1/pagesize/1000",//新疆
	};

	public static final String FOCUS = "focus";
	public static final int No_Focus = 000;
	public static final int Music = 10001;
	public static final int News = 10002;
	public static final int Radio = 10003;
	public static final int Calendar = 10004;
	public static final int Time = 10005;
	public static final int Weather = 10006;
	public static final int Joke = 10007;
	public static final int PoiSearch = 10008;
	public static final int AppIntroduce = 10009;
	public static final int UPDATE_MESSAGE = 0;

	public static final String LAUNCHER_ONE_KEY = "oneKeyApp";
	public static final String LAUNCHER_App = "LauncherApp";
	public static final String LAUNCHER_Empty = "LauncherEmpty";
	public static final String LAUNCHER_COMMUNICATE = "launcherCommunicate";
	public static final String LAUNCHER_WEATHER = "LauncherWeather";
	public static final String LAUNCHER_SOS = "launcherSOS";
	public static final String LAUNCHER_DB = "tatansLauncher";
	public static final String LAUNCHER_TAB = "launcher";
	public static final String LAUNCHER_NAME = "添加";
	public static final int LAUNCHER_ICON = R.mipmap.addtainjia;

	public static final String OneKey_Name_0 = "天坦新闻";
	public static final String OneKey_Content_0 = "天坦新闻";
	public static final int OneKey_Icon_0 = R.mipmap.luancher_news;

	public static final String OneKey_Name_1 = "天坦电台";
	public static final String OneKey_Content_1 = "天坦电台";
	public static final int OneKey_Icon_1 = R.mipmap.luancher_radio;

	public static final String OneKey_Name_2 = "天坦音乐";
	public static final String OneKey_Content_2 = "天坦音乐";
	public static final int OneKey_Icon_2 = R.mipmap.luancher_music;

	public static final String OneKey_Name_3 = "天坦笑话";
	public static final String OneKey_Content_3 = "天坦笑话";
	public static final int OneKey_Icon_3 = R.mipmap.luancher_joke;

	public static final String OneKey_Name_4 = "本地音乐";
	public static final String OneKey_Content_4 = "本地音乐";
	public static final int OneKey_Icon_4 = R.mipmap.luancher_native_music;

	public static final String OneKey_Name_5 = "天坦天气";
	public static final String OneKey_Content_5 = "天坦天气";
	public static final int OneKey_Icon_5 = R.mipmap.luancher_weather;

	public static final String OneKey_Name_6 = "央广新闻";
	public static final String OneKey_Content_6 = "央广新闻";
	public static final int OneKey_Icon_6 = R.mipmap.luancher_news;

	public static final String OneKey_Name_7 = "921评书";
	public static final String OneKey_Content_7 = "921评书";
	public static final int OneKey_Icon_7 = R.mipmap.luancher_joke;

	public static final String OneKey_Name_8 = "音乐之声";
	public static final String OneKey_Content_8 = "音乐之声";
	public static final int OneKey_Icon_8 = R.mipmap.luancher_music;

	public static final String OneKey_Name_9 = "新闻大视野";
	public static final String OneKey_Content_9 = "新闻大视野";
	public static final int OneKey_Icon_9 = R.mipmap.fenghuang;

	public static final String OneKey_Name_10 = "我的位置";
	public static final String OneKey_Content_10 = "我的位置";
	public static final int OneKey_Icon_10 = R.mipmap.mylocation;

	// Name/Sort/Icon/Package/MainClass
	public static final String LAUNCHER_SORT_0 = Const.LAUNCHER_App;
	public static final String LAUNCHER_PACK_0 = "";
	public static final String LAUNCHER_MAINCLASS_0 = "";
	public static final String LAUNCHER_NAME_0 = "全部应用";
	public static final int LAUNCHER_ICON_0 = R.mipmap.dock_allapp;

	public static final String LAUNCHER_NAME_1 = "天坦天气";
	public static final String LAUNCHER_SORT_1 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_1 = R.mipmap.luancher_weather;
	public static final String LAUNCHER_PACK_1 ="net.tatans.rhea.weather";
	public static final String LAUNCHER_MAINCLASS_1 = "net.tatans.rhea.weather.activity.WeatherActivity";

	public static final String LAUNCHER_NAME_2 = "简易设置";
	public static final String LAUNCHER_SORT_2 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_2 = R.mipmap.luancher_simple_setup;
	public static final String LAUNCHER_PACK_2 = "net.tatans.coeus.setting";
	public static final String LAUNCHER_MAINCLASS_2 = "net.tatans.coeus.setting.activity.MainActivity";

	public static final String LAUNCHER_SORT_3 = Const.LAUNCHER_App;
	public static final String LAUNCHER_PACK_3 = "";
	public static final String LAUNCHER_MAINCLASS_3 = "";
	public static final String LAUNCHER_NAME_3 = "全部应用";
	public static final int LAUNCHER_ICON_3 = R.mipmap.dock_allapp;

	public static final String LAUNCHER_NAME_4 = "天坦导航";
	public static final String LAUNCHER_SORT_4 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_4 = R.mipmap.icon_launcher;
	public static final String LAUNCHER_PACK_4 = "net.tatans.coeus.navigation";
	public static final String LAUNCHER_MAINCLASS_4 = "net.tatans.coeus.navigation.MainActivity";
	public static final String MyLocation_PACK = "net.tatans.coeus.navigation";
	public static final String MyLocation = "net.tatans.coeus.navigation.MyLocationActivity";

	public static final String LAUNCHER_NAME_5 = "天坦商店";
	public static final String LAUNCHER_SORT_5 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_5= R.mipmap.luancher_application_market;
	public static final String LAUNCHER_PACK_5 = "net.tatans.rhea.app";
	public static final String LAUNCHER_MAINCLASS_5 = "net.tatans.rhea.app.activities.AppMainActivity";

	public static final String LAUNCHER_NAME_6 = "使用帮助";
	public static final String LAUNCHER_SORT_6 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_6 = R.mipmap.luancher_help;
	public static final String LAUNCHER_PACK_6 = "net.tatans.coeus.course";
	public static final String LAUNCHER_MAINCLASS_6 = "net.tatans.coeus.course.activity.MainActivity";

	public static final String LAUNCHER_NAME_7 = "我的位置";
	public static final String LAUNCHER_SORT_7 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_7 = R.mipmap.mylocation;
	public static final String LAUNCHER_PACK_7 = "net.tatans.coeus.navigation";
	public static final String LAUNCHER_MAINCLASS_7 = "net.tatans.coeus.navigation.MyLocationActivity";

	public static final String LAUNCHER_NAME_8 = "微信";
	public static final String LAUNCHER_SORT_8 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_8 = R.mipmap.wechat;
	public static final String LAUNCHER_PACK_8 = "com.tencent.mm";
	public static final String LAUNCHER_MAINCLASS_8 = "com.tencent.mm.ui.LauncherUI";

	public static final String LAUNCHER_NAME_9 = "天坦客服";
	public static final String LAUNCHER_SORT_9 = Const.LAUNCHER_COMMUNICATE;
	public static final String LAUNCHER_PACK_9 = "";
	public static final String LAUNCHER_MAINCLASS_9 = "";
	public static final int LAUNCHER_ICON_9 = R.mipmap.launchar_linkman_1;

	public static final String LAUNCHER_NAME_10 = "天坦小说";
	public static final String LAUNCHER_SORT_10 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_10 = R.mipmap.ic_launchernovel;
	public static final String LAUNCHER_PACK_10 = "net.tatans.coeus.novel";
	public static final String LAUNCHER_MAINCLASS_10 = "net.tatans.coeus.novel.activities.MainActivity";

	public static final String LAUNCHER_NAME_11= "天坦新闻";
	public static final String LAUNCHER_SORT_11 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_11 = R.mipmap.luancher_news;
	public static final String LAUNCHER_PACK_11 ="net.tatans.rhea.player";
	public static final String LAUNCHER_MAINCLASS_11 = "net.tatans.rhea.player.activity.NewsActivity";

	public static final String LAUNCHER_NAME_12 = "天坦电台";
	public static final String LAUNCHER_SORT_12 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_12 = R.mipmap.luancher_radio;
	public static final String LAUNCHER_PACK_12 ="net.tatans.rhea.player";
	public static final String LAUNCHER_MAINCLASS_12 = "net.tatans.rhea.player.activity.RadioActivity";

	public static final String LAUNCHER_NAME_13 = "天坦短信";
	public static final String LAUNCHER_SORT_13 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_13 = R.mipmap.dock_sms;
	public static final String LAUNCHER_PACK_13 = "net.tatans.coeus.messages";
	public static final String LAUNCHER_MAINCLASS_13 = "net.tatans.coeus.messages.activity.StartActivity";

	public static final String LAUNCHER_NAME_14 = "添加";
	public static final String LAUNCHER_SORT_14 = Const.LAUNCHER_Empty;
	public static final int LAUNCHER_ICON_14 = R.mipmap.addtainjia;
	public static final String LAUNCHER_PACK_14 = "";
	public static final String LAUNCHER_MAINCLASS_14 = "";

	public static final String LAUNCHER_NAME_15 = "天坦倒计时";
	public static final String LAUNCHER_SORT_15 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_15 = R.mipmap.ic_launcher;
	public static final String LAUNCHER_PACK_15 = "net.tatans.rhea.countdowntimer";
	public static final String LAUNCHER_MAINCLASS_15 = "net.tatans.rhea.countdowntimer.MainActivity";

	public static final String LAUNCHER_NAME_DAIL = "拨号盘";
	public static final String LAUNCHER_SORT_DAIL = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_DAIL = R.mipmap.dock_dail;
	public static final String LAUNCHER_PACK_DAIL = "net.tatans.coeus.contacts";
	public static final String LAUNCHER_MAINCLASS_DAIL = "net.tatans.coeus.contacts.activity.DailpadActivity";

	public static final String LAUNCHER_NAME_Q = "通话记录";
	public static final String LAUNCHER_SORT_Q = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_Q = R.mipmap.luancher_q;
	public static final String LAUNCHER_PACK_Q = "net.tatans.coeus.contacts";
	public static final String LAUNCHER_MAINCLASS_Q = "net.tatans.coeus.contacts.activity.CallLogActivity";


	public static final String LAUNCHER_NAME_SH508 = "简易设置";
	public static final String LAUNCHER_SORT_SH508 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_SH508 = R.mipmap.luancher_simple_setup;
	public static final String LAUNCHER_PACK_SH508 = "net.tatans.coeus.setting";
	public static final String LAUNCHER_MAINCLASS_SH508 = "net.tatans.coeus.setting.activity.MainActivity";

	public static final String LAUNCHER_NAME_STCL = "简易设置";
	public static final String LAUNCHER_SORT_STCL = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_STCL = R.mipmap.luancher_simple_setup;
	public static final String LAUNCHER_PACK_STCL = "net.tatans.coeus.setting.tcl.platform";
	public static final String LAUNCHER_MAINCLASS_STCL = "net.tatans.coeus.setting.activity.MainActivity";

	public static final String LAUNCHER_NAME_H508 = "天坦商店";
	public static final String LAUNCHER_SORT_H508 = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_H508 = R.mipmap.luancher_application_market;
	public static final String LAUNCHER_PACK_H508 = "net.tatans.rhea.app";
	public static final String LAUNCHER_MAINCLASS_H508 = "net.tatans.rhea.app.activities.AppMainActivity";

	public static final String LAUNCHER_NAME_TCL = "天坦商店";
	public static final String LAUNCHER_SORT_TCL = Const.LAUNCHER_App;
	public static final int LAUNCHER_ICON_TCL = R.mipmap.luancher_application_market;
	public static final String LAUNCHER_PACK_TCL = "net.tatans.rhea.app.tcl.platform";
	public static final String LAUNCHER_MAINCLASS_TCL = "net.tatans.rhea.app.activities.AppMainActivity";


	public static final String NULL_TTS = "TTS引擎出现问题，请重启手机。";
	public static final String NULL_APP = "该应用还未安装";
	public static final String NULL_SIM = "sim卡不存在或sim卡暂时不可用！";
	public static final String NULL_APP_DOWN = "该应用还没有安装，将为您跳转到该应用下载界面";
	public static final String NULL_APP_NODOWN = "天坦商店还没有安装,无法下载该应用";
	public static final String NULL_NEW_APP = "天坦商店还没有安装或不是最新版,无法下载该应用";
	public static final String SOS_PHONE = "号码为空，将为您跳转到号码设置界面";
	public static final String SEETING_PACK = "net.tatans.coeus.setting";
	public static final String SEETING_CLASS = "net.tatans.coeus.setting.activity.EmergencyCallSettingActivity";
	public static final String SEETING_NAME = "简易设置";
	public static final String TATANS_APP_PACK = "net.tatans.rhea.app";
	public static final String TATANS_APP_CLASS = "net.tatans.rhea.app.activities.AppDetailsActivity";
	public static final String NOT_NETWORK = "当前网络未连接，请联网后再试！";
	public static final String INFORMATION_PACK = "net.tatans.coeus.information";
	public static final String INFORMATION_CLASS = "net.tatans.coeus.information.activities.InformationActivity";
	public static final String NOVEL_PACK = "net.tatans.coeus.novel";
	public static final String NOVEL_CLASS = "net.tatans.coeus.novel.activities.MainActivity";

	public static final String STATES_WIFI_CLASS = "net.tatans.coeus.setting.activity.WIFIActivity";
	public static final String STATES_MAIN_CLASS = "net.tatans.coeus.setting.activity.MainActivity";
	public static final String STATES_SEND_WIFI = "net.tatans.coeus.setting.receiver.flowOnOff";

	//H508的pack
//	public static final String SEETING_PACK = "net.tatans.coeus.setting.h508.platform";
	public static final String STATES_TCLSEETING_PACK = "net.tatans.coeus.setting.tcl.platform";
	public static final String STATES_TCLSEETING_MOB = "net.tatans.coeus.setting.activity.MoblieDataActivity";
	public static final String STATES_TCLAPP_PACK = "net.tatans.rhea.app.tcl.platform";
	public static final String STATES_NO_IN = "简易设置还没有安装无法对数据流量或WiFi进行操作，将为您跳转到该应用下载界面";

	//锁屏的包名
	public static final String LOCKSCREEN_PACK = "net.tatans.rhea.lockScreen";
	//锁屏的包名+服务类
	public static final String LOCKSCREEN_PACK_SERVICE = "net.tatans.rhea.lockScreen.service.LockAndScrService";


	public static final String STATES_CLOCK = "闹钟已开启,选中点击进入闹钟";
	public static final String STATES_ONCK = "，选中可以关闭数据流量";
	public static final String STATES_NETWORK_LINK = "无线网络已连接";
	public static final String STATES_NETWORK_NOLINK = "无线网络没有连接";
	public static final String STATES_NETWORK_UNKNOW = "当前网络未知";
	public static final String STATES_NETWORK_ONCK = "无线网络已关闭，选中打开WiFi并进入WiFi列表";
	public static final String STATES_2G_FLOW = "2g网络已连接";
	public static final String STATES_3G_FLOW = "3g网络已连接";
	public static final String STATES_4G_FLOW = "4g网络已连接";
	public static final String STATES_ON_FLOW = "数据流量已打开";
	public static final String STATES_OFF_FLOW_ONCK = "数据流量已关闭，选中可以打开数据流量";
	public static final String STATES_OFF_FLOW = "数据流量已关闭";
	public static final String STATES_ON_GPS = "gps已打开";
	public static final String STATES_OFF_GPS = "gps已关闭";
	public static final String STATES_NO_SIM = "无SIM卡";
	public static final String STATES_ON_FLY = "飞行模式已打开，选中可以跳转飞行模式设置";
	public static final String STATES_ON_BLUE = "蓝牙已打开";
	public static final String STATES_OFF_BLUE = "蓝牙已关闭";

	/**
	 * sms的uri
	 */
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	public static final int ONEKEY_DELETE_TIME = 10 * 1000;

	/**
	 * 天气相关
	 */
	public static final String[] weekDay = { "日", "一", "二", "三", "四", "五", "六" };
	public static final String XING_QI = "星期";
}
