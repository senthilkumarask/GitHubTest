<dsp:getvalueof param="LandingTemplateVO.registryCategoryImage" var="registryCategoryImage"/>
<c:if test="${not empty registryCategoryImage}">
	<div class="addToYourRegistryToFun">
		 <div class="container_12 clearfix"> 
		  <a id="addMoreGifts"></a>
		   <h3><bbbt:textArea key="txt_blanding_muladd_heading" language ="${pageContext.request.locale.language}"/></h3>	
		   <div class="grid_12 clearfix alpha omega productDisplay marTop_20">
			  <ul class="clearfix row1 alpha small-block-grid-2 medium-block-grid-3 large-block-grid-6">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param param="registryCategoryImage" name="array"/>
				<dsp:oparam name="output">
				
				<dsp:getvalueof var="count" param="count" />
				<dsp:getvalueof var="title" param="element.linkLabel"/>
			 	<c:choose>
					 <c:when test="${count==1}">
						  <c:choose>
								<c:when test="${title eq 'Bridal Toolkit'}">
									<li class="alpha">
									 <a title="${title}" href="${contextPath}/bbregistry/registry_features.jsp?pageName=BridalToolkit">
									<img src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
									<p><dsp:valueof param="element.linkLabel" /></p>
									 </a> 
									</li>                                                         
								</c:when>
								<c:otherwise>
									<li class="alpha">
									<a title="${title}" href="<dsp:valueof param="element.linkUrl"/>">
									<img src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
									<p><dsp:valueof param="element.linkLabel" /></p>
									 </a> 
									</li>
								</c:otherwise>
							</c:choose>
					  </c:when>
					 <c:otherwise>
					  <c:choose>
							<c:when test="${title eq 'Bridal Toolkit'}">
								<li>
								 <a title="${title}" href="${contextPath}/bbregistry/registry_features.jsp?pageName=BridalToolkit">
								<img src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
								<p><dsp:valueof param="element.linkLabel" /></p>
								 </a> 
								</li>                                                         
							</c:when>
							<c:otherwise>
								<li>
								<a title="${title}" href="<dsp:valueof param="element.linkUrl"/>">
								<img src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
								<p><dsp:valueof param="element.linkLabel" /></p>
								       </a> 
								</li>
							</c:otherwise>
					  </c:choose>
					 </c:otherwise>
				</c:choose>
			 	</dsp:oparam>
				</dsp:droplet>
			   </ul>
			 </div>
		  </div>
	</div>	
</c:if>
