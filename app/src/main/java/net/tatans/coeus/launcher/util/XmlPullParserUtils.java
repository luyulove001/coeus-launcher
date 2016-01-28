package net.tatans.coeus.launcher.util;

import java.io.InputStream;
import java.util.ArrayList;

import net.tatans.coeus.launcher.bean.RadioBean;
import net.tatans.coeus.network.callback.HttpRequestCallBack;
import net.tatans.coeus.network.tools.TatansHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

/**
 * 使用Pull解析XML
 * 
 * @author luojianqin
 * 
 * 解析的数据来源于channel.xml
 * 
 */
public class XmlPullParserUtils {
	public static ArrayList<RadioBean> localRadioList = new ArrayList<RadioBean>();
	private static String TAG = "XmlPullParserUtils";

	/**Purpose:
	 * @author luojianqin
	
	 * Create Time: 2015-4-23 上午9:33:14
	
	 * @param is
	 *         传入的文件流，读取XML文件的流
	 * @param ChannelDataList
	 *         电台列表
	 * @return
	 * @throws Exception
	 */
	public static boolean getRadioListData(InputStream is,
			ArrayList<RadioBean> ChannelDataList)
			throws Exception {
		// 临时电台数据对象
		RadioBean tempChannelItem = null;
//		// 保存每个频道类型里面具体电台数据
//		ArrayList<RadioChannelData> channelDataList = null;
		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser parser = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				// 处理文档开始
				break;
			case XmlPullParser.START_TAG:
				 if (parser.getName().equals("RadioChannel")) {
					// 具体电台信息
					tempChannelItem = new RadioBean();
					tempChannelItem.setChannelID(parser.getAttributeValue(1));
					tempChannelItem.setChannelName(parser.getAttributeValue(0));
					tempChannelItem.setChannelICON(parser.getAttributeValue(2));
					tempChannelItem.setLevel(Integer.valueOf(parser
							.getAttributeValue(3)));
					tempChannelItem.setChannelURL(parser.getAttributeValue(4));
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("RadioChannel")) {
					ChannelDataList.add(tempChannelItem);
					tempChannelItem = null;
				}
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			}
			eventType = parser.next();
		}
		return true;
	}
	/**
	 * 根据城市地址获得本地电台列表
	 * 
	 * @param address 
	 *            城市地址
	 * @return 本地电台列表
	 * @author luojianqin
	 */
	public static ArrayList<RadioBean> getRadioListByProvince(String address) {
		String URL = null;
		localRadioList.clear();
		if (address.contains("北京")) {
			URL = Const.RADIO_URL[0];
		}
		if (address.contains("上海")) {
			URL = Const.RADIO_URL[1];
		}
		if (address.contains("天津")) {
			URL = Const.RADIO_URL[2];
		}
		if (address.contains("重庆")) {
			URL = Const.RADIO_URL[3];
		}
		if (address.contains("广东")) {
			URL = Const.RADIO_URL[4];
		}
		if (address.contains("浙江")) {
			URL = Const.RADIO_URL[5];
		}
		if (address.contains("江苏")) {
			URL = Const.RADIO_URL[6];
		}
		if (address.contains("湖南")) {
			URL = Const.RADIO_URL[7];
		}
		if (address.contains("四川")) {
			URL = Const.RADIO_URL[8];
		}
		if (address.contains("山西")) {
			URL = Const.RADIO_URL[9];
		}
		if (address.contains("河南")) {
			URL = Const.RADIO_URL[10];
		}
		if (address.contains("湖北")) {
			URL = Const.RADIO_URL[11];
		}
		if (address.contains("黑龙江")) {
			URL = Const.RADIO_URL[12];
		}
		if (address.contains("辽宁")) {
			URL = Const.RADIO_URL[13];
		}
		if (address.contains("河北")) {
			URL = Const.RADIO_URL[14];
		}
		if (address.contains("山东")) {
			URL = Const.RADIO_URL[15];
		}
		if (address.contains("安徽")) {
			URL = Const.RADIO_URL[16];
		}
		if (address.contains("福建")) {
			URL = Const.RADIO_URL[17];
		}
		if (address.contains("广西")) {
			URL = Const.RADIO_URL[18];
		}
		if (address.contains("贵州")) {
			URL = Const.RADIO_URL[19];
		}
		if (address.contains("云南")) {
			URL = Const.RADIO_URL[20];
		}
		if (address.contains("江西")) {
			URL = Const.RADIO_URL[21];
		}
		if (address.contains("吉林")) {
			URL = Const.RADIO_URL[22];
		}
		if (address.contains("甘肃")) {
			URL = Const.RADIO_URL[23];
		}
		if (address.contains("陕西")) {
			URL = Const.RADIO_URL[24];
		}
		if (address.contains("宁夏")) {
			URL = Const.RADIO_URL[25];
		}
		if (address.contains("内蒙古")) {
			URL = Const.RADIO_URL[26];
		}
		if (address.contains("海南")) {
			URL = Const.RADIO_URL[27];
		}
		if (address.contains("西藏")) {
			URL = Const.RADIO_URL[28];
		}
		if (address.contains("青海")) {
			URL = Const.RADIO_URL[29];
		}
		if (address.contains("新疆")) {
			URL = Const.RADIO_URL[30];
		}
		TatansHttp http = new TatansHttp();
		http.configTimeout(10000);
		http.post(URL, new HttpRequestCallBack<String>() {

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				Log.e(TAG, "网络请求失败，获取本地电台失败");
			}

			@Override
			public void onSuccess(String jString) {
				super.onSuccess(jString);
				try {
					JSONObject object = new JSONObject((String) jString);
					JSONArray array = object.getJSONArray("data");
					if (array.length() == 0) {
						Log.e(TAG, "该城市没有本地电台");
						return;
					}
					for (int i = 0; i < array.length(); i++) {
						RadioBean radioDto = new RadioBean();
						JSONObject obj = array.getJSONObject(i);
						String radioId = obj.getString("mediainfo");
						JSONObject object2 = new JSONObject((String) radioId);
						String mediainfoId = object2.getString("id");
						String radioTitle = obj.getString("title");
						radioDto.setChannelID(mediainfoId);
						radioDto.setChannelName(radioTitle);
						radioDto.setChannelURL("http://hls.qingting.fm/live/"
								+ mediainfoId + ".m3u8?bitrate=64");
						localRadioList.add(radioDto);
						Log.v("localRadio","mediainfoId" + i+":"+localRadioList.get(i).getChannelID());
						Log.v("localRadio","radio_title" + i+":"+localRadioList.get(i).getChannelName());
						Log.v("localRadio","radio_url" + i+":"+localRadioList.get(i).getChannelURL());
					}
					if(localRadioList.size()!=0){
						RadioLauncherTouch.ChannelDataList.clear();
						RadioLauncherTouch.pullXml();
						RadioLauncherTouch.ChannelDataList.addAll(localRadioList);
					}
					Log.e("localRadio", " ChannelDataList.size:"+RadioLauncherTouch.ChannelDataList.size());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
		return localRadioList;
	}
}


