<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>

<div class="sddMainWrapper" id="sddMainWrapper">
	<div id="sameDayDeliveryWrapper" class="sddwrapper" style='background: #6a6a6a url(<bbbc:config key="backgroundImage" configName="SameDayDeliveryKeys"/>) no-repeat 0px 0;background-size: 100% 100%;'> 
		<div id="clock-wrap">
			<div class="clock-img">
				<%-- keeping "alt" blank as this is a decorative image --%>
				<img alt="" src="<bbbc:config key='landClockImgDsk' configName='SameDayDeliveryKeys'/>" />
			</div>
		</div>

		<dsp:getvalueof bean="SessionBean.currentZipcodeVO"
			var="currentZipCodeVo" />
		<dsp:getvalueof value="${currentZipCodeVo.zipcode}" var="zipCode" />
		<dsp:getvalueof value="${currentZipCodeVo.minShipFee}"
			var="minShipFee" />
		<dsp:getvalueof value="${currentZipCodeVo.sddEligibility}"
			var="sddEligibility" />
		<dsp:getvalueof value="${currentZipCodeVo.displayCutoffTime}"
			var="displayCutoffTime" />

		<h1 class="same-day-delivery-header"><bbbl:label key="lbl_same_day_delivery_heading" language="${pageContext.request.locale.language}" /></h1>

		<%-- If sdd is not eligible, then we will display the generic header message  --%>
		<div class="confirm-shipping-code-text">
			<c:choose>
				<c:when test="${sddEligibility}">
					<jsp:useBean id="placeHolderDisplayTime" class="java.util.HashMap"
						scope="request" />
					<c:set target="${placeHolderDisplayTime}" property="displayTime"
						value="${displayCutoffTime}" />
					<bbbt:textArea key="txt_same_day_delivery_subheading"
						placeHolderMap="${placeHolderDisplayTime}"
						language="${pageContext.request.locale.language}" />
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_sdd_generic_subheading"
						language="${pageContext.request.locale.language}" />
				</c:otherwise>
			</c:choose>
		</div>

		<div id="centerControlWrapper">
			<form id="formSameDayDelivery" class="form" name="formSameDayDelivery" method="post" action="">
				<div class="textCenter sddajaxziploader hidden">
                	<img width="20" height="20" alt="loading data" class="loader" src="/_assets/global/images/widgets/small_loader_2.gif">
                </div>
				<div class="input textBox">
					<label for="sddZip" id="lblSddZip" class="txtOffScreen"><bbbl:label key="lbl_enter_zip_code" language="${pageContext.request.locale.language}" /></label>
					<input type="text" id="sddZip" maxlength="10" name="sddZip" value="${zipCode}" />
				</div>
				<div class="showOnError hidden" id="lblerrSddNotAvailable"><bbbe:error key="err_sdd_validation" language="${pageContext.request.locale.language}" /></div>
				<div class="button_sdd">
					<label for="confirmZipSdd" class="txtOffScreen"><bbbl:label key="lbl_sdd_confirm_zip_code_verbose" language="${pageContext.request.locale.language}" /></label>
					<input type="submit" id="confirmZipSdd" value="<bbbl:label key="lbl_sdd_confirm_zip_code" language="${pageContext.request.locale.language}"/>" />
				</div>
				<input type="hidden" name="zipCode" value="${zipCode}" />
			</form>

			<div class="orBar">
				<%-- keeping "alt" blank as this is a decorative image --%>
				<img src="../../_assets/global/images/sdd/orHalfLine.png" class="orHalfBar" alt="" />
				<span class="orsdd"><bbbl:label key="lbl_sdd_or" language="${pageContext.request.locale.language}" /></span>
				<%-- keeping "alt" blank as this is a decorative image --%>
				<img src="../../_assets/global/images/sdd/orHalfLine.png" class="orHalfBar" alt="" />
			</div>

			<div class="gray-btn">
				<a href="/store" class="continueShopSDD" role="button"><bbbl:label	key="lbl_sdd_continue_shopping"	language="${pageContext.request.locale.language}" /></a>
			</div>

		</div>
		
		<dsp:droplet name="CurrencyFormatter">
			<dsp:param name="currency" value="${minShipFee}"/>
			<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="formattedShipFee" param="formattedCurrency" ></dsp:getvalueof>
				<c:if test="${fn:contains(formatCurrency, '($0.00)')}">
					<c:set var="formattedShipFee" value="$0.00"></c:set>
				</c:if>
			</dsp:oparam>
		</dsp:droplet>

		<%-- If sdd is not eligible, then we will display the generic footer message  --%>
		<div class="footerTextSDD">
			<dsp:setvalue bean="SessionBean.showSDD" value="false" />
			<c:choose>
				<c:when test="${sddEligibility}">
					<jsp:useBean id="placeHolderMapShipFee" class="java.util.HashMap"
						scope="request" />
					<c:set target="${placeHolderMapShipFee}" property="minShipFee"
						value="${formattedShipFee}" />
					<bbbt:textArea key="txt_sdd_footer_msg"
						placeHolderMap="${placeHolderMapShipFee}"
						language="${pageContext.request.locale.language}" />
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_sdd_generic_msg"
						language="${pageContext.request.locale.language}" />
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
<script type="text/javascript">
		if (typeof s !== 'undefined') {
		s.pageName ='My Account>Same Day Delivery Landing Page';
		s.channel = 'My Account';
		s.prop1 = 'My Account';
		s.prop2 = 'My Account';
		s.prop3 = 'My Account';
		s.prop6='${pageContext.request.serverName}'; 
		s.eVar9='${pageContext.request.serverName}';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
		}
</script>