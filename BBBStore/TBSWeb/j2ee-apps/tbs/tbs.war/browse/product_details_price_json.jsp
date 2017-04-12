<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>.
  <dsp:getvalueof var="skuInCartFlag" param="inCartFlag"/>
  <dsp:getvalueof var="currentSiteId" param="currentSiteId"/>
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode"
			configName="ContentCatalogKeys" />
	</c:set>

  <c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
  </c:choose>
  
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>

              <dsp:droplet name="PriceDroplet">
                <dsp:param name="product" param="product"/>
                <dsp:param name="sku" param="sku"/>
                <dsp:oparam name="output">
                  <dsp:setvalue param="theListPrice" paramvalue="price"/>
                  <%-- The second call is in case the incart price exists --%>
                  		 
  						<c:if test="${skuInCartFlag}">
		                  <dsp:droplet name="PriceDroplet">	
		                	<dsp:param name="product" param="product"/>
		                	<dsp:param name="sku" param="sku"/>
		                	<dsp:param name="priceList" value="${incartPriceList}"/>
		                  	<dsp:oparam name="output">
		                  	<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double" param="price.listPrice"/>
		                  	</dsp:oparam>
		                  </dsp:droplet>
		                 </c:if>
						
						
					  <%-- The second call is in case the sale price exists --%>
					  <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
						 
                      <dsp:droplet name="PriceDroplet">
                        <dsp:param name="priceList" value="${profileSalePriceList}"/>
                        <dsp:oparam name="output">

						<c:choose>
  							<c:when test="${skuInCartFlag}">
  								<c:set var="listPrice" value="${inCartPrice}"/>
  							</c:when>
  							<c:otherwise>
  								<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
  							</c:otherwise>
  						</c:choose>

						<json:object name="price">
						<json:property name="wasPrice">
                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                <dsp:param name="price" value="${price }"/>
                              </dsp:include>
						</json:property>
						
						
						<c:choose>
			              <c:when test="${price<=0.01}">
						<json:property name="priceTBD" value="true"></json:property>
					    </c:when>
					    <c:otherwise>
					    <json:property name="priceTBD" value="false"></json:property>
					    </c:otherwise>
					    </c:choose>
						<json:property name="isPrice">	
                            <dsp:include page="/global/gadgets/formattedPrice.jsp">
                              <dsp:param name="price" value="${listPrice }"/>
                            </dsp:include>
                         </json:property>
                         </json:object>   
                        </dsp:oparam>
                        <dsp:oparam name="empty">
						<c:choose>
							<c:when test="${skuInCartFlag}">
								<c:set var="price" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
							</c:otherwise>
						</c:choose>
						<json:property name="price">
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" value="${price }"/>
                          </dsp:include>
                         </json:property>
                         	<c:choose>
			              <c:when test="${price<=0.01}">
						<json:property name="priceTBD" value="true"></json:property>
					    </c:when>
					    <c:otherwise>
					    <json:property name="priceTBD" value="false"></json:property>
					    </c:otherwise>
					    </c:choose>
                        </dsp:oparam>
                      </dsp:droplet><%-- End price droplet on sale price --%>
                </dsp:oparam>
              </dsp:droplet><%-- End Price Droplet --%>
              
</dsp:page>