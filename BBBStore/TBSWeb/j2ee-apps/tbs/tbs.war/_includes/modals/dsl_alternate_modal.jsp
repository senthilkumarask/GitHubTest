<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="skuId" value="${param.skuId}"></c:set>
<c:set var="altNumber" value="${param.altNumber}"></c:set>
<c:set var="productId" value="${param.productId}"></c:set>
<c:set var="parentProdId" value="${param.altNumber}"></c:set>
<c:set var="fromComparisonPage" value="${param.fromComparisonPage}"></c:set>
<c:set var="qty" value="${param.qty}"></c:set>
<c:set var="prodSize" value="${param.prodSize}"></c:set>
<c:set var="prodColor" value="${param.prodColor}"></c:set>
<c:set var="itemPrice" value="${param.itemPrice}"></c:set>
<c:set var="returnURL" value="${param.returnURL}"></c:set>
<c:set var="prodFinish" value="${param.prodFinish}"></c:set>
<c:set var="refNum" value="${param.refNum}"></c:set>
<c:set var="customizationRequiredFlag" value="${param.customizationRequiredFlag}"></c:set>
<c:set var="personalizationType" value="${param.personalizationType}"></c:set>
<c:set var="ltlFlag" value="${param.ltlFlag}"></c:set>
<c:set var="registryName" value="${param.registryName}"></c:set>
<c:set var="registryId" value="${param.registryId}"></c:set>
<c:set var="noDslOption" value="${param.noDslOption}"></c:set>
<c:set var="ltlShipMethod" value="${param.ltlShipMethod}"></c:set>
<c:set var="updateDslFromModal" value="${param.updateLTLDslFromModal}"></c:set>
<c:set var="addCartDslModal" value="${param.addCartDslModal}"></c:set>
<c:set var="bts" value="${param.bts}"></c:set>
<c:set var="isSfl" value="${param.isSfl}"></c:set>
<c:set var="storeId" value="${param.storeId}"></c:set>

