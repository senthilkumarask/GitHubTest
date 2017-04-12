<dsp:page>
<%--BBBP-5940 start --%>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="entryCd" param="item"/>
<div title="<bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/>">
	   <dsp:droplet name="/atg/targeting/RepositoryLookup">
    	<dsp:param name="repository" bean="/com/bbb/commerce/promotion/CouponRuleRepository"/>
    	<dsp:param name="itemDescriptor" value="exclusionText"/>
    	<dsp:param name="id" value="${entryCd}:${currentSiteId}"/>
    	<dsp:oparam name="output">  
					<dsp:valueof param="element.exclusionText"  valueishtml="true"></dsp:valueof>
		</dsp:oparam>
	  </dsp:droplet>
</div>
<dsp:getvalueof var="exclusion" param="exclusion" />
<c:if test="${!empty exclusion}">
	<div title="<bbbl:label key="lbl_coupon_exclusions_title" language="${language}"/>">
		<dsp:valueof param="exclusion" valueishtml="true"></dsp:valueof>
	</div>
</c:if>
</dsp:page>