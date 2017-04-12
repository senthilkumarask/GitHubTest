<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:getvalueof var="siteId" bean="/atg/multisite/SiteContext.site.id" />
<dsp:getvalueof var="defaultUserCountryCode" param="defaultUserCountryCode" />


<c:set var="listPriceClass" value="isPrice highlightRed"/>
<dsp:getvalueof var="dynamicProdEligible" param="element.dynamicPriceVO.dynamicProdEligible"/>

<c:choose>
	<c:when test="${defaultUserCountryCode eq 'MX'}">
	
		<dsp:getvalueof var="wasPriceRange" param="element.wasPriceRangeMX"/>
		
		<dsp:getvalueof var="isPriceRange" param="element.priceRangeMX"/>
		<dsp:getvalueof var="pricingLabelCode" param="element.dynamicPriceVO.mxPricingLabelCode"/>
		<dsp:getvalueof var="inCartFlag" param="element.dynamicPriceVO.mxIncartFlag"/>
		<dsp:getvalueof var="pennyPriceAttr" param="element.pennyPriceMX" />
		</c:when>
	<c:otherwise>
		<dsp:getvalueof var="wasPriceRange" param="element.wasPriceRange"/>
		<dsp:getvalueof var="isPriceRange" param="element.priceRange"/>
		<dsp:getvalueof var="pennyPriceAttr" param="element.pennyPrice" />
		<c:choose>
		<c:when test="${siteId eq 'BedBathCanada'}">
		<dsp:getvalueof var="pricingLabelCode" param="element.dynamicPriceVO.caPricingLabelCode"/>
		<dsp:getvalueof var="inCartFlag" param="element.dynamicPriceVO.caIncartFlag"/>
		</c:when>
		<c:when test="${siteId eq 'BuyBuyBaby'}">
		<dsp:getvalueof var="pricingLabelCode" param="element.dynamicPriceVO.babyPricingLabelCode"/>
		<dsp:getvalueof var="inCartFlag" param="element.dynamicPriceVO.babyIncartFlag"/></c:when>
		<c:otherwise>
		<dsp:getvalueof var="pricingLabelCode" param="element.dynamicPriceVO.bbbPricingLabelCode"/>
		<dsp:getvalueof var="inCartFlag" param="element.dynamicPriceVO.bbbIncartFlag"/></c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
	
<%-- <compress:html removeIntertagSpaces="true"> --%>

<div class="priceOfProduct">
<c:choose>  
	<c:when test="${dynamicProdEligible}">
		<c:if test="${inCartFlag}">
			<%-- inCartFlag=true pricLabel Code= null/WAS  --%>
			<div class="disPriceString"> ${lblDiscountedIncart} </div>
			<c:set var="listPriceClass" value="isPrice"/>
		</c:if>
	<dsp:droplet name="IsEmpty">
	<dsp:param name="value" value="${wasPriceRange }" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="isPrice" value="${isPriceRange}" />
			<dsp:getvalueof var="wasPrice" value="${wasPriceRange}" />
			<c:choose>
				<c:when test="${pricingLabelCode eq WAS}">
					<c:choose>
						<c:when test="${inCartFlag}">
						<%-- inCartFlag=true pricLabel Code=WAS--%>
							<c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${isPrice}"/></c:set>
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${wasPrice}"/></c:set>
							<div class="wasPrice">
								${priceFromConvertorSale}&nbsp;(${lblWasText}&nbsp;${priceFromConvertor})		
							</div>
						</c:when>
						<c:otherwise>
							<div class="isPrice highlightRed">
								<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPrice}"/></c:set>
								<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
							</div>
							<div class="wasPrice">
								<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${wasPriceRange}"/></c:set>
								${lblWasText} ${priceFromConvertor}
							 </div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${pricingLabelCode eq ORIG}">
						<%-- inCartFlag=true/false pricLabel Code= ORIG --%>
					    <div class="wasPrice">
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPrice}"/></c:set>
							<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${wasPrice}"/></c:set>
							(${lblOrigText} ${priceFromConvertor})
						</div>
						<div class="disclaimer">${lblPVOrigText}</div>
				</c:when>
			</c:choose>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="isPrice" value="${isPriceRange}" />
			<c:choose>
			<c:when test="${pricingLabelCode eq ORIG && inCartFlag}">
				<%-- inCartFlag=true pricLabel Code= null/WAS  --%>
				<div class="wasPrice">
					<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPrice}"/></c:set>
				 ${priceFromConvertor}
				 </div>
			</c:when>
			<c:otherwise>
				<div class="isPrice">
					<%-- inCartFlag=true/false pricLabel Code= null  --%>
					<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPrice}"/></c:set>
					<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
				</div>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	</c:when>
	<c:otherwise>
	<c:if test="${inCartFlag}">
			<%-- inCartFlag=true pricLabel Code= null/WAS  --%>
			<div class="disPriceString">${lblDiscountedIncart} </div>
			<c:set var="listPriceClass" value="isPrice"/>
	</c:if>
			<c:if test="${pennyPriceAttr ne 'true'}">
				<dsp:droplet name="IsEmpty">
					<dsp:param name="value" value="${wasPriceRange}"/>
					<dsp:oparam name="false">
					<c:choose>
					<c:when test="${inCartFlag}">
						<c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${isPriceRange}"/></c:set>
						<c:set var="priceFromConvertorList"><dsp:valueof converter="currency" value="${wasPriceRange}"/></c:set>
						<div class="wasPrice">
							${priceFromConvertorSale}&nbsp;(${lblWasText}&nbsp;${priceFromConvertorList})
						</div>
					</c:when>
					<c:otherwise>
						<div class="${listPriceClass}">
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPriceRange}"/></c:set>
							<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
							
						 </div>
						<div class="wasPrice">
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${wasPriceRange}"/></c:set>
							${lblWasText} ${priceFromConvertor}
					 	 </div>
					 </c:otherwise>
				   </c:choose>	
				</dsp:oparam >
				<dsp:oparam name="true">
					<div class="isPrice">
						<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${isPriceRange}"/></c:set>
						<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
						
					 </div>				   
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
		<c:if test="${pennyPriceAttr eq 'true'}">
		<div class="isPrice">
		 ${lblPriceTBD}
		</div>
		</c:if>
	</c:otherwise>
</c:choose>
</div>
<%-- </compress:html> --%>
</dsp:page>
