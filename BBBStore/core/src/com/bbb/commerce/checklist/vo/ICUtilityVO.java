package com.bbb.commerce.checklist.vo;


public class ICUtilityVO {
	
	String registryTypeDescription;

	public String getRegistryTypeDescription() {
		return registryTypeDescription;
	}

	public void setRegistryTypeDescription(String registryTypeDescription) {
		this.registryTypeDescription = registryTypeDescription;
	}

	private String c1Category;
	private String c2Category;
	private String c3Category;

	private String c1ShowOnChecklist;
	private String c2ShowOnChecklist;
	private String c3ShowOnChecklist;

	public boolean isC1Disabled() {
		return c1Disabled;
	}

	public void setC1Disabled(boolean c1Disabled) {
		this.c1Disabled = c1Disabled;
	}

	public boolean isC2Disabled() {
		return c2Disabled;
	}

	public void setC2Disabled(boolean c2Disabled) {
		this.c2Disabled = c2Disabled;
	}

	public boolean isC3Disabled() {
		return c3Disabled;
	}

	public void setC3Disabled(boolean c3Disabled) {
		this.c3Disabled = c3Disabled;
	}

	public boolean isC1Deleted() {
		return c1Deleted;
	}

	public void setC1Deleted(boolean c1Deleted) {
		this.c1Deleted = c1Deleted;
	}

	public boolean isC2Deleted() {
		return c2Deleted;
	}

	public void setC2Deleted(boolean c2Deleted) {
		this.c2Deleted = c2Deleted;
	}

	public boolean isC3Deleted() {
		return c3Deleted;
	}

	public void setC3Deleted(boolean c3Deleted) {
		this.c3Deleted = c3Deleted;
	}

	private boolean c1Disabled;
	private boolean c2Disabled;
	private boolean c3Disabled;
	
	private boolean c1Deleted;
	private boolean c2Deleted;
	private boolean c3Deleted;

	public String getC1ShowOnChecklist() {
		return c1ShowOnChecklist;
	}

	public void setC1ShowOnChecklist(String c1ShowOnChecklist) {
		this.c1ShowOnChecklist = c1ShowOnChecklist;
	}

	public String getC2ShowOnChecklist() {
		return c2ShowOnChecklist;
	}

	public void setC2ShowOnChecklist(String c2ShowOnChecklist) {
		this.c2ShowOnChecklist = c2ShowOnChecklist;
	}

	public String getC3ShowOnChecklist() {
		return c3ShowOnChecklist;
	}

	public void setC3ShowOnChecklist(String c3ShowOnChecklist) {
		this.c3ShowOnChecklist = c3ShowOnChecklist;
	}

	public String getC1Category() {
		return c1Category;
	}

	public void setC1Category(String c1Category) {
		this.c1Category = c1Category;
	}

	public String getC2Category() {
		return c2Category;
	}

	public void setC2Category(String c2Category) {
		this.c2Category = c2Category;
	}

	public String getC3Category() {
		return c3Category;
	}

	public void setC3Category(String c3Category) {
		this.c3Category = c3Category;
	}

}
