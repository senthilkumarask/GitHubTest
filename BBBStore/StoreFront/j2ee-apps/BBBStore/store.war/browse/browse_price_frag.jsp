<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<dsp:page>
<dsp:getvalueof var="priceLabelCode" param="priceLabelCode" />
<dsp:getvalueof var="inCartFlag" param="inCartFlag" />
<dsp:getvalueof var="salePrice"	param="salePrice" />
<dsp:getvalueof var="listPrice" param="listPrice" />
<dsp:getvalueof var="isFromPDP" param="isFromPDP" />
<dsp:getvalueof var="isFormatRequired" param="isFormatRequired" />
<dsp:getvalueof var="isKatoriPrice" param="isKatoriPrice" />
<dsp:getvalueof var="defaultPriceRange" param="defaultPriceRange" />
<dsp:getvalueof var="isdynamicPriceEligible" param="isdynamicPriceEligible" />
<dsp:getvalueof var="shopperCurrency" param="shopperCurrency" />
<dsp:getvalueof var="mutliSKUQuickView" param="mutliSKUQuickView" />

<c:set var="WAS">
	<bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" />
</c:set>	
<c:set var="ORIG">
 	<bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" />
</c:set>
<c:set var="listPriceClass" value="isPrice highlightRed"/>
<c:choose>
	<c:when test="${isKatoriPrice}">
	<c:set var="wasFlag" value="false:true"/>
		<c:choose>
			<c:when test="${isFromPDP}">
				<c:set var="isFromPDP" value="true:true"/>
				<c:set var="showVisuallyHidden" value="true"/>
			</c:when>
			<c:otherwise>
				<c:set var="isFromPDP" value="false:true"/>
				<c:set var="showVisuallyHidden" value="false"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
	<c:set var="shopperCurrency" value=""/>
	<c:set var="wasFlag" value="false:false"/>
	<c:choose>
		<c:when test="${isFromPDP}">
		<c:set var="isFromPDP" value="true:false"/>
		<c:set var="showVisuallyHidden" value="true"/>
		</c:when>
		<c:otherwise>
		<c:set var="isFromPDP" value="false:false"/>
		<c:set var="showVisuallyHidden" value="false"/>
		</c:otherwise>
	</c:choose>
	</c:otherwise>
</c:choose>

