<dsp:page>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><dsp:valueof param="emailTemplateVO.siteId"
		valueishtml="true" /></title>
</head>
<body bgcolor="#ffffff">
	<!-- If you see this message, please enable HTML e-mail -->

	<%-- Header Section Starts--%>
	<dsp:valueof param="emailHeader" valueishtml="true" />
	<%-- Header Section Ends--%>
	<%-- Main Content --%>
	<c:set var="imagePath">
	http:<bbbc:config key="image_host_gs" configName="ThirdPartyURLs" />
	</c:set>
	<tr>
		<td class="main"
			style="border-top: 3px solid #F5F5F5; border-bottom: 3px solid #F5F5F5; padding-top: 10px; padding-bottom: 10px; margin-bottom: 20px;">
			<table class="content" width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="100%" style="padding: 5px;" align="left">
						<h3 style="font-size: 20px; margin-bottom: 5px">
							<bbbl:label key='lbl_gs_checkList_heading'
								language="${pageContext.request.locale.language}" />
						</h3></td>
				</tr>
				<tr>
					<td width="100%" valign="top">
						<div class="section-wrapper" width="100%" border="0">
							<table class="item-container" cellpadding="0" cellspacing="0" border="0" width="100%">
	                            <tr>
	                                <td width="100%" style="vertical-align: top">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array"
												param="emailTemplateVO.tableCheckListMapOuter" />
											<dsp:param name="elementName" value="tableCheckListMapInner" />
											<dsp:oparam name="outputStart">
											<table class="my-table-item" width="100%" align="left" border="0" cellpadding="0" cellspacing="0" style="font-family:Futura, Trebuchet MS, Arial, sans-serif; border-collapse:collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;">
											<tr>
											</dsp:oparam>
											<dsp:oparam name="output">
												<dsp:getvalueof var="outerMapKey" param="key" />
												<dsp:getvalueof var="count" param="count" />
												<td width="50%" valign="top">
														<h3
															style="font-size: 14px; margin-bottom: 5px; color: #33B5F8;">
															<dsp:valueof param="key" valueishtml="true" />
														</h3>
														<%-- <dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array"
																param="emailTemplateVO.primaryCategoryCountMap" />
															<dsp:param name="elementName" value="count" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="countKey" param="key" />
																<c:choose>
																	<c:when test="${countKey eq outerMapKey}">
																		<h5
																			style="font-size: 11px; margin-bottom: 5px; color: #CCCCCC;">
																			<dsp:valueof param="count" valueishtml="true" />
																			each
																		</h5>
																	</c:when>
																</c:choose>
															</dsp:oparam>
														</dsp:droplet> --%>
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="tableCheckListMapInner" />
															<dsp:param name="elementName" value="tableCheckListArray" />
															<dsp:oparam name="output">
																<table border="0" cellpadding="5" cellspacing="0">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param name="array" param="tableCheckListArray" />
																		<dsp:param name="elementName" value="tableCheckListVO" />
																		<dsp:oparam name="output">
																			<tr>
																				<td style="width: 14px;"><dsp:getvalueof
																						var="isPresent" param="tableCheckListVO.isPresent" />
																					<c:if test="${isPresent eq 'true'}">
																						<img
																							src="${imagePath}/_assets/emailtemplates/images/check.gif">
																					</c:if>
																				</td>
																				<td style="font-size: 12px; color: #33B5F8"><dsp:valueof
																						param="tableCheckListVO.categoryName"
																						valueishtml="true" /></td>
																			</tr>
																		</dsp:oparam>
																	</dsp:droplet>

																</table>
															</dsp:oparam>
														</dsp:droplet>
												</td>
												<c:if test="${count % 2 == 0}">
													</tr>
												</table>
												<table class="my-table-item" width="100%" align="left" border="0" cellpadding="0" cellspacing="0" style="font-family:Futura, Trebuchet MS, Arial, sans-serif; border-collapse:collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;">
													<tr>
												</c:if>
											</dsp:oparam>
											<dsp:oparam name="outputEnd">
												</tr>
											</table>
											</dsp:oparam>
										</dsp:droplet>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<dsp:valueof param="emailTemplateVO.emailFooter" valueishtml="true" />

</body>
	</html>
</dsp:page>