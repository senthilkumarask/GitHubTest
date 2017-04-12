<dsp:page>
	<dsp:getvalueof var="commerceItemVOList" param="commerceItemVOList"/>
	<dsp:getvalueof var="registryMap" param="registryMap"/>
	<dsp:getvalueof var="fromOrderSummary" param="fromOrderSummary"/>
	
<%-- need some config vars because they are not set when calling the frag directly from ajax --%>	
<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
<c:set var="prodImageAttrib" scope="request">class="fl productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
<c:if test="${disableLazyLoadS7Images eq true}">
       <c:set var="prodImageAttrib" scope="request">class="fl productImage noImageFound" src</c:set>        
</c:if>

	<div class="grid_6 omega">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="shippingGroup.commerceItemRelationshipVOList" />
			<dsp:param name="elementName" value="commerceItemRelationship" />
			<dsp:oparam name="output">
				<dsp:getvalueof param="commerceItemRelationship.commerceItemId" var="sgCommerceItemId"/>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
   							<dsp:getvalueof var="totalIndex" param="size"/>
   							<dsp:getvalueof var="dataIndex" param="count"/>
   							<dsp:getvalueof var="referenceNumber" param="commerceItem.referenceNumber"/>
							<dsp:getvalueof var="personalisedImage" param="commerceItem.fullImagePath"/>
	
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
				               <dsp:param name="id" value="${productId}" />
				               <dsp:param name="itemDescriptorName" value="product" />
				               <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
				               <dsp:oparam name="output">
				                   <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
				               </dsp:oparam>
							</dsp:droplet>
							<div class="registry-product clearfix">
								<c:if test="${not empty registratantVO}">
									<h4 class="product-registry">
										<span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/>&nbsp;</span>                                                
										<span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/></span>                                       
										<span><dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/></span>
										<span>&nbsp;<bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>	
									</h4>
								</c:if>
								<div class="grid_1 alpha previewImageContent">
									<c:choose>
										<c:when test="${empty imgSrc}">
											<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" width="63" height="63"/>
											</dsp:a>
										</c:when>
										<c:when test="${not empty referenceNumber}">
											
												<img id="katoriFlag" ${prodImageAttrib}="${personalisedImage}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" class="noImageFound" width="63" height="63"/>
												<div class="katoriFlagLink 11">
													<span class="icon-fallback-text"><span class="icon-zoomin" aria-hidden="true"></span><span class="icon-text">Zoom</span></span><a onclick="javascript:s_crossSell('Order History Online');" title="${skuName}" page="#" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}" class="katoriFlagLinkText"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></a>
												</div>
											
										</c:when>
										<c:otherwise>
											<dsp:a page="${finalUrl}?skuId=${skuId}" onclick="javascript:s_crossSell('Order History Online');" title="${skuName}">
												<img ${prodImageAttrib}="${scene7Path}/${imgSrc}?wid=63&amp;hei=63&amp;" alt="${skuName}" title="${skuName}" class="noImageFound" width="63" height="63"/>
											</dsp:a>
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



									<%-- PORCH display
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
									<dsp:droplet name="/atg/dynamo/droplet/Switch">
										<%--<dsp:param name="value" param="commerceItemRelationship.commerceItem.porchService"/>--%>
										<dsp:param name="value" param="commerceItem.porchService"/>
										<dsp:oparam name="true">			
											<div class="porchServiceAdded">
												<%-- this should come from the commerce item data --%>
												<span class="serviceType"><dsp:valueof param="commerceItem.porchServiceFamilyType"/></span>
												<dsp:droplet name="/atg/dynamo/droplet/IsNull">
												<dsp:param name="value" param="commerceItem.porchProjectId"/>
												<dsp:oparam name="false">
													<c:set var="serviceStatusURL"><bbbc:config key="serviceStatusURL" configName="PorchServiceKeys"/></c:set>
													<dsp:getvalueof var="porchProjectId" param="commerceItem.porchProjectId"/> 
													<c:set var="porchURL" value="${fn:replace(serviceStatusURL,'projectId', porchProjectId)}" />
													<span class="projectId"><dsp:a target="_blank" href="${porchURL}"><bbbl:label key="lbl_bbby_porch_project_status" language="<c:out param='${language}'/>"/></dsp:a>  </span>
												</dsp:oparam>
												</dsp:droplet>
												
												
												
											</div>         
											<div class="clear"></div> 
										</dsp:oparam>
									</dsp:droplet> 	


								</div>
								<%--BBBSL-11060 | Removing the product active check to display write a review button --%>
				<%-- 				<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
									<dsp:param name="productId" value="${productId}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="isProductActive" param="isProductActive" />
									</dsp:oparam>
								</dsp:droplet> --%>
								<div class="grid_2 omega">
									<c:if test="${writeReviewOn and empty registratantVO}">
									<c:choose>
									<c:when test="${not empty fromOrderSummary}">
										<div class="button fr clearfix bvSubmitReviewButtonContainer">
											<input type="button" onclick="javascript:customLinkTracking('write a review - order history');" class="triggerBVsubmitReview" data-BVProductId="${productId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"></input>
										</div>
										
									</c:when>
									<c:otherwise>
									<div class="button fr clearfix bvSubmitReviewButtonContainer">
											<input type="button" onclick="javascript:customLinkTracking('write a review - track order');" class="triggerBVsubmitReview" data-BVProductId="${productId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"></input>
									</div>
									</c:otherwise>
									</c:choose>
									<div id="sa_track-${orderId}-${dataIndex}"></div>
										<c:set var="CurrentProduct" value="sa_track-${orderId}-${dataIndex}:${productId}-${orderId}-${dataIndex}"/>
										<div id="currentProduct" data-trackproduct="${CurrentProduct}" class="hidden"></div>
										
									</c:if>
								</div>										
							</div>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</div>
</dsp:page>