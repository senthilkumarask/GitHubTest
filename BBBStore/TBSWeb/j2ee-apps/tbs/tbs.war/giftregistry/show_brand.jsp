<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<div class="carouselBody grid_12">
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
<dsp:getvalueof var="desc" param="desc"/>	
							<div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
								&nbsp;
								<a class="carouselScrollPrevious" title="Previous" href="#"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
							</div>

 

							<div class="carouselContent grid_10 clearfix">
								<ul class="prodGridRow">
								   <dsp:droplet name="ForEach">
								     <dsp:param param="brandList" name="array" />
								      <dsp:oparam name="output">
									   <dsp:getvalueof var="brandImage" param="element.brandImage" />
								       <dsp:getvalueof var="brandImageAltText" param="element.brandImageAltText" />
									   <dsp:getvalueof var="brandId" param="element.brandId" />
									   <dsp:getvalueof var="brandName" param="element.brandName" />
									   <dsp:getvalueof var="count" param="count" />
									   	<c:choose>
										<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
											<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="onClickEvent" value=""/>
										</c:otherwise>
										</c:choose>
										<%-- R2.2 SEO Friendly URL changes : Start--%>
										<dsp:droplet name="BrandDetailDroplet">
										<dsp:param name="origBrandName" value="${brandName}"/>
										<dsp:oparam name="seooutput">
											<dsp:getvalueof var="seoUrl" param="seoUrl" />
											<c:set var="Keyword" value="${brandName}" scope="request" />
											<c:url var="seoUrl" value="${seoUrl}" scope="request" />
										</dsp:oparam>
										</dsp:droplet>
										<c:url var="url" value="${seoUrl}"/>
										<%-- R2.2 SEO Friendly URL changes : End--%>
									    <c:if test="${(count mod 2) ne 0}">
									      <li>
									      <a title="${brandName}" class="block" href="${url}" onclick="${onClickEvent}"><img width="130" height="103" alt="${brandImageAltText}" src="${brandImage}" /></a>
									    </c:if>
											
																					
										<c:if test="${(count mod 2) eq 0}">
									      <a title="${brandName}" class="block cb" href="${url}" onclick="${onClickEvent}"><img width="130" height="103" alt="${brandImageAltText}" src="${brandImage}" /></a>
									      </li>
									    </c:if>
										
								      </dsp:oparam>
								   </dsp:droplet>
						 			 
								</ul>
							</div>

							<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
								&nbsp;
								<a class="carouselScrollNext" title="Next" href="#"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
							</div>
                     </div>
							 
						<div class="carouselPages">
							<div class="carouselPageLinks clearfix">
								<a title="Page 1" class="selected" href="#">1</a>
								<a title="Page 2" href="#">2</a>
							</div>
						</div>
</dsp:page>