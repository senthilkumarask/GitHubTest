<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:page>

	<div class="carousel clearfix">
		<div class="carouselBody grid_12">
			<div
				class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
				&nbsp; <a class="carouselScrollPrevious" title="Previous" role="button" href="#">Previous</a>
			</div>

			<div class="carouselContent grid_10 clearfix">
				<ul class="prodGridRow">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="clearanceProductsList" name="array" />
						<dsp:param name="sortProperties" value="+" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="imgSrc"
								param="element.productImages.thumbnailImage" />
							
							<li class="grid_2 product">
								<div class="productShadow"></div>
								<div class="productContent">
									<a class="prodImg" href="#"> 
									<c:choose>
									<c:when test="${empty imgSrc}">
										<dsp:img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" /> 
									</c:when>
									<c:otherwise>
										<dsp:img src="${scene7Path}/${imgSrc}" height="146" width="146" iclass="noImageFound"/> 
									</c:otherwise>
									</c:choose>
									</a>
									<ul class="prodInfo">
										<li class="prodName"><a href="#"><dsp:valueof
													param="element.name" valueishtml="true"/> </a>
										</li>
										
										<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
										<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
										<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
										<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
										<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
												
										<c:choose>
								        	<c:when test="${isdynamicPriceProd}">
								           		<dsp:include page="dynamic_pricing_prod_frag.jsp">
											    <dsp:param name="priceLabelCodeProd" value="${priceLabelCodeProd}" />
												<dsp:param name="inCartFlagProd" value="${inCartFlagProd}" />
												<dsp:param name="salePriceRangeDescription" value="${salePriceRangeDescription}" />
												<dsp:param name="priceRangeDescription" value="${priceRangeDescription}" />
												</dsp:include>   		
										 	</c:when>
											<c:otherwise>
												<c:if test="${inCartFlagProd}">
												 <li class="red fontSize_14 bold inCartMsg"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </li>
												</c:if>
												<li class="prodPrice">
												<c:choose>
														<c:when
															test="${not empty element.salePriceRangeDescription}">
															<div class="prodPriceOLD">
																<span> <dsp:valueof converter="currency"
																		param="element.priceRangeDescription" /> </span>
															</div>
															<dsp:getvalueof var="salePriceRange" param="element.salePriceRangeDescription"/>
															<c:choose>
																<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
																<c:otherwise></c:otherwise>
															</c:choose>
															<div class="prodPriceNEW">
																<dsp:valueof converter="currency"
																	param="element.salePriceRangeDescription" />
															</div>
														</c:when>
														<c:otherwise>
															<dsp:valueof converter="currency"
																param="element.priceRangeDescription" />
														</c:otherwise>
													</c:choose>
													</li>
											</c:otherwise>
										</c:choose>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.attributesList" name="array" />
											<dsp:oparam name="output">
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param param="element" name="array" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="countAttribute" param="count" />
														<c:choose>
															<c:when test="${countAttribute % 2==0}">
																<li class="prodPrimaryAttribute prodAttribWrapper"><dsp:valueof
																		param="element.attributeDescrip" />
																</li>
															</c:when>
															<c:otherwise>
																<li class="prodSecondaryAttributee prodAttribWrapper"><dsp:valueof
																		param="element.attributeDescrip" />
																</li>
															</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
										<li class="prodReview clearfix prodReview40">10 Reviews</li>
									</ul>
								</div></li>
						</dsp:oparam>
					</dsp:droplet>
				</ul>
			</div>

			<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
				&nbsp; <a class="carouselScrollNext" title="Next" role="button" href="#">Next</a>
			</div>
		</div>

		<div class="carouselPages">
			<div class="carouselPageLinks clearfix">
				<a title="Page 1" class="selected" href="#">1</a> <a title="Page 2"
					href="#">2</a> <a title="Page 3" href="#">3</a> <a title="Page 4"
					href="#">4</a>
			</div>
		</div>
	</div>

</dsp:page>
