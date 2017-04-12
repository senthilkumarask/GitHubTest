<div class="catTabsData carosalBrands">
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<div class="carousel clearfix row padBottom_10"> 
		 <div class="carouselBody grid_9"> 
			<div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix"> &nbsp; <a href="#" title="Previous" role="button" class="carouselScrollPrevious" style="display: block;">Previous</a> </div>
                        
			<div class="carouselContent grid_7 marTop_10 clearfix">
				<ul class="prodGridRow">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="brandList" name="array" />
						<dsp:oparam name="output">
						   <dsp:getvalueof var="brandImage" param="element.brandImage" />
					       <dsp:getvalueof var="brandImageAltText" param="element.brandImageAltText" />
						   <dsp:getvalueof var="brandId" param="element.brandId" />
						   <dsp:getvalueof var="brandName" param="element.brandName" />
						   <dsp:getvalueof var="count" param="count" />
						   
					    
					      <li>
					      <%-- R2.2 SEO Friendly URL changes : Start--%>
					      <dsp:droplet name="BrandDetailDroplet">
								<dsp:param name="origBrandName" value="${brandName}"/>
								<dsp:oparam name="seooutput">
									<dsp:getvalueof var="seoUrl" param="seoUrl" />
									<c:set var="Keyword" value="${name}" scope="request" />
									<c:url var="url" value="${seoUrl}" scope="request" />
								</dsp:oparam>
							</dsp:droplet>
							<%-- R2.2 SEO Friendly URL changes : End--%>
					      <a title="${brandName}" href="${url}"><img width="130" height="103" alt="${brandImageAltText}" src="${brandImage}" /></a>
					   
								
						  </li>
						   
						</dsp:oparam>
					</dsp:droplet>
				</ul>
			</div>
            <div class="grid_1 carouselArrow alpha carouselArrowNext clearfix"> &nbsp; <a href="#" title="Next" role="button" class="carouselScrollNext" style="display: block;">Next</a> </div>         
		</div>
          <div class="carouselPages">
              <div class="carouselPageLinks clearfix" style="display: block;">     <a href="#" class="selected"><span>1</span></a><a href="#"><span>2</span></a><a href="#"><span>3</span></a><a href="#"><span>4</span></a></div>
          </div>	
	</div>
</div>