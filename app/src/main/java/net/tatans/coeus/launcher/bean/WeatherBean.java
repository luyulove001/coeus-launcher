package net.tatans.coeus.launcher.bean;

public class WeatherBean {	
	private String cityName;
	private String wendu;
	private String[] high;
	private String[] low;
	private String[] type_day;
	private String[] type_night;
	private String[] wind_x_day;
	private String[] wind_x_night;
	private String[] wind_l_day;
	private String[] wind_l_night;
	private String clothes;
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getWendu() {
		return wendu;
	}
	public void setWendu(String wendu) {
		this.wendu = wendu;
	}
	public String[] getHigh() {
		return high.clone();
	}
	public void setHigh(String[] high) {
		this.high = (String[])high.clone();
	}
	public String[] getLow() {
		return low.clone();
	}
	public void setLow(String[] low) {
		this.low = (String[])low.clone();
	}
	public String[] getType_day() {
		return type_day.clone();
	}
	public void setType_day(String[] type_day) {
		this.type_day = (String[])type_day.clone();
	}
	public String[] getType_night() {
		return type_night.clone();
	}
	public void setType_night(String[] type_night) {
		this.type_night = (String[])type_night.clone();
	}
	public String[] getWind_x_day() {
		return wind_x_day.clone();
	}
	public void setWind_x_day(String[] wind_x_day) {
		this.wind_x_day = (String[])wind_x_day.clone();
	}
	public String[] getWind_x_night() {
		return wind_x_night.clone();
	}
	public void setWind_x_night(String[] wind_x_night) {
		this.wind_x_night = (String[])wind_x_night.clone();
	}
	public String[] getWind_l_day() {
		return wind_l_day.clone();
	}
	public void setWind_l_day(String[] wind_l_day) {
		this.wind_l_day = (String[])wind_l_day.clone();
	}
	public String[] getWind_l_night() {
		return wind_l_night.clone();
	}
	public void setWind_l_night(String[] wind_l_night) {
		this.wind_l_night = (String[])wind_l_night.clone();
	}
	public String getClothes() {
		return clothes;
	}
	public void setClothes(String clothes) {
		this.clothes = clothes;
	}
	public String speachWeatherInfo() {
		//今天天气
		String type_today;
		if(type_day[0].equals(type_night[0])){
			type_today = type_day[0];
		}else{
			type_today = type_day[0]+"转"+type_night[0];
		}
		//明天天气
		String type_tomorrow;
		if(type_day[1].equals(type_night[1])){
			type_tomorrow = type_day[1];
		}else{
			type_tomorrow = type_day[1]+"转"+type_night[1];
		}
		//今天温度
		String temp_today;
		temp_today = low[0].substring(2)+"~"+high[0].substring(2);
		//明天温度
		String temp_tomorrow;
		temp_tomorrow = low[1].substring(2)+"~"+high[1].substring(2);
		//风向及风力
		String wind_day = wind_x_day[0].endsWith("无持续风向")?"":(wind_x_day[0]+(wind_l_day[0].equals("微风级")?"小于三级":wind_l_day[0]));
		String wind_night = wind_x_night[0].endsWith("无持续风向")?"":(wind_x_night[0]+(wind_l_night[0].equals("微风级")?"小于三级":wind_l_night[0]));
		String wind = wind_day.equals(wind_night)?wind_day:!wind_day.equals("")&&!wind_night.equals("")?wind_day+"转"+wind_night:wind_day+wind_night;
		return cityName+",当前温度"+wendu+",今天白天到夜间:"+type_today+","+temp_today+","+wind+","+clothes+"明天天气:"+type_tomorrow+","+temp_tomorrow;
	}	
}
