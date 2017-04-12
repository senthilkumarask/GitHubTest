<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean var="CommonConfiguration" bean="/com/bbb/utils/CommonConfiguration"/>
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean	bean="/com/bbb/tag/droplet/ReferralInfoDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderOmnitureDroplet" />
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
	<dsp:importbean bean="/atg/commerce/order/droplet/WCRegistryInfoDroplet" />
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
    <dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
  	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
	<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<c:set var="isCreditCard" value="${false}"/>
	
	<bbb:pageContainer index="false" follow="false" >
		<jsp:attribute name="headerRenderer">
	      <dsp:include page="/checkout/checkout_confirmation_header.jsp" flush="true">
	      	<dsp:param name="pageId" value="8"/>
	      </dsp:include>
	    </jsp:attribute>
		<jsp:attribute name="pageWrapper">billing chekoutReview chekoutConfirm useCertonaJs</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="PageType">CheckoutConfirmation</jsp:attribute>
		<dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/>
		<jsp:body>
    	<c:choose>
			<c:when test="${babyCA}">
				<c:set var="eDialogUrl"><bbbc:config key="eDialog_url_babyCanada" configName="ThirdPartyURLs" /></c:set>
        		<c:set var="eDialogCookie"><bbbc:config key="eDialog_cookie_babyCanada" configName="ContentCatalogKeys" /></c:set>
    			<c:set var="eDialogCookie2"><bbbc:config key="eDialog_cookie2_babyCanada" configName="ContentCatalogKeys" /></c:set>
			</c:when>
			<c:otherwise>
    			<c:set var="eDialogUrl"><bbbc:config key="eDialog_url" configName="ThirdPartyURLs" /></c:set>
        		<c:set var="eDialogCookie"><bbbc:config key="eDialog_cookie" configName="ContentCatalogKeys" /></c:set>
    			<c:set var="eDialogCookie2"><bbbc:config key="eDialog_cookie2" configName="ContentCatalogKeys" /></c:set>
    		</c:otherwise>
		</c:choose>

		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="orderCode" bean="ShoppingCart.last.id"/>
		<dsp:getvalueof var="orderDate" bean="ShoppingCart.last.submittedDate"/>
		<dsp:getvalueof var="onlineOrderNumber" bean="ShoppingCart.last.onlineOrderNumber"/>
		<dsp:getvalueof var="bopusOrderNumber" bean="ShoppingCart.last.bopusOrderNumber"/>
		<dsp:getvalueof var="currencyId" value="${pageContext.response.locale}"/>
		<dsp:getvalueof var="orderTax" bean="ShoppingCart.last.priceInfo.tax"/>
		<dsp:getvalueof var="orderShippingAmount" bean="ShoppingCart.last.priceInfo.shipping"/>
		<dsp:getvalueof var="orderAmount" bean="ShoppingCart.last.priceInfo.total"/>
		<dsp:getvalueof var="order" bean="ShoppingCart.last" />
		<dsp:getvalueof var="cmState" bean="ShoppingCart.last.billingAddress.state"/>
		<dsp:getvalueof var="city" bean="ShoppingCart.last.billingAddress.city"/>
		<dsp:getvalueof var="country" bean="ShoppingCart.last.billingAddress.country"/>
		<dsp:getvalueof var="profileID" bean="/atg/userprofiling/Profile.id"/>
		<dsp:getvalueof var="itemCount" bean="ShoppingCart.last.commerceItemCount"/>

		<%--RM-defect 29278 Displaying order id for Bopus item in CMPR changes start --%>
		<c:choose>
			<c:when test="${not empty onlineOrderNumber}">
		 		<c:set var="customerFacingOrderId" value="${fn:substringAfter(onlineOrderNumber, 'BBB')}"/>
			</c:when>
		 	<c:otherwise>
				<c:set var="customerFacingOrderId" value="${fn:substringAfter(bopusOrderNumber, 'OLP')}"/>
		 	</c:otherwise>
		</c:choose>	 
		<%--RM-defect 29278 Displaying order id for Bopus item in CMPR changes end --%>
		
		  <%--Added error code when there is tax failure due to cybersource connectivity issue --%>
		  <c:if test="${not empty order.specialInstructions['CYBERSOURCE_TAX_FAILURE']}">
		   <div id="cybersourceTaxFailure" class="hidden">
		    CYBERSOURCE_TAX_FAILURE = error_cybersource_1002
		   </div>
		  </c:if>

		<c:choose>
				<c:when test="${empty onlineOrderNumber}">
				<c:set var="genOrderCode" value="${bopusOrderNumber}" />
				<c:set var="bopusOrder" value="true" />
				</c:when>
				<c:otherwise>
				<c:set var="genOrderCode" value="${onlineOrderNumber}" />
				<c:set var="bopusOrder" value="false" />
				</c:otherwise>
		</c:choose>
			<dsp:droplet name="/atg/commerce/order/droplet/BBBOrderInfoDroplet">
			<dsp:param name="profile" bean="Profile"/>
	        <dsp:param name="order" bean="ShoppingCart.last" />
	        <dsp:oparam name="output">
	        	<dsp:getvalueof var="storeId" param="storeId"/>
	        	<dsp:getvalueof var="PromoCode" param="PromoCode"/>
				<dsp:getvalueof var="PromoAmount" param="PromoAmount"/>
				<dsp:getvalueof var="itemSkuIds" param="itemSkuIds"/>
				<dsp:getvalueof var="itemAmts" param="itemAmts"/>
				<dsp:getvalueof var="itemSkuNames" param="itemSkuNames"/>
				<dsp:getvalueof var="itemIds" param="itemIds"/>
				<dsp:getvalueof var="itemSkuNames" param="itemSkuNames"/>
				<dsp:getvalueof var="itemQuantities" param="itemQuantities"/>
				<dsp:getvalueof var="cItemIds" param="cItemIds"/>
				<dsp:getvalueof var="wcItemUrl" param="wcItemUrl"/>
				<c:if test="${TagManOn}">
					<dsp:include page="/tagman/frag/checkoutConfirmation_frag.jsp">
			        	<dsp:param name="PromoCode" value="${PromoCode}"/>
			        	<dsp:param name="PromoAmount" value="${PromoAmount}"/>
			        	<dsp:param name="itemSkuIds" value="${itemSkuIds}"/>
			        	<dsp:param name="itemAmts" value="${itemAmts}"/>
			        	<dsp:param name="itemSkuNames" value="${itemSkuNames}"/>
			        	<dsp:param name="itemIds" value="${itemIds}"/>
			        	<dsp:param name="itemSkuNames" value="${itemSkuNames}"/>
			        	<dsp:param name="itemQuantities" value="${itemQuantities}"/>
			        	<dsp:param name="cItemIds" value="${cItemIds}"/>
			        	<dsp:param name="itemIds" value="${itemIds}"/>
			        	<dsp:param name="wcItemUrl" value="${wcItemUrl}"/>
		        	</dsp:include>
				</c:if>
		        <dsp:getvalueof var="resxEventType" param="resxEventType"/>
		        <dsp:getvalueof var="itemQtys" param="itemQtys"/>
		        <dsp:getvalueof var="itemprices" param="itemprices"/>
		        <dsp:getvalueof var="itemAmounts" param="itemAmounts"/>
		        <dsp:getvalueof var="bpItemUrl" param="bpItemUrl"/>
		        <dsp:getvalueof var="rkgItemUrl" param="rkgItemUrl"/>
		        <dsp:getvalueof var="rkgComparisonItemUrl" param="rkgComparisonItemUrl"/>
		        <dsp:getvalueof var="grandTotal" param="grandTotal"/>
		        <dsp:getvalueof var="orderList" param="orderList" scope="request" vartype="java.util.List"/>
				<dsp:setvalue param="wcItems" value=""/>
				<dsp:setvalue param="bpItems" value=""/>
				<dsp:setvalue param="cjItems" value=""/>
				<c:set var="comma" value="" scope="page" />
				<c:set var="ampersand" value="" scope="page" />

				<dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
				<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
				<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
			<%--DoubleClick Floodlight START --%>
			<dsp:droplet name="BBBOrderRKGInfo">
	        <dsp:param name="order" bean="ShoppingCart.last" />
	        <dsp:oparam name="output">
				<dsp:getvalueof var="rkgProductNames" param="rkgProductNames"/>
				<dsp:getvalueof var="rkgProductIds" param="rkgProductIds"/>
				<dsp:getvalueof var="rkgProductCount" param="rkgProductCount"/>
				<dsp:getvalueof var="rkgPromotions" param="rkgPromotions"/>
				<dsp:getvalueof var="rkgProdCatIdL1" param="rkgProdCatIdL1"/>
				<dsp:getvalueof var="rkgProdCatNameL1" param="rkgProdCatNameL1"/>
				<dsp:getvalueof var="rkgProdCatNameL2" param="rkgProdCatNameL2"/>
				<dsp:getvalueof var="rkgProdCatNameL3" param="rkgProdCatNameL3"/>
				<c:if test="${empty rkgPromotions}">
				<c:set var="rkgPromotions" value="null" />
				</c:if>
				<c:if test="${empty rkgProdCatIdL1}">
				<c:set var="rkgProdCatIdL1" value="null" />
				</c:if>
				<c:if test="${empty rkgProdCatNameL1}">
				<c:set var="rkgProdCatNameL1" value="null" />
				</c:if>
				<c:if test="${empty rkgProdCatNameL2}">
				<c:set var="rkgProdCatNameL2" value="null" />
				</c:if>
				<c:if test="${empty rkgProdCatNameL3}">
				<c:set var="rkgProdCatNameL3" value="null" />
				</c:if>
	    <c:if test="${DoubleClickOn}">
	    <dsp:getvalueof var="state" bean="ShoppingCart.last.billingAddress.state"/>
	    <dsp:getvalueof var="zip" bean="ShoppingCart.last.billingAddress.postalCode"/>
	    <dsp:getvalueof var="country" bean="ShoppingCart.last.billingAddress.country"/>
    		  <c:if test="${(currentSiteId eq BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_confirm_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_3_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_confirm_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_3_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
			<c:if test="${(currentSiteId eq BedBathCanadaSite)}">
				  <c:set var="cat"><bbbc:config key="cat_confirm_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="type"><bbbc:config key="type_3_bedbathcanada" configName="RKGKeys" /></c:set>
			</c:if> 
		       <c:if test="${not empty cat}">
		       <c:set var="total" value="${fn:replace(grandTotal,',','')}" />
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=${rkgProductCount};cost=${total};u7=${state};u8=${country};u11=${rkgProdCatNameL1};u12=${rkgProdCatNameL2};u13=${rkgProdCatNameL3};u10=${rkgProdCatIdL1};u9=${rkgPromotions};u1=${genOrderCode};u2=${rkgProductCount};u3=${total};u4=${rkgProductIds};u5=${rkgProductNames};u6=${zip};ord=${genOrderCode}?"/>
                        <dsp:param name="ord" value="true"/>
			 		</dsp:include>
		 		</c:if>
		 		</c:if>
		 	</dsp:oparam>
		 </dsp:droplet>
		 		 <%--DoubleClick Floodlight END --%>
				<c:set var="userDefined1" scope="request">
					<dsp:valueof value="${preTaxAmout}" number="0.00"/>
				</c:set>
				<dsp:getvalueof var="userDefined2" bean="ShoppingCart.last.priceInfo.discountAmount" scope="request"/>
				<c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>

				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="wcItemUrl" />
					<dsp:param name="elementName" value="wcUrlItem" />
					<dsp:oparam name="outputStart">
	  					<dsp:getvalueof var="sizeWCItems" param="size" />

	  				</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="countWCItems" param="count" />
						<c:if test="${sizeWCItems != countWCItems}">
							<c:set var="comma" value="," scope="page" />
						</c:if>
						<c:if test="${sizeWCItems eq countWCItems}">
							<c:set var="comma" value="" scope="page" />
						</c:if>
						<dsp:getvalueof var="itemWC" param="wcUrlItem" idtype="java.lang.String" />
						<dsp:getvalueof var="wcItem" param="wcItems" idtype="java.lang.String" />
						<c:if test="${ ( not empty regId ) }">
							<dsp:setvalue param="wcItems" value="${wcItem}${itemWC}|reg:${regId}${comma}"/>
						</c:if>
						<c:if test="${ ( empty regId ) }">
						<dsp:setvalue param="wcItems" value="${wcItem}${itemWC}${comma}"/>
						</c:if>
						<dsp:getvalueof var="wcItemValue" param="wcItems"/>
					</dsp:oparam>
				</dsp:droplet>

				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="bpItemUrl" />
					<dsp:param name="elementName" value="bpUrlItem" />
					<dsp:oparam name="outputStart">
	  					<dsp:getvalueof var="sizeBPItems" param="size" />
	  				</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="countBPItems" param="count" />
						<c:if test="${sizeBPItems != countBPItems}">
							<c:set var="comma" value="," scope="page" />
						</c:if>
						<c:if test="${sizeBPItems eq countBPItems}">
							<c:set var="comma" value="" scope="page" />
						</c:if>
						<dsp:getvalueof var="itemBP" param="bpUrlItem" idtype="java.lang.String" />
						<dsp:getvalueof var="bpItem" param="bpItems" idtype="java.lang.String" />
						<c:if test="${ ( not empty regId ) }">
						<dsp:setvalue param="bpItems" value="${bpItem}${itemBP}|reg:${regId}${comma}"/>
						</c:if>
						<c:if test="${ ( empty regId ) }">
						<dsp:setvalue param="bpItems" value="${bpItem}${itemBP}${comma}"/>
						</c:if>
						<dsp:getvalueof var="bpItemValue" param="bpItems"/>
					</dsp:oparam>
				</dsp:droplet>



				<c:if test="${(RKGOn) && (bopusOrder eq false)}">
					<c:set var="rkgSaleUrl"><bbbc:config key="rkg_referral_sale_url" configName="ThirdPartyURLs" /></c:set>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="rkgItemUrl" />
						<dsp:param name="elementName" value="rkgUrlItem" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="itemRKG" param="rkgUrlItem" idtype="java.lang.String" />
							<dsp:getvalueof var="rkgRefUrl" value="${rkgSaleUrl}${itemRKG}"/>
							<img src="${rkgRefUrl}" height="1" width="1" alt="rkgRefUrl" />
						</dsp:oparam>
					</dsp:droplet>


					<%--RKG comparision shopping tag starts --%>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="rkgComparisonItemUrl" />
						<dsp:param name="elementName" value="rkgComparisonUrlItem" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="itemRKG" param="rkgComparisonUrlItem" idtype="java.lang.String" />
							<img src="/_assets/global/images/blank.gif" id="${itemRKG}" height="0" width="0" boarder="0" alt="Blank image" />
						</dsp:oparam>
					</dsp:droplet>
					<%--RKG comparision shopping tag ends --%>
				</c:if>
	     </dsp:oparam>
	    </dsp:droplet>	
	    <%-- CJ pixel code starts here --%>
	    <dsp:droplet name="/atg/commerce/order/droplet/CJOrderInfoDroplet">
			<dsp:param name="profile" bean="Profile"/>
	        <dsp:param name="order" bean="ShoppingCart.last" />
	        <dsp:oparam name="output">
	        	<dsp:getvalueof var="cjItemUrl" param="cjItemUrl"/>
		        <dsp:getvalueof var="cjBopusOnly" param="cjBopusOnly"/>
		        <dsp:getvalueof var="cjSkuIds" param="cjSkuIds"/>
		        <dsp:getvalueof var="cjSkuPrices" param="cjSkuPrices"/>
		        <dsp:getvalueof var="cjSkuQty" param="cjSkuQty"/>
		        <dsp:setvalue param="cjItems" value=""/>
		        <c:if test="${cjBopusOnly == 'false' }">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="cjItemUrl" />
					<dsp:param name="elementName" value="cjUrlItem" />
					<dsp:oparam name="outputStart">
	  					<dsp:getvalueof var="sizeCjItems" param="size" />
	  				</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="countCjItems" param="count" />
						<c:if test="${sizeCjItems != countCjItems}">
							<c:set var="ampersand" value="&" scope="page" />
						</c:if>
						<dsp:getvalueof var="itemCJ" param="cjUrlItem" idtype="java.lang.String" />
						<dsp:getvalueof var="cjItem" param="cjItems" idtype="java.lang.String" />
						<dsp:setvalue param="cjItems" value="${cjItem}${itemCJ}${ampersand}"/>
						<dsp:getvalueof var="cjItemValue" param="cjItems"/>
					</dsp:oparam>
				</dsp:droplet>
				<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/checkoutConfirmation_cj.jsp">
				<dsp:param name="cjSkuIds" value="${cjSkuIds}"/>
				<dsp:param name="cjSkuPrices" value="${cjSkuPrices}"/>
				<dsp:param name="cjSkuQty" value="${cjSkuQty}"/>
				</dsp:include>
				</c:if>
				</c:if>
	        </dsp:oparam>
	        </dsp:droplet>
	     <%-- CJ pixel code ends here --%>
		<dsp:droplet name="ReferralInfoDroplet">
			<dsp:param name="currentPage" value="${requestURI}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="refId" param="refId"/>
				<dsp:getvalueof var="todayDate" param="todayDate"/>
	     <c:if test="${not empty refId && (refId eq 'wc' || refId eq 'bp')}">
				<dsp:droplet name="WCRegistryInfoDroplet">
		        <dsp:param name="order" bean="ShoppingCart.last" />
		        <dsp:oparam name="empty">
		        </dsp:oparam>
		        <dsp:oparam name="output">
				<dsp:getvalueof var="pixelRegistryIds" param="registryIds" />
				</dsp:oparam>
				</dsp:droplet>
		</c:if>
				<c:if test="${not empty refId && refId eq 'wc' && WeddingchannelOn}">
					<dsp:getvalueof var="wcSaleUrl" param="wcSaleUrl" />
					<dsp:getvalueof var="wcRefUrl" value="${wcSaleUrl}&orderNumber=${genOrderCode}&orderDate=${todayDate}&salesTotal=${grandTotal}&retailerRegistryCodes=${pixelRegistryIds}" />
				</c:if>
				<c:if test="${ ( not empty refId ) && refId eq 'bp' && TheBumpsOn}">
					<dsp:getvalueof var="bpSaleUrl" param="bpSaleUrl" />
					<dsp:getvalueof var="bpRefUrl" value="${bpSaleUrl}&orderNumber=${genOrderCode}&orderDate=${todayDate}&salesTotal=${grandTotal}&retailerRegistryCodes=${pixelRegistryIds}" />
				</c:if>
				<c:if test="${ ( not empty refId ) && refId eq 'cj' && CommisionJunctionOn}">
					 <c:set var="currencyId">USD</c:set>
			       	 <c:choose>
			       	 <c:when test="${currentSiteId eq BuyBuyBabySite}">
			       		<c:set var="cj_cid"><bbbc:config key="cj_cid_baby" configName="ReferralControls" /></c:set>
		    	   		<c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_baby" configName="ReferralControls" /></c:set>
		       		 </c:when>
		       		 <c:when test="${currentSiteId eq BedBathCanadaSite}">
			       		<c:set var="cj_cid"><bbbc:config key="cj_cid_ca" configName="ReferralControls" /></c:set>
		    	   		<c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_ca" configName="ReferralControls" /></c:set>
		       			<c:set var="currencyId">CAD</c:set>
		       		 </c:when>
			       	 <c:otherwise>
			       		<c:set var="cj_cid"><bbbc:config key="cj_cid_us" configName="ReferralControls" /></c:set>
			       		<c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_us" configName="ReferralControls" /></c:set>
		    	   	 </c:otherwise>
		       		 </c:choose>
					<dsp:getvalueof var="cjSaleUrl" param="cjSaleUrl" />
					<dsp:getvalueof var="cjSaleParam" param="cjSaleParam" />
					<dsp:getvalueof var="cjRefUrl" value="${cjSaleUrl}CID=${cj_cid}&OID=${genOrderCode}&${cjItemValue}&CURRENCY=${currencyId}&TYPE=${cj_type}&METHOD=IMG" />
				</c:if>
				<dsp:getvalueof var="oracleResponsysFlag" param="oracleResponsysFlag" />
				<dsp:getvalueof var="ResponsysEnabled" param="ResponsysEnabled" />
				<dsp:getvalueof var="OracleResponsys_url" param="OracleResponsys_url" />
			</dsp:oparam>
		</dsp:droplet>
		<c:if test="${refId == 'cj' && (bopusOrder eq false)}">
			<dsp:include page="/_includes/commissionJunction.jsp">
			<dsp:param name="refUrl" value="${cjRefUrl}"/>
			</dsp:include>
		</c:if>

	
		<%-- edialog code starts --%>
		<c:if test="${eDialogON}">
            <script type="text/javascript">
                (function(){
                    var ordTot='${grandTotal}'.replace(/,/g,''),
                        ordId='${genOrderCode}',
                        eDUrl='${eDialogUrl}'.trim(),
                        eDC='${eDialogCookie}'.trim(),
                        eDC2='${eDialogCookie2}'.trim(),
                        sv='<c:choose><c:when test="${currentSiteId eq BedBathCanadaSite}">CAN</c:when><c:otherwise>US</c:otherwise></c:choose>',
                        s,f,e,img,
                        c=document.cookie;

                    if (eDUrl !== '' && eDC !== '' && eDC2 !== '') {
                        s=c.indexOf(eDC);
                        if(s==-1){s=c.indexOf(eDC2);}
                        if(s>-1){
                            c=c.substring(s+1);
                            c=c.substring(c.indexOf('=')+1);
                            e=c.indexOf(';');
                            f=c.substring(0,e==-1? c.length:e);
                            img='<img height="1" width="1" style="display:none;" alt="edialog conversion pixel" src="'+eDUrl+'CEDID='+f+'&m='+ordTot+'&pk='+ordId+'&siteVersion='+sv+'" />';
                            document.write(img);
                        }
                    }
                }());
            </script>
		</c:if>
		<%-- edialog code ends --%>
		
		<%-- oracle responsys code starts --%>		
		<c:if test="${ResponsysEnabled && oracleResponsysFlag}">
			<dsp:include page="/_includes/oracleResponsys.jsp">
			<dsp:param name="OracleResponsys_url" value="${OracleResponsys_url}"/>
			<dsp:param name="onlineOrderNumber" value="${genOrderCode}"/>
			<dsp:param name="orderAmount" value="${grandTotal}"/>
			<dsp:param name="profileID" value="${profileID}"/>
			<dsp:param name="currentSiteId" value="${currentSiteId}"/>
			</dsp:include>
		</c:if>
	    <%-- oracle responsys code ends --%>
	    
	   		<div class="container_12 clearfix" id="content">
				<div class="topContent clearfix">
					<div id="subHeader" class="grid_10 clearfix">
						<h2 class="section fl">add <bbbl:label key="lbl_spc_checkoutconfirmation_confirmation" language="${language}"/></h2>
                        <div class="clear"></div>
                        <div class="a marTop_10 clearfix">
                            <p class="noMar bold"><bbbl:label key="lbl_spc_checkoutconfirmation_orderconfirmationsuccessmsg" language="${language}"/></p>
                            <p class="noMar marTop_5 "><bbbl:label key="lbl_spc_checkoutconfirmation_confirmationemailsentto" language="${language}"/>
						<dsp:valueof bean="ShoppingCart.last.billingAddress.email"/></p>
                        </div>

                        <%-- this only needs to be shown if the user is not logged in/guest checkout --%>
					<div class="grid_12 expressProfile marTop_20 alpha omega">
						<dsp:droplet name="/atg/dynamo/droplet/Switch">
							<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
							<dsp:oparam name="true">
       							<dsp:form name="frmCheckoutExpressProfile" id="frmCheckoutExpressProfile" action="${contextPath}/account/frags/checkout_registration_json.jsp" method="post">								 	
								 	<dsp:input bean="ProfileFormHandler.registrationSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp"/>
									<dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp?email="/>   
									<dsp:getvalueof var="firstName" bean="ShoppingCart.last.billingAddress.firstName"/>
									<dsp:getvalueof var="lastName" bean="ShoppingCart.last.billingAddress.lastName"/>

									<input name="fname" id="firstName" type="hidden" value="${firstName}"  />
									<input name="lname" id="lastName" type="hidden" value="${lastName}"  />

                                    <h3 class="sectionHeading"><bbbl:label key="lbl_spc_checkoutconfirmation_createaccount" language="${language}"/></h3>
									<p class="noMar padBot"><bbbl:label key="lbl_spc_checkoutconfirmation_wecansavetheinformationyouenteredandcreateanaccount" language="${language}"/></p>
									<p class="noMar padBot"><bbbl:label key="lbl_spc_checkoutconfirmation_advantagesofcreatinganaccount" language="${language}"/></p>
									<div class="pageErrors"></div>
									<div class="formRow marTop_10">
                                        <div class="createAccountForm hidden">
                                            <div class="clearfix">
                                                <div class="input">
                                                    <div class="label width_3 posRel">
                                                        <label id="lblpassword" for="password"> <bbbl:label key="lbl_spc_checkoutconfirmation_password" language="${language}"/><span class="required">&nbsp;*</span></label>
                                                        <div class="showPassDiv clearfix">
                                                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassCheckoutConfirm" class="showPassword" id="showPassword" />
                                                            <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_spc_show_password" language="${pageContext.request.locale.language}"/></label>
                                                            <div class="clear"></div>
                                                        </div>
                                                    </div>
                                                    <div class="text width_3">
                                                         <dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassCheckoutConfirm" >
							                                <dsp:tagAttribute name="aria-required" value="true"/>
							                                <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
							                            </dsp:input>
                                                    </div>
                                                </div>
                                                <div class="input">
                                                    <div class="label width_3">
                                                        <label id="lblconfirmPassword" for="confirmPassword"> <bbbl:label key="lbl_spc_checkoutconfirmation_confirmpassword" language="${language}"/><span class="required">&nbsp;*</span></label>
                                                    </div>
                                                    <div class="text width_3">
                                                        <dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassCheckoutConfirm" >
							                                <dsp:tagAttribute name="aria-required" value="true"/>
							                                <dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
							                            </dsp:input>
                                                    </div>
                                                </div>
                                                <div class="formRow clearfix noMarBot">
                                                    <div class="optIn input">
                                                      <dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
                                                           <c:choose>
												                <c:when test="${currentSiteId eq BedBathCanadaSite}">
												                    <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false" iclass="fl">
												                    <dsp:tagAttribute name="aria-checked" value="false"/>
												                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
												                    </dsp:input>
												                </c:when>
												                <c:otherwise>
												                    <c:if test="${flag == 'yes'}">
												                        <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="true" value="true" iclass="fl">
												                        <dsp:tagAttribute name="aria-checked" value="true"/>
												                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
												                        </dsp:input>
												                    </c:if>
												                    <c:if test="${flag == 'no'}">
												                      <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false" iclass="fl">
												                      <dsp:tagAttribute name="aria-checked" value="false"/>
												                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
												                      </dsp:input>
												                    </c:if>
												                </c:otherwise>
												              </c:choose>
                                                        </dsp:getvalueof> 
                                                        
                                                      <label for="emailOptIn" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_spc_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
                                                     <%--  <p class="textLgray11 cf"><bbbt:textArea key="txtarea_profile_optinmessage" language="${pageContext.request.locale.language}"/></p> --%>
                                                    </div>
                                                    
                                                    <%-- Baby Canada --%>
	                                                    <c:if test="${currentSiteId == BedBathCanadaSite}">
		                                                    <div class="optIn input">
		                                                      <dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
		                                                          <c:choose>
													                <c:when test="${currentSiteId eq BedBathCanadaSite}">
													                    <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
													                    <dsp:tagAttribute name="aria-checked" value="false"/>
													                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
													                    </dsp:input>
													                </c:when>
													                <c:otherwise>
													                    <c:if test="${flag == 'yes'}">
													                        <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="true" value="true" iclass="fl">
													                        <dsp:tagAttribute name="aria-checked" value="true"/>
													                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
													                        </dsp:input>
													                    </c:if>
													                    <c:if test="${flag == 'no'}">
													                      <dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
													                      <dsp:tagAttribute name="aria-checked" value="false"/>
													                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
													                      </dsp:input>
													                    </c:if>
													                </c:otherwise>
													              </c:choose>
		                                                        </dsp:getvalueof> 
		                                                        
		                                                      <label for="emailOptIn_BabyCA" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_spc_email_optin_baby_canada" language="${pageContext.request.locale.language}"/></label>
		                                                    </div>
		                                                </c:if>
                                                    <%-- Baby Canada --%>
                                                </div>
                                             <c:if test="${shareProfileOn}">
                                                <c:if test="${currentSiteId ne BedBathCanadaSite}">
                                                    <div class="formRow shareAccount clearfix noMarBot">
                                                        <dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" iclass="fl" id="shareAccount" />
                                                        <label for="shareAccount" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_spc_profile_shareaccount" language="${pageContext.request.locale.language}"/></label>
                                                        <p class="textLgray11"><bbbt:textArea key="txt_spc_profile_shareSite_text" language="${pageContext.request.locale.language}"/></p>
                                                    </div>
                                                </c:if>
                                             </c:if>
											</div>
											
                                            <dsp:getvalueof var="order" bean="ShoppingCart.last" />
                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" bean="ShoppingCart.last.paymentGroups"/>
												<dsp:param name="elementName" value="paymentGroup" />
												<dsp:oparam name="output">
													<dsp:droplet name="Switch">
														<dsp:param name="value" param="paymentGroup.paymentMethod"/>
														<dsp:oparam name="creditCard">
															<c:set var="isCreditCard" value="${true}" />
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>

                                            <c:if test="${internationalCCFlag ne 'true' && isCreditCard}">
                                            	<div class="formRow shareAccount clearfix">
                                            		<dsp:input bean="ProfileFormHandler.saveCreditCardInfoToProfile" type="checkbox" iclass="fl" id="saveCreditCard" />
									            	<label class="textDgray11 wrapTextAfterCheckBox" for="saveCreditCard"><bbbl:label key="lbl_spc_checkout_confirmation_donot_save_cc" language="${pageContext.request.locale.language}"/></label>
									            </div>
                                            </c:if>
                                            <c:if test="${isTransient}">
                                                <c:set var="profile_private_policy_title"><bbbl:label key="lbl_spc_private_policy_account" language="${pageContext.request.locale.language}"/></c:set>
                                                 <div class="clearfix formRow clearfix viewPrivacyPolicy noMarTop">
                                                    <a href="/store/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModal" title="${profile_private_policy_title}"><bbbl:label key="lbl_spc_private_policy_account" language="${pageContext.request.locale.language}"/></a>
                                                	 <c:if test="${currentSiteId eq BedBathCanadaSite}">
									             	 	<bbbt:textArea key="txt_spc_ca_address" language="${pageContext.request.locale.language}"/>
									             	</c:if> 
                                                 </div>
                                            </c:if>
                                        </div>
                                        <div class="clearfix">
                                            <div class="button button_active button_disabled fl">
                                               <c:set var="submitKey">
                                                    <bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
                                                </c:set>
                                                <dsp:input bean="ProfileFormHandler.value.email" type="hidden" beanvalue="ShoppingCart.last.billingAddress.email" name="email"></dsp:input>
                                                <dsp:input bean="ProfileFormHandler.registration" id="createAccountBtn" type="Submit" value="${submitKey}" disabled="true" iclass="enableOnDOMReady openForm" />
                                            </div>
                                           <a title="Cancel" class="btnCancel hidden" href="#"><bbbl:label key="lbl_payament_button_cancel" language="${pageContext.request.locale.language}"/></a>
                                        </div>
									</div>
								</dsp:form>
						     </dsp:oparam>
                         </dsp:droplet>
					</div>

						<p class="a marBottom_5 lblOrderId marTop_10"><bbbl:label key="lbl_spc_checkoutconfirmation_order_date" language="${language}"/></p>
						<span class="bold">
							<c:choose>
								<c:when test="${currentSiteId == BedBathCanadaSite}">
									<dsp:valueof value="${orderDate}" date="dd/MM/yyyy"/>
								</c:when>
								<c:otherwise>
									<dsp:valueof value="${orderDate}" date="MM/dd/yyyy"/>
								</c:otherwise>
							</c:choose>
						</span>

						 <c:if test="${not empty onlineOrderNumber}">
						 	<p class="a marBottom_5"><bbbl:label key="lbl_spc_checkoutconfirmation_delivery_order" language="${language}"/></p>
						 	<c:choose>
					            <c:when test="${CommonConfiguration.displayOrderXML eq false}">
					              	<span class="bold">${onlineOrderNumber}</span>
					            </c:when>
					            <c:otherwise>
					              	<span class="bold"><a href="${pageContext.request.contextPath}/checkout/display_order_xml.jsp">${onlineOrderNumber}</a></span>
					            </c:otherwise>
					        </c:choose>
					        <c:if test="${not empty txt_checkoutconfirmation_delivery_text}">
					         <p><bbbl:textArea key="txt_checkoutconfirmation_delivery_text" language="${language}"/></p>
					        </c:if>
						 </c:if>

						 <c:if test="${not empty bopusOrderNumber}">
						 	<p class="marBottom_5"><bbbl:label key="lbl_spc_checkoutconfirmation_instorepickup_order" language="${language}"/></p>
						 	<c:choose>
					            <c:when test="${CommonConfiguration.displayOrderXML eq false}">
					              	<span class="bold">${bopusOrderNumber}</span>
					            </c:when>
					            <c:otherwise>
					              	<span class="bold"><a href="${pageContext.request.contextPath}/checkout/display_order_xml.jsp">${bopusOrderNumber}</a></span>
					            </c:otherwise>
					        </c:choose>
					        <c:if test="${not empty txt_checkoutconfirmation_instorepickup_text}">
					        <p><bbbl:textArea key="txt_checkoutconfirmation_instorepickup_text" language="${language}"/></p>
					        </c:if>
						 </c:if>

					</div>
					<div class="share grid_2 omega clearfix">
						<div id="socialShare" class="fr">
							<a class="facebook shareIcon" href="#facebook" data-socialshare-role="icon" data-socialshare-network="facebook" title="Share this order on Facebook"><bbbl:label key="lbl_js_shareOrderOn_facebook" language="${pageContext.request.locale.language}"/></a>
							<a class="twitter shareIcon" href="#twitter" data-socialshare-role="icon" data-socialshare-network="twitter" title="Share this order on Twitter"><bbbl:label key="lbl_js_shareOrderOn_twitter" language="${pageContext.request.locale.language}"/></a>
							<a class="pinterest shareIcon" href="#pinterest" data-socialshare-role="icon" data-socialshare-network="pinterest" title="Share this order on Pinterest"><bbbl:label key="lbl_js_shareOrderOn_pinterest" language="${pageContext.request.locale.language}"/></a>
							<a class="print" href="#" title="Print confirmation"><bbbl:label key="lbl_checkoutconfirmation_printconfirmation" language="${language}"/></a>
						</div>
					</div>
				<div class="clearfix marTop_20">
                    <%-- WEB-279 --%>
                    <script type="text/javascript">
                        var BBB = BBB || {};
                        BBB.gaTransData = BBB.gaTransData || {};
                        BBB.gaTransData.items = [];
                    </script>
					<dsp:include page="/checkout/preview/frag/checkout_review_frag.jsp" flush="true">
						<dsp:param name="order" bean="ShoppingCart.last"/>
						<dsp:param name="displayTax" value="true"/>
					</dsp:include>
                    <script type="text/javascript">
                        <%-- BBBSL-3097 --%>
                        BBB.gaTransData.transaction = [];
                        BBB.gaTransData.transaction[0] = ["${genOrderCode}"];                                               /* orderNumber */
                        BBB.gaTransData.transaction[1] = ['<dsp:valueof value="${orderAmount}" number="0.00"/>'];           /* totalCharges */
                        BBB.gaTransData.transaction[2] = ['<dsp:valueof value="${orderTax}" number="0.00"/>'];              /* totalTax */
                        BBB.gaTransData.transaction[3] = ['<dsp:valueof value="${orderShippingAmount}" number="0.00"/>'];   /* totalShippingCharges */
                    </script>
					<dsp:include page="/checkout/preview/frag/checkout_review_right_frag.jsp" />
				</div>
                    <div class="clearfix">
                         <a class="backToHomepage fl" href="/"><bbbl:label key="lbl_spc_checkoutconfirmation_backtohomepage" language="${language}"/></a>
                     </div>
			</div>

	<%-- Added for social site integration : Starts --%>
		<script type="text/javascript">
			<dsp:include page="jsonCreatorForSocialMedia.jsp">
				<dsp:param name="orderVoList" value="${orderList}" />
			</dsp:include>
		</script>

	<%-- Added for social site integration : Ends --%>
	<%-- Start code for wedding channel and bump pixel --%>
