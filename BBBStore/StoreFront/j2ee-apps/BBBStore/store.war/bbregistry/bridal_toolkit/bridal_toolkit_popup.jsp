<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/BridalToolkitLinkDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="bridaltoolkit" param="bridaltoolkit"/>
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${transient eq 'false'}">
			<dsp:droplet name="BridalToolkitLinkDroplet">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:oparam name="output">
				<c:set var="sizeValue"><dsp:valueof param="size" /></c:set>
				<c:set var="bridalRegistryVOList"><dsp:valueof param="bridalRegistryVOList" /></c:set>
				<c:choose>
					<c:when test="${sizeValue eq 1}">
						<dsp:droplet name="ForEach">
						<dsp:param name="array" param="bridalRegistryVOList"/>
						<dsp:oparam name="output">
							<dsp:param name="futureRegList" param="element" />
							<dsp:getvalueof var="bridalToolkitToken" param="futureRegList.bridalToolkitToken" />
						</dsp:oparam>
						</dsp:droplet>
						<c:set var="bridaltoolkit">${bridalToolkitToken}</c:set>
						<c:redirect url="${contextPath}/bbregistry/bridal_toolkit/bridal_toolkit.jsp?bridaltoolkit=${bridaltoolkit}"/>
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="bridaltoolkit" param="bridaltoolkit"/>
						<c:choose>
							<c:when test="${bridaltoolkit eq ''}">
								<c:redirect url="${contextPath}/registry/BridalToolkit"/>
							</c:when>
							<c:otherwise>
								<c:redirect url="${contextPath}/bbregistry/bridal_toolkit/bridal_toolkit.jsp?bridaltoolkit=${bridaltoolkit}"/>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</dsp:oparam>
			</dsp:droplet>
		</c:when>
	</c:choose>
</dsp:page>

