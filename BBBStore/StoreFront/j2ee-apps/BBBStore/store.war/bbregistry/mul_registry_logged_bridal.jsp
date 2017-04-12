<dsp:getvalueof param="LandingTemplateVO.registryCategoryImage" var="registryCategoryImage"/>
 <c:if test="${not empty registryCategoryImage}">
    <div class="addRegistry clearfix container_12 spacingBtmSml marTop_10">
	  <bbbt:textArea key="txt_blanding_muladd_heading" language ="${pageContext.request.locale.language}"/>
    </div>		                      
									
    <div class="container_12 clearfix productDisplay cb" >
	<ul class="clearfix row1 alpha">
	  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param param="LandingTemplateVO.registryCategoryImage" name="array"/>
      <dsp:oparam name="output">
      <dsp:getvalueof var="count" param="count" />
     <c:if test="${count==5}">
	 </ul>													 
	 <ul class="clearfix row2 alpha">
	 </c:if>
 	   <dsp:getvalueof var="title" param="element.linkLabel"/>
         <c:choose>
          <c:when test="${title eq 'Bridal Toolkit'}">
                 <li class="grid_2">
                    <a title="${title}" href="${contextPath}/bbregistry/registry_features.jsp?pageName=BridalToolkit">
                    <img width="83" height="83" src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
                    <p><dsp:valueof param="element.linkLabel" /></p>
                    </a> 
                 </li>                                                         
          </c:when>
          <c:otherwise>
             <li class="grid_2">
              <a title="${title}" href="<dsp:valueof param="element.linkUrl"/>">
                 <img width="83" height="83" src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
                    <p><dsp:valueof param="element.linkLabel" /></p>
              </a> 
             </li>
          </c:otherwise>
         </c:choose>
        </dsp:oparam>
     </dsp:droplet>
	 </ul>
</div>  
</c:if>           
                
	               