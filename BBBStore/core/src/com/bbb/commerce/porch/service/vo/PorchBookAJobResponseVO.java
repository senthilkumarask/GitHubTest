package com.bbb.commerce.porch.service.vo;

import java.util.ArrayList;
import java.util.List;

import atg.repository.loader.Job;

public class PorchBookAJobResponseVO {

	private int porchUserId;
	private List<PorchJobVO> jobs = new ArrayList<PorchJobVO>();

	/**
	*
	* @return
	* The porchUserId
	*/
	public int getPorchUserId() {
	return porchUserId;
	}
 
	/**
	*
	* @param porchUserId
	* The porchUserId
	*/
	public void setPorchUserId(int porchUserId) {
	this.porchUserId = porchUserId;
	}

	/**
	*
	* @return
	* The jobs
	*/
	public List<PorchJobVO> getJobs() {
	return jobs;
	}

	/**
	*
	* @param jobs
	* The jobs
	*/
	public void setJobs(List<PorchJobVO> jobs) {
	this.jobs = jobs;
	}	

}