<c:if test="${refId == 'wc'}">
<img src="${wcRefUrl}" height="1" width="1">
</c:if>
<c:if test="${refId == 'bp'}">
<img src="${bpRefUrl}" height="1" width="1">
</c:if>
<%-- end code for wedding channel and bump pixel --%>

	<script type="text/javascript">
		var price = "${preTaxAmout}"; //With two decimal places (Total Order)
		//ISO 4217 values:- 840 US Dollar, 124 Canadian Dollar, 826 UK Pound, 978 Euro
		var currency_id = "";
		if ('${currencyId}' == 'en_US') {
			currency_id = '840';
		} else if ('${currencyId}' == 'en_CA') {
			currency_id = '124';
		} else if ('${currencyId}' == 'de_DE') {
			currency_id = '978';
		} else if ('${currencyId}' == 'en_GB') {
			currency_id = '826';
		}
		var order_code = "${genOrderCode}";
		var sku = "${skuIds}"; //List of SKUs, comma separated
		var user_defined1 = "${userDefined1}"; //Total Order S&H after Discounts
		var user_defined2 = "${userDefined2}"; //Gross Margin after Discounts(does not include S&H)
        var pageAction = "2";
	</script>
<c:if test="${TellApartOn}">
<bbb:tellApart actionType="tx"/>
</c:if>

 <dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="Profile.transient"/>
	<dsp:oparam name="false">
		<dsp:getvalueof var="userId" bean="Profile.id"/>
	</dsp:oparam>
	<dsp:oparam name="true">
		<dsp:getvalueof var="userId" value=""/>
	</dsp:oparam>
