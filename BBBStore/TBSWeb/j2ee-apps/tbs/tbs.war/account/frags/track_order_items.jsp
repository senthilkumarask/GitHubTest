<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductStatusDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof var="commerceItemVOList" param="commerceItemVOList"/>
	<dsp:getvalueof var="registryMap" param="registryMap"/>
	<dsp:getvalueof var="fromOrderSummary" param="fromOrderSummary"/>

	<%-- need some config vars because they are not set when calling the frag directly from ajax --%>
	<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
	<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
		<c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="shippingGroup.commerceItemRelationshipVOList" />
		<dsp:param name="elementName" value="commerceItemRelationship" />
		<dsp:oparam name="output">
			<dsp:getvalueof param="commerceItemRelationship.commerceItemId" var="sgCommerceItemId"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="commerceItemVOList" />
				<dsp:param name="elementName" value="commerceItem" />
				<dsp:oparam name="output">
					<dsp:getvalueof param="commerceItem.commerceItemId" var="commerceItemId"/>
					<c:if test="${commerceItemId eq sgCommerceItemId}">
						<dsp:getvalueof param="commerceItem.registryId" var="registratantId"/>
						<dsp:getvalueof param="registryMap.${registratantId}" var="registratantVO"/>
						<dsp:getvalueof var="skuId" param="commerceItem.skuId" />
						<dsp:getvalueof var="productId" param="commerceItem.productId" />
						<dsp:getvalueof var="imgSrc" param="commerceItem.basicImage"/>
						<dsp:getvalueof var="skuName" param="commerceItem.skuDisplayName" />
						<dsp:getvalueof var="referenceNumber" param="commerceItem.referenceNumber"/>
						<dsp:getvalueof var="personalisedImage" param="commerceItem.fullImagePath"/>
						<dsp:getvalueof var="commItem" param="commerceItem"/>
						<dsp:droplet name="CanonicalItemLink">
						<dsp:param name="id" value="${productId}" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
						</dsp:oparam>
						</dsp:droplet>
						<div class="row">
							<c:if test="${not empty registratantVO}">
								<div class="small-12 columns">
									<h4 class="product-registry">
										<span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/></span>
										<span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/></span>
										<span><dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/></span>
										<span><bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>
									</h4>
								</div>
							</c:if>


<%--							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes starts--%>
							<div class="small-4 columns">
								<c:choose>
										<c:when test="${empty imgSrc}">
											<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" width="63" height="63"/>
											</dsp:a>
										</c:when>
										<c:when test="${not empty referenceNumber}">
												<img id="katoriFlag" ${prodImageAttrib}="${personalisedImage}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" class="noImageFound" width="63" height="63"/>
												<div class="zoomin-link-wrapper">
														<a onclick="javascript:s_crossSell('Order History Online');" title="${skuName}" page="#" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}" class="zoomin-link" data-reveal-id="previewImageModalPopup_${commItem.commerceItemId}">
															<span class="zoomin-icon"></span>
															<span class="zoomin-label"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span>
														</a>
											     </div>
									<div id="previewImageModalPopup_${commItem.commerceItemId}" class="reveal-modal small" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal="">
                                     <img src="${personalisedImage}" alt="${commItem.skuDisplayName}&nbsp;$${unitListPrice}" title="${commItem.skuDisplayName}&nbsp;$${unitListPrice}"/>
                                        <a class="close-reveal-modal" aria-label="Close">&#215;</a>
										 </div>

										</c:when>
										<c:otherwise>
											<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">
												<img ${prodImageAttrib}="${scene7Path}/${imgSrc}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" class="noImageFound" width="63" height="63"/>
											</dsp:a>
										</c:otherwise>
									</c:choose>
							</div>
<%-- 							BBBH-2394 | Story18: Display Personalized Items on Trak  Order & Order Inquiry pages and Order Confirmation Email |changes ends--%>
							<div class="small-8 columns">
								<c:choose>
									<c:when test="${empty finalUrl}">
										<dsp:valueof param="skuName" valueishtml="true"/>
									</c:when>
									<c:otherwise>
										<dsp:a page="${finalUrl}?skuId=${skuId}" iclass="prod-link" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">${skuName}</dsp:a>
									</c:otherwise>
								</c:choose>
									<%--BBBSL-11060 | Removing the product active check to display write a review button --%>
				<%-- 				<dsp:droplet name="ProductStatusDroplet">
									<dsp:param name="productId" value="${productId}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="isProductActive" param="isProductActive" />
									</dsp:oparam>
								</dsp:droplet> --%>
								<c:if test="${writeReviewOn and empty registratantVO}">
									<div class="bvSubmitReviewButtonContainer">
										<input type="button" onclick="javascript:s_crossSell('write a review - track order');" class="triggerBVsubmitReview tiny button secondary" data-BVProductId="${productId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"></input>
									</div>
								</c:if>
							</div>
						</div>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
