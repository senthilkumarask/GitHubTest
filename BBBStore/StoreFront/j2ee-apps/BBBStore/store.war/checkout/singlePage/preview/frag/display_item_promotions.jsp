<dsp:page>
 	<dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
    <ul class="prodDeliveryInfo couponInfoWrapper marTop_20">
		<c:if test="${not empty adjustmentsList}">
			<li class="couponInfo">
				<p class="upperCase"><bbbl:label key="lbl_spc_cartdetail_couponapplied" language="<c:out param='${language}'/>"/></p>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="priceInfoVO.adjustmentsList" />
					<dsp:param name="elementName" value="adjustments" />
					<dsp:oparam name="output">
						<p class="smallText noMar"><dsp:valueof param="adjustments.pricingModel.displayName"/></p>																				
					</dsp:oparam>
				</dsp:droplet>
			</li>
		</c:if>
	</ul>
</dsp:page>

