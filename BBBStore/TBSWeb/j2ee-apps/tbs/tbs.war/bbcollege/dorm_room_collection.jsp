<c:set var="section" value="browse" scope="request" />
   <c:set var="pageWrapper" value="college dormRoomCollection" scope="request" />
   <c:set var="titleString" value="Bed Bath &amp; Beyond - College - Category Landing" scope="request" />
   <c:set var="pageVariation" value="bc" scope="request" />

   <dsp:page>
   <dsp:importbean bean="/com/bbb/cms/droplet/CollegeCollectionsDroplet" />
   <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
      <jsp:body>

			<div id="content" class="container_12 clearfix" role="main">
				<div class="grid_12 clearfix">
                    <div class="grid_7 alpha">
                        <h1 class="noMarBot marTop_10"><bbbl:label key="lbl_dorm_room_collections_heading" language="${pageContext.request.locale.language}" /></h1>
                        <bbbt:textArea key="txt_dorm_room_collections_info" language ="${pageContext.request.locale.language}"/>
                    </div>

                    <div class="grid_5 omega">
                        <div class="pushDown">
                           <bbbl:label key="lbl_dorm_room_collections_placeholder_image" language="${pageContext.request.locale.language}" />
                        </div>
                    </div>
                </div>
                <div class="grid_12 pushDown clearfix">
	                <dsp:droplet name="CollegeCollectionsDroplet">
					<dsp:oparam name="output">
						<dsp:param param="listCollectionVo" name="array" />
						<dsp:getvalueof var="listCollectionVo" param="listCollectionVo" />
					    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="listCollectionVo" name="array" />

       <%-- At the start of every row add div class="clearfix" and divclass alpha .for this we are putting check  {((count-1) mod 3) eq 0 || count eq 1
	   At the end of each row after 3 products we need to close the clearfix div so we have put logic at end (count mod 3) eq 0 Also for every 3rd row the divClass should be omega
	   hence the test {(count mod 3) eq 0} after output of Foreach--%>


								<dsp:oparam name="output">
								<dsp:getvalueof var="count" param="count" />
								<dsp:getvalueof var="size" param="size" />
								<c:set var="divClass" value=""/>
								<c:if test="${(count mod 3) eq 0}">
									<c:set var="divClass" value="omega"/>
								</c:if>
								<c:if test="${((count-1) mod 3) eq 0 || count eq 1}">
									<div class="clearfix">
									<c:set var="divClass" value="alpha"/>
								</c:if>
									<dsp:getvalueof var="collectionImage" param="element.productImages.largeImage" />
									<dsp:getvalueof var="productId"	param="element.productId" />
									<dsp:getvalueof var="productName"	param="element.name" />
										<div class="grid_4 ${divClass} clearfix">
											<div class="collectionImage">
												<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
													<dsp:param name="id" value="${productId}" />
													<dsp:param name="itemDescriptorName" value="product" />
													<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
													</dsp:oparam>
												</dsp:droplet>

												<c:choose>
												<c:when test="${empty collectionImage}">
													<dsp:a page="${finalUrl}"><img alt="${productName}" title="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" width="312" height="312"/>
						                               <dsp:param name="showFlag" value="1"/>
						                            </dsp:a>
												</c:when>
												<c:otherwise>
													<dsp:a page="${finalUrl}"><img alt="${productName}" title="${productName}" src="${scene7Path}/${collectionImage}" class="noImageFound" width="312" height="312" />
						                               <dsp:param name="showFlag" value="1"/>
						                            </dsp:a>
												</c:otherwise>
												</c:choose>
											</div>
											<div class="pushDown">
												<ul class="prodInfo">
													<li>
													<dsp:getvalueof var="element_title" param="element.name"/>
													<dsp:a page="${finalUrl}" title="${element_title}">
														<dsp:valueof param="element.name" valueishtml="true"/>
					                                    	<dsp:param name="showFlag" value="1"/>

					                                    </dsp:a>
					                                </li>
													<c:if test="${BazaarVoiceOn}">
													<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
													<c:choose>
													<c:when test="${ratingAvailable == true}">
														<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
														<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
														<c:choose>
															<c:when test="${totalReviewCount == 1}">
																<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																(<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</li>
															</c:when>
															<c:otherwise>
																<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																(<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</li>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
														<li class="prodReview writeReview"><dsp:a page="${finalUrl}?writeReview=true" title="${writeReviewLink}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></dsp:a></li>
													</c:otherwise>
													</c:choose>
													</c:if>
												</ul>
											</div>
										</div>
									<c:if test="${(count mod 3) eq 0 || count eq size }">
										</div>
									</c:if>
							</dsp:oparam>
						</dsp:droplet>

				</dsp:oparam>
			</dsp:droplet>
				</div>
            </div>
        </jsp:body>
      <jsp:attribute name="footerContent">
				<script type="text/javascript">   if(typeof s !=='undefined') {
						s.channel='College';
						s.pageName='College>College>Dorm Decorating'// pagename
						s.prop1='Sub Category Page'; // page type
						s.prop2='';// category level 1
						s.prop3='College>College>Dorm Decorating';// category level 2
						s.prop4='';// page type = page title
						s.prop5='';// page type = page title
						s.prop6='${pageContext.request.serverName}';
						s.eVar9='${pageContext.request.serverName}';
						var s_code=s.t();
						if(s_code)document.write(s_code);
				}
				</script>
	</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
