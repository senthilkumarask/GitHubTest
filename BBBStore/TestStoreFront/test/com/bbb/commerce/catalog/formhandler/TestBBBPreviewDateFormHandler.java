package com.bbb.commerce.catalog.formhandler;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bbb.commerce.catalog.PreviewAttributes;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBPreviewDateFormHandler extends BaseTestCase {
	public void testBBBPreviewDateFormHandler() throws Exception {
		BBBPreviewDateFormHandler previewDateFormHandler = (BBBPreviewDateFormHandler) getObject("bbbPreviewDateFormHandler");
		String pDate = getObject("date").toString();
		String pTime = getObject("time").toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		previewDateFormHandler.setDate(pDate);
		previewDateFormHandler.setTime(pTime);
		Date date = dateFormat.parse(pDate + " " + pTime);
		previewDateFormHandler.handleSubmit(getRequest(), getResponse());
		PreviewAttributes previewAttributes = (PreviewAttributes) getRequest().resolveName("/com/bbb/commerce/catalog/PreviewAttributes");
		Date previewDate = previewAttributes.getPreviewDate();
		boolean error = previewDateFormHandler.getFormError();
		addObjectToAssert("testValue", error);
		assertEquals(date, previewDate);
	}
}
