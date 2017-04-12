<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<dsp:page>
 <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
   <c:set var="omniPrice" value="" scope="request"/>
   <c:set var="sflSalePrice" value="" scope="request"/>
   <dsp:getvalueof var="registryId" param="registryId"/>
   <dsp:getvalueof var="paramSkuId" param="skuId"/>
   <dsp:getvalueof var="paramProductId" param="productId"/>
   <dsp:getvalueof var="currentProductId" param="product"/>
   <dsp:getvalueof var="currentSkuId" param="sku"/>
   <dsp:getvalueof var="isFromPDP" param="isFromPDP"/>
   <dsp:getvalueof var="priceLabelCodeSKU" param="priceLabelCodeSKU"/>
   <dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU"/>
   <dsp:getvalueof var="isFormatRequired" param="isFormatRequired" />
   <c:set var="WAS">
	<bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" />
	</c:set>	
	<c:set var="ORIG">
 	<bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="listPriceClass" value="isPrice highlightRed"/>
	<c:set var="isFormatRequiredClass" value=""/>
	<c:if test="${not empty isFormatRequired && isFormatRequired}">
	<c:set var="isFormatRequiredClass" value="priceFormatter"/>
	</c:if>
	<%--BBBH-3982 | flag introduced so that incart price is shown only on kickstarter page --%>
   <dsp:getvalueof var="showInCartPrice" param="showInCartPrice"/>
   <c:if test="${empty showInCartPrice}">
		<c:set var="showInCartPrice" value="false"/>
   </c:if>
   <c:choose>
	<c:when test="${fn:containsIgnoreCase(defaultUserCountryCode, 'mx')}">
    	<c:set var="incartPriceList"><bbbc:config key="MexInCartPriceList" configName="ContentCatalogKeys" /></c:set>
	</c:when>
	<c:otherwise>
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite || currentSiteId eq BuyBuyBabySite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BedBathCanadaSite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
   </c:choose>
   </c:otherwise>
   </c:choose>
	
