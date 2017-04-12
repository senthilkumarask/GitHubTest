<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:page>
 	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
 	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
 	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:droplet name="BBBPaymentGroupDroplet">
      <dsp:param param="order" name="order"/>
      <dsp:param name="serviceType" value="GiftCardDetailService"/>
      <dsp:oparam name="output">
      		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty"> 
			  			<dsp:param name="value" param="giftcards"/>
			  			<dsp:oparam name="false">
							 <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left" style="width: 280px; margin-bottom:10px;">
								<tbody>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="giftcards" />
										<dsp:param name="elementName" value="giftcard" />
										<dsp:oparam name="output">
										<tr>
											<td valign="top"><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></td>
											<td valign="top" align="right">
												<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
												<bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}
											</td>
										</tr>
										<tr>
											<td valign="top"><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></td>
											<td valign="top" align="right">
												<dsp:droplet name="CurrencyFormatter">
														    <dsp:param name="currency" param="giftcard.amount"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
												</dsp:droplet>
											</td>
										</tr>
										</dsp:oparam>
									</dsp:droplet>
								</tbody>
							</table>
						</dsp:oparam>
			</dsp:droplet>     	
      </dsp:oparam>
	</dsp:droplet>
</dsp:page>