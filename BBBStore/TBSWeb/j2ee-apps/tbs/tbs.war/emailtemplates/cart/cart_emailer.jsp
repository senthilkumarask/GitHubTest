
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />


	<dsp:getvalueof var="serverName" param="serverName" />
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />


	<c:set var="serverPath" value="http://${serverName}" />
	<c:set var="imagePath">
http:<bbbc:config key="image_host" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
http:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>


	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><dsp:valueof param="emailTemplateVO.siteId" /> Email
	Cart</title>
</head>
<body bgcolor="#ffffff" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">


	<dsp:valueof param="emailTemplateVO.emailHeader" valueishtml="true" />
	<%-- Main Content --%>

	<%-- // Begin Template Main Content \\ --%>
	<tr>
		<td align="left" valign="top">
			<table width="96%" cellspacing="0" cellpadding="0" border="0"
				align="center">
				<tbody>
					<tr>
						<td valign="middle" height="50"><h3 class="resetTextSize"
								style="color: #666666; font-family: Arial; padding: 0; margin: 0; font-family: arial;">
								<bbbl:label key="lbl_cart_contents" language="<c:out param='${language}'/>" />
								<dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
									<dsp:param name="order" param="order" />
									<dsp:oparam name="output">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" param="commerceItemList" />
											<dsp:oparam name="outputEnd">
												<dsp:valueof param="size" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
								Item(s)
						</td>
					</tr>
				</tbody>
			</table>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0" width="100%"
				align="left"
				style="border-bottom: 1px solid #E8E8E8; color: #666666; font-family: Arial; font-size: 14px; line-height: 16px;">
				<tbody>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="order.commerceItems" />
						<dsp:oparam name="false">
							<dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
								<dsp:param name="order" param="order" />
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="commerceItemList" />
										<dsp:param name="sortProperties"
											value="+BBBCommerceItem.registryId" />
										<dsp:param name="elementName" value="commerceItem" />
										<dsp:getvalueof var="commItem" param="commerceItem" />

										<dsp:oparam name="output">
											<tr>
												<c:choose>
													<c:when
														test="${not empty commItem.BBBCommerceItem.registryId}">
														<td bgcolor="#FBF8FD"
															style="border-top: 1px solid #a80bd6;">
													</c:when>
													<c:otherwise>
														<td style="border-top: 1px solid #E8E8E8;">
													</c:otherwise>
												</c:choose>

												<table width="100%" cellspacing="0" cellpadding="5"
													style="color: #666666; font-family: Arial;"
													id="tblProductDetailsRow">
													<tbody>

														<%--for registry items in cart--%>
														<c:if
															test="${not empty commItem.BBBCommerceItem.registryId}">
															<c:set var="registryFlag" value="true" />
															<c:set var="RegistryContext" scope="request">${RegistryContext}${commItem.BBBCommerceItem.registryId};</c:set>

															<dsp:getvalueof
																param="order.registryMap.${commItem.BBBCommerceItem.registryId}"
																var="registratantVO" />
															<c:if
																test="${commItem.BBBCommerceItem.registryInfo ne null}">
																<tr>
																	<td height="20" bgcolor="#f6f0fb" colspan="2"><p
																			style="margin: 0; padding: 0; font-size: 12px; color: #45165C;">
																			<bbbl:label key="lbl_cart_registry_from_text"
																				language="<c:out param='${language}'/>" />
																			&nbsp;
																			<dsp:getvalueof var="registryInfo"
																				param="commerceItem.BBBCommerceItem.registryInfo" />
																			<font style="font-weight: bold;">${registratantVO.primaryRegistrantFirstName}<c:if
																					test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp; ${registratantVO.coRegistrantFirstName}</c:if>
																				<bbbl:label key="lbl_cart_registry_name_suffix"
																					language="<c:out param='${language}'/>" />
																			</font>&nbsp;
																			<dsp:droplet name="/atg/commerce/order/droplet/BBBCartRegistryDisplayDroplet">
																				<dsp:param name="registryId" value="${commItem.BBBCommerceItem.registryId}" />
																				<dsp:param name="order" param="order" />
																				<dsp:oparam name="regOutput">
																					<dsp:valueof param="registryType" />&nbsp;<bbbl:label
																						key="lbl_cart_registry_text"
																						language="<c:out param='${language}'/>" />
																				</dsp:oparam>
																			</dsp:droplet>
																		</p></td>
																</tr>
															</c:if>


														</c:if>
														<tr>
															<%--registry items end here--%>
															<dsp:droplet
																name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
																<dsp:param name="priceObject"
																	value="${commItem.BBBCommerceItem}" />
																<dsp:param name="orderObject" param="order" />	
																<dsp:param name="elementName" value="priceInfoVO" />
																<dsp:oparam name="output">
																	<dsp:getvalueof var="unitSavedAmount"
																		param="priceInfoVO.unitSavedAmount" />
																	<dsp:getvalueof var="unitListPrice"
																		param="priceInfoVO.unitListPrice" />
																	<dsp:getvalueof var="unitSalePrice"
																		param="priceInfoVO.unitSalePrice" />
																	<dsp:getvalueof var="unitSavedPercentage"
																		param="priceInfoVO.unitSavedPercentage" />
																	<dsp:getvalueof var="totalSavedAmount"
																		param="priceInfoVO.totalSavedAmount" />
																	<dsp:getvalueof var="adjustmentsList"
																		param="priceInfoVO.adjustmentsList" />
																	<dsp:getvalueof var="undiscountedItemsCount"
																		param="priceInfoVO.undiscountedItemsCount" />
																	<dsp:getvalueof var="totalAmount"
																		param="priceInfoVO.totalAmount" />
																	<dsp:getvalueof var="priceBeans"
																		param="priceInfoVO.priceBeans" />	
																	 <dsp:getvalueof var="deliverySurcharge" 
																        param="priceInfoVO.deliverySurcharge"/>
    																<dsp:getvalueof var="deliverySurchargeSaving" 
    																    param="priceInfoVO.deliverySurchargeSaving"/>
    																<dsp:getvalueof var="assemblyFee" 
    																    param="priceInfoVO.assemblyFee"/>
     																<dsp:getvalueof var="shippingMethodAvl" 
     																    param="shippingMethodAvl"/>
																</dsp:oparam>
															</dsp:droplet>
															<%--image--%>
															<td align="center" valign="top" style="width: 125px;">
																<c:choose>
																	<%-- <c:when test="${empty commItem.skuDetailVO.skuImages.smallImage}"> --%>
																	<c:when
																		test="${empty commItem.skuDetailVO.skuImages.thumbnailImage}">
																		<dsp:img
																			src="${imagePath}/_assets/global/images/no_image_available.jpg"
																			iclass="alignCenter" border="0" width="83"
																			alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																			title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																	</c:when>
																	<c:otherwise>
																		<%-- <dsp:img src="${scene7Path}/${commItem.skuDetailVO.skuImages.smallImage}" style="max-width:83px;" width="83" height="83" alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" iclass="noImageFound"/> --%>
																		<dsp:img
																			src="${scene7Path}/${commItem.skuDetailVO.skuImages.thumbnailImage}"
																			iclass="alignCenter" border="0" width="83"
																			alt="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}"
																			title="${commItem.skuDetailVO.displayName}&nbsp;$${unitListPrice}" />
																	</c:otherwise>
																</c:choose>
															</td>
															<td valign="top"><dsp:droplet
																	name="/atg/repository/seo/CanonicalItemLink">
																	<dsp:param name="id"
																		param="commerceItem.BBBCommerceItem.repositoryItem.productId" />
																	<dsp:param name="itemDescriptorName" value="product" />
																	<dsp:param name="repositoryName"
																		value="/atg/commerce/catalog/ProductCatalog" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="finalUrl"
																			vartype="java.lang.String" param="url" />
																	</dsp:oparam>
																</dsp:droplet> <%--display Name with sku id--%>
																<table cellspacing="0" cellpadding="0" width="40%"
																	align="left">
																	<tbody>
																		<tr>
																			<td align="left">
																				<p style="margin: 0; padding: 0;">
																					<c:choose>
																						<c:when
																							test="${commItem.BBBCommerceItem.storeSKU}">
																							<c:out
																								value="${commItem.skuDetailVO.displayName}"
																								escapeXml="false" />
																						</c:when>
																						<c:otherwise>
																						<dsp:getvalueof var="siteId" param="emailTemplateVO.siteId" />
																							<c:choose>
																							<c:when test="${siteId eq 'BedBathUS'}">
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																										<c:out
																											value="${commItem.skuDetailVO.displayName}"
																											escapeXml="false" />
																									</dsp:a>
																								</c:when>
																								<c:when test="${siteId eq 'BuyBuyBaby'}">
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #32BCF3; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																										<c:out
																											value="${commItem.skuDetailVO.displayName}"
																											escapeXml="false" />
																									</dsp:a>
																								</c:when>
																								<c:otherwise>
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																										<c:out
																											value="${commItem.skuDetailVO.displayName}"
																											escapeXml="false" />
																									</dsp:a>

																								</c:otherwise>
																							</c:choose>

																						</c:otherwise>
																					</c:choose>
																				</p>
																				<p style="font-size:12px" class="resetTextSize">
																					<c:if test='${not empty commItem.skuDetailVO.color}'>
																						 <bbbl:label key="lbl_item_color" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${commItem.skuDetailVO.color}" valueishtml="true" />
																					</c:if>
																					<c:if test='${not empty commItem.skuDetailVO.size}'>
																						 <c:if test='${not empty commItem.skuDetailVO.color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${commItem.skuDetailVO.size}" valueishtml="true" />
																					</c:if>
																				</p>
																			</td>
																		</tr>
																		<tr>
																			<td height="25"><font
																				style="font-size: 12px; font-family: Arial;" class="resetTextSize">SKU
																					${commItem.BBBCommerceItem.catalogRefId}</font></td>
																		</tr>
																		<tr>
																			<td height="10">&nbsp;</td>
																		</tr>
																	</tbody>
																</table> <%--price+quantity--%>
																<table cellspacing="0" cellpadding="0" width="55%"
																	align="right">
																	<tbody>
																		<tr>
																			<td>

																				<table cellspacing="0" cellpadding="0" width="20%"
																					align="left" height="20">
																					<tbody>
																						<tr>
																							<td class="alignLeft" align="center" valign="top">
																								<font style="font-family:arial;font-size:12px;line-height:15px;color:#666;font-weight:bold;" class="resetTextSize">QTY:</font>&nbsp;<font class="resetTextSize" style="font-family:arial;font-size:12px;line-height:15px;color:#666;">${commItem.BBBCommerceItem.quantity}</font>
																							</td>
																						</tr>
																					</tbody>
																				</table>
																				<table cellspacing="0" cellpadding="0" align="right"
																					width="70%">
																					<tbody>
																						<tr>
																							<td valign="top"><table cellspacing="0"
																									cellpadding="0" class="resetTextSize"
																									style="margin: 0; padding: 0; color: #666; font-family: Arial; font-size: 14px;">
																									<tbody>
																										<tr>
																											<td valign="top" class="hideOnSmallScreens"
																												style="height: 20px; vertical-align: top;"
																												height="20"><font
																												style="font-family: Arial; font-weight: bold;">Total:
																													<c:choose>
																														<c:when
																															test="${undiscountedItemsCount eq commItem.BBBCommerceItem.quantity}">

																															<dsp:valueof value="${totalAmount}"
																																converter="currency" />
																														</c:when>
																														<c:otherwise>
																															<dsp:valueof value="${totalAmount}"
																																converter="currency" />
																														</c:otherwise>
																													</c:choose> </font>
																											</td>
																										</tr>
																										<tr>
																											<td valign="top" style="height: 20px;"
																												height="20" style="vertical-align: middle;"><font
																												style="font-family: Arial; font-size: 12px; line-height: 25px;"
																												class="boldInSmallScreens"> Your
																													Price:</font> <font
																												style="font-family: Arial; font-size: 12px; line-height: 25px;"><c:choose>
																														<c:when
																															test="${undiscountedItemsCount gt 0}">
																															<c:choose>
																																<c:when test="${unitSavedAmount gt 0.0}">
																																	<dsp:valueof
																																		value="${undiscountedItemsCount}" />
																																	<bbbl:label key="lbl_cart_multiplier"
																																		language="${language}" />
																																	<dsp:valueof value="${unitSalePrice}"
																																		converter="currency" />


																																</c:when>
																																<c:otherwise>
																																	<dsp:valueof
																																		value="${undiscountedItemsCount}" />
																																	<bbbl:label key="lbl_cart_multiplier"
																																		language="${language}" />
																																	<dsp:valueof value="${unitListPrice}"
																																		converter="currency" />

																																</c:otherwise>
																															</c:choose>
																														</c:when>
																													</c:choose> <dsp:droplet name="ForEach">
																														<dsp:param name="array"
																															value="${priceBeans}" />
																														<dsp:param name="elementName"
																															value="unitPriceBean" />
																														<dsp:oparam name="output">
																															<dsp:droplet name="IsEmpty">
																																<dsp:param name="value"
																																	param="unitPriceBean.pricingModels" />
																																<dsp:oparam name="false">
																																	<dsp:valueof
																																		param="unitPriceBean.quantity" />
																																	<bbbl:label key="lbl_cart_multiplier"
																																		language="${language}" />
																																	<span class="highlight"><dsp:valueof
																																			param="unitPriceBean.unitPrice"
																																			converter="currency" /> </span>
																																	<br />
																																	<ul
																																		class="prodDeliveryInfo pricingModels noMar">
																																		<li class="pricingModel"><dsp:droplet
																																				name="ForEach">
																																				<dsp:param name="array"
																																					param="unitPriceBean.pricingModels" />
																																				<dsp:param name="elementName"
																																					value="pricingModel" />
																																				<dsp:oparam name="output">
																																					<span><strong><dsp:valueof
																																								param="pricingModel.displayName" />
																																					</strong> </span>
																																				</dsp:oparam>
																																			</dsp:droplet>
																																		</li>
																																	</ul>
																																</dsp:oparam>
																															</dsp:droplet>
																														</dsp:oparam>
																													</dsp:droplet> </font>
																											</td>
																										</tr>
																										<tr>
																											<td valign="top" style="height: 20px;"
																												class="hideOnSmallScreens" height="20">
																												<font
																												style="font-family: Arial; font-size: 12px; line-height: 25px;">Our
																													Price: <c:choose>
																														<c:when test="${unitSavedAmount gt 0.0}">
																															<dsp:valueof value="${unitSalePrice}"
																																converter="currency" />
																															<%--<br/>(<bbbl:label key="lbl_preview_reg" language="<c:out param='${language}'/>"/> <span><dsp:valueof value="${unitListPrice}" converter="currency"/></span>)--%>
																														</c:when>
																														<c:otherwise>
																															<dsp:valueof value="${unitListPrice}"
																																converter="currency" />
																														</c:otherwise>
																													</c:choose> </font>
																											</td>
																										</tr>
																										<c:if test="${deliverySurcharge eq 0.0 && shippingMethodAvl eq false && commItem.skuDetailVO.ltlItem  }">
																										<tr>
																										<td>		
																										<font style="font-size:12px;line-height:16px;">																								
																											+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: TBD
																										</font>																										
																										</td>
																										</tr>
																										</c:if>
																										
																										<%-- LTL additional info --%>
																										<c:if test="${deliverySurcharge gt 0.0}">
																										<tr>
																											<td valign="top" style="height: 20px; width:175px;" class="showOnSmallScreens fullWidth" height="20">
																											<font style="font-size:12px;line-height:16px;"><%-- + Delivery Surcharge: $250.00 --%> 																											
																												+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/>
																											</font>																											
																											</td>
																										</tr> 
																										</c:if>
																										<c:if test="${deliverySurchargeSaving gt 0.0}">
																										<tr>
																											<td valign="top" style="height: 20px;width:175px;"  class="showOnSmallScreens fullWidth" height="20">
																											<font style="font-size:12px;line-height:15px;color:red;"><%-- - Surcharge Savings ($100.00) --%>
																											
																												- <bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/>: (<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)
																											</font>
																											
																										     </td>																											
																										</tr> 
																										</c:if>	
																										<c:if test="${assemblyFee gt 0.0}">
																										<tr>
																											<td valign="top" style="height: 20px; width:175px;"  class="showOnSmallScreens fullWidth" height="20">
																											<font style="font-size:12px;line-height:15px;"><%-- + Assembly Fee: $40.00 --%>																											
																												+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:<dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/>
																											</font>																											
																											</td>
																										</tr>
																										</c:if>
																										<%-- LTL additional info --%>
																										
																									</tbody>
																								</table>
																							</td>
																						</tr>
																					</tbody>
																				</table></td>
																		</tr>
																	</tbody>
																</table>
															</td>
														</tr>

													</tbody>
												</table>
												</td>
											</tr>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>



				</tbody>
			</table>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<table width="96%" align="center">
				<tr>
					<td>
						<bbbl:textArea key="txt_cart_email_text" language="<c:out param='${language}'/>"></bbbl:textArea>
					</td>
				</tr>
			</table></td>
	</tr>


	<tr>
		<td valign="top" height="15">&nbsp;</td>
	</tr>


	<%-- // End Template Main Content \\ --%>
	<%-- // Begin Template Footer \\ --%>

	<dsp:valueof param="emailTemplateVO.emailFooter" valueishtml="true" />


</body>
	</html>

</dsp:page>
