<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>	
<dsp:getvalueof var="prodSmallImage" param="commItem.auxiliaryData.catalogRef.thumbnailImage"/>
<dsp:getvalueof var="giftWrapEligible" param="SKUDetailVO.giftWrapEligible"/>
<dsp:getvalueof var="skuName" param="SKUDetailVO.displayName"/>
<dsp:getvalueof var="unitPrice" param="commItem.priceInfo.listPrice"/>
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="quantity" param="quantity" />
<dsp:getvalueof var="listPrice" param="commItem.priceInfo.listPrice" />
<dsp:getvalueof var="salePrice" param="commItem.priceInfo.salePrice" />
<dsp:getvalueof var="cisiShipGroupName" param="cisiShipGroupName" />
<dsp:getvalueof var="skuColor" param="SKUDetailVO.color" />
<dsp:getvalueof var="skuSize"  param="SKUDetailVO.size" />
<dsp:getvalueof var="productVO"  param="productVO" />
<dsp:getvalueof var="ltlItemFlag" param="SKUDetailVO.ltlItem" />
<dsp:getvalueof var="personalizationType" param="SKUDetailVO.personalizationType" />

<dsp:getvalueof var="referenceNumber" param="commItem.referenceNumber" />
<dsp:getvalueof var="personalizationOptions" param="commItem.personalizationOptions" />
<dsp:getvalueof var="personalizationOptionsDisplay" param="commItem.personalizationOptionsDisplay" />
<dsp:getvalueof var="personalizePrice" param="commItem.personalizePrice" />
<dsp:getvalueof var="personalizationDetails" param="commItem.personalizationDetails" />
<dsp:getvalueof var="fullImagePath" param="commItem.fullImagePath" />
<dsp:getvalueof var="showPorchMultishipErr" param="showPorchMultishipErr" />


<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
	<dsp:oparam name="output">
		<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	</dsp:oparam>
</dsp:droplet>

<c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
<c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
<c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>        
</c:if>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param param="productVO.rollupAttributes" name="array" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="menu" param="key"/>${menu}
		<c:if test="${menu eq 'FINISH'}">
			<c:set var="rollupAttributesFinish" value="true" />
		</c:if>
	</dsp:oparam>
</dsp:droplet>

