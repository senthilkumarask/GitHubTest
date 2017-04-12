package com.bbb.certona.vo;

import java.io.Serializable;

public class Prop65TypeVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean prop65Lighting;
	private boolean prop65Crystal;
	private boolean prop65Dinnerware;
	private boolean prop65Other;
	/**
	 * @return the prop65Lighting
	 */
	public boolean isProp65Lighting() {
		return prop65Lighting;
	}
	/**
	 * @param pProp65Lighting the prop65Lighting to set
	 */
	public void setProp65Lighting(boolean pProp65Lighting) {
		prop65Lighting = pProp65Lighting;
	}
	/**
	 * @return the prop65Crystal
	 */
	public boolean isProp65Crystal() {
		return prop65Crystal;
	}
	/**
	 * @param pProp65Crystal the prop65Crystal to set
	 */
	public void setProp65Crystal(boolean pProp65Crystal) {
		prop65Crystal = pProp65Crystal;
	}
	/**
	 * @return the prop65Dinnerware
	 */
	public boolean isProp65Dinnerware() {
		return prop65Dinnerware;
	}
	/**
	 * @param pProp65Dinnerware the prop65Dinnerware to set
	 */
	public void setProp65Dinnerware(boolean pProp65Dinnerware) {
		prop65Dinnerware = pProp65Dinnerware;
	}
	/**
	 * @return the prop65Other
	 */
	public boolean isProp65Other() {
		return prop65Other;
	}
	/**
	 * @param pProp65Other the prop65Other to set
	 */
	public void setProp65Other(boolean pProp65Other) {
		prop65Other = pProp65Other;
	}

}