<%-- This jsp displays the price for default sku products--%>
<compress:html removeIntertagSpaces="true">      
<div class="priceOfProduct">
<%-- incartFlag= true and priceLabelCode= WAS/null--%>
<c:if test="${inCartFlagSKU && !showInCartPrice}">
	 <div class="disPriceString"> 
     <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> 
     <c:set var="listPriceClass" value="isPrice"/>
     </div>
 </c:if>
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>
       
        <dsp:droplet name="PriceDroplet">
        <dsp:param name="product" param="product"/>
        <dsp:param name="sku" param="sku"/>
       	<dsp:oparam name="output">
	        <dsp:setvalue param="theListPrice" paramvalue="price"/>
	         <dsp:getvalueof var="profileListPrice" param="price.listPrice"/>
	         	<%-- The second call is in case the incart price exists --%>
			<c:if test="${inCartFlagSKU && showInCartPrice}">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" param="product" />
					<dsp:param name="sku" param="sku" />
					<dsp:param name="priceList" value="${incartPriceList}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
							param="price.listPrice" />
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
			<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
	         <%-- list price form list price list--%>
	        <dsp:getvalueof var="price" vartype="java.lang.Double" param="price.listPrice"/>
	         <c:choose>
	               <c:when test="${not empty profileSalePriceList && (profileListPrice > 0.01)}">
	               <%-- when sale price list is available--%>
	              <dsp:droplet name="PriceDroplet">
		               <dsp:param name="priceList" bean="Profile.salePriceList"/>
		                <dsp:oparam name="output">
	                	<c:choose>
							<c:when test="${inCartFlagSKU && showInCartPrice}">
								<c:set var="price" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
							    <dsp:getvalueof var="price" vartype="java.lang.Double" param="price.listPrice"/>
							</c:otherwise>
						</c:choose>
		                 <%-- list price from sale price list--%>
	                    <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="theListPrice.listPrice"/>	                   
						<c:set var="sflSalePrice" scope="request" value="${price}"/>
	                     <c:choose>
			 				<c:when test="${not empty priceLabelCodeSKU }">
			 				<%--priceLabelCode= WAS/Orig--%>
			 				<c:choose>
				 				<c:when test="${priceLabelCodeSKU eq WAS}">
				 				<%--priceLabelCode= WAS--%>
					 				<c:choose>
										<c:when test="${inCartFlagSKU && !showInCartPrice}">
										<%-- incartFlag= true and priceLabelCode= WAS--%>
										<c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${price}"/></c:set>
				                        <c:set var="priceFromConvertorList"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
				                         <div class="wasPrice">
											${priceFromConvertorSale}&nbsp;(<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${priceFromConvertorList})
										</div>
										</c:when>
										<c:otherwise>
										<%-- incartFlag= false and priceLabelCode= WAS--%>
											<div class="isPrice highlightRed">
										      <dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price }"/>
	                             				<dsp:param name="isFromPDP" value="${isFromPDP }"/>
	                           				  </dsp:include>
	                           				  <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
	                           				  <c:if test="${isFromPDP}">
										      <span class="visuallyhidden">${priceFromConvertor}</span>
										      </c:if>
										    </div>
										    <div class="wasPrice">         
										      <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
										      <bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${priceFromConvertor}        
										   </div>
										</c:otherwise>
								    </c:choose>
				 			     </c:when>
				 				<c:when test="${priceLabelCodeSKU eq ORIG}">
				 				<%-- incartFlag= true/false and priceLabelCode= ORIG--%>

									 <div class="wasPrice">
									 <dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price }"/>
		                          		<dsp:param name="isFromPDP" value="${isFromPDP }"/>
		                        	 </dsp:include>
							<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
							<c:if test="${isFromPDP}">
								      <span class="visuallyhidden">${priceFromConvertor}</span>
								      </c:if>
							<c:set var="priceFromConvertor">
								<dsp:valueof converter="currency" value="${listPrice}"/>
							</c:set>
							(<bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" />&nbsp;${shopperCurrency}${priceFromConvertor})
						</div>
						 <div class="disclaimer"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></div>
							 </c:when>
				 		</c:choose>
				 		</c:when>
				 		<c:otherwise>
				 		<c:choose>
				 		<c:when test="${inCartFlagSKU && !showInCartPrice}">
				 		<%-- incartFlag= true and priceLabelCode= not available--%>
				 			<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
	                         <c:set var="priceFromConvertorSale"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
	                         <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
		                      <c:set var="priceFromConvertorList"><dsp:valueof converter="currency" value="${price}"/></c:set>
		                      <div class="wasPrice">
				                 ${priceFromConvertorSale}&nbsp;(<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${priceFromConvertorList})
		                     </div>
	                     </c:when>
				 		<c:otherwise>
				 		<c:choose>
							<c:when test="${inCartFlagSKU && showInCartPrice}">
								<c:set var="listPrice" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
							    <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
							</c:otherwise>
						</c:choose>
				 		<%-- incartFlag= false and priceLabelCode= not available--%>
				 		<div class="isPrice highlightRed">
                                <dsp:include page="/global/gadgets/formattedPrice.jsp">
	                              <dsp:param name="price" value="${listPrice }"/>
	                              <dsp:param name="isFromPDP" value="${isFromPDP }"/>
	                            </dsp:include>
	                            <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${listPrice}"/></c:set>
	                            <c:if test="${isFromPDP}">
	                            <span class="visuallyhidden">${priceFromConvertor}</span>
	                            </c:if>
                          </div>
                            <c:choose>
	                          <c:when test="${listPrice <= 0.01}">
	                          		<%-- Don't show was price --%>
	                          </c:when>
	                          <c:otherwise>
                          		<div class="wasPrice">
							   		<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
		                            <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
	                             	<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />&nbsp;${priceFromConvertor}
	                            </div>
	                          </c:otherwise>
	                       </c:choose>
				 		</c:otherwise>
				 		</c:choose>
				 	</c:otherwise>
				 	</c:choose>
				 	<c:set var="priceFromConvertor"><dsp:valueof value="${price}" converter="currency"/></c:set>
                 <c:set var="omniPrice" scope="request" value="${priceFromConvertor}">
                 </c:set>
				 		
		        </dsp:oparam>
		        <dsp:oparam name="empty">
		         <c:choose>
                      <c:when test="${inCartFlagSKU && showInCartPrice}">
                          <c:set var="price" value="${inCartPrice}" />
                      </c:when>
                      <c:otherwise>
                        <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                      </c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${priceLabelCodeSKU eq ORIG && inCartFlagSKU && !showInCartPrice}">
						<div class="wasPrice">
							 <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
							${priceFromConvertor}
						</div>
					</c:when>
			<c:otherwise>	
				  <%-- no output from sale Price list--%>
		               <div class="isPrice">
		         		<dsp:include page="/global/gadgets/formattedPrice.jsp">
		                      <dsp:param name="isFromPDP" value="${isFromPDP }"/>
		                       <dsp:param name="price" value="${price}"/>
                      </dsp:include>
                       <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
                       <c:if test="${isFromPDP}">
                       <span class="visuallyhidden">${priceFromConvertor}</span>
                       </c:if>
                     </div>
			 </c:otherwise>
			 </c:choose>
				
		      
                     <c:set var="priceFromConvertor"><dsp:valueof value="${price}" converter="currency"/></c:set>
                 <c:set var="omniPrice" scope="request" value="${priceFromConvertor}">
                 </c:set>
                      
               </dsp:oparam>
		      </dsp:droplet>
	        </c:when>
	          <c:otherwise>
				<c:choose>
					<c:when test="${inCartFlagSKU && showInCartPrice}">
						<c:set var="price" value="${inCartPrice}" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
					</c:otherwise>
				</c:choose>
	          <%-- sale price list not available--%>
	             <div class="isPrice">
                 <dsp:include page="/global/gadgets/formattedPrice.jsp">
	                 <dsp:param name="isFromPDP" value="${isFromPDP }"/>
	                  <dsp:param name="price" value="${price }"/>
                 </dsp:include>
                 <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
                 <c:if test="${isFromPDP}">
                 <span class="visuallyhidden">${priceFromConvertor}</span>
                 </c:if>
                 </div>
                 <c:set var="priceFromConvertor"><dsp:valueof value="${price}" converter="currency"/></c:set>
                 <c:set var="omniPrice" scope="request" value="${priceFromConvertor}">
                 </c:set>
                   <input type="hidden" name="listPrice" value="${price }"  />
	          </c:otherwise>
	      </c:choose>
	   </dsp:oparam>
  </dsp:droplet>
        <c:if test="${not empty registryId && (paramSkuId == currentSkuId || paramProductId == currentProductId)}">
	          <dsp:getvalueof var="registryEvar23Price" bean="SessionBean.registryEvar23Price" />
			  <input type="hidden" value="${registryEvar23Price}" id="registryItemPrice" name="registryItemPrice" />
		</c:if>
        <script>BBB.addPerfMark("ux-primary-content-displayed");</script>
</div>
 <dsp:getvalueof var="finalPrice" vartype="java.lang.Double" value="${price}" scope="request"/>	
 <c:if test="${not empty listPrice}">
  <dsp:getvalueof var="finalPrice" vartype="java.lang.Double" value="${listPrice}" scope="request"/>	
 </c:if>
</compress:html>
</dsp:page>