<dsp:page>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="skuID" param="skuID" />
	<dsp:getvalueof var="personalisedCode" param="personalisedCode" />
	<dsp:getvalueof var="personlisedPrice" param="personlisedPrice" />
	<dsp:getvalueof var="topFragment" param="topFragment" />
	<dsp:getvalueof var="active" param="active" />
	<dsp:getvalueof var="upc" param="upc" />
	<dsp:getvalueof var="size" param="size" />
	<dsp:getvalueof var="color" param="color" />
	<dsp:getvalueof var="customizedPrice1" param="customizedPrice1" />
	<dsp:getvalueof var="personlisedPrice1" param="personlisedPrice1" />
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="refNum" param="refNum" />
	<dsp:getvalueof var="productName" param="productName" />
	<dsp:getvalueof var="contextPath" param="contextPath" />
	<dsp:getvalueof var="finalUrl" param="finalUrl" />
	<dsp:getvalueof var="customizationDetails" param="customizationDetails" />
	<dsp:getvalueof var="personalizedImageUrls" param="personalizedImageUrls" />
	<dsp:getvalueof var="personalizedImageUrlThumbs" param="personalizedImageUrlThumbs" />
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="customizedPrice" param="customizedPrice" />
	<dsp:getvalueof var="regItem" param="regItem" />
	<dsp:getvalueof var="isDSLUpdateable" param="isDSLUpdateable" />
	<dsp:getvalueof var="personalizationOptionsDisplay" param="personalizationOptionsDisplay"/>
	<dsp:getvalueof var="ltlShipMethodDesc" param="ltlShipMethodDesc"/>
	<dsp:getvalueof var="deliverySurcharge" param="deliverySurcharge"/>
	<dsp:getvalueof var="assemblyFee" param="assemblyFee"/>
	<dsp:getvalueof var="shipMethodUnsupported" param="shipMethodUnsupported"/>	
	<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
	<dsp:getvalueof var="ltlShipMethod" param="ltlShipMethod"/>	
	<dsp:getvalueof var="skuInCartFlag" param="skuInCartFlag"/>
	
	<c:if test="${skuInCartFlag}">	
		<c:set var="TBS_BedBathUSSite">
			<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BuyBuyBabySite">
			<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BedBathCanadaSite">
			<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>				
			
		<c:choose>
			<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
				<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
			</c:when>
			<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
				<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
			</c:when>
	   </c:choose>
	</c:if>	
	
	<c:choose>
	<c:when test="${ltlItemFlag}">
	<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
	<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
	<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
	<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
	
		  <ul>
			<!--li class="totalLtl"> Total</li-->
			  <li class="toalpriceLtl"><dsp:droplet name="PriceDroplet">
				<dsp:param name="product" value="${productId}" />
				<dsp:param name="sku" value="${skuID}" />
				<dsp:oparam name="output">
					<dsp:setvalue param="theListPrice" paramvalue="price" />
					
					<c:if test="${skuInCartFlag}">
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
				   
					<dsp:getvalueof var="profileSalePriceList"
						bean="Profile.salePriceList" />
					<c:choose>
						<c:when test="${not empty profileSalePriceList}">
							<dsp:droplet name="PriceDroplet">
								<dsp:param name="priceList" bean="Profile.salePriceList" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								<c:choose>		
									<c:when test="${skuInCartFlag}">
										<c:set var="listPrice" value="${inCartPrice}" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
											param="price.listPrice" />
									</c:otherwise>
								</c:choose>

									<c:if test="${listPrice gt 0.10}">
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<c:choose>	
												<c:when test="${shipMethodUnsupported}">
													<dsp:param name="price" value="${listPrice}" />
												</c:when>
												<c:otherwise>
												 	<dsp:param name="price" value="${listPrice+deliverySurcharge+assemblyFee }" />
												</c:otherwise>
											</c:choose>
										</dsp:include>
									</c:if>										
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

									<c:if test="${price gt 0.10}">
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<c:choose>	
												<c:when test="${shipMethodUnsupported}">
													<dsp:param name="price" value="${price}" />
												</c:when>
												<c:otherwise>
												 	<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
												</c:otherwise>
											</c:choose>
										</dsp:include>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
							<%-- End price droplet on sale price --%>
						</c:when>
						<c:otherwise>
						<c:choose>
							<c:when test="${skuInCartFlag}">
								<c:set var="price" value="${inCartPrice}" />
							</c:when>
							<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
								param="theListPrice.listPrice" />
							</c:otherwise>
						</c:choose>

							<c:if test="${price gt 0.10}">
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<c:choose>	
										<c:when test="${shipMethodUnsupported}">
											<dsp:param name="price" value="${price}" />
										</c:when>
										<c:otherwise>
											<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
										</c:otherwise>
									</c:choose>
								</dsp:include>
							</c:if>
						</c:otherwise>
					</c:choose>
					<c:if test="${topFragment}">
						<c:if test="${not empty listPrice}">
							<c:set value="${listPrice}" var="price" />
						</c:if>
						<input type="hidden" value="${price}" class="itemPrice" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet></li>
			
			<c:choose>
					<c:when test="${empty ltlShipMethod or shipMethodUnsupported}">
						 <c:choose>
							 <c:when test="${isDSLUpdateable}">
						 		 <li><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" />
								 </a></li>
						 		 <li class="deliveryLtl"></li>
                         		 <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo hidden" /> </li>
							 </c:when>
                         	 <c:otherwise> <li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
                                        <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"><bbbl:label key="lbl_cart_tbd" language="${pageContext.request.locale.language}"/></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /> </li>
                        	 </c:otherwise>
                         </c:choose>
                     </c:when>
					<c:otherwise>
						 <c:choose>
							 <c:when test="${ltlShipMethod== 'LWA' }">
							   <li class="deliveryLtl"> <bbbl:label key="lbl_Incl_White_Glove" language="${pageContext.request.locale.language}"/> <span><bbbl:label key="lbl_With_Assembly" language="${pageContext.request.locale.language}"/></span></li>
							   <li class="deliverypriceLtl">
							       <span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
							       <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
							   </li>
							</c:when>
							<c:otherwise>
							<li class="deliveryLtl">
									 Incl ${ltlShipMethodDesc}:
                                         </li>     <li class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> <bbbl:label key="lbl_shipping_free" language="${pageContext.request.locale.language}"/></c:if>
                                                                 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
								</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
								</c:otherwise>
						</c:choose>
					</c:otherwise>
				 </c:choose>
				 <input type="hidden" value="${ltlShipMethod}" name="ltlShipMethod"/>
			 
			  <li class="itemLtl"><c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">Item Price</c:if>
			<span class="itempriceLtl">
			<dsp:droplet name="PriceDroplet">
				<dsp:param name="product" value="${productId}" />
				<dsp:param name="sku" value="${skuID}" />
				<dsp:oparam name="output">
					<dsp:setvalue param="theListPrice" paramvalue="price" />
					
					<c:if test="${skuInCartFlag}">
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
					
					<dsp:getvalueof var="profileSalePriceList"
						bean="Profile.salePriceList" />
					<c:choose>
						<c:when test="${not empty profileSalePriceList}">
							<dsp:droplet name="PriceDroplet">
								<dsp:param name="priceList" bean="Profile.salePriceList" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
									
								<c:choose>		
									<c:when test="${skuInCartFlag}">
										<c:set var="listPrice" value="${inCartPrice}" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
										param="price.listPrice" />
									</c:otherwise>
								</c:choose>									
									<c:if test="${listPrice gt 0.10}">
									<input type="hidden" name="itemPrice" value="${listPrice}" class="frmAjaxSubmitData" />
										<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${listPrice }" />
										</dsp:include>
										</c:if>
									</c:if>										
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

									<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
									<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
									<c:if test="${price gt 0.10}">
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${price }" />
										</dsp:include>
									</c:if>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
							<%-- End price droplet on sale price --%>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="price" vartype="java.lang.Double"
								param="theListPrice.listPrice" />
							<c:if test="${price gt 0.10}">
							<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
							<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${price}" />
								</dsp:include>
							</c:if>
							</c:if>
						</c:otherwise>
					</c:choose>
					<c:if test="${topFragment}">
						<c:if test="${not empty listPrice}">
							<c:set value="${listPrice}" var="price" />
						</c:if>
						<input type="hidden" value="${price}" class="itemPrice" />
						<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			</span>
		</li> 
				 </ul>
				 </li> 
	</c:when>
	<c:otherwise>
			
	<c:choose>
		<c:when test="${not empty personalisedCode}">
			<div>
				<dsp:include page="/global/gadgets/formattedPrice.jsp">
					<dsp:param name="price" value="${personlisedPrice}" />
				</dsp:include>
				
			</div>
			<input type="hidden" value="${personlisedPrice}" class="itemPrice" />
		</c:when>
		<c:otherwise>
		<span class="columnHeader rlpPrice">	
			<dsp:droplet name="PriceDroplet">
				<dsp:param name="product" value="${productId}" />
				<dsp:param name="sku" value="${skuID}" />
				<dsp:oparam name="output">
					<dsp:setvalue param="theListPrice" paramvalue="price" />
					
				<c:if test="${skuInCartFlag}">
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

					<dsp:getvalueof var="profileSalePriceList"
						bean="Profile.salePriceList" />
					<c:choose>
						<c:when test="${not empty profileSalePriceList}">
							<dsp:droplet name="PriceDroplet">
								<dsp:param name="priceList" bean="Profile.salePriceList" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
									<c:choose>
								<c:when test="${skuInCartFlag}">
									<c:set var="listPrice" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
										param="price.listPrice" />
								</c:otherwise>
							</c:choose>	
										
									<c:if test="${listPrice gt 0.10}">
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${listPrice }" />
										</dsp:include>
									</c:if>										
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
								
									<c:if test="${price gt 0.10}">
										<dsp:include page="/global/gadgets/formattedPrice.jsp">
											<dsp:param name="price" value="${price }" />
										</dsp:include>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
							<%-- End price droplet on sale price --%>
						</c:when>
						<c:otherwise>
						<c:choose>
								<c:when test="${skuInCartFlag}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
								param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
							<c:if test="${price gt 0.10}">
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${price }" />
								</dsp:include>
							</c:if>
						</c:otherwise>
					</c:choose>
					<c:if test="${topFragment}">
						<c:if test="${not empty listPrice}">
							<c:set value="${listPrice}" var="price" />
						</c:if>
						<input type="hidden" value="${price}" class="itemPrice" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			</span>
		</c:otherwise>
	</c:choose>
	</li>
	</c:otherwise>
	</c:choose>
</dsp:page>