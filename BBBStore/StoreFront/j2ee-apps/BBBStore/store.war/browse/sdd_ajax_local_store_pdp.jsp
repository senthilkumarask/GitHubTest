<dsp:page>

<dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBDisplaySDDEligibilityOnPDPDroplet">
	 <dsp:param name="skuId" param="skuId" />
	 <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
	 <dsp:param name="inputZip" value="inputZip" />
	 <dsp:oparam name="message">
		<dsp:getvalueof param="message" var="message" />
	 </dsp:oparam>
	 <dsp:oparam name="availableStatus">
		<dsp:getvalueof param="availableStatus" var="availableStatus" />
	 </dsp:oparam>
	  <dsp:oparam name="empty">
		<dsp:getvalueof var="emptyInput" param="emptyInput" scope="page" />
  
	 </dsp:oparam>
 </dsp:droplet>
<json:object>
	<json:property name="success" value="${availableStatus}"/>
		<c:choose>
			<c:when test="${availableStatus  == 'available'}">
				<json:property name="successMessage" escapeXml="false">
					<dsp:valueof value="${message}" valueishtml="true"/>
				</json:property>
			</c:when>
			<c:otherwise>
				<json:property name="errorMessage" escapeXml="false">
					<dsp:valueof value="${message}" valueishtml="true"/>
				</json:property>
			</c:otherwise>
		</c:choose>
</json:object>                                                         

</dsp:page>

