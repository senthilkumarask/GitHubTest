<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
				
				<dsp:param name="currentSiteId" param="currentSiteId" />
				<dsp:param name="registrySummaryVO" param="registrySummaryVO" />
				<dsp:param name="PageType" param="PageType"/>
			
				<table border="0" cellpadding="0" cellspacing="0" class="subnav grid_12 clearfix">
                    <tr>
                        <td class="col1">
                        	<%--variable babyCAMode is setup in pagestart  --%>                        	
                            <c:choose>							
                            	<%-- show baby styled subheader on CANADA when viewing baby registry--%>
                                <c:when test="${currentSiteId == 'TBS_BuyBuyBaby' || (currentSiteId  == 'TBS_BedBathCanada' && babyCAMode == 'true')}">
                                    <%-- Start: Added for Scope # 81 H1 tags --%>
                                    <c:if test="${PageType == 'RegistryLandingBaby'}">
                                        <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_baby_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
                                    </c:if>
                                    <%-- End: Added for Scope # 81 H1 tags --%>
                                    <a href="${contextPath}/page/BabyRegistry">
                                        <img alt="Baby Registry" src="/_assets/bbbaby/images/baby-registry.png" height="32" width="144" />
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <%-- Start: Added for Scope # 81 H1 tags --%>
                                    <c:if test="${PageType == 'RegistryLanding'}">
                                        <h1 class="txtOffScreen"><bbbt:textArea key="txt_wedding_header_h1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/></h1>
                                    </c:if>
                                    <%-- End: Added for Scope # 81 H1 tags --%>
                                    <a href="${contextPath}/page/Registry"><bbbl:label key='lbl_mng_regitem_the_Bridal' language="${pageContext.request.locale.language}" /> <img src="${imagePath}/_assets/bbregistry/images/icons/and.png" height="22" width="27" alt="and"> 
                                    	<c:choose>
                                    		<c:when test="${currentSiteId  == 'TBS_BedBathCanada'}">
                                    			<bbbl:label key='lbl_mng_regitem_gift_registry_ca' language="${pageContext.request.locale.language}" />
                                    		</c:when>
                                    		<c:otherwise>
                                    			<bbbl:label key='lbl_mng_regitem_gift_registry' language="${pageContext.request.locale.language}" />                                    	
                                    		</c:otherwise>
                                    	</c:choose>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="col2">
                        <bbbl:label key="lbl_persistentheader_welcome" language ="${pageContext.request.locale.language}"/>
                            ${registrySummaryVO.primaryRegistrantFirstName}<c:if test="${registrySummaryVO.coRegistrantFirstName != null && fn:length(registrySummaryVO.coRegistrantFirstName) > 0}">&nbsp;and&nbsp;${registrySummaryVO.coRegistrantFirstName}
                            </c:if>
                        </td>
                        <td title="${registrySummaryVO.registryType.registryTypeDesc} Date" class="col3">
                            ${registrySummaryVO.registryType.registryTypeDesc}&nbsp;<bbbl:label key="lbl_persistentheader_date" language ="${pageContext.request.locale.language}"/>
                            ${registrySummaryVO.eventDate}
                        </td>
                        <td title="View/Manage" class="col4">
                            <dsp:a href="${contextPath}/giftregistry/view_registry_owner.jsp"
                                title="View/Manage">
                                <dsp:param name="registryId"
                                    value="${registrySummaryVO.registryId}" />
                                <dsp:param name="eventType"
                                    value="${registrySummaryVO.registryType.registryTypeDesc}" />
                            
                                <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
                                <c:set target="${placeHolderMap}" property="giftRegisteredCount" value="${registrySummaryVO.giftRegistered}" />
                                <c:choose>
                                    <c:when test="${registrySummaryVO.giftRegistered gt 1}">
                                        <bbbt:textArea key="txt_persistent_view_manage_multiple" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
                                    </c:when>
                                    <c:otherwise>
                                        <bbbt:textArea key="txt_persistent_view_manage_single" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
                                    </c:otherwise>
                                </c:choose>
                            </dsp:a>
                        </td>
					</tr>
				</table>
</dsp:page>