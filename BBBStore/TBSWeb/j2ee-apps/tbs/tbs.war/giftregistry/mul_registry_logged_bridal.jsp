<dsp:getvalueof param="LandingTemplateVO.registryCategoryImage" var="registryCategoryImage"/>
 <c:if test="${not empty registryCategoryImage}">
    <div class="addRegistry small-12 columns">
	  <bbbt:textArea key="txt_blanding_muladd_heading" language ="${pageContext.request.locale.language}"/>
    </div>		                      
									
    <div class="clearfix productDisplay" >
	<ul class="clearfix small-block-grid-2 large-block-grid-4">
	  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param param="LandingTemplateVO.registryCategoryImage" name="array"/>
      <dsp:oparam name="output">
      <dsp:getvalueof var="count" param="count" />
     
 	   <dsp:getvalueof var="title" param="element.linkLabel"/>
         <c:choose>
          <c:when test="${title eq 'Bridal Toolkit'}">
                 <li>
                    <a title="${title}" href="${contextPath}/bbregistry/registry_features.jsp?pageName=BridalToolkit">
                    <img width="83" height="83" src="<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
                    <p><dsp:valueof param="element.linkLabel" /></p>
                    </a> 
                 </li>                                                         
          </c:when>
          <c:otherwise>
             <li>
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
                
	               