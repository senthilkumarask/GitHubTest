<dsp:page>

	<%-- Imports --%>
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
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSBarcodeGeneratorDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	
	<%-- Variables --%>
	<dsp:getvalueof var="isTBSOrderSubmitted" bean="CheckoutProgressStates.isTBSOrderSubmitted"/>
	<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
	<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
	
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<c:set var="isCreditCard" value="${false}"/>
	<c:set var="isCheckoutConfirmation" value="true" scope="request"/>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:droplet name="OrderLookup">
		<dsp:param name="orderId" param="orderId" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="order" param="result"/>
		</dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
		<dsp:param name="order" value="${order}"/>
	</dsp:droplet>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="orderCode" param="orderId"/>
	<dsp:getvalueof var="orderDate" value="${order.submittedDate}"/>
	<dsp:getvalueof var="onlineOrderNumber" value="${order.onlineOrderNumber}"/>
	<dsp:getvalueof var="bopusOrderNumber" value="${order.bopusOrderNumber}"/>
	<dsp:getvalueof var="currencyId" value="${pageContext.response.locale}"/>
	<dsp:getvalueof var="orderAmount" value="${order.priceInfo.total}"/>

	<bbb:pageContainer index="false" follow="false">
	 	<script>
	$(document).ready(function(){
		$.ajax({
			url: '/tbs/checkout/checkoutLogout.jsp',
			cache: false,
			success: function(data){
			},
			error: function(data){
				//console.log('ajax error getting search form: ', data);
			}
		});
	});
	</script>
		<jsp:attribute name="pageWrapper">billing chekoutReview chekoutConfirm useCertonaJs</jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="PageType">CheckoutConfirmation</jsp:attribute>
		<jsp:attribute name="bodyClass">checkout confirmation</jsp:attribute>

		<jsp:body>
		 
			<c:set var="eDialogUrl"><bbbc:config key="eDialog_url" configName="ThirdPartyURLs" /></c:set>
			<c:set var="eDialogCookie"><bbbc:config key="eDialog_cookie" configName="ContentCatalogKeys" /></c:set>
			<c:set var="eDialogCookie2"><bbbc:config key="eDialog_cookie2" configName="ContentCatalogKeys" /></c:set>

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
				<dsp:param name="order" value="${order}" />
				<dsp:oparam name="output">
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
					<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
					
					<c:set var="userDefined1" scope="request">
						<dsp:valueof value="${preTaxAmout}" number="0.00"/>
					</c:set>
					<dsp:getvalueof var="userDefined2" value="${order.priceInfo.discountAmount}" scope="request"/>
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
								<img src="${rkgRefUrl}" height="1" width="1" />
							</dsp:oparam>
						</dsp:droplet>

						<%--RKG comparision shopping tag starts --%>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="rkgComparisonItemUrl" />
							<dsp:param name="elementName" value="rkgComparisonUrlItem" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="itemRKG" param="rkgComparisonUrlItem" idtype="java.lang.String" />
								<img src="/_assets/global/images/blank.gif" id="${itemRKG}" height="0" width="0" boarder="0" />
							</dsp:oparam>
						</dsp:droplet>
						<%--RKG comparision shopping tag ends --%>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>

			<%-- CJ pixel code starts here --%>
			<dsp:droplet name="/atg/commerce/order/droplet/CJOrderInfoDroplet">
				<dsp:param name="profile" bean="Profile"/>
				<dsp:param name="order" value="${order}" />
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
							<dsp:param name="order" value="${order}" />
							<dsp:oparam name="empty"></dsp:oparam>
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
				</dsp:oparam>
			</dsp:droplet>
			
			<%-- edialog code starts --%>
			<c:if test="${eDialogON}">
				<script type="text/javascript">
					(function(){
						var ordTot='${grandTotal}'.replace(/,/g,''),
							ordId='${genOrderCode}',
							eDUrl='${eDialogUrl}'.trim(),
							eDC='${eDialogCookie}'.trim(),
							eDC2='${eDialogCookie2}'.trim(),
							sv='<c:choose><c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">CAN</c:when><c:otherwise>US</c:otherwise></c:choose>',
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
								img='<img height="1" width="1" style="display:none;" alt="edialog_conversion_pixel" src="'+eDUrl+'CEDID='+f+'&m='+ordTot+'&pk='+ordId+'&siteVersion='+sv+'" />';
								document.write(img);
							}
						}
					}());
				</script>
			</c:if>
			<%-- edialog code ends --%>
			
			<dsp:droplet name="/com/bbb/commerce/order/droplet/TBSCheckCustomOrderDroplet">
				<dsp:param name="order" value="${order}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="kirsch" var="kirsch"/>
					<dsp:getvalueof param="cmo" var="cmo"/>
				</dsp:oparam>
			</dsp:droplet>
			
			<c:set var="paymentMethodText"><bbbl:label key="lbl_checkoutconfirmation_orderconfirmation_alreadypaid" language="${language}"/></c:set>			
			<c:forEach items="${order.paymentGroups}" var="paymentGroup">
				<c:if test="${paymentGroup.paymentMethod eq 'payAtRegister'}">
					<c:set var="paymentMethodText"><bbbl:label key="lbl_checkoutconfirmation_orderconfirmation_payatregister" language="${language}"/></c:set>
				</c:if>
			</c:forEach>

			<div class="row" id="content">
				<div class="small-12 large-8 columns print-12 no-padding">
					<div class="small-12 large-8 columns print-8">
						<h1>
							<bbbl:label key="lbl_checkoutconfirmation_confirmation" language="${language}"/>
						</h1>
						<h1 class="subheader">
							<span class="left"><strong><bbbl:label key="lbl_checkoutconfirmation_orderconfirmationsuccessmsg" language="${language}"/></strong></span><strong><span class="show-for-print hide">&nbsp;${paymentMethodText}</strong></span>
						</h1>
						<p class="p-secondary left no-margin">
							<bbbl:label key="lbl_checkoutconfirmation_confirmationemailsentto" language="${language}"/>&nbsp;<strong><dsp:valueof value="${order.billingAddress.email}"/></strong>
						</p>
					</div>
					<div class="small-12 columns print-right print-4">
						<p class="no-padding no-margin show-for-print hide"><Strong><bbbl:label key="lbl_orderconfirmation_siteheader_bbb" language="${language}"/></Strong></p>
						<p class="p-secondary no-padding no-margin">
						<%-- TODO: need the following in a bbbl:lable --%>
							<strong>Questions About Your Order?</strong></p>
						<p class="p-secondary beyondCall no-padding"><bbbl:label key="lbl_orderconfirmation_contactinfo" language="${language}"/></p>
					</div>
					<div class="small-12 columns">
						<hr class="divider show-for-print hide"></hr>
						<div class="row">
							<c:if test="${not empty onlineOrderNumber}">
								<div class="small-12 large-7 columns">
									<div class="row">
										<div class="small-12 large-4 columns no-padding-right print-3">
											<p class="p-secondary"><strong>Order Number<%--<bbbl:label key="lbl_checkoutconfirmation_delivery_order" language="${language}"/>--%></strong></p>
										</div>
										<div class="small-12 large-8 columns print-9">
											<p class="p-secondary">
												<c:choose>
													<c:when test="${CommonConfiguration.displayOrderXML eq false}">
														${onlineOrderNumber}
													</c:when>
													<c:otherwise>
														<a href="${pageContext.request.contextPath}/checkout/display_order_xml.jsp">${onlineOrderNumber}</a>
													</c:otherwise>
												</c:choose>
											</p>
											<c:if test="${not empty txt_checkoutconfirmation_delivery_text}">
												<p class="p-secondary"><bbbl:textArea key="txt_checkoutconfirmation_delivery_text" language="${language}"/></p>
											</c:if>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${not empty bopusOrderNumber}">
								<div class="small-12 large-7 columns">
									<div class="row">
										<div class="small-12 large-4 columns no-padding-right print-3">
											<p class="p-secondary"><strong>Order Number<%--<bbbl:label key="lbl_checkoutconfirmation_instorepickup_order" language="${language}"/>--%></strong></p>
										</div>
										<div class="small-12 large-8 columns print-9">
											<p class="p-secondary">
												<c:choose>
													<c:when test="${CommonConfiguration.displayOrderXML eq false}">
														${bopusOrderNumber}
													</c:when>
													<c:otherwise>
														<a href="${pageContext.request.contextPath}/checkout/display_order_xml.jsp">${bopusOrderNumber}</a>
													</c:otherwise>
												</c:choose>
											</p>
											<c:if test="${not empty txt_checkoutconfirmation_instorepickup_text}">
												<p class="p-secondary"><bbbl:textArea key="txt_checkoutconfirmation_instorepickup_text" language="${language}"/></p>
											</c:if>
										</div>
									</div>
								</div>
							</c:if>
							<div class="small-12 large-5 columns">
								<div class="row">
									<div class="small-12 large-5 columns no-padding-right print-4">
										<p class="p-secondary"><strong>Store Number</strong></p>
									</div>
									<div class="small-12 large-7 columns print-8">
										<p class="p-secondary"><c:out value="${order.tbsStoreNo}"/></p>
									</div>
								</div>
							</div>
							<div class="small-12 large-7 columns">
								<div class="row">
									<div class="small-12 large-4 columns print-3">
										<p class="p-secondary"><strong>Order Date<%--<bbbl:label key="lbl_checkoutconfirmation_order_date" language="${language}"/>--%></strong></p>
									</div>
									<div class="small-12 large-8 columns print-9">
										<p class="p-secondary">
											<c:choose>
												<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
													<dsp:valueof value="${orderDate}" date="dd/MM/yyyy"/>
												</c:when>
												<c:otherwise>
													<dsp:valueof value="${orderDate}" date="MM/dd/yyyy"/>
												</c:otherwise>
											</c:choose>
										</p>
									</div>
								</div>
							</div>
							<div class="small-12 large-5 columns">
								<div class="row">
									<div class="small-12 large-5 columns print-4">
										<p class="p-secondary"><strong>Associate ID</strong></p>
									</div>
									<div class="small-12 large-7 columns print-8">
										<p class="p-secondary"><c:out value="${order.TBSAssociateID}"/></p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
		 
				<%-- this only needs to be shown if the user is not logged in/guest checkout --%>
				<div class="small-12 large-4 columns">
					<div class="row">
						<div class="small-12 columns">
						<c:choose>
							<c:when test="${fn:contains(header['User-Agent'],'Adobe_AIR_3.6')}">
								<a class="print-page button small service expand" href="#" title="Print confirmation">
								<bbbl:label key="lbl_checkoutconfirmation_printconfirmation" language="${language}"/>
							</a>
							</c:when>
							<c:otherwise>
								<a class="print-trigger button small service expand" href="#" title="Print confirmation">
									<bbbl:label key="lbl_checkoutconfirmation_printconfirmation" language="${language}"/>
								</a>
							</c:otherwise>
						</c:choose>
						<%-- <c:if test="${fn:contains(header['User-Agent'],'Adobe_AIR_3.6')}">margin-bottom: 3rem;</c:if>
							<a class="print-trigger button small service expand" href="#" title="Print confirmation">
								<bbbl:label key="lbl_checkoutconfirmation_printconfirmation" language="${language}"/>
							</a> --%>
						</div>
					</div>
					<div class="row">
						<div class="small-12 columns">
						 <dsp:droplet name="/atg/dynamo/droplet/Switch">
						 <dsp:param name="value" value="${isTBSOrderSubmitted}"/>
						 <dsp:oparam name="true">
									<dsp:form name="frmCheckoutExpressProfile" id="frmCheckoutExpressProfile" action="${contextPath}/account/frags/checkout_registration_json.jsp" method="post">
										<dsp:input bean="ProfileFormHandler.registrationSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp"/>
										<dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp?email="/>
										<dsp:getvalueof var="firstName" value="${order.billingAddress.firstName}"/>
										<dsp:getvalueof var="lastName" value="${order.billingAddress.lastName}"/>

										<input name="fname" id="firstName" type="hidden" value="${firstName}"  />
										<input name="lname" id="lastName" type="hidden" value="${lastName}"  />
											
										<div class="gray-panel">
											<h3><bbbl:label key="lbl_checkoutconfirmation_createaccount" language="${language}"/></h3>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_wecansavetheinformationyouenteredandcreateanaccount" language="${language}"/>
											</p>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_advantagesofcreatinganaccount" language="${language}"/>
											</p>
											<div class="pageErrors"></div>

											<div class="row createAccountForm hidden">

												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_password" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
													</dsp:input>
												</div>

												<%-- confirm password --%>
												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_confirmpassword" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
													</dsp:input>
												</div>

												<%-- email opt-in --%>
												<div class="small-12 columns">
													<label class="inline-rc checkbox" for="emailOptIn">
														<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
															<c:choose>
																<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
																	<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																		<dsp:tagAttribute name="aria-checked" value="false"/>
																		<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																	</dsp:input>
																</c:when>
																<c:otherwise>
																	<c:if test="${flag == 'yes'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="true" value="true">
																			<dsp:tagAttribute name="aria-checked" value="true"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																	<c:if test="${flag == 'no'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																			<dsp:tagAttribute name="aria-checked" value="false"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																</c:otherwise>
															</c:choose>
														</dsp:getvalueof>
														<span></span>
														<bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/>
													</label>
												</div>

												<%-- Baby Canada --%>
												<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
													<div class="small-12 columns">
														<label class="inline-rc checkbox" for="emailOptIn_BabyCA">
															<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
																<c:choose>
																	<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
															<span></span>
															<bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/>
														</label>
													</div>
												</c:if>
												<!-- Baby Canada -->

												<%-- KP COMMENT START: don't see this in the designs --%>
												<!--fixing as per the BUG in RM#30224 -->
												<c:if test="${shareProfileOn}">
													<c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
															<div class="small-12 columns">
																<label for="shareAccount" class="inline-rc checkbox">
																	<dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" iclass="fl" id="shareAccount" />
																	<span></span>
																	<bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/>
																	<bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/>
																</label>	
															</div>
													</c:if>
												</c:if>

												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" value="${order.paymentGroups}"/>
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
														<div class="small-12 columns">
															<label class="inline-rc checkbox" for="saveCreditCard">
																<dsp:input bean="ProfileFormHandler.saveCreditCardInfoToProfile" type="checkbox" id="saveCreditCard" />
																<span></span>
																<bbbl:label key="lbl_checkout_confirmation_donot_save_cc" language="${pageContext.request.locale.language}"/>
															</label>
														</div>
												</c:if>

												<c:if test="${isTransient}">
													<div class="row viewPrivacyPolicy">
														<div class="small-12 columns">
															<c:set var="profile_private_policy_title"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></c:set>
															<a href="/tbs/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModalTrigger" title="${profile_private_policy_title}"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></a>
														</div>
													</div>
												</c:if>

												<c:set var="submitKey">
													<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
												</c:set>
												<dsp:input bean="ProfileFormHandler.value.email" type="hidden" value="${order.billingAddress.email}" name="email"></dsp:input>

											</div>
											<div class="row">
												<div class="small-12 columns">
													<dsp:input bean="ProfileFormHandler.registration" id="createAccountBtn" type="Submit" value="${submitKey}" iclass="button tiny service openForm" />
													<a title="Cancel" class="button tiny download btnCancel hidden" href="#">Cancel</a>
												</div>
											</div>
										</div>
									</dsp:form>
									<div id="loginExtensionModal" class="reveal-modal" data-reveal></div>
						 </dsp:oparam>
						  <dsp:oparam name="default">
						  <dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
								<dsp:oparam name="true">
									<dsp:form name="frmCheckoutExpressProfile" id="frmCheckoutExpressProfile" action="${contextPath}/account/frags/checkout_registration_json.jsp" method="post">
									
										<dsp:input bean="ProfileFormHandler.registrationSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp"/>
										<dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp?email="/>
										<dsp:getvalueof var="firstName" value="${order.billingAddress.firstName}"/>
										<dsp:getvalueof var="lastName" value="${order.billingAddress.lastName}"/>

										<input name="fname" id="firstName" type="hidden" value="${firstName}"  />
										<input name="lname" id="lastName" type="hidden" value="${lastName}"  />
											
										<div class="gray-panel">
											<h3><bbbl:label key="lbl_checkoutconfirmation_createaccount" language="${language}"/></h3>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_wecansavetheinformationyouenteredandcreateanaccount" language="${language}"/>
											</p>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_advantagesofcreatinganaccount" language="${language}"/>
											</p>
											<div class="pageErrors"></div>

											<div class="row createAccountForm hidden">

												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_password" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
													</dsp:input>
												</div>

												<%-- confirm password --%>
												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_confirmpassword" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
													</dsp:input>
												</div>

												<%-- email opt-in --%>
												<div class="small-12 columns">
													<label class="inline-rc checkbox" for="emailOptIn">
														<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
															<c:choose>
																<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
																	<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																		<dsp:tagAttribute name="aria-checked" value="false"/>
																		<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																	</dsp:input>
																</c:when>
																<c:otherwise>
																	<c:if test="${flag == 'yes'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="true" value="true">
																			<dsp:tagAttribute name="aria-checked" value="true"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																	<c:if test="${flag == 'no'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																			<dsp:tagAttribute name="aria-checked" value="false"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																</c:otherwise>
															</c:choose>
														</dsp:getvalueof>
														<span></span>
														<bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/>
													</label>
												</div>

												<%-- Baby Canada --%>
												<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
													<div class="small-12 columns">
														<label class="inline-rc checkbox" for="emailOptIn_BabyCA">
															<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
																<c:choose>
																	<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
															<span></span>
															<bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/>
														</label>
													</div>
												</c:if>
												<!-- Baby Canada -->

												<%-- KP COMMENT START: don't see this in the designs --%>
												<!--fixing as per the BUG in RM#30224 -->
												<c:if test="${shareProfileOn}">
													<c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
															<div class="small-12 columns">
																<label for="shareAccount" class="inline-rc checkbox">
																	<dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" iclass="fl" id="shareAccount" />
																	<span></span>
																	<bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/>
																	<bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/>
																</label>	
															</div>
													</c:if>
												</c:if>

												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" value="${order.paymentGroups}"/>
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
														<div class="small-12 columns">
															<label class="inline-rc checkbox" for="saveCreditCard">
																<dsp:input bean="ProfileFormHandler.saveCreditCardInfoToProfile" type="checkbox" id="saveCreditCard" />
																<span></span>
																<bbbl:label key="lbl_checkout_confirmation_donot_save_cc" language="${pageContext.request.locale.language}"/>
															</label>
														</div>
												</c:if>

												<c:if test="${isTransient}">
													<div class="row viewPrivacyPolicy">
														<div class="small-12 columns">
															<c:set var="profile_private_policy_title"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></c:set>
															<a href="/tbs/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModalTrigger" title="${profile_private_policy_title}"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></a>
														</div>
													</div>
												</c:if>

												<c:set var="submitKey">
													<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
												</c:set>
												<dsp:input bean="ProfileFormHandler.value.email" type="hidden" value="${order.billingAddress.email}" name="email"></dsp:input>
												<dsp:input bean="ProfileFormHandler.orderId" type="hidden" value="${order.id}" name="order"></dsp:input>

											</div>
											<div class="row">
												<div class="small-12 columns">
													<dsp:input bean="ProfileFormHandler.registration" id="createAccountBtn" type="Submit" value="${submitKey}" iclass="button tiny service openForm" />
													<a title="Cancel" class="button tiny download btnCancel hidden" href="#">Cancel</a>
												</div>
											</div>
										</div>
									</dsp:form>
									<div id="loginExtensionModal" class="reveal-modal" data-reveal></div>
								</dsp:oparam>
						  <dsp:oparam name="default">
						  <dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
								<dsp:oparam name="true">
									<dsp:form name="frmCheckoutExpressProfile" id="frmCheckoutExpressProfile" action="${contextPath}/account/frags/checkout_registration_json.jsp" method="post">
										<dsp:input bean="ProfileFormHandler.registrationSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp"/>
										<dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/checkout_registration_json.jsp?email="/>
										<dsp:getvalueof var="firstName" value="${order.billingAddress.firstName}"/>
										<dsp:getvalueof var="lastName" value="${order.billingAddress.lastName}"/>
										<input name="fname" id="firstName" type="hidden" value="${firstName}"  />
										<input name="lname" id="lastName" type="hidden" value="${lastName}"  />
										<div class="gray-panel">
											<h3><bbbl:label key="lbl_checkoutconfirmation_createaccount" language="${language}"/></h3>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_wecansavetheinformationyouenteredandcreateanaccount" language="${language}"/>
											</p>
											<p class="p-secondary">
												<bbbl:label key="lbl_checkoutconfirmation_advantagesofcreatinganaccount" language="${language}"/>
											</p>
											<div class="pageErrors"></div>
											<div class="row createAccountForm hidden">
												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_password" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
													</dsp:input>
												</div>
												<%-- confirm password --%>
												<div class="small-12 columns">
													<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_confirmpassword" language="${language}"/></c:set>
													<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassCheckoutConfirm" >
														<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
													</dsp:input>
												</div>
												<%-- email opt-in --%>
												<div class="small-12 columns">
													<label class="inline-rc checkbox" for="emailOptIn">
														<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
															<c:choose>
																<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
																	<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																		<dsp:tagAttribute name="aria-checked" value="false"/>
																		<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																	</dsp:input>
																</c:when>
																<c:otherwise>
																	<c:if test="${flag == 'yes'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="true" value="true">
																			<dsp:tagAttribute name="aria-checked" value="true"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																	<c:if test="${flag == 'no'}">
																		<dsp:input type="checkbox" bean="ProfileFormHandler.emailOptIn" id="emailOptIn" name="emailOptIn" checked="false">
																			<dsp:tagAttribute name="aria-checked" value="false"/>
																			<dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
																		</dsp:input>
																	</c:if>
																</c:otherwise>
															</c:choose>
														</dsp:getvalueof>
														<span></span>
														<bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/>
													</label>
												</div>
												<%-- Baby Canada --%>
												<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
													<div class="small-12 columns">
														<label class="inline-rc checkbox" for="emailOptIn_BabyCA">
															<dsp:getvalueof bean="ProfileFormHandler.value.receiveEmail" id="flag">
																<c:choose>
																	<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
															<span></span>
															<bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/>
														</label>
													</div>
												</c:if>
												<!-- Baby Canada -->
												<%-- KP COMMENT START: don't see this in the designs --%>
												<!--fixing as per the BUG in RM#30224 -->
												<c:if test="${shareProfileOn}">
													<c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
															<div class="small-12 columns">
																<label for="shareAccount" class="inline-rc checkbox">
																	<dsp:input bean="ProfileFormHandler.sharedCheckBoxEnabled" type="checkbox" iclass="fl" id="shareAccount" />
																	<span></span>
																	<bbbl:label key="lbl_profile_shareaccount" language="${pageContext.request.locale.language}"/>
																	<bbbt:textArea key="txt_profile_shareSite_text" language="${pageContext.request.locale.language}"/>
																</label>	
															</div>
													</c:if>
												</c:if>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" value="${order.paymentGroups}"/>
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
														<div class="small-12 columns">
															<label class="inline-rc checkbox" for="saveCreditCard">
																<dsp:input bean="ProfileFormHandler.saveCreditCardInfoToProfile" type="checkbox" id="saveCreditCard" />
																<span></span>
																<bbbl:label key="lbl_checkout_confirmation_donot_save_cc" language="${pageContext.request.locale.language}"/>
															</label>
														</div>
												</c:if>
												<c:if test="${isTransient}">
													<div class="row viewPrivacyPolicy">
														<div class="small-12 columns">
															<c:set var="profile_private_policy_title"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></c:set>
															<a href="/tbs/static/PrivacyPolicy?showAsPopup=true" class="privacyPolicyModalTrigger" title="${profile_private_policy_title}"><bbbl:label key="lbl_profile_private_policy" language="${pageContext.request.locale.language}"/></a>
														</div>
													</div>
												</c:if>
												<c:set var="submitKey">
													<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
												</c:set>
												<dsp:input bean="ProfileFormHandler.value.email" type="hidden" value="${order.billingAddress.email}" name="email"></dsp:input>
											</div>
											<div class="row">
												<div class="small-12 columns">
													<dsp:input bean="ProfileFormHandler.registration" id="createAccountBtn" type="Submit" value="${submitKey}" iclass="button tiny service openForm" />
													<a title="Cancel" class="button tiny download btnCancel hidden" href="#">Cancel</a>
												</div>
											</div>
										</div>
									</dsp:form>
									<div id="loginExtensionModal" class="reveal-modal" data-reveal></div>
								</dsp:oparam>
							</dsp:droplet>
						  </dsp:oparam>
						 </dsp:droplet>
						 </dsp:oparam>
						 </dsp:droplet>
						 
						</div>
					</div>
				</div>
			</div>

			<%-- barcodes: this should be shown only for print --%>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${order.paymentGroups}"/>
				<dsp:param name="elementName" value="paymentGroup" />
				<dsp:oparam name="outputStart">
					<div class="row barcodes">
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="paymentGroup.paymentMethod"/>
						<dsp:oparam name="payAtRegister">
							<dsp:droplet name="TBSBarcodeGeneratorDroplet">
								<dsp:param name="orderId" value="${genOrderCode }"/>
								<dsp:param name="orderTotal" value="${order.priceInfo.total }"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="orderIdBarcodeImg" var="orderCode"/>
									<dsp:getvalueof param="orderTotalBarcodeImg" var="orderTotal"/>
									<hr class="divider show-for-print hide"></hr>
									<div class="small-12 large-12 columns">
										<div class="small-12 large-7 columns"> </div>
										<div class="small-12 large-5 columns">
											<div class="small-12 large-5 columns">Order Total:</div>
											<div class="small-12 large-7 columns"><div class="print-center divider"><fmt:formatNumber value="${order.priceInfo.total}" type="currency" /></div></div>
										</div>
									</div>
									<div class="small-12 large-12 columns">
										<div class="small-12 columns medium-1">&nbsp;&nbsp;</div>
										<div class="small-12 columns medium-11">Order Number <strong>${genOrderCode}</strong></div>
										
									</div>
									<div class="small-12 columns medium-1 print-right">
										<span class="digits">1</span>
									</div>
									<div class="small-12 large-5 columns">
										<img src="data:image/jpg;base64, <c:out value='${orderCode}' />" />
									</div>
									<!-- <div class="small-12 columns medium-1 print-right left">
										
									</div> -->
									<div class="small-12 large-5 columns">
										<div class="small-12 columns print-1 no-padding">
										<span class="digits">2</span>
										</div>
										<div class="small-12 columns print-11 left">
										<img src="data:image/jpg;base64, <c:out value='${orderTotal}' />" />
										</div>
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="outputEnd">
					</div>
				</dsp:oparam>
			</dsp:droplet>

			<div class="row">

				<c:choose>
					<c:when test="${fn:length(order.shippingGroups) > 1}">
						<%-- multiship --%>
						<div class="small-12 columns">
							<dsp:include page="/checkout/preview/multi_preview.jsp" flush="true">
								<dsp:param name="order" value="${order}"/>
								<dsp:param name="hideOrderNumber" value="true"/>
								<dsp:param name="displayTax" value="true"/>
								<dsp:param name="isConfirmation" value="true"/>
								<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
							</dsp:include>
						</div>
					</c:when>
					<c:otherwise>
						<%-- singleship --%>
						<div class="small-12 columns">
							<h2 class="divider">Shipping Information</h2>
							<dsp:include page="/checkout/shipping/single_shipping_review.jsp">
								<dsp:param name="order" value="${order}"/>
								<dsp:param name="isConfirmation" value="true"/>
							</dsp:include>
						</div>
						<%-- billing --%>
						<div class="small-12 columns">
							<h2 class="divider">Billing Information</h2>
							<dsp:include page="/checkout/billing/single_billing_review.jsp">
								<dsp:param name="order" value="${order}"/>
								<dsp:param name="isConfirmation" value="true"/>
							</dsp:include>
						</div>

						<%-- payment --%>
						<div class="small-12 columns">
							<h2 class="divider">Payment Information</h2>
							<dsp:include page="/checkout/payment/single_payment_review.jsp">
								<dsp:param name="order" value="${order}"/>
								<dsp:param name="isConfirmation" value="true"/>
							</dsp:include>
						</div>

						<%-- preview --%>
						<div class="small-12 columns">
							<h2 class="divider preview-section">Delivery Information<span></span></h2>
							<dsp:include page="/checkout/preview/single_preview.jsp" flush="true">
								<dsp:param name="order" value="${order}"/>
								<dsp:param name="hideOrderNumber" value="true"/>
								<dsp:param name="displayTax" value="true"/>
								<dsp:param name="isConfirmation" value="true"/>
								<dsp:param value="${isShippingMethodChanged}" name="isShippingMethodChanged" />
							</dsp:include>
						</div>

					</c:otherwise>
				</c:choose>

			</div>
			<c:if test="${kirsch}">
				<div class="row kirsch-row">
					<dsp:include page="/checkout/preview/frag/kirsch_confirmation_frag.jsp"></dsp:include>
				</div>
			</c:if>
			<c:if test="${cmo}">
				<div class="row kirsch-row">
					<dsp:include page="/checkout/preview/frag/cmo_confirmation_frag.jsp"></dsp:include>
				</div>
			</c:if>
			
			<div class="row">
				<div class="small-12 large-offset-8 large-4 columns">
					<a href="/tbs" class="button small secondary expand"><bbbl:label key="lbl_checkoutconfirmation_backtohomepage" language="${language}"/></a>
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
				<!-- ######################################################################### -->
				<!--  Configuring the javascript for tracking purchases (to be placed on the   -->
				<!--  purchase confirmation page).                                             -->
				<!-- ######################################################################### -->
				<c:choose>
					<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
						<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
					</c:when>
					<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
						<script src="https://support.youramigo.com/73053126/trace.js"></script>
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
							<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
								<c:set var="ya_cust" value="52657396"></c:set>
							</c:when>
							<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
								<c:set var="ya_cust" value="73053126"></c:set>
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
				<bbbt:textArea key="txt_value_click_iframe" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderValueClick}"/>
			</c:if>

			<dsp:include src="${contextPath}/selfservice/store/p2p_directions_input.jsp" />
			<c:import url="/_includes/modules/social_share.jsp" />

		</jsp:body>

		<jsp:attribute name="footerContent">
			<dsp:droplet name="BBBOrderOmnitureDroplet">
				<dsp:param name="order" value="${order}"/>
				<dsp:param name="loggedin" value="${param.loggedin}"/>				
				<dsp:oparam name="output">
					<dsp:getvalueof var="omnitureVO" param="omnitureVO" />
					<c:if test="${not empty omnitureVO}">
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
								s.eVar16 = '${omnitureVO.evar16}'; 
								s.prop17 = '${omnitureVO.prop17}';
								s.eVar19 = '${omnitureVO.evar19}';
								s.eVar20 = '${omnitureVO.evar20}';
								s.eVar21 = '${omnitureVO.evar21}';
								s.eVar31 = '${omnitureVO.evar31}';
								s.eVar54 = '${omnitureVO.eVar54}';
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
