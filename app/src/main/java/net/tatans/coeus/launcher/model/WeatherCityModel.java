package net.tatans.coeus.launcher.model;

import java.util.List;

/**
 * @author SiLiPing
 * Purpose:市
 * Create Time: 2016-1-13 上午8:50:37
 */
public class WeatherCityModel {
	private String name;
	private List<WeatherDistrictModel> districtList;
	
	public WeatherCityModel() {
		super();
	}

	public WeatherCityModel(String name, List<WeatherDistrictModel> districtList) {
		super();
		this.name = name;
		this.districtList = districtList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WeatherDistrictModel> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<WeatherDistrictModel> districtList) {
		this.districtList = districtList;
	}

	@Override
	public String toString() {
		return "CityModel [name=" + name + ", districtList=" + districtList
				+ "]";
	}
	
}
