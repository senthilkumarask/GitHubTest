<dsp:page>
<dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalShippingCheckoutDroplet" />
<dsp:importbean bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler" />
 <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

 <dsp:droplet name="InternationalShippingCheckoutDroplet">
 				<dsp:param name="IPaddress" value="${applicationId}" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="VO" param="allContextList" />
				<dsp:getvalueof var="countryCode" param="countryCode" />
				<dsp:getvalueof var="currencyCode" param="currencyCode" />
				<dsp:getvalueof var="countryName" param="countryName" />
				<dsp:getvalueof var="currencyName" param="currencyName" />
				<dsp:getvalueof var="currencyMap" param="allCurrencyMap"/>
				</dsp:oparam>
</dsp:droplet>
<input type="hidden" id="countryCookie" value="${countryCode}"/>
<input type="hidden" id="currencyCookie" value="${currencyCode}"/>
<div id="intlShippingModal" tabindex="0">
	<dsp:form id="intlShipping" iclass="form" name="intlShipping" method="post" action="">



			<div class="fl intlShippingDropDownSize" for="selIntlCountry">
				<dsp:select name="selIntlCountry" id="selIntlCountry"    bean="InternationalShipFormHandler.userSelectedCountry">
				 <dsp:tagAttribute name="aria-required" value="true"/>
				  <dsp:tagAttribute name="class" value="uniform"/>
				  <dsp:tagAttribute name="aria-hidden" value="false"/>
				<option value=""><bbbl:label key="lbl_intl_shipping_modal_select_country" language="${pageContext.request.locale.language}" /></option> 
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param value="${VO}" name="array" />
					<dsp:oparam name="output">
								<dsp:getvalueof var="Code" param="element.shippingLocation.countryCode" />
								<dsp:getvalueof var="Name" param="element.shippingLocation.countryName" />
								<dsp:getvalueof var="currCode" param="element.shoppingCurrency.currencyCode" />
								
								<c:choose>
								<c:when test="${Code eq countryCode}">
									<c:if test="${currencyCode eq null || empty currencyCode || empty currCode}">
										<c:set var="currencyCode" value="USD"/>
										</c:if>	
									<c:set var="currencyEnable" value="true"/>
									<dsp:option value="${Code}"  id="${currCode}" selected="true">${Name}</dsp:option>
								</c:when>
								<c:otherwise>
									
								
								<dsp:option value="${Code}" id="${currCode}">${Name}</dsp:option>
								</c:otherwise>
								</c:choose>
					</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</div>

			<div class="fl intlShippingDropDownSize selIntlCurrency marTop_15">
				<dsp:select name="selIntlCurrency" id="selIntlCurrency"    bean="InternationalShipFormHandler.userSelectedCurrency">
				 <dsp:tagAttribute name="aria-required" value="true"/>
				 <dsp:tagAttribute name="aria-hidden" value="false"/>
                 <dsp:tagAttribute name="class" value="uniform"/>
				 
					<option value=""><bbbl:label key="lbl_intl_shipping_modal_select_currency" language="${pageContext.request.locale.language}" /></option> 
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param value="${currencyMap}" name="array" />
					<dsp:oparam name="output">
								<dsp:getvalueof var="mCurrencyName" param="key" />
								<dsp:getvalueof var="mCurrencyCode" param="element" />
								<c:choose>
								<c:when test="${countryCode ne null && not empty countryCode &&  currencyEnable eq 'true' && mCurrencyCode eq currencyCode}">
									
									<dsp:option value="${mCurrencyCode}" selected="true">${mCurrencyName}</dsp:option>
								</c:when>
								<c:otherwise>
									<dsp:option value="${mCurrencyCode}">${mCurrencyName}</dsp:option>
								</c:otherwise>
								</c:choose>
					</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</div>

		<div class="clearfix  marTop_15">
			 <div class="button_shipto fl">
			  <c:set var="updateButton"><bbbl:label key="lbl_intl_shipping_modal_shipping_update" language="${language}"/></c:set>
			  <c:set var="shipToUSAButton"><bbbl:label key="lbl_intl_shipto_usa" language="${language}"/></c:set>
				<input name="internationalOrderType" type="hidden" value="internationalOrder"/>
				<dsp:input  type="submit" value="${updateButton}" bean="InternationalShipFormHandler.updateUserContext" >
				 <dsp:tagAttribute name="role" value="button"/>
				</dsp:input>

			</div>
			 <div class="interShipOr fl">
				<bbbl:label key="lbl_intl_context_or" language="${pageContext.request.locale.language}" />
			</div>
		</div>
		</dsp:form>
		<dsp:form id="intlShippingReset" iclass="form"  name="intlShippingReset" method="post" action="">
			<div class="fr shipToUs">
				<dsp:input  type="hidden" bean="InternationalShipFormHandler.userSelectedCurrency" beanvalue="InternationalShipFormHandler.usCurrencyCode" />
				<dsp:input  type="hidden" bean="InternationalShipFormHandler.userSelectedCountry" beanvalue="InternationalShipFormHandler.usCountryCode" />
				<dsp:input  type="submit" value="${shipToUSAButton}" bean="InternationalShipFormHandler.updateUserContext" >
					<dsp:tagAttribute name="role" value="button"/>
				</dsp:input>
			<span class="flag flag_modal"></span>
			</div>
			
		</dsp:form>
	<div>
		<p class="bold"><bbbl:label key="lbl_intl_shipping_modal_note" language="${pageContext.request.locale.language}" /></p>
		<p><bbbl:textArea key="txt_intl_shipping_modal_checkout_logout" language="${pageContext.request.locale.language}" /></p>
		<p><bbbl:textArea key="txt_intl_shipping_modal_APO/FPO" language="${pageContext.request.locale.language}" /></p>
		<p><bbbl:textArea key="txt_intl_shipping_modal_learn_more" language="${pageContext.request.locale.language}" /></p>
	
	</div>
</div>

	<script type="text/javascript">
		
	function internationalLoadOmniture() {
	if (typeof s !== "undefined") {
			s.prop1 = s.prop2 = s.prop3 = s.prop4 = s.prop5 = s.prop6 =s.prop7 = s.prop8 = s.prop25 ='';
			s.eVar1 = s.eVar2 = s.eVar3 = s.eVar4 = s.eVar5 = s.eVar6 =s.eVar7 = s.eVar8 = s.eVar47 ='';
			s.pageName  = 'My Account>Select your country';
			s.channel = s.prop1 = s.prop2 = s.prop3 = "My Account";
			s.prop4 = s.prop5 = "";
			s.events ='';
			s.products ='';

			s.t();
			s.linkTrackVars="None";
			s.linkTrackEvents="None";

		}	
	}
	
	internationalLoadOmniture();
	</script>

</dsp:page>