<dsp:page>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/droplet/MxRegistryInfoDisplayDroplet" />
	<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
  	<dsp:importbean var="storeConfig" bean="/atg/store/StoreConfiguration"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
		<c:set var="parenthesis" value='('/>
		<c:set var="underscore" value='_'/>
		<c:set var="pwsurl"><c:out value="${fn:replace(param.pwsurl,parenthesis, underscore)}"/></c:set>
		<c:set var="eventType"><c:out value="${fn:replace(param.eventType,parenthesis, underscore)}"/></c:set>
		<c:set var="registryId"><c:out value="${fn:replace(param.registryId,parenthesis, underscore)}"/></c:set>
		<c:set var="pwsToken"><c:out value="${fn:replace(param.pwsToken,parenthesis, underscore)}"/></c:set>
	<dsp:importbean
	bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<%-- <c:choose>
		<c:when test="${not empty eventType && (eventType eq 'Wedding' || eventType eq 'Commitment Ceremony')}">
			<c:set var="pageVariation" value="br" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="" scope="request" />
		</c:otherwise>
	</c:choose> --%>
    <%-- as per update from Raj/Lokesh all registry pages will use Purple theme on BedBathUS and BedBathCA --%>
    <c:choose>
        <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>

	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>

	<bbb:mxPageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">giftView updateRegistry useFB useCertonaJs useMapQuest viewRegistry useGoogleAddress</jsp:attribute>
<jsp:body>

	<%-- Droplet for showing error messages --%>
	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
		<dsp:oparam name="output">
			<div class="error"><dsp:valueof param="message"/></div>
		</dsp:oparam>
	</dsp:droplet>


