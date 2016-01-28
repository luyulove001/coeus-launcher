package net.tatans.coeus.launcher.model;

import java.util.List;

/**
 * @author SiLiPing
 * Purpose:省
 * Create Time: 2016-1-13 上午8:50:37
 */
public class WeatherProvinceModel {
	private String name;
	private List<WeatherCityModel> cityList;
	
	public WeatherProvinceModel() {
		super();
	}

	public WeatherProvinceModel(String name, List<WeatherCityModel> cityList) {
		super();
		this.name = name;
		this.cityList = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WeatherCityModel> getCityList() {
		return cityList;
	}

	public void setCityList(List<WeatherCityModel> cityList) {
		this.cityList = cityList;
	}

	@Override
	public String toString() {
		return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
	}
	
}
