<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<dsp:droplet name="BBBPaymentGroupDroplet">
		<dsp:param param="order" name="order"/>
		<dsp:param name="serviceType" value="GiftCardDetailService"/>
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="giftcards" />
				<dsp:param name="elementName" value="giftcard" />
				<dsp:oparam name="output">
					<h3 class="checkout-title">
						Gift Card Details
					</h3>
					<ul class="address">
						<%-- <li><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></li> --%>
						<li>
							<img class="" src="/_assets/global/images/products/giftcards/giftcard2002.jpg" width="41" height="26" alt="Gift Card">
							<dsp:droplet name="CurrencyFormatter">
								<dsp:param name="currency" param="giftcard.amount"/>
								<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
								<dsp:oparam name="output">
									<dsp:valueof param="formattedCurrency"/>
								</dsp:oparam>
							</dsp:droplet>
						</li>
						<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
						<li><bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}</li>
						<%-- <li><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></li> --%>
					</ul>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