</dsp:droplet>

<script type="text/javascript">
	var resx = new Object();
	resx.appid = "${appIdCertona}";
	resx.event = "${resxEventType}";
	resx.itemid = "${itemIds}";
	resx.qty = "${itemQtys}";
	resx.price = "${itemAmounts}";
	resx.total = "${grandTotal}";
	resx.transactionid = "${genOrderCode}";
    resx.customerid = "${userId}";
</script>

<%--RKG micro pixel for pickup in store Starts --%>
<script type="text/javascript">
var bopusOrder = '${bopusOrderNumber}';
if(bopusOrder.length>0){
	var appid = '${currentSiteId}';
	var type = 'pickup';
	$(function () {
		rkg_micropixel(appid,type);
	});
}

</script>
<%--RKG micro pixel for pickup in store Starts --%>
<%--YourAmigo Starts --%>
<c:if test="${YourAmigoON}">
<%-- ######################################################################### --%>
<%--  Configuring the javascript for tracking purchases (to be placed on the   --%>
<%--  purchase confirmation page).                                             --%>
<%-- ######################################################################### --%>
<c:choose>
<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
</c:when>
<c:when test="${(currentSiteId eq BedBathUSSite)}">
<script src="https://support.youramigo.com/73053126/trace.js"></script>
</c:when>
<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
<script src="https://support.youramigo.com/73053127/tracev2.js"></script>
</c:when>
</c:choose>

	

	
<dsp:droplet name="ForEach">
<dsp:param name="array" value="${orderList}"/>
<dsp:oparam name="outputStart">
		<script type="text/javascript">
			/* <![CDATA[ */

		    /*** YA conversion purchase tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/

			// --- begin customer configurable section ---

			ya_dv = "${grandTotal}"; // Set DDDD to the purchase price excluding tax and freight
			ya_tid = "${genOrderCode}"; // Set XXXXX to the transaction ID
			ya_pid = "${itemIds}"; // Set YYYYY to a comma-separated list of product/part numbers
			/* ]]> */
		</script>
		
