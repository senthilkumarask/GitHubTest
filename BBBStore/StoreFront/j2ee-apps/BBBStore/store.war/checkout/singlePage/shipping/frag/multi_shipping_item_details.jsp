<dsp:page>
 <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
 
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
<c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>

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
							<bbbl:label key="lbl_spc_item_finish" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
						</c:when>
						<c:otherwise>
							<bbbl:label key="lbl_spc_item_color" language="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuColor}" valueishtml="true" />
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test='${not empty skuSize}'>
					<c:if test='${not empty skuColor}'><br/></c:if>
					<bbbl:label key="lbl_spc_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" />
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
			<bbbl:label key="lbl_spc_shipping_qty" language="<c:out param='${language}'/>"/>:
		</span> 
			<dsp:valueof param="cisi.quantity" />				
		</li>
		<c:if test="${quantity gt 1}">
			<li>
			<a href="#" class="lnkShipToMultiple" data-submitButtonID="shipToMultiplePeople" data-hiddenFieldNameValuePairs="{shipToMultiplePeople_cisiIndex:'${index}',cisiShipGroupName:'${cisiShipGroupName}', shipToMultiplePeople_shippingGr:'true'}">
                    <bbbl:label key="lbl_spc_shipping_ship_to_multi_ppl" language="<c:out param='${language}'/>"/>
            </a>
			<%--	 <dsp:a page="/checkout/shipping/shipping.jsp">
					<dsp:property bean="BBBShippingGroupFormhandler.cisiIndex" value="${index}" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeople" value="true" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeopleSuccessURL" value="/checkout/shipping/multiShipping.jsp" />
					<dsp:property bean="BBBShippingGroupFormhandler.shipToMultiplePeopleErrorURL" value="/checkout/shipping/multiShipping.jsp" />
					<bbbl:label key="lbl_spc_shipping_ship_to_multi_ppl" language="<c:out param='${language}'/>"/>
					<dsp:param name="shippingGr" value="multi"/>
				</dsp:a> --%>
			</li>
		</c:if>
		<c:if test="${giftWrapEligible}">
			<li class="smallText">
				<bbbl:label key="lbl_spc_shipping_eligible_gift_pack" language="<c:out param='${language}'/>"/>
			</li>
		</c:if>
	</ul>	
</li>
</dsp:page>