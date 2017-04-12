	<dsp:page>
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
		<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
		<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
		<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
		<dsp:importbean bean="/atg/userprofiling/Profile"/>
		<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
		<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
		
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

		<%-- Validate external parameters --%>
		<dsp:droplet name="ValidateParametersDroplet">
			<dsp:param value="registryId;eventType" name="paramArray" />
			<dsp:param value="${param.registryId};${param.eventType}" name="paramsValuesArray" />
			<dsp:oparam name="error">
			  <dsp:droplet name="RedirectDroplet">
				<dsp:param name="url" value="/404.jsp" />
			  </dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
		<%-- Droplet Placeholder here --%>
				<dsp:getvalueof var="sortSeq" param="sorting"/>
				<c:set var="sortSeq"><dsp:valueof value="${fn:escapeXml(param.sorting)}"/></c:set>
				<dsp:getvalueof var="view" param="view"/>
				<c:set var="view"><dsp:valueof value="${fn:escapeXml(param.view)}"/></c:set>				
				<c:if test="${not empty sortSeq && not empty view}">
				<dsp:droplet name="ValidateParametersDroplet">
					<dsp:param value="sortSeq;view" name="paramArray" />
					<dsp:param value="${param.sorting};${param.view}" name="paramsValuesArray" />
						<dsp:oparam name="error">
			  				<dsp:droplet name="RedirectDroplet">
								<dsp:param name="url" value="/404.jsp" />
			  				</dsp:droplet>
						</dsp:oparam>
				</dsp:droplet>
				</c:if>

		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/multisite/Site"/>
		
		<dsp:getvalueof var="appid" bean="Site.id" />

		<c:choose>
			<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
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

		<bbb:pageContainer>
			<jsp:attribute name="section">registry</jsp:attribute>
			<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
			<jsp:attribute name="pageWrapper">giftView updateRegistry useFB useCertonaJs useMapQuest viewRegistry useStoreLocator</jsp:attribute>
			<jsp:attribute name="bodyClass">registry</jsp:attribute>
			
			<jsp:body>

				<%-- Droplet for showing error messages --%>
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
						<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">
						<div class="error"><dsp:valueof param="message"/></div>
					</dsp:oparam>
				</dsp:droplet>				

				 <c:if test="${not empty registryId}">
					<dsp:droplet name="RegistryInfoDisplayDroplet">
						<dsp:param value="${registryId}" name="registryId" />
						<dsp:oparam name="output">
				
					<dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
					<c:choose>
					<c:when test="${eventTypeVar eq  eventType}">


                    <div id="content" class="container_12 clearfix" role="main">
                    
                        <dsp:form name="copyReg" method="post" id="copyRegForm" style="display:none;">
                            <%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.successURL" value="/store/addgiftregistry/copyRegistryStatus.jsp" />
                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.errorURL" value="/store/addgiftregistry/copyRegistryStatus.jsp" /> --%>
                                    <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="viewRegistryGuest" />
                            <dsp:input  type="hidden" bean="GiftRegistryFormHandler.srcRegistryId" value="${registryId}" />
                            <dsp:input iclass="" type="submit" bean="GiftRegistryFormHandler.copyRegistry" value="Copy This Registry" />
                        </dsp:form>
                        <%-- trying to get value of the logged in user's active registry id --%>
                        <dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
                        
                        <dsp:droplet name="RegistryInfoDisplayDroplet">
                            <dsp:param value="${sessionRegId}" name="registryId" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="eventTypeName" param="registrySummaryVO.eventType"/>
                                    <dsp:form style="display:none;" method="post" id="frmRowAddToRegistry" action="${contextPath}/kickstarters/top_consultants_picks.jsp">
                                        <div class="registryDataItemsWrap listDataItemsWrap" style="display:none;">
                                            <input type="hidden" name="prodId" class="frmAjaxSubmitData _prodId addItemToRegis productId addItemToList" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internaldata="true" />
                                            <input id="quantity" title="Enter Quantity" class="addItemToRegis _qty itemQuantity addItemToList" type="hidden" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity"/>
                                            <input type="hidden" name="skuId"  class="frmAjaxSubmitData addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internaldata="true" />
                                            <input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
                                            <input type="hidden" name="parentProdId" class="frmAjaxSubmitData _prodId addItemToRegis addItemToList" />
                                            <input type="hidden" name="heading1" class="frmAjaxSubmitData _heading1 addItemToRegis addItemToList" />
                                            <input type="hidden" name="bts" class="addToCartSubmitData" value="false" />
                                            <input type="hidden" name="sessionRegId" value="${sessionRegId}" class="frmAjaxSubmitData _prodId addItemToRegis addItemToList" />
                                            <input type="hidden" name="sessionRegType" value="${eventTypeName}" class="frmAjaxSubmitData _prodId addItemToRegis addItemToList" />
                
                                            <dsp:getvalueof var="regIdLst" value="${param.registryId}" />
                                            <dsp:getvalueof var="registryNameLst" value="${param.eventType}" />
                                            <c:if test="${not empty regIdLst}">
                                                <input type="hidden" class="frmAjaxSubmitData addItemToRegis addItemToList" name="registryId" value="${regIdLst}" data-change-store-submit="registryId"/>
                                            </c:if>
                                            <c:if test="${not empty registryNameLst}">
                                                <input type="hidden" name="registryName" class="frmAjaxSubmitData addItemToRegis" value="${registryNameLst}" />
                                            </c:if>
                                            <input type="hidden" name="price"  class="frmAjaxSubmitData addItemToList addItemToRegis" />
                                        </div>
                                    </dsp:form>
                                </dsp:oparam>
                            </dsp:droplet>


			  
								<div class="row">
									<div class="left small-11 columns no-padding"> 
										<ul class="tbs-crumbs"> 
											<li>
												${eventType}
											</li> 
										</ul> 
									</div> 
									<div class="row"> 
										<div class="right print-email"> 
											<a href="#" class="pdp-sprite email btnEmail" title="Email Cart">
												<span></span>
											</a>|<a href="#" class="pdp-sprite print print-trigger" title="Print">
												<span></span>
												</a> 
										</div> 
									</div> 
										<div id="emailModal" class="reveal-modal medium" data-reveal=""> <a class="close-reveal-modal">&#215;</a> </div>
								
									<div class="small-12 columns no-padding">
										<dsp:setvalue bean="SessionBean.registryTypesEvent" value="${eventTypeCode}"/>
											<h1 class="small-12 large-5 columns fl">
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
											<dsp:setvalue  bean="EmailHolder.handlerName" value="EmailRegistry" />
											<c:set var="emailTitle"><bbbl:label key='lbl_mng_regitem_emailreg_title' language="${pageContext.request.locale.language}" /></c:set>
											<dsp:setvalue  bean="EmailHolder.values.title" value="${emailTitle}" />
											<dsp:setvalue  bean="EmailHolder.values.senderEmail" beanvalue="Profile.email"/>
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
				
											<div id="emailAFriendData">
												<dsp:getvalueof bean="EmailHolder.values.dateCheck" var="dateCheck"/>
												<dsp:getvalueof bean="EmailHolder.values.daysToNextCeleb" var="daysToNextCeleb"/>
												<dsp:getvalueof bean="EmailHolder.values.daysToGo" var="daysToGo"/>
												<dsp:getvalueof bean="EmailHolder.values.eventType" var="eventType"/>
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
												<dsp:getvalueof bean="EmailHolder.values.eventDate" var="eventDate"/>
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
												<input type="hidden" name="eventDate" value="${eventDate}" class="emailAFriendFields"/>
												<input type="hidden" name="eventTypeRegistry" value="${eventTypeRegistry}" class="emailAFriendFields"/>
												<input type="hidden" name="subject" value="${subject}" class="emailAFriendFields"/>
											</div>
				
									</dsp:form> <%-- CR9 - Don't want to allow people to share someone else's registry online.
											Remove Like button while viewing another person's registry --%>
									<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
									<dsp:contains var="isPresent" values="${sessionMapValues.userRegistriesList}"
														object="${registryId}"/>
									<c:if test="${FBOn && isPresent}">
									</c:if>

								

								<div class="small-12 large-7 columns registryDetails">
								<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
								<c:set var="registryTable">registryTableBridal</c:set>
								<c:if test="${appid == 'TBS_BuyBuyBaby'}">
									<c:set var="registryTable">registryTableBaby</c:set>
								</c:if>

								<div class="${registryTable} ">
									<c:if test="${eventTypeCode eq 'BA1' }">

										<div class="registryBabyNameBlock">
											<div class="small-6 columns">
												Registry # :  <dsp:valueof param="registryId"/>
											</div>
											<div class="small-6 columns">
												<dsp:getvalueof var="validCheck" param="validCheck"/>
													<c:choose>
														<c:when test="${ not empty validCheck && validCheck && !isTransient}">
														<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
														<dsp:a href="${regUrl}" title="Sign in and manage">
															<dsp:param name="registryId" value="${registryId}"/>
															<dsp:param name="eventType" value="${eventTypeVar}"/>
															<%-- <bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" /> --%>
														</dsp:a>
														</c:when>
														<c:when test="${ not empty validCheck && !validCheck && !isTransient}">
															<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/my_registries.jsp"/>
															<%-- <dsp:a href="${regUrl}" title="Sign in and manage">
																<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
															</dsp:a> --%>
														</c:when>
														<c:when test="${isTransient }">
														<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
															<dsp:a href="${regUrl}" title="Sign in and manage">
															<dsp:param name="registryId" value="${registryId}"/>
																<dsp:param name="eventType" value="${eventTypeVar}"/>
																 <span><bbbl:label key='lbl_mng_regitem_isthisreg' language="${pageContext.request.locale.language}" />
																<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
															</dsp:a>
														</c:when>
													</c:choose>
												</span>
											</div>
										</div>
									</c:if>
									<dsp:getvalueof var="babyName" param="registrySummaryVO.eventVO.babyName"/>
									<dsp:getvalueof var="babyShowerDate" param="registrySummaryVO.eventVO.showerDateObject"/>
									<dsp:getvalueof var="babyGender" param="registrySummaryVO.eventVO.babyGender"/>
									<dsp:getvalueof var="babyNurseryTheme" param="registrySummaryVO.eventVO.babyNurseryTheme"/>

									<div class="small-12 columns no-padding">
												<span class="boldTitle">Registry Type:</span>
												<span><dsp:valueof param="registrySummaryVO.eventType"/></span>
												<c:choose>
												<c:when test="${eventTypeCode eq 'BA1' }">
												   <span class="boldTitle">Baby's Expected Arrival Date:</span>
												   <span><dsp:getvalueof var="eventDateStr" param="eventDate"/>${eventDateStr}</span><br/>
												</c:when>
												<c:otherwise>
													<span class="boldTitle">Event Date:</span>
													<span><dsp:getvalueof var="eventDateStr" param="eventDate"/>${eventDateStr}</span>
												</c:otherwise>
												</c:choose>
			
												<c:if test="${eventTypeCode eq 'BA1' }">
													<c:if test="${not empty babyShowerDate}">
														<span class="boldTitle">Shower Date:</span>
														<span><dsp:valueof param="registrySummaryVO.eventVO.showerDateObject" converter="date" date="MM/dd/yyyy"/></span>
													</c:if>
													<c:if test="${not empty babyName}">
														<span class="boldTitle"><bbbl:label key='lbl_mng_regitem_baby_name' language="${pageContext.request.locale.language}" />:</span>
														<span><dsp:valueof param="registrySummaryVO.eventVO.babyName"/></span>
													</c:if>
													<c:if test="${not empty babyGender}">
														<span class="boldTitle">Baby Gender:</span>
														<span>
															<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GetGenderKeyDroplet">
																<dsp:param name="genderKey" value="${babyGender}"/>
																<dsp:param name="inverseflag" value="true"/>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="genderValue" param="genderCode" />
																</dsp:oparam>
															</dsp:droplet>
															${genderValue}
														</span>
													</c:if>
													<c:if test="${not empty babyNurseryTheme}">
														<span class="boldTitle"><bbbl:label key='lbl_mng_regitem_nursery_theme' language="${pageContext.request.locale.language}" />:</span>
														<span><dsp:valueof param="registrySummaryVO.eventVO.babyNurseryTheme"/></span>
													</c:if>
												</c:if>
												<c:if test="${eventTypeCode ne 'BA1' }">
													<span class="boldTitle">Registry No.:</span>
													<span><dsp:valueof param="registryId"/></span>
												</c:if>
								</div>



                        </div>
                    </div>
                    <hr/>
                    </div>
                    </div>
                    <!-- Copy Registry changes start -->
                    <c:choose>
                        <c:when test="${registryId!=sessionRegId}">
                            <dsp:getvalueof var="totalToCopy" param="registrySummaryVO.giftRegistered"/>
                            <dsp:droplet name="AddItemToGiftRegistryDroplet">
                                <dsp:param name="siteId" value="${appid}"/>
                                    <dsp:oparam name="output">
                                    <c:set var="sizeValue">
                                        <dsp:valueof param="size" />
                                    </c:set>
                                </dsp:oparam>
                            </dsp:droplet>
                            <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
                            <c:choose>
                                <c:when test="${totalToCopy>=1}">
                                    <c:choose>
                                        <c:when test="${!isTransient }">
                                        <c:choose>
                                            <c:when test="${sizeValue>=1 }">
                                                <div id="copyRegistrySection" class="kickstarterSection row">
                                                    <div class="kickstarterSectionHeader small-12 columns">
                                                    <div class="small-12 large-3 columns">                   
                                                        <h2><bbbt:textArea key="txt_copy_this_registry_header" language ="${pageContext.request.locale.language}"/></h2>
                                                    </div>
                                                    <div class="small-12 large-9 columns">
                                                        <p><bbbt:textArea key="txt_copy_this_registry_description" language ="${pageContext.request.locale.language}"/></p>
                                                    </div>
                                                         <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
                                                         <dsp:form name="copyReg" method="post" id="copyRegForm" style="display:none;">
                                                      
                                                           <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="viewRegistryGuest" />
                                                            <%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.successURL" value="/tbs/addgiftregistry/copyRegistryStatus.jsp" />
                                                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.errorURL" value="/tbs/addgiftregistry/copyRegistryStatus.jsp" /> --%>
                                                            <dsp:input  type="hidden" bean="GiftRegistryFormHandler.srcRegistryId" value="${registryId}" />
                                                            <dsp:input iclass="" type="submit" bean="GiftRegistryFormHandler.copyRegistry" value="Copy This Registry" />
                                                        </dsp:form>
                                                        <%-- --%>
                                                    <div class="button_active marTop_10 marLeft_25 omega">
                                                        <input class="tiny button transaction" type="button" class="btnCopyRegistry" value="Copy This Registry" />
                                                    </div>
                                                    </div>
                                                </div>
                                            </c:when>
                                        </c:choose>
                                        </c:when>
                                    </c:choose>
                                </c:when>
                            </c:choose>
                            </c:when>
                        </c:choose>
                        <!-- Copy Registry changes end -->


							<%--Including the Registry Items Page --%>
							<c:set var="maxBulkSize" scope="request">
								<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
							</c:set>

							<div id="regAjaxLoad" data-currentView="guest" data-registryId="${registryId}" data-startIdx="0" data-isGiftGiver="true" data-blkSize="${maxBulkSize}" data-isAvailForWebPurchaseFlag="false"  data-userToken="UT1021" data-sortSeq="${sortSeq}" data-eventTypeCode="${eventTypeCode}" data-eventType="${eventType}" data-pwsurl="${pwsurl}" data-view="${view}" >
									<div class="ajaxLoadWrapper">
										<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
									</div>
							</div>

							<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />          
							<dsp:getvalueof id="ownerRegEventType"  param="giftRegistryViewBean.registryName"/>
							<%-- TODO make variable with item count --%>
							<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="registryName"/>
							<div id="confirmCopyRegistryDialog" title="${totalToCopy} Items Will be Added to Your <c:if test="${not empty ownerRegEventType}">${ownerRegEventType}</c:if> Registry" class="hidden">
								<bbbt:textArea key="txt_copy_this_registry_confirm_modal_msg" language ="${pageContext.request.locale.language}"/>
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
				<div class="row">
					<div class="small-12 columns">
						<div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
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
					s.eVar23 = '${fn:escapeXml(eventType)}';
					s.eVar24 = '${fn:escapeXml(registryId)}';
					var s_code=s.t();if(s_code)document.write(s_code);
			   }
			</script>
		</jsp:attribute>
    </bbb:pageContainer>
    <c:choose>
     <c:when test="${minifiedJSFlag}">
      <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/min/browse.min.js?v=${buildRevisionNumber}"></script>
     </c:when>
     <c:otherwise>
      <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/browse.js?v=${buildRevisionNumber}"></script>
     </c:otherwise>
    </c:choose>
</dsp:page>
