<dsp:page>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><dsp:valueof param="emailTemplateVO.siteId"
		valueishtml="true" />
</title>
<meta name="viewport" content="width=device-width" />
</head>
<body bgcolor="#FFFFFF">
	<c:set var="imagePath">
	https:<bbbc:config key="image_host_gs" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
	https:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<dsp:getvalueof var="host" param="emailTemplateVO.hostUrl" />
	<c:set var="hostUrl">${host}/store</c:set>

	<style type="text/css">
/****** EMAIL CLIENT BUG FIXES - BEST NOT TO CHANGE THESE ********/
/* Forces Hotmail to display emails at full width. */
.ExternalClass {
	width: 100%;
}

.ReadMsgBody {
	width: 100%;
}

/* Forces Hotmail to display normal line spacing. */
.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div
	{
	line-height: 100%;
}
.disableText { color: #BDBDBD !important }

/* Prevents Webkit and Windows Mobile platforms from changing default font sizes. */
body {
	-webkit-text-size-adjust: none;
	-ms-text-size-adjust: none;
}

/* Resets all body margins and padding to "0" for good measure. */
body {
	margin: 0;
	padding: 0;
}

/* Resolves webkit padding issue. */
table {
	border-spacing: 0;
}

/* Resolves the Outlook 2007, 2010, and Gmail td padding issue. */
table td {
	border-collapse: collapse;
}

/****** END BUG FIXES ********/

/* Reset Styles */
table,p,h1,h2,h3,h4,h5,h6,strong,a,font {
	font-family: Futura, "Trebuchet MS", Arial, sans-serif !important;
}

h1,h2,h3,h4,h5,h6 {
	line-height: 100%;
}

p {
	margin: 0;
	padding: 0;
}

body {
	margin: 0;
	padding: 0;
	color: #666666;
}

h3,h4,h5 {
	letter-spacing: -1px;
}

a img {
	border: none !important;
}

.container {
	display: block !important;
	max-width: 600px !important;
	margin: 0 auto !important;
	padding: 5px !important;
	clear: both !important;
	font-family: Futura, "Trebuchet MS", Arial, sans-serif;
}

.content {
	margin: 20px 0;
}

table {
	padding: 0 5px;
}

td {
	font-size: 11px;
}

tr {
	border-bottom: 1px solid #ccc !important;
}

.middle {
	
}

.bbb-blue {
	color: #2D368C;
}

.header .bbb-hdr-logo {
	width: 40%;
}

.header .bbb-hdr-message {
	width: 50%;
}

.header h3 {
	margin: 0 0 12px 0;
	font-size: 40px;
}

.header p {
	margin: 0;
}

img.remove {
	float: left;
}

img.rating {
	margin-left: 10px;
}

.cta img.seeDetails {
	margin: 0 4px 0 0;
}

.copy {
	font-size: 16px;
}

.price span {
	font-size: 20px;
}

.price sup {
	font-size: 10px;
}

.main .content .upc {
	font-size: 11px;
}

.main .content .product-img {
	width: 20%;
}

.main .content .product-details {
	width: 70%;
}

.main .content .my-table-item {
	width: 50%;
}

.footer .content td a {
	color: #2D368C;
	font-size: 16px;
	font-weight: bold;
	text-decoration: none;
}

@media ( max-width : 580px) {
	.container,.header .content,.header .content table,.main .content,.main .content table
		{
		width: 100% !important;
	}
	.main .content .section-hdr h3 {
		font-size: 16px !important;
	}
	.middle {
		border-left: none;
		border-right: none;
	}
}
</style>

	<%-- Header Section Starts--%>
	<dsp:valueof param="emailHeader" valueishtml="true" />
	<%-- Header Section Ends--%>
	<tr>
		<td>
			<h3 style="font-size: 20px; margin-bottom: 5px; color: #"width:14px;";">
				<bbbl:label key='lbl_gs_checkList_heading'
					language="${pageContext.request.locale.language}" />
			</h3>
		</td>
	</tr>
	<%-- content section --%>
	<tr>
		<td class="main"
			style="border-top: 3px solid #F5F5F5; border-bottom: 3px solid #F5F5F5; padding-top: 10px; padding-bottom: 10px; margin-bottom: 20px;">
			<table class="content" width="100%" border="0" cellpadding="0"
				cellspacing="0">
				<tr>
					<td style="padding: 5px;" align="left"><dsp:droplet
							name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="emailTemplateVO.compareArray" />
							<dsp:param name="elementName" value="compareVO" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="productIdParam" param="compareVO.productId" />
								<dsp:getvalueof id="skuId" param="compareVO.skuId" />
								<dsp:getvalueof id="isWebStockAvailable" param="compareVO.webStockAvailable"/>
								<dsp:getvalueof var="productName" param="compareVO.name" />
								<dsp:getvalueof var="smallImage" param="compareVO.smallImage" />
								<dsp:getvalueof var="isActive" param="compareVO.isActive" />
								
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${productIdParam}" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="seoUrl" vartype="java.lang.String"
											param="url" />
									</dsp:oparam>
								</dsp:droplet>
								
								<dsp:droplet name="IsEmpty">
									<dsp:param name="value" param="compareVO.skuId" />
									<dsp:oparam name="false">
										<c:set var="finalUrl" value="${seoUrl}?skuId=${skuId}" scope="page" />
									</dsp:oparam>
									<dsp:oparam name="true">
										<c:set var="finalUrl" value="${seoUrl}" scope="page" />
									</dsp:oparam>
								</dsp:droplet>
								
								<table width="180" align="left" class="content left" border="0"
									cellpadding="0" cellspacing="0">
									<tr>
										<td width="50%" valign="top">
											<div class="section-wrapper" width="100%" border="0">
												<dsp:droplet name="Switch">
													<dsp:param name="value" param="compareVO.isActive" />
													<dsp:oparam name="true">
														<dsp:a page="${hostUrl}${finalUrl}" title="${productName}">
															<dsp:droplet name="IsEmpty">
																<dsp:param name="value" param="compareVO.smallImage" />
																<dsp:oparam name="true">
																	<img
																		src="${imagePath}/_assets/global/images/no_image_available.jpg"
																		height="83" width="83"
																		alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
																</dsp:oparam>
																<dsp:oparam name="false">
																	<img src="${scene7Path}/${smallImage}" height="83"
																		width="83"
																		alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
																</dsp:oparam>
															</dsp:droplet>
														</dsp:a>
													</dsp:oparam>
													<dsp:oparam name="false">
															<dsp:droplet name="IsEmpty">
																<dsp:param name="value" param="compareVO.smallImage" />
																<dsp:oparam name="true">
																	<img
																		src="${imagePath}/_assets/global/images/no_image_available.jpg"
																		height="83" width="83"
																		alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
																</dsp:oparam>
																<dsp:oparam name="false">
																	<img src="${scene7Path}/${smallImage}" height="83"
																		width="83"
																		alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
																</dsp:oparam>
															</dsp:droplet>
													</dsp:oparam>
												</dsp:droplet>
												
												<h3 style="font-size: 14px; font-weight: light;">
													<dsp:droplet name="Switch">
														<dsp:param name="value" param="compareVO.isActive" />
														<dsp:oparam name="false">
															<dsp:valueof param="compareVO.name" valueishtml="true" />
														</dsp:oparam>
														<dsp:oparam name="true">
															<dsp:a page="${hostUrl}${finalUrl}" title="${productName}">
																<dsp:valueof param="compareVO.name" valueishtml="true" />
															</dsp:a>
														</dsp:oparam>
													</dsp:droplet>
												</h3>
												<div class="rating">
													<dsp:getvalueof var="reviewRating"
														param="compareVO.reviewRating" />
													<dsp:droplet name="IsEmpty">
														<dsp:param name="value" param="compareVO.reviewRating" />
														<dsp:oparam name="false">
															<img class="rating"
																src="${imagePath}/_assets/emailtemplates/images/${reviewRating}"
																width="40%" />
															<span style="font-size: 10px; margin-left: 3px;">
																<c:out value="${fn:substring(reviewRating,6,11)}"></c:out></span>
														</dsp:oparam>
														<dsp:oparam name="true">
															<span style="font-size: 12px;"><bbbl:label
																	key='lbl_gs_no_ratings'
																	language="${pageContext.request.locale.language}" /></span>
														</dsp:oparam>
													</dsp:droplet>
												</div>
												<h4 class="price">
												<dsp:getvalueof var="productLowSalePrice"
												param="compareVO.productLowSalePrice" /> 
												<c:choose>
												<c:when test="${not empty productLowSalePrice}">
												<span style="text-decoration: line-through;">
												</c:when>
												<c:otherwise>
													<span>
												</c:otherwise>
												</c:choose>
													<span>$<dsp:valueof
															param="compareVO.productLowPrice" valueishtml="true" /><sup><dsp:valueof
																param="compareVO.productLowDecimalPrice"
																valueishtml="true" /> </sup> </span>
													<dsp:getvalueof var="productHighPrice" param="compareVO.productHighPrice" />
														<c:if test="${not empty productHighPrice}">
													-<span>$<dsp:valueof
																param="compareVO.productHighPrice" valueishtml="true" /><sup><dsp:valueof
																	param="compareVO.productHighDecimalPrice"
																	valueishtml="true" /> </sup> </span><br>
													</c:if></span>
												<c:if test="${not empty productLowSalePrice}">
												<span style="color: #009cd7">
													<span>$<dsp:valueof
														param="compareVO.productLowSalePrice"
														valueishtml="true" /> <sup><dsp:valueof
															param="compareVO.productLowDecimalSalePrice"
															valueishtml="true" /> </sup></span> <dsp:getvalueof
														var="productHighSalePrice"
														param="compareVO.productHighSalePrice" /> <c:if
														test="${not empty productHighSalePrice}">
																							- <span>$
														<dsp:valueof
															param="compareVO.productHighSalePrice"
															valueishtml="true" />
														<sup><dsp:valueof
																param="compareVO.productHighDecimalSalePrice"
																valueishtml="true" /> </sup></span>
													</c:if>
												</span>
											</c:if>
												</h4>
												<h5 style="font-size: 9px;"><dsp:valueof param="compareVO.productAvailabilityFlag" valueishtml="true"/></h5>
												<c:if test="${isActive eq 'true' and isWebStockAvailable eq 'true'}">
													<div class="cta">
														<dsp:a page="${hostUrl}${finalUrl}">
															<img class="seeDetails"
																src="${imagePath}/_assets/emailtemplates/images/details.gif"
																height="27px" width="83px">
														</dsp:a>
													</div>
												</c:if>
												
												<%--GS-157 defect. Sort Good To Knows in the order defined in BCC. --%>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" param="compareVO.goodToKnow" />
													<dsp:param name="elementName" value="goodToKnow" />
													<dsp:oparam name="outputStart">
														<h5
															style="font-size: 11px; margin-bottom: 5px; color: #273691;">
															Features</h5>
														<table border="0" cellpadding="5" cellspacing="0">
															</dsp:oparam>
															<dsp:oparam name="output">
																<tr>
																	<td><b></b><dsp:valueof param="goodToKnow" /></b>
																	</td>
																	<td style="width: 14px;"><img
																		src="${imagePath}/_assets/emailtemplates/images/goodToKnow.gif">
																	</td>
																</tr>
															</dsp:oparam>
															<dsp:oparam name="outputEnd">
														</table>
													</dsp:oparam>
												</dsp:droplet>

												<h5
													style="font-size: 11px; margin-bottom: 5px; color: #273691;">
													Product Specs</h5>
												<table width="100%" border="0" cellpadding="5"
													cellspacing="0">
													<tr>
														<dsp:valueof param="compareVO.longDescription"
															valueishtml="true" />
													</tr>
												</table>

												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" param="compareVO.siblingProducts" />
													<dsp:param name="elementName" value="siblingProducts" />
													<dsp:oparam name="outputStart">
														<h5
															style="font-size: 11px; margin-bottom: 5px; color: #273691">
															<strong>Accessories</strong> (Sold Separately)
														</h5>
														<table width="100%" border="0" cellpadding="5"
															cellspacing="0">
															</dsp:oparam>
															<dsp:oparam name="output">
																<dsp:getvalueof var="siblingProductIdParam"
																	param="siblingProducts.productRestVO.productVO.productId" />
																<dsp:getvalueof var="siblingSmallImage"
																	param="siblingProducts.productRestVO.productVO.productImages.smallImage" />
																<dsp:droplet
																	name="/atg/repository/seo/CanonicalItemLink">
																	<dsp:param name="id" value="${siblingProductIdParam}" />
																	<dsp:param name="itemDescriptorName" value="product" />
																	<dsp:param name="repositoryName"
																		value="/atg/commerce/catalog/ProductCatalog" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="siblingFinalUrl"
																			vartype="java.lang.String" param="url" />
																	</dsp:oparam>
																</dsp:droplet>
																<tr>
																	<td style="width: 40px"><dsp:a
																			page="${hostUrl}${siblingFinalUrl}">
																			<c:choose>
																				<c:when test="${empty siblingSmallImage}">
																					<img
																						src="${imagePath}/_assets/global/images/no_image_available.jpg"
																						height="83" width="83"
																						alt="<dsp:valueof param="siblingProducts.productRestVO.productVO.name" valueishtml="true"/>" />
																				</c:when>
																				<c:otherwise>
																					<img src="${scene7Path}/${siblingSmallImage}"
																						height="83" width="83"
																						alt="<dsp:valueof param="siblingProducts.productRestVO.productVO.name" valueishtml="true"/>" />
																				</c:otherwise>
																			</c:choose>
																		</dsp:a></td>
																	<td style="font-size: 10px;"><dsp:getvalueof
																			var="siblingName"
																			param="siblingProducts.productRestVO.productVO.name" />
																		<c:set var="siblingProductName"
																			value="${fn:substring(siblingName, 0, 16)}" />
																		${siblingProductName}...<br /> <dsp:valueof
																			param="siblingProducts.productRestVO.productVO.priceRangeDescription"
																			valueishtml="true" />
																	</td>
																</tr>
															</dsp:oparam>
															<dsp:oparam name="outputEnd">
														</table>
													</dsp:oparam>
												</dsp:droplet>
											</div>
										</td>
									</tr>
								</table>
							</dsp:oparam>
						</dsp:droplet></td>
				</tr>
			</table>
		</td>
	</tr>
	<%-- Main Content Ends--%>
	<%-- footer section --%>
	<dsp:valueof param="emailFooter" valueishtml="true" />

</body>
	</html>
</dsp:page>