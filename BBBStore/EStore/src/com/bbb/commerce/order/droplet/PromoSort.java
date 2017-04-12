package com.bbb.commerce.order.droplet;


import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Map;

import com.bbb.constants.BBBCoreConstants;

import atg.repository.RepositoryItem;

class PromoSort implements Comparator<RepositoryItem> {
 
    Map<String, RepositoryItem> map;
 
    public PromoSort(Map<String, RepositoryItem> base) {
        this.map = base;
    }
 
    public int compare(RepositoryItem obj1, RepositoryItem obj2) {
    	Timestamp endUsableDateObj1 = (Timestamp)obj1.getPropertyValue(BBBCoreConstants.END_USABLE);
    	Timestamp endUsableDateObj2 = (Timestamp)obj2.getPropertyValue(BBBCoreConstants.END_USABLE);
    	long l1 = endUsableDateObj1.getTime();
        long l2 = endUsableDateObj2.getTime();
        if (l2 > l1)
        return 1;
        else if (l1 > l2)
        return -1;
        else
        return 0;
    }
}