</dsp:oparam>
<dsp:oparam name="output">
<dsp:getvalueof var="orderInfoVO" param="element"/>
		<script type="text/javascript">
			/* <![CDATA[ */

		    // Item details - for each item sold, add a 'yaAddItemDetail' JS call
			// Set PID to the product/part number (as in the comma separated list above)
			// Set QTY to the number sold of this product/part
			// Set PRICE to the price of the product/part
			var prodId = "${orderInfoVO.productId}";
			var itemCount = "${orderInfoVO.itemCount}";
			var itemPrice = "${orderInfoVO.price}";
			 yaAddItemDetail(prodId, itemCount, itemPrice);

			/* ]]> */
		</script>
</dsp:oparam>
<dsp:oparam name="outputEnd">
<c:choose>
<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
<c:set var="ya_cust" value="52657396"></c:set>
</c:when>
<c:when test="${(currentSiteId eq BedBathUSSite)}">
<c:set var="ya_cust" value="73053126"></c:set>
</c:when>
<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
<c:set var="ya_cust" value="73053127"></c:set>
</c:when>
</c:choose>
		<script type="text/javascript">
			/* <![CDATA[ */
			// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---

			ya_cust = '${ya_cust}';
			try { yaConvert(); } catch(e) {}
			/* ]]> */
		</script>
		
