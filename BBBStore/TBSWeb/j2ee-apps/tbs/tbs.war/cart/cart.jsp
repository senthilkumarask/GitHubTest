<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBOrderRKGInfo" />
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
	</dsp:droplet>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="CertonaContext" value="" scope="request" />
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
	<dsp:getvalueof var="cartState" value="${0}"/>
	<c:if test="${currentState ne cartState}">
		<dsp:setvalue bean="CheckoutProgressStates.currentLevel" value="CART"/>
	</c:if>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
	<c:set var="RegistryContext" value="" scope="request"/>
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>

	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient"/>
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id"/>
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value=""/>
		</dsp:oparam>
	</dsp:droplet>


	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="ShoppingCart.current.commerceItems" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
			<c:if test="${orderPriceInfo == null}">
				<dsp:droplet name="RepriceOrderDroplet">
					<dsp:param value="ORDER_SUBTOTAL_SHIPPING" name="pricingOp"/>
				</dsp:droplet>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<bbb:pageContainer>

		<%--R2.2 SCOPE #158 START deviceFingerprint JS call to cybersource--%>
		<c:if test="${deviceFingerprintOn}">
			<c:set var="merchandId"><bbbc:config key="DF_merchandId" configName="ThirdPartyURLs"/></c:set>
			<c:set var="orgId"><bbbc:config key="DF_orgId" configName="ThirdPartyURLs"/></c:set>
			<c:set var="jSessionId">${pageContext.session.id}</c:set>
			<c:set var="jSession_id" value="${fn:split(jSessionId, '!')}" />
			<c:set var="org_jsessionid">${jSession_id[0]}</c:set>

			<div style="background:url(<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}&amp;m=1)"></div>
			<img src="<bbbc:config key="deviceFinger_image_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}&amp;m=2" />
			<script src="<bbbc:config key="deviceFinger_js" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}"></script>
			<object type="application/x-shockwave-flash" data="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}" width="1" height="1">
				<param name="movie" value="<bbbc:config key="deviceFinger_flasf_url" configName="ThirdPartyURLs"/>?org_id=${orgId}&amp;session_id=${merchandId}${org_jsessionid}" />
				<param name="wmode" value="transparent" />
				<div></div>
			</object>
		</c:if>

		<%--R2.2 SCOPE #158 END deviceFingerprint JS call to cybersource --%>
		<jsp:attribute name="section">cartDetail</jsp:attribute>
		<jsp:attribute name="PageType">CartDetails</jsp:attribute>
		<jsp:attribute name="bodyClass">cart</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist cartDetail useCertonaJs useMapQuest useStoreLocator</jsp:attribute>

		<jsp:body>

			<script type="text/javascript">
				var resx = new Object();
				var productIdsCertona = '';
				var eventTypeCertona = '';
			</script>

			<dsp:droplet name="CartRegistryInfoDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
			</dsp:droplet>

			<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
			<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
			<c:set var="isStockAvailability" value="yes" scope="request"/>

			<div id="content" role="main">

				<%-- Header --%>
				<dsp:include page="/cart/cartHeader.jsp">
				</dsp:include>

				<%-- Body --%>
				<dsp:include page="/cart/cartBody.jsp">
				</dsp:include>


				<c:import url="/selfservice/find_in_store.jsp" >
					<c:param name="enableStoreSelection" value="true"/>
				</c:import>

				<c:import url="/_includes/modules/change_store_form.jsp" >
					<c:param name="action" value="${contextPath}/_includes/modules/cart_store_pickup_form.jsp"/>
				</c:import>


				<script type="text/javascript">
					function removeHandler(commerceItemId,productId){
						document.getElementById('remove').value=commerceItemId;
						document.getElementById('remove').click();
					}
					function changeToShipOnline(commerceItemId,oldShippingId,newQuantity){
						document.getElementById('onlineComId').value=commerceItemId;
						document.getElementById('onlineShipId').value=oldShippingId;
						document.getElementById('onlineQuantity').value=newQuantity;
						document.getElementById('onlineSubmit').click();
					}
					function omniAddToRegis(elem, prodId, qty, itemPrice, skuId, isPersonalized ,personalizationType){
						var wrapper = $(elem).closest('.savedItemRow'),
							regData = wrapper.find('.omniRegData').find('option:selected'),
							regId = (regData[0] ? regData.attr('value') : wrapper.find('.omniRegId').val()) || "",
							regName = (regData[0] ? regData.attr('class') : wrapper.find('.omniRegName').val()) || "",
							regProdString = ';' + prodId + ';;;event22=' + qty + '|event23=' + itemPrice + ';eVar30=' + skuId;

						if (typeof addItemWishListToRegistry === "function") { addItemWishListToRegistry(regProdString, regName, regId, isPersonalized ,personalizationType); }
					}
				</script>

				<c:if test="${YourAmigoON}">
					<c:if test="${isTransient eq false}">
						<!-- ######################################################################### -->
						<!--  Configuring the javascript for tracking signups (to be placed on the     -->
						<!--  signup confirmation page, if any).                                       -->
						<!-- ######################################################################### -->

						<c:choose>
							<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
								<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
								<c:set var="ya_cust" value="52657396"></c:set>
							</c:when>
							<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
								<script src="https://support.youramigo.com/73053126/trace.js"></script>
								<c:set var="ya_cust" value="73053126"></c:set>
							</c:when>
						</c:choose>
						<script type="text/javascript">
							/* <![CDATA[ */

								/*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/

								// --- begin customer configurable section ---

								ya_tid = Math.floor(Math.random()*1000000); // Set XXXXX to the ID counting the signup, or to a random
													// value if you have no such id - eg,
													// ya_tid = Math.random();
								ya_pid = ""; // Set YYYYY to the type of signup - can be blank
													// if you have only one signup type.

								ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
								// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---

								ya_cust = "${ya_cust}";
								try { yaConvert(); } catch(e) {}

							/* ]]> */
						</script>
					</c:if>
				</c:if>
				<c:set var="lastMinItemsMax" scope="request">
				  <bbbc:config key="LastMinItemsMax" configName="CertonaKeys" />
				</c:set>
				<%-- <dsp:droplet name="CertonaDroplet">
					 <dsp:param name="scheme" value="fc_lmi"/>
					 <dsp:param name="context" param="CertonaContext"/>
					 <dsp:param name="exitemid" param="RegistryContext"/>
					 <dsp:param name="userid" value="${userId}"/>
					 <dsp:param name="number" value="${lastMinItemsMax}"/>
					 <dsp:param name="siteId" value="${appid}"/>
					 <dsp:param name="shippingThreshold" param="shippingThreshold"/>
					 <dsp:oparam name="output">
					 	<dsp:getvalueof var="skuDetailVOList" param="certonaResponseVO.resonanceMap.fc_lmi.skuDetailVOList"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${skuDetailVOList}"/>
							<dsp:param name="elementName" value="skuVO"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
								<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
							 </dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet> --%>
				<dsp:droplet name="/com/bbb/commerce/order/droplet/TBSLinksCertonaDroplet">
    			<dsp:param name="order" bean="ShoppingCart.current" />
       				<dsp:oparam name="output">
       				<dsp:getvalueof var ="linksCertona" param="itemSkuIds"/>
				</dsp:oparam>
			</dsp:droplet>
				<c:set var="linksCertona" value="${fn:replace(linksCertona, '|',';')}" />
				<dsp:getvalueof var="linkString" param="linkString" />
				<script type="text/javascript">
					linksCertona ='${linksCertona}' + '${linkString}';
				</script>
				<c:if test="${TellApartOn}">
					<bbb:tellApart actionType="updatecart"/>
				</c:if>
				<script type="text/javascript">
					resx.appid = "${appIdCertona}";
					resx.top1 = 100000;
					resx.top2 = 100000;
					resx.customerid = "${userId}";
					resx.itemid = "${linksCertona}";
					resx.event = "viewcart";
					resx.links = "${linksCertona}";
					resx.pageid = '';
				</script>
			</div>

			<%--DoubleClick Floodlight START --%>
			<dsp:droplet name="BBBOrderRKGInfo">
				<dsp:param name="order" bean="ShoppingCart.current" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="rkgProductNames" param="rkgProductNames"/>
					<dsp:getvalueof var="rkgProductIds" param="rkgProductIds"/>
					<dsp:getvalueof var="rkgProductCount" param="rkgProductCount"/>
				</dsp:oparam>
			</dsp:droplet>
			<c:if test="${not empty commItem.BBBCommerceItem.personalizationOptions}">
			  <c:set var="personalizationMessage">${eximCustomizationCodesMap[pOpt]}</c:set>
			  <input type="hidden" name="personalizationMessageCart" id="personalizationMessageCart" value="${personalizationMessage}"/>
			</c:if>
			<script type="text/javascript">
				function omniRemove(prodId, skuId){
					if(typeof s !=='undefined') {
						s.events="scRemove";
						s.products=';'+prodId +";;;;eVar30=" + skuId;
						var s_code=s.t();
						if(s_code)document.write(s_code);
					}
				}
				function omniAddToList(elem, prodId, skuId, qty){
					var totalPrice = $(elem).closest('.cartRow').find('.omniTotalPrice').text().trim().replace("$","");
					var persDone = '';
					var personalizationMessage = $('input[name=personalizationMessageCart]').val();
					if(typeof personalizationMessage !== 'undefined'){
					  var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId+'|eVar54='+personalizationMessage;
					  persDone = true;
					 }
					 else {
					   var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId;
					  }

					if (typeof additemtolist === "function") { additemtolist(finalOut); }
				}
				function omniAddToListPers(elem, prodId, skuId, qty, personalizationMessageCart){
					   var totalPrice = $(elem).closest('.cartRow').find('.omniTotalPriceHidden').val();
					   var persDone = '';
					   if(personalizationMessageCart){
						var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId+'|eVar54='+personalizationMessageCart;
						persDone = true;
					   }
					   else{
						var finalOut= ';'+prodId+';;;event38='+qty+'|event39='+totalPrice+';eVar30='+skuId;
					   }

					   if (typeof additemtolist === "function") { additemtolist(finalOut,persDone); }
					}
			</script>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<c:set var="productIds" scope="request"/>
			<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
				<dsp:param name="order" bean="ShoppingCart.current"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="productIds" param="commerceItemList" />
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="IsEmpty">
			    <dsp:param bean="Profile.activePromotions" name="value"/>
			    <dsp:oparam name="true">
					<c:set var="promoApplied" value="true"/>
			    </dsp:oparam>
			    <dsp:oparam name="false">
					<c:set var="promoApplied" value="false"/>
			    </dsp:oparam>
			</dsp:droplet>
			<script type="text/javascript">
				var event = "scView";
				if('${promoApplied}' === "false"){
					event = event+";event11";
				}
				if(typeof s !=='undefined') {
					s.channel='Check Out';
					s.pageName='Check Out>Full Cart';
					s.prop1='Check Out';
					s.prop2='Check Out';
					s.prop3='Check Out';
					s.events=event;
					s.products = '${productIds}';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
