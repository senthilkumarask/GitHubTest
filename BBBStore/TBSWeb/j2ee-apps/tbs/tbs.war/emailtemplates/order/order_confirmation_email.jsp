<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/> 
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
	 <dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" param="order"/>
            <dsp:oparam name="output">
               <dsp:getvalueof param="isPackHold" var="isPackHold"/>     
           </dsp:oparam>
     </dsp:droplet>

	 <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
			       <dsp:oparam name="output">
				      <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			       </dsp:oparam>
		      </dsp:droplet>

     <dsp:getvalueof var="commerceItems" param="order.commerceItems" />
	<c:set var="cmo" value="false" />   
    <c:forEach items="${commerceItems}" var="commerceItem">
        <c:if test="${commerceItem.commerceItemClassType eq 'tbsCommerceItem' and not empty commerceItem.CMO}">
        	<dsp:getvalueof var="cmo" value="${commerceItem.CMO}"/>
         </c:if>
     </c:forEach>
	
	<c:if test="${isPackHold eq true}"> 
		<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet">
	 	  	<dsp:param name="order" param="order"/>
	 	  	<dsp:param name="shippingGroup" param="order.shippingGroups"/>
	 	   	<dsp:param name="isPackHold" value="${true}"/>
	 	   	<dsp:oparam name="beddingKit">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="true"/>
           	</dsp:oparam>
		   	<dsp:oparam name="weblinkOrder">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="weblinkOrder" value="true"/>
           	</dsp:oparam>
           	<dsp:oparam name="notBeddingKit">
           		<c:set var="beddingKit" value="false"/>
           	</dsp:oparam>
	</dsp:droplet>
	</c:if>
	
	
	<c:if test="${beddingShipAddrVO != null && isPackHold eq true }">
		<dsp:getvalueof var="collegeName" value="${beddingShipAddrVO.collegeName}"/>
	</c:if>


<dsp:getvalueof var="serverName" param="hostUrl"/>
<dsp:getvalueof var="contextPath" value="/store"/>

<c:set var="serverPath" value="${serverName}"/>
<c:set var="scene7Path">
    http:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<c:set var="imagePath">
	http:<bbbc:config key="image_host" configName="ThirdPartyURLs" />
</c:set>
<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber" />
<dsp:getvalueof var="bopusOrderNumber"  param="order.bopusOrderNumber" />

<dsp:getvalueof var="firstName"  param="order.billingAddress.firstName" />
<dsp:getvalueof var="lastName"  param="order.billingAddress.lastName" />
<dsp:getvalueof var="submittedDate"  param="order.submittedDate" />
<dsp:getvalueof var="currentSiteId" param="emailTemplateVO.siteId" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>
	<dsp:valueof param="emailTemplateVO.siteId" /> Order Confirmation
</title>
</head>
<body bgcolor="#ffffff" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">

	<dsp:valueof param="emailHeader" valueishtml="true"/>

	<%-- Main Content Starts--%>
  <%-- // Begin Template Main Content \\ --%>
    <tr>
        <td align="left" valign="top">
        
        <table id="templateMainContent" border="0" cellpadding="0" cellspacing="0" width="100%">
		<tbody>
		<%--Online pick up in store-- starts--%>
		
		<c:if test="${cmo}">
			<c:set var="bopusOrderNumber"><dsp:valueof param="order.onlineOrderNumber"/> </c:set>
			<c:set var="onlineOrderNumber"></c:set>
		</c:if>
		<c:if test="${not empty bopusOrderNumber}">
		<tr>
			<td valign="top" width="100%">
				<table id="tblOrderMessage" border="0" cellpadding="0" cellspacing="0" width="98%" align="center">
				<tbody><tr>
					<td valign="top">
						<%-- // Begin Template Order confirmation message table \\ --%>
						<table border="0" cellpadding="2" cellspacing="0" width="100%">
						<tbody>
                         <tr>
                            <td height="15">
                            </td>
              			</tr>
						<tr>
							<td valign="top" class="orderGreeting">
								<h3 class="resetTextSize" style="margin:0;padding:0;color:#666; font-size: 14px; line-height: 16px;font-family:Arial;"><jsp:useBean id="placeHolderOrderEmail" class="java.util.HashMap" scope="request"/>
															<c:set target="${placeHolderOrderEmail}" property="userFirstName" value="${firstName}"/>
															<c:set target="${placeHolderOrderEmail}" property="userLastName" value="${lastName}"/>
															<bbbl:label key="lbl_email_orderconfirm_thankyoutext" language="${language}" placeHolderMap="${placeHolderOrderEmail}"/></h3>
							</td>
						</tr>
                        <tr>
                            <td height="5">
                            </td>
              			</tr>
						<tr>
							<td width="50%" valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;line-height:16px;"><bbbl:label key="lbl_order_processing_message" language="${language}"/></p>
									</td>
								</tr>
                                <tr>
                                    <td height="15">
                                    </td>
              					</tr>
								<tr>
									<td>
									<%-- // Begin Template Order details short summary  \\ --%>
										<table cellpadding="1" cellspacing="0" border="0" style="color:#666666;font-family:Arial;">
											<tbody>
                                        		<tr class="order_fields">
                                                <td>
                                                    <font style="font-size:12px; font-weight: bold;"><bbbl:label key="lbl_email_orderconfirm_orderdate" language="${language}"/></font>&nbsp;<font style="font-size:12px; font-weight:normal;">
                                                    <c:choose>
														<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
															<dsp:valueof value="${submittedDate}" date="dd/MM/yyyy"/>
														</c:when>
														<c:otherwise>
															<dsp:valueof value="${submittedDate}" date="MM/dd/yyyy"/>
														</c:otherwise>
													</c:choose>
													</font>	
                                                </td>
                                            </tr>
											<c:if test="${not empty bopusOrderNumber}">
																		
                                            <tr class="order_fields">
                                                <td>
                                                    <font style="font-size:12px; font-weight: bold;"><bbbl:label key="lbl_checkoutconfirmation_instorepickup_order" language="${language}"/></font>&nbsp;<font style="font-size:12px; font-weight:normal;">${bopusOrderNumber}</font>
                                                </td>
                                            </tr>
											</c:if>
											<c:if test="${not empty onlineOrderNumber}">
											<tr class="order_fields">
                                                <td>
												
                                                    <font style="font-size:12px; font-weight: bold;">
																			<bbbl:label key="lbl_checkoutconfirmation_delivery_order" language="${language}"/> 
                                            </font>&nbsp;<font style="font-size:12px; font-weight:normal;">${onlineOrderNumber}
																	</font>
                                                </td>
                                            </tr>
													
											</c:if>
                                            </tbody>
                                            </table>
                                            <%-- // End Template Order details short summary  \\ --%>
                                        </td>							
                            </tr>
                            </tbody></table>
                            <%-- // End Template Order confirmation message table \\ --%>
                        </td>
                    </tr>
                    <tr>
                        <td height="20">&nbsp;                            
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="padLeft10" style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">
		                    <c:choose>
		                    	<c:when test="${cmo}">
		                    		<bbbl:label key="lbl_cmo_order_details" language="${language}"/>
		                    	</c:when>
		                    	<c:otherwise>
		                    		<bbbl:label key="lbl_store_pickup_order_details" language="${language}"/>
		                    	</c:otherwise>
		                    </c:choose>
                        </td>
                    </tr>
                    </tbody></table>
                    <%-- // Begin Template Order details table \\ --%>
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="order.shippingGroups" />
								<dsp:param name="elementName" value="shippingGroup" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
									<dsp:droplet name="/atg/dynamo/droplet/Compare">
										<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
										<dsp:param name="obj2" value="storePickupShippingGroup"/>
										<dsp:oparam name="equal">
											<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
													<c:if test="${commerceItemRelationshipCount gt 0}">	
													<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
															 <dsp:param name="priceObject" value="${shippingGroup}" />
															<dsp:param name="orderObject" param="order" />
															<dsp:oparam name="output">
															<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
																  <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
																  <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
																  <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
																  <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
																  <dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/>
																  <dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
																  <dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shippingGroupItemsTotal"/>
																  <dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
																  <dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>	
																  <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal" />
																  <dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage" />
																  <dsp:getvalueof var="shippingAdjustments" param="priceInfoVO.shippingAdjustments" />
																  <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
																  <dsp:getvalueof var="shippingSurchargeSavings" param="priceInfoVO.surchargeSavings"/>
															
                    <table id="templateOrderDetails" border="0" cellpadding="2" cellspacing="0" width="100%">
                    <tbody>
                    
					
                    <tr>
                        <td valign="top">
							
                            <table border="0" cellpadding="4" cellspacing="0" width="100%" align="left" style="border-bottom: 1px solid #E8E8E8;color:#666666;font-family:Arial;font-size:12px;line-height:16px;">						
                            <tbody>
                          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
								<dsp:param name="elementName" value="commerceItemRelationship" />
									<dsp:param name="sortProperties" value="+shippingGroup.registryId" />
									<dsp:oparam name="output">
									<dsp:getvalueof param="commerceItemRelationship.commerceItem.registryId" var="registratantId"/>
									<dsp:getvalueof param="order.registryMap.${registratantId}" var="registratantVO"/>
									<dsp:getvalueof param="commerceItemRelationship.commerceItem.repositoryItem.type" var="cItemType"/>
	               					<c:if test="${cItemType eq  'tbsCommerceItem'}">			
                        <%-- Normal Products Row change --%>
                        <tr>
						<%--for registry products--%>
																				
						
														<c:choose>
													<c:when
														test="${not empty registratantId}">
														<td bgcolor="#FBF8FD" style="border-top: 1px solid #a80bd6;">
													</c:when>
													<c:otherwise>
														<td style=" border-top: 1px solid #E8E8E8; ">
													</c:otherwise>
												</c:choose>
													
                                
                                        <table id="tblProductDetailsRow" cellspacing="0" cellpadding="4" width="98%" style="color:#666666;font-family:Arial;">
                                        <tbody><tr>
										
										<%--registry item starts--%>
										<c:if test="${not empty registratantId}">
									
									
										<bbbl:label key="lbl_cart_registry_from_text" language="${language}"/> ${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName} 
											<c:if test="${registratantVO.coRegistrantFirstName ne null}">
												&nbsp;&amp;&nbsp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
											</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
											<dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/><bbbl:label key="lbl_cart_registry_text" language="${language}"/>
										
										
								</c:if>
										
									<%--registry item ends--%>	
										
										<%--product image--%>
                                           <td align="left" valign="top" width="105">
							<dsp:getvalueof var="smallImageURL" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.smallImage"/>
							<c:choose>
								<c:when test="${empty smallImageURL}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="83"  />
										</c:when>
										<c:otherwise>
											<img src="${scene7Path}/${smallImageURL}" style="max-width:63px;" width="83" />
											</c:otherwise>
												</c:choose>
                                               
                                            </td>
                                            <td valign="top" style=" width: 85%;" width="85%">
											<%--product title--%>
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="commerceItemRelationship.commerceItem.repositoryItem.productId" />
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												</dsp:oparam>
												</dsp:droplet>																												
												<dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem"/>
												<dsp:getvalueof var="skuColor" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.color"/>
												<dsp:getvalueof var="skuSize" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.size"/>
												<table cellspacing="0" cellpadding="0" width="40%" align="left">
												<tbody>
												<tr>
													<td align="left">
													<dsp:getvalueof var="siteId" param="emailTemplateVO.siteId" />
												
												<p>
													<c:choose>
											<c:when test="${siteId eq 'TBS_BedBathUS'}">
													<dsp:a
														page="${serverPath}${contextPath}${finalUrl}"
														style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
														<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
													</dsp:a>
												</c:when>
												<c:when test="${siteId eq 'TBS_BuyBuyBaby'}">
													<dsp:a
														page="${serverPath}${contextPath}${finalUrl}"
														style="margin: 0; float: left; color: #32BCF3; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
														<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
													</dsp:a>
												</c:when>
												<c:otherwise>
													<dsp:a
														page="${serverPath}${contextPath}${finalUrl}"
														style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
														<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
													</dsp:a>

												</c:otherwise>
											</c:choose>
											</p>
											<p style="font-size:12px; clear:both;" class="resetTextSize">
												<c:if test='${not empty skuColor}'>
													 <bbbl:label key="lbl_item_color" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
												</c:if>
												<c:if test='${not empty skuSize}'>
													<c:if test='${not empty skuColor}'>&nbsp;|&nbsp;</c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" />
												</c:if>
											</p>
											</td>
                                                 </tr>
                                                      <tr>
                                                                    <td height="20">
                                                                        <p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;line-height:15px;">SKU <dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.id"/></p>
                                                                    </td>
                                                                </tr>
                                                   </tbody>
												</table>
                                                            
                                                         <%--product prices--%>               
                                                                      
                                                                    
                                                           
                                                            <table cellspacing="0" cellpadding="0" width="55%" align="right">
                                                                <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <table cellspacing="0" cellpadding="0" width="20%" align="left" height="20">
                                                                            <tbody><tr>
                                                                               <td class="alignLeft" align="center" valign="top"><font style="font-family:arial;font-size:12px;line-height:15px;color:#666;font-weight:bold;" class="resetTextSize">QTY:</font>&nbsp;<font class="resetTextSize" style="font-family:arial;font-size:12px;line-height:15px;color:#666;"><dsp:valueof param="commerceItemRelationship.quantity"/>		</font>             </td>
                                                                            </tr>															
                                                                        </tbody>
                                                                        </table>
                                                                          <%--price column starts--%> 
                                                                        <table cellspacing="0" cellpadding="0" align="right" width="75%">
                                                                            <tbody><tr>
                                                                                <td valign="top">
																				<%--total--%>
																		<p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:14px;"><font class="hideOnSmallScreens" style="font-weight: bold;">
																		<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
																							 <dsp:param name="priceObject" param="commerceItemRelationship.commerceItem" />
																							 <dsp:param name="profile" bean="Profile"/>
																							 <dsp:oparam name="output">
 																								<dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
																								<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
																								<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
																								 <dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
								   																 <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
								   																 <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
																								</dsp:oparam>
																							</dsp:droplet>
																		
																		<dsp:getvalueof var="commerceItemRelationshipTemp" param="commerceItemRelationship"/>
																		 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
																			<dsp:param name="priceObject" param="commerceItemRelationship" />
																			<dsp:param name="profile" bean="Profile"/>
																			<dsp:oparam name="output">
																			<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>	
																				  Total:<dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/>
																				  </dsp:oparam>
																				  </dsp:droplet>
																									
																								
																  <br></font> </p>
																  <%--your price & our price--%>
								<dsp:getvalueof param="commerceItemRelationship.commerceItem.TBSItemInfo" var="tbsItemInfo"/>
								<dsp:getvalueof param="commerceItemRelationship.commerceItem" var="commerceItem"/>
							   <table cellspacing="0" cellpadding="0" width="100%" style="text-align:right;" class="alignLeft">
								 <tbody>
								 <tr>
								 <td valign="top" style="height:40px; vertical-align: middle;color:#666;">
										 <font style="font-family:Arial;font-size:12px;line-height:15px;" class="boldInSmallScreens">Your Price:</font>
									 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
										<dsp:param name="priceObject" value="${commerceItem}" />
										<dsp:param name="profile" bean="Profile"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
											<dsp:getvalueof var="unitSavedAmountTemp" param="priceInfoVO.unitSavedAmount"/>
											<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
											<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
											<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="priceInfoVO.priceBeans" />
													<dsp:param name="elementName" value="unitPriceBean" />		
													<dsp:oparam name="output">
													
														<dsp:getvalueof param="unitPriceBean.pricingModels" var="promos"/>
														<dsp:getvalueof param="unitPriceBean.quantity" var="promoqty"/>
														<c:if test="${not empty promos}">
															<font style="font-family:Arial;font-size:12px;line-height: 15px;color:#666;">
																<dsp:valueof param="unitPriceBean.quantity"/> &nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/></font>
															<br/>
															<font style="font-family:Arial;font-size:11px;color:red;">COUPON APPLIED:<br/>
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="unitPriceBean.pricingModels" />
																	<dsp:param name="elementName" value="pricingModel" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="appliedPromoId" param="pricingModel.id"/>
																		<c:if test="${not empty appliedPromoId}">
																			<dsp:getvalueof var="promoId" value="${appliedPromoId}"/>
																			<dsp:valueof param="pricingModel.displayName"/><br/>
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
															</font>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
												
												<c:choose>
													<c:when test="${undiscountedItemsCount gt 0 && unitSalePrice gt 0}">
														<font style="font-family:Arial;font-size:12px;line-height: 15px;color:#666;">
															<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof value="${unitSalePrice}" converter="currency" /><br/>
														</font>
													</c:when>
													<c:when test="${undiscountedItemsCount gt 0 && unitListPrice gt 0}">
														<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof value="${unitListPrice}" converter="currency" /><br/>
													</c:when>
												</c:choose>
												
												<c:if test="${not empty tbsItemInfo && tbsItemInfo.overridePrice > 0 }"></br>
													<span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/> &nbsp;
														<dsp:valueof value="${tbsItemInfo.overridePrice}" converter="currency"/>
													</span>
												</c:if>
												
												<c:if test="${priceInfoVO.unitSavedAmount gt '0.0' || (not empty tbsItemInfo && tbsItemInfo.overridePrice > 0)}">
													</br>
													<font style="font-family:Arial;font-size:11px;color:red;">
													<bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
													<dsp:valueof value="${priceInfoVO.unitSavedAmount * promoqty}" converter="currency"/>
													</font>
												</c:if>																												 																										 
											</dsp:oparam>
										</dsp:droplet>
										
										
										<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
											<dsp:param name="priceObject" param="shippingGroup" />
											<dsp:param name="orderObject" param="order" />
											<dsp:param name="commerceIdForEcoFee" param="commerceItemRelationship.commerceItem.id" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="ecoFeeAmountTemp" param="ecoFeeAmount" />
												<c:if test="${ecoFeeAmountTemp gt '0.0'}">						
												
												<bbbl:label key="lbl_eco_fee" language="${language}"/>: 
												<dsp:valueof param="ecoFeeAmount" converter="currency"/>
												
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
									</td>
								</tr>
								<tr> 
								<td valign="top">
									<font class="hideOnSmallScreens" style="font-family:Arial;color:#666;font-size:12px;line-height:15px;">Our Price:
								<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
										<dsp:param name="priceObject" param="commerceItemRelationship.commerceItem" />
										<dsp:param name="profile" bean="Profile"/>
										<dsp:param name="temp" value="true"/>
										<dsp:oparam name="output">
										<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
										<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
										<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
										<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
										<dsp:valueof param="priceInfoVO.unitListPrice" converter="currency"/>
																																							 
												</dsp:oparam>
												</dsp:droplet>
										</font> </td>
								</tr>                                                        
								<%-- LTL additional info --%>
								<c:if test="${deliverySurcharge gt 0.0}">
									<tr>
	                                    <td valign="top" style="height: 20px; width:105px;"class="showOnSmallScreens fullWidth" height="20">
	                                    <font style="line-height:20px;">+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></font></td>
	                                </tr>
                                </c:if>
                                <c:if test="${deliverySurchargeSaving gt 0.0}"> 
									<tr>
	                                    <td valign="top" style="height: 20px; width:105px;" class="showOnSmallScreens fullWidth" height="20">
	                                    <font style="line-height:20px;color:red;">- <bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/></font></td>
	                                </tr> 
	                            </c:if>
								<c:if test="${assemblyFee gt 0.0}">
									<tr>
	                                    <td valign="top" style="height: 20px; width:105px;" class="showOnSmallScreens fullWidth" height="20">
	                                    <font style="line-height:20px;">+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/></font></td>
	                                    
	                                </tr>
	                            </c:if> 
                                <%-- LTL additional info --%>                                                      
								</tbody></table>
										   
										   <p></p>																		
									</td>
							</tr>
							</tbody>
							</table>
                                                             <%--price column ends--%> 
                                                                </td>
                                                            </tr>
                                                            </tbody>
                                                            </table>																			
                                        </td>
                                    </tr>								
                                    </tbody></table>
                                </td>
						</tr>
                        <%--Product Row change --%>
						 </c:if>
                        </dsp:oparam>
					 </dsp:droplet>					
                        </tbody>
                     </table>
					 
					 </td>
					 </tr>
					 </tbody>
					 </table>
					 
                     
                <%-- <c:if test="${not empty bopusOrderNumber}"> --%>
                <c:if test="${not cmo}">
                    <table id="instorePickupMessage" border="0" cellpadding="0" cellspacing="0" width="100%">
                        <tbody>
                            <tr>
                                <td valign="top" style="padding-top: 20px; padding-bottom: 20px;">
                                    <table cellpadding="0" cellspacing="0" width="100%" align="left" style=" background-color: #FEF9DC; border: 1px solid #FAD611; padding:10px">
                                        <tr>
                                            <td style="padding-top:10px; padding-bottom: 10px;">
                                                <h6 style="font-family: arial; font-size: 15px; padding: 0; margin: 0;"><bbbl:label key="lbl_items_for_store_pickup_title" language="<c:out param='${language}'/>"/></h6>
                                                <p style="font-family: arial; font-size: 12px; margin-top: 10px; margin-bottom: 0; margin-left: 0; margin-right: 0; padding: 0;"><bbbt:textArea key="txt_items_for_store_pickup_preview" language="<c:out param='${language}'/>"/></p>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </c:if>
                    <%-- </c:if> --%>
					 
				<%-- // End Template Order details table \\ --%>
				<%-- // Begin Template Order Summary table \\ --%>
				<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
															<dsp:param name="priceObject" param="shippingGroup" />
															<dsp:param name="orderObject" param="order" />
															<dsp:oparam name="output">
																  <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
																  <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
																  <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
																  <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
																  <dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/>
																  <dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingcountyLevelTax"/>
																  <dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
																  <dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
																  <dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shippingGroupItemsTotal"/>
																  <dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
																  <dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>	
																  <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal" />
																  <dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage" />
																  <dsp:getvalueof var="shippingAdjustments" param="priceInfoVO.shippingAdjustments" />
																  <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
																  <dsp:getvalueof var="shippingSurchargeSavings" param="priceInfoVO.surchargeSavings"/>
																  <dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>
																  <dsp:getvalueof var="shipMethod" param="shippingMethodDescription"/>
															</dsp:oparam>
														</dsp:droplet>	
				
				<table id="tblGroupsOrderInfo" cellspacing="0" cellpadding="0" width="100%" style="color:#666666;font-family:Arial;float:left;font-size:14px;border-top: 1px outset #fff;" align="left" bgcolor="#f7f7f7">
				<tbody><tr>
					<td>
						<table id="tblOrderSummary" align="left" cellspacing="0" cellpadding="4" bgcolor="#f7f7f7" width="300" style="float: left;color:#666666;font-family:Arial;font-size:14px;margin-left: 25px;">
						<tbody><tr>
							<td colspan="2" style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Order Summary</td>
						</tr>
                        <%--number of items--%>
						<tr>
							<td width="180">
								<font style="font-weight: bold;font-size:14px; "><dsp:valueof value="${itemCount}"/> <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-weight: bold;font-size:14px; "><dsp:valueof value="${shippingGroupItemsTotal}" converter="currency"/></font>
							</td>
						</tr>
						<%--shipping--%>
						
						<tr>
					         <td width="180">
					          <font style="font-size:14px; color:red;"><bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/> &nbsp;
					          </font>
					          
					         </td>
					         <td width="90" align="left">
					            <font style="font-size:14px; color:red;"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></font>
					         </td>
					     </tr>
						
						<%--tax--%>
						<c:if test="${totalSurcharge gt 0.0}">
						<tr>
							<td width="180">
								<font style="font-size:14px; "><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								<dsp:valueof value="${totalSurcharge}" converter="currency"/></font>
							</td>
						</tr>                                               
					</c:if>
					<c:if test="${shippingSurchargeSavings gt 0.0}">
						<tr>
							<td width="180">
								<font style="font-size:14px; "><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								(<dsp:valueof value="${shippingSurchargeSavings}" converter="currency"/>)</font>
							</td>
						</tr>															
					</c:if>
					<%----tax code fix --%>
					<c:choose>
						<c:when test="${currentSiteId ne 'TBS_BedBathCanada'}">
						<tr>
							<td width="180">
								<font style="font-size:14px; ">
								<bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								<p>	<dsp:valueof value="${shippingLevelTax}" converter="currency"/></font>
							</td>
						</tr>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${countyLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${stateLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									
									<c:set var="totalStateTax" value="${totalStateTax+countyLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+stateLevelTax}" scope="request" />
								</c:when>
								<c:otherwise>
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${stateLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${countyLevelTax}" converter="currency"/></font>
										</td>
									</tr>								
									<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+countyLevelTax}" scope="request" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<%----tax code fix --%>
						<%--totall--%>
						<c:if test="${totalAmount gt 0.0}">
							<tr>
								<td colspan="2">
								<table width="100%" align="left" cellspacing="0" cellpadding="0" style="float:left;color:#666666;font-family:Arial;border-top: 1px outset #e6e6e6;font-weight: bold;font-size:18px;">
								 <tbody><tr>
								<td height="8" colspan="2" style="border-top: 1px outset #fff;"></td>
							</tr><tr>

													<td width="180">
														<bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>
													</td>
												   <td width="90" align="left">
													  <dsp:valueof value="${totalAmount}" converter="currency"/>
													</td>
												</tr>                                        
											</tbody></table>
												</td>						
											</tr>     
						</c:if>
						<c:if test="${totalSavedAmount gt 0.0}">
	                  		 <tr>
								<td colspan="2">
								<table width="100%" align="left" cellspacing="0" cellpadding="0" style="float:left;color:red;font-family:Arial;border-top: 1px outset #e6e6e6;font-weight: bold;font-size:12px;">
								 <tbody><tr>
								<td height="8" colspan="2" style="border-top: 1px outset #fff;"></td>
							</tr><tr>

													<td width="180">
														<bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>
													</td>
												   <td width="90" align="left">
													  <dsp:valueof value="${totalSavedAmount}" converter="currency"/>
													</td>
												</tr>                                        
											</tbody></table>
												</td>						
											</tr>   
	               		</c:if>							
						</tbody></table>
						<c:if test="${not cmo}">
							<table id="tblBillingInfo" class="hideOnSmallScreens" align="right" cellspacing="0" cellpadding="4" width="200" style="color:#666666;font-family:Arial; margin-right: 10px;">
							<tbody><tr>
								<td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Important:</td>
							</tr>
							<tr>
								<td align="left">
                            <p style="font-family: arial; font-size: 11px;"><bbbt:textArea key="txt_ropis_confirmation_info" language="<c:out param='${language}'/>"/></p>
                            <p style="font-family: arial; font-size: 11px;"><bbbt:textArea key="txt_ropis_confirmation_info_important" language="<c:out param='${language}'/>"/></p>
								</td>
							</tr>                      
							</tbody></table>
						</c:if>
					     <%--Curly border at bottom --Start --%>
                          <table class="hideOnSmallScreens" cellspacing="0" cellpadding="0" width="600" bgcolor="white" style="clear:both">						
        <tbody><tr><td align="left">                
        <img src="${serverPath}/_assets/emailtemplates/bbbeyond/images/lightgrey_curlyborder.gif" width="600" height="14" border="0" class="hideOnSmallScreens" align="left">    
    </td>
						</tr>                      
						</tbody></table>                        
                        <%--Curly border at bottom --End --%>
                        
					</td>
				</tr>
				</tbody></table>
				
						<table class="hideOnSmallScreens" cellspacing="0" cellpadding="0" width="100%" bgcolor="white" align="left">
                            <tr>
                                <td width="25"></td>
                                <td style="font-family: arial; font-size: 12px; line-height: 20px; height: 40px;">*This amount is due at the time of purchase in the store</td>
                            </tr>
                        </table>
                    <table cellspacing="0" cellpadding="0" width="100%" bgcolor="white" align="left" style="clear:both;"><tbody><tr><td>
                        <table class="storeLocation" cellspacing="0" cellpadding="0" width="100%" bgcolor="white" align="left" style="clear:both;">
                            <tr>
                                <td width="20"></td>
                                <td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Store Location</td>
                            </tr>
                        </table>
						<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
						<dsp:param name="storeId" param="shippingGroup.storeId" />
						<dsp:oparam name="output">
                        <table cellspacing="0" cellpadding="0" width="100%" bgcolor="white" align="left" style="clear:both;"><tbody><tr><td>
                        <table id="storeDetail" cellspacing="0" cellpadding="0" width="200" bgcolor="white" align="left" style="margin-left: 20px; margin-top: 10px; clear:both;">
                            <tr>
                                <td style="font-family: arial; font-size: 12px; line-height: 16px; height: 25px;"><strong><dsp:valueof param="StoreDetails.storeName"/></strong> <br/>
                                <dsp:valueof param="StoreDetails.address"/> <br/>
                                <dsp:valueof param="StoreDetails.city"/><br/>
                                <dsp:valueof param="StoreDetails.state"/>&nbsp;<dsp:valueof param="StoreDetails.postalCode"/><br/>
								<dsp:valueof param="StoreDetails.storePhone"/>
                               </td>
                             </tr>
							 <dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
                             <tr>
                             	<td height="35" valign="middle"><dsp:a page="${serverPath}${contextPath}/selfservice/FindStore?flashEnabled=true" style="color: #273691; font-size: 12px; font-weight:bold; font-family: arial; text-decoration: none;">Map &amp; Directions </dsp:a></td>
                             </tr>
                        </table>                        
                        <table id="storeDetailTimings" cellspacing="0" cellpadding="0" width="240" bgcolor="white" align="left" style="margin-top: 10px;">
                            <tr>
                               <td align="left"style="font-family: arial; font-size: 12px; line-height: 16px; font-weight: bold; color:#666;padding:0;margin:0;">
									<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										${item}
									</c:when>
									<c:otherwise>
										${item}
									</c:otherwise>				
								</c:choose>
								</c:forTokens>
							   
							   </td>
                             </tr>
                             <tr>
                             	
                             	<td align="left" style="font-family: arial; font-size: 12px; line-height: 16px; font-weight: bold; color:#666;padding:0;margin:0;">
								<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item" varStatus="status">
										<c:choose>
											<c:when test="${status.count == 1}">
												${item}
											</c:when>
											<c:otherwise>
												${item}	
											</c:otherwise>				
										</c:choose>
										</c:forTokens>
								
								
								</td>
                             </tr>
                             <tr>
                             	
                             	<td align="left" style="font-family: arial; font-size: 12px; line-height: 16px; font-weight: bold; color:#666;padding:0;margin:0;">
								<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item" varStatus="status">
									<c:choose>
										<c:when test="${status.count == 1}">
											${item}
										</c:when>
										<c:otherwise>
											${item}
										</c:otherwise>				
									</c:choose>
									</c:forTokens>
								
								
								
								
								</td>
                             </tr>
                        </table>
                       </td></tr></tbody></table>
						</dsp:oparam>
						</dsp:droplet>
						
						
						
						<c:if test="${not cmo}">
	                     	<table id="tblBillingInfo2" class="showOnSmallScreens" width="98%"  align="left" cellspacing="0" cellpadding="0" style="clear:both; color:#666666;font-family:Arial; display: none; margin-top: 20px;">
								<tbody><tr>
									<td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Important:</td>
								</tr>
								<tr>
									<td align="left">
                            <p style="font-family: arial; font-size: 11px;"><bbbt:textArea key="txt_ropis_confirmation_info" language="<c:out param='${language}'/>"/></p>
                            <p style="font-family: arial; font-size: 11px;"><bbbt:textArea key="txt_ropis_confirmation_info_important" language="<c:out param='${language}'/>"/></p>
									</td>
								</tr>
								</tbody>
	                    	</table>
                    	</c:if>
                    <table cellspacing="0" cellpadding="0" width="98%" bgcolor="white" align="center" style="clear:both;margin-top: 10px;">
                        <tr>
                            <td style="border-top: 1px solid #e8e8e8" height="35">&nbsp;</td>
                        </tr>
                    </table>
                    </td></tr></tbody></table>
                    </dsp:oparam>
					</dsp:droplet>
					</c:if>
					 </dsp:oparam>
					 </dsp:droplet>
					 </dsp:oparam>
					 </dsp:droplet>
				<%-- // End Template Order Summary table \\ --%>
					</td>
		</tr>
		</c:if>
		<%--online pickup in store ends--%>
		<%--normal +registry products--%>
        <tr>
			<td valign="top" width="100%">
				<table id="tblOrderMessage" border="0" cellpadding="0" cellspacing="0" width="98%" align="center">
				<tbody><tr>
					<td valign="top">
						<%-- // Begin Template Order confirmation message table \\ --%>
						<table border="0" cellpadding="2" cellspacing="0" width="100%">
						<tbody>
                         <tr>
                            <td height="15">
                            </td>
              			</tr>
						<c:if test="${empty bopusOrderNumber}">
						<tr>
							<td valign="top" class="orderGreeting">
								<h3 class="resetTextSize" style="margin:0;padding:0;color:#666; font-size: 14px; line-height: 16px;font-family:Arial;"><jsp:useBean id="placeHolderOrderEmail" class="java.util.HashMap" scope="request"/>
															<c:set target="${placeHolderOrderEmail}" property="userFirstName" value="${firstName}"/>
															<c:set target="${placeHolderOrderEmail}" property="userLastName" value="${lastName}"/>
															<bbbl:label key="lbl_email_orderconfirm_thankyoutext" language="${language}" placeHolderMap="${placeHolderOrderEmail}"/></h3>
							</td>
						</tr>
						
                        <tr>
                            <td height="5">
                            </td>
              			</tr>
						<tr>
							<td width="50%" valign="top"><p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;line-height:16px;"><bbbl:label key="lbl_order_processing_message" language="${language}"/></p>
									</td>
								</tr>
								</c:if>
                                <tr>
                                    <td height="15">
                                    </td>
              					</tr>
								<tr>
									<td>
									<%-- // Begin Template Order details short summary  \\ --%>
										<table cellpadding="1" cellspacing="0" border="0" style="color:#666666;font-family:Arial;">
											<tbody>
											<c:if test="${not empty onlineOrderNumber}">
                                       		<tr class="order_fields">
                                                <td>
                                                    <font style="font-size:12px; font-weight: bold;"><bbbl:label key="lbl_email_orderconfirm_orderdate" language="${language}"/></font>&nbsp;<font style="font-size:12px; font-weight:normal;">
                                                    <c:choose>
														<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
															<dsp:valueof value="${submittedDate}" date="dd/MM/yyyy"/>
														</c:when>
														<c:otherwise>
															<dsp:valueof value="${submittedDate}" date="MM/dd/yyyy"/>
														</c:otherwise>
													</c:choose>
													</font>	
                                                </td>
                                            </tr>
                                            </c:if>
											<c:if test="${(not empty bopusOrderNumber) && (not empty onlineOrderNumber)}">
																		
                                            <tr class="order_fields">
                                                <td>
                                                    <font style="font-size:12px; font-weight: bold;"><bbbl:label key="lbl_checkoutconfirmation_instorepickup_order" language="${language}"/></font>&nbsp;<font style="font-size:12px; font-weight:normal;">${bopusOrderNumber}</font>
                                                </td>
                                            </tr>
											</c:if>
											<c:if test="${not empty onlineOrderNumber}">
											<tr class="order_fields">
                                                <td>
												
                                                    <font style="font-size:12px; font-weight: bold;">
																			<bbbl:label key="lbl_checkoutconfirmation_delivery_order" language="${language}"/> 
                                            </font>&nbsp;<font style="font-size:12px; font-weight:normal;">${onlineOrderNumber}
																	</font>
                                                </td>
                                            </tr>
													
											</c:if>
                                            </tbody>
                                            </table>
                                            <%-- // End Template Order details short summary  \\ --%>
                                        </td>							
                            </tr>
                            </tbody></table>
                            <%-- // End Template Order confirmation message table \\ --%>
                        </td>
                    </tr>
                    <tr>
                        <td height="20">&nbsp;                            
                        </td>
                    </tr>
                    <c:if test="${not empty onlineOrderNumber}">
	                     <tr>
	                        <td valign="top" class="padLeft10" style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Order Details</td>
	                    </tr>
                    </c:if>
                    </tbody></table>
                    <%-- // Begin Template Order details table \\ --%>
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="order.shippingGroups" />
										<dsp:param name="elementName" value="shippingGroup" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
											<dsp:droplet name="/atg/dynamo/droplet/Compare">
												<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
												<dsp:param name="obj2" value="hardgoodShippingGroup"/>
												<dsp:oparam name="equal">
													<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
													<c:if test="${commerceItemRelationshipCount gt 0}">	
													<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
															 <dsp:param name="priceObject" value="${shippingGroup}" />
															<dsp:param name="orderObject" param="order" />
															<dsp:oparam name="output">
															<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
																  <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
																  <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
																  <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
																  <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
																  <dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/>
																  <dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
																  <dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shippingGroupItemsTotal"/>
																  <dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
																  <dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>	
																  <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal" />
																  <dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage" />
																  <dsp:getvalueof var="shippingAdjustments" param="priceInfoVO.shippingAdjustments" />
																  <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
																  <dsp:getvalueof var="shippingSurchargeSavings" param="priceInfoVO.surchargeSavings"/>
																  <dsp:getvalueof var="shipMethod" param="shippingMethodDescription"/>
																  
                    <table id="templateOrderDetails" border="0" cellpadding="2" cellspacing="0" width="100%">
                    <tbody>
                   
					
                    <tr>
                        <td valign="top">
							
                            <table border="0" cellpadding="4" cellspacing="0" width="100%" align="left" style="border-bottom: 1px solid #E8E8E8;color:#666666;font-family:Arial;font-size:12px;line-height:16px;">						
                            <tbody>
                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
															<dsp:param name="elementName" value="commerceItemRelationship" />
															<dsp:param name="sortProperties" value="+shippingGroup.registryId" />
																<dsp:oparam name="output">
																<dsp:getvalueof param="commerceItemRelationship.commerceItem.registryId" var="registratantId"/>
																<dsp:getvalueof param="order.registryMap.${registratantId}" var="registratantVO"/>
															
																	<dsp:getvalueof param="commerceItemRelationship.commerceItem.repositoryItem.type" var="cItemType"/>
                         											<c:if test="${cItemType eq  'tbsCommerceItem'}">	
                        <%-- Normal Products Row change --%>
                        <tr>
						<c:choose>
													<c:when
														test="${not empty registratantId}">
														<td bgcolor="#FBF8FD" style="border-top: 1px solid #a80bd6;">
													</c:when>
													<c:otherwise>
														<td style=" border-top: 1px solid #E8E8E8; ">
													</c:otherwise>
												</c:choose>
											
                                
                                        <table id="tblProductDetailsRow" cellspacing="0" cellpadding="4" width="98%" style="color:#666666;font-family:Arial;">
                                        <tbody><tr>
										
										<%--registry item starts--%>
										<c:if test="${not empty registratantId}">
									
									
										<bbbl:label key="lbl_cart_registry_from_text" language="${language}"/> ${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName} 
											<c:if test="${registratantVO.coRegistrantFirstName ne null}">
												&nbsp;&amp;&nbsp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
											</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
											<dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/><bbbl:label key="lbl_cart_registry_text" language="${language}"/>
										
										
										
								</c:if>
										
									<%--registry item ends--%>	
										
	<%--							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes starts--%>
										<%--product image--%>
                                           <td align="left" valign="top" width="105">
							<dsp:getvalueof var="smallImageURL" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.smallImage"/>
							<dsp:getvalueof var="referenceNumber" param="commerceItemRelationship.commerceItem.referenceNumber" />
							<dsp:getvalueof var="personalizationOptions" param="commerceItemRelationship.commerceItem.personalizationOptions" />
							<dsp:getvalueof var="personalizationOptionsDisplay" param="commerceItemRelationship.commerceItem.personalizationOptionsDisplay" />
							<dsp:getvalueof var="personalizePrice" param="commerceItemRelationship.commerceItem.personalizePrice" />
							<dsp:getvalueof var="personalizationDetails" param="commerceItemRelationship.commerceItem.personalizationDetails" />
							<dsp:getvalueof var="mobileThumbnailImagePath" param="commerceItemRelationship.commerceItem.mobileThumbnailImagePath" />
							<dsp:getvalueof var="fullImagePath" param="commerceItemRelationship.commerceItem.fullImagePath" />
							<c:choose>
							    <c:when test="${not empty referenceNumber}">

					                     <img src="http:${mobileThumbnailImagePath}" style="max-width:63px;" width="83" />
					                     <a href="http:${fullImagePath}" target="_blank" style="color:#170b63;text-decoration:none;font-size: 11px;"><span class="icon-fallback-text">
					                     <span class="icon-zoomin" aria-hidden="true"><img width="13" height="13" src="${imagePath}/_assets/global/images/icons/zoom.png" /></span></span>
					                     </a>
					                     <a href="http:${fullImagePath}" target="_blank" style="color:#170b63;text-decoration:none;font-size: 11px;">
					                     <span valign="top" style="font-size:11px;color:#170b63;"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span></a>
								</c:when>
								<c:when test="${empty smallImageURL}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="63" />
								</c:when>
                                <c:otherwise>
										<img src="${scene7Path}/${smallImageURL}" style="max-width:63px;" width="83"  />
								</c:otherwise>
							</c:choose>
<%--							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes ends--%>
                             </td>
                             <td valign="top" style=" width: 85%;" width="85%">
											<%--product title--%>
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="commerceItemRelationship.commerceItem.repositoryItem.productId" />
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												</dsp:oparam>
												</dsp:droplet>																												
												<dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem"/>
												<dsp:getvalueof var="skuColor" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.color"/>
												<dsp:getvalueof var="skuSize" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.size"/>
												<dsp:getvalueof var="personalizationType" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.personalizationType" />
												<dsp:droplet name="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet">
													<dsp:param name="skuId" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.id" />
													<dsp:param name="fullDetails" value="true" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="skuDetailVO" param="pSKUDetailVO" />
													</dsp:oparam>
												</dsp:droplet>
												<table cellspacing="0" cellpadding="0" width="40%" align="left">
												<tbody>
												<tr>
													<td align="left">
													<dsp:getvalueof var="siteId" param="emailTemplateVO.siteId" />
												<c:choose>
													<c:when test="${commItem.storeSKU}">
														<c:choose>
															<c:when test="${siteId eq 'TBS_BedBathUS'}">
															<p class="resetTextSize" style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
																<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/></p>
															</c:when>
															<c:when test="${siteId eq 'TBS_BuyBuyBaby'}">
															<p  class="resetTextSize" style="margin: 0; float: left; color: #32BCF3; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
																<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/></p>
															</c:when>
															<c:otherwise>
															<p class="resetTextSize" style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;">
																<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/></p>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<p>
														<c:choose>
															<c:when test="${siteId eq 'TBS_BedBathUS'}">
																<dsp:a
																	page="${serverPath}${contextPath}${finalUrl}"
																	style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																	<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
																</dsp:a>
															</c:when>
															<c:when test="${siteId eq 'TBS_BuyBuyBaby'}">
																<dsp:a
																	page="${serverPath}${contextPath}${finalUrl}"
																	style="margin: 0; float: left; color: #32BCF3; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																	<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
																</dsp:a>
															</c:when>
															<c:otherwise>
																<dsp:a
																	page="${serverPath}${contextPath}${finalUrl}"
																	style="margin: 0; float: left; color: #273691; padding: 0; font-weight: bold; font-family: Arial; font-size: 12px; text-decoration: none;" iclass="resetTextSize">
																	<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
																</dsp:a>

															</c:otherwise>
														</c:choose>
														</p>
													</c:otherwise>
												</c:choose>
	<%--							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes starts--%>
												<div style="font-size:12px; clear:both;" class="resetTextSize">
													<c:if test='${not empty skuColor}'>
														 <bbbl:label key="lbl_item_color" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
													</c:if>
													<c:if test='${not empty skuSize}'>
														 <c:if test='${not empty skuColor}'>&nbsp;|&nbsp;</c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" />
													</c:if>
													<c:if test='${not empty personalizationOptions}'>
														<br>${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
													</c:if>
													<c:choose>
														<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
															<c:set var="customizeTxt" value="true"/>
														</c:when>
														<c:otherwise>
															<c:set var="customizeTxt" value="false"/>
														</c:otherwise>
													</c:choose>
													<c:if test='${not empty referenceNumber}'>
														  <br><c:choose>
												                <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
																	<dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
																</c:when>
																<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
																	<dsp:valueof value="${personalizePrice}" converter="currency"/> <c:choose>
																																			<c:when test="${customizeTxt eq true}">
																																				<bbbl:label key="lbl_exim_cr_added_price_customize"
																																					language="${pageContext.request.locale.language}"/>
							 																												</c:when>
							 																												<c:otherwise>
							 																													<bbbl:label key="lbl_exim_cr_added_price"
																																					language="${pageContext.request.locale.language}"/>
							 																												</c:otherwise>
							 																										</c:choose>
																</c:when>
																<c:when test='${not empty personalizationType && personalizationType == "PB"}'>
																    <bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
																</c:when>
															</c:choose>
													</c:if>
												</div>
<%-- 							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes ends--%>
											</td>
                                                 </tr>
                                                     <tr>
                                                         <td height="10">
                                                             <dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem" />
                                                            <c:if test="${commItem.skuSurcharge gt 0.0}">
                                                                <bbbl:label key="lbl_cartdetail_surchargeapplies" language="<c:out param='${language}'/>"/>
                                                            </c:if>
                                                            <c:if test="${commItem.vdcInd and not skuDetailVO.ltlItem}">
                                                            	<div style="font-size:12px; clear:both;" class="resetTextSize">
		                                                            <dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
																	<dsp:param name="skuId" value="${skuDetailVO.skuId}"/>
																	<dsp:param name="shippingMethodCode" value="${shippingGroup.shippingMethod}"/>
																	<dsp:param name="requireMsgInDates" value="true"/>
																	<dsp:oparam name="output">
																			<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
																	</dsp:oparam>
																	<dsp:oparam name="error">
																	</dsp:oparam>
																	</dsp:droplet>
																	<c:if test="${not empty vdcDelTime }">
																		<div class="facet">Expected delivery <br>${vdcDelTime}</div>
																	</c:if>
																</div>                                                            
                                                        	</c:if>
                                                          </td>
                                                      </tr>
                                                       <tr>
                                                                    <td height="20">
                                                                        <p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;line-height:15px;">SKU <dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.id"/></p>
                                                                    </td>
                                                                </tr>
                                                    <tr>            
                                                         <td height="20">      
															<c:if test="${skuDetailVO.ltlItem}">
																<dsp:droplet name="/atg/commerce/order/droplet/BBBCartDeliveryDateDisplayDroplet">
																<dsp:param name="shippingGroup" param="shippingGroup" />
																<dsp:param name="isFromOrderDetail" param="isFromOrderDetail" />
																<dsp:param name="orderDate" param="orderDate" />
																<dsp:param name="ltlSkuId" value="${skuDetailVO.skuId}" />
																<dsp:param name="isltlSku" value="${true}" />
																<dsp:oparam name="expectedDeliveryDateOutput">
																	<dt  style="font-weight:bold"><bbbl:label key="lbl_preview_expecteddelivery" language="<c:out param='${language}'/>"/> </dt>
																	<dd  style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;line-height:15px;"><dsp:valueof param="expectedDeliveryDate"/></dd>
																	
																</dsp:oparam>
																</dsp:droplet>
																</c:if>
                                                 		 </td>              
														</tr>		                               </tbody>
												</table>
                                                            
                                                         <%--product prices--%>               
                                                                      
                                                                    
                                                           
                                                            <table cellspacing="0" cellpadding="0" width="55%" align="right">
                                                                <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <table cellspacing="0" cellpadding="0" width="20%" align="left" height="20">
                                                                            <tbody><tr>
                                                                                <td class="alignLeft" align="center" valign="top"><font style="font-family:arial;font-size:12px;line-height:15px;color:#666;font-weight:bold;" class="resetTextSize">QTY:</font>&nbsp;<font class="resetTextSize" style="font-family:arial;font-size:12px;line-height:15px;color:#666;"><dsp:valueof param="commerceItemRelationship.quantity"/>		</font>             </td>
                                                                            </tr>													
                                                                        </tbody>
                                                                        </table>
                                                                          <%--price column starts--%> 
                                                                        <table cellspacing="0" cellpadding="0" align="right" width="75%">
                                                                            <tbody><tr>
                                                                                <td valign="top">
																				<%--total--%>
																		<p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:14px;"><font class="hideOnSmallScreens" style="font-weight: bold;">
																		<dsp:getvalueof var="commerceItemRelationshipTemp" param="commerceItemRelationship"/>
																		 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
																				<dsp:param name="priceObject" param="commerceItemRelationship" />
																				<dsp:param name="profile" bean="Profile"/>
																				<dsp:oparam name="output">
																				<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
																				  Total:<dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/>
																				  </dsp:oparam>
																				  </dsp:droplet>
																									  
																								
																  <br></font> </p>
																  <%--your price & our price--%>
									<dsp:getvalueof param="commerceItemRelationship.commerceItem.TBSItemInfo" var="tbsItemInfo"/>
									<dsp:getvalueof param="commerceItemRelationship.commerceItem" var="commerceItem"/>
								   <table cellspacing="0" cellpadding="0" width="100%" style="text-align:right;" class="alignLeft">
								 <tbody
								<tr>
								
								 <td valign="top" style="height:40px; vertical-align: middle;color:#666;">
										 <font style="font-family:Arial;font-size:12px;line-height:15px;" class="boldInSmallScreens">Your Price:</font>
									 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
										<dsp:param name="priceObject" value="${commerceItem}" />
										<dsp:param name="profile" bean="Profile"/>
										<dsp:oparam name="output">
										
											<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
											<dsp:getvalueof var="unitSavedAmountTemp" param="priceInfoVO.unitSavedAmount"/>
											<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
											<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
											<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="priceInfoVO.priceBeans" />
													<dsp:param name="elementName" value="unitPriceBean" />		
													<dsp:oparam name="output">

														<dsp:getvalueof param="unitPriceBean.pricingModels" var="promos"/>
														<dsp:getvalueof param="unitPriceBean.quantity" var="promoqty"/>
														<c:if test="${not empty promos}">
															<font style="font-family:Arial;font-size:12px;line-height: 15px;color:#666;">
																<dsp:valueof param="unitPriceBean.quantity"/> &nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/>
															</font>
															<br/>
															<font style="font-family:Arial;font-size:11px;color:red;">COUPON APPLIED:<br/>
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="unitPriceBean.pricingModels" />
																	<dsp:param name="elementName" value="pricingModel" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="appliedPromoId" param="pricingModel.id"/>
																		<c:if test="${not empty appliedPromoId}">
																			<dsp:getvalueof var="promoId" value="${appliedPromoId}"/>
																			<dsp:valueof param="pricingModel.displayName"/><br/>
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
															</font>
														</c:if>
																			
													</dsp:oparam>
												</dsp:droplet>
																								
												<c:choose>
													<c:when test="${undiscountedItemsCount gt 0 && unitSalePrice gt 0}">
														<font style="font-family:Arial;font-size:12px;line-height: 15px;color:#666;">
															<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof value="${unitSalePrice}" converter="currency" /><br/>
														</font>
													</c:when>
													<c:when test="${undiscountedItemsCount gt 0 && unitListPrice gt 0}">
														<font style="font-family:Arial;font-size:12px;line-height: 15px;color:#666;">
															<dsp:valueof value="${undiscountedItemsCount}" />&nbsp;<bbbl:label key="lbl_cart_multiplier" language="${language}"/>&nbsp;<dsp:valueof value="${unitListPrice}" converter="currency" /><br/>
														</font>
													</c:when>
												</c:choose>
												
												<c:if test="${not empty tbsItemInfo && tbsItemInfo.overridePrice > 0 }"></br>
													<span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/> &nbsp;
														<dsp:valueof value="${tbsItemInfo.overridePrice}" converter="currency"/>
													</span>
												</c:if>
												<c:if test="${priceInfoVO.unitSavedAmount gt '0.0' || (not empty tbsItemInfo && tbsItemInfo.overridePrice > 0)}">
													</br>
													<font style="font-family:Arial;font-size:11px;color:red;">
													<bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
													<dsp:valueof value="${priceInfoVO.unitSavedAmount * promoqty}" converter="currency"/>
													</font>
												</c:if>				
											</dsp:oparam>
										</dsp:droplet>
										
										
										<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
												<dsp:param name="priceObject" param="shippingGroup" />
												<dsp:param name="orderObject" param="order" />
												<dsp:param name="commerceIdForEcoFee" param="commerceItemRelationship.commerceItem.id" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="ecoFeeAmountTemp" param="ecoFeeAmount" />
												<c:if test="${ecoFeeAmountTemp gt '0.0'}">						
												
												<bbbl:label key="lbl_eco_fee" language="${language}"/>: 
												<dsp:valueof param="ecoFeeAmount" converter="currency"/>
												
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
									
										</td>
								</tr>
								 <tr> 
								<td valign="top">
									<font class="hideOnSmallScreens" style="font-family:Arial;color:#666;font-size:12px;line-height:15px;">Our Price:
								<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
										<dsp:param name="priceObject" param="commerceItemRelationship.commerceItem" />
										<dsp:param name="profile" bean="Profile"/>
										<dsp:param name="temp" value="true"/>
										<dsp:oparam name="output">
										<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
										<dsp:valueof param="priceInfoVO.unitListPrice" converter="currency"/>
										<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
									    <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
									    <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
												</dsp:oparam>
												</dsp:droplet>
										</font> </td>
								</tr>                                                   
								<%-- LTL additional info --%>
								<c:if test="${deliverySurcharge gt 0.0}">
	                                <tr>
	                                    <td valign="top" style="height: 20px; width:105px;"class="showOnSmallScreens fullWidth" height="20">
	                                    <font style="line-height:20px;">+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></font></td>
                                	</tr>
                                </c:if>
                                <c:if test="${deliverySurchargeSaving gt 0.0}"> 
	                                <tr>
	                                    <td valign="top" style="height: 20px; width:105px;" class="showOnSmallScreens fullWidth" height="20">
	                                     <font style="line-height:20px;color:red;">- <bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/></font></td>
	                                </tr> 
	                            </c:if>
								<c:if test="${assemblyFee gt 0.0}">
	                                <tr>
	                                    <td valign="top" style="height: 20px; width:105px;" class="showOnSmallScreens fullWidth" height="20">
	                                    <font style="line-height:20px;">+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>: <dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/></font></td>
	                                </tr>
	                            </c:if> 
                                <%-- LTL additional info --%>                                                    
								</tbody></table>
										   
										   <p></p>																		
									</td>
							</tr>
							</tbody>
							</table>
                                                             <%--price column ends--%> 
                                                                </td>
                                                            </tr>
                                                            </tbody>
                                                            </table>																			
                                        </td>
                                    </tr>								
                                    </tbody></table>
                                </td>
						</tr>
						</c:if>
                        <%--Product Row change --%>
                        </dsp:oparam>
					 </dsp:droplet>					
                        </tbody>
                     </table>
					 
					 </td>
					 </tr>
					 </tbody>
					 </table>

				<%-- // End Template Order details table \\ --%>
				<%-- // Begin Template Order Summary table \\ --%>
			 <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
															<dsp:param name="priceObject" param="shippingGroup" />
															<dsp:param name="orderObject" param="order" />
															<dsp:oparam name="output">
																  <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
																  <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
																  <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
																  <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
																  <dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/>
																  <dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingcountyLevelTax"/>
																  <dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
																  <dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
																  <dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shippingGroupItemsTotal"/>
																  <dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
																  <dsp:getvalueof var="totalTax" param="priceInfoVO.totalTax"/>
																  <dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>	
																  <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal" />
																  <dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage" />
																  <dsp:getvalueof var="shippingAdjustments" param="priceInfoVO.shippingAdjustments" />
																  <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
																  <dsp:getvalueof var="shippingSurchargeSavings" param="priceInfoVO.surchargeSavings"/>
																  <dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
																  <dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
																  <dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
   																  <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
   																  <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
    															  <dsp:getvalueof var="maxDeliverySurcharge" value="0.0"/>
																  <dsp:getvalueof var="maxDeliverySurchargeReached" value="0.0"/>
																  <dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>
															</dsp:oparam>
														</dsp:droplet>	
					<c:if test="${totalDeliverySurcharge gt 0.0}">
					<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
															 <dsp:param name="priceObject" param="order"  />
												<dsp:param name="profile" bean="Profile"/>
								<dsp:oparam name="output">
								
										<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
										<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
										</dsp:oparam>
								</dsp:droplet>
				</c:if>			
				<table id="tblGroupsOrderInfo" cellspacing="0" cellpadding="0" width="100%" style="color:#666666;font-family:Arial;float:left;font-size:14px;border-top: 1px outset #fff;" align="left" bgcolor="#f7f7f7">
				<tbody><tr>
					<td>
						<table id="tblOrderSummary" align="left" cellspacing="0" cellpadding="4" bgcolor="#f7f7f7" width="300" style="float: left;color:#666666;font-family:Arial;font-size:14px;margin-left: 25px;">
						<tbody><tr>
							<td colspan="2" style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Order Summary</td>
						</tr>
                        <%--number of items--%>
						<tr>
							<td width="180">
								<font style="font-weight: bold;font-size:14px; "><dsp:valueof value="${itemCount}"/> <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-weight: bold;font-size:14px; "><dsp:valueof value="${shippingGroupItemsTotal}" converter="currency"/></font>
							</td>
						</tr>
						<%--shipping--%>
						<c:if test="${totalEcoFeeAmount gt 0.0}">
							<tr>
								<td width="180">
								<font style="font-size:14px; ">
									<bbbl:label key="lbl_eco_fees" language="<c:out param='${language}'/>"/></font>
								</td>
								<td width="90" align="left">
								<font style="font-size:14px; ">
									<dsp:valueof value="${totalEcoFeeAmount}" converter="currency"/></font>
								</td>
							</tr>
						</c:if>
						<c:if test="${giftWrapTotal gt 0.0}">
							<tr>
								<td width="180">
								<font style="font-size:14px; ">
								<bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/></font>
								</td>
								<td width="90" align="left">
								<font style="font-size:14px; ">
									<dsp:valueof value="${giftWrapTotal}" converter="currency"/></font>
								</td>
							</tr>
						</c:if>	
						<c:choose>
       						<c:when test="${rawShippingTotal gt 0.0}">
							<tr>
								<td width="180">
									<font style="font-size:14px; "><bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/> &nbsp;
									(<dsp:valueof value="${shipMethod}"/>)</font>
									
								</td>
								<td width="90" align="left">
											<font style="font-size:14px; "><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/></font>
								</td>
							</tr>
							
							<c:if test="${not empty shippingAdjustments}">	
								<tr>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="priceInfoVO.shippingAdjustments" />
										<dsp:param name="elementName" value="shippingPromoDiscount" />
										<dsp:oparam name="outputStart">
												<td td width="180">
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
												</td>
										</dsp:oparam>
										<dsp:oparam name="output">
											<c:set var="shipPromo" value="true" />
											<dsp:getvalueof var="count" param="count" />
											<dsp:getvalueof var="size" param="size" />
											<font style="font-family:Arial;font-size:14px;color:red;">
											<dsp:valueof param="key.displayName"/>
											<c:if test="${count lt size}">
												,<!-- comma seperator -->
											</c:if>
											</font>
										</dsp:oparam>
									</dsp:droplet>
									
										<c:if test="${not empty shipPromo}">
											<td width="90" align="left">
												<font style="font-family:Arial;font-size:14px;color:red;">
													(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)
												</font>
											</td>
										</c:if>
									</tr>
								</c:if>
						   </c:when>
					       <c:otherwise>
					        <tr>
					         <td width="180">
					          <font style="font-size:14px; "><bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/> &nbsp;
					          (<dsp:valueof value="${shipMethod}"/>)</font>
					          
					         </td>
					         <td width="90" align="left">
					            <font style="font-size:14px; "><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></font>
					         </td>
					        </tr>
					       </c:otherwise>
					      </c:choose>
						
						<%-- LTL additional info --%>
						<c:if test="${totalDeliverySurcharge gt 0.0}">
							<tr>
								<td width="180">
									<font style="font-size:14px; "><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></font>
								</td>
								<td width="90" align="left">
									<font style="font-size:14px; "><dsp:valueof value="${totalDeliverySurcharge}" converter="currency" number="0.00"/></font>
								</td>
							</tr>
						</c:if>
						<c:if test="${maxDeliverySurchargeReached eq true}">
							<tr style=" color: red; ">
								<td width="180">
									<font style="font-size:14px; ">Maximum Surcharge Reached</font>
									<a href="#" style="font-size: 11px; text-decoration:none;"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
								</td>
								<td width="90" align="left" valign="top">
									<font style="font-size:14px; ">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" converter="currency" number="0.00"/>)</font>
								</td>
							</tr>
						</c:if>
						<c:if test="${assemblyFee gt 0.0}">
							<tr>
								<td width="180">
									<font style="font-size:14px; "><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></font>
								</td>
								<td width="90" align="left">
									<font style="font-size:14px; "><dsp:valueof value="${assemblyFee}" converter="currency" number="0.00"/></font>
								</td>
							</tr>
						</c:if>
						<%-- LTL additional info --%>

						<%--tax--%>
						<c:if test="${totalSurcharge gt 0.0}">
						<tr>
							<td width="180">
								<font style="font-size:14px; "><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								<dsp:valueof value="${totalSurcharge}" converter="currency"/></font>
							</td>
						</tr>                                               
					</c:if>
					<c:if test="${shippingSurchargeSavings gt 0.0}">
						<tr>
							<td width="180">
								<font style="font-size:14px; "><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								(<dsp:valueof value="${shippingSurchargeSavings}" converter="currency"/>)</font>
							</td>
						</tr>															
					</c:if>
					
					<%----tax code fix --%>
					<c:choose>
						<c:when test="${currentSiteId ne 'TBS_BedBathCanada'}">
						<tr>
							<td width="180">
								<font style="font-size:14px; ">
								<bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></font>
							</td>
							<td width="90" align="left">
								<font style="font-size:14px; ">
								<p>	<dsp:valueof value="${shippingLevelTax}" converter="currency"/></font>
							</td>
						</tr>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${countyLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${stateLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									
									<c:set var="totalStateTax" value="${totalStateTax+countyLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+stateLevelTax}" scope="request" />
								</c:when>
								<c:otherwise>
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${stateLevelTax}" converter="currency"/></font>
										</td>
									</tr>
									<tr>
										<td width="180">
											<font style="font-size:14px; ">
											<bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/></font>
										</td>
										<td width="90" align="left">
											<font style="font-size:14px; ">
											<p>	<dsp:valueof value="${countyLevelTax}" converter="currency"/></font>
										</td>
									</tr>								
									<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+countyLevelTax}" scope="request" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<%----tax code fix --%>
					
							
						<%--totall--%>
						<c:if test="${totalAmount gt 0.0}">
																										
							
							<tr>
											<td colspan="2">
											<table width="100%" align="left" cellspacing="0" cellpadding="0" style="float:left;color:#666666;font-family:Arial;border-top: 1px outset #e6e6e6;font-weight: bold;font-size:18px;">
											 <tbody><tr>
								<td height="8" colspan="2" style="border-top: 1px outset #fff;"></td>
							</tr><tr>

													<td width="180">
														<bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>
													</td>
												   <td width="90" align="left">
													  <dsp:valueof value="${totalAmount}" converter="currency"/>
													</td>
												</tr>                                        
											</tbody></table>
												</td>						
											</tr>     
						</c:if>
						<c:if test="${totalSavedAmount gt 0.0}">
	                  		 <tr>
								<td colspan="2">
								<table width="100%" align="left" cellspacing="0" cellpadding="0" style="float:left;color:red;font-family:Arial;border-top: 1px outset #e6e6e6;font-weight: bold;font-size:12px;">
								 <tbody><tr>
								<td height="8" colspan="2" style="border-top: 1px outset #fff;"></td>
							</tr><tr>

													<td width="180">
														<bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>
													</td>
												   <td width="90" align="left">
													  <dsp:valueof value="${totalSavedAmount}" converter="currency"/>
													</td>
												</tr>                                        
											</tbody></table>
												</td>						
											</tr>   
	               		</c:if>							
						</tbody></table>
						
						
						<table id="tblBillingInfo" align="right" cellspacing="0" cellpadding="4" width="170" style="color:#666666;font-family:Arial;">
						<tbody>
						
						
						<%-- added for 83 T  --%>
					
						<c:set var="isPayPal" value="${false}"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="order.paymentGroups"/>
								<dsp:param name="elementName" value="paymentGroup" />
								<dsp:oparam name="output">
									<dsp:droplet name="Switch">
			                			<dsp:param name="value" param="paymentGroup.paymentMethod"/>
			                			<dsp:oparam name="paypal">
										<c:set var="isPayPal" value="${true}"/>
											<tr>	<td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;"><bbbl:label key="lbl_payment_title" language="<c:out param='${language}'/>"/></td>
											</tr>
						
											<tr>
												<td>
													<font style="font-weight: bold;font-size:14px; "><bbbl:label key="lbl_preview_billingmethod" language="<c:out param='${language}'/>"/>:</font>
												</td>
											</tr>
											<tr> <td> <bbbl:label key="lbl_paypal_title"  language="<c:out param='${language}'/>"/> </td>
											</tr>
											<tr><td>
													<dsp:valueof param="paymentGroup.payerEmail" />
												</td>
											</tr>
										
										
			                			</dsp:oparam>
			                		</dsp:droplet>
			                	</dsp:oparam>
			            </dsp:droplet>
					
						
						
					    <c:if test="${not isPayPal}">	
						
						  <tr>
							<td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;"><bbbl:label key="lbl_preview_billinginfo" language="<c:out param='${language}'/>"/></td>
						  </tr>
						  <dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
						  <tr>
							<td align="left">
                                <table cellspacing="0" cellpadding="0" align="left" width="100%"><tbody><tr>
                                                        <td>
                                    <p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;">
                                        <strong><bbbl:label key="lbl_preview_billingaddress" language="<c:out param='${language}'/>"/></strong><br>
                                         ${billingAddress.firstName} ${billingAddress.lastName}<br>
                                        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                            <dsp:param name="value" param="order.billingAddress.address2"/>
                                            <dsp:oparam name="true">
                                                ${billingAddress.address1}
                                            </dsp:oparam>
                                            <dsp:oparam name="false">
                                                ${billingAddress.address1}<br> ${billingAddress.address2}
                                            </dsp:oparam>
                                        </dsp:droplet><br>
                                         ${billingAddress.city}<br> ${billingAddress.state}<br> ${billingAddress.postalCode}<br>
                                         <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                            <dsp:param name="value" param="order.billingAddress.countryName"/>
                                            <dsp:oparam name="false">
                                                ${billingAddress.countryName}
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </p>
                                    </td></tr></tbody>
                                </table>
                            </td>
                          </tr>
                        </c:if>
                        <tr>
                            <td>
                                <table cellspacing="0" cellpadding="0" align="left" width="100%" class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;"><tbody><tr>
							<td height="10">&nbsp;						
							</td>
						</tr>
						<%--payment method info using credit card--%>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="order.paymentGroups"/>
						<dsp:param name="elementName" value="paymentGroup" />
						<dsp:oparam name="output">
							<dsp:droplet name="Switch">
								<dsp:param name="value" param="paymentGroup.paymentMethod"/>
									<dsp:oparam name="creditCard">
						
						<tr>
                       <td><strong><dsp:valueof param="paymentGroup.creditCardType"/>:</strong><br>
					   <dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBCreditCardDisplayDroplet">
						<dsp:param name="creditCardNo" param="paymentGroup.creditCardNumber"/>
						<dsp:oparam name="output">																									
								<dsp:valueof param="maskedCreditCardNo"/>
						</dsp:oparam>
					</dsp:droplet><br>
                       </td>
					</tr>
                    <tr>
							<td class="vspace" height="5">						
							</td>
						</tr>
                    <tr>
                    	<td><strong><bbbl:label key="lbl_preview_exp" language="<c:out param='${language}'/>" />:</strong><br>
						<dsp:getvalueof var="expDate" param="paymentGroup.expirationMonth" scope="page" />
							<c:if test='<%=new Integer((String)pageContext.getAttribute("expDate"))<10 %>'>											
								<c:out value="0${expDate}" />
							</c:if>
							<c:if test='<%=new Integer((String)pageContext.getAttribute("expDate"))>=10 %>'>											
								<c:out value="${expDate}" />
							</c:if>											
							/
							<dsp:valueof param="paymentGroup.expirationYear" />
						</td>
                    </tr>
					</dsp:oparam>
					</dsp:droplet>
					</dsp:oparam>
					</dsp:droplet>
                 <%--</tbody></table>--%>
                 <%--payment method info using gift card--%>
                 <dsp:droplet name="BBBPaymentGroupDroplet">
      <dsp:param param="order" name="order"/>
      <dsp:param name="serviceType" value="GiftCardDetailService"/>
      <dsp:oparam name="output">
      		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty"> 
			  			<dsp:param name="value" param="giftcards"/>
			  			<dsp:oparam name="false">
							 <tbody>--%>
							 <tr>
							<td height="10">&nbsp;						
							</td>
							</tr>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="giftcards" />
										<dsp:param name="elementName" value="giftcard" />
										<dsp:oparam name="output">
										<tr>
											<td><strong><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></strong><br>
											
												<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
												<bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}
											<br> </td>
										</tr>
										 <tr>
						<td class="vspace" height="5">						
						</td>
					</tr>
										<tr>
												<td><strong><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></strong><br>
											
												<dsp:droplet name="CurrencyFormatter">
														    <dsp:param name="currency" param="giftcard.amount"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
												</dsp:droplet>
											</td>
										</tr>
										</dsp:oparam>
									</dsp:droplet>
								<%--</tbody>
							</table>--%>
						</dsp:oparam>
			</dsp:droplet>     	
      </dsp:oparam>
	</dsp:droplet>
						</tbody></table>	
                      
                   
                   
                	
							</td>
						</tr>                      
						</tbody></table>
                         <%--Curly border at bottom --Start --%>
                          <table class="hideOnSmallScreens" cellspacing="0" cellpadding="0" width="600" bgcolor="white" style="clear:both">						
        <tbody><tr><td align="left">                
        <img src="${serverPath}/_assets/emailtemplates/bbbeyond/images/lightgrey_curlyborder.gif" width="600" height="14" border="0" class="hideOnSmallScreens" align="left">    
    </td>
						</tr>                      
						</tbody></table>                        
                        <%--Curly border at bottom --End --%>
<!-- Certona Crousal BBBSL-2683 Start -->

<c:set var="CertonaOn_Confirmation_email">
	<bbbc:config key="CertonaOn_Confirmation_email" configName="CertonaKeys" />
</c:set>
<c:if test="${CertonaOn_Confirmation_email}">
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="schemeName" value="orderconf_rr"/>
	<c:set var="campaignId"> 
		<bbbc:config key="ris_campaign_id" configName="CertonaKeys" />	
	</c:set>	
	<dsp:getvalueof var="userId"  param="userid" />
	<c:set var="applicationId"> 
		<bbbc:config key="${appid}" configName="CertonaKeys" />  
	</c:set>	
	 <c:set var="ris_href_path">
		<bbbc:config key="ris_href_path" configName="ThirdPartyURLs" />
	</c:set>
	 <c:set var="ris_href_path_image">
		<bbbc:config key="ris_href_path_image" configName="ThirdPartyURLs" />
	</c:set>
	 <c:set var="confirmation_product_limit">
		<bbbc:config key="confirmation_product_limit" configName="CertonaKeys" />
	</c:set>
	
	<table id="tblCertonaContainer" cellspacing="0" cellpadding="10" width="600" bgcolor="white" align="center" style="color:#666666;font-family:Arial;">
		<tbody>
			<tr>
				<td colspan="3" style="font-family:arial; font-size:19px; line-height:22px; font-weight:bold; color:#444444;">${firstName}<bbbl:label key="lbl_confirmation_certona_txt" language="${language}"/></td>
			</tr>
			<tr>
			<c:forEach var="index" begin="1" end="${confirmation_product_limit}">
							<td style="vertical-align: top;">
								<table cellspacing="6">
									<tr>
										<td style="">
										<a href="${ris_href_path}?appid=${applicationId}&userid=${userId}&index=${index}&scheme=${schemeName}&campaignid=${campaignId}"><img
												width="146" height="146"
												src="${ris_href_path_image}?appid=${applicationId}&userid=${userId}&index=${index}&scheme=${schemeName}&campaignid=${campaignId}"></a>
										</td>
									</tr>
								</table>
							</td>
			</c:forEach>
			</tr>
		</tbody>
	</table>	
</c:if>					
	
<!-- Certona Crousal BBBSL-2683 End -->			
                        
                        <table id="tblDeliveryInfoContainer" cellspacing="0" cellpadding="0" width="600" bgcolor="white" style="color:#666666;font-family:Arial;" align="center">
						<tbody><tr>
							<td align="left">                        
						<table id="tblDeliveryInfo" cellspacing="0" cellpadding="0" width="575" bgcolor="white" style="color:#666666;font-family:Arial;margin-left: 25px;">
						<tbody><tr>
							<td height="10" class="hideOnSmallScreens">&nbsp;						
							</td>
						</tr>
						<tr>
							<td style="font-family:arial; font-size:18px; line-height:22px; font-weight:bold; color:#444444;">Delivery Info</td>
						</tr>
                        <tr>
							<td height="10" class="hideOnSmallScreens">							
							</td>
						</tr>
						<tr>
							<td valign="top">							
								<table cellspacing="0" cellpadding="0" width="100%" align="left" style="color:#666666;font-family:Arial;">
									<tbody>
									<tr>
										<td>
											<table cellspacing="0" cellpadding="0" width="30%" align="left" height="35">
												<tbody><tr>
													<td>
														<p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;">
															<strong><bbbl:label key="lbl_preview_shippingaddress" language="<c:out param='${language}'/>"/>:</strong><br>
															<c:choose>
															<c:when test="${not empty shippingGroup.registryId}">
																<dsp:getvalueof param="order.registryMap.${shippingGroup.registryId}" var="registratantVO"/>
																
																	<bbbl:label key="lbl_cart_registry_from_text" language="${language}"/> ${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName} 
											<c:if test="${registratantVO.coRegistrantFirstName ne null}">
												&nbsp;&amp;&nbsp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
											</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
											<dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/><bbbl:label key="lbl_cart_registry_text" language="${language}"/>
										
															
															</c:when>
															<c:otherwise>
																${shippingGroup.shippingAddress.firstName} ${shippingGroup.shippingAddress.lastName}<br>
				<c:choose>
					<c:when test="${not empty beddingKit && beddingKit eq true}">
						<dsp:valueof value="${collegeName}" valueishtml="true"/><br>
					</c:when>
					<c:when test="${not empty weblinkOrder && weblinkOrder eq true}">
						<dsp:valueof value="${collegeName}" valueishtml="true"/><br>
					</c:when>
					<c:otherwise>
														
					</c:otherwise>
				</c:choose>	
																
																	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
																		<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
																		<dsp:oparam name="true">
																			${shippingGroup.shippingAddress.address1}
																		</dsp:oparam>
																		<dsp:oparam name="false">
																			${shippingGroup.shippingAddress.address1}, ${shippingGroup.shippingAddress.address2}
																		</dsp:oparam>
																	</dsp:droplet><br>
																	
																${shippingGroup.shippingAddress.city}, ${shippingGroup.shippingAddress.state} ${shippingGroup.shippingAddress.postalCode}<br>
																${shippingGroup.shippingAddress.mobileNumber}<br>
															</c:otherwise>
														</c:choose>
															 
														</p>
													</td>
												</tr>															
											</tbody>
											</table>
											
											<table cellspacing="0" cellpadding="0" width="35%" align="left" class="padTop8">
												<tbody><tr>
													<td>
														<p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;">
															<strong><bbbl:label key="lbl_preview_shippingmethod" language="<c:out param='${language}'/>"/>:</strong><br>
															<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
																  <dsp:param name="priceObject" value="${shippingGroup}" />
																  <dsp:param name="shippingMethod" value="${shippingGroup.shippingMethod}" />
																  <dsp:oparam name="output">
																  <dsp:getvalueof var="shippingMethodDesc" param="shippingMethodDescription"/>
																	
																		${shippingMethodDesc}
																	
																	  </dsp:oparam>
															</dsp:droplet>
													</td>
											</tr>
											</tbody>
											</table>
											
											<table cellspacing="0" cellpadding="0" width="30%" align="left" class="padTop8">
												<tbody><tr>
													<td>
														<p class="resetTextSize" style="margin:0;padding:0;color:#666;font-family:Arial;font-size:12px;">
															<strong><bbbl:label key="lbl_preview_giftpackage" language="<c:out param='${language}'/>"/>:</strong><br>
															<c:choose>
																									<c:when test="${shippingGroup.containsGiftWrap}">
																										<bbbl:label key="lbl_preview_gift_wrap_true" language="<c:out param='${language}'/>"/>
																									</c:when>
																									<c:otherwise>
																										<bbbl:label key="lbl_preview_gift_wrap_false" language="<c:out param='${language}'/>"/>
																									</c:otherwise>
																								</c:choose>
															<br><strong><bbbl:label key="lbl_preview_message" language="<c:out param='${language}'/>"/></strong><br>
																	${shippingGroup.specialInstructions.giftMessage}																								
														</p>
													</td>
													
											</tr>
											</tbody>
											</table>
									</td>
								</tr>
								</tbody>
								</table>							
							</td>
						</tr>
						</tbody></table>
                        </td>
						</tr>
							</tbody></table>
							
					</td>
				</tr>
				</tbody></table>
				</dsp:oparam>
				</dsp:droplet>
						
											 </c:if>
				</dsp:oparam>
				</dsp:droplet>
				</dsp:oparam>
				</dsp:droplet>
						
				<%-- // End Template Order Summary table \\ --%>
				
				
					</td>
		</tr>
		</tbody></table>		
	</td>
</tr>

<%-- // End Template Main Content \\ --%>
	
	<%-- Main Content Ends--%>	


	<dsp:valueof param="emailFooter" valueishtml="true"/>		

</body>
</html>		
			
</dsp:page>
