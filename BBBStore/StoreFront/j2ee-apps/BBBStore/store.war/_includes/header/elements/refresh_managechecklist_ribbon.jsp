<%@page contentType="application/json"%>

<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ManageRegistryChecklistDroplet" />
 <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="guideType" param="guideType"/>
<dsp:getvalueof bean="SessionBean" var="bean"/>
<dsp:page>
              			
	<dsp:droplet name="ManageRegistryChecklistDroplet">
		<dsp:param name="registryId" param="registryId" />
		<dsp:param name="guideType" param="guideType" />
		<dsp:param name="fromAjax" value="${true}" />
		<dsp:param name="sessionBean" value="${bean}" />	
		<dsp:oparam name="output">
		 <dsp:param name="ManageCheckListLink" param="ManageCheckListLink"/>
		 <dsp:param name="registrySummaryVO" param="registrySummaryVO"/>
		   	<c:choose>
					<c:when test="${not empty guideType}">
						<dsp:include page="/_includes/header/elements/refresh_manage_checklist_guide_ribbon.jsp"> 
						</dsp:include>	
					</c:when>
					<c:otherwise>
					  <dsp:include page="/_includes/header/elements/refresh_manage_registrychecklist_ribbon.jsp"> 
						</dsp:include>
					</c:otherwise>
				</c:choose>
		</dsp:oparam>
	</dsp:droplet>
                             </dsp:page>
