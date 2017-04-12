<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  multiShipping.jsp
 *
 *  DESCRIPTION: page for collection multi shipping address for an order
 *
 *  HISTORY:
 *  Dec 27, 2011  Initial version

--%>
<dsp:page>
 <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
 <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
 <dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
 <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>
 <dsp:getvalueof var= "errorList" bean = "PayPalSessionBean.errorList"/>
 <c:set var="errorSize" value="${fn:length(errorList)}"/>
 <bbb:pageContainer index="false" follow="false" >

     <jsp:attribute name="headerRenderer">
       <dsp:include page="/checkout/checkout_header.jsp" flush="true">
         <dsp:param name="step" value="shipping"/>
         <dsp:param name="link" value="multiple"/>
         <dsp:param name="pageId" value="6"/>
       </dsp:include>
     </jsp:attribute>
     <jsp:attribute name="section">checkout</jsp:attribute>
     <jsp:attribute name="pageWrapper">billing shippingWrapper useMapQuest multiShippingPage useStoreLocator useGoogleAddress</jsp:attribute>
    <jsp:attribute name="PageType">MultiShipping</jsp:attribute>
     <jsp:body>
     	 <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
     	 <c:choose>
				<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
				<c:set var="iclassValue" value=""/>
				</c:when>
				<c:otherwise>
				<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
		</c:choose>
     <c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	 <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	 <c:set var="errMessageShown" value="false" scope="page"/>
      <div id="content" class="container_12 clearfix" role="main">
        <div class="clearfix marTop_20">
         <div id="leftCol" class="grid_8 highlightSection">
         	<dsp:getvalueof var="commerceItems" bean="ShoppingCart.current.commerceItems"/>
         	<c:if test="${fn:length(commerceItems) != 0}" >

				<%-- Defect ID : BBBR-441 - If paypal error is there in session then dont show form exception : It displays the error twice--%>
				<c:if test="${errorSize eq 0}">
					<dsp:include page="/global/gadgets/errorMessage.jsp" />
				</c:if>

				<dsp:getvalueof bean="BBBShippingGroupFormhandler.formExceptions" id="ShipFormExceptions">
				  <c:if test="${not empty ShipFormExceptions}" >
							<c:set var="errMessageShown" value="true" scope="page" /></c:if>
				</dsp:getvalueof>
				<dsp:form action="#" name="form" id="formShippingMultipleLocation" method="post">
				<input type="hidden" name="cisiShipGroupName" value="" />
					<ul class="gridItemWrapper noMarTop">
						<dsp:include page="frag/multi_shipping_line_item.jsp" />
					</ul>

					<c:choose>
						<c:when test="${empty sessionScope.giftsAreIncludedInOrder}">
							<c:set var="showCheckbox" value="true"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="showCheckbox" value="${sessionScope.giftsAreIncludedInOrder}"></c:set>
						</c:otherwise>
					</c:choose>

					<dsp:getvalueof var="displayGiftWrap" bean ="ShoppingCart.current.bopusOrder" />
					<c:if test="${not displayGiftWrap or containsShipOnline}">
						<div class="checkboxItem input clearfix noBorder noPad">
							<div class="checkbox">
								<dsp:input type="checkbox" checked="${showCheckbox}" value="true" name="orderIncludesGifts" id="orderIncludesGifts" bean="BBBShippingGroupFormhandler.orderIncludesGifts" >
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblorderIncludesGifts"/>
                                </dsp:input>
							</div>
							<div class="label">
								<label id="lblorderIncludesGifts" for="orderIncludesGifts">
									<strong><bbbl:label key="lbl_gift_order_include_gifts" language="<c:out param='${language}'/>"/></strong> <bbbl:label key="lbl_gift_packing_slip_msg" language="<c:out param='${language}'/>"/>
								</label>
							</div>
						</div>
					</c:if>
					<c:set var="lbl_button_next" scope="page">
	                               <bbbl:label
									key="lbl_spc_shipping_button_next"
									language="${pageContext.request.locale.language}" />
                        </c:set>

					<dsp:getvalueof id="disableButton" bean="BBBShippingGroupFormhandler.multiShipOutOfStock"/>

					<c:choose>
						<c:when test="${disableButton eq 'yes'}">
							<div class="clearfix error cb padTop_20">
							<bbbl:label key="lbl_spc_cart_outofstockmessage" language="${pageContext.request.locale.language}" />
						</div>
						<div class="button button_active marTop_20 button_disabled">
						</c:when>
						<c:otherwise>
							<div class="button button_active marTop_20">
						</c:otherwise>
					</c:choose>

						<c:set var="productIds" scope="request"/>
                        <dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
							<dsp:param name="order" bean="ShoppingCart.current"/>
							 <dsp:oparam name="output">
								<dsp:getvalueof var="productIds" param="commerceItemList" />
					        </dsp:oparam>
					   </dsp:droplet>

                        <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.cisiIndex" value="-1" name="shipToMultiplePeople_cisiIndex" />
                        <input type="hidden" value="" name="shipToMultiplePeople_shippingGr" />
                        <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleSuccessURL" value="${contextPath}/checkout/shipping/multiShipping.jsp" />
                        <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.shipToMultiplePeopleErrorURL" value="${contextPath}/checkout/shipping/multiShipping.jsp" />
                        <dsp:input type="submit" bean="BBBShippingGroupFormhandler.shipToMultiplePeople" value="true" iclass="hidden" id="shipToMultiplePeople" />
						<c:choose>
							<c:when test="${disableButton eq 'yes'}">
								<dsp:input type="submit" disabled="true" id="submitShippingMultipleLocationBtn" value="${lbl_button_next}" bean="BBBShippingGroupFormhandler.multipleShipping" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="submitShippingMultipleLocationBtn"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                           </c:when>
							<c:otherwise>
								<dsp:input type="submit" id="submitShippingMultipleLocationBtn" value="${lbl_button_next}" bean="BBBShippingGroupFormhandler.multipleShipping" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="submitShippingMultipleLocationBtn"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
							</c:otherwise>
						</c:choose>
						<dsp:input type="submit" iclass="hidden" id="saveBaseFormChanges" value="true" bean="BBBShippingGroupFormhandler.saveNewAddress" />
					</div>
					</dsp:form>
				</c:if>

			</div>
            <dsp:include page="/checkout/order_summary_frag.jsp">
				<dsp:param name="displayTax" value="false"/>
				<dsp:param name="displayShippingDisclaimer" value="true"/>
			</dsp:include>
      </div>

      </div>
      <div class="hidden">
				<div id="addNewAddressDialogWrapper" class="addNewAddressDialogWrapper">

					<dsp:form name="addNewAddressFrm" id="addNewAddressFrm" action="#" method="post">
						<dsp:input bean="BBBShippingGroupFormhandler.address.country" value="${defaultCountry}" type="hidden"/>
						<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.addNewAddressURL" value="/store/checkout/shipping/frag/newAdd_MultiShip.jsp" />
						<dsp:input type="hidden" name="cisiIndex" bean="BBBShippingGroupFormhandler.cisiIndex" value="-1" iclass="addNewAddressIndexTarget" />
						<div class="container_6 clearfix">
							<div class="grid_3 alpha clearfix">

								<div class="input">
									<div class="label">
										<label id="lblcheckoutfirstName" for="checkoutfirstName">
											<bbbl:label key="lbl_spc_shipping_new_first_name" language="<c:out param='${language}'/>"/>
											<span class="required">*</span>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="checkoutfirstName" value="" id="checkoutfirstName" bean="BBBShippingGroupFormhandler.address.firstName" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
                                            </dsp:input>
										</div>
									</div>
								</div>

								<%-- Defect LTL-601 Removing Middle Name --%>
								<%--<div class="input">
	                                <div class="label">
	                                    <label id="lblmidleName" for="midleName">Middle Name </label>
	                                </div>
	                                <div class="text">
	                                    <input type="text" name="midleName" value="" id="midleName" aria-required="false" aria-labelledby="lblmidleName errormidleName" />
	                                </div>
	                            </div> --%>


								<div class="input">
									<div class="label">
										<label id="lblcheckoutlastName" for="checkoutlastName">
											<bbbl:label key="lbl_spc_shipping_new_last_name" language="<c:out param='${language}'/>"/>
											<span class="required">*</span>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="checkoutlastName" value="" id="checkoutlastName" bean="BBBShippingGroupFormhandler.address.lastName" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
                                            </dsp:input>
										</div>
									</div>
								</div>


								<div class="input">
									<div class="label">
										<label id="lblcompany" for="company">
											<bbbl:label key="lbl_spc_shipping_new_company" language="<c:out param='${language}'/>"/>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="company" value="" id="company" iclass="optional" bean="BBBShippingGroupFormhandler.address.companyName" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                                            </dsp:input>
										</div>
									</div>
								</div>

								<div class="input ltlField">
									<div class="label">
										<label id="lblphone" for="basePhone">
											<bbbl:textArea key="txt_ltl_shipping_phone" language="<c:out param='${language}'/>"/>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="basePhoneFull" value="" id="basePhone" bean="BBBShippingGroupFormhandler.address.phoneNumber" maxlength="10" iclass="phoneField required">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblphone errorphone"/>
                                                <dsp:tagAttribute name="role" value="textbox"/>
                                            </dsp:input>

										</div>
									</div>
								</div>

								<div class="input ltlField">
									<div class="label">
										<label id="lblaltphone" for="altphone">
											<bbbl:label key="lbl_spc_shipping_alternate_phone" language="<c:out param='${language}'/>"/>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="alternatePhoneNumber" value="" id="altphone" bean="BBBShippingGroupFormhandler.address.alternatePhoneNumber" maxlength="10" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblaltphone erroraltphone"/>
                                                <dsp:tagAttribute name="role" value="textbox"/>
                                            </dsp:input>
										</div>
									</div>
								</div>

								<div class="input ltlField">
									<div class="label">
										<label id="lblemail" for="email">
											<bbbl:textArea key="txt_ltl_shipping_email" language="<c:out param='${language}'/>"/>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="email" value="" id="email" bean="BBBShippingGroupFormhandler.address.email" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                                 <dsp:tagAttribute name="role" value="textbox"/>
                                            </dsp:input>
										</div>
									</div>
								</div>

	                            <dsp:droplet name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
									<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
									<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="profileAddresses" param="profileAddresses"/>
									</dsp:oparam>
								</dsp:droplet>
	                            <dsp:droplet name="/atg/dynamo/droplet/Switch">
	                                <dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
	                                <dsp:oparam name="false">

											<div class="checkboxItem input clearfix">
												<div class="checkbox">
						                            <c:choose>
														<c:when test="${empty profileAddresses}">
															<dsp:input type="checkbox" name="saveToAccount" id="saveToAccount" bean="BBBShippingGroupFormhandler.saveShippingAddress" checked="true" disabled="true">
                                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                            </dsp:input>
															<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.saveShippingAddress" id="saveShippingAddress" value="true" />
														</c:when>
														<c:otherwise>
															<dsp:input type="checkbox" name="saveToAccount" id="saveToAccount" bean="BBBShippingGroupFormhandler.saveShippingAddress" checked="true" >
                                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                                                 <dsp:tagAttribute name="role" value="checkbox"/>
                                                            </dsp:input>
														</c:otherwise>
													</c:choose>
					                        	</div>
												<div class="label">
													<label id="lblsaveToAccount" for="saveToAccount"> <bbbl:label key="lbl_spc_shipping_new_save_account" language="<c:out param='${language}'/>"/></label>
												</div>
											</div>
									</dsp:oparam>
								</dsp:droplet>
							</div>




							<div class="grid_3 alpha omega clearfix">

								<div class="input">
									<div class="label">
										<label id="lbladdress1" for="address1">
											<bbbl:label key="lbl_spc_shipping_address1" language="<c:out param='${language}'/>"/>
											<span class="required">*</span>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="address1" value="" iclass="${iclassValue}" id="address1" bean="BBBShippingGroupFormhandler.address.address1" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
												<dsp:tagAttribute name="autocomplete" value="off"/>
                                            </dsp:input>
										</div>
									</div>
								</div>



								<div class="input">
									<div class="label">
										<label id="lbladdress2" for="address2">
											<bbbl:label key="lbl_spc_shipping_address2" language="<c:out param='${language}'/>"/>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="address2" value="" iclass="${iclassValue } optional" id="address2" bean="BBBShippingGroupFormhandler.address.address2" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                                            </dsp:input>
										</div>
									</div>
								</div>


								<div class="input">
									<div class="label">
										<label id="lblcityName" for="cityName">
											<bbbl:label key="lbl_spc_shipping_new_city" language="<c:out param='${language}'/>"/>
											<span class="required">*</span>
										</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" name="cityName" value="" id="cityName" bean="BBBShippingGroupFormhandler.address.city" maxlength="25">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                                            </dsp:input>
										</div>
									</div>
								</div>



								<div class="input">
									<div class="label">
										<label id="lblstateName" for="stateName">
										<c:choose>
											 <c:when test="${currentSiteId eq 'BedBathCanada'}">
												  <bbbl:label key="lbl_spc_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" />
											  </c:when>
											  <c:otherwise>
												  <bbbl:label key="lbl_spc_shipping_new_state" language="<c:out param='${language}'/>"/>
											  </c:otherwise>
										  </c:choose>
										  <span class="required">*</span></label>
									</div>
									<dsp:setvalue bean="BBBShippingGroupFormhandler.address.state" value="" />
									<div class="select">
										<dsp:select name="stateName" id="stateName" iclass="shippingStateName" bean="BBBShippingGroupFormhandler.address.state">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
											<c:choose>
												 <c:when test="${currentSiteId eq 'BedBathCanada'}">
													  <option value=""><bbbl:label key="lbl_js_province" language="${pageContext.request.locale.language}"/></option>
												  </c:when>
												  <c:otherwise>
													  <option value=""><bbbl:label key="lbl_spc_shipping_new_selectstate" language="<c:out param='${language}'/>"/></option>
												  </c:otherwise>
										   </c:choose>
											<dsp:droplet name="/com/bbb/commerce/common/BBBPopulateStatesDroplet">
												<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
												<dsp:oparam name="output">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		  												<dsp:param name="array" param="states" />
		  												<dsp:oparam name="output">
															<dsp:param name="state" param="element" />
															<dsp:getvalueof var="stateCode" param="state.stateCode" />
															<dsp:getvalueof var="stateName" param="state.stateName" />
															<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
															<option data-saveToProfile="${showOnBilling}" value="${stateCode}">${stateName}</option>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:select>
									</div>
									<label id="errorstateName" generated="true" class="error"></label>
								</div>


								<div class="input">
									<div class="label">
										<label id="lblzip" for="zip">
											 <c:choose>
												 <c:when test="${currentSiteId eq 'BedBathCanada'}">
													  <bbbl:label key="lbl_spc_subscribe_canadazipcode" language="${pageContext.request.locale.language}" />
												  </c:when>
												  <c:otherwise>
													  <bbbl:label key="lbl_spc_shipping_new_zip" language="<c:out param='${language}'/>"/>
												  </c:otherwise>
										   </c:choose>
											<span class="required">*</span>
										</label>
									</div>
			                        <c:choose>
			                            <c:when test="${defaultCountry ne 'US'}">
			                              <c:set var="zipCodeClass" value="zipCA" scope="page"/>
			                            </c:when>
			                            <c:otherwise>
			                              <c:set var="zipCodeClass" value="zipUS" scope="page"/>
			                            </c:otherwise>
			                        </c:choose>
									<div class="text">
										<div>
											<dsp:input type="text" name="${zipCodeClass}" value="" id="zip" bean="BBBShippingGroupFormhandler.address.postalCode" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
                                            </dsp:input>
										</div>
									</div>
								</div>

							</div>
							<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.ltlCommerceItem" value="false" iclass="ltlCommerceItem" />
						</div>
						<div class="marTop_20 buttonpane clearfix">
	                        <div class="ui-dialog-buttonset">
	                            <div class="button button_active">
	                                <c:set var="lbl_button_create" scope="page">
		                               <bbbl:label
											key="lbl_spc_shipping_button_create"
											language="${pageContext.request.locale.language}" />
	                                </c:set>
									<dsp:input type="submit" value="${lbl_button_create}" name="addNewAddressBtn" id="addNewAddressBtn" bean="BBBShippingGroupFormhandler.addNewAddress" >
                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="addNewAddressBtn"/>
                                        <dsp:tagAttribute name="role" value="button"/>
                                    </dsp:input>
								</div>
	                            <a href="#" class="buttonTextLink close-any-dialog" role="link" ><bbbl:label key="lbl_payament_button_cancel" language="${pageContext.request.locale.language}"/></a>
	                        </div>
	                    </div>
					</dsp:form>
				</div>
			</div>