<%-- Droplet Placeholder here --%>
<dsp:getvalueof var="sortSeq" param="sorting"/>
<dsp:getvalueof var="view" param="view"/>

 <c:if test="${not empty registryId}">
	<dsp:droplet name="MxRegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:oparam name="output">

    <dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
	<c:choose>
	<c:when test="${eventTypeVar eq  eventType}">


	<div id="content" class="container_12 clearfix" role="main">
        	<div class="grid_12">
				<div class="grid_9 alpha omega">
				<dsp:setvalue bean="SessionBean.registryTypesEvent" value="${eventTypeCode}"/>
                    <h1 class="fl">
                    	<dsp:droplet name="IsEmpty">
                    		<dsp:param param="registrySummaryVO.primaryRegistrantFirstName" name="value"/>
                           	<dsp:oparam name="false">
                           		<dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/> <dsp:valueof param="registrySummaryVO.primaryRegistrantLastName"/>
                        	</dsp:oparam>
                        	<dsp:oparam name="true">
                        	&nbsp;
                        	</dsp:oparam>
	                    </dsp:droplet>
						<dsp:droplet name="IsEmpty">
                                <dsp:param param="registrySummaryVO.coRegistrantFirstName" name="value"/>
	                            <dsp:oparam name="false"> &amp; <dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/> <dsp:valueof param="registrySummaryVO.coRegistrantLastName"/>
	                         </dsp:oparam>
	                    </dsp:droplet>
	                  </h1>
	        	</div>

	        	<div class="grid_3 marTop_20 omega fr">

							<c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_mxregitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
							<div class="button">
                                <a href="#" class="btnPrint" title="${btnPrintTitle}"><span class="btnPrint print">${btnPrintTitle}</span></a>
                            </div>

						<dsp:form method="post" action="index.jsp" id="frmRegistryInfo">

						<%-- Email Specific --%>

	  					<dsp:getvalueof var="guest_registry_uri" param="registryURL"/>

	          			<c:url var="registryURL"
							value="${scheme}://${serverName}:${serverPort}${contextPath}${guest_registry_uri}">
	            			<c:param name="registryId" value="${registryId}"/>
	            			<c:param name="eventType" value="${eventType}"/>
	            			<c:param name="pwsurl" value="${pwsurl}"/>
	          			</c:url>

						<dsp:setvalue  bean="EmailHolder.values.eventType" paramvalue="registrySummaryVO.registryType.registryTypeName"/>
						<dsp:setvalue  bean="EmailHolder.values.registryURL" value="${registryURL}"/>
						<dsp:setvalue  bean="EmailHolder.values.registryId" value="${param.registryId}" />
						<dsp:setvalue  bean="EmailHolder.formComponentName"
												value="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
						<dsp:setvalue  bean="EmailHolder.handlerName" value="EmailMxRegistry" />
						<c:set var="emailTitle"><bbbl:label key='lbl__mx_mng_regitem_emailreg_title' language="${pageContext.request.locale.language}" /></c:set>
						<dsp:setvalue  bean="EmailHolder.values.title" value="${emailTitle}" />
						<dsp:setvalue  bean="EmailHolder.values.senderEmail" beanvalue="Profile.email"/>
						<div class="button">
							<a href="#" class="btnMxEmail" onclick="javascript:emailMxRegistry();" title='<bbbl:label key='lbl_mng_regitem_emailmxreg' language="${pageContext.request.locale.language}" />'><bbbl:label key='lbl_mng_regitem_emailmxreg' language="${pageContext.request.locale.language}" /></a>
						</div>
						<dsp:setvalue  bean="EmailHolder.values.pRegFirstName" paramvalue="registrySummaryVO.primaryRegistrantFirstName" />
						<dsp:setvalue  bean="EmailHolder.values.pRegLastName" paramvalue="registrySummaryVO.primaryRegistrantLastName" />
						<dsp:setvalue  bean="EmailHolder.values.coRegFirstName" paramvalue="registrySummaryVO.coRegistrantFirstName" />
						<dsp:setvalue  bean="EmailHolder.values.coRegLastName" paramvalue="registrySummaryVO.coRegistrantLastName" />
						<dsp:setvalue  bean="EmailHolder.values.eventDate" paramvalue="registrySummaryVO.eventDate" />
						<dsp:setvalue  bean="EmailHolder.values.eventTypeRegistry" paramvalue="registrySummaryVO.eventType" />
						<c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emai_registry_subject' language='${pageContext.request.locale.language}'/></c:set>
					    <dsp:setvalue  bean="EmailHolder.values.subject" value="${defaultSubjectTxt}" />

						<dsp:droplet name="DateCalculationDroplet">
						<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
						<dsp:param name="convertDateFlag" value="true" />
						<dsp:oparam name="output">
							<dsp:setvalue  bean="EmailHolder.values.daysToGo" paramvalue="daysToGo" />
							<dsp:setvalue  bean="EmailHolder.values.dateCheck" paramvalue="check" />
							<dsp:setvalue  bean="EmailHolder.values.daysToNextCeleb" paramvalue="daysToNextCeleb" />
						</dsp:oparam>
						<dsp:oparam name="error">
							<dsp:valueof param="errorMsg"></dsp:valueof>
						</dsp:oparam>
						</dsp:droplet>

						<dsp:getvalueof bean="EmailHolder.values.eventDate" var="eventDate"/>
						<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/MxDateFormatDroplet">
							<dsp:param name="currentDate" value="${eventDate}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="mxConvertedDate" param="mxConvertedDate" />
							</dsp:oparam>
						</dsp:droplet>
						
						<dsp:getvalueof var="eventType" param="registrySummaryVO.eventType"/>
						<dsp:getvalueof var="spEventTypeLabel" value="lbl_mx_event_type_${eventType}" /> 
						<c:set var="mxEventType"><bbbl:label key="${spEventTypeLabel}" language="${pageContext.request.locale.language}" /></c:set>

                     	<div id="emailAFriendData">
						<dsp:getvalueof bean="EmailHolder.values.dateCheck" var="dateCheck"/>
						<dsp:getvalueof bean="EmailHolder.values.daysToNextCeleb" var="daysToNextCeleb"/>
                        <dsp:getvalueof bean="EmailHolder.values.daysToGo" var="daysToGo"/>

                        <dsp:getvalueof bean="EmailHolder.values.registryURL" var="registryURL"/>
                        <dsp:getvalueof bean="EmailHolder.values.senderEmail" var="senderEmail"/>
                        <dsp:getvalueof bean="EmailHolder.values.registryId" var="registryId"/>
                        <dsp:getvalueof bean="EmailHolder.formComponentName" var="formComponentName"/>
                        <dsp:getvalueof bean="EmailHolder.handlerName" var="handlerName"/>
                        <dsp:getvalueof bean="EmailHolder.values.title" var="title"/>
                        <dsp:getvalueof bean="EmailHolder.values.pRegFirstName" var="pRegFirstName"/>
                        <dsp:getvalueof bean="EmailHolder.values.pRegLastName" var="pRegLastName"/>
                        <dsp:getvalueof bean="EmailHolder.values.coRegFirstName" var="coRegFirstName"/>
                        <dsp:getvalueof bean="EmailHolder.values.coRegLastName" var="coRegLastName"/>

                        <dsp:getvalueof bean="EmailHolder.values.eventTypeRegistry" var="eventTypeRegistry"/>
                        <dsp:getvalueof bean="EmailHolder.values.subject" var="subject"/>

						<input type="hidden" name="dateCheck" value="${dateCheck}" class="emailAFriendFields"/>
						<input type="hidden" name="daysToNextCeleb" value="${daysToNextCeleb}" class="emailAFriendFields"/>
                        <input type="hidden" name="daysToGo" value="${daysToGo}" class="emailAFriendFields"/>
                        <input type="hidden" name="eventType" value="${eventType}" class="emailAFriendFields"/>
                        <input type="hidden" name="registryURL" value="${registryURL}" class="emailAFriendFields"/>
                        <input type="hidden" name="senderEmail" value="${senderEmail}" class="emailAFriendFields"/>
                        <input type="hidden" name="registryId" value="${registryId}" class="emailAFriendFields"/>
                        <input type="hidden" name="formComponentName" value="${formComponentName}" class="emailAFriendFields"/>
                        <input type="hidden" name="handlerName" value="${handlerName}" class="emailAFriendFields"/>
                        <input type="hidden" name="title" value="${title}" class="emailAFriendFields"/>
                        <input type="hidden" name="pRegFirstName" value="${pRegFirstName}" class="emailAFriendFields"/>
                        <input type="hidden" name="pRegLastName" value="${pRegLastName}" class="emailAFriendFields"/>
                        <input type="hidden" name="coRegFirstName" value="${coRegFirstName}" class="emailAFriendFields"/>
                        <input type="hidden" name="coRegLastName" value="${coRegLastName}" class="emailAFriendFields"/>
                        <input type="hidden" name="eventDate" value="${mxConvertedDate}" class="emailAFriendFields"/>
                        <input type="hidden" name="eventTypeRegistry" value="${mxEventType}" class="emailAFriendFields"/>
                        <input type="hidden" name="subject" value="${subject}" class="emailAFriendFields"/>
                        </div>
                        <input type="submit" class="visuallyhidden" value=" "/>
					</dsp:form>	<%-- CR9 - Don't want to allow people to share someone else's registry online.
										Remove Like button while viewing another person's registry --%>
								<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
								<dsp:contains var="isPresent" values="${sessionMapValues.userRegistriesList}"
													object="${registryId}"/>
                                <c:if test="${FBOn && isPresent}">
                                <!--[if IE 7]>
                                    <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                                        <dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof id="encodedURL" param="encodedURL"/>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
                                        <dsp:param name="configKey" value="FBAppIdKeys"/>
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                    <div class="fb-like">
                                        <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=button_count&amp;width=90&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:90px; height:24px;" allowTransparency="true"></iframe>
                                    </div>
                                <![endif]-->
                                <!--[if !IE 7]><!-->
                                    <div class="fb-like" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
                                <!--<![endif]-->
                                </c:if>

					</div>

          		<div class="grid_12 alpha omega registryDetails">
          		<a name="backToTop"></a>
          		<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
          		<c:set var="registryTable">registryTableBridal</c:set>
          		<c:if test="${appid == 'BuyBuyBaby'}">
          		<c:set var="registryTable">registryTableBaby</c:set>
          		</c:if>

          		<div class="grid_12 alpha omega ${registryTable} ">
          				<c:if test="${eventTypeCode eq 'BA1' }">

						<div class="grid_12 alpha registryBabyNameBlock omega">
							<div class="grid_8 alpha omega">
								Registry # :  <span class="babyName"><dsp:valueof param="registryId"/></span>
							</div>
							<div class="grid_4 omega fr textRight">
							</div>
						</div>
						</c:if>
						<dsp:getvalueof var="babyName" param="registrySummaryVO.eventVO.babyName"/>
						<dsp:getvalueof var="babyShowerDate" param="registrySummaryVO.eventVO.showerDateObject"/>
			            <dsp:getvalueof var="babyGender" param="registrySummaryVO.eventVO.babyGender"/>
			            <dsp:getvalueof var="babyNurseryTheme" param="registrySummaryVO.eventVO.babyNurseryTheme"/>

						<table class="width_12">
							<thead>
								<tr>
									<th class="width_2"><span>Tipo de Registro</span></th>
									<c:choose>
									<c:when test="${eventTypeCode eq 'BA1' }">
										<th class="width_2"><span>Baby's Expected Arrival Date</span></th>
									</c:when>
									<c:otherwise>
										<th class="width_2"><span>Fecha del Evento</span></th>
									</c:otherwise>
									</c:choose>

									<c:if test="${eventTypeCode eq 'BA1' }">
										<c:if test="${not empty babyShowerDate}">
											<th class="width_2"><span>Shower Date</span></th>
										</c:if>
										<c:if test="${not empty babyName}">
											<th class="width_2"><span><bbbl:label key='lbl_mng_regitem_baby_name' language="${pageContext.request.locale.language}" /></span></th>
										</c:if>
										<c:if test="${not empty babyGender}">
											<th class="width_2"><span>Baby Gender</span></th>
										</c:if>
										<c:if test="${not empty babyNurseryTheme}">
											<th class="width_2"><span><bbbl:label key='lbl_mng_regitem_nursery_theme' language="${pageContext.request.locale.language}" /></span></th>
										</c:if>
									</c:if>
									<c:if test="${eventTypeCode ne 'BA1' }">
										<th class="width_2"><span>N&uacute;mero de Registro</span></th>
										<th class="width_6"><span>&nbsp;</span></th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td class="width_2">
									<dsp:getvalueof var="eventType" param="registrySummaryVO.eventType"/>
									<dsp:getvalueof var="spEventTypeLabel" value="lbl_mx_event_type_${eventType}" /> 
									<span><bbbl:label key="${spEventTypeLabel}" language="${pageContext.request.locale.language}" /></span></td>
									<td class="width_2">
										<span>
											<dsp:getvalueof var="eventDateStr" param="eventDate"/>
											<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/MxDateFormatDroplet">
												<dsp:param name="currentDate" value="${eventDateStr}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="mxConvertedDate" param="mxConvertedDate" />
				                            					</dsp:oparam>
				                            				</dsp:droplet>
						                			${mxConvertedDate}
										</span>
									</td>

									<c:if test="${eventTypeCode eq 'BA1' }">
										<c:if test="${not empty babyShowerDate}">
											<td class="width_2">
											<span>
												<dsp:getvalueof var="eventDateStr" param="eventDate"/>
												<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/MxDateFormatDroplet">
													<dsp:param name="currentDate" value="${registrySummaryVO.eventVO.showerDateObject}"/>
													<dsp:oparam name="output">
					                            						<dsp:getvalueof var="mxConvertedDate" param="mxConvertedDate" />
					                            					</dsp:oparam>
					                            				</dsp:droplet>
							                			${mxConvertedDate}
											</span></td>
										</c:if>
										<c:if test="${not empty babyName}">
											<td class="width_2 breakWord"><span><dsp:valueof param="registrySummaryVO.eventVO.babyName"/></span></td>
										</c:if>
										<c:if test="${not empty babyGender}">
											<td class="width_2">
											<span>
												<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GetGenderKeyDroplet">
													<dsp:param name="genderKey" value="${babyGender}"/>
													<dsp:param name="inverseflag" value="true"/>
					                            	<dsp:oparam name="output">
					                            		<dsp:getvalueof var="genderValue" param="genderCode" />
					                            	</dsp:oparam>
					                            </dsp:droplet>
							                	${genderValue}
											</span></td>
										</c:if>
										<c:if test="${not empty babyNurseryTheme}">
											<td class="width_2 breakWord"><span><dsp:valueof param="registrySummaryVO.eventVO.babyNurseryTheme"/></span></td>
										</c:if>
									</c:if>
									<c:if test="${eventTypeCode ne 'BA1' }">
										<td class="width_2"><span><dsp:valueof param="registryId"/></span></td>
										<td class="width_6"></td>
									</c:if>
								</tr>
							</tbody>
						</table>
					</div>

				</div>
          	</div>

<%--Including the Registry Items Page --%>
	<c:set var="maxBulkSize" scope="request">
		<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
 	</c:set>
<%-- <dsp:include page="frags/registry_items_guest.jsp" flush="true">
	<dsp:param name="registryId" value="${registryId}" />
	<dsp:param name="startIdx" value="0" />
	<dsp:param name="isGiftGiver" value="false" />
	<dsp:param name="blkSize" value="${maxBulkSize}"/>
	<dsp:param name="isAvailForWebPurchaseFlag" value="false" />
	<dsp:param name="userToken" value="UT1021" />
	<dsp:param name="sortSeq" value="${sortSeq}" />
	<dsp:param name="view" value="${view}" />
	<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
	<dsp:param name="eventType" value="${eventType}"/>
	<dsp:param name="pwsurl" value="${pwsurl}"/>
