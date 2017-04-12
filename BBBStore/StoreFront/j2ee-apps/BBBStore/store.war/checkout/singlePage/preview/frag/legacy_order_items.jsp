<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<ul class="productsListHeader">
		<li class="grid_3"><strong><bbbl:label key="lbl_spc_cartdetail_item" language="<c:out param='${language}'/>" />	</strong></li>
		<li class="grid_1 alpha textCenter"><strong><bbbl:label key="lbl_spc_cartdetail_quantity" language="<c:out param='${language}'/>" /> </strong></li>
		<li class="grid_1 textCenter"><strong><bbbl:label key="lbl_spc_cartdetail_unitprice" language="<c:out param='${language}'/>" /> </strong></li>
		<li class="grid_3 textRight yourPrice"><strong><bbbl:label key="lbl_spc_you_pay" language="<c:out param='${language}'/>"/></strong></li>
		<li class="grid_2 alpha omega textRight"><strong><bbbl:label key="lbl_spc_cartdetail_totalprice" language="<c:out param='${language}'/>" /> </strong></li>
	</ul>
	<dsp:getvalueof id="orderHeaderInfo"
		param="orderDetails.orderInfo.orderHeaderInfo" />
	<dsp:getvalueof id="cartDetailInfo"
		param="orderDetails.orderInfo.cartDetailInfo" />
	<c:set var="totalQty" value="0" />
	<c:set var="totalExtPrice" value="0" />
	<c:set var="totalUnitPrice" value="0" />
	<c:set var="totalDisAmt" value="0" />
	
	<c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
    <c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>        
    </c:if>
	
	<ul class="gridItemWrapper noMar">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" value="${cartDetailInfo.cartItemDetailList}" />
			<dsp:param name="elementName" value="cartItemDetails" />
			<dsp:oparam name="output">
				<li>				
					<ul class="clearfix">
					<dsp:getvalueof var="cartItemDetails" param="cartItemDetails" />
					
						<li class="grid_3"><img width="63" height="63"
							title="${cartItemDetails.itemDesc}"
							alt="${cartItemDetails.itemDesc}"
							${prodImageAttrib}="${scene7Path}/${cartItemDetails.photoURL}" />
							<c:set var="productId"></c:set>
							<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
								<dsp:param name="skuId" value="${cartItemDetails.SKU}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
								</dsp:oparam>
							</dsp:droplet>
							<c:choose>
								<c:when test="${not empty productId}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" value="${productId}" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								</dsp:oparam>
								<dsp:oparam name="empty">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
								</dsp:oparam>
								</dsp:droplet>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
								</c:otherwise>
							</c:choose>
							<ul class="productInfoContainer">
								<li class="productName">
									<c:choose>
										<c:when test="${not empty finalUrl}">
											<dsp:a page="${finalUrl}"> 
												<c:out value="${cartItemDetails.itemDesc}" escapeXml="false"/>
											</dsp:a>
										</c:when>
										<c:otherwise>
											<c:out value="${cartItemDetails.itemDesc}" escapeXml="false"/>
										</c:otherwise>
									</c:choose>
									
								</li>
							</ul>
						</li>
						<li class="grid_1 alpha textRight"><span>${cartItemDetails.orderQty}  </span>
						</li>
						<c:set var="totalQty"
									value="${totalQty+ cartItemDetails.orderQty}" />
						<c:set var="totalExtPrice"
						value="${totalExtPrice + (cartItemDetails.orderQty * cartItemDetails.extPrice)}" />
						<c:set var="totalUnitPrice"
						value="${totalUnitPrice + (cartItemDetails.orderQty * cartItemDetails.unitCost)}" />
						<c:set var="totalDisAmt"
						value="${totalDisAmt + (cartItemDetails.orderQty * cartItemDetails.discountAmt)}" />
						  
						<dsp:include
							page="/checkout/singlePage/preview/frag/legacy_order_Item_price.jsp"
							flush="true">							
							<dsp:param name="cartItemDetails" param="cartItemDetails" />
						</dsp:include>
					</ul>
				</li>
			</dsp:oparam>
		</dsp:droplet>
	</ul>
	<dsp:include
		page="/checkout/singlePage/preview/frag/legacy_order_subtotal_details.jsp"
		flush="true">
		<dsp:param name="orderHeaderInfo" value="${orderHeaderInfo}" />
		<dsp:param name="totalQty" value="${totalQty}" />
		<dsp:param name="totalDisAmt" value="${totalDisAmt}" />
		<dsp:param name="disPercentage" value="${(totalDisAmt/totalExtPrice)*100}" number="0.00" />
	</dsp:include>
	
	<dsp:include
		page="/checkout/singlePage/preview/frag/legacy_order_billing_details.jsp"
		flush="true">
		<dsp:param name="orderDetails" param="orderDetails" />
		<dsp:param name="totalQty" value="${totalQty}" />
		<dsp:param name="totalExtPrice" value="${totalExtPrice}" />
		<dsp:param name="totalUnitPrice" value="${totalUnitPrice}" />
		<dsp:param name="totalDisAmt" value="${totalDisAmt}" />		
	</dsp:include>
</dsp:page>

