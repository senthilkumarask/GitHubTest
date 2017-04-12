<%-- Katori Integration --%>
<dsp:getvalueof var="skuVO" param="skuVO" />
<dsp:getvalueof var="inStock" param="inStock" />
<c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
<c:choose>
<c:when test="${requestScope.personalizedSku ne null }">
	<c:set var="itemAlreadyPersonalized" value="true" scope="request"/>
</c:when>
<c:otherwise>
	<c:set var="itemAlreadyPersonalized" value="false" scope="request" />
</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
		<c:set var="customizeTxt" value="true"/>
	</c:when>
	<c:otherwise>
		<c:set var="customizeTxt" value="false"/>
	</c:otherwise>
</c:choose>

<dsp:getvalueof var="removePersonalization" param="removePersonalization" scope="request"/>
<c:choose>
        <c:when test="${removePersonalization eq true}">
	 <div class="grid_12">
        <div class="personalizedPDPContainer">
	 <h3 class="personaliseMsg" >
	 	<c:choose>
	 		<c:when test="${customizeTxt eq true}">
	 			<bbbt:textArea key="txt_customization_removed" language="${pageContext.request.locale.language}" />
	 		</c:when>
	 		<c:otherwise>
	 			<bbbt:textArea key="txt_personalization_removed" language="${pageContext.request.locale.language}" />
	 		</c:otherwise>
	 	</c:choose>
      </c:when>
     <c:when test="${itemAlreadyPersonalized eq true and personalizedSku.personalizationComplete eq false and not isInternationalCustomer }">
	  <div class="grid_12">
       <div class="personalizedPDPContainer">
       	 <h3 class="personaliseMsg" >
       	 	<c:choose>
		 		<c:when test="${customizeTxt eq true}">
		 			<bbbt:textArea key="txt_inProgress_customization" language="${pageContext.request.locale.language}" />
		 		</c:when>
		 		<c:otherwise>
		       	 	<bbbt:textArea key="txt_inProgress_Personalization" language="${pageContext.request.locale.language}" />
		 		</c:otherwise>
		 	</c:choose>
       	 </h3>
     </c:when>
     <c:when test="${itemAlreadyPersonalized eq true and personalizedSku.personalizationComplete eq false and isInternationalCustomer}">
       <div class="grid_12">
       <div class="personalizedPDPContainer">
       	 <h3 class="personaliseMsg" >
       		 <c:choose>
		 		<c:when test="${customizeTxt eq true}">
		 			<bbbt:label key="txt_inProgress_customization_Non_USA" language="${pageContext.request.locale.language}" />
		 		</c:when>
		 		<c:otherwise>
		       	 	<bbbt:label key="txt_inProgress_Personalization_Non_USA" language="${pageContext.request.locale.language}" />
		 		</c:otherwise>
		 	</c:choose>
       	 </h3>
       </c:when>
      <c:when test="${itemAlreadyPersonalized and not isInternationalCustomer }">
	 <div class="grid_12">
      <div class="personalizedPDPContainer">
      	 <h3 class="personaliseMsg" >
      	 	<c:choose>
      	 		<c:when test="${inStock}">
      	 			<c:choose>
				 		<c:when test="${customizeTxt eq true}">
				 			<bbbt:label key="lbl_cart_customization_saved" language="${pageContext.request.locale.language}" />
				 		</c:when>
				 		<c:otherwise>
				       	 	<bbbt:label key="lbl_cart_personalization_saved" language="${pageContext.request.locale.language}" />
				 		</c:otherwise>
				 	</c:choose>
      	 		</c:when>
      	 		<c:otherwise>
      	 			<c:choose>
				 		<c:when test="${customizeTxt eq true}">
				 			<bbbt:label key="lbl_cart_customization_saved_OOS" language="${pageContext.request.locale.language}" />
				 		</c:when>
				 		<c:otherwise>
				       	 	<bbbt:label key="lbl_cart_personalization_saved_OOS" language="${pageContext.request.locale.language}" />
				 		</c:otherwise>
				 	</c:choose>
      	 		</c:otherwise>
      	 	</c:choose>
      	 </h3>
      </c:when>
      <c:when test="${itemAlreadyPersonalized and isInternationalCustomer}">
	 <div class="grid_12">
      <div class="personalizedPDPContainer">
      	 <h3 class="personaliseMsg" >
      	 	<c:choose>
		 		<c:when test="${customizeTxt eq true}">
		 			<bbbt:label key="lbl_cart_customization_saved_nonUS" language="${pageContext.request.locale.language}" />
		 		</c:when>
		 		<c:otherwise>
		       	 	<bbbl:label key='lbl_cart_personalization_saved_nonUS' language="${pageContext.request.locale.language}" />
		 		</c:otherwise>
		 	</c:choose>
      	 </h3>
      </c:when>
        <c:otherwise>
	 <div class="grid_12">
       <div class= "personalizedPDPContainer  <c:if test="${ itemAlreadyPersonalized || isInternationalCustomer || not enableKatoriFlag || (empty skuVO.customizableRequired) || (skuVO.customizableRequired=='false')}">hidden</c:if>">
	 <h3 class="personaliseMsg">
			<c:choose>
		 		<c:when test="${customizeTxt eq true}">
		 			<bbbt:label key="lbl_customization_required_below" language="${pageContext.request.locale.language}" />
		 		</c:when>
		 		<c:otherwise>
		       	 	<bbbl:label key='lbl_personalization_required_below' language="${pageContext.request.locale.language}" />
		 		</c:otherwise>
		 	</c:choose>			 		
	   </c:otherwise>
    </c:choose>
	</h3>
