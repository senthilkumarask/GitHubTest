<dsp:page>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />

	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
  	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

    <dsp:getvalueof var="registryId" param="registryId" />
    <dsp:getvalueof var="skuList" param="skuList" />
	<dsp:getvalueof var="certonaSkuList" param="${certonaSkuList}"/>

	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient"/>
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id"/>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	 <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
     <div class="grid_2 omega marTop_10">
    			<%-- Commenting Click to Chat as part of 34473 
    			<dsp:include page="/common/click2chatlink.jsp">
                	<dsp:param name="pageId" value="2"/>
                </dsp:include>
				--%>
		<c:set var="topRegistryMax" scope="request">
		  <bbbc:config key="TopRegistryMax" configName="CertonaKeys" />
	    </c:set>

         <c:if test= "${CertonaOn}">
		 <dsp:droplet name="CertonaDroplet">
			 <dsp:param name="scheme" value="gru_tri"/>
			 <dsp:param name="giftregid" value="${registryId}"/>
			 <dsp:param name="userid" value="${userId}"/>
			 <dsp:param name="context" value="${itemList}"/>
			 <dsp:param name="number" value="${topRegistryMax}"/>
			 <dsp:param name="siteId" value="${appid}"/>
 			 <dsp:param name="exitemid" value="${certonaSkuList}"/>
			 <dsp:oparam name="output">

             <dsp:getvalueof var="skuDetailVOList" param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuDetailVOList"/>

 			<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
 			<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>

			<div class="certonaProducts clearfix">
			<dsp:getvalueof var="sorting" param="sorting" />
			<dsp:getvalueof var="view" param="view" />
			<c:if test="${empty sorting}">
				<dsp:getvalueof var="sorting" value="1" />
			</c:if>
			<c:if test="${empty view}">
				<dsp:getvalueof var="view" value="1" />
			</c:if>

				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:getvalueof var="idx" param="index"/>
				<dsp:form name="frmCertonaProduct" method="post" id="formID${idx}">
					<dsp:param name="array" value="${skuDetailVOList}"/>
					<dsp:param name="elementName" value="skuVO"/>
					<dsp:oparam name="outputStart">
						<h6><bbbl:label key="lbl_reg_top_item" language ="${pageContext.request.locale.language}"/></h6>
				    </dsp:oparam>
					<dsp:oparam name="output">
					<div class="certonaProduct clearfix">
					<dsp:getvalueof var="skuId" param="skuVO.skuId"/>

						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			               	<dsp:param name="array" param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuParentProductMap"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="skuIdParam" param="key"/>
								<c:if test="${skuIdParam eq skuId}">
									<dsp:getvalueof var="productIdParam" param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuParentProductMap.${skuIdParam}"/>

								</c:if>
							</dsp:oparam>
					   </dsp:droplet>

				<dsp:getvalueof var="eventType" param="eventType"/>

				<dsp:input bean="GiftRegistryFormHandler.productId" type="hidden" name="prodId" value="${productIdParam}" />
	            <dsp:input bean="GiftRegistryFormHandler.skuIds"  type="hidden" name="skuId" value="${skuId}" />
	            <dsp:input bean="GiftRegistryFormHandler.quantity" type="hidden" name="qty" value="1" />
	            <dsp:input bean="GiftRegistryFormHandler.registryId" type="hidden" name="registryId" value="${registryId}" />
	            <%-- <dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="/tbs/giftregistry/view_registry_owner.jsp?eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" />
	            <dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" value="/tbs/giftregistry/view_registry_owner.jsp?eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" /> --%>
	            <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="?eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" />
                <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="topRegistryItemsFrags" />
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

				<dsp:getvalueof var="smallImage" param="skuVO.skuImages.smallImage"/>

				<div class="productContent">
				<c:set var="productName">
				<dsp:valueof param="skuVO.displayName" valueishtml="true"/>
				</c:set>
		            <dsp:a iclass="prodImg" page="${finalUrl}?skuId=${skuId}" title="${productName}" onclick="javascript:s_crossSell('registry update, top registry items');">
		               <c:choose>
							<c:when test="${empty smallImage}">
								<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="83" width="83" alt="<dsp:valueof param="productVO.name"/>" />
							</c:when>
							<c:otherwise>
								<img class="productImage noImageFound" src="${scene7Path}/${smallImage}" height="83" width="83" alt="<dsp:valueof param="productVO.name"/>" />
							</c:otherwise>
						</c:choose>
			        </dsp:a>
	 				    <ul class="prodInfo">
							<li class="prodName"><dsp:a page="${finalUrl}?skuId=${skuId}" title="${productName}" onclick="javascript:s_crossSell('registry update, top registry items');">${productName}</dsp:a></li>

							 <li class="prodPrice prodPriceSALE">
								<dsp:getvalueof var="certonaPrice" value="" scope="request" />
							    <dsp:getvalueof var="isSale" param="skuVO.onSale"/>
			                    <dsp:include page="/cart/cart_product_details_price.jsp">
									<dsp:param name="product" value="${productIdParam}"/>
									<dsp:param name="sku" param="skuVO.skuId"/>
									<dsp:param name="isSale" value="${isSale}"/>
								</dsp:include>
					        </li>

						 <li class="prodSubInfo clearfix">
					        <ul>
									 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="skuVO.skuAttributes" />
										<dsp:param name="elementName" value="attributeVOList"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="pageName" param="key" />
												<c:if test="${pageName eq 'RLP'}">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param param="attributeVOList" name="array" />
													<dsp:param name="elementName" value="attributeVO"/>
													<dsp:param name="sortProperties" value="+priority"/>
														<dsp:oparam name="output">
															<li class="highlight prodAttribWrapper">
																<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
															</li>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</dsp:oparam>
									</dsp:droplet>
									</ul>
								</li>
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
														<dsp:a  page="${finalUrl}?skuId=${skuId}&writeReview=true" onclick="javascript:s_crossSell('registry update, top registry items');"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></dsp:a>
													</li>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
					       </ul>
							<div class="button button_secondary marBottom_10 button_active">
								<dsp:input bean="GiftRegistryFormHandler.addItemToGiftRegistryFromCetona" type="submit" name="submit" value="Add to Registry" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
							</div>
						</div>
						</div>
					</dsp:oparam>
						</dsp:form>
				</dsp:droplet>

		</div>
	</dsp:oparam>
	<dsp:oparam name="error">
	</dsp:oparam>
	<dsp:oparam name="empty">
	</dsp:oparam>
</dsp:droplet>
</c:if>

	</div>

<script type="text/javascript">
	var resx = new Object();
	resx.appid = "${appIdCertona}";
	resx.links = '${linksCertona}' +"${itemList}";
	resx.pageid = "${pageIdCertona}";
	resx.customerid = "${userId}";
	resx.itemid="${itemList}";
	resx.event="registry+"+"${eventType}";
	certonaResx.run();
</script>

</dsp:page>