<c:if test="${currentSiteId ne BedBathCanadaSite}">
	<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
		<dsp:param name="skuId" value="${skuId}" />
		<dsp:param name="siteId" value="${appid}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList"/>
		</dsp:oparam>
	</dsp:droplet>

	<section class="dslAlternateModal row">
		<input type="hidden" value="${shipMethodVOList == null}" id="shipMethodVO" />
		<input type="hidden" value="${addCartDslModal}" id="fromCart" />
		<%--<c:choose>
		<c:when test="${shipMethodVOList == null }">
		
		<h3 class="modalTitleContent"><bbbt:textArea key="txt_no_dsl_available" language="${pageContext.request.locale.language}" /></h3>
		</c:when>
		<c:otherwise>--%>
		<c:choose>
			<c:when test="${isSfl eq 'true'}">
				<h3 class="modalTitle"><bbbl:label key="lbl_dsl_alt_modal_head_sfl" language="${pageContext.request.locale.language}" /></h3>
			</c:when>
			<c:when test="${addCartDslModal eq 'true'}">
				<h3 class="modalTitle"><bbbt:textArea key="lbl_dsl_alt_modal_head_cart" language="${pageContext.request.locale.language}" /></h3>
			</c:when>
			<c:otherwise>
				<h3 class="modalTitle"><bbbl:label key="lbl_dsl_alt_modal_head" language="${pageContext.request.locale.language}" /></h3>
			</c:otherwise>
		</c:choose>

		<form id="dslModalForm" class="listDataItemsWrap registryDataItemsWrap">
			<input type="hidden" class="addItemToList addItemToRegis" value="${qty}" name="qty" data-change-store-submit="qty" />
			<input type="hidden" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
			<c:if test="${not empty skuId}">
				<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuId}" name="skuId"/>
			</c:if>
			<input type="hidden" name="price" value="${itemPrice}" class="addItemToList addItemToRegis" />
			<input type="hidden" name="parentProdId" value="${productId}" class="addItemToList addItemToRegis" />
			<input type="hidden" value="${fromComparisonPage}" class="addItemToList addItemToRegis" name="fromComparisonPage"/>
			<input type="hidden" value="${altNumber}" class="addItemToList addItemToRegis" id="${registryId}" name="altNumber"/>
			<input type="hidden" value="${prodSize}" class="addItemToList addItemToRegis" name="prodSize"/>
			<input type="hidden" value="${prodColor}" class="addItemToList addItemToRegis" name="prodColor"/>
			<input type="hidden" value="${prodFinish}" class="addItemToList addItemToRegis" name="prodFinish"/>
			<input type="hidden" value="${refNum}" class="addItemToList addItemToRegis" name="refNum"/>
			<input type="hidden" value="${returnURL}" class="addItemToList addItemToRegis" name="regReturnUrl"/>
			<input type="hidden" value="${refNum}" class="addItemToList addItemToRegis" name="refNum"/>
			<input type="hidden" value="${customizationRequiredFlag}" class="addItemToList addItemToRegis" name="customizationRequiredFlag"/>
			<input type="hidden" value="${personalizationType}" class="addItemToList addItemToRegis" name="personalizationType"/>
			<input type="hidden" value="${registryName}" class="addItemToList addItemToRegis" name="registryName"/>
			<input type="hidden" value="${registryId}" class="addItemToList addItemToRegis" name="registryId"/>
			<input type="hidden" value="${ltlFlag}" name="isLtlItem"/>
			<input type="hidden" value="${bts}" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts">
			<input type="hidden" data-change-store-storeid="storeId" value="${storeId}" name="storeId" class="addToCartSubmitData">

			<c:if test="${noDslOption eq 'true'}">
				<div id="ltlDeliveryMethodWrapper" class="clearfix large-6 columns no-padding">	
					<a href="/tbs/static/ltlDeliveryInfo" role="link" class="fl popupShipping"> <img class="marBottom_5" width="20" height="15" src="/_assets/global/images/LTL/truck.png" alt="Truck Options"> <span><bbbl:label key='ltl_truck_delivery_options' language="${pageContext.request.locale.language}" /></span><img class="quesMark marLeft_5" width="15" height="15" src="/_assets/global/images/LTL/quesMark.png" alt="Question Mark"></a>
					
					<a class="secondary radius button dropdown selServiceLevel small-12" data-dropdown="selServiceLevel" href="#"><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /><span></span></a>
					<ul class="f-dropdown small selServiceUl" data-dropdown-content data-ltlflag="true" id="selServiceLevel" name="selServiceLevel">
					<!-- <select data-ltlflag="true" aria-required="true" class="customSelectBoxCollection" id="selServiceLevel" name="selServiceLevel"> --> 
						<%-- <option value=""><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>"/></option> --%>
						<li class="selected"><a href="#"><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /></a></li>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param value="${shipMethodVOList}" name="array" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
								<dsp:getvalueof var="shipMethodName" param="element.shipMethodDescription"/>
								<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge"/>
								<c:set var="deliverySurcharge" value="" />
								<c:if test="${deliverySurchargeval gt 0}">
									<c:set var="deliverySurcharge"><dsp:valueof  converter="currency" value="${deliverySurchargeval}" /></c:set>
								</c:if>
								<c:if test="${deliverySurchargeval eq 0}">
								<c:set var="deliverySurcharge">Free</c:set>
							    </c:if>
								<%-- <option value="${shipMethodId}" class="enable">${shipMethodName} - ${deliverySurcharge}</option> --%>
								<li><a href="#" data-method_id="${shipMethodId}" data-method_name="${shipMethodName}" data-surcharge="${deliverySurcharge}">${shipMethodName} - ${deliverySurcharge}</a></li>
							</dsp:oparam>
						</dsp:droplet>
					<!-- </select> -->
					</ul>
				</div>
			</c:if>
			<input type="hidden" value="${ltlShipMethod}" class="addItemToList addItemToRegis" name="selServiceLevel">
			<c:if test="${altNumber eq '' or altNumber eq 'undefined'}">
				<div>
					<label id="lblalternatePhone" for="alternatePhone"><bbbl:label key='lbl_alternate_no' language="${pageContext.request.locale.language}" /></label>

					<input id="alternatePhone" role="textbox" type="text" value="" name="alternatePhone" class="alternatePhone phoneField required escapeHTMLTag" maxlength="10" aria-required="false" aria-labelledby="lblalternatePhone erroralternatePhone" placeholder="<bbbl:label key="lbl_alternate_number" language="${pageContext.request.locale.language}"></bbbl:label>">
				</div>
			</c:if>

			<c:choose>
				<c:when test="${addCartDslModal eq 'true'}">
					<div class="addToCart large-6">					
							<input type="button" role="button" name="btnAddToCartDsl" value="Add to cart" class="button dslModalAddToCart">
					</div>
				</c:when>
				<c:otherwise>
					<div class="addToRegistry large-6">
						<c:choose>
						<c:when test="${isSfl eq 'true'}">
							<input type="button" value="Save for Later"  role="button" aria-pressed="false" class="button dslModalSfl expand" />
					    </c:when>
						<c:when test="${updateDslFromModal eq 'true'}">
							<c:set var="updateButton"><bbbl:label key='lbl_update_registry' language="${pageContext.request.locale.language}" /></c:set>
							<input class="dslModalUpdateRegistry medium button btnPrimary centerTxt" name="dslModalUpdateRegistry" type="button" role="button" value="${updateButton}">
						</c:when>
						<c:otherwise>
							<input class="dslModalAddToRegistry expand button btnPrimary centerTxt" name="dslModalAddToRegistry" type="button" role="button" value="Add To Registry">
						</c:otherwise>
						</c:choose>
					</div>
				</c:otherwise>
			</c:choose>
		</form>
	</section>

	<script>
		if (typeof s !== "undefined") {
			var alternateNum = ${altNumber eq '' or altNumber eq 'undefined'},
				dslOptions = ${noDslOption eq 'true'};

			if (dslOptions) {
				if (alternateNum) {
					s.pageName = "My Account > Alternate Phone And Delivery Option";
				} else {
					s.pageName = "My Account > Delivery Option";
				}
			} else {
				if (alternateNum) {
					s.pageName = "My Account > Alternate Phone";
				}
			}

			s.channel = s.prop1 = s.prop2 = s.prop3 = "My Account";
			s.prop4 = s.prop5 = "";
			s.events = '';
			s.products = '';
			s.eVar23 = s.eVar24 = s.eVar46 = '';

			s.t();
			s.linkTrackVars = "None";
			s.linkTrackEvents = "None";
		}
	</script>
	<%--</c:otherwise>
	</c:choose>--%>
</c:if>