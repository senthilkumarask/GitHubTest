<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:getvalueof vartype="java.lang.String" var="comID" param="commerceItem.id"/>
	<dsp:droplet name="ForEach">
	<dsp:param name="array" param="promoExclusionMap.${comID}" />
	<dsp:param name="elementName" value="excludedPromotion" />
		<dsp:oparam name="outputStart">
			<li class="smallText">
				<bbbl:label key="lbl_exclusions_opening_parenthesis" language="${language}"/>
				<bbbl:label key="lbl_exclusions_msg" language="${language}"/>
			</li>		
		</dsp:oparam>																						
		<dsp:oparam name="output">
			<li class="smallText"><dsp:valueof param="excludedPromotion.displayName"/></li>
            <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="excludedPromotion.media.tandc.data"/>
                <dsp:oparam name="false">
        			<li class="smallText">
        			<c:set var="title"><bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/></c:set>
        				<dsp:a iclass="popup" page="/checkout/coupons/exclusions.jsp" title='${title}'>
        					<dsp:param name="item" param="excludedPromotion.bbbcoupons"/>
        	            	<bbbl:label key="lbl_reviewpage_exclusions_title" language="${language}"/>
        	            </dsp:a>
                    </li>	
                </dsp:oparam>
            </dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			<li class="smallText">
				<bbbl:label key="lbl_exclusions_closing_parenthesis" language="${language}"/>
			</li>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
