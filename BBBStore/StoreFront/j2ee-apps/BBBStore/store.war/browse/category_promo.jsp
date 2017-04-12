<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<c:url var="url" value="productListing.jsp" >
  		<c:param name="categoryId" value="${categoryId}" />
	</c:url>
	<div class="categoryContent grid_12 noMarBot">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param param="landingTemplateVO.promoTierLayout1" name="array" />
			<dsp:oparam name="output">
				<div class="contentImages clearfix marBottom_20">
					<dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
					<c:choose>
						<c:when test="${not empty firstPromoContent1}">
							<dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
							<dsp:getvalueof var="imgSrc1" param="element.PromoBoxFirstVOList.imageURL" />
							<dsp:getvalueof var="imgLink1" param="element.PromoBoxFirstVOList.imageLink" />
							<div class="grid_4 alpha noOverflow" id="chat_grid_container_a"><dsp:a href="${imgLink1}" title="${imageAltText1}"><img width="312" height="220" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc1}" alt="${imageAltText1}" /></dsp:a></div> 
						</c:otherwise>
					</c:choose>
					<dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
					<c:choose>
						<c:when test="${not empty secondPromoContent1}">
							<dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
							<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
							<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
							<div class="grid_4 noOverflow" id="chat_grid_container_b"><dsp:a href="${imgLink2}" title="${imageAltText2}"><img width="312" height="220"  class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc2}" alt="${imageAltText2}" /></dsp:a></div> 
						</c:otherwise>
					</c:choose>
					<dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
					<c:choose>
						<c:when test="${not empty thirdPromoContent1}">
							<dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
							<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
							<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
							<div class="grid_4 omega noOverflow" id="chat_grid_container_c"><dsp:a href="${imgLink3}" title="${imageAltText3}"><img width="312" height="220"  class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc3}" alt="${imageAltText3}" /></dsp:a></div> 
						</c:otherwise>
					</c:choose>
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</dsp:oparam>
		</dsp:droplet>
	</div>
</dsp:page>
