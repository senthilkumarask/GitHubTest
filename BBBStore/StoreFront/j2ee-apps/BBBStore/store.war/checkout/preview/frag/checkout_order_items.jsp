<dsp:page>
<c:set var="skuIds" scope="request"/>
<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
<dsp:getvalueof var="order" param="order"/>
<dsp:getvalueof var="prefixToAddDataIndex" param="prefixToAddDataIndex"/>
    <dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <ul class="productsListHeader">
 		<li class="grid_3"><strong><bbbl:label key="lbl_cartdetail_item" language="<c:out param='${language}'/>"/></strong></li>
 		<li class="grid_1 alpha textCenter"><strong><bbbl:label key="lbl_cartdetail_quantity" language="<c:out param='${language}'/>"/></strong></li>
 		<li class="grid_1 textCenter"><strong><bbbl:label key="lbl_cartdetail_unitprice" language="<c:out param='${language}'/>"/></strong></li>
 		<li class="grid_3 textRight yourPrice"><strong><bbbl:label key="lbl_you_pay" language="${language}"/></strong></li>
 		<li class="grid_2 alpha omega textRight"><strong><bbbl:label key="lbl_cartdetail_totalprice" language="<c:out param='${language}'/>"/></strong></li>
 	</ul>
	<ul class="gridItemWrapper noMar">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
			<dsp:param name="elementName" value="commerceItemRelationship" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="dataIndex" param="count"/>
				<dsp:droplet name="/atg/dynamo/droplet/Compare">
					<dsp:param name="obj1" param="commerceItemRelationship.commerceItem.repositoryItem.type"/>
						 <c:choose>
							 <c:when test="${order.TBSAssociateID ne null && order.TBSAssociateID ne '' }">
						  		<dsp:param name="obj2" value="tbsCommerceItem"/>
						  	 </c:when>
							 <c:otherwise>
								 <dsp:param name="obj2" value="bbbCommerceItem"/>
							 </c:otherwise>
						</c:choose>
					<dsp:oparam name="equal">
						<dsp:getvalueof param="commerceItemRelationship.commerceItem.registryId" var="registratantId"/>
						<dsp:getvalueof param="order.registryMap.${registratantId}" var="registratantVO"/>
						<dsp:getvalueof var="skuId" param="commerceItemRelationship.commerceItem.catalogRefId" />
						<c:choose>
							<c:when test="${not empty skuIds}">
								<c:set var="skuIds" scope="request">
									${skuIds},${skuId}
								</c:set>

							</c:when>
							<c:otherwise>
								<c:set var="skuIds" scope="request">
									${skuId}
								</c:set>
							</c:otherwise>

						</c:choose>
						<c:choose>
						 <c:when test="${registratantId ne null}">
							 <li class="registeryItem">
								<div class="registeryItemHeader clearfix">
									 <div class="grid_4 noMar">
										<span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/></span>
										<span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/></span>
										<span><dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/></span>
										<span><bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>
									</div>
								</div>
						 </c:when>
                         <c:otherwise>
							<li>
						 </c:otherwise>
						</c:choose>

							<ul class="clearfix">
								<dsp:getvalueof var="isFromPreviewPage" param="isFromPreviewPage"/>
								<dsp:include page="/checkout/preview/frag/sku_details.jsp" flush="true">
									<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
									<dsp:param name="isFromOrderDetail" value="${isFromOrderDetail}"/>
									<dsp:param name="isFromPreviewPage" value="${isFromPreviewPage}"/>
									<dsp:param name="order" param="order"/>
								</dsp:include>
								<dsp:include page="/checkout/preview/frag/confirm_detailed_Item_price.jsp" flush="true">
									<dsp:param name="commerceItemRelationship" param="commerceItemRelationship"/>
									<dsp:param name="shippingGroup" param="shippingGroup"/>
									<dsp:param name="promoExclusionMap" param="promoExclusionMap"/>
									<dsp:param name="order" param="order"/>
								</dsp:include>
								<dsp:getvalueof param="commerceItemRelationship.commerceItem.repositoryItem.productId" var="prodId"/>
							</ul>

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
								<dsp:param name="value" param="commerceItemRelationship.commerceItem.porchService"/>
								<dsp:oparam name="true">
									<div class="porchServiceAdded">
										<c:choose>
											<c:when test="${isFromOrderDetail}">
											 
												<%-- this should come from the commerce item data --%>
												<span class="serviceType">	<dsp:valueof param="commerceItemRelationship.commerceItem.porchServiceType"/> </span>
												<%--<span class="projectId">Project ID: OU812-UR2FAT</span>--%>
													<dsp:droplet name="/atg/dynamo/droplet/IsNull">
												<dsp:param name="value" param="commerceItemRelationship.commerceItem.porchServiceRef.porchProjectId"/>
												<dsp:oparam name="false">
													<c:set var="serviceStatusURL"><bbbc:config key="serviceStatusURL" configName="PorchServiceKeys"/></c:set>
													<dsp:getvalueof var="porchProjectId" param="commerceItemRelationship.commerceItem.porchServiceRef.porchProjectId"/> 
													<c:set var="porchURL" value="${fn:replace(serviceStatusURL,'projectId', porchProjectId)}" />
													<span class="projectId"> <dsp:a target="_blank" href="${porchURL}"><bbbl:label key="lbl_bbby_porch_project_status" language="<c:out param='${language}'/>"/></dsp:a> </span>
												</dsp:oparam>
												</dsp:droplet>
											</c:when>
											<c:otherwise>

												<c:set var="lbl_porch_tooltip"><bbbl:label key="lbl_porch_tooltip" language="<c:out param='${language}'/>"/></c:set>

												<%-- this should come from the commerce item data --%>
												<p>
													<span class="serviceType"><dsp:valueof param="commerceItemRelationship.commerceItem.porchServiceType"/></span>

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

												</p>
												
												<dsp:getvalueof var="priceEstimation" param="commerceItemRelationship.commerceItem.priceEstimation"/>	
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
												
											</c:otherwise>
										</c:choose>
									</div>         
								  
								</dsp:oparam>
							</dsp:droplet>

							<c:if test="${writeReviewOn}">
								<%--BBBSL-11060 | Removing the product active check to display write a review button --%>
									<%-- <dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
										<dsp:param name="productId" value="${prodId}" />
										<dsp:oparam name="output">
												<dsp:getvalueof var="isProductActive" param="isProductActive" />
										</dsp:oparam>
									</dsp:droplet> --%>
									<c:if test="${isFromOrderDetail and (registratantId eq null)}">
										<div class="button button_secondary orderHistoryReviewButton bvSubmitReviewButtonContainer">
										<c:choose>
							               <c:when test="${!isLoggedIn}">
							                 	<input type="button" class="triggerBVsubmitReview" onclick="javascript:s_crossSell('write a review - order history');" data-BVProductId="${prodId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"/>
							                  </c:when>
							                <c:otherwise>
												<input type="button" class="triggerBVsubmitReview" onclick="javascript:s_crossSell('write a review- track order');" data-BVProductId="${prodId}" value="<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />"/>
											</c:otherwise>
							             </c:choose>
										<c:set var="dataIndex" value="${dataIndex + prefixToAddDataIndex}"/>
										<c:set var="CurrentProduct" value="sa_track-${dataIndex}:${prodId}-${dataIndex}"/>
										<div id="currentProduct" data-trackproduct="${CurrentProduct}" class="hidden"></div>
										</div>
										<div id="sa_track-${dataIndex}"></div>
									</c:if>
							</c:if>

							<dsp:droplet name="/atg/dynamo/droplet/Compare">
								<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
								<dsp:param name="obj2" value="storePickupShippingGroup"/>
								<dsp:oparam name="equal">
										<dsp:getvalueof var="registryId" param="commerceItemRelationship.commerceItem.registryId"/>
										<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
											<dsp:param name="value" value="${registryId}"/>
											<dsp:oparam name="false">
												<p class="regItemBopus">
													<strong><bbbl:label key="lbl_gift_registry_store_pickup_part1" language="<c:out param='${language}'/>"/></strong>
												</p>
											</dsp:oparam>
										</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</li>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</ul>
</dsp:page>