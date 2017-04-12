<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
   <dsp:getvalueof var="priceLabelCodeSKU" param="priceLabelCodeSKU"/>
   <dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU"/>
   <dsp:getvalueof var="showInCartPrice" param="showInCartPrice"/>
   <c:set var="WAS">
	<bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" />
	</c:set>	
	<c:set var="ORIG">
 	<bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" />
	</c:set>
	
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
  
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>
       <c:if test="${inCartFlagSKU  && !showInCartPrice}">
		<li class="red fontSize_14 bold inCartMsg"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </li>
	 </c:if>
  <dsp:getvalueof var="isSale" param="isSale"/> 
  <dsp:droplet name="PriceDroplet">
       <dsp:param name="product" param="product"/>
       <dsp:param name="sku" param="sku"/>
       <dsp:oparam name="output">
              <dsp:setvalue param="theListPrice" paramvalue="price"/>
			  
			  <%-- The second call is in case the incart price exists --%>
				<c:if test="${inCartFlagSKU && showInCartPrice && not empty showInCartPrice}">
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
              <%-- The third call is in case the sale price exists --%>

              <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
              <dsp:getvalueof var="profileListPrice" param="price.listPrice"/>
              
              <c:choose>
                <c:when test="${not empty profileSalePriceList && (profileListPrice > 0.01)}">
                  <dsp:droplet name="PriceDroplet">
                    <dsp:param name="priceList" bean="Profile.salePriceList"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
						
						<c:choose>
							<c:when test="${inCartFlagSKU && not empty showInCartPrice && showInCartPrice}">
								<c:set var="listPrice" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
							</c:otherwise>
						</c:choose>
                        
                        <c:set var="omniPrice" value="${listPrice}" scope="request"/>
                        <c:choose>
			 				<c:when test="${not empty priceLabelCodeSKU }">
			 				
                        	<c:choose>
					   		<c:when test="${priceLabelCodeSKU eq WAS}">
					   				<c:choose>
									<c:when test="${inCartFlagSKU}">
										<li class="grayText fontSize_11 bold wasOrigMsg"> 
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price}"/>
                              					<dsp:param name="isFromPDP" value="${isFromPDP}"/>
                            				</dsp:include> 
                            				(<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /><dsp:include page="/global/gadgets/formattedPrice.jsp"><dsp:param name="price" value="${listPrice}"/><dsp:param name="isFromPDP" value="${isFromPDP}"/></dsp:include>)</li>
									</c:when>
									<c:otherwise>
									<li class="red fontSize_18 bold skuPrice"> 
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${price}"/>
                             					<dsp:param name="isFromPDP" value="${isFromPDP}"/>
                           				</dsp:include>
									</li>
									<li class="grayText fontSize_11 bold wasOrigMsg">  
										<bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /> 
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice}"/>
                              					<dsp:param name="isFromPDP" value="${isFromPDP}"/>
                            				</dsp:include>
										</li>
									</c:otherwise>
									</c:choose>
					   		</c:when>
					   		<c:when test="${priceLabelCodeSKU eq ORIG}">
					   		
					   				<div class="wasPrice">
					   					<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${price }"/>
                             				<dsp:param name="isFromPDP" value="${isFromPDP }"/>
                           				</dsp:include>
					   				   &nbsp;(<bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" /> 
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice }"/>
                              					<dsp:param name="isFromPDP" value="${isFromPDP }"/>
                            			</dsp:include>)
                            		</div>		
								  	<div class="disclaimer"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></div>	
					   		</c:when>
					   		</c:choose>
					   		</c:when>
					   		<c:otherwise>
                        
                        <%-- BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand starts --%>
						<c:choose>
							<c:when test="${inCartFlagSKU && not empty showInCartPrice && showInCartPrice}">              
								<c:set var="price" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="price" vartype="java.lang.Double" param="price.listPrice"/>
							</c:otherwise>
						</c:choose>
						
						
            <li class="prodPrice">
               <li class="isPrice">
                <span class="highlightRed"><dsp:valueof converter="currency" value="${price}" /></span>
              </li>
               <li class="wasPrice">
                <span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /> 
                  &nbsp;<dsp:valueof converter="currency" param="theListPrice.listPrice" /></span>
              </li>
            </li>
             </c:otherwise>
					   		</c:choose>
            <%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>
                    </dsp:oparam>
                    <dsp:oparam name="empty">
                     <c:choose>
							<c:when test="${inCartFlagSKU && not empty showInCartPrice && showInCartPrice}">              
								<c:set var="price" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
							</c:otherwise>
						</c:choose>
						 <c:set var="omniPrice" value="${price}" scope="request"/>
                    <c:choose>
					<c:when test="${priceLabelCodeSKU eq ORIG && inCartFlagSKU && !showInCartPrice}">
						<div class="wasPrice">
							 <c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${price}"/></c:set>
							 ${priceFromConvertor}
						</div>
					</c:when>
					<c:otherwise>
					<li class="prodPrice">
                        <dsp:include page="/global/gadgets/formattedPrice.jsp">
		                        <dsp:param name="price" value="${price }"/>
		                    </dsp:include>
                       </li>  
					</c:otherwise>
					</c:choose>
                    </dsp:oparam>
                  </dsp:droplet><%-- End price droplet on sale price --%>
                </c:when>
                <c:otherwise>
				<c:choose>
					<c:when test="${inCartFlagSKU && not empty showInCartPrice && showInCartPrice}">              
						<c:set var="price" value="${inCartPrice}" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
					</c:otherwise>
				</c:choose>
                  <c:set var="omniPrice" value="${price}" scope="request" />
                  <li class="prodPrice">
                 <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${price }"/>
                  </dsp:include>
                  </li>
                </c:otherwise>
              </c:choose><%-- End Is Empty Check --%>
            </dsp:oparam>
          </dsp:droplet><%-- End Price Droplet --%>
</dsp:page>              