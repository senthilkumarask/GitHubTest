<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof param="productId" var="productId"/>
	<dsp:getvalueof param="productCountDisplayed" var="productCountDisplayed"/>
	<dsp:getvalueof var="productCount" param="browseSearchVO.bbbProducts.bbbProductCount"/>
	<c:set var="featuredProductMinCount"><bbbc:config key="featured_product_display_count" configName="FlagDrivenFunctions" /></c:set>
	<%-- Start :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="browseSearchVO.promoMap" />
            <dsp:oparam name="output">
	  		<dsp:getvalueof var="elementList" param="element"/>
            <dsp:getvalueof var="promoListKey" param="key"/>
				<c:if test="${promoListKey eq promoKeyCenter &&  not empty elementList}">
       	     		<dsp:droplet name="ForEach">
                   		<dsp:param name="array" value="${elementList}" />
							<dsp:oparam name="output">	
							<dsp:getvalueof  var="promoVO" param="element" />
							    <dsp:droplet name="ForEach">
									<dsp:param name="array" value="${promoVO.featuredProducts}" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="featuredProduct" param="element"/>
					          			<dsp:getvalueof var="featureProductId" param="key"/>
											<c:if test="${featureProductId eq productId}">
												<c:if test="${featuredProduct eq 'POPULAR_FEATURED_PRODUCT' && productCountDisplayed > featuredProductMinCount}">
													<span class="featured-ribbon featured-ribbon-mostpopular" height="20px" width="85px"></span>
												</c:if>
												<c:if test="${featuredProduct eq 'TOP_RATED_FEATURED_PRODUCT' && productCountDisplayed > featuredProductMinCount}">
													<span class="featured-ribbon featured-ribbon-top-rated" height="20px" width="85px"></span>
												</c:if>
												<c:if test="${featuredProduct eq 'TRENDING_FEATURED_PRODUCT' && productCountDisplayed > featuredProductMinCount}">
													<span class="featured-ribbon featured-ribbon-mostviewed" height="20px" width="85px"></span>
												</c:if>
												<c:if test="${featuredProduct eq 'NEWEST_FEATURED_PRODUCT' && productCountDisplayed > featuredProductMinCount}">
													<span class="featured-ribbon featured-ribbon-new" height="20px" width="85px"></span>
												</c:if>
												<c:if test="${featuredProduct eq 'SPONSORED_FEATURE_PRODUCT'}">
													<span class="featured-ribbon featured-ribbon-sponsored" height="20px" width="85px"></span>
												</c:if>
											</c:if>
										</dsp:oparam>
								</dsp:droplet>
					   		 </dsp:oparam>
                     	</dsp:droplet>
                      </c:if>
                </dsp:oparam>
		</dsp:droplet>
			
	<%-- End :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
			
			
</dsp:page>