</dsp:oparam>
</dsp:droplet>
</c:if>
<%--YourAmigo Ends --%>

<%--RKG Comparison Shopping tracking starts --%>
<c:if test="${RKGOn}">
<script type="text/javascript" src="<bbbc:config key="secure_merchand_ma2q_js" configName="ThirdPartyURLs" />"></script>
</c:if>
<%--RKG Comparison Shopping tracking ends --%>

<c:if test="${ValueClickOn}">
	<jsp:useBean id="placeHolderValueClick" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderValueClick}" property="Order_Value">${orderPreTaxAmout}</c:set>
	<c:set target="${placeHolderValueClick}" property="mpuid">${genOrderCode}</c:set>
	<bbbt:textArea key="txt_spc_value_click_iframe" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderValueClick}"/>
</c:if>


<%--FB & Twitter Pixel tracking starts --%>
<%-- <c:if test="${pixelFbOn}">
	<c:set var="fb_purchase"><bbbc:config key="pixel_fb_purchase" configName="ContentCatalogKeys" /></c:set>
	<script>(function() {
	  var _fbq = window._fbq || (window._fbq = []);
	  if (!_fbq.loaded) {
	    var fbds = document.createElement('script');
	    fbds.async = true;
	    fbds.src = '//connect.facebook.net/en_US/fbds.js';
	    var s = document.getElementsByTagName('script')[0];
	    s.parentNode.insertBefore(fbds, s);
	    _fbq.loaded = true;
	  }
	})();
	window._fbq = window._fbq || [];
	window._fbq.push(['track', '${fb_purchase}', {'value':'${grandTotal}','currency':'USD'}]);
	</script>
	<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=${fb_purchase}&amp;cd[value]=${grandTotal}&amp;cd[currency]=USD&amp;noscript=1" /></noscript>
