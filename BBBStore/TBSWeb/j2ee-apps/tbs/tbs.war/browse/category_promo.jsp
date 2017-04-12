<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<c:url var="url" value="productListing.jsp" >
  		<c:param name="categoryId" value="${categoryId}" />
	</c:url>
	
		<dsp:droplet name="ForEach">
			<dsp:param param="landingTemplateVO.promoTierLayout1" name="array" />
			<dsp:oparam name="outputStart">
				<div class="row">
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</div>
			</dsp:oparam>
			<dsp:oparam name="output">
                    <dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
					
					<c:choose>
						<c:when test="${not empty firstPromoContent1}">
							<div class="small-12 medium-4 large-4 columns radius-tile"><dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/></div>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
							<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
							<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
							<dsp:getvalueof var="promoBoxTitle1" param="element.promoBoxFirstVOList.promoBoxTitle" />	
							<div class="small-12 medium-4 large-4 columns radius-tile">
							
								<a href="${imgLink1}" title="${imageAltText1}">
									<img data-interchange="[${imgSrc1}, (default)], [${imgSrc1}, (small)], [${imgSrc1}, (medium)],  [${imgSrc1}, (large)]">
									<noscript><img src="${imgSrc1}"></noscript>
								</a>
								<%-- <div class="radius-tile-headline">
									<c:choose>
									   <c:when test="${empty promoBoxTitle1}">
										<h5>${imageAltText1}</h5>
									   </c:when>
									   <c:otherwise>
									    <h5>${promoBoxTitle1}</h5>
									   </c:otherwise>
									</c:choose>	
								</div> --%>
							</div>
						</c:otherwise>
					</c:choose>
					
					
					
					
					
					<dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
					
					
					<c:choose>
						<c:when test="${not empty secondPromoContent1}">
							<div class="small-12 medium-4 large-4 columns radius-tile"><dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/></div>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
							<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
							<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
							<dsp:getvalueof var="promoBoxTitle2" param="element.promoBoxSecondVOList.promoBoxTitle" />
							<div class="small-12 medium-4 large-4 columns radius-tile">
							
								<a href="${imgLink2}" title="${imageAltText1}">
									<img data-interchange="[${imgSrc2}, (default)], [${imgSrc2}, (small)], [${imgSrc2}, (medium)],  [${imgSrc2}, (large)]">
									<noscript><img src="${imgSrc2}"></noscript>
								</a>
								<%-- <div class="radius-tile-headline">
									<c:choose>
									   <c:when test="${empty promoBoxTitle2}">
										<h5>${imageAltText2}</h5>
									   </c:when>
									   <c:otherwise>
									    <h5>${promoBoxTitle2}</h5>
									   </c:otherwise>
									</c:choose>	
								</div> --%>
							</div>
						</c:otherwise>
					</c:choose>
					
					<dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
					
					<c:choose>
						<c:when test="${not empty thirdPromoContent1}">
							<div class="small-12 medium-4 large-4 columns radius-tile"><dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/></div>
						</c:when>
						<c:otherwise>
					
						<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
						<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
						<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
						<dsp:getvalueof var="promoBoxTitle3" param="element.promoBoxThirdVOList.promoBoxTitle" />
						<div class="small-12 medium-4 large-4 columns radius-tile">
							<a href="${imgLink3}" title="${imageAltText3}">
								<img data-interchange="[${imgSrc3}, (default)], [${imgSrc3}, (small)], [${imgSrc3}, (medium)], [${imgSrc3}, (large)]">
								<noscript><img src="${imgSrc3}"></noscript>
							</a>
							<%-- <div class="radius-tile-headline">
								<c:choose>
								   <c:when test="${empty promoBoxTitle3}">
									<h5>${imageAltText3}</h5>
								   </c:when>
								   <c:otherwise>
								    <h5>${promoBoxTitle3}</h5>
								   </c:otherwise>
								</c:choose>	
							</div> --%>
						</div>
						
					
						</c:otherwise>
					</c:choose>
					
					
					
			</dsp:oparam>
		</dsp:droplet>
	
</dsp:page>
