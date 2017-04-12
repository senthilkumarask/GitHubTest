package com.bbb.framework;


import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.framework.performance.BBBPerformanceMonitor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Nucleus.class, BBBPerformanceMonitor.class})
public class BaseTestCase extends TestCase {
	
	private Nucleus nucleus;
	@Spy DynamoHttpServletRequest mockDynamoHttpServletRequest = new DynamoHttpServletRequest();
	@Spy DynamoHttpServletResponse mockDynamoHttpServletResponse =  new DynamoHttpServletResponse();
	@Mock HttpServletResponse mockHttpResponse;
	@Mock HttpServletRequest mockHttpRequest;
	
	@Override
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		mockNucleus();
		mockBBBPerformanceMonitor();
		prepareServletUtil();
		prepareDynRequestResponse();	
	}

	public void prepareDynRequestResponse(){
//		when(mockDynamoHttpServletResponse.getResponse()).thenReturn(mockHttpResponse);
//		when(mockDynamoHttpServletRequest.getRequest()).thenReturn(mockHttpRequest);	
		mockDynamoHttpServletResponse.setResponse(mockHttpResponse);
		mockDynamoHttpServletRequest.setRequest(mockHttpRequest);
	}
	public DynamoHttpServletRequest getDynHttpRequest() {
		return mockDynamoHttpServletRequest;
	}

	public void setDynHttpRequest(DynamoHttpServletRequest dynHttpRequest) {
		this.mockDynamoHttpServletRequest = dynHttpRequest;
	}

	public DynamoHttpServletResponse getDynHttpResponse() {
		return mockDynamoHttpServletResponse;
	}

	public void setDynHttpResponse(DynamoHttpServletResponse dynHttpResponse) {
		this.mockDynamoHttpServletResponse = dynHttpResponse;
	}
	
	@Test
	public void test1(){
		
	}
	public Nucleus getNucleus() {
		return nucleus;
	}

	public void setNucleus(Nucleus nucleus) {
		this.nucleus = nucleus;
	}
	
	public void mockNucleus(){
		
		PowerMockito.mockStatic(Nucleus.class);
		Nucleus nucleus=mock(Nucleus.class);
		when(Nucleus.getGlobalNucleus()).thenReturn(nucleus);
	}
	public void mockBBBPerformanceMonitor(){
		BBBPerformanceMonitor mockPerfMonitor=mock(BBBPerformanceMonitor.class);
		PowerMockito.mockStatic(BBBPerformanceMonitor.class);
		doReturn(null).when(getDynHttpRequest()).
			resolveName("/com/bbb/framework/performance/logger/PerformanceLogger");
	}
	public void prepareServletUtil(){
		ServletUtil.setCurrentRequest(getDynHttpRequest());
//		ServletUtil.getCurrentRequest();
	}
	
	

}