</dsp:include>  --%>

<div id="mxRegAjaxLoad" data-currentView="guest" data-registryId="${registryId}" data-startIdx="0" data-isGiftGiver="true" data-blkSize="${maxBulkSize}" data-isAvailForWebPurchaseFlag="false"  data-userToken="UT1021" data-sortSeq="${sortSeq}" data-eventTypeCode="${eventTypeCode}" data-eventType="${eventType}" data-pwsurl="${pwsurl}" data-view="${view}" >
		<div class="ajaxLoadWrapper">
			<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
		</div>
</div>
</div>

</c:when>
	<c:otherwise>
		<div id="content" class="container_12 clearfix" role="main">
		<div class="grid_12 clearfix marTop_20">
			<h3><span class="error">
			Access Denied!!!
			</span></h3>
		</div>
		</div>
	</c:otherwise>
	</c:choose>

	</dsp:oparam>
	<dsp:oparam name="error">
		<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
		<div class="container_12 clearfix">
			<div class="grid_12">
				<div class="error marTop_20"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
			</div>
        </div>
	</dsp:oparam>
</dsp:droplet>

</c:if>
</jsp:body>
<jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
        	    s.pageName = 'Registry View Page';
				s.channel = 'Registry';
				s.prop1 = 'Registry';
				s.prop2 = 'Registry';
				s.prop3 = 'Registry';
        	    s.events="event28";
				s.eVar23 = '${eventType}';
				s.eVar24 = '${registryId}';
				var s_code=s.t();if(s_code)document.write(s_code);
		   }
        </script>
    </jsp:attribute>

<script language="javascript">
	// BSL-448 - IE7 performance issue on view registry