<li class="grid_2 alpha itemDetails">	
	<c:choose>
		<c:when test="${empty prodSmallImage}">
			<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="productImage" />
		</c:when>
		<c:when test="${not empty referenceNumber}">
			<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" ${prodImageAttrib}="${fullImagePath}"/>
		</c:when>
		<c:otherwise>
			<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" ${prodImageAttrib}="${scene7Path}/${prodSmallImage}"/>
		</c:otherwise>
	</c:choose>
	<ul class="marTop_10">
		<li class="productName">
			<dsp:valueof param="productVO.name" valueishtml="true"/>
		</li>
		<li>
			<span class="prodAttribsMultiShipping">
				<c:if test='${not empty skuColor}'>
					<c:choose>
						<c:when test="${rollupAttributesFinish == 'true'}">
							<bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
						</c:when>
						<c:otherwise>
							<bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test='${not empty skuSize}'>
					<c:if test='${not empty skuColor}'><br/></c:if>
					<bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" />
				</c:if>
				<c:if test='${not empty personalizationOptions}'>
					<br>${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
				</c:if>
				<c:if test='${not empty referenceNumber}'>
					<div class="personalizationAttr katoriPrice">
					<%--BBBSL-8154 --%>
		               <%-- <span  aria-hidden="true">${personalizationOptionsDisplay}</span> --%>
					   <div class="priceAddText">
					   <c:choose>
			                <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
								<dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
								<dsp:valueof value="${personalizePrice}" converter="currency"/> 
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
			</span>
		</li>
		<li class="noMarBot">
			<strong>
				<c:choose>
                   <c:when test="${salePrice gt 0.0}">
                       <dsp:valueof value="${salePrice}" converter="currency"/>
                   </c:when>
                   <c:otherwise>
                       <dsp:valueof value="${listPrice}" converter="currency"/>
                   </c:otherwise>
               </c:choose>
			</strong>
		</li>
		<li><span class="smallText">
			<bbbl:label key="lbl_shipping_qty" language="<c:out param='${language}'/>"/>:
		</span> 
			<dsp:valueof param="cisi.quantity" />				
		</li>
		<c:if test="${quantity gt 1}">
			<li>
			<a href="#" class="lnkShipToMultiple" data-submitButtonID="shipToMultiplePeople" data-hiddenFieldNameValuePairs="{shipToMultiplePeople_cisiIndex:'${index}',cisiShipGroupName:'${cisiShipGroupName}', shipToMultiplePeople_shippingGr:'true'}">
                    <bbbl:label key="lbl_shipping_ship_to_multi_ppl" language="<c:out param='${language}'/>"/>
            </a>
			<%--	 <dsp:a page="/checkout/shipping/shipping.jsp">
					<dsp:property bean="BBBShippingGroupFormhandler.cisiIndex" value="${index}" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeople" value="true" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeopleSuccessURL" value="/checkout/shipping/multiShipping.jsp" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeopleErrorURL" value="/checkout/shipping/multiShipping.jsp" />
					<bbbl:label key="lbl_shipping_ship_to_multi_ppl" language="<c:out param='${language}'/>"/>
					<dsp:param name="shippingGr" value="multi"/>
				</dsp:a> --%>
			</li>
		</c:if>
		<c:if test="${giftWrapEligible}">
			<li class="smallText">
				<bbbl:label key="lbl_shipping_eligible_gift_pack" language="<c:out param='${language}'/>"/>
			</li>
		</c:if>
		<%-- 	PORCH display
				Conditions to check for:
				* is global porch config on?
				* is porch cart config on
				* is this product service eligible?
				* does this item have a service attached?
			--%>
			<%-- TODO
				* show service attached string from commerce item
				* BCC label for disclaimer text
			--%>
		<dsp:droplet name="Switch">
			<dsp:param name="value" param="commItem.porchService"/>
			<dsp:oparam name="true">	

				<c:set var="lbl_porch_tooltip"><bbbl:label key="lbl_porch_tooltip" language="<c:out param='${language}'/>"/></c:set>

				<li>					
					<div class="porchServiceAdded" >
							
						<%-- this should come from the commerce item data --%>
						<span class="serviceType">
							<dsp:valueof param="commItem.porchServiceType"/>

							<c:if test="${not empty lbl_porch_tooltip}">
								<span class="whatsThis">
									<span class="icon-fallback-text"> 
                                       <a class="info icon icon-question-circle"   aria-hidden="true">                                                                                                      
                                          <span>${lbl_porch_tooltip}</span> 
                                       </a>
                                       <span class="icon-text">${lbl_porch_tooltip}</span>
                                    </span>
                                </span>
							</c:if>
						</span>
						
						<%-- this should come from the commerce item data --%>
						
					 		<dsp:getvalueof var="priceEstimation" param="commItem.priceEstimation"/>
							<c:choose>
							
								<c:when test ="${priceEstimation ne null}">	
								<p class="serviceEstimate">																						
								<bbbl:label key="lbl_bbby_porch_service_estimated_price" language="<c:out param='${language}'/>"/>
								${priceEstimation}
								</p>
								<c:if test="${empty lbl_porch_tooltip}">
									<p class="serviceDisclaimer">
									<bbbl:label key="lbl_bbby_porch_service_disclaimer" language="<c:out param='${language}'/>"/>	
									</p>
								</c:if>
								</c:when>
								<c:otherwise>
								<p class="serviceEstimate">
								<bbbl:label key="lbl_porch_service_estimated_by_pro" language="<c:out param='${language}'/>"/>
								</p>
								</c:otherwise>
							</c:choose>	
						</p>


						
					</div> 
					<div class="clear"></div>
				</li>
			</dsp:oparam>
		</dsp:droplet> 

	</ul>	
</li>
</dsp:page>