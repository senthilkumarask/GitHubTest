package com.bbb.commerce.giftregistry.droplet;

import java.util.Comparator;
import java.util.StringTokenizer;

import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;

/**
 * Registry event date comparator
 * @author sku134
 *
 */
public class RegistryEventDateComparator implements Comparator {

	public int sortOrder = 0;

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int compare(Object o1, Object o2) {
		RegistrySkinnyVO registrySkinnyVO1 = (RegistrySkinnyVO) o1;
		RegistrySkinnyVO registrySkinnyVO2 = (RegistrySkinnyVO) o2;

		int result = 0;
		
		String var1 = "";
		String var2 = "";
		switch (sortOrder) {

		case 1: {
			var1 = getDateIntforFM(registrySkinnyVO1.getEventDate());
			var2 = getDateIntforFM(registrySkinnyVO2.getEventDate());
			if (var1 == null)
				var1 = "";
			if (var2 == null)
				var2 = "";
			result =  var1.compareToIgnoreCase(var2);
			break;
			
		}
		case 2: {
			var1 = getDateIntforFM(registrySkinnyVO1.getEventDate());
			var2 = getDateIntforFM(registrySkinnyVO2.getEventDate());
			if (var1 == null)
				var1 = "";
			if (var2 == null)
				var2 = "";
			result = -var1.compareToIgnoreCase(var2);
			break;
		}
		
		default:
			break;
		}
		
		return result;
	}


public String getDateIntforFM(String date) {
	if (date == null)
		return null;
	StringTokenizer st = new StringTokenizer(date, "/-");
	String scatteredDate[] = new String[3];
	for (int i = 0; i < 3; i++) {
		scatteredDate[i] = st.nextToken();
	}
	String integer = scatteredDate[2]+scatteredDate[1]+scatteredDate[0];
	return integer;

}
}