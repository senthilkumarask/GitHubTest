<dsp:page>
	<dsp:getvalueof var="recommCatVO" param="recommCatVO"/>
	<dsp:getvalueof var="productId" param="productId"/>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<dsp:getvalueof var="siblingProductDetails" param="siblingProductDetails"/>
	<c:set var="div_class" value=""></c:set>
	<c:set var="disableLazyLoadS7Images">
		<bbbc:config key="disableLazyLoadS7ImagesFlag" configName="FlagDrivenFunctions" />
	</c:set>
	<c:if test="${empty siblingProductDetails}">
	<c:set var="div_class" value="fullWidthCarousel"></c:set>
	</c:if>
	<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>
	<c:if test="${(not empty disableLazyLoadS7Images and not disableLazyLoadS7Images)}">
		<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
	</c:if>
	<div id="cartFaves" class="widthCartFaves ${div_class}">
	<h3><bbbl:label key="lbl_category_recomm" language="${pageContext.request.locale.language}" /></h3>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" value="${recommCatVO}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="catRecommId" param="element" />
				<div class="grid_2">
				<dsp:a page="${catRecommId.recommCategoryLink}" title='${catRecommId.recommCategoryText}'
						onclick="${onClickEvent}">
						<c:choose>
							<c:when test="${empty catRecommId.recommCategoryImageUrl}">
								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${catRecommId.recommCategoryText}" />
							</c:when>
							<c:otherwise>
								<img ${prodImageAttrib}="${catRecommId.recommCategoryImageUrl}" height="146" width="146" alt="image of ${catRecommId.recommCategoryText}"/>
							</c:otherwise>
						</c:choose>
						<noscript>
							<c:choose>
								<c:when test="${empty catRecommId.recommCategoryImageUrl}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${catRecommId.recommCategoryText}" />
								</c:when>
								<c:otherwise>
									<img src="${catRecommId.recommCategoryImageUrl}" height="146" width="146" alt="image of ${catRecommId.recommCategoryText}" class="noImageFound" />
								</c:otherwise>
							</c:choose>
						</noscript>
						<p class="cartFaveCats">${catRecommId.recommCategoryText}</p>
					</dsp:a>
					
					
				</div>
			</dsp:oparam>
		</dsp:droplet>
	</div>
</dsp:page>