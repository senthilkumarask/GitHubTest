<dsp:page>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><dsp:valueof param="emailTemplateVO.siteId" />
</title>
</head>
<body bgcolor="#ffffff">
	<c:set var="imagePath">
	http:<bbbc:config key="image_host_gs" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
	http:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<dsp:getvalueof var="host" param="emailTemplateVO.hostUrl" />
	<c:set var="hostUrl">${host}/store</c:set>

	<dsp:valueof param="emailHeader" valueishtml="true" />

	<%-- Main Content Starts--%>
	<dsp:getvalueof id="productEmailVO" param="productEmailVO" />
	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
		<dsp:param name="id" param="productEmailVO.productId" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName"
			value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="finalUrl"
				vartype="java.lang.String" param="url" />
		</dsp:oparam>
	</dsp:droplet>
	<tr>
		<td class="main"
			style="border-top: 3px solid #F5F5F5; border-bottom: 3px solid #F5F5F5; padding-top: 10px; padding-bottom: 10px; margin-bottom: 20px;">
			<table class="content" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td style="padding: 20px;"><dsp:getvalueof var="upcAvailable"
							param="productEmailVO.upc" /> <c:if
							test="${not empty upcAvailable}">
							<div style="display: block; font-size: 11px;">
								<bbbl:label key='lbl_gs_sku_upc'
									language="${pageContext.request.locale.language}" />
								<dsp:valueof param="productEmailVO.upc" valueishtml="true" />
							</div>
						</c:if> <%-- Product Title --%>
						<div style="display: block; font-size: 24px; margin: 5px 0">
							<dsp:valueof param="productEmailVO.name" valueishtml="true" />
						</div>
						<div style="margin-bottom: 20px; font-size: 12px;">
							<%-- Rating --%>
							<dsp:getvalueof var="reviewRating"
								param="productEmailVO.reviewRating" />
							<c:choose>
								<c:when test="${not empty reviewRating}">
									<img
										src="${imagePath}/_assets/emailtemplates/images/${reviewRating}">
								</c:when>
								<c:otherwise>
									<bbbl:label key='lbl_gs_no_ratings' language="${pageContext.request.locale.language}" />
								</c:otherwise>
							</c:choose>
						</div> <%-- Price --%> <dsp:getvalueof var="productLowSalePrice"
							param="productEmailVO.productLowSalePrice" /> <c:choose>
							<c:when test="${not empty productLowSalePrice}">
								<span class="price"
									style="font-size: 20px; margin-top: 5px; text-decoration: line-through;">
							</c:when>
							<c:otherwise>
								<span class="price" style="font-size: 20px; margin-top: 5px;">
							</c:otherwise>
						</c:choose> <sup style="font-size: 12px;">$</sup> <dsp:valueof
							param="productEmailVO.productLowPrice" valueishtml="true" /> <sup
						style="font-size: 12px;"><dsp:valueof
								param="productEmailVO.productLowDecimalPrice" valueishtml="true" />
					</sup> <dsp:getvalueof var="productHighPrice"
							param="productEmailVO.productHighPrice" /> <c:if
							test="${not empty productHighPrice}">
								- <sup style="font-size: 12px;">$</sup>
							<dsp:valueof param="productEmailVO.productHighPrice"
								valueishtml="true" />
							<sup style="font-size: 12px;"><dsp:valueof
									param="productEmailVO.productHighDecimalPrice"
									valueishtml="true" /> </sup>
						</c:if> </span> <span style="width: 20px;">&nbsp;</span> <c:if
							test="${not empty productLowSalePrice}">
							<span class="price"
								style="font-size: 20px; margin-top: 5px; color: #009cd7">
								<sup style="font-size: 12px;">$</sup> <dsp:valueof
									param="productEmailVO.productLowSalePrice" valueishtml="true" />
								<sup style="font-size: 12px;"><dsp:valueof
										param="productEmailVO.productLowDecimalSalePrice"
										valueishtml="true" /> </sup> <dsp:getvalueof
									var="productHighSalePrice"
									param="productEmailVO.productHighSalePrice" /> <c:if
									test="${not empty productHighSalePrice}">
								- <sup style="font-size: 12px;">$</sup>
									<dsp:valueof param="productEmailVO.productHighSalePrice"
										valueishtml="true" />
									<sup style="font-size: 12px;"><dsp:valueof
											param="productEmailVO.productHighDecimalSalePrice"
											valueishtml="true" /> </sup>
								</c:if> </span>
						</c:if></td>
				</tr>
				<tr>
					<td valign="middle" align="center" width="100%"
						style="padding: 20px 50px;"><dsp:getvalueof
							param="productEmailVO.largeImage" id="largeImage" /> <c:choose>
							<c:when test="${empty largeImage}">
								<img
									src="${imagePath}/_assets/global/images/no_image_available.jpg"
									width="100%;"
									alt="<dsp:valueof param="productEmailVO.name" valueishtml="true" />" />
							</c:when>
							<c:otherwise>
								<img src="${scene7Path}/${largeImage}" width="100%;"
									alt="<dsp:valueof param="productEmailVO.name" valueishtml="true" />" />
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td style="padding: 20px;">
						<%-- Product Description --%>
						<h3 style="font-size: 14px; margin-bottom: 5px;">Overview</h3>
						<div class="description"
							style="font-size: 12px; margin-top: 10px; margin-bottom: 10px;">
							<dsp:valueof param="productEmailVO.longDescription"
								valueishtml="true" />
						</div> <dsp:getvalueof var="productAvailabilityFlag"
							param="productEmailVO.productAvailabilityFlag" /> <dsp:getvalueof
							var="isActive" param="productEmailVO.isActive" /> <c:if
							test="${isActive eq 'true' || productAvailabilityFlag eq 'true'}">
							<h3 style="font-size: 14px; margin-bottom: 5px;">
								<bbbl:label key='lbl_gs_table_avlbl' language="${pageContext.request.locale.language}" /></h3>
							<c:if test="${productAvailabilityFlag eq 'true'}">
								<span
									style="display: block; margin-bottom: 10px; font-size: 16px;">
									<bbbl:label key='lbl_gs_in_store'
										language="${pageContext.request.locale.language}" /><br>
									<span style="display: block; color: #2D368C;"><dsp:a
											href="${hostUrl}/selfservice/FindStore?flashEnabled=true">
											<dsp:valueof param="storeVO.storeName" valueishtml="true" />
										</dsp:a> </span> <dsp:valueof param="storeVO.address" valueishtml="true" /><br>
									<dsp:valueof param="storeVO.city" valueishtml="true" />, <dsp:valueof
										param="storeVO.state" valueishtml="true" /> <dsp:valueof
										param="storeVO.postalCode" valueishtml="true" /> </span>
							</c:if>
							<br>
							<c:if test="${isActive eq 'true'}">
								<span
									style="display: block; margin-bottom: 10px; font-size: 16px;">
									<bbbl:label key='lbl_gs_online_only'
										language="${pageContext.request.locale.language}" /><br>
								</span>
								<dsp:a page="${hostUrl}${finalUrl}" title="Buy Now"
									style="display: block; color: #2D368C;">
									<img
										src="${imagePath}/_assets/emailtemplates/images/buy-now.gif">
								</dsp:a>
							</c:if>
						</c:if>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<%-- Main Content Ends--%>
	<dsp:valueof param="emailFooter" valueishtml="true" />
</body>
	</html>
</dsp:page>