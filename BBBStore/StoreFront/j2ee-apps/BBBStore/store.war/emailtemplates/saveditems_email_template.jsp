<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/wishlist/BBBWishlistItemCountDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/com/bbb/profile/session/BBBSavedItemsSessionBean" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/com/bbb/commerce/cart/StatusChangeMessageDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	
	<dsp:getvalueof var="serverName" param="serverName" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>

	 <dsp:getvalueof id="applicationId" bean="Site.id" />
	<c:set var="serverPath" value="https://${serverName}" />
	<c:set var="imagePath">
	https:<bbbc:config key="image_host" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
	https:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>

	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Your saved items</title>
</head>
<body leftmargin="0" marginwidth="0" topmargin="0" marginheight="0" bgcolor="#ffffff">

	<%-- // Begin Template Header \\ --%>
	<dsp:valueof param="emailTemplateVO.emailHeader" valueishtml="true" />
	<%-- // End Designer Border \\ --%>
	<%-- // End Template Header \\ --%>
	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
			<dsp:oparam name="output">
				 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			</dsp:oparam>
	 </dsp:droplet>
			<tr>
				<td align="left" valign="top">
					<%-- // Begin Template Body \\ --%>
					<table border="0" cellpadding="0" cellspacing="0" width="100%"
						style="color: #666666; font-family: Arial;">
						<tr>

							<td align="left" valign="top">
							 <table border="0" cellpadding="0" cellspacing="0" width="96%" align="center">
							 <td valign="middle" height="50"><h3 class="resetTextSize" style="color:#666666;font-family:Arial;padding:0;margin:0;font-family:arial;">
								<dsp:getvalueof id="savedItems" param="savedItems" />
							
									Your Saved Items -
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" value="${savedItems}" />
										<dsp:oparam name="outputEnd">
										 <dsp:valueof param="size"/>
										</dsp:oparam>
										</dsp:droplet>
									Item(s)
								</h3></td>
								</table>
								</td>
						</tr>
						<tr>
						
							<td valign="top">
								<table border="0" cellpadding="0" cellspacing="0" width="100%"
									align="left"
									style="border-bottom: 1px solid #E8E8E8; color: #666666; font-family: Arial; font-size: 14px; line-height: 16px;">
									<tbody>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" value="${savedItems}"/>
															<dsp:oparam name="outputStart">
															</dsp:oparam>
															<dsp:oparam name="output">
																<dsp:getvalueof var="skuVO" param="element.skuVO" />
																<dsp:getvalueof var="productId" param="element.prodID" />
																<dsp:getvalueof var="catalogRefId" param="element.skuID" />
																<dsp:getvalueof var="registryId" param="element.registryId" />
																<dsp:setvalue paramvalue="element.giftListId" param="giftlistId" />
																<dsp:getvalueof var="ltlShipMethod" param="element.ltlShipMethod"/>
													             <dsp:getvalueof var="shipMethodUnsupported" param="element.shipMethodUnsupported"/>
																<dsp:getvalueof id="priceMessageVO" value="" />
																<dsp:getvalueof var="personalizationDetails" param="element.personalizationDetails" />
																<dsp:getvalueof var="personalizationOptions" param="element.personalizationOptions" />
																<dsp:getvalueof var="mobileThumbnailImagePath" param="element.mobileThumbnailImagePath"/>
																<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																<dsp:getvalueof var="referenceNumber" param="element.referenceNumber" />
																<dsp:getvalueof var="personalizePrice" param="element.personalizePrice" />
																<dsp:getvalueof var="eximErrorExists" param="element.eximErrorExists"/>
																<dsp:getvalueof var="personalizationType" param="element.personalizationType"/>
																<dsp:getvalueof var="skuIncartFlag" param="element.skuIncartFlag"/>
															<tr>
													<c:choose>
													<c:when
														test="${not empty registryId}">
														<td bgcolor="#FBF8FD"
															style="border-top: 1px solid #a80bd6;">
													</c:when>
													<c:otherwise>
														<td style="border-top: 1px solid #E8E8E8;">
													</c:otherwise>
												</c:choose>
											
												<table id="tblProductDetailsRow" cellspacing="0"
													cellpadding="5" width="100%"
													style="color: #666666; font-family: Arial;">
													<tbody>
														
																	<%--registry item in saved items--%>
																	<c:if test="${not empty registryId}">
																<dsp:droplet name="GetRegistryVODroplet">
																	<dsp:param name="siteId" value="${applicationId}"/>
																	<dsp:param value="${registryId}" name="registryId" />
																	<dsp:param value="true" name="isRegTypeNameReq" />
																	<dsp:oparam name="output">
																		  <dsp:getvalueof var="registryVO" param="registryVO"/>
																		  <tr>
																				<td height="20" bgcolor="#f6f0fb" colspan="2">
																				
																			
																				<p style="margin:0;padding:0;font-size:12px;color:#45165C;"><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>&nbsp;
																				<font style="font-weight:bold;">${registryVO.primaryRegistrant.firstName}
																				<c:if test="${not empty registryVO.coRegistrant.firstName}">
																					&nbsp;&amp;&nbsp;${registryVO.coRegistrant.firstName}
																				</c:if>	
																					<bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
																				</font>&nbsp;
																					<dsp:getvalueof var="eventType" param="registryTypeName"/>
																					<c:if test="${not empty eventType}">
																						<dsp:valueof param="registryTypeName"/>
																					</c:if>
																				&nbsp;<bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></p></td>
																			</tr>
																	</dsp:oparam>
																</dsp:droplet>
																
																
														</c:if>
																<%--image of product--%>
																<tr>
																	<td align="center" valign="top" style="width: 125px;" width="125">
																	<c:choose>
																		<c:when test="${empty referenceNumber}">
																			<c:choose>
																				<c:when
																				test="${empty skuVO.skuImages.mediumImage}">
																				<img
																					src="${imagePath}/_assets/global/images/no_image_available.jpg"
																					class="alignCenter" alt="${skuVO.displayName}"
																					title="${skuVO.displayName}" border="0" width="83"/>
																				</c:when>
																				<c:otherwise>
																				<img
																					src="${scene7Path}/${skuVO.skuImages.mediumImage}"
																					class="alignCenter" alt="${skuVO.displayName}"
																					title="${skuVO.displayName}" border="0" width="83" />
																				</c:otherwise>
																			</c:choose>																		
																		</c:when>
																		<c:otherwise>
																		   	<c:choose>
																		     	<c:when test="${!eximErrorExists && not empty thumbnailImagePath && fn:contains(thumbnailImagePath, 'http')}">
																		     		 <img alt="${skuVO.displayName}" src="${thumbnailImagePath}" class="alignCenter"
																			  title="${skuVO.displayName}" border="0" width="83">
																		     	</c:when>
																		     	<c:when test="${!eximErrorExists && not empty thumbnailImagePath}">
																		     		<img alt="${skuVO.displayName}" src="https:${thumbnailImagePath}" class="alignCenter"
																			  title="${skuVO.displayName}" border="0" width="83">
																		     	</c:when>
																		     	<c:otherwise>
																		     	  <img src="${imagePath}/_assets/global/images/no_image_available.jpg"
																					class="alignCenter" alt="${skuVO.displayName}"
																					title="${skuVO.displayName}" border="0" width="83"/>
																		     	</c:otherwise>
																		    </c:choose>
																		</c:otherwise>																	
																	</c:choose>
																		</td>
																	<%--product displayname--%>
																	<td valign="top">
																		<table cellspacing="0" cellpadding="0" width="40%"
																			align="left">
																			<tbody>
																				<tr>
																					<td align="left">
																					 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                            <dsp:param name="id" value="${productId}" />
                                                                            <dsp:param name="itemDescriptorName" value="product" />
                                                                            <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                                            <dsp:oparam name="output">
                                                                                <c:choose>
                                                                                    <c:when test="${not flagOff}">
                                                                                        <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="#" />
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </dsp:oparam>
                                                                        </dsp:droplet>
																						<dsp:getvalueof var="siteId" param="emailTemplateVO.siteId" />
																							<c:choose>
																							<c:when test="${siteId eq 'BedBathUS'}">
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;"  iclass="resetTextSize">
																										<dsp:valueof value="${skuVO.displayName}" valueishtml="true"/>
																									</dsp:a>
																								</c:when>
																								<c:when test="${siteId eq 'BuyBuyBaby'}">
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #32BCF3; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;"  iclass="resetTextSize">
																										<dsp:valueof value="${skuVO.displayName}" valueishtml="true"/>
																									</dsp:a>
																								</c:when>
																								<c:otherwise>
																									<dsp:a
																										page="${serverPath}${contextPath}${finalUrl}"
																										style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;"  iclass="resetTextSize">
																										<dsp:valueof value="${skuVO.displayName}" valueishtml="true"/>
																									</dsp:a>

																								</c:otherwise>
																							</c:choose>

																						</td>
																				</tr>
																				<tr>
																					<td align="left">
																						<c:if test='${not empty skuVO.color}'>
																							 <bbbl:label key="lbl_item_color" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuVO.color}" valueishtml="true" />
																						</c:if>
																						<c:if test='${not empty skuVO.size}'>
																							 <c:if test='${not empty skuVO.color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuVO.size}" valueishtml="true" />
																						</c:if>
																					</td>
																				</tr>
																				<tr>
																					<td height="25"><font
																						style="font-family: Arial; font-size: 12px; line-height: 25px;">SKU
																							<dsp:valueof value="${catalogRefId}" />
																					</font></td>
																				</tr>
																				<%--BBBH-3982 DSK | Incart Pricing | Displaying discounted price msz for eligible sku's --%>
																				<c:if test="${skuIncartFlag}">
																				<tr>
																				<td height="25"><font
																					style="color:red; font-family: Arial, Helvetica, sans-serif; font-size: 12px; font-weight:bold; line-height: 18px;">
																					<bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" />
																				</font></td>
																				</tr>
																				</c:if>
																		<!-- fix for BPSI-2939 | start | personalized details in saved items email-->
																				<tr><td>
																				<c:if test='${not empty personalizationOptions}'><br>${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}</c:if>
																			<c:if test='${not empty referenceNumber && !eximErrorExists}'>
																			<table width="100%" border="0" cellspacing="0" cellpadding="0">
  																		<tbody>
																			<tr>
																		      <td align="left" valign="top" style="width:20px;">&nbsp;</td>
																		      <td>&nbsp;</td>
																		    </tr>
																				
																				   <tr>
																					<td align="left" valign="top" style="FONT-SIZE: 13px;">
																				    <c:choose>
																					
																					 <c:when test='${not empty personalizePrice && not empty skuVO.personalizationType && skuVO.personalizationType == "PY"}'>
																						 <dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																					 </c:when>
																					 <c:when test='${not empty personalizePrice && not empty skuVO.personalizationType && skuVO.personalizationType == "CR"}'>
																						 <dsp:valueof value="${personalizePrice}" converter="currency"/> 
																						<c:choose>
																							<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																								<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
																							</c:when>
																							<c:otherwise>
																								<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
																							</c:otherwise>
																						</c:choose>
																					 </c:when>
																					 <c:when test='${not empty skuVO.personalizationType && skuVO.personalizationType == "PB"}'>
																						 <bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																					 </c:when>
																					</c:choose>
																					</td>
																			    </tr>
																			   
																			  </tbody>
																			</table>
																			
																			</c:if>
																		<!-- ****************************************fix for BPSI-2939 | end | personalized details in saved items email -->	
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
																										<font style="font-family:arial;font-size:12px;line-height:15px;color:#666;font-weight:bold;" class="resetTextSize">QTY:</font>&nbsp;<font class="resetTextSize" style="font-family:arial;font-size:12px;line-height:15px;color:#666;"><dsp:valueof param="element.quantity" />
																								</font>
																								</td>
																											
																									
																								</tr>
																							</tbody>
																						</table>
																						<table cellspacing="0" cellpadding="0"
																							align="right" width="60%">
																							<tbody>
																								<tr>
																									<td valign="top"><table cellspacing="0"
																											cellpadding="0" class="resetTextSize"
																											style="margin: 0; padding: 0; color: #666; font-family: Arial; font-size: 14px;">
																											<tbody>
																												<tr>
																													<td class="hideOnSmallScreens" valign="top"
																														style="height: 20px; vertical-align: top;"
																														height="20"><font
																														style="font-family: Arial; font-weight: bold;">Total:
																														<c:choose>
                                                                                                                             <c:when test="${!eximErrorExists}">
																															     <dsp:valueof param="element.totalPrice" converter="currency"/>
																															 </c:when>
																															 <c:otherwise>
																															      TBD
																															 </c:otherwise>
																														</c:choose>
																													</font></td>
																												</tr>
																												<c:if test="${skuVO.ltlItem}">
																								   <tr>
																										<td class="hideOnSmallScreens" valign="top"
																														style="height: 20px; vertical-align: top;"
																														height="20"><font
																														style="font-family: Arial; font-size: 12px;">
																														
																			                                             <dsp:getvalueof var="ltlShipMethodDesc" param="element.ltlShipMethodDesc"/>
																				                                         <dsp:getvalueof var="deliverySurcharge" param="element.deliverySurcharge"/>
																				                                         <dsp:getvalueof var="assemblyFee" param="element.assemblyFees"/>
																				                                         <c:choose>
																	                                                       <c:when test="${ltlShipMethod == null or shipMethodUnsupported}">
				                                                                                                            + <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:TBD
			                                                                                                               </c:when>
																				                                             <c:otherwise>
																				                                             + ${ltlShipMethodDesc}:
				                                                                                                            <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
				                                                                                                           <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
				                                                                                                              </c:otherwise>
																				                                              </c:choose>
																				                                      </font></td>
																									</tr>
																									<c:if test="${assemblyFee gt 0.0 && !shipMethodUnsupported}">
																									<tr>
																										<td class="hideOnSmallScreens" valign="top"
																														style="height: 20px; vertical-align: top;"
																														height="20"><font
																														style="font-family: Arial; font-size: 12px;">
																														+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:
																														<dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/>
																				                                      </font></td>
																									</tr>
																									</c:if>
																								</c:if>		
																											</tbody>
																										</table></td>
																								</tr>
																								
																							</tbody>
																						</table></td>
																				</tr>
																			</tbody>
																		</table></td>
																</tr>




																<%--<td valign="top" width="100%" style="padding-left: 10px; padding-right: 0px;">
																									<p class="productTitle"><dsp:valueof param="element.skuVO.displayName"/></p>																										
																									<p class="highlightRed"><dsp:valueof param="element.prevPrice"/></p>
																									<p class="quantity"><strong>Quanitity: </strong><dsp:valueof param="element.quantity"/></p>
																								</td>--%>
														</tbody>
												</table> <%-- // End Module: Standard Content \\ --%></td>
										</tr>
															</dsp:oparam>
														</dsp:droplet>
													
									</tbody>
								</table></td>
							<%-- // End Standard Content \\  --%>
						</tr>
						<tr>
							<td valign="top">
							<table width="96%" align="center">
							<tr>
							<td>
							<bbbl:textArea key="txt_saveforlater_email_text" language="<c:out param='${language}'/>"></bbbl:textArea>
									</td>
									</tr>
									</table>
									</td>
						</tr>
						<tr>
							<td valign="top" height="15">&nbsp;</td>
						</tr>
					</table></td>
			</tr>

			<%-- // Begin Template Footer \\ --%>
			<dsp:valueof param="emailTemplateVO.emailFooter" valueishtml="true" />
			<%-- // End Template Footer \\ --%>
		

</body>
	</html>
</dsp:page>