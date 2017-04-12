<dsp:page>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="com/bbb/config/BBBPorchConfigDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:getvalueof id="applicationId" bean="Site.id" />
<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
<dsp:getvalueof bean="SessionBean.buyoffStartBrowsingSummaryVO" var="buyOffRegVo"/>
<dsp:getvalueof bean="SessionBean.buyoffStartBrowsingSummaryVO.registryId" var="browseRegId"/>
<dsp:getvalueof bean="SessionBean.currentZipcodeVO" var="currentZipCodeVo"/>
<dsp:getvalueof value="${currentZipCodeVo.minShipFee}" var="minShipFee"/>
<dsp:getvalueof value="${currentZipCodeVo.displayCutoffTime}" var="displayCutoffTime"/>
<dsp:getvalueof value="${currentZipCodeVo.displayGetByTime}" var="displayGetByTime"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
<jsp:useBean id="currentDate" class="java.util.Date"/>
<fmt:formatDate value="${currentDate}" var="currentDate" pattern="MM/dd/yyyy"/>

<dsp:droplet name="BBBPackNHoldDroplet">
	 	 <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
	 	 <dsp:oparam name="output">
	 	 	<dsp:getvalueof param="hasSingleCollegeItem" var="hasSingleCollegeItem"/>
          </dsp:oparam>
</dsp:droplet>
 <c:choose>
	<c:when test="${isInternationalCustomer}">
	   <c:set var="isInternationalUser" value="true"/>
	</c:when>
	<c:otherwise>
	<c:set var="isInternationalUser" value="false"/>
	</c:otherwise>
</c:choose>
	<c:set var="internationalShippingOn" scope="request"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>
<c:set var="deliveryDetailsUL">
<dsp:getvalueof var="commItem" param="commItem"/>
<dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus"/>
<dsp:getvalueof var="storeDetails" param="storeDetails"/>
<dsp:getvalueof var="skuBelowLine" value="${commItem.skuDetailVO.skuBelowLine}"/>
<dsp:getvalueof var="productId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
<dsp:getvalueof var="hasRegistryPorchServiceRemoved" param="hasRegistryPorchServiceRemoved"/>

<c:set var="gotPorchEligibleItem" value="${false}" />
<c:set var="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
<c:set var="commerceItemId" value="${commItem.BBBCommerceItem.id}"/>
 <c:set var="isBelowLineItem" value="${false}"/>
   <c:if test="${skuBelowLine and not empty commItem.BBBCommerceItem.registryId}">
	 <c:set var="isBelowLineItem" value="${true}"/>
    </c:if>
 <dsp:droplet name="BBBBeddingKitsAddrDroplet">
	 	  <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
	 	   <dsp:param name="isPackHold" value="${true}"/> 
	 	   <dsp:oparam name="beddingKit">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="true"/>
           </dsp:oparam>
		   <dsp:oparam name="weblinkOrder">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="weblinkOrder" value="true"/>
           </dsp:oparam>
           <dsp:oparam name="notBeddingKit">
		    <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="false"/>
           </dsp:oparam>
</dsp:droplet>
			<c:choose>
				<c:when test="${currentSiteId eq 'BedBathCanada'}">
					<fmt:parseDate value="${beddingShipAddrVO.shippingEndDate}" var="endDate" pattern="dd/MM/yyyy" />
					<fmt:formatDate value="${endDate}" var="endDate" type="date" pattern="MM/dd/yyyy"/>
				</c:when>
				<c:otherwise>
					<fmt:formatDate value="${beddingShipAddrVO.shippingEndDate}" var="endDate" pattern="MM/dd/yyyy"/>
				</c:otherwise>
			</c:choose>
			
	<dsp:droplet name="CurrencyFormatter">
		<dsp:param name="currency" value="${minShipFee}"/>
		<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="formattedShipFee" param="formattedCurrency" />
			<c:if test="${fn:contains(formatCurrency, '($0.00)')}">
				<c:set var="formattedShipFee" value="$0.00"></c:set>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
