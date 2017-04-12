package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class OmnitureReportDataVO implements Serializable, Comparable<OmnitureReportDataVO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1879067108459527570L;
	private String name;
	private String url;
	private List<OmnitureReportDataPathVO> path;
	private String parentID;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private double trend;
	
	private double[] counts;
	private double[] upperBounds;
	private double[] lowerBounds;
	private double[] forecasts;
	private double[] breakdownTotal;
	private List<OmnitureReportDataVO> breakdown;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<OmnitureReportDataPathVO> getPath() {
		return path;
	}
	public void setPath(List<OmnitureReportDataPathVO> path) {
		this.path = path;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public double getTrend() {
		return trend;
	}
	public void setTrend(double trend) {
		this.trend = trend;
	}
	public double[] getCounts() {
		return counts;
	}
	public void setCounts(double[] counts) {
		this.counts = counts;
	}
	public double[] getUpperBounds() {
		return upperBounds;
	}
	public void setUpperBounds(double[] upperBounds) {
		this.upperBounds = upperBounds;
	}
	public double[] getLowerBounds() {
		return lowerBounds;
	}
	public void setLowerBounds(double[] lowerBounds) {
		this.lowerBounds = lowerBounds;
	}
	public double[] getForecasts() {
		return forecasts;
	}
	public void setForecasts(double[] forecasts) {
		this.forecasts = forecasts;
	}
	public double[] getBreakdownTotal() {
		return breakdownTotal;
	}
	public void setBreakdownTotal(double[] breakdownTotal) {
		this.breakdownTotal = breakdownTotal;
	}
	public List<OmnitureReportDataVO> getBreakdown() {
		return breakdown;
	}
	public void setBreakdown(List<OmnitureReportDataVO> breakdown) {
		this.breakdown = breakdown;
	}
	@Override
	public String toString() {
		return "OmnitureReportDataVO [name=" + name + ", url=" + url
				+ ", path=" + path + ", parentID=" + parentID + ", year="
				+ year + ", month=" + month + ", day=" + day + ", hour=" + hour
				+ ", minute=" + minute + ", trend=" + trend + ", counts="
				+ Arrays.toString(counts) + ", upperBounds="
				+ Arrays.toString(upperBounds) + ", lowerBounds="
				+ Arrays.toString(lowerBounds) + ", forecasts="
				+ Arrays.toString(forecasts) + ", breakdownTotal="
				+ Arrays.toString(breakdownTotal) + ", breakdown=" + breakdown
				+ "]";
	}
	
	
	public int compareTo(OmnitureReportDataVO omnitureReportVO) {
		return ((Double) omnitureReportVO.getCounts()[0]).compareTo((Double)this.getCounts()[0]);
	}

}