<%-- BBBH-4488  Pricing changes --%>
<compress:html removeIntertagSpaces="true"><c:choose>
	<c:when test="${isdynamicPriceEligible}">
	<%-- Product available in Dynamic Price Repository--%>
	<div class="priceOfProduct">
		<c:if test="${inCartFlag}">
		<%-- inCartFlag=true pricLabel Code= null/WAS  --%>
		<div class="disPriceString"><bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
		<c:set var="listPriceClass" value="isPrice"/>
		</c:if>
		<c:choose>
			<c:when test="${not empty salePrice}">
			<c:choose>
					<c:when test="${priceLabelCode eq WAS}">
						<c:choose>
							<c:when test="${inCartFlag}">
							<%-- inCartFlag=true pricLabel Code=WAS--%>
								<c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
								<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
								 <div class="wasPrice">
									${shopperCurrency}${priceFromConvertorSale}&nbsp;(<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor})
								</div>
							</c:when>
							<c:otherwise>
							<%-- inCartFlag=false pricLabel Code=WAS--%>
								<div class="isPrice highlightRed">
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
									<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
									<c:if test="${showVisuallyHidden}">
									<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
									</c:if>
								</div>
								<div class="wasPrice">
							         <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
									 <bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor}
								</div>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${priceLabelCode eq ORIG}">
						 <div class="wasPrice">
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
							<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
							<c:if test="${showVisuallyHidden}">
								<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
							</c:if>
							<c:set var="priceFromConvertor">
								<dsp:valueof converter="currency" value="${listPrice}"/>
							</c:set>
							(<bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor})
						</div>
						 <div class="disclaimer"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></div>
					</c:when>
					<c:otherwise>
						 		<div class="isPrice highlightRed">
									<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
									<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
									<c:if test="${showVisuallyHidden}">
									<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
									</c:if>
									
								</div>
								<div class="wasPrice">
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
									<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor} 
								</div>
					</c:otherwise>
			</c:choose>
			</c:when>
			<c:otherwise>
			<c:choose>
			<c:when test="${priceLabelCode eq ORIG && inCartFlag}">
				<%-- inCartFlag=true pricLabel Code= null/WAS  --%>
				<div class="wasPrice">
					<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
					 ${shopperCurrency}${priceFromConvertor}
				 </div>
			</c:when>
			<c:otherwise>	
			<div class="isPrice">
			<%-- inCartFlag=true/false pricLabel Code= null  --%>
				<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
				<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
				<c:if test="${showVisuallyHidden}">
				<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
				</c:if>
				
			 </div>
			 </c:otherwise>
			 </c:choose>
		</c:otherwise>
		</c:choose>
		</div>			
	</c:when>
	<c:otherwise>
	<%-- Product not available in Dynamic Price Repository  --%>
	<div class="priceOfProduct">
		 <c:if test="${inCartFlag}">
		 <%-- inCartFlag=true  --%>
		   <div class="disPriceString"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
		   <c:set var="listPriceClass" value="isPrice"/>
		</c:if>
		<c:choose>
			<c:when test="${not empty salePrice}">
			<dsp:getvalueof var="tbdVar" value="${salePrice}" />
				<c:choose>
						<c:when test="${fn:contains(tbdVar, 'TBD')}">
							<div class="isPrice highlightRed"><dsp:valueof value="${salePrice}" /></div>
						</c:when>
						<c:when test="${salePrice eq '0.01'}">
							<div class="isPrice highlightRed">
							<c:choose>
								<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
									<bbbl:label key='lbl_price_is_tbd_customize' language="${pageContext.request.locale.language}"/>
								</c:when>
								<c:otherwise>
									<bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}"/>
								</c:otherwise>
							</c:choose>		
							</div>
						</c:when>
						<c:otherwise>
						<c:choose>
						<c:when test="${inCartFlag}">
							<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
							<c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
							<c:set var="priceFromConvertorList"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
							 <div class="wasPrice">
								${shopperCurrency}${priceFromConvertorSale}&nbsp;(<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertorList})
							</div>
						</c:when>
						<c:otherwise>
						 		<div class="isPrice highlightRed">
									<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${salePrice}"/></c:set>
									<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
									<c:if test="${showVisuallyHidden}">
									<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
									</c:if>
									
								</div>
								<div class="wasPrice">
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
									<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor} 
								</div>
						</c:otherwise>
						</c:choose>
						</c:otherwise>
					</c:choose>
			</c:when>
			<c:otherwise>
			<dsp:getvalueof var="tbdVar" value="${listPrice}" />
					<c:choose>
						<c:when test="${fn:contains(tbdVar, 'TBD')}">
							<div class="isPrice"><dsp:valueof value="${listPrice}" /></div>
						</c:when>
						<c:when test="${listPrice eq '0.01'}">
							<div class="isPrice">
							<c:choose>
								<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
									<bbbl:label key='lbl_price_is_tbd_customize' language="${pageContext.request.locale.language}"/>
								</c:when>
								<c:otherwise>
									<bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}"/>
								</c:otherwise>
							</c:choose>
							</div>
						</c:when>
						<c:otherwise>
						<div class="isPrice"><c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
								<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
								<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
								<c:if test="${showVisuallyHidden}">
								<span class="visuallyhidden">${shopperCurrency}${priceFromConvertor}</span>
								</c:if>
							</div>
						</c:otherwise>
					</c:choose>
			</c:otherwise>
        </c:choose>
        </div>
    </c:otherwise>
</c:choose></compress:html>

</dsp:page>