<dsp:form method="post" action="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi" formid="changeToShipOnline">
		<dsp:input id="onlineSubmit" type="submit" style="display:none" bean="BBBShippingGroupFormhandler.changeToShipOnline"/>
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.successURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/>
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.errorURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/>
		<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.systemErrorURL" value="${contextPath}/checkout/shipping/shipping.jsp?shippingGr=multi"/>

		<dsp:input id="onlineComId" type="hidden" bean="BBBShippingGroupFormhandler.commerceItemId"/>
		<dsp:input id="onlineShipId" type="hidden" bean="BBBShippingGroupFormhandler.oldShippingId"/>
		<dsp:input id="onlineQuantity" type="hidden" bean="BBBShippingGroupFormhandler.newQuantity"/>
	</dsp:form>
		<c:import url="/selfservice/find_in_store.jsp" >
			<c:param name="enableStoreSelection" value="true"/>
			<c:param name="errMessageShown" value="${errMessageShown}"/>
		</c:import>
		<c:import url="/_includes/modules/change_store_form.jsp" >
			<c:param name="action" value="${contextPath}/_includes/modules/cart_store_pickup_form.jsp"/>
		</c:import>
		<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
	<script type="text/javascript">

		function changeToShipOnline(commerceItemId,oldShippingId,newQuantity){
			document.getElementById('onlineComId').value=commerceItemId;
			document.getElementById('onlineShipId').value=oldShippingId;
			document.getElementById('onlineQuantity').value=newQuantity;
			document.getElementById('onlineSubmit').click();
		}
	</script>
	<%--Including the p2p_directions_input.jsp for taking the Start and destination locations input from customer for displaying the directions.--%>

 </jsp:body>


		<jsp:attribute name="footerContent">

				<c:set var="productIds" scope="request"/>
				<dsp:droplet name="/com/bbb/commerce/order/droplet/LTLCustomItemExclusionDroplet">
					<dsp:param name="order" bean="ShoppingCart.current"/>
					 <dsp:oparam name="output">
						<dsp:getvalueof var="productIds" param="commerceItemList" />
			        </dsp:oparam>
			    </dsp:droplet>

			<script type="text/javascript">
			           if(typeof s !=='undefined') {

			       	s.pageName='Check Out>Shipping';
				    s.channel='Check Out';
				    s.prop1='Check Out';
				    s.prop2='Check Out';
				    s.prop3='Check Out';
				    s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					s.events = "scCheckout,event8";
					s.products = '${productIds}';
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}
			</script>
		</jsp:attribute>


</bbb:pageContainer>

</dsp:page>