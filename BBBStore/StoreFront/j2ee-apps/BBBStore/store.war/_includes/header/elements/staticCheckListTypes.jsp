<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler"/>
 <dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
		<dsp:setvalue bean="InteractiveChecklistFormHandler.fetchStaticCheckListTypes" value="true" />
 <c:set var="interactiveChecklistCacheTimeout"><bbbc:config key="interactiveChecklistCacheTimeout" configName="HTMLCacheKeys" /></c:set>
 <dsp:getvalueof var="staticCheckListType" bean="InteractiveChecklistFormHandler.staticCheckListTypes" /> 
 <dsp:droplet name="/atg/dynamo/droplet/Cache">
              <dsp:param name="key" value="staticCheckListTypes_${currentSiteId}" />
                <dsp:param name="cacheCheckSeconds" value="${interactiveChecklistCacheTimeout}"/>
                <dsp:oparam name="output">
			<json:object>
				<json:property escapeXml="false" name="staticCheckListTypes">
					${staticCheckListType}
				</json:property>
			</json:object>	
		</dsp:oparam>		
          </dsp:droplet>
</dsp:page>