(function ($, window, document, undefined) {
    $(function () {
        if ($('#mxRegAjaxLoad')[0]) {
            var el = $('#mxRegAjaxLoad'),
                currentView = el.attr('data-currentView'),
                registryId = el.attr('data-registryId'),
                startIdx = el.attr('data-startIdx'),
                isGiftGiver = el.attr('data-isGiftGiver'),
                blkSize = el.attr('data-blkSize'),
                isAvailForWebPurchaseFlag = el.attr('data-isAvailForWebPurchaseFlag'),
                userToken = el.attr('data-userToken'),
                view = el.attr('data-view'),
                eventTypeCode = el.attr('data-eventTypeCode'),
                eventType = el.attr('data-eventType'),
                pwsurl = el.attr('data-pwsurl'),
                sortSeq = el.attr('data-sortSeq');

            if (typeof sortSeq === 'undefined' || sortSeq === '') { sortSeq = 1; }

            if (typeof view === 'undefined' || view === '') { view = 1; }

            if ($('html').hasClass('buyBuyBaby')) {
                siteId = 'buyBuyBaby';
            } else if ($('html').hasClass('bedBathUS')) {
                siteId = 'bedBathUS';
            } else {
                siteId = 'bedBathCA';
            }

            var guestRegistryURL = '/store/mx/frags/registry_items_guest.jsp',
                guestRegistryParams = '?registryId=' + registryId + '&startIdx=' + startIdx + '&isGiftGiver=' + isGiftGiver + '&blkSize=' + blkSize + '&isAvailForWebPurchaseFlag=' + isAvailForWebPurchaseFlag + '&userToken=' + userToken + '&sortSeq=' + sortSeq + '&view=' + view + '&eventTypeCode=' + eventTypeCode + '&eventType=' + eventType + '&pwsurl=' + pwsurl,

                ownerRegistryURL = '/store/giftregistry/frags/regAjaxLoad.jsp',
                ownerRegistryParams = '?registryId=' + registryId + '&startIdx=' + startIdx + '&isGiftGiver=' + isGiftGiver + '&blkSize=' + blkSize + '&isAvailForWebPurchaseFlag=' + isAvailForWebPurchaseFlag + '&userToken=' + userToken + '&sortSeq=' + sortSeq + '&view=' + view + '&eventTypeCode=' + eventTypeCode + '&eventType=' + eventType + '&pwsurl=' + pwsurl,

                url = '',
                currentParam = '',
                currentURL = location.pathname,
                isLoading = false,
                accordionDIVsNum = 1,
                loaderDIVsNum = 1,
                accordionDIVs = '.accordionReg',
                loaderDIVs = '.load',
                loadMoreDIV = '.loadMore',
                contentDIVs = '.accordionDiv',
                breakOut = false,
                collapseAll = null,
                expandAll = null
                timeDelayFirstLoad = ($.browser.oldie)? 1000: 100,
                timeDelayScrollChecks = ($.browser.oldie)? 400: 100,
                $regDataContainer = $('#mxRegAjaxLoad'),
                $window = $(window),
                dataForAjax = '',
                ajaxUrl = '';

            if (currentView == 'guest') {
                url = guestRegistryURL + guestRegistryParams;
                ajaxUrl = guestRegistryURL;
                dataForAjax = guestRegistryParams.substr(1);
            } else {
                url = ownerRegistryURL + ownerRegistryParams;
                ajaxUrl = ownerRegistryURL;
                dataForAjax = ownerRegistryParams.substr(1);
            }

            var checkLoader = function() {
                // var st = $window.scrollTop();

                if (BBB.fn.inView($regDataContainer.find(loaderDIVs + loaderDIVsNum))) {
                    setTimeout(showAccordion, timeDelayScrollChecks);
                } else {
                    isLoading = false;
                }

                // $('html, body').animate({ scrollTop: (st + 1) + 'px' }, 0).animate({ scrollTop: (st - 1) + 'px' }, 0);
            };

            var showAddToCartModal = function(type, wrapper, qty) {
                var $currentRowItem, prodQty, prodPrice, prodTitle, prodImageSRC, _HTML = '', $html;

                if (typeof type !== 'undefined' && typeof type === 'string' && type.trim() !== '') {
                    if (type.trim() === 'mini') {
                        _HTML = '<div title="'+ BBB.glblStringMsgs.titleRegPageAddToCart +'" class="clearfix addToCartRegModalInner">';
                        _HTML += '<div class="clearfix padTop_20">';
                        _HTML += '<div class="button button_active button_active_orange">';
                        _HTML += '<a href="/store/cart/cart.jsp">'+ BBB.glblStringMsgs.btnRegPageViewCart +'</a>';
                        _HTML += '</div>';
                        _HTML += '<div class="fl padTop_10 padLeft_20 quickViewContinueShopping">';
                        _HTML += '<a href="#" class="close-any-dialog">'+ BBB.glblStringMsgs.quickViewContinueShopping +'</a>';
                        _HTML += '</div>';
                        _HTML += '<div class="clear"></div>';
                        _HTML += '</div>';
                        _HTML += '</div>';

                        $html = $(_HTML);

                        BBB.modalDialog.openDialog({
                            wrapperSetting: {
                                type: 'html',
                                modalContentHTML: $html
                            },
                            uiDialiogSettings: {
                                dialogClass: 'modalWindow addToCartRegModal'
                            }
                        });
                    } else if (type.trim() === 'full' && typeof wrapper !== 'undefined' && $(wrapper)[0] && typeof qty !== 'undefined' && /^\d+$/.test(qty)) {
                        $currentRowItem = $(wrapper).find('.productContent');
                        prodQty = qty;
                        prodPrice = $currentRowItem.find('.price').html().trim();
                        prodTitle = $currentRowItem.find('.productName .prodTitle a')[0]? $currentRowItem.find('.productName .prodTitle a').html().trim(): ($currentRowItem.find('.productName .prodTitle')[0]? $currentRowItem.find('.productName .prodTitle').html().trim(): '');
                        prodImageSRC = $(wrapper).find('img.prodImage').attr('src');

                        _HTML += '<div title="'+ BBB.glblStringMsgs.titleRegPageAddToCart +'" class="clearfix addToCartRegModalInner">';
                        _HTML += '<div class="clearfix padTop_5">';
                        _HTML += '<div class="width_2 fl marRight_10">';
                        _HTML += '<img src="'+ prodImageSRC +'" alt="'+ prodTitle +'" height="146" width="146" />';
                        _HTML += '</div>';
                        _HTML += '<div class="width_5 fl marLeft_10">';
                        _HTML += '<p class="noMar prodTitle">'+ prodTitle +'</p>';
                        _HTML += '<p class="noMar prodPrice bold">'+ prodPrice +'</p>';
                        _HTML += '<p class="noMar prodQty">'+ BBB.glblStringMsgs.lblRegPageQuantity +':&nbsp;'+ prodQty +'</p>';
                        _HTML += '<div class="clearfix padTop_20">';
                        _HTML += '<div class="button button_active button_active_orange">';
                        _HTML += '<a href="/store/cart/cart.jsp">'+ BBB.glblStringMsgs.btnRegPageViewCart +'</a>';
                        _HTML += '</div>';
                        _HTML += '<div class="fl padTop_10 padLeft_20 quickViewContinueShopping">';
                        _HTML += '<a href="#" class="close-any-dialog">'+ BBB.glblStringMsgs.quickViewContinueShopping +'</a>';
                        _HTML += '</div>';
                        _HTML += '<div class="clear"></div>';
                        _HTML += '</div>';
                        _HTML += '</div>';
                        _HTML += '<div class="clear"></div>';
                        _HTML += '</div>';
                        _HTML += '</div>';

                        $html = $(_HTML);

                        BBB.modalDialog.openDialog({
                            wrapperSetting: {
                                type: 'html',
                                modalContentHTML: $html
                            },
                            uiDialiogSettings: {
                                width: 610,
                                dialogClass: 'modalWindow addToCartRegModal'
                            }
                        });
                    }
                }
            };

            var fAddToCart = function (evt) {
                evt.preventDefault();
                var okToAdd = true,
                    $this = $(this),
                    $obj = $this.parents('li.addToCartGiftRegWrapper'),
                    prodId, skuId, qty, registryId, storeId, oosSKUId,
                    cartHasItems = (parseInt($('#shoppingCartItems span').html(), 10) === 0 || isNaN(parseInt($('#shoppingCartItems span').html(), 10))) ? false : true,
                    idx = 0,
                    extraData = {},
                    formDataForAjax = {
                        addItemResults: []
                    },
                    errMessage = BBB.glblStringMsgs.errGeneric,
                    tmp, name, value, certonaLinks = '',
                    regProducts = '',
                    $storeId = $obj.find('input[name=storeId]');

                prodId = $obj.find('input[name=prodId]')[0] ? $obj.find('input[name=prodId]').val() : 'null';
                skuId = $obj.find('input[name=skuId]')[0] ? $obj.find('input[name=skuId]').val() : 'null';
                registryId = $obj.find('input[name=registryId]')[0] ? $obj.find('input[name=registryId]').val() : 'null';
                qty = $obj.find('input.itemQuantity')[0] ? $obj.find('input.itemQuantity').val() : 'null';
                storeId = $storeId[0] ? $storeId.val() : 'null';
                oosSKUId = $obj.find('input[name=oosSKUId]')[0] ? $obj.find('input[name=oosSKUId]').val() : 'null';

                if (skuId === 'null' || skuId === '' || registryId === 'null' || registryId === '' || qty === 'null' || qty === '' || !/^\d+$/.test(qty) || parseInt(qty, 10) <= 0 || (oosSKUId === skuId && (storeId === 'null' || /^\s*$/.test(storeId)))) {
                    okToAdd = false;

                    if (qty !== 'null' && (qty === '' || parseInt(qty, 10) <= 0)) {
                        errMessage = BBB.glblStringMsgs.errSelectQty;
                    } else if (qty !== 'null' && !/^\d+$/.test(qty)) {
                        errMessage = BBB.glblStringMsgs.errSelectQtyNotNumber;
                    } else if (oosSKUId === skuId) {
                        errMessage = BBB.glblStringMsgs.errOutOfStock;
                    }
                } else {
                    idx = formDataForAjax.addItemResults.length;
                    formDataForAjax.addItemResults[idx] = {
                        "skuId": skuId,
                        "prodId": prodId,
                        "registryId": registryId,
                        "qty": qty
                    };
                    certonaLinks += prodId + ';';
                    regProducts += prodId + ';' + registryId + '|' + skuId + ',';
                    extraData = {};
                    $obj.find('input[type=hidden].addToCartSubmitData').each(function () {
                        tmp = {};
                        name = $(this).attr('name');
                        value = $(this).val();
                        tmp[name] = value;
                        extraData = $.extend({}, extraData, tmp);
                    });
                    formDataForAjax.addItemResults[idx] = $.extend({}, formDataForAjax.addItemResults[idx], extraData);
                }

                if (!okToAdd) {
                    BBB.openMsgDialog(errMessage);
                } else {
                    formDataForAjax = "addItemResults=" + JSON.stringify(formDataForAjax).doURLEncode('&', '?', '=');
                    BBB.service.getAjaxData("addToCart" + BBB.fn.getUID(), {
                        oDataForAjax: formDataForAjax,
                        oShowPreloader: false,
                        oAjaxFormat: "json",
                        oSuccessCallback: function (response) {
                            var $ul, $li, i;
                            if (response.hasOwnProperty('error') && response.error === true) {
                                $ul = $('<ul></ul>');
                                if (response.hasOwnProperty('errorMessages') && response.errorMessages.length > 0) {
                                    for (i = 0, j = response.errorMessages.length; i < j; i++) {
                                        $li = $('<li>').addClass('error').html(response.errorMessages[i]);
                                        $ul.append($li);
                                    }
                                } else {
                                    $li = $('<li>').addClass('error').html(BBB.glblStringMsgs.errGeneric);
                                    $ul.append($li);
                                }
                                BBB.openErrorDialog($ul);
                            } else {
                                if (response.hasOwnProperty('commItemCount') && response.hasOwnProperty('count')) {
                                    if (typeof callCertonaResxRun === 'function') {
                                        callCertonaResxRun(certonaLinks);
                                    }
                                    if (typeof registryAddToCart === 'function') {
                                        regProducts = regProducts.substring(0, regProducts.length - 1);
                                        registryAddToCart(cartHasItems, regProducts);
                                    }
                                    // var st = $window.scrollTop();
                                    // BBB.updateMiniCart(parseInt(response.commItemCount, 10), parseInt(response.count, 10), function() {
                                        // setTimeout(function(){
                                            // $('html, body').animate({ scrollTop: st + 'px' }, 'fast');
                                        // }, 3000);
                                    // }, true, true, true);

                                    BBB.updateMiniCart(parseInt(response.commItemCount, 10), parseInt(response.count, 10), null, true, true, true);

                                    //showAddToCartModal('full', $obj, response.count);
                                }
                            }
                            $storeId.val('');
                        },
                        oServiceUrl: BBB.fn.getUrl('addItemToCartFromReg'),
                        oAjaxCommMethod: "POST"
                    });
                }
            };

            var showAccordion = function() {
                var hasData = false;

                isLoading = true;
                breakOut = false;

                for (accordionDIVsNum = accordionDIVsNum; accordionDIVsNum <= 12; accordionDIVsNum++) {
                    var $accordion = $regDataContainer.find(accordionDIVs + accordionDIVsNum);

                    $accordion // .addClass('accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons')
                        .find('h2.ui-accordion-header, h3.ui-accordion-header').each(function(){
                            var $this = $(this),
                                $thisAccordionDiv = $this.next('div.accordionDiv'),
                                thisHasData = $thisAccordionDiv.find('li.productRow')[0];

                            if ($thisAccordionDiv[0]) {
                                hasData = hasData || thisHasData;

                                if (!thisHasData) {
                                    $thisAccordionDiv.css({
                                        'height': '0',
                                        'padding': '0',
                                        'margin': '0',
                                        'padding-bottom': '5px',
                                        'font-size': '1px',
                                        'line-height': '1px'
                                    });
                                }

                                // $this.addClass('ui-accordion-header ui-helper-reset ui-state-default ui-corner-all').prepend('<span class="ui-icon ui-icon-triangle-1-e"></span>');
                                // $thisAccordionDiv.addClass('ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom').css('visibility', 'hidden').hide();
                                $thisAccordionDiv.css('visibility', 'hidden').hide();

                                if (typeof expandAll !== 'undefined' && expandAll) {
                                    expandDiv($this);
                                } else if (typeof collapseAll === 'undefined' || (typeof collapseAll !== 'undefined' && !collapseAll)) {
                                    if (thisHasData) {
                                        expandDiv($this);
                                        breakOut = true;
                                    }
                                }
                            }
                        });

                    if (hasData || currentView != 'guest') {
                        $accordion.removeClass('hidden');
                    }

                    // remove old loader
                    $regDataContainer.find(loaderDIVs + loaderDIVsNum).remove();
                    for (loaderDIVsNum = loaderDIVsNum; loaderDIVsNum <= 12; loaderDIVsNum++) {
                        if ($regDataContainer.find(loaderDIVs + loaderDIVsNum)[0]) {
                            // show new loader
                            $regDataContainer.find(loaderDIVs + loaderDIVsNum).removeClass('hidden');
                            break;
                        }
                    }

                    if (breakOut) {
                        ++accordionDIVsNum;
                        break;
                    }
                }

                checkLoader();
            };

            var managePrintImages = function() {
                var loadImages = false,
                    beforePrint = function () {
                        if (loadImages == true) {
                            return;
                        }
                        var allImages = $regDataContainer.find('.prodImage[data-original]');
                        allImages.each(function (i) {
                            var _this = $(this);
                            try { _this.attr("src", _this.data('original')).removeClass('loadingGIF'); } catch(e) {}
                        });
                        BBB.fn.errNoImageFound();
                        loadImages = true;
                    };

                    if (window.matchMedia) {
                        var mediaQueryList = window.matchMedia('print');
                        mediaQueryList.addListener(function (mql) {
                            if (mql.matches) {
                                beforePrint();
                            }
                        });
                    }

                try { window.onbeforeprint = beforePrint; } catch(e) {}
            };

            var getUrlParameter = function(param) {
                var ret;

                if (typeof param !== 'undefined' && typeof param === 'string' && /^\s*$/.test(param) === false) {
                    ret = BBB.fn.getUrlParameter(location.search, param);
                }

                return (typeof ret === 'undefined'? '': ret);
            };

            var expandDiv = function($header) {
                var $h2 = $([]),
                    hasNewImages = false;

                if (typeof $header !== 'undefined' && $regDataContainer.find($header)[0]) {
                    $h2 = $regDataContainer.find($header);
                } else {
                    $h2 = $regDataContainer.find('h2.ui-accordion-header, h3.ui-accordion-header');
                }

                $h2.each(function() {
                    $(this).removeClass('ui-state-default ui-corner-all').addClass('ui-state-active ui-corner-top')
                        .find('span').removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s')
                        .end()
                        .next('.accordionDiv').css('visibility', 'visible').show()
                            .addClass('ui-accordion-content-active')
                            // .find('.prodImage[data-original]').lazyload({
                                // errorAbort: function(remaining, settings) {
                                    // var $this = $(this),
                                        // noImgURL = BBB.fn.getUrl('noImageFound'),
                                        // errURL = $this.attr('data-original');

                                    // if ($this.attr('src').indexOf(noImgURL) > -1) {
                                        // $this.addClass('errBBBNoImageFound')
                                            // .removeAttr('data-original');
                                    // } else {
                                        // $this.data('errImageURL', errURL)
                                            // .attr('data-errImageURL', errURL)
                                            // .attr('src', noImgURL)
                                            // .removeAttr('data-original')
                                            // .removeClass('loadingGIF');
                                    // }
                                // },
                                // load: function(remaining, settings) {
                                    // $(this).removeClass('loadingGIF').removeAttr('data-original');
                                // }
                            // });
                            .find('.prodImage[data-original]').each(function(){
                                var $this = $(this);

                                hasNewImages = true;

                                try { $this.attr('src', $this.data('original')).removeAttr('data-original').addClass('loadingGIF'); } catch(e) {}
                            });
                });

                if (hasNewImages) { BBB.fn.errNoImageFound(); }

                checkLoader();
            };

            var collapseDiv = function($header) {
                var $h2 = $([]);

                if (typeof $header !== 'undefined' && $regDataContainer.find($header)[0]) {
                    $h2 = $regDataContainer.find($header);
                } else {
                    $h2 = $regDataContainer.find('h2.ui-accordion-header, h3.ui-accordion-header');
                }

                $h2.each(function() {
                    $(this).addClass('ui-state-default ui-corner-all').removeClass('ui-state-active ui-corner-top')
                        .find('span').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s')
                        .end()
                        .next('.accordionDiv').css('visibility', 'hidden').hide()
                            .removeClass('ui-accordion-content-active');
                });

                checkLoader();
            };

            var manageAjaxResponse = function (data) {
                var response = BBB.fn.whiteSpaceFix(data);

                if (currentView == 'guest') {
                    $regDataContainer.html(response);
                } else {
                    var $response = $(response),
                        $regData = $response.find('#regData'),
                        $topItems = $response.find('#topItems');

                    if ($regData[0]) {
                        $regDataContainer.html($regData.html());
                        $('#regAjaxLoadTopRegItems').html($topItems.html());
                    } else {
                        $regDataContainer.html(response);
                    }
                }

                if (sortSeq == 2 && $('#sortSeqCat')[0] && $('#sortSeqPrice')[0]) {
                    $('#sortSeqCat').removeAttr('checked');
                    $('#sortSeqPrice').attr('checked', 'checked');
                }

                // using setTimeout to thread the execution of this function
                setTimeout(showAccordion, 1);

                BBB.eventTarget.delegate('.lnkQVPopup', 'click', function (evt) {
                    var $this = $(this),
                        $window = $(window),
                        $body = $('body'),
                        url = $this.data('pdp-qv-url');

                    if (typeof url !== 'undefined' && url !== false && url.trim() !== '') {
                        evt.preventDefault();

                        $body.data('lnkQVPopupScrollTop', $window.scrollTop());

                        try {
                            $('.quickViewPopup .ui-dialog-content').dialog('destroy').remove();
                        } catch(e) {}

                        BBB.modalDialog.openDialog({
                            wrapperSetting: {
                                type: "ajax",
                                ajaxParams: {
                                    oServiceUrl: url.trim()
                                },
                                destroyDialog: false
                            },
                            uiDialiogSettings: {
                                width: 860,
                                dialogClass: 'modalWindow quickViewPopup productDetails',
                                open: function (event, ui) {
                                    var $this = $(this);

                                    $this.closest('.ui-dialog')
                                        .off('.ui-dialog-tabbingfix')
                                        .on('keydown.ui-dialog-tabbingfix', function(e) {
                                            if (e.keyCode !== $.ui.keyCode.TAB) { return; }

                                            e.stopPropagation();

                                            var tabbables = $(':tabbable', this),
                                                first = tabbables.filter(':first'),
                                                last  = tabbables.filter(':last'),
                                                current = $(document.activeElement),
                                                currentIndex = -1;

                                            tabbables.each(function(i, e) {
                                                if ($(this).is(current)) {
                                                    currentIndex = i;
                                                }
                                            });

                                            if (currentIndex !== -1) {
                                                if (e.target === last[0] && !e.shiftKey) {
                                                    first.focus(1);
                                                } else if (e.target === first[0] && e.shiftKey) {
                                                    last.focus(1);
                                                } else {
                                                    if (e.shiftKey) {
                                                        $(tabbables[currentIndex-1]).focus(1);
                                                    } else {
                                                        $(tabbables[currentIndex+1]).focus(1);
                                                    }
                                                }
                                                return false;
                                            } else {
                                                if (tabbables[0]) {
                                                    $(tabbables[0]).focus(1);
                                                    return false;
                                                }
                                            }
                                        })
                                        .find('.ui-dialog-titlebar-close').focus(1);
                                },
                                close: function (event, ui) {
                                    if (typeof $body.data('qvAttribPopupOpen') === 'undefined') {
                                        $(this).dialog('destroy').remove();
                                        if (typeof $body.data('lnkQVPopupScrollTop') !== 'undefined') {
                                            $window.scrollTop($body.data('lnkQVPopupScrollTop'));
                                            $body.removeData('lnkQVPopupScrollTop');
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

                BBB.eventTarget.delegate('.qvAttribPopup', 'click', function (evt) {
                    var $this = $(this),
                        modal = null,
                        newWindow = null,
                        url = (this.href.trim() !== '') ? this.href.trim() : '',
                        href = ($this.attr('href')) ? $this.attr('href').trim() : '',
                        options,
                        $window = $(window),
                        $body = $('body'),
                        currentWinSize = {
                            width: $window.width(),
                            height: $window.height()
                        },
                        popupTitle = ($this.attr('title') !== "") ? $this.attr('title') : '',
                        modalGapV = 50,
                        modalGapH = 100,
                        btnsObj = {};

                    btnsObj[BBB.glblStringMsgs.btnBackToQuickView] = function () {
                        $body.data('keepQVPopup', true);
                        $(this).dialog('close');
                        $('.quickViewPopup .ui-dialog-content').dialog('open');
                    };

                    if (href === '' || href.indexOf('javascript') === 0 || href.indexOf('#') === 0) {
                        return;
                    }

                    if (href.indexOf('http') === 0 || href.indexOf('//') === 0) {
                        newWindow = true;
                    } else {
                        modal = true;
                    }

                    if (modal || newWindow) {
                        evt.preventDefault();
                        $body.data('qvAttribPopupScrollTop', $window.scrollTop()).data('qvAttribPopupOpen', true);
                        $body.removeData('keepQVPopup');
                        $('.quickViewPopup .ui-dialog-content').dialog('close');
                    }

                    if (modal) {
                        BBB.modalDialog.openDialog({
                            wrapperSetting: {
                                type: 'ajax',
                                ajaxParams: {
                                    oServiceUrl: url
                                },
                                destroyDialog: false
                            },
                            uiDialiogSettings: {
                                title: popupTitle,
                                maxHeight: currentWinSize.height - modalGapH,
                                maxWidth: currentWinSize.width - modalGapV,
                                open: function (event, ui) {
                                    $(this).css({
                                        'max-height': currentWinSize.height - modalGapH + 'px',
                                        'max-width': currentWinSize.width - modalGapV + 'px',
                                        'overflow-y': 'auto',
                                        'overflow-x': 'auto'
                                    }).closest('.ui-dialog').scrollTo({'chkInView': true});
                                },
                                close: function (event, ui) {
                                    $(this).dialog('destroy').remove();
                                    $body.removeData('qvAttribPopupOpen');

                                    if (typeof $body.data('qvAttribPopupScrollTop') !== 'undefined') {
                                        $window.scrollTop($body.data('qvAttribPopupScrollTop'));
                                        $body.removeData('qvAttribPopupScrollTop');
                                    }

                                    if (typeof $body.data('keepQVPopup') === 'undefined') {
                                        $('.quickViewPopup .ui-dialog-content').dialog('destroy').remove();

                                        if (typeof $body.data('lnkQVPopupScrollTop') !== 'undefined') {
                                            $window.scrollTop($body.data('lnkQVPopupScrollTop'));
                                            $body.removeData('lnkQVPopupScrollTop');
                                        }
                                    }
                                },
                                buttons: btnsObj
                            }
                        });
                    } else if (newWindow) {
                        options = $.extend(true, {}, {
                            currentWinWidth: $window.width(),
                            currentWinHeight: $window.height(),
                            modalGapV: 4,
                            modalGapH: 80,
                            url: url,
                            iframeID: ('popupIframe' + BBB.fn.getUID()),
                            iframeHeight: 750,
                            iframeWidth: 996,
                            scrollbars: 'auto'
                        }, evt.data || {});

                        BBB.eventTarget.append('<iframe type="some_value_to_prevent_js_error_on_ie7" class="posAbs posTopLeft" id="' + options.iframeID + '" src="" width="' + options.iframeWidth + '" height="' + options.iframeHeight + '" scrolling="' + options.scrollbars + '" frameBorder="0"></iframe>');
                        $('#' + options.iframeID).attr('src', options.url).css({
                            'width': options.iframeWidth + 'px',
                            'height': options.iframeHeight + 'px'
                        });

                        BBB.modalDialog.openDialog({
                            wrapperSetting: {
                                type: "html",
                                modalContentHTML: $('#' + options.iframeID),
                                destroyDialog: false
                            },
                            uiDialiogSettings: {
                                title: popupTitle,
                                height: options.iframeHeight + options.modalGapH,
                                width: options.iframeWidth + options.modalGapV,
                                dialogClass: 'popupIframe bridalBookDemo',
                                close: function (event, ui) {
                                    $(this).dialog('destroy').remove();
                                    $body.removeData('qvAttribPopupOpen');

                                    if (typeof $body.data('qvAttribPopupScrollTop') !== 'undefined') {
                                        $window.scrollTop($body.data('qvAttribPopupScrollTop'));
                                        $body.removeData('qvAttribPopupScrollTop');
                                    }

                                    if (typeof $body.data('keepQVPopup') === 'undefined') {
                                        $('.quickViewPopup .ui-dialog-content').dialog('destroy').remove();

                                        if (typeof $body.data('lnkQVPopupScrollTop') !== 'undefined') {
                                            $window.scrollTop($body.data('lnkQVPopupScrollTop'));
                                            $body.removeData('lnkQVPopupScrollTop');
                                        }
                                    }
                                },
                                open: function (event, ui) {
                                    $(this).closest('.ui-dialog').scrollTo();
                                },
                                buttons: btnsObj
                            }
                        });
                    }
                });

                BBB.eventTarget.delegate('h2.ui-accordion-header, h3.ui-accordion-header', 'click', function (evt) {
                    var $this = $(this);

                    if ($this.hasClass('ui-state-default')) {
                        expandDiv($this);
                    } else {
                        collapseDiv($this);
                    }
                });

                BBB.eventTarget.delegate('h2.ui-accordion-header, h3.ui-accordion-header', 'mouseenter', function (evt) {
                    $(this).addClass('ui-state-hover');
                });

                BBB.eventTarget.delegate('h2.ui-accordion-header, h3.ui-accordion-header', 'mouseleave', function (evt) {
                    $(this).removeClass('ui-state-hover');
                });

                BBB.eventTarget.delegate('h2.ui-accordion-header a, h3.ui-accordion-header a', 'click', function (evt) {
                    var $this = $(this);

                    if ($this.hasClass('addNewItem')) {
                        evt.stopPropagation();
                        $this.closest('.ui-accordion-header').unbind('click');
                    } else {
                        evt.preventDefault();
                    }
                });

                BBB.eventTarget.delegate('.sorting li input', 'click', function (evt) {
                    evt.preventDefault();

                    var $this = $(this),
                        thisID = $this.attr('id'),
                        thisVal = $this.val();

                    // if sorting is selected
                    if (thisID === 'sortSeqCat' || thisID === 'sortSeqPrice') {
                        location.href = currentURL + '?sorting=' + thisVal + '&pwsToken=' + getUrlParameter('pwsToken') + '&eventType=' + getUrlParameter('eventType') + '&registryId=' + getUrlParameter('registryId') + '&pwsurl=' + getUrlParameter('pwsurl') + '&view=' + view;
                    // if view is selected
                    } else {
                        location.href = currentURL + '?sorting=' + sortSeq + '&pwsToken=' + getUrlParameter('pwsToken') + '&eventType=' + getUrlParameter('eventType') + '&registryId=' + getUrlParameter('registryId') + '&pwsurl=' + getUrlParameter('pwsurl') + '&view=' + thisVal;
                    }
                });

                if (currentView != 'guest') {
                	BBB.eventTarget.delegate('.btnAjaxSubmitCart', 'click', function (evt) {
                        evt.preventDefault();

                        var $this = $(this),
                            $wrapper = $this.closest('li.productRow'),
                            $frm = $('#frmRowItemRemove');

                        $frm.find('.frmAjaxSubmitData').each(function(){
                            var $t = $(this),
                                fn = ($t.attr('data-ajax-fieldName') && $t.attr('data-ajax-fieldName').trim() !== '')? $t.attr('data-ajax-fieldName').trim(): '';

                            if (fn !== '') {
                                $t.val($wrapper.find('input[name="'+fn+'"].frmAjaxSubmitData').val());
                            }
                        });

                        $frm.find('input[type="submit"]').trigger('click');

                    });



		 			function addWrapperLoader($wrapper)
		 			{
			 			$wrapper.css("position","relative");
	                	$wrapper.prepend('<div class="ajaxBusy"><img class="loader" src="/_assets/global/images/widgets/small_loader_by.gif" /></div>');
	                	$('.ajaxBusy').css({
	        				opacity : '0.7',
	        				display:"none",
	        				//background: "white url(/store/loader_bbby_MD.gif) no-repeat 50% 50%",
	        				background: "white",
	        				width:"808px",
	        				height:"138px",
	        				position:"absolute",
	        				top:"0",
	        				left:"0",
	        				zIndex:"10",
	        				margin:"0 auto",
	        				verticalAlign: "middle"
	        			});

	                	$('.loader').css({
	        				opacity : '1',
	        				zIndex:"10",
	        				marginLeft:"366px",
	        				marginTop:"54px"
	        			});
	                	return $wrapper.find('.ajaxBusy');
		 			};

		 			function removeWrapperLoader($wrapper)
		 			{
		 				$wrapper.find('.ajaxBusy').remove();
		 			};

		 			function updateRegistryFlyout()
	                {
			 			//update the registry flyout to show updated registry information
				 		//we need browse.js loaded in registry for this to work
				 		//copying the fuctionality instead
				 		//BBB.browse.regAjax('#bridalGiftRegistryFlyout', BBB.fn.getUrl('regFlyout'));
				 		var elID = '#bridalGiftRegistryFlyout',
				 		 	fileURL =  BBB.fn.getUrl('regFlyout');

			 	        if (typeof elID !== 'undefined' && $(elID)[0] && typeof fileURL !== 'undefined' && typeof fileURL === 'string' && fileURL.trim() !== '') {
			 	            var $elID = $(elID);

			 	            BBB.service.getAjaxData('regAjax' + BBB.fn.getUID(), {
			 	                oServiceUrl: fileURL,
			 	                oShowPreloader: false,
			 	                oExecuteDefaultErrHndlr: false,
			 	                oAjaxFormat: 'html',
			 	                oSuccessCallback: function (response) {
			 	                    $elID.html(BBB.fn.whiteSpaceFix(response));
			 	                    BBB.fn.doUniform();
			 	                    BBB.fn.triggerSubmit();
			 	                }
			 	            });
			 	        }
	                };

		 			function showUndoRemoveRow($wrapper){
		 				var $textbox,
		 					$frm = $('#frmRowAddToRegistry');

		 				$textbox = $wrapper.find('input.valQtyGiftReg'),
		 				$undoRow = $('#removeUndoRow').clone();
		 				$undoRow.removeAttr("id");

    			 		$undoRow.find('.frmAjaxSubmitData').each(function(){
                            var $t = $(this),
                                fn = ($t.attr('name') && $t.attr('name').trim() !== '')? $t.attr('name').trim(): '';

                            if (fn !== '') {
                                $t.val($wrapper.find('input[name="'+fn+'"].frmAjaxSubmitData').val());
                            }
                        });
    			 		$undoRow.find('input[name="quantity"]').val($textbox.val());
    			 		$undoRow.find('span.blueName').html($wrapper.find('span.blueName').html());
    			 		//$undoRow.find('undoRemoveRegistryItem').data('prodId', $wrapper.find('input[name="prodId"]').val());

    			 		$wrapper.after($undoRow);
    			 		$undoRow.slideDown();
		 			};

		 			function updateCategoryCount($wrapper)
		 			{
			 			categoryCount = $wrapper.siblings('.productRow:visible').length;
				 		categoryTitle = $wrapper.closest('.accordionDiv').prevAll('h2:first');

				 		if(categoryTitle[0])
				 		{
				 			categoryTitle.html(categoryTitle.html().replace(/\(\d+\)/g, '('+categoryCount+')'));
				 		}
				 		return $wrapper;
		 			};

		 			  //initialize old quantity
                    $('input.valQtyGiftReg').each(function(){
                    	var $this = $(this),
                    		$wrapper = $this.closest('li.productRow');

                    		$this.data('oldQuantity',$this.val());
                    });

                	//registry bulk update, 'Remove' item link
                    BBB.eventTarget.delegate('.btnAjaxSubmitRegistry', 'click', function (evt) {
                        evt.preventDefault();

                        var $this = $(this),
                            $wrapper = $this.closest('li.productRow'),
                            $frm = $('#frmRowItemRemove'),
                            $textbox = $this.closest('li.qtyWrapper').find('input.valQtyGiftReg');

                        addWrapperLoader($wrapper).show();

                        //set the old quantity
                		$wrapper.find('input[name="regItemOldQty"]').val($textbox.val());
                        $frm.find('.frmAjaxSubmitData').each(function(){
                            var $t = $(this),
                                fn = ($t.attr('data-ajax-fieldName') && $t.attr('data-ajax-fieldName').trim() !== '')? $t.attr('data-ajax-fieldName').trim(): '';

                            if (fn !== '') {
                                $t.val($wrapper.find('input[name="'+fn+'"].frmAjaxSubmitData').val());
                            }
                        });
                    	$frm.ajaxForm({
                			success: function( responseJson, statusText, xhr, $form)
                			{
                				var categoryCount, categoryTitle, undoRow;
                				if(responseJson.error == "false")
                				{
                			 		$('#regGiftsWanted').html(responseJson.regGiftsWanted + $('#regGiftsWanted').html().replace(/\d/g, "")).effect('bounce',100);
                			 		$('#regGiftsRemaining').html(responseJson.regGiftsRemaining + $('#regGiftsRemaining').html().replace(/\d/g, "")).effect('bounce',100);


                			 		updateRegistryFlyout();

                			 		removeWrapperLoader($wrapper);
                			 		$wrapper.slideUp({complete: function(){showUndoRemoveRow($wrapper);updateCategoryCount($wrapper)}});
                			 		$textbox.data('oldQuantity', $textbox.val());
                			 		$wrapper.find('input[name="regItemOldQty"]').val($textbox.val());
                			 		//showUndoRemoveRow($wrapper);
                				}
                				else
                				{
                					removeWrapperLoader($wrapper);
                					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrongmx);
                				}
        		 			},
        		 			error: function(){
        		 				removeWrapperLoader($wrapper);
            					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
        		 			},
            	        	dataType: 'json'
                	    });//.submit();

                    	//removeWrapperLoader($wrapper);
                    	$frm.find('input[type="submit"]').trigger('click');
                    });


                    //registry bulk update, 'Undo' item link
                    BBB.eventTarget.delegate('.undoRemoveRegistryItem', 'click', function (evt) {
                        evt.preventDefault();

                        var $this = $(this),
                        	$textbox,
                        	$originalProductWrapper,
                        	$wrapper = $this.closest('li.registeryItemUndoRow'),
	 						$frm = $('#frmRowAddToRegistry');

                        //$originalProductWrapper = $wrapper.find('input[name="productId"]').val()
                        $originalProductWrapper =  $wrapper.prevAll('li.productRow:first');

                        $frm.find('.frmAjaxSubmitData').each(function(){
                            var $t = $(this),
                                fn = ($t.attr('data-ajax-fieldName') && $t.attr('data-ajax-fieldName').trim() !== '')? $t.attr('data-ajax-fieldName').trim(): '';

                            if (fn !== '') {
                                $t.val($wrapper.find('input[name="'+fn+'"].frmAjaxSubmitData').val());
                            }
                        });


                        $frm.ajaxForm({
                			success: function( responseJson, statusText, xhr, $form)
                			{
                				var categoryCount, categoryTitle, undoRow;
                				if(responseJson.error == "false")
                				{
                			 		$('#regGiftsWanted').html(responseJson.regGiftsWanted + $('#regGiftsWanted').html().replace(/\d/g, "")).effect('bounce',100);
                			 		$('#regGiftsRemaining').html(responseJson.regGiftsRemaining + $('#regGiftsRemaining').html().replace(/\d/g, "")).effect('bounce',100);

                			 		//update the count of items in the category
                			 		//console.log($wrapper.siblings());



                			 		updateRegistryFlyout();

                			 		//removeWrapperLoader($wrapper);
                			 		$wrapper.slideUp();
                			 		$originalProductWrapper.slideDown().show(400,updateCategoryCount($wrapper).remove());


                				}
                				else
                				{
                					//removeWrapperLoader($wrapper);
                					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
                				}
        		 			},
        		 			error: function(){
        		 				//removeWrapperLoader($wrapper);
            					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
        		 			},
            	        	dataType: 'json'
                	    });//.submit();


                    	$frm.find('input[type="submit"]').trigger('click');

                    });




                    //registry bulk update, 'Update' item link
                    BBB.eventTarget.delegate('a.validateQuantity', 'click', function (evt) {
                        evt.preventDefault();

                        var $this = $(this),
                            $textbox = $this.closest('li.qtyWrapper').find('input.valQtyGiftReg'),
                            $wrapper = $this.closest('li.productRow'),
                            $frm = $('#frmRowItemUpdate');

                        if (!/^\d+$/.test($textbox.val()) || $textbox.val() == 0) {
                            BBB.openMsgDialog(BBB.glblStringMsgs.errInvalidQtyNotNumber, function (e) {
                                $textbox.focus();
                            });
                        }
                        else if ($this.attr('data-trigger-button') && $this.attr('data-trigger-button') !== '' && $('#' + $this.attr('data-trigger-button'))[0])
                        {
                            //$('#' + $this.attr('data-trigger-button')).trigger('click');

                        	addWrapperLoader($wrapper).show();
                    		//$wrapper.find('.ajaxBusy').show(10).delay(1000).hide(10);

                        	//set the old quantity
                    		$wrapper.find('input[name="regItemOldQty"]').val($textbox.data('oldQuantity'));

                    		$frm.find('.frmAjaxSubmitData').each(function(){
                    			var $t = $(this),
							  	fn = ($t.attr('data-ajax-fieldName') && $t.attr('data-ajax-fieldName').trim() !== '')? $t.attr('data-ajax-fieldName').trim(): '';

							  	if (fn !== '') {
						  			$t.val($wrapper.find('input[name="'+fn+'"].frmAjaxSubmitData').val());
							  	}
                    		});
							$frm.find('input[name="modifiedItemIndex"]').val($this.closest('li.qtyWrapper').find('input[name="itemIndex"]').val());
							$frm.find('input[name="modifiedItemQuantity"]').val($textbox.val());

							$frm.ajaxForm({
	            	        	success: function( responseJson, statusText, xhr, $form)
	                			{
	                				var categoryCount, categoryTitle;
	                				if(responseJson.error == "false")
	                				{
	                			 		$('#regGiftsWanted').html(responseJson.regGiftsWanted + $('#regGiftsWanted').html().replace(/\d/g, "")).effect('bounce',100);
	                			 		$('#regGiftsRemaining').html(responseJson.regGiftsRemaining + $('#regGiftsRemaining').html().replace(/\d/g, "")).effect('bounce',100);

	                			 		updateRegistryFlyout();
	                			 		removeWrapperLoader($wrapper);
	                			 		$textbox.data('oldQuantity', $textbox.val());
	                			 		//$wrapper.slideUp();
	                				}
	                				else
	                				{
	                					removeWrapperLoader($wrapper);
	                					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
	                				}
	        		 			},
	        		 			error: function(){
	        		 				removeWrapperLoader($wrapper);
	            					BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
	        		 			},
	            	        	dataType: 'json'
    	            	    });//.submit();

    	                	$frm.find('input[type="submit"]').trigger('click');

    	                    //$(this).closest('.productRow').slideUp();
                        }
                    });

                    $('#content div.expandCollapse ul li.expandAll a').on('click', function (e) {
                        expandAll = true;
                        collapseAll = false;
                        e.preventDefault();
                        expandDiv();
                    });

                    $('#content div.expandCollapse ul li.collapseAll a').on('click', function (e) {
                        expandAll = false;
                        collapseAll = true;
                        e.preventDefault();
                        collapseDiv();
                    });
                }

                if ($('#pageWrapper.giftView')[0]) {
                    $regDataContainer.find('ul.giftViewProduct .changeStore').changeStoreBopusDialog({
                        findItemDesInsideClass: "giftViewProductChangeStoreWrapper",
                        fillDataInDialog: {
                            productImageClass: "productImage",
                            productNameClass: "productName"
                        },
                        fillDataFromPage: {
                            productImageClass: "prodImage",
                            productNameClass: "prodTitle"
                        },
                        changeStoreFormDialogId: "changeStoreDialogWrapper",
                        submitChangeStoreFormOpts: {
                            submitForm: false,
                            fCallBack: function ($itemParentWrapper, storeId) {
                                var $btn = $itemParentWrapper.find('input[type=button][name=btnAddToCart]');
                                if (!$btn[0]) {
                                    $btn = $itemParentWrapper.find('input[type=submit][name=btnAddToCart]');
                                }
                                $btn.trigger('click');
                            }
                        }
                    });
                }

                if ($regDataContainer.find('li.addToCartGiftRegWrapper div.addToCart, li.addToCartGiftRegWrapper a.lnkAddToCartGiftReg')[0]) {
                    $regDataContainer.delegate('li.addToCartGiftRegWrapper div.addToCart input[name=btnAddToCart]', 'click', fAddToCart);
                    $regDataContainer.delegate('li.addToCartGiftRegWrapper a.lnkAddToCartGiftReg', 'click', fAddToCart);
                }

                $(window).on('scroll.regLazyLoad', function() {
                    if (!isLoading && BBB.fn.inView($regDataContainer.find(loaderDIVs + loaderDIVsNum))) {
                        isLoading = true;
                        setTimeout(showAccordion, timeDelayScrollChecks);
                    }

                    if (!$regDataContainer.find(loadMoreDIV)[0]) {
                        $(this).off('.regLazyLoad');
                    }
                });

                managePrintImages();
                BBB.fn.triggerSubmit();
                BBB.fn.doUniform();
                BBB.manageUpDownQtyArrows($regDataContainer);
            };

            setTimeout(function () {
                BBB.service.getAjaxData("mxRegAjaxLoad" + BBB.fn.getUID(), {
                    oAjaxFormat: 'html',
                    oDataForAjax: dataForAjax,
                    oServiceUrl: ajaxUrl,
                    oShowPreloader: false,
                    oAjaxCommMethod: 'POST',
                    oErrorCallBack: function (response) {
                        if ((typeof response === 'string' && response.trim() !== '') || (typeof response !== 'string' && response !== null)) {
                            BBB.openErrorDialog(BBB.glblStringMsgs.somethingWentWrong);
                        }
                    },
                    oSuccessCallback: function (response) {
                        manageAjaxResponse(response);
                    },
                    oExecuteAjaxScripts: true
                });
            }, timeDelayFirstLoad);
        }
    });
})(jQuery, this, this.document);

function emailMxRegistry()
{
            var $this = $(this),
                serviceUrl, formDataForAjax;
            if ($('#pageWrapper.productDetails')[0] || $this.hasClass('avoidGlobalEmailHandler')) {
                return;
            }
            if ($this.parents('#cmsPageHead:first')[0]) {
                serviceUrl = BBB.fn.getUrl('mxEmailAPageCMS');
                formDataForAjax = 'url=' + encodeURIComponent(window.location.href);
            } else {
                serviceUrl = BBB.fn.getUrl('mxEmailAFriend');
                $('#emailAFriendData .emailAFriendFields').each(function(){
                var $t = $(this), param;

                if ($t.val().trim() !== '') {
                    param = $t.attr('name') + '=' + encodeURIComponent($t.val().trim());
                    formDataForAjax = (formDataForAjax === '')? (param): (formDataForAjax + '&' + param);
                }
               });
            }

            BBB.modalDialog.openDialog({
                wrapperSetting: {
                    type: "ajax",
                    ajaxParams: {
                        oDataForAjax: formDataForAjax,
                        oServiceUrl: serviceUrl,
                        oSuccessCallback: callBackEmailAFriend
                    }
                }
            });

        };

        var callBackEmailAFriend = function ($dialog) {
            // refresh captcha
            BBB.fn.getNewCaptcha($dialog.find('#captchaDiv'));
            BBB.fn.doUniform('#frmEmailAFriend');

            BBB.validationUtils.localizeAndValidate({
                form: '#frmEmailAFriend'
            });

            var options = {
                success: function (response) {
                    callBackEmailAFriendSubmit(response, $dialog);
                },
                dataType: 'json'
            };
            $('#frmEmailAFriend').ajaxForm(options);
        };
        
         var callBackEmailAFriendSubmit = function (response, $dialog) {
            var dlgTitle = BBB.fn.getString('emailAFriend');
            // if error and contains a string message to show
            if (response.hasOwnProperty('error') && typeof response.error === 'string' && response.hasOwnProperty('errorMessages') && typeof response.errorMessages === 'string') {
                try {
                    if ($(response.errorMessages)[0]) {
                        if (typeof $(response.errorMessages).attr('title') !== 'undefined' && $(response.errorMessages).attr('title') !== '') {
                            dlgTitle = $(response.errorMessages).attr('title');
                        }
                    }
                } catch (e) {}
                // if error text is 'server'
                if (response.error.toLowerCase() === 'server') {
                    // show the message in a new dialog
                    $dialog.dialog('close');

                    var btnsObj = {};
                    btnsObj[BBB.fn.getString('okBtn')] = function () {
                        $(this).dialog('close');
                    };

                    BBB.modalDialog.openDialog({
                        wrapperSetting: {
                            type: 'html',
                            modalContentHTML: response.errorMessages
                        },
                        uiDialiogSettings: {
                            title: dlgTitle,
                            buttons: btnsObj
                        }
                    });
                } else {
                    // show errors
                    if ($dialog.find('form .pageErrors')[0]) {
                        $dialog.find('form .pageErrors').html(response.errorMessages);
                    } else {
                        $dialog.find('form').prepend('<div class="pageErrors">' + response.errorMessages + '</div>');
                    }
                    // refresh captcha
                    BBB.fn.getNewCaptcha($dialog.find('#captchaDiv'));
                }
                // if success and contains a string message to show
            } else if (response.hasOwnProperty('success') && typeof response.success === 'string') {
                try {
                    if ($(response.success)[0]) {
                        if (typeof $(response.success).attr('title') !== 'undefined' && $(response.success).attr('title') !== '') {
                            dlgTitle = $(response.success).attr('title');
                        }
                    }
                } catch (e) {}

                // show the message in a new dialog
                $dialog.dialog('close');

                var btnsObj = {};
                btnsObj[BBB.fn.getString('okBtn')] = function () {
                    $(this).dialog('close');
                };

                BBB.modalDialog.openDialog({
                    wrapperSetting: {
                        type: 'html',
                        modalContentHTML: response.success
                    },
                    uiDialiogSettings: {
                        title: dlgTitle,
                        buttons: btnsObj
                    }
                });
            }
        };
        

</script>

    </bbb:mxPageContainer>
</dsp:page>