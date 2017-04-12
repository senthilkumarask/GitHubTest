<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
		<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
		<dsp:getvalueof var="key" param="key"/>	 
		<dsp:getvalueof var="desc" param="desc"/>	 
		<c:set var="onClickEvent" value=""/>
		<c:choose>
			<c:when test="${currentSiteId eq TBS_BedBathUSSite or currentSiteId eq 'BedBathUS'}">
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
			</c:when>
			<c:when test="${currentSiteId eq TBS_BuyBuyBabySite or currentSiteId eq 'BuyBuyBaby'}">
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
			</c:otherwise>
		</c:choose>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="productsVOsList" />
			<dsp:param name="elementName" value="productVO"/>
			<dsp:oparam name="outputStart">
				<div class="carouselContent grid_10 clearfix">
			</dsp:oparam>
			<dsp:oparam name="output">
				<div class="productContent small-12 large-3 columns">
					<dsp:getvalueof var="productId" param="productVO.productId"/>
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="productVO.productId" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName"
							value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
								param="url" />								
						</dsp:oparam>
					</dsp:droplet>
					<dsp:getvalueof var="mediumImage" param="productVO.productImages.mediumImage"/>
					<c:set var="productName">
						<dsp:valueof param="productVO.name" valueishtml="true"/>
					</c:set>
					<c:choose>
						<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
							<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="onClickEvent" value=""/>
						</c:otherwise>
					</c:choose>
					<dsp:a iclass="prodImg" page="${finalUrl}" title="${productName}" onclick="${onClickEvent}">
						<c:choose>
							<c:when test="${empty mediumImage}">
								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${productName}" /> 
							</c:when>
							<c:otherwise>
								<img class="productImage noImageFound" src="${scene7Path}/${mediumImage}" alt="${productName}" data-src-url="${scene7Path}/${mediumImage}" height="146" width="146"/>
							</c:otherwise>
						</c:choose>
						<noscript>
							<c:choose>
								<c:when test="${empty mediumImage}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${productName}"/>
								</c:when>
								<c:otherwise>
									<img src="${scene7Path}/${mediumImage}" height="146" width="146" alt="${productName}" class="noImageFound"/>
								</c:otherwise>
							</c:choose>
						</noscript>
					</dsp:a>
					<ul class="prodInfo">
						<li class="prodName"><dsp:a page="${finalUrl}" title="${productName}" onclick="${onClickEvent}">${productName}</dsp:a></li>
						<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
						<li class="prodPrice">
							<c:choose>
								<c:when test="${not empty salePriceRangeDescription}">
									<div class="prodPriceNEW">
										<dsp:valueof converter="currency"
											param="productVO.salePriceRangeDescription" />
									</div>
									<div class="prodPriceOLD">
										<span><dsp:valueof converter="currency"
												param="productVO.priceRangeDescription" /></span>
									</div>
								</c:when>
								<c:otherwise>
									<dsp:valueof converter="currency"
										param="productVO.priceRangeDescription" />
								</c:otherwise>
							</c:choose>
							
						</li>
						
						
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="productVO.attributesList" name="array" />
							<dsp:param name="elementName" value="attributeVOList"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="placeholder" param="key"/>
								<c:if test="${placeholder eq 'CRSL'}">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="attributeVOList" name="array" />
										<dsp:param name="elementName" value="attributeVO"/>
										<dsp:param name="sortProperties" value="+priority"/>
										<dsp:oparam name="output">
											<li class="productAttributes prodAttribWrapper prodAttribute">
												<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
											</li>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${BazaarVoiceOn}">
							<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<c:choose>
								<c:when test="${ratingAvailable == true}">
									<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
									<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
									<c:choose>
										<c:when test="${totalReviewCount == 1}">
											<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">
											(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</li>
										</c:when>
										<c:otherwise>
											<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">
											(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</li>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									<li class="prodReview writeReview ratingsReviews"><dsp:a page="${finalUrl}?writeReview=true"  title="${writeReviewLink}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></dsp:a></li>
								</c:otherwise>
							</c:choose>
						</c:if>				
					</ul>
					
				</div>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</div>
			</dsp:oparam>
		</dsp:droplet>
		
	     <script type="text/javascript">
	     /*This function definition is actually of pdpCrossSellProxy() written globally in omniture_tracking.js*/

	     /*function pdpOmnitureProxy(productId, event,event1,desc) {
			   if(event == 'crossSell') {
				   if (typeof s_crossSell === 'function') { s_crossSell(); }
			   } 
			   if(event1 == 'pfm') {
				
				   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
			   } 
			   
		   } */
	        $(document).ready(function() {
	            /* slick sllider for PDP carouselContent */
	            $('.carouselContent').each(function(){
	                var _this = $(this);
	                if(_this.hasClass('slick-initialized')){
	                    return;
	                }
	                _this.slick({
		                infinite: true,
		                slidesToShow: 4,
		                slidesToScroll: 4,
		                dots: true,
		                responsive: [
		                {
		                    breakpoint: 455,
		                    settings: {
		                        slidesToShow: 1,
		                        slidesToScroll: 1
		                    }
		                }
		                ]
		            });
		        });
		        $(document).foundation('reflow');
		    });
           
        </script>	
</dsp:page>
