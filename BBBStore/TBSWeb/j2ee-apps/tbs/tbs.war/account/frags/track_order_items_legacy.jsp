<dsp:page>
	<dsp:getvalueof var="cartDetailInfo" param="cartDetailInfo"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
<c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>

	<c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>        
    </c:if>
	<div class="grid_6 omega">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" value="${cartDetailInfo.cartItemDetailList}" />
			<dsp:param name="elementName" value="cartItemDetails" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="cartItemDetails" param="cartItemDetails" />
				<c:set var="skuId" value="${cartItemDetails.SKU}" />
				<c:set var="registryID" value="${cartItemDetails.registryID}" />
				<c:set var="imgSrc" value="${cartItemDetails.photoURL}"/>
				<c:set var="skuName" value="${cartItemDetails.itemDesc}" />
				<c:set var="productId"></c:set>
				<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
					<dsp:param name="skuId" value="${skuId}" />
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
						</dsp:droplet>
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
					</c:otherwise>
				</c:choose>
				<div class="registry-product clearfix">
					<c:if test="${cartItemDetails.registryID gt 0}">
						<h4 class="product-registry">
							<span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/>&nbsp;</span>                                                
							<span>${cartItemDetails.registrantNm}<c:if test="${not empty cartItemDetails.coRegistrantNm}">&nbsp;&amp;&nbsp;${cartItemDetails.coRegistrantNm}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/></span>                                       
							<span>&nbsp;<bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>	
						</h4>
					</c:if>
					<div class="grid_1 alpha">
						<c:choose>
							<c:when test="${empty imgSrc}">
								<c:choose>
									<c:when test="${empty finalUrl}">
										<img src="${imagePath}/_assets/global/images/no_image_available.jpg?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" width="63" height="63"/>
									</c:when>
									<c:otherwise>
										<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Track Order');" title="${skuName}">
											<img ${prodImageAttrib}="${imagePath}/_assets/global/images/no_image_available.jpg?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" class="noImageFound" width="63" height="63"/>
										</dsp:a>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${empty finalUrl}">
										<img src="${scene7Path}/${imgSrc}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}"/>
									</c:when>
									<c:otherwise>
										<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">
											<img src="${scene7Path}/${imgSrc}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}"/>
										</dsp:a>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="grid_3 product-descript">
						<c:choose>
							<c:when test="${empty finalUrl}">
								<dsp:valueof param="skuName" valueishtml="true"/>
							</c:when>
							<c:otherwise>
								<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">${skuName}</dsp:a>
							</c:otherwise>
						</c:choose>
					</div>
					<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
						<dsp:param name="productId" value="${productId}" />
						<dsp:oparam name="output">
								<dsp:getvalueof var="isProductActive" param="isProductActive" />
						</dsp:oparam>
					</dsp:droplet>
					<div class="grid_2 omega">
						<c:if test="${writeReviewOn and isProductActive and cartItemDetails.registryID eq 0}">
							<div class="button fr clearfix bvSubmitReviewButtonContainer">
								<input type="button" onclick="javascript:s_crossSell('write a review - track order');" class="triggerBVsubmitReview" data-BVProductId="${productId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"></input>
							</div>
						</c:if>
					</div>										
				</div>
			</dsp:oparam>
		</dsp:droplet>			
	</div>
</dsp:page>