package com.bbb.simplifyRegistry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RegistryInputVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String fieldName;
	private boolean displayOnForm;
	private boolean requiredInputCreate;
	private boolean requiredInputUpdate;
	private boolean requiredToMakeRegPublic;
	
	public boolean isRequiredInputCreate() {
		return requiredInputCreate;
	}
	public void setRequiredInputCreate(boolean requiredInputCreate) {
		this.requiredInputCreate = requiredInputCreate;
	}
	public boolean isRequiredInputUpdate() {
		return requiredInputUpdate;
	}
	public void setRequiredInputUpdate(boolean requiredInputUpdate) {
		this.requiredInputUpdate = requiredInputUpdate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public boolean isDisplayOnForm() {
		return displayOnForm;
	}
	public void setDisplayOnForm(boolean displayOnForm) {
		this.displayOnForm = displayOnForm;
	}
	public boolean isRequiredToMakeRegPublic() {
		return requiredToMakeRegPublic;
	}
	public void setRequiredToMakeRegPublic(boolean requiredToMakeRegPublic) {
		this.requiredToMakeRegPublic = requiredToMakeRegPublic;
	}


}