</c:if> --%>

<c:if test="${pixelTwtOn}">
	<c:set var="twt_purchase"><bbbc:config key="pixel_twt_purchase" configName="ContentCatalogKeys" /></c:set>
	
	<!-- script src="//platform.twitter.com/oct.js" type="text/javascript"></script -->
	<script type="text/javascript">
	var twttrTracking = setInterval(isTwitterApiLoaded, 1000);
    function isTwitterApiLoaded() {
    	if (BBB.twttrjs) {
    		twttr.conversion.trackPid('${twt_purchase}');
    		clearInterval(twttrTracking);
    	}
    }
	</script>
	<noscript>
	<img height="1" width="1" style="display:none;" alt="" src="https://analytics.twitter.com/i/adsct?txn_id=${twt_purchase}&p_id=Twitter" />
	<img height="1" width="1" style="display:none;" alt="" src="//t.co/i/adsct?txn_id=${twt_purchase}&p_id=Twitter" />
	</noscript>
</c:if>
<%--FB & Twitter Pixel tracking ends --%>

<dsp:include src="${contextPath}/selfservice/store/p2p_directions_input.jsp" />
<c:import url="/_includes/modules/social_share.jsp" />
	</jsp:body>
	<jsp:attribute name="footerContent">
		<dsp:droplet name="BBBOrderOmnitureDroplet">
				<dsp:param name="order" bean="ShoppingCart.last" />
				<dsp:getvalueof var="orderSubStatus" bean="ShoppingCart.last.subStatus"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="omnitureVO" param="omnitureVO" />
					<c:if test="${not empty omnitureVO && !(orderSubStatus=='DUMMY_RESTORE_INVENTORY'|| orderSubStatus=='DUMMY_IGNORE_INVENTORY')}">
						<script type="text/javascript">
							if (typeof s !== 'undefined') {
								s.pageName = 'Check Out>Confirmation';
								s.channel = 'Check Out';
								s.prop1 = 'Check Out';
								s.prop2 = 'Check Out';
								s.prop3 = 'Check Out';
								s.prop6='${pageContext.request.serverName}';
								s.eVar9='${pageContext.request.serverName}';
								s.events = '${omnitureVO.events}';
								s.products = '${omnitureVO.products}';
								s.purchaseID = '${omnitureVO.purchaseID}';
								s.state = '${omnitureVO.state}';
								s.zip = '${omnitureVO.zip}';
								s.eVar12 = '${omnitureVO.evar12}';
								s.eVar14 = '${omnitureVO.evar14}';
								// s.eVar16 = '${omnitureVO.evar16}';
								s.prop17 = '${omnitureVO.prop17}';
								s.eVar19 = '${omnitureVO.evar19}';
								s.eVar20 = '${omnitureVO.evar20}';
								s.eVar21 = '${omnitureVO.evar21}';
								s.eVar31 = '${omnitureVO.evar31}';
								var s_code = s.t();
								if (s_code)
									document.write(s_code);
							}
						</script>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</jsp:attribute>
</bbb:pageContainer>

</dsp:page>