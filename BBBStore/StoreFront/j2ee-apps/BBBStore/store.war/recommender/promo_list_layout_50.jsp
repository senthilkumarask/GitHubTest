           <dsp:getvalueof var="imageURL" vartype="java.lang.String" param="promoBox.imageURL" />
	       <dsp:getvalueof var="imageAltText" vartype="java.lang.String" param="promoBox.imageAltText" />
		   <dsp:getvalueof var="promoBoxContent" vartype="java.lang.String" param="promoBox.promoBoxContent" />
	       <dsp:getvalueof var="imageMapName" vartype="java.lang.String" param="promoBox.imageMapName" scope="request" />
	       <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoBox.imageMapContent" scope="request" />

	      	<dsp:getvalueof var="imageLink" vartype="java.lang.String" param="promoBox.imageLink" scope="request" />
	        
	        <c:if test="${!empty imageURL}">
	           <a href="${imageLink}"> <img src="${imageURL}" alt="${imageAltText}" title="${imageAltText}" border="0" width="478" height="183" usemap="#${imageMapName}" /></a>
		     </c:if>
            
            <c:if test="${!empty promoBoxContent}">
	           <dsp:valueof param="promoBox.promoBoxContent" valueishtml="true"/>
            </c:if>
                       
	         
            