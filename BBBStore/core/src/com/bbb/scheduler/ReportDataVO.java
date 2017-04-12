package com.bbb.scheduler;

public class ReportDataVO {

	String lastWeekDate;
	String todayDate;
	String totalCountLastWeek;
	String totalCountToday;
	String difference;
	float percentDifference=0f;

	public String getLastWeekDate() {
		return lastWeekDate;
	}

	public void setLastWeekDate(String lastWeekDate) {
		this.lastWeekDate = lastWeekDate;
	}

	public String getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(String todayDate) {
		this.todayDate = todayDate;
	}

	public String getTotalCountLastWeek() {
		return totalCountLastWeek;
	}

	public void setTotalCountLastWeek(String totalCountLastWeek) {
		this.totalCountLastWeek = totalCountLastWeek;
	}

	public String getTotalCountToday() {
		return totalCountToday;
	}

	public void setTotalCountToday(String totalCountToday) {
		this.totalCountToday = totalCountToday;
	}

	public int getDifference() {
		int cntDifference = Integer.parseInt(getTotalCountToday())
				- Integer.parseInt(getTotalCountLastWeek());
		return cntDifference;
	}

	public void setDifference(String difference) {
		this.difference = difference;
	}

	public float getPercentDifference() {
		float cntDifference = 0.0f;
		if(Integer.parseInt(getTotalCountLastWeek())!=0 && getDifference()!=0){
			cntDifference=((float)getDifference()/(float)Integer.parseInt(getTotalCountLastWeek()))*100;
		}
		return cntDifference;
	}

	public void setPercentDifference(float percentDifference) {
		this.percentDifference = percentDifference;
	}

}
