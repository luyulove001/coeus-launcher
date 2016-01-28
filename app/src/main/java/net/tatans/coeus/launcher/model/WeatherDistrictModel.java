package net.tatans.coeus.launcher.model;

/**
 * @author SiLiPing
 * Purpose:区
 * Create Time: 2016-1-13 上午8:50:37
 */
public class WeatherDistrictModel {
	private String name;
	private String zipcode;
	
	public WeatherDistrictModel() {
		super();
	}

	public WeatherDistrictModel(String name, String zipcode) {
		super();
		this.name = name;
		this.zipcode = zipcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Override
	public String toString() {
		return "DistrictModel [name=" + name + ", zipcode=" + zipcode + "]";
	}

}
