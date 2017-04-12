<dsp:page>
<dsp:getvalueof var="regType" param="regType" />
<c:choose>
     <c:when test="${regType eq 'COL' }">
      <div id="cmsCarousel" class="grid_9">
       <bbbt:textArea key="txt_${regType}_registry_landing" language ="${pageContext.request.locale.language}"/>
        <bbbt:textArea key="txt_${regType}_registry_FPO_landing" language ="${pageContext.request.locale.language}"/>
</div>
     </c:when>
      <c:otherwise>
<div id="cmsCarousel" class="row">
						<div class="large-12 columns carousel">
							<div class="carouselBody">
								<div class="carouselContent">
								   <div class="carouselImages">
								
								
									 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
									   <dsp:param param="heroImage" name="array"/>
									   
										<dsp:oparam name="output">
										  <dsp:getvalueof var="imgSrc" param="element.imageURL" />
										  <dsp:getvalueof var="imageAltText" param="element.imageAltText" />
											 <img width="976" height="348" alt="${imageAltText}" src="${imgSrc}" />
										 </dsp:oparam>
									   </dsp:droplet>
									</div>
								</div>
							</div>
							<div class="carouselPages">
								<div class="carouselArrow carouselArrowPrevious">
									<a class="carouselScrollPrevious" title="Previous" href="#">Previous</a>
								</div>
								<div class="carouselPageLinks">
									<a title="Page 1" class="selected" href="#">1</a>
									<a title="Page 2" href="#">2</a>
									<a title="Page 3" href="#">3</a>
									<a title="Page 4" href="#">4</a>
								</div>
								<div class="carouselArrow carouselArrowNext">
									<a class="carouselScrollNext" title="Next" href="#">Next</a>
								</div>
							</div>
						</div>
					</div>
					</c:otherwise>
					</c:choose>
					</dsp:page>