<c:set var="sddSignatureThreshold"><bbbc:config key="sddSignatureThreshold" configName="SameDayDeliveryKeys" language="${pageContext.request.locale.language}"/></c:set>			
<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap" scope="request"/>
<c:set target="${placeHolderMapShipFee}" property="minShipFee" value="${formattedShipFee}"/>
<c:set target="${placeHolderMapShipFee}" property="displayCutOffTime" value="${displayCutoffTime}"/>
<c:set target="${placeHolderMapShipFee}" property="displayGetByTime" value="${displayGetByTime}"/>
<c:set target="${placeHolderMapShipFee}" property="signatureThreshold" value="${sddSignatureThreshold}"/>
 
<c:if test="${empty commItem.BBBCommerceItem.storeId}">
  <ul class="prodDeliveryInfo squareBulattedList">
      <c:if test="${not empty commItem.skuDetailVO.freeShipMethods  && not isInternationalCustomer}">
          <dsp:droplet name="ForEach">
              <dsp:param name="array" value="${commItem.skuDetailVO.freeShipMethods}" />
              <dsp:oparam name="outputStart">
                  <li><bbbl:label key="lbl_cartdetail_free" language="<c:out  param='${language}'/>"/>
              </dsp:oparam>
              <dsp:oparam name="output">
                  <dsp:getvalueof var="index" param="index" />
                  <dsp:getvalueof var="count" param="count" />
                  <dsp:getvalueof var="size" param="size" />
                  <bbbl:label key="lbl_free_shipping_${commItem.skuDetailVO.freeShipMethods[index].shipMethodDescription}" language="<c:out  param='${language}'/>"/>
                  <c:if test="${count lt size}" >,</c:if>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                  <bbbl:label key="lbl_cartdetail_shipping" language="<c:out  param='${language}'/>"/></li>
              </dsp:oparam>
          </dsp:droplet>
      </c:if>

      <c:if test="${commItem.skuDetailVO.shippingSurcharge gt 0.0}">
          <li><bbbl:label key="lbl_cartdetail_surchargeapplies" language="<c:out param='${language}'/>"/></li>
      </c:if>

      <c:choose>
          <c:when test="${commItem.stockAvailability eq 0 || commItem.stockAvailability eq 2}">
              <li><bbbl:label key="lbl_cartdetail_instockandreadytouse." language="<c:out param='${language}'/>"/></li>
          </c:when>
          <c:otherwise>
              <c:set var="isStockAvailability" value="no" scope="request"/>
              <li><bbbl:label key="lbl_cartdetail_outofstock" language="<c:out param='${language}'/>"/></li>
          </c:otherwise>
      </c:choose>

      <dsp:droplet name="IsProductSKUShippingDroplet">
          <dsp:param name="siteId" value="${applicationId}"/>
          <dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
          <dsp:param name="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
          <dsp:oparam name="true">
              <dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
              <c:forEach var="item" items="${restrictedAttributes}">
                  <li>
                      <c:choose>
                          <c:when test="${null ne item.actionURL}">
                              <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
                          </c:when>
                          <c:otherwise>
                              ${item.attributeDescrip}
                          </c:otherwise>
                      </c:choose>
                  </li>
              </c:forEach>
          </dsp:oparam>
          <dsp:oparam name="false">
          </dsp:oparam>
          <%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
	       <dsp:oparam name="vdcMsg">
			<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC" />
		  </dsp:oparam>
		  <%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
      </dsp:droplet>

      <c:if test="${currentSiteId == BedBathCanadaSite}">
          <dsp:droplet name="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet">
              <dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}" />
              <dsp:oparam name="true">
                  <li><bbbl:label key="lbl_cartdetail_elegibleforecofee" language="<c:out param='${language}'/>"/></li>
              </dsp:oparam>
          </dsp:droplet>
      </c:if>
        <%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
        <c:set var="vdcOffsetFlag">
			<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
		</c:set>
      <c:if test="${commItem.skuDetailVO.vdcSku and not commItem.skuDetailVO.ltlItem}">
			<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
				<dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
				</dsp:oparam>
			</dsp:droplet>
				<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
				<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="" />
				<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${commItem.BBBCommerceItem.catalogRefId}" />
				<jsp:useBean id="placeHolderMapVdcCarts" class="java.util.HashMap" scope="request" />
				<c:set target="${placeHolderMapVdcCarts}" property="vdcDelTime" value="${vdcDelTime}" />
				<c:if test="${not commItem.skuDetailVO.customizationOffered }">
				<li>
				 	<bbbt:label key="lbl_vdc_del_time_cart_msg" placeHolderMap="${placeHolderMapVdcCarts}"	language="${pageContext.request.locale.language}" />
				 	<bbbl:label key="lbl_vdc_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/>
				 </li>
				 </c:if>
          <%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
           <c:if test="${vdcOffsetFlag}">
			<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
			<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
			<li>
			 	<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}"	language="${pageContext.request.locale.language}" />
			 	<bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/>
			 </li>
	 </c:if>
      </c:if>
       <%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>

      <c:if test="${commItem.skuDetailVO.shippingRestricted  && not isInternationalCustomer}">
		  <li>
			<a class="shippingRestrictionsApplied" href="/store/cart/static/shipping_restrictions_applied.jsp" data-skuId="${commItem.skuDetailVO.skuId}" title="<bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>">
			<bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>
			</a>
		  </li>
      </c:if>
			 <c:if test="${endDate gt currentDate && hasSingleCollegeItem eq true}"> 
						<c:choose>
                          <c:when test="${commItem.BBBCommerceItem.bts && not commItem.skuDetailVO.vdcSku}">
                            <li><bbbl:label key="lbl_cartdetail_elegibleforpackandhold" language="<c:out param='${language}'/>"/></li>
                          </c:when>
                          <c:otherwise>
                             <li><bbbl:label key="lbl_cartdetail_not_elegibleforpackandhold" language="<c:out param='${language}'/>"/></li>
                          </c:otherwise>
                      </c:choose>
			</c:if>   
  </ul>
  
		  
</c:if>
<c:if test="${not empty commItem.BBBCommerceItem.storeId}">
  <ul class="prodDeliveryInfo squareBulattedList">
   <c:if test="${endDate gt currentDate && hasSingleCollegeItem eq true}"> 
						<c:choose>
                          <c:when test="${commItem.BBBCommerceItem.bts && not commItem.skuDetailVO.vdcSku}">
                            <li><bbbl:label key="lbl_cartdetail_elegibleforpackandhold" language="<c:out param='${language}'/>"/></li>
                          </c:when>
                          <c:otherwise>
                             <li><bbbl:label key="lbl_cartdetail_not_elegibleforpackandhold" language="<c:out param='${language}'/>"/></li>
                          </c:otherwise>
                      </c:choose>
	</c:if>   
  </ul>

</c:if>
</c:set>

<c:choose>
<c:when test="${defaultCountry eq 'US' || defaultCountry eq 'Canada'}">
  <div class="deliveryBy">
  
      <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
          <dsp:param name="value" value="${commItem.BBBCommerceItem.storeId}"/>
          <dsp:oparam name="true">
		    <c:set var="commItemStoreId" value="" />
              <c:if test="${MapQuestOn}">
	      <c:if test="${(not commItem.skuDetailVO.vdcSku) and (not commItem.skuDetailVO.ltlItem) and (empty commItem.BBBCommerceItem.referenceNumber)}">

              	<c:choose>
              		<c:when test="${(not commItem.skuDetailVO.bopusAllowed) and (not commItem.skuDetailVO.bopusExcludedForMinimalSku)}">
              			<div class="radioItem input clearfix noPad noBorder">
		                      <div class="radio">
		                          <input type="radio" role="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" <c:if test="${isInternationalCustomer}">disabled="disabled"</c:if> aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
		                      </div>
		                      <div class="label">
		                       <label id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}" class="lnkShippingMethodChange noline changeStore<c:if test="${isInternationalCustomer}">disableText</c:if>"><span class="txtOffScreen" aria-hidden='false'><bbbl:label key="lbl_reserve_online_radio_button" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.displayName}&nbsp.</span><strong class="upperCase <c:if test="${isInternationalCustomer}">disableText</c:if>"  aria-hidden='true'><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
									<span class="chkStore <c:if test="${isInternationalCustomer}">disableText</c:if>"  aria-hidden='true'><bbbl:label key="lbl_check_store_avail" language="<c:out param='${language}'/>"/></span>
									<c:if test="${not isInternationalCustomer}"><span class='hidden' aria-hidden='false'><bbbl:label key="lbl_radio_change_store_modal" language="<c:out param='${language}'/>"/></span></c:if></label>

								<c:if test="${internationalShippingOn}">
									<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">
										<div class="error">
											<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
											</div>
									</c:if>
								</c:if>
		                      </div>
		                  </div>
              		</c:when>
              		<%-- <c:when test="${isInternationalCustomer}">
						<div class="radioItem input clearfix noPad noBorder">
		                      <div class="radio">
		                          <input type="radio" role="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" disabled="disabled" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" aria-checked="false" />
		                      </div>
		                      <div class="label">
		                        <label id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}"><strong class="upperCase disableText"><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
								<span class="chkStore disableText"><bbbl:label key="lbl_check_store_avail" language="<c:out param='${language}'/>"/></span></label>
								<c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">

							<div class="error">
							<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
							</div>
							</c:if>
								</c:if>
							 </div>
		                </div>
					</c:when> --%>
              		<c:otherwise>
              			<div class="radioItem input clearfix noPad noBorder">
		                      <div class="radio">
		                          <input type="radio" role="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" disabled="disabled" aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
		                      </div>
		                      <div class="label">

		                          <label id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}"><strong class="upperCase disableText"><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
									<span class="chkStore disableText"><bbbl:label key="lbl_check_store_avail" language="<c:out param='${language}'/>"/></span></label>
									
		                      </div>
		                  </div>
              		</c:otherwise>
              	</c:choose>
              </c:if>
              </c:if>
				<c:if test="${(not isBelowLineItem) || (isBelowLineItem and commItem.skuDetailVO.ltlItem and (not empty browseRegId))}">
              	<div class="radioItem input clearfix noPad noBorder">
                  <div class="radio">
                      <input type="radio" role="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" checked="checked" class="curShippingMethod" aria-checked="true" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
                  </div>
                  <div class="label">
                      <label id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}" class="upperCase noline selShippingMethod">
                         <span class="txtOffScreen">ship ${commItem.skuDetailVO.displayName}&nbsp to a specified location </span><strong><bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/></strong></label>
                          <c:out value="${deliveryDetailsUL}" escapeXml="false" />
							<c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForIntShip}">
							<div class="error">
							<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_GENERIC_ERROR" language ="${pageContext.request.locale.language}"/>
							</div>
							</c:if>
								</c:if>
                  </div>
				 
              	</div>
				
	            <c:if test="${not empty storeDetails && (favStoreStockStatus[commerceItemId] == 0 || favStoreStockStatus[commerceItemId] == 2)}">
					<dsp:include page="fav_store_result_frag.jsp">
						<dsp:param name="favStoreStockStatus" value="${favStoreStockStatus}" />
						<dsp:param name="commerceItemId" value="${commerceItemId}" />
					</dsp:include>
				</c:if>
			  </c:if>
			  
			 <div class="sddaui">
	   			<ul class="squareBulattedList prodDeliveryInfo">
	  				<c:choose>
	  					<c:when test="${commItem.sddAvailabilityStatus eq 'available'}">
							<li class="sddli"><bbbl:label key="lbl_sdd_on_cart_available" language="<c:out param='${language}'/>"/>
								<a class="sddQuestion" href="javascript:;" >2</a>
								<div class="hidden showSDDToolTipCart" id="tooltipInfo" aria-hidden="true"><bbbt:textArea key="txt_sdd_cart_available" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}" /></div>
								<div><bbbt:textArea key="txt_sdd_cart_available_desc" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}" /></div>
							</li>
						</c:when>
						<c:when test="${commItem.sddAvailabilityStatus eq 'unavailable'}">
							<li class="sddli"><bbbl:label key="lbl_sdd_on_cart_unavailable" language="<c:out param='${language}'/>"/>
								<a class="sddQuestion" href="javascript:;">2</a>
								<div class="hidden showSDDToolTipCart" id="tooltipInfo" aria-hidden="true"><div class="unavailable"><bbbt:textArea key="txt_sdd_cart_unavailable" language="${pageContext.request.locale.language}" /></div></div>
							</li>
						</c:when>
						<c:when test="${commItem.sddAvailabilityStatus eq 'ineligible'}">		
							<li class="sddli"><bbbl:label key="lbl_sdd_on_cart_unavailable" language="<c:out param='${language}'/>"/>
								<a class="sddQuestion" href="javascript:;">2</a>
								<div class="hidden showSDDToolTipCart" id="tooltipInfo" aria-hidden="true"><div class="unavailable"><bbbt:textArea key="txt_sdd_cart_ineligible" language="${pageContext.request.locale.language}" /></div></div>
							</li>		
						</c:when>
				   </c:choose>
				</ul>
			</div>

              <c:if test="${not empty browseRegId and empty commItem.BBBCommerceItem.registryId and commItem.BBBCommerceItem.buyOffAssociatedItem eq false}">
								<div> <div class = 'flatform'>
								 <input type ='checkbox' class="btnAjaxSubmitCart" data-ajax-frmid="frmCartAssociateRegistryContext" data-id="${commItem.BBBCommerceItem.id}"  name ="associateCheck" onclick="javascript:buyOutsideRegistry('${productId}','${commItem.BBBCommerceItem.catalogRefId}','${browseRegId}','${buyOffRegVo.registryType.registryTypeDesc}')";><div class = 'associateText'><span id="regHead"><bbbl:label key="lbl_buyoff_cart_unchecked" language="<c:out param='${language}'/>"/></span> &nbsp;<strong>${buyOffRegVo.primaryRegistrantFirstName}<c:if test="${not empty buyOffRegVo.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${buyOffRegVo.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong> <br> ${buyOffRegVo.registryType.registryTypeDesc} <bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></div>
								</div></div>
							</c:if>
							
							
          </dsp:oparam>
          <dsp:oparam name="false">
		  
             <c:set var="commItemStoreId" value="${commItem.BBBCommerceItem.storeId}" />
            <div class="radioItem input clearfix noPad noBorder">
                  <div class="radio">
                      <input type="radio" role="radio" value="1" checked="checked" class="curShippingMethod" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
                  </div>
                  <div class="label">
                 <div class="label <c:if test="${isInternationalCustomer}">disableText</c:if>">
                 	 <c:if test="${!isInternationalCustomer}">
                      <label id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}">
                          <strong class="upperCase"><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
                          <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
                              <dsp:param name="storeId" value="${commItem.BBBCommerceItem.storeId}" />
                              <dsp:oparam name="output">
                                  <span class="storeAddress <c:if test="${isInternationalCustomer}">disableText</c:if>">
                                      <c:choose>
                                          <c:when test="${commItem.stockAvailability eq 0 || commItem.stockAvailability eq 2}">
                                              <span><bbbl:label key="lbl_cartdetail_availableforpickupinstore." language="<c:out param='${language}'/>"/></span>
                                          </c:when>
                                          <c:otherwise>
                                              <c:set var="isStockAvailability" value="no" scope="request"/>
                                              <span><bbbl:label key="lbl_cartdetail_outofstock" language="<c:out param='${language}'/>"/></span>
                                          </c:otherwise>
                                      </c:choose>
                                      <span class="storeName bold"><dsp:valueof param="StoreDetails.storeName"/> <a href="#" class="changeStore <c:if test="${isInternationalCustomer}">disableText</c:if>"><bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/></a></span>
                                      <span><dsp:valueof param="StoreDetails.address"/></span>
                                      <span><dsp:valueof param="StoreDetails.city"/>,&nbsp;<dsp:valueof param="StoreDetails.state"/>,&nbsp;<dsp:valueof param="StoreDetails.postalCode"/></span>
                                      <span><dsp:valueof param="StoreDetails.storePhone"/></span>
                                  </span>
                              </dsp:oparam>
                          </dsp:droplet>
                      </label>
                        </c:if>
						
						
					  <c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">
							<div class="error">
							<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
							</div>
							</c:if>
								</c:if>
                  </div>
              </div></div>
              <c:if test="${(not isBelowLineItem) || (isBelowLineItem and commItem.skuDetailVO.ltlItem and (not empty browseRegId))}">
              <div class="radioItem input clearfix noPad noBorder">
                  <div class="radio">
                       <input type="radio" role="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" aria-checked="false" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
                  </div>
                  <div class="label">
                      <label id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}" class="lnkShippingMethodChange upperCase noline btnAjaxSubmitCart" data-ajax-frmID="frmCartShipOnline"><strong><bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/></strong></label>
					  <c:out value="${deliveryDetailsUL}" escapeXml="false" />
					  <c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForIntShip}">
							<div class="error">
							<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_GENERIC_ERROR" language ="${pageContext.request.locale.language}"/>
							</div>
							</c:if>
								</c:if>
                  </div>
                  <c:if test="${not empty storeDetails && (favStoreStockStatus[commerceItemId] == 0 || favStoreStockStatus[commerceItemId] == 2)}">
					<dsp:include page="fav_store_result_frag.jsp">
						<dsp:param name="favStoreStockStatus" value="${favStoreStockStatus}" />
						<dsp:param name="commerceItemId" value="${commerceItemId}" />
					</dsp:include>
				  </c:if>
              </div>
              </c:if>
              <div class="sddaui">
	              <ul class="squareBulattedList prodDeliveryInfo">
	                <c:choose>
	                  <c:when test="${commItem.sddAvailabilityStatus eq 'available'}">
	                    <li class="sddli">
	                      <bbbl:label key="lbl_sdd_on_cart_available" language="<c:out param='${language}'/>" /> 
	                      <a class="sddQuestion" href="javascript:;">2</a>
	                      <div class="hidden showSDDToolTipCart" id="tooltipInfo" aria-hidden="true"><bbbt:textArea key="txt_sdd_cart_available" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}" /></div>
	                      <div><bbbt:textArea key="txt_sdd_cart_available_desc" placeHolderMap="${placeHolderMapShipFee}" language="${pageContext.request.locale.language}" /></div>
	                    </li>
	                  </c:when>
	                  <c:when test="${commItem.sddAvailabilityStatus eq 'unavailable'}">
	                    <li class="sddli">
	                      <bbbl:label key="lbl_sdd_on_cart_unavailable" language="<c:out param='${language}'/>" />
	                      <a class="sddQuestion" href="javascript:;">2</a>
	                      <div class="hidden showSDDToolTipCart" id="tooltipInfo" aria-hidden="true">
	                        <div class="unavailable"><bbbt:textArea key="txt_sdd_cart_unavailable" language="${pageContext.request.locale.language}" /></div>
	                      </div>
	                    </li>
	                  </c:when>
	                  <c:when test="${commItem.sddAvailabilityStatus eq 'ineligible'}">
	                    <li class="sddli">
	                      <bbbl:label key="lbl_sdd_on_cart_unavailable" language="<c:out param='${language}'/>" />
	                      <a class="sddQuestion" href="javascript:;">2</a>
	                      <div class="hidden showSDDToolTipCart" aria-hidden="true">
	                        <div class="unavailable"><bbbt:textArea key="txt_sdd_cart_ineligible" language="${pageContext.request.locale.language}" /></div>
	                      </div>
	                    </li>
	                  </c:when>
	                </c:choose>
	              </ul>
	            </div>

              <c:if test="${not empty browseRegId and empty commItem.BBBCommerceItem.registryId and commItem.BBBCommerceItem.buyOffAssociatedItem eq false}">
								<div> <div class = 'flatform'>
								 <input type ='checkbox' class="btnAjaxSubmitCart" data-ajax-frmid="frmCartAssociateRegistryContext" data-id="${commItem.BBBCommerceItem.id}"  name ="associateCheck" onclick="javascript:buyOutsideRegistry('${productId}','${commItem.BBBCommerceItem.catalogRefId}','${browseRegId}','${buyOffRegVo.registryType.registryTypeDesc}')";><div class = 'associateText'><span id="regHead"><bbbl:label key="lbl_buyoff_cart_unchecked" language="<c:out param='${language}'/>"/></span> &nbsp;<strong>${buyOffRegVo.primaryRegistrantFirstName}<c:if test="${not empty buyOffRegVo.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${buyOffRegVo.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong> <br> ${buyOffRegVo.registryType.registryTypeDesc} <bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></div>
								</div></div>
							</c:if>
          </dsp:oparam>
      </dsp:droplet>

      <c:if test="${hasRegistryPorchServiceRemoved}">
	  	<p class="red noMar ">
	  		<bbbl:label key="lbl_bbby_porch_cart_add_to_registry" language="<c:out param='${language}'/>"/>			
		</p>
	  </c:if>
											
	<dsp:droplet name="com/bbb/config/BBBPorchConfigDroplet">
		<dsp:param name="siteId" value="${currentSiteId}"/>
			<dsp:param name="pageName" value="cartPage"/>
			<dsp:param name="commerceItem" value="${commItem.BBBCommerceItem}"/>
			<dsp:oparam name="CartPorch">
			<dsp:getvalueof var="porchServiceTypeCode" param="porchServiceTypeCodes[0]"/>
			<dsp:droplet name="/atg/dynamo/droplet/IsNull">
			<dsp:param name="value" value="${porchServiceTypeCode}"/>
			<dsp:oparam name="false">												 
				<c:set var="gotPorchEligibleItem" value="${true}" />
			</dsp:oparam>
			</dsp:droplet>
			</dsp:oparam>
	</dsp:droplet>
								
												
	<c:if test="${gotPorchEligibleItem and commItem.BBBCommerceItem.buyOffAssociatedItem  and (hasRegistryPorchServiceRemoved ne null and !hasRegistryPorchServiceRemoved)}">
	  	<p class="red noMar ">
	  		<bbbl:label key="lbl_bbby_porch_cart_add_to_buyoff_registry" language="<c:out param='${language}'/>"/>			
		</p>
	  </c:if>
	  
  </div>
</c:when>
<c:otherwise>
  <c:out value="${deliveryDetailsUL}" escapeXml="false" />
  <c:set var="commItemStoreId" value="" />
</c:otherwise>
</c:choose>
<input type="submit" name="btnPickUpInStore" id="btnPickUpInStore${commItem.BBBCommerceItem.id}" class="hidden" value="PickUpInStore" aria-pressed="false" aria-labelledby="btnPickUpInStore${commItem.BBBCommerceItem.id}" role="button" />
</dsp:page>