</div>
</div>
<c:if test="${itemAlreadyPersonalized }">
<c:forEach var="element" items="${personalizedSku.eximResponse.images[0].previews }">
	<c:choose>
		<c:when test="${element.size eq 'large'}">
			<c:set var="personalizedLargeImage" value="${element.url}" scope="request" />
		</c:when>
		<c:when test="${element.size eq 'medium'}">
			<c:set var="personalizedMediumImage" value="${element.url}" scope="request" />
		</c:when>
		<c:when test="${element.size eq 'x-small'}">
			<c:set var="personalizedSmallImage" value="${element.url}" scope="request" />
		</c:when>
		<c:when test="${element.size eq 'x-large'}">
		   <c:set var="personalizedXLargeImage" value="${element.url}" scope="request" />
	    </c:when>
	</c:choose>
	</c:forEach>
</c:if>
<input type="hidden" value="${itemAlreadyPersonalized }" id="itemAlreadyPersonalized"/>
<input type="hidden" value="${personalizedMediumImage }" id="imageURL_thumb"/>
<input type="hidden" value="${personalizedLargeImage }" id="imageURL_full" />
<input type="hidden" value="${personalizedSku.altImages }" id="hasAltImages" />
<input type="hidden" value="${personalizedXLargeImage}" id="imageURL_hires"/>
<c:choose>
	<c:when test="${skuVO.customizableRequired eq true and itemAlreadyPersonalized eq true}">
		<c:choose>
			<c:when test="${personalizedSku.personalizationComplete eq false }">
				<c:set var="disableCTA" value="true"  scope="request" />
			</c:when>
			<c:when test="${personalizedSku.personalizationComplete eq true }">
				<c:set var="disableCTA" value="false" scope="request" />
			</c:when>
		</c:choose>
		<c:set var="disableSFL" value="false" />
	</c:when>
	<c:when test="${skuVO.customizationOffered eq true and itemAlreadyPersonalized eq true and personalizedSku.personalizationComplete eq false}">
		<c:choose>
			<c:when test="${personalizedSku.personalizationComplete eq false }">
				<c:set var="disableCTA" value="true" scope="request" />
			</c:when>
			<c:when test="${personalizedSku.personalizationComplete eq true }">
				<c:set var="disableCTA" value="false" scope="request" />
			</c:when>
		</c:choose>
		<c:set var="disableSFL" value="false" />
	</c:when>
	<c:when test="${skuVO.customizableRequired eq true and itemAlreadyPersonalized eq false}">
		<c:set var="disableCTA" value="true" scope="request" />
		<c:set var="disableSFL" value="true" scope="request" />
	</c:when>
	<c:when test="${skuVO.customizationOffered eq true and itemAlreadyPersonalized eq false }">
		<c:set var="disableCTA" value="false" scope="request" />
		<c:set var="disableSFL" value="false" scope="request" />
	</c:when>
</c:choose>
<c:if test="${isInternationalCustomer eq true and itemAlreadyPersonalized eq true }">
	<c:set var="disableCTA" value="true" scope="request" />
	<c:set var="disableSFL" value="true" scope="request" />
	<c:set var="disableEditRemove" value="true" scope="request" />
</c:if>
<%-- Katori Integration ends --%>