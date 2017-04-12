           <dsp:getvalueof var="imageURL" vartype="java.lang.String" param="promoTierLayout1.imageURL" />
	       <dsp:getvalueof var="promoBoxContent" vartype="java.lang.String" param="promoTierLayout1.promoBoxContent" />
	       <dsp:getvalueof var="imageMapName" vartype="java.lang.String" param="promoTierLayout1.imageMapName" scope="request" />
	       <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoTierLayout1.imageMapContent" scope="request" />
		   <dsp:getvalueof var="imageAltText" vartype="java.lang.String" param="promoTierLayout1.imageAltText" />
	       <dsp:getvalueof var="imageLink" vartype="java.lang.String" param="promoTierLayout1.imageLink" scope="request" />
	        
	        <c:if test="${!empty imageURL}">
	           
		      <a href="${imageLink}"> <img src="${imageURL}" alt="${imageAltText}" border="0" title="${imageAltText}"usemap="#${imageMapName}" class="stretch" /></a>
		       
		       
            </c:if>
            
            <c:if test="${!empty promoBoxContent}">
	           <dsp:valueof param="promoTierLayout1.promoBoxContent" valueishtml="true"/>
            </c:if>
                       
	         
            