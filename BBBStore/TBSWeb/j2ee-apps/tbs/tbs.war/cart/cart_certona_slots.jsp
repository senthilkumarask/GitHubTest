<dsp:page>
<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/> 
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<dsp:getvalueof var="key" param="key"/>	 
<div class="certonaProducts clearfix">
    
    <dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient"/>
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id"/>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	 
	<c:set var="lastMinItemsMax" scope="request">
	  <bbbc:config key="LastMinItemsMax" configName="CertonaKeys" />
	</c:set>
	 
    <dsp:droplet name="CertonaDroplet">
		 <dsp:param name="scheme" value="fc_lmi"/>
		 <dsp:param name="context" param="CertonaContext"/>
		 <dsp:param name="exitemid" param="RegistryContext"/>
		 <dsp:param name="userid" value="${userId}"/>
		 <dsp:param name="number" value="${lastMinItemsMax}"/>
		 <dsp:param name="siteId" value="${appid}"/>
		 <dsp:param name="shippingThreshold" param="shippingThreshold"/>
		 <dsp:oparam name="output">
			<dsp:getvalueof var="skuDetailVOList" param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuDetailVOList"/>
			
		  
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" value="${skuDetailVOList}"/>	
				<dsp:param name="elementName" value="skuVO"/>	
					<dsp:oparam name="outputStart">
						<h6><bbbl:label key='lbl_certona_slots_last_minute_items' language="${pageContext.request.locale.language}" /></h6>
					</dsp:oparam>									
					<dsp:oparam name="output">
						<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
						<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
						<div class="certonaProduct clearfix">
							<dsp:form name="frmCertonaProduct" method="post">
								<dsp:getvalueof var="skuId" param="skuVO.skuId"/>
					            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuParentProductMap"/>					
									<dsp:oparam name="output">
										<dsp:getvalueof var="skuIdParam" param="key"/>
										<c:if test="${skuIdParam eq skuId}">
											<dsp:getvalueof var="productIdParam" param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuParentProductMap.${skuIdParam}"/>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" />
								<dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="cartCertonaSlots" />	
								<%-- <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${contextPath}/cart/cart.jsp"/>						
								<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" value="${contextPath}/cart/cart.jsp"/> --%>
								<dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" iclass="addItemCount" value="1" />
								<dsp:input bean="CartModifierFormHandler.items[0].productId" type="hidden" name="prodId" value="${productIdParam}" />
					            <dsp:input bean="CartModifierFormHandler.items[0].catalogRefId"  type="hidden" name="skuId" value="${skuId}" />
					            <dsp:input bean="CartModifierFormHandler.items[0].quantity" type="hidden" name="qty" value="1" />
					            <dsp:input name="bts" bean="CartModifierFormHandler.value.bts" type="hidden" iclass="isBTS" value="false" />
					            <div class="productContent">
					            <dsp:getvalueof var="smallImage" param="skuVO.skuImages.smallImage"/>
                                <dsp:getvalueof var="thumbnailImage" param="skuVO.skuImages.thumbnailImage"/>
					            
					            <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${productIdParam}" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />								
									</dsp:oparam>
								</dsp:droplet>
								<c:set var="productName">
									<dsp:valueof param="skuVO.displayName" valueishtml="true"/>
								</c:set>
					                <dsp:a iclass="prodImg block textCenter" page="${finalUrl}?skuId=${skuId}" title="${productName}" onclick="javascript:pdpOmnitureProxy('pfm','Last Minute Items(cart)')">
					                    <c:choose>
											<c:when test="${empty thumbnailImage}">
												<img class="productImage" align="center" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="83" width="83" alt="${productName}" title="${productName}" />
											</c:when>
											<c:otherwise>
												<img class="productImage noImageFound" align="center" src="${scene7Path}/${thumbnailImage}" height="83" width="83" alt="${productName}" title="${productName}" />
											</c:otherwise>
										</c:choose>
                                        <span class="prodInfo inlineBlock cb fl padTop_10 noMar textLeft">
                                            <span class="prodName">${productName}</span>
                                        </span>
                                        <span class="clear block"></span>
					                </dsp:a>
					                <ul class="prodInfo cb">
					                    <dsp:getvalueof var="isSale" param="skuVO.onSale"/>       
					                    
					                     
					                    <dsp:include page="cart_product_details_price.jsp">	
											<dsp:param name="product" param="productId"/>
											<dsp:param name="sku" param="skuVO.skuId"/>
											<dsp:param name="isSale" value="${isSale}"/>
											
										</dsp:include>
					                    
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="skuVO.skuAttributes" />
										<dsp:param name="elementName" value="attributeVOList"/>
                                                  <dsp:oparam name="outputStart">
                                                      <li class="prodSubInfo clearfix">
                                                          <ul>
                                                  </dsp:oparam>
											<dsp:oparam name="output">
												<dsp:getvalueof var="pageName" param="key" />
												<c:if test="${pageName eq 'RLP'}">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param param="attributeVOList" name="array" />
													<dsp:param name="sortProperties" value="+priority"/>
													<dsp:param name="elementName" value="attributeVO"/>
														<dsp:oparam name="output">
															<li class="highlight prodAttribWrapper">
															<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
															</li>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>							
											</dsp:oparam>
                                                  <dsp:oparam name="outputEnd">
                                                          </ul>
                                                      </li>
                                                  </dsp:oparam>
										</dsp:droplet>
										<c:if test="${BazaarVoiceOn}">
											<dsp:droplet name="ProductDetailDroplet">
												<dsp:param name="id" value="${productIdParam}"/>
												<dsp:param name="siteId" value="${appid}"/>
												<dsp:param name="skuId" param="skuVO.skuId"/>
												<dsp:param name="registryId" value="${registryId}" />
												<dsp:param name="isDefaultSku" value="true"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
													<dsp:getvalueof var="ratings" param="productVO.bvReviews.averageOverallRating" vartype="java.lang.Integer"/>
													<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
													<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
													<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
													
													<c:choose>
														<c:when test="${ratings ne null && ratings ne '0' && (totalReviewCount eq '1' || totalReviewCount gt '1') }">
															<c:choose>
																<c:when test="${totalReviewCount == 1}">
																	<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">
																		${totalReviewCount} <bbbl:label key="lbl_review_count" language="<c:out param='${language}'/>"/>
																	</li>
																</c:when>
																<c:otherwise>
																	<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">
																		${totalReviewCount} <bbbl:label key="lbl_reviews_count" language="<c:out param='${language}'/>"/>
																	</li>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<li class="prodReview writeReview">
																<dsp:a  page="${finalUrl}?skuId=${skuId}&writeReview=true"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:otherwise>
													</c:choose>
												</dsp:oparam>
											</dsp:droplet>
										</c:if>
					                </ul>
					                <div class="button button_secondary">
					                    <dsp:input bean="CartModifierFormHandler.addItemToOrder" onclick="addItemCartOmniture('${productIdParam}', '${skuId}');" type="submit" name="submit" value="add TO CART" >
                                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                                            <dsp:tagAttribute name="role" value="button"/>
                                        </dsp:input>
					                </div>
                                    <div class="clear"></div>
					            </div>
							</dsp:form>
						</div>
					</dsp:oparam>
				</dsp:droplet> 
			</dsp:oparam>
			<dsp:oparam name="error">
			
			</dsp:oparam>			
			<dsp:oparam name="empty">
			
			</dsp:oparam>
	</dsp:droplet>
    
    
    
    
</div>
<script type="text/javascript">
	resx.pageid = "${pageIdCertona}";
	resx.links = '${linksCertona}';
	
	function addItemCartOmniture(productId, skuId) {
		if (typeof addItemCartLastMinute === 'function'){
			addItemCartLastMinute(productId, skuId);
		}
	}
	
	  
    function pdpOmnitureProxy(event1,desc) {
		  
		   if(event1 == 'pfm') {
			
			   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
		   } 
		   
	   }
</script>
</dsp:page>