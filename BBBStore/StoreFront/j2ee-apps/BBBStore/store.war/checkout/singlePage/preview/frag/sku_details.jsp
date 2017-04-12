<dsp:page>
	<li class="grid_3">
	<dsp:getvalueof var="imgSrc" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.thumbnailImage"/>
    <dsp:getvalueof var="skuName" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" />
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem" />
	<dsp:getvalueof var="color" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.color" />
	<dsp:getvalueof var="size" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.size" />
	<dsp:getvalueof var="giftWrapEligible" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.giftWrapEligible" />
	<dsp:getvalueof var="ltlItem" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.ltlFlag" />
	<dsp:getvalueof var="skuId" param="commerceItemRelationship.commerceItem.catalogRefId" />
	<dsp:getvalueof var="referenceNumber" param="commerceItemRelationship.commerceItem.referenceNumber" />
	<dsp:getvalueof var="personalizationOptions" param="commerceItemRelationship.commerceItem.personalizationOptions" />	
	<dsp:getvalueof var="personalizePrice" param="commerceItemRelationship.commerceItem.personalizePrice" />
	<dsp:getvalueof var="personalizationDetails" param="commerceItemRelationship.commerceItem.personalizationDetails" />
	<dsp:getvalueof var="fullImagePath" param="commerceItemRelationship.commerceItem.fullImagePath" />
	<dsp:getvalueof var="thumbnailImagePath" param="commerceItemRelationship.commerceItem.thumbnailImagePath" />
	<dsp:getvalueof var="personalizationType" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.personalizationType" />
	<dsp:getvalueof var="order" param="order" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:getvalueof bean="SessionBean.shippingZipcodeVO" var="shippingZipCodeVo"/>
	<dsp:getvalueof value="${shippingZipCodeVo.displayCutoffTime}" var="displayCutoffTime"/>
	<dsp:getvalueof value="${shippingZipCodeVo.displayGetByTime}" var="displayGetByTime" />
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set> 
		<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
		<dsp:param name="personalizationOptions" value="${personalizationOptions}"/>	
		<dsp:oparam name="output">
			 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			 <dsp:getvalueof var="personalizationOptionsDisplay" param="personalizationOptionsDisplay" />
		</dsp:oparam>
	 </dsp:droplet>

    <%-- WEB-279 --%>
    <script type="text/javascript">
        if (typeof BBB !== "undefined" && typeof BBB.gaTransData !== "undefined" && typeof BBB.gaTransData.items !== "undefined") {
            // BBB.gaTransData.items[BBB.gaTransData.items.length] = ["skuId", "skuName", "quantity"];
            BBB.gaTransData.items[BBB.gaTransData.items.length] = ["${commItem.catalogRefId}", "${fn:replace(fn:replace(skuName,'\'',''),'"','')}", "<dsp:valueof param="commerceItemRelationship.quantity"/>"];
        }
    </script>
	    <c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>

		<c:if test="${disableLazyLoadS7Images eq true}">
		        <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>
		</c:if>
		<c:choose>
			<c:when test="${not empty referenceNumber}">
				<c:choose>
					<c:when test="${not empty fullImagePath}">
					<ul class="grid_1 noMarLeft previewImageContent">
					<li>
						<img id="katoriFlag" width="63" height="63" title="${skuName}" alt="${skuName}" ${prodImageAttrib}="${fullImagePath}" /> </li>
					<li class="katoriFlagLink"><span class="icon-fallback-text"><span class="icon-zoomin" aria-hidden="true"></span><span class="icon-text"><bbbl:label key="lbl_pdp_product_image_zoom" language="${pageContext.request.locale.language}"/></span></span><a href='#' class="katoriFlagLinkText"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></a> </li>
					</ul>
					</c:when>
					<c:otherwise>
						<img id="katoriFlag" width="63" height="63" title="${skuName}" alt="${skuName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="fl" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
		<c:choose>
			<c:when test="${empty imgSrc}">
				<img width="63" height="63" title="${skuName}" alt="${skuName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="fl" />
			</c:when>
			<c:otherwise>
				<img width="63" height="63" title="${skuName}" alt="${skuName}" ${prodImageAttrib}="${scene7Path}/${imgSrc}"/>
			</c:otherwise>
		</c:choose>
			</c:otherwise>
		</c:choose>
			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
               <dsp:param name="id" param="commerceItemRelationship.commerceItem.repositoryItem.productId" />
               <dsp:param name="itemDescriptorName" value="product" />
               <dsp:param name="repositoryName"
                   value="/atg/commerce/catalog/ProductCatalog" />
               <dsp:oparam name="output">
                   <dsp:getvalueof var="finalUrl" vartype="java.lang.String"
                       param="url" />
               </dsp:oparam>
           </dsp:droplet>
         <dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem" />
			<dsp:getvalueof var="storeSKU" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.storeSKU" />
         <c:if test="${storeSKU || commItem.storeSKU}">
             	<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
     								 value="" />
          </c:if>
		<ul class="productInfoContainer">
			<li class="productName">
			<c:choose>
			<c:when test="${empty finalUrl}">
				<dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/>
			</c:when>
			<c:otherwise>
			<c:choose>
               <c:when test="${isFromOrderDetail}">
                 		<dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}" onclick="javascript:s_crossSell('Order History Online');"  title="${skuName}"><dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/></dsp:a>
               </c:when>
               <c:otherwise>
				<dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}" title="${skuName}"><dsp:valueof param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" valueishtml="true"/></dsp:a>
				</c:otherwise>
			</c:choose>
			</c:otherwise>
			</c:choose>
			</li>
			<li>
			<div class="prodAttribsCart">
				<c:if test='${not empty color}'>
					 <bbbl:label key="lbl_spc_item_color" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${color}" valueishtml="true" />
					</c:if>
				<c:if test='${not empty size}'>
					 <c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_spc_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${size}" valueishtml="true" />
					</c:if>
				<c:if test='${not empty personalizationOptions}'>
					<br>${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
				</c:if>
			</div>
				<dsp:getvalueof var="isFromPreviewPage" param="isFromPreviewPage"/>
				<c:if test="${isFromPreviewPage and giftWrapEligible}">
					<br/><bbbl:label key="lbl_spc_cartdetail_elegibleforgiftpackaging" language="<c:out param='${language}'/>"/>&#32;<bbbt:textArea key="txt_spc_giftpackaging_popup_link" language="<c:out param='${language}'/>"/>
				</c:if>
				<c:if test='${not empty referenceNumber}'>
					<div class="personalizationAttr katoriPrice">
					<%--BBBSL-8154 --%>
		              <%--  <span  aria-hidden="true">${personalizationOptionsDisplay}</span> --%>
					   <div class="priceAddText">
		                 <c:choose>
			                 <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
			                  <dsp:valueof value="${personalizePrice}" converter="defaultCurrency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
			                  <dsp:valueof value="${personalizePrice}" converter="defaultCurrency"/>
			                  <c:choose>
								<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
									<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
								</c:when>
								<c:otherwise>
									<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
								</c:otherwise>
							</c:choose>
							</c:when>
							<c:when test='${not empty personalizationType && personalizationType == "PB"}'>
							  <bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
							</c:when>
						</c:choose>
						</div>
	              	</div>
				</c:if>
			</li>
			<c:if test="${not isFromOrderDetail && empty commItem.storeId}">
			<li>	<ul class="squareBulattedList marTop_10">
					<c:if test="${commItem.skuSurcharge gt 0.0}">
					<li>
						<bbbl:label key="lbl_spc_cartdetail_surchargeapplies" language="<c:out param='${language}'/>"/>
					</li>
					</c:if>
	   </ul></li>
			</c:if>
			<!-- BBBH-3030 SDD Order Preview and Confirmation Page Changes start-->
			<dsp:getvalueof var="shippingMethod" param="shippingGroup.shippingMethod" />
			<c:choose>
			<c:when test="${shippingMethod eq 'SDD'}">
				<c:set var="sddSignatureThreshold"><bbbc:config key="sddSignatureThreshold" configName="SameDayDeliveryKeys" language="${pageContext.request.locale.language}"/></c:set>
				<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap" scope="request"/>
				<c:set target="${placeHolderMapShipFee}" property="displayCutOffTime" value="${displayCutoffTime}"/>
				<c:set target="${placeHolderMapShipFee}" property="displayGetByTime" value="${displayGetByTime}"/>
				<jsp:useBean id="signatureThresholdMap" class="java.util.HashMap" scope="request"/>
				<c:set target="${signatureThresholdMap}" property="signatureThreshold" value="${sddSignatureThreshold}"/>
				<!-- BBBH-3534 STORY 28 | Changes for SDD tracking  start -->
				<c:choose>
					<c:when test="${isFromOrderDetail}">
						<li class="smedaydel"><bbbl:label key="lbl_sdd_heading" language="${language}"/></li>
						<c:if test="${order.priceInfo.onlineStoreTotal gt sddSignatureThreshold}">
							<bbbt:textArea key="txt_sdd_item_level_signature" placeHolderMap="${signatureThresholdMap}" language="${pageContext.request.locale.language}" />
						</c:if>				
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty placeHolderMapShipFee}">
								<bbbt:textArea key="txt_sdd_order_available_desc" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<li class="smedaydel"><bbbl:label key="lbl_sdd_heading" language="${language}"/></li>
								<c:if test="${order.priceInfo.onlineStoreTotal gt sddSignatureThreshold}">
									<bbbt:textArea key="txt_sdd_item_level_signature" placeHolderMap="${signatureThresholdMap}" language="${pageContext.request.locale.language}" />
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<!-- BBBH-3534 STORY 28 | Changes for SDD tracking  end -->
			</c:when>
			<c:otherwise>
			<ul class="expectedDelivery cb padTop_5">
			<%-- BPSI-2326- VDC messaging - combine cart and PDP-Desktop --%>
			<dsp:droplet name="/atg/dynamo/droplet/Compare">
				<dsp:param name="obj1" param="shippingGroup.repositoryItem.type" />
				<dsp:param name="obj2" value="hardgoodShippingGroup" />
				<dsp:oparam name="equal">
					<dsp:droplet
						name="/atg/commerce/order/droplet/BBBExpectedDeliveryDroplet">
						<dsp:param name="shippingGroup" param="shippingGroup" />
						<dsp:param name="isFromOrderDetail" param="isFromOrderDetail" />
						<dsp:param name="skipAvailablityCheck" value="true" />
						<dsp:param name="orderDate" param="orderDate" />
						<dsp:param name="vdcSkuId" value="${commItem.catalogRefId}" />
						<dsp:param name="isVdcSku" value="${commItem.vdcInd}" />
						<c:if test="${ltlItem}">
			               <dsp:param name="ltlSkuId" value="${skuId}" />
						   <dsp:param name="isltlSku" value="${true}" />
						</c:if>
						<dsp:oparam name="expectedDeliveryDateOutput">
							<li class="bold padBottom_5">
								<bbbl:label key="lbl_spc_preview_expecteddelivery"
									language="<c:out param='${language}'/>" />
								<br>
								<dsp:valueof param="expectedDeliveryDate" />
							</li>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</ul>
		
		<%-- BPSI-2446 DSK VDC message & offset message changes --%>
			<c:if test="${commItem.vdcInd and not skuDetailVO.ltlItem and isFromPreviewPage}">
						   <dsp:getvalueof var="vdcMessage"
								param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.vdcSkuMessage" />

							<c:set var="vdcOffsetFlag">
								<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
							</c:set>
							<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
								<dsp:param name="shippingMethodCode" param="shippingGroup.shippingMethod" />
								<dsp:param name="skuId" value="${commItem.catalogRefId}"/>
								<dsp:param name="siteId" value="${commItem.auxiliaryData.siteId}"/>

								<dsp:oparam name="vdcMsg">
									<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC" />
								</dsp:oparam>

							</dsp:droplet>

							<c:if test="${vdcOffsetFlag}">
								<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
								<dsp:getvalueof var="shippingMethod" param="shippingGroup.shippingMethod"/>
								<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="${shippingMethod}" />
								<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${commItem.catalogRefId}" />

								<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
								<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
								<c:if test="${!ltlItem}">
									<li><bbbt:textArea key="txt_vdc_offset_msg"	placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
									<bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></li>
								</c:if>
							</c:if>
						</c:if>
			</c:otherwise>
			</c:choose>
			<!-- BBBH-3030 SDD Order Preview and Confirmation Page Changes end-->
		</ul>
	</li>
	<li class="grid_1 alpha textCenter">
		<span tabindex="0" aria-label="Quantity:<dsp:valueof param="commerceItemRelationship.quantity"/>"><dsp:valueof param="commerceItemRelationship.quantity"/></span>
	</li>
</dsp:page>