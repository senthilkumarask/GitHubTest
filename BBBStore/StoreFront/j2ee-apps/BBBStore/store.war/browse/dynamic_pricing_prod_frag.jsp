<dsp:page>
<dsp:getvalueof var="priceLabelCodeProd" param="priceLabelCodeProd" />
<dsp:getvalueof var="inCartFlagProd" param="inCartFlagProd" />
<dsp:getvalueof var="salePriceRangeDescription"	param="salePriceRangeDescription" />
<dsp:getvalueof var="priceRangeDescription" param="priceRangeDescription" />
<c:set var="WAS">
	<bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" />
</c:set>	
<c:set var="ORIG">
 	<bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" />
</c:set>


<ul class="inCartPricingAll">

<c:set var="listPriceClass" value="red fontSize_18 bold skuPrice"/>


	<c:if test="${inCartFlagProd && priceLabelCodeProd ne ORIG}">
		 <li class="red fontSize_14 bold inCartMsg"><bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </li>
		 <c:set var="listPriceClass" value="grayText fontSize_11 bold wasOrigMsg"/>
	</c:if>
	<c:choose>
		<c:when test="${not empty salePriceRangeDescription}">
					<c:choose>
				<c:when test="${priceLabelCodeProd eq WAS}">
					<c:choose>
						<c:when test="${inCartFlagProd}">
							<li class="grayText fontSize_11 bold wasOrigMsg"> ${salePriceRangeDescription} (<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /> ${priceRangeDescription})</li>
						</c:when>
						<c:otherwise>
							<li class="red fontSize_18 bold skuPrice"> ${salePriceRangeDescription} </li>
							<li class="grayText fontSize_11 bold wasOrigMsg">  <bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /> ${priceRangeDescription}</li>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${priceLabelCodeProd eq ORIG}">
					<li class="red fontSize_18 bold skuPrice"> ${salePriceRangeDescription} </li>
					<li class="grayText fontSize_11 bold wasOrigMsg"><bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" /> ${priceRangeDescription}</li>		
				  	<li class="grayText fontSize_10 priceVariationMsg"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></li>	
				  	<c:if test="${inCartFlagProd}">	
				     	<li class="grayText fontSize_10 inCartDisclaimerMsg"><bbbl:label key="lbl_discount_incart_orig_text" language="${pageContext.request.locale.language}" /></li>
					</c:if>
				</c:when>
			</c:choose>
		</c:when>
		<c:otherwise>
			<li class="${listPriceClass}"> ${priceRangeDescription} </li>
		</c:otherwise>
      </c:choose>
</ul>
</dsp:page>