<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />



	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><dsp:valueof param="emailTemplateVO.siteId"
		valueishtml="true" /></title>
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

	<!-- If you see this message, please enable HTML e-mail -->

	<dsp:valueof param="emailTemplateVO.emailHeader" valueishtml="true" />
	<%-- Main Content --%>

	<tr>
		<td class="main"
			style="border-top: 3px solid #F5F5F5; border-bottom: 3px solid #F5F5F5; padding-top: 10px; padding-bottom: 10px; margin-bottom: 20px;">
			<table class="content" border="0" cellpadding="" cellspacing="0">
				<tr>
					<td valign="top" width="100%">
						<%-- ITEM SECTION HEADING --%> <dsp:droplet
							name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array"
								param="emailTemplateVO.tableRegistryCartMap" />
							<dsp:param name="elementName" value="tableRegistryCartArray" />
							<dsp:oparam name="output">
								<div class="section-wrapper" width="100%" border="0">
									<table class="section-hdr" width="100%" border="0"
										cellpadding="15" cellspacing="0"
										style="font-family: Futura, Trebuchet MS, Arial, sans-serif; margin-bottom: 10px;">
										<tr>
											<td valign="middle" align="left">
												<h3 style="font-size: 18px; font-weight: normal !important;">
													<bbbl:label key='lbl_gs_table_registry_heading'
														language="${pageContext.request.locale.language}" />
													<dsp:valueof param="key" valueishtml="true" />
												</h3>
											</td>
											<td valign="middle" align="right"><dsp:a
													page="${hostUrl}" title="Buy Online Now">
													<img
														src="${imagePath}/_assets/emailtemplates/images/buy-online-now.gif">
												</dsp:a></td>
										</tr>
									</table>
									<%-- Item WRAPPER --%>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="tableRegistryCartArray" />
										<dsp:param name="elementName" value="tableRegistryCartVO" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="productIdParam"
												param="tableRegistryCartVO.productId" />
											<dsp:getvalueof var="productName"
												param="tableRegistryCartVO.name" />
											<dsp:getvalueof var="smallImage"
												param="tableRegistryCartVO.smallImage" />
											<dsp:getvalueof var="productAvailabilityFlag"
												param="tableRegistryCartVO.productAvailabilityFlag" />
											<dsp:getvalueof var="isActive"
												param="tableRegistryCartVO.isActive" />
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
											<table class="item-container" cellpadding="0" cellspacing="0"
												border="0">
												<tr>
													<td>
														<%-- ITEM --%>
														<table class="my-table-item" width="100%" align="left"
															border="0" cellpadding="0" cellspacing="0"
															style="font-family: Futura, Trebuchet MS, Arial, sans-serif; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
															<thead
																style="text-transform: uppercase; font-size: 12px; color: #DEDEDE;">
																<tr>
																	<td
																		style="padding-bottom: 5px; border-bottom: 2px solid #DEDEDE;"><bbbl:label
																			key='lbl_gs_table_item'
																			language="${pageContext.request.locale.language}" />
																	</td>
																	<td
																		style="padding-bottom: 5px; border-bottom: 2px solid #DEDEDE;"><bbbl:label
																			key='lbl_gs_table_desc'
																			language="${pageContext.request.locale.language}" />
																	</td>
																</tr>
															</thead>
															<tbody>
																<tr>
																	<%-- Product Image --%>
																	<td valign="top" align="left" width="25%"
																		style="padding-top: 10px;"><c:choose>
																			<c:when test="${isActive eq 'true' }">
																				<dsp:a page="${hostUrl}${finalUrl}"
																					title="${productName}">
																					<c:choose>
																						<c:when test="${empty smallImage}">
																							<img
																								src="${imagePath}/_assets/global/images/no_image_available.jpg"
																								height="83" width="83"
																								alt="<dsp:valueof param="tableRegistryCartVO.name" valueishtml="true" />" />
																						</c:when>
																						<c:otherwise>
																							<img src="${scene7Path}/${smallImage}"
																								height="83" width="83"
																								alt="<dsp:valueof param="tableRegistryCartVO.name" valueishtml="true" />" />
																						</c:otherwise>
																					</c:choose>
																				</dsp:a>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${empty smallImage}">
																						<img
																							src="${imagePath}/_assets/global/images/no_image_available.jpg"
																							height="83" width="83"
																							alt="<dsp:valueof param="tableRegistryCartVO.name" valueishtml="true" />" />
																					</c:when>
																					<c:otherwise>
																						<img src="${scene7Path}/${smallImage}" height="83"
																							width="83"
																							alt="<dsp:valueof param="tableRegistryCartVO.name" valueishtml="true" />" />
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose></td>
																	<%-- Product Details (UPC, Title, Rating, Price) --%>
																	<td valign="top" width="25%" align="left"
																		style="padding-top: 10px;"><dsp:getvalueof
																			var="upcAvailable" param="tableRegistryCartVO.upc" />
																		<c:if test="${not empty upcAvailable}">
																			<div style="display: block; font-size: 11px;">
																				<bbbl:label key='lbl_gs_sku_upc'
																					language="${pageContext.request.locale.language}" />
																				<dsp:valueof param="tableRegistryCartVO.upc"
																					valueishtml="true" />
																			</div>
																		</c:if>
																		<div style="display: block; font-size: 16px;">
																			<dsp:valueof param="tableRegistryCartVO.name"
																				valueishtml="true" />
																		</div> <dsp:getvalueof var="reviewRating"
																			param="tableRegistryCartVO.reviewRating" /> <c:choose>
																			<c:when test="${not empty reviewRating}">
																				<img
																					src="${imagePath}/_assets/emailtemplates/images/${reviewRating}">
																			</c:when>
																			<c:otherwise>
																				<span style="font-size: 12px;"><bbbl:label
																						key='lbl_gs_no_ratings'
																						language="${pageContext.request.locale.language}" />
																				</span>
																			</c:otherwise>
																		</c:choose> <br> <dsp:getvalueof var="productLowSalePrice"
																			param="tableRegistryCartVO.productLowSalePrice" /> <c:choose>
																			<c:when test="${not empty productLowSalePrice}">
																				<span class="price"
																					style="font-size: 20px; margin-top: 5px; text-decoration: line-through;">
																			</c:when>
																			<c:otherwise>
																				<span class="price"
																					style="font-size: 20px; margin-top: 5px;">
																			</c:otherwise>
																		</c:choose> <%-- <div class="price"
																			style="display: block; font-size: 20px;"> --%> <sup
																		style="font-size: 12px;">$</sup> <dsp:valueof
																			param="tableRegistryCartVO.productLowPrice"
																			valueishtml="true" /> <span style="display: none;">.</span><sup
																		style="font-size: 12px;"><dsp:valueof
																				param="tableRegistryCartVO.productLowDecimalPrice"
																				valueishtml="true" /> </sup> <dsp:getvalueof
																			var="productHighPrice"
																			param="tableRegistryCartVO.productHighPrice" /> <c:if
																			test="${not empty productHighPrice}">
																					- <sup style="font-size: 12px;">$</sup>
																			<dsp:valueof
																				param="tableRegistryCartVO.productHighPrice"
																				valueishtml="true" />
																			<span style="display: none;">.</span>
																			<sup style="font-size: 12px;"><dsp:valueof
																					param="tableRegistryCartVO.productHighDecimalPrice"
																					valueishtml="true" /> </sup>
																			<br>
																		</c:if> </span>
																		<c:if test="${not empty productLowSalePrice}">
																			<span class="price"
																				style="font-size: 20px; margin-top: 5px; color: #009cd7">
																				<sup style="font-size: 12px;">$</sup> <dsp:valueof
																					param="tableRegistryCartVO.productLowSalePrice"
																					valueishtml="true" /> <sup
																				style="font-size: 12px;"><dsp:valueof
																						param="tableRegistryCartVO.productLowDecimalSalePrice"
																						valueishtml="true" /> </sup> <dsp:getvalueof
																					var="productHighSalePrice"
																					param="tableRegistryCartVO.productHighSalePrice" />
																				<c:if test="${not empty productHighSalePrice}">
																							- <sup style="font-size: 12px;">$</sup>
																					<dsp:valueof
																						param="tableRegistryCartVO.productHighSalePrice"
																						valueishtml="true" />
																					<sup style="font-size: 12px;"><dsp:valueof
																							param="tableRegistryCartVO.productHighDecimalSalePrice"
																							valueishtml="true" /> </sup>
																				</c:if>
																			</span>
																		</c:if></td>
																</tr>
															</tbody>
														</table>
														<table class="my-table-item" width="100%" align="left"
															border="0" cellpadding="0" cellspacing="0"
															style="font-family: Futura, Trebuchet MS, Arial, sans-serif; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt;">
															<thead
																style="text-transform: uppercase; font-size: 12px; color: #DEDEDE;">
																<tr>
																	<td
																		style="padding-bottom: 5px; border-bottom: 2px solid #DEDEDE;"><bbbl:label
																			key='lbl_gs_table_qty'
																			language="${pageContext.request.locale.language}" />
																	</td>
																	<td
																		style="padding-bottom: 5px; border-bottom: 2px solid #DEDEDE;"><bbbl:label
																			key='lbl_gs_table_avlbl'
																			language="${pageContext.request.locale.language}" />
																	</td>
																</tr>
															</thead>
															<tbody>
																<tr>
																	<%-- Quantity --%>
																	<td valign="top" width="25%" align="left" align="left"
																		style="padding-top: 10px;"><dsp:valueof
																			param="tableRegistryCartVO.quantity"
																			valueishtml="true" /></td>
																	<%-- Product Availability (In store, Store Details, Buy Now) --%>

																	<td valign="top" width="25%" align="left"
																		style="padding-top: 10px;"><c:if
																			test="${productAvailabilityFlag == 'true'}">
																			<span
																				style="display: block; margin-bottom: 10px; font-size: 16px;">
																				<bbbl:label key='lbl_gs_in_store'
																					language="${pageContext.request.locale.language}" /><br>
																				<span style="display: block; color: #2D368C;"><dsp:a
																						href="${hostUrl}/selfservice/FindStore?flashEnabled=true">
																						<dsp:valueof
																							param="emailTemplateVO.storeVO.storeName"
																							valueishtml="true" />
																					</dsp:a> </span> <dsp:valueof
																					param="emailTemplateVO.storeVO.address"
																					valueishtml="true" /><br> <dsp:valueof
																					param="emailTemplateVO.storeVO.city"
																					valueishtml="true" />, <dsp:valueof
																					param="emailTemplateVO.storeVO.state"
																					valueishtml="true" /> <dsp:valueof
																					param="emailTemplateVO.storeVO.postalCode"
																					valueishtml="true" />
																			</span>
																		</c:if> <dsp:getvalueof var="isActive"
																			param="tableRegistryCartVO.isActive" /> <c:if
																			test="${isActive eq 'true'}">
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
																		</c:if></td>
																</tr>
															</tbody>
														</table>
													</td>
												</tr>
											</table>
											<%-- CLEAR SPACE BETWEEN ITEMS --%>
											<table class="clear"
												style="clear: both; margin-top: 5px; margin-bottom: 5px;"
												width="100%" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>&nbsp;</td>
												</tr>
											</table>
										</dsp:oparam>
									</dsp:droplet>
								</div>
							</dsp:oparam>
						</dsp:droplet> <%-- ITEM SECTION HEADING --%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<dsp:valueof param="emailTemplateVO.emailFooter" valueishtml="true" />
</body>
	</html>
</dsp:page>