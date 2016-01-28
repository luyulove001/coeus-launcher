package net.tatans.coeus.launcher.util;

import java.util.ArrayList;
import java.util.List;

import net.tatans.coeus.launcher.model.WeatherCityModel;
import net.tatans.coeus.launcher.model.WeatherDistrictModel;
import net.tatans.coeus.launcher.model.WeatherProvinceModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author SiLiPingon
 * Purpose:城市解析所需
 * Create Time: 2016-1-13 上午8:48:05
 */
public class WeatherXmlParser extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 */
	private List<WeatherProvinceModel> provinceList = new ArrayList<WeatherProvinceModel>();
	 	  
	public WeatherXmlParser() {
		
	}

	public List<WeatherProvinceModel> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	WeatherProvinceModel provinceModel = new WeatherProvinceModel();
	WeatherCityModel cityModel = new WeatherCityModel();
	WeatherDistrictModel districtModel = new WeatherDistrictModel();
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("province")) {
			provinceModel = new WeatherProvinceModel();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setCityList(new ArrayList<WeatherCityModel>());
		} else if (qName.equals("city")) {
			cityModel = new WeatherCityModel();
			cityModel.setName(attributes.getValue(0));
			cityModel.setDistrictList(new ArrayList<WeatherDistrictModel>());
		} else if (qName.equals("district")) {
			districtModel = new WeatherDistrictModel();
			districtModel.setName(attributes.getValue(0));
			districtModel.setZipcode(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("district")) {
			cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("city")) {
        	provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
        	provinceList.add(provinceModel);
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
