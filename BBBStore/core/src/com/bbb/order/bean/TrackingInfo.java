package com.bbb.order.bean;

import java.io.Serializable;
import java.util.Date;

import atg.repository.MutableRepositoryItem;

public class TrackingInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String TRACKING_NUM = "trackingNumber";
	private static final String CARRIER_CODE = "carrierCode";
	private static final String ACTUAL_SHIPDATE = "actualShipDate";
	private static final String TRACKING_URL = "trackingURL";
	private static final String SHIPMENT_QTY = "shipmentQty";
	
	private static final int PRIME = 31;
	
	private MutableRepositoryItem mRepositoryItem;
	
	public final MutableRepositoryItem getRepositoryItem() {
        return mRepositoryItem;
    }

    public final void setRepositoryItem(MutableRepositoryItem pRepositoryItem) {
        this.mRepositoryItem = pRepositoryItem;
    }

    public TrackingInfo(final MutableRepositoryItem pRepositoryItem) {
		mRepositoryItem = pRepositoryItem;
	}
	
	public String getTrackingNumber() {
		return (String)getPropertyValue(TRACKING_NUM);
	}

	public void setTrackingNumber(final String pTrackingNumber) {
		setPropertyValue(TRACKING_NUM, pTrackingNumber);
	}
	
	public Date getActualShipDate() {
		return (Date)getPropertyValue(ACTUAL_SHIPDATE);
	}

	public void setActualShipDate(final Date pActualShipDate) {
		setPropertyValue(ACTUAL_SHIPDATE, pActualShipDate);
	}
	
	public String getCarrierCode() {
		return (String)getPropertyValue(CARRIER_CODE);
	}

	public void setCarrierCode(final String pCarrierCode) {
		setPropertyValue(CARRIER_CODE, pCarrierCode);
	}
	
	public Object getPropertyValue(final String property) {
		return mRepositoryItem.getPropertyValue(property);
	}
	
	
	private void setPropertyValue(final String property, Object value) {
		mRepositoryItem.setPropertyValue(property, value);
	}
	
	public String getTrackingUrl() {
		return (String)getPropertyValue(TRACKING_URL);
	}

	public void setTrackingUrl(String pTrackingUrl) {
		setPropertyValue(TRACKING_URL, pTrackingUrl);
	}
	
	public long getShipmentQty() {
		Integer qty = ((Integer) getPropertyValue(SHIPMENT_QTY));
		return ((qty == null) ? 0 : qty.intValue());
	}

	public void setShipmentQty(int pShipmentQty) {
		setPropertyValue(SHIPMENT_QTY, pShipmentQty);
	}

	@Override
	public int hashCode() {			
		int result = 1;
		result = PRIME * result
				+ ((getCarrierCode() == null) ? 0 : getCarrierCode().hashCode());
		result = PRIME * result
				+ ((getTrackingNumber() == null) ? 0 : getTrackingNumber().hashCode());
		result = PRIME * result
				+ ((getActualShipDate() == null) ? 0 : getActualShipDate().hashCode());
		result = PRIME * result
				+ ((getTrackingUrl() == null) ? 0 : getTrackingUrl().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TrackingInfo other = (TrackingInfo) obj;
		if (getCarrierCode() == null) {
			if (other.getCarrierCode() != null) {
				return false;
			}
		} else if (!getCarrierCode().equals(other.getCarrierCode())) {
			return false;
		}
		if (getTrackingNumber() == null) {
			if (other.getTrackingNumber() != null) {
				return false;
			}
		} else if (!getTrackingNumber().equals(other.getTrackingNumber())) {
			return false;
		}
		if (getActualShipDate() == null) {
			if (other.getActualShipDate() != null) {
				return false;
			}
		} else if (!getActualShipDate().equals(other.getActualShipDate())) {
			return false;
		}
		if (getTrackingUrl() == null) {
			if (other.getTrackingUrl() != null) {
				return false;
			}
		} else if (!getTrackingUrl().equals(other.getTrackingUrl())) {
			return false;
		}
		return true;
	}
	
}
