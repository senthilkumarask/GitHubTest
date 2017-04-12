<%@page contentType="application/json"%>

<dsp:page>
<c:set var="hideGuide" scope="request"><bbbl:label key="lbl_checklist_hide_guide_link" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="guideBlog" scope="request"><bbbl:label key="lbl_checklist_guide_blog_link" language="${pageContext.request.locale.language}"/></c:set>
<dsp:getvalueof var="ManageCheckListLink" param="ManageCheckListLink" />
<dsp:getvalueof var="selectedGuideVO" param="selectedGuideVO" />
	<json:object escapeXml="false">
		<json:property name="manageRegistryAttribs" escapeXml="false">
				<c:forEach items="${ManageCheckListLink.links}" var="linkVO">
					<c:if test="${linkVO.bannerText eq hideGuide}">
						<li data-guide-type="${selectedGuideVO.guideTypeCode}"><a class="hideGuide" data-guide-type="${selectedGuideVO.guideTypeCode}">${linkVO.bannerText}</a></li>
					</c:if>
					<c:if test="${linkVO.bannerText eq guideBlog}">
						<li><a href="${linkVO.bannerLink}/">${selectedGuideVO.guideDisplayName} ${linkVO.bannerText}</a></li>
					</c:if>
				</c:forEach>
		</json:property>
	</json:object>
</dsp:page>
