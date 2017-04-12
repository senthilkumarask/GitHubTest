<dsp:page>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
	<dsp:valueof param="emailTemplateVO.siteId" valueishtml="true"/>
</title>
<meta name="viewport" content="width=device-width" />
</head>
<body bgcolor="#FFFFFF">
	
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
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
    .ExternalClass {width:100%;}
    .ReadMsgBody {width: 100%;}
    
    /* Forces Hotmail to display normal line spacing. */
    .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height:100%;}

    /* Prevents Webkit and Windows Mobile platforms from changing default font sizes. */
    body {-webkit-text-size-adjust:none; -ms-text-size-adjust:none;}

    /* Resets all body margins and padding to "0" for good measure. */
    body {margin:0; padding:0;}

    /* Resolves webkit padding issue. */
    table {border-spacing:0;}

    /* Resolves the Outlook 2007, 2010, and Gmail td padding issue. */
    table td {border-collapse:collapse;}
    
    /****** END BUG FIXES ********/
    
    /* Reset Styles */
    table, p, h1, h2, h3, h4, h5, h6, strong, a, font { font-family: Futura, "Trebuchet MS", Arial, sans-serif!important; }
    h1, h2, h3, h4, h5, h6 {line-height:100%;}
    p {margin:0; padding:0;}

    body {
        margin: 0;
        padding: 0;
        color: #666666;
    }
    a img {border: none!important;}
    .container {
        display: block!important;
        width: 100%!important;
        max-width: 600px!important;
        margin: 0 auto!important;
        padding: 5px!important;
        clear: both!important;
        font-family: Futura, "Trebuchet MS", Arial, sans-serif;
    }
    .content {
        margin: 20px 0;
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
    .copy {
        font-size: 16px;
    }
    .price span {
        font-size: 20px;
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
    .footer .content td a {
        color: #2D368C;
        font-size: 16px;
        font-weight: bold;
        text-decoration: none;
    }
    
    @media (max-width: 580px) {
        .header .content,
        .header .content table,
        .main .content,
        .main .content table {
            width: 100%!important;
        } 
    }
    </style>
	<%-- Header Section Starts--%>
	<dsp:valueof param="emailHeader" valueishtml="true"/>
	<%-- Header Section Ends--%>
		<%-- Main Content Starts--%>
		<tr>
			<td class="main">
				<table class="content" border="0" cellpadding="" cellspacing="0">
					<%-- Item --%>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="emailTemplateVO.compareArray" />
						<dsp:param name="elementName" value="compareVO" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productIdParam" param="compareVO.productId" />
							<dsp:getvalueof var="productName" param="compareVO.name" />
							<dsp:getvalueof var="smallImage" param="compareVO.smallImage" />
							<dsp:getvalueof var="isActive" param="compareVO.isActive" />
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" value="${productIdParam}" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>

							<tr>
								<td valign="top" width="100%">
									<%-- Product Image --%>
									<table class="product-img" width="100%" align="left" border="0"
										cellpadding="10" cellspacing="0"
										style="font-family: Futura, Trebuchet MS, Arial, sans-serif; margin-bottom: 15px;">
										<tr>
											<td valign="top" align="center"><c:choose><c:when test="${isActive eq 'true' }"><dsp:a
													page="${hostUrl}${finalUrl}" title="${productName}">
													<c:choose>
														<c:when test="${empty smallImage}">
															<img
																src="${imagePath}/_assets/global/images/no_image_available.jpg"
																height="83" width="83"
																alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${smallImage}" height="83"
																width="83" alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
														</c:otherwise>
													</c:choose>
												</dsp:a></c:when>
												<c:otherwise>
												<c:choose>
														<c:when test="${empty smallImage}">
															<img
																src="${imagePath}/_assets/global/images/no_image_available.jpg"
																height="83" width="83"
																alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${smallImage}" height="83"
																width="83" alt="<dsp:valueof param="compareVO.name" valueishtml="true"/>" />
														</c:otherwise>
												</c:choose>
												</c:otherwise></c:choose>
											</td>
										</tr>
									</table>
									<table class="product-details" width="100%" align="right"
										border="0" cellpadding="10" cellspacing="0"
										style="font-family: Futura, Trebuchet MS, Arial, sans-serif; margin-bottom: 15px;">
										<tr>
											<%-- Product Details (UPC, Title, Rating, Price) --%>
											<td valign="top" width="50%"><dsp:getvalueof var="upcAvailable" param="compareVO.upc" />
											 <c:if test="${not empty upcAvailable}">
														<div style="display: block; font-size: 11px;">
															<bbbl:label key='lbl_gs_sku_upc' language="${pageContext.request.locale.language}" />
															<dsp:valueof param="compareVO.upc" valueishtml="true"/>
														</div>
													</c:if>

												<div style="display: block; font-size: 16px;">
												<dsp:valueof param="compareVO.name" valueishtml="true" />
											</div> 
											<dsp:getvalueof var="reviewRating" param="compareVO.reviewRating" />
											<c:choose>
								<c:when test="${not empty reviewRating}"><img
											src="${imagePath}/_assets/emailtemplates/images/${reviewRating}"></c:when><c:otherwise>
									<span style="font-size: 12px;"><bbbl:label key='lbl_gs_no_ratings' language="${pageContext.request.locale.language}" /></span>
								</c:otherwise>
							</c:choose>
											<br> <dsp:getvalueof var="productLowSalePrice"
												param="compareVO.productLowSalePrice" /> <c:choose>
												<c:when test="${not empty productLowSalePrice}">
													<span class="price"
														style="font-size: 20px; text-decoration: line-through;">
												</c:when>
												<c:otherwise>
													<span class="price" style="font-size: 20px;">
												</c:otherwise>
											</c:choose> <%-- <div class="price" style="display: block; font-size: 20px;"> --%>
											<sup style="font-size: 12px;">$</sup> <dsp:valueof
												param="compareVO.productLowPrice" valueishtml="true" /> <span
											style="display: none;">.</span><sup style="font-size: 12px;"><dsp:valueof
													param="compareVO.productLowDecimalPrice" valueishtml="true" /></sup>
											<dsp:getvalueof var="productHighPrice"
												param="compareVO.productHighPrice" /> <c:if
												test="${not empty productHighPrice}">
													- <sup style="font-size: 12px;">$</sup>
												<dsp:valueof param="compareVO.productHighPrice"
													valueishtml="true" />
												<span style="display: none;">.</span>
												<sup style="font-size: 12px;"><dsp:valueof
														param="compareVO.productHighDecimalPrice"
														valueishtml="true" /></sup><br>
											</c:if></span> 
											<c:if test="${not empty productLowSalePrice}">
												<span class="price"
													style="font-size: 20px; color: #009cd7">
													<sup style="font-size: 12px;">$</sup> <dsp:valueof
														param="compareVO.productLowSalePrice"
														valueishtml="true" /> <sup style="font-size: 12px;"><dsp:valueof
															param="compareVO.productLowDecimalSalePrice"
															valueishtml="true" /> </sup> <dsp:getvalueof
														var="productHighSalePrice"
														param="compareVO.productHighSalePrice" /> <c:if
														test="${not empty productHighSalePrice}">
																							- <sup style="font-size: 12px;">$</sup>
														<dsp:valueof
															param="compareVO.productHighSalePrice"
															valueishtml="true" />
														<sup style="font-size: 12px;"><dsp:valueof
																param="compareVO.productHighDecimalSalePrice"
																valueishtml="true" /> </sup>
													</c:if>
												</span>
											</c:if>
										</td>
											<%-- Product Availability (In store, Store Details, Buy Now) --%>
											<dsp:getvalueof var="productAvailability"
												param="compareVO.productAvailabilityFlag" />
											<td valign="top" width="50%"><c:if
													test="${productAvailability eq 'true' }">
													<span
														style="display: block; margin-bottom: 10px; font-size: 16px;">
														<bbbl:label key='lbl_gs_in_store' language="${pageContext.request.locale.language}" /><br> <span
														style="display: block; color: #2D368C;"> <dsp:a
																page="${hostUrl}/selfservice/FindStore?flashEnabled=true">
																<dsp:valueof param="emailTemplateVO.storeVO.storeName" valueishtml="true"/>
															</dsp:a> </span><br> <dsp:valueof
															param="emailTemplateVO.storeVO.address" valueishtml="true"/><br> <dsp:valueof
															param="emailTemplateVO.storeVO.city" valueishtml="true"/>, <dsp:valueof
															param="emailTemplateVO.storeVO.state" valueishtml="true"/> <dsp:valueof
															param="emailTemplateVO.storeVO.postalCode" valueishtml="true"/> </span>
												</c:if><br> 
												<c:if test="${isActive eq 'true' }">
												<span style="display: block;margin-bottom:10px;font-size:16px;">
															<bbbl:label key='lbl_gs_online_only' language="${pageContext.request.locale.language}" />
															<br>
															</span>
												<dsp:a page="${hostUrl}${finalUrl}" title="Buy Now"
													style="display: block; color: #2D368C;">
													<img src="${imagePath}/_assets/emailtemplates/images/buy-now.gif">
												</dsp:a>
												</c:if>
											</td>
										</tr>
									</table></td>
							</tr>
						</dsp:oparam>
					</dsp:droplet>
				</table></td>
		</tr>
		<%-- Main Content Ends--%>
		<%-- footer section --%>
		<dsp:valueof param="emailFooter" valueishtml="true"/>	
	
</body>
	</html>
</dsp:page>