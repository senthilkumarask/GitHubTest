<dsp:page>
<style type="text/css">
	#registryHeader div.grid_2 {
		width: 125px;
	}
</style>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
  	<dsp:importbean var="storeConfig" bean="/atg/store/StoreConfiguration"/>
  	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<%--<c:set var="saSrc" value="//cdn.socialannex.com/partner/9411181/universal.js" />--%>
	<c:set var="WarrantyOn" scope="request">
		<bbbc:config key="WarrantyOn" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="giftGiverItemsChecklistFlag" scope="request">
		<bbbc:config key="GiftGiver_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="regItemsWSCall" scope="request">
		<bbbc:config key="RegItemsWSCall" configName="FlagDrivenFunctions" />
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
		<dsp:getvalueof var="requestURL" bean="/OriginatingRequest.requestURIWithQueryString"/>
        <dsp:getvalueof param="fromGiftGiver"  var="fromGiftGiver"/>
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

	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<%-- BBBSL-4343 DoubleClick Floodlight START  
	    <c:if test="${DoubleClickOn}">
		     <c:set var="rkgcollectionProd" value="${fn:trim(rkgProductList)}" />
			 <c:choose>
				 <c:when test="${not empty rkgcollectionProd }">
				 	<c:set var="rkgProductList" value="${rkgcollectionProd}"/>
				 </c:when>
				 <c:otherwise>
				 	<c:set var="rkgProductList" value="null"/>
				 </c:otherwise>
			 </c:choose>
			 <c:choose>
	   		 <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
	   		   <c:set var="cat"><bbbc:config key="cat_registry_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		 <c:when test="${(currentSiteId eq BedBathUSSite)}">
		    	<c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
		    	<c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    	<c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
	    	</c:when>
	   		 </c:choose>
	 		<dsp:include page="/_includes/double_click_tag.jsp">
	 			<dsp:param name="doubleClickParam"
	 			value="src=${src};type=${type};cat=${cat};u10=null,u11=null"/>
	 		</dsp:include>
 		</c:if>
		DoubleClick Floodlight END --%>

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



	<%-- Droplet for showing error messages --%>
	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
		<dsp:oparam name="output">
			<div class="error"><dsp:valueof param="message"/></div>
		</dsp:oparam>
	</dsp:droplet>


<%-- Droplet Placeholder here --%>
<dsp:getvalueof var="sortSeq" value="${fn:escapeXml(param.sorting)}"/>
<dsp:getvalueof var="view" value="${fn:escapeXml(param.view)}" />

 <c:if test="${not empty registryId}">
	<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:param value="${fromGiftGiver}" name="fromGiftGiver" />
        <dsp:param value="${requestURL}" name="requestURL" />
		<dsp:oparam name="output">

    <dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
    <dsp:getvalueof var="eventDateStr" param="eventDate"/>
    <dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
    <dsp:getvalueof var="registryVO" param="registryVO"/>
	<dsp:getvalueof var="regEventDate" param="registrySummaryVO.eventDate"/>
	<dsp:getvalueof var="regIsPublic" param="registryVO.isPublic" />
	<dsp:getvalueof var="registryTitle" param="registrySummaryVO.regTitle" />
	<dsp:getvalueof var="isChecklistDisabled" param="isChecklistDisabled"/>
	<c:if test="${regIsPublic == 1}">
		<dsp:getvalueof var="regPublic" value="true"/>
	</c:if>
	
	<!-- 	BBBSL-9360 | Checking whether the registry is public or private and setting the index and follow value -->
	<c:choose>
		<c:when test="${registrySummaryVO.isPublic eq '1'}">
				<c:set var= "indexValue" value="true"></c:set>
				<c:set var= "followValue" value="true"></c:set>
		</c:when>
		<c:otherwise>
			<c:set var= "indexValue" value="false"></c:set>
			<c:set var= "followValue" value="false"></c:set>
		</c:otherwise>
	</c:choose>
	
	<!-- 	BBBSL-9360 ends -->

		<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">giftView updateRegistry useFB useCertonaJs useMapQuest viewRegistry useStoreLocator useGoogleAddress</jsp:attribute>
		<jsp:attribute name="regTitle">${registryTitle}</jsp:attribute>
	<jsp:body>

	<c:choose>
	<c:when test="${eventTypeVar eq  eventType && regPublic}">


	<div id="content" class="container_12 clearfix" role="main">

		<dsp:getvalueof param="invalidateTokenError"  var="invalidateTokenError"/>
		<c:if test="${not empty invalidateTokenError}">
			<div class="container_12 clearfix">
				<div class="grid_12">
					<div class="error marTop_20"><bbbe:error key="${invalidateTokenError}" language="${pageContext.request.locale.language}"/></div>
				</div>
	        </div>
		</c:if>
			<dsp:form name="copyReg" method="post" id="copyRegForm" style="display:none;">
				<%-- Client DOM XSRF | Part -1

				<dsp:input type="hidden" bean="GiftRegistryFormHandler.successURL" value="/store/addgiftregistry/copyRegistryStatus.jsp" />
				<dsp:input type="hidden" bean="GiftRegistryFormHandler.errorURL" value="/store/addgiftregistry/copyRegistryStatus.jsp" /> --%>
				<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="copyRegistry" />
				<dsp:input  type="hidden" bean="GiftRegistryFormHandler.srcRegistryId" value="${registryId}" />
	        	<dsp:input iclass="" type="submit" bean="GiftRegistryFormHandler.copyRegistry" value="Copy This Registry" />
			</dsp:form>
			
			
			<%-- trying to get value of the logged in user's active registry id --%>
			<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
			<dsp:getvalueof bean="SessionBean.values.registrySkinnyVOList" var="registrySkinnyVOList" /> 
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${registrySkinnyVOList}" />
				<dsp:oparam name="output">
					<dsp:param name="futureRegList" param="element" />
					<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
					<dsp:getvalueof var="alternateNumber"
						param="futureRegList.alternatePhone" />
					<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
					<%--BBBH-5957: input with blank value added in case registry id doesnot match--%>
					<c:choose>
						<c:when test="${regId eq sessionRegId}">
							<input type="hidden" id="${regId}" value="${alternateNumber}" name="altNumber" />
							<input type="hidden" id="regPoBoxFlag" value="${poBoxAddress}" />
						</c:when>
						<c:otherwise>
							<input type="hidden" id="${regId}" value="" name="altNumber" />
						</c:otherwise>
					</c:choose>
				</dsp:oparam>
			</dsp:droplet>

					<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="eventTypeName"/>
					
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

				            <dsp:getvalueof var="regIdLst" value="${fn:escapeXml(param.registryId)}" />
				            <dsp:getvalueof var="registryNameLst" value="${fn:escapeXml(param.eventType)}" />
				            <c:if test="${not empty regIdLst}">
				            	<input type="hidden" class="frmAjaxSubmitData addItemToRegis addItemToList" name="registryId" value="${regIdLst}" data-change-store-submit="registryId"/>
				            </c:if>
				            <c:if test="${not empty registryNameLst}">
				            	<input type="hidden" name="registryName" class="frmAjaxSubmitData addItemToRegis" value="${registryNameLst}" />
				            </c:if>
				            <input type="hidden" name="price"  class="frmAjaxSubmitData addItemToList addItemToRegis" />
				     	</div>
					<input type="submit" class="visuallyhidden" value=" " />
					</dsp:form>
				
	
			<div id="registryHeader" class="grid_12"  >
				<div id="sa_s22_instagram" class="img_placeholder grid_3 alpha omega fl">
				</div>
				<script type="text/javascript">
					var
						site_id= '9411181',
						sa_page="1",
						sa_instagram_user_email= '${userEmail}',
						sa_instagram_registry_id= '${registryId}',
						sa_instagram_registry_type= '${eventType}',
						sa_instagram_registry_is_owner_view= '0';
					(function() {function sa_async_load() {
						var sa = document.createElement('script');sa.type = 'text/javascript';
						sa.async = true;sa.src = '${saSrc}';
						var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);}
						if (window.attachEvent) {window.attachEvent('onload', sa_async_load);}
						else {window.addEventListener('load', sa_async_load,false);}})();
				</script>
	    		<div class="grid_6 alpha">
            		<div class="regInfo">

            			<h1 class="registrantNames">
	                    	<dsp:droplet name="IsEmpty">
							   	<dsp:param param="registrySummaryVO.coRegistrantFirstName" name="value"/>
							   	<dsp:oparam name="false">
							   		<dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/> <dsp:valueof param="registrySummaryVO.primaryRegistrantLastName"/>
							   		&amp;
									<dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/> <dsp:valueof param="registrySummaryVO.coRegistrantLastName"/>
							   	</dsp:oparam>
							   	<dsp:oparam name="true">
							   		<dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/>  <dsp:valueof param="registrySummaryVO.primaryRegistrantLastName"/>
								</dsp:oparam>
							</dsp:droplet>
	                    </h1>

	                    <div class="eventDateRow">
	                    	<span class="eventDateHeader">
		                    	<c:choose>
									<c:when test="${eventTypeCode eq 'BA1' }">
										Expected Arrival Date:
									</c:when>
									<c:otherwise>
										<dsp:valueof param="registrySummaryVO.eventType"/> <bbbl:label key="lbl_registry_header_date" language="${pageContext.request.locale.language}" />
									</c:otherwise>
								</c:choose>
							</span>
	                        <span class="eventDate">${eventDateStr}</span>
							<span class="registryIdHeader"><bbbl:label key="lbl_registry_id" language="${pageContext.request.locale.language}" /></span>
							 <span class="registryId"><dsp:valueof param="registrySummaryVO.registryId"/></span>
							<c:if test="${eventType == 'Baby'}">

								<dsp:getvalueof var="babyName" param="registrySummaryVO.eventVO.babyName"/>
								<dsp:getvalueof var="babyShowerDate" param="registrySummaryVO.eventVO.showerDateObject"/>
					            <dsp:getvalueof var="babyGender" param="registrySummaryVO.eventVO.babyGender"/>
					            <dsp:getvalueof var="babyNurseryTheme" param="registrySummaryVO.eventVO.babyNurseryTheme"/>



		                        <span class="registryType"><span class="icon-fallback-text"><span class="icon-gift" aria-hidden="true"></span></span>${eventType} Registry</span>
		                        <span class="getGender"><bbbl:label key="lbl_event_baby_gender" language="${pageContext.request.locale.language}" />:
		                        	<c:choose>
		                        		<c:when test="${babyGender == 'B'}">
			                        		It's a Boy!
			                        	</c:when>
		                        		<c:when test="${babyGender == 'G'}">
		                        			It's a Girl!
		                        		</c:when>
		                        		<c:when test="${babyGender == 'T'}">
		                        			It's multiples!
		                        		</c:when>
		                        		<c:otherwise>
		                        			It's a surprise!
		                        		</c:otherwise>
		                        	</c:choose>
		                        </span>
		                    </c:if>
	                    </div>


            		</div>
            	</div>
            	<div id="regGuestCounter" class="grid_3 alpha omega">

	            	<div class="counters clearfix">
	                    <div class="counterWrapper">
	                    	<dsp:getvalueof var="eventType" param="eventType"/>

	                        <span class="counterHeader">
	                        	<dsp:getvalueof var="eventType" param="eventType"/>
	                        	<dsp:droplet name="DateCalculationDroplet">
									<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
									<dsp:param name="convertDateFlag" value="true" />
									<dsp:oparam name="output">
										<dsp:setvalue bean="EmailHolder.values.daysToGo" paramvalue="daysToGo" />
										<dsp:setvalue  bean="EmailHolder.values.dateCheck" paramvalue="check" />
										<dsp:setvalue  bean="EmailHolder.values.daysToNextCeleb" paramvalue="daysToNextCeleb" />

										<dsp:droplet name="Switch">

	                            			<dsp:param name="value" param="check" />
	                            			<dsp:oparam name="true">
	                            				<bbbl:label key="lbl_registry_header_days_to_go" language="${pageContext.request.locale.language}" />

											</dsp:oparam>
											<dsp:oparam name="false">
												<%-- <c:choose>
													<c:when test="${ not empty eventType && eventType eq 'Baby'}">
														<span></span>
														<strong><bbbl:label key='lbl_mng_regitem_welcome_parenthood' language="${pageContext.request.locale.language}" /></strong>
													</c:when>
													<c:otherwise>
														<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
														<strong><bbbl:label key='lbl_regflyout_everafter' language="${pageContext.request.locale.language}" /></strong>
													</c:otherwise>
												</c:choose> --%>

												<c:choose>
				                					<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
				                						<c:choose>
						                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
																<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
										                		<span class="txtDaysLeft counterValue">${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></span>
							                				</c:when>
							                				<c:otherwise>
						                						<span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"> Days Since Event</span>
						                					</c:otherwise>
					                					</c:choose>
				                					</c:when>
				                					<c:otherwise>

					                					<c:choose>
						                					<c:when test="${not empty eventType && eventType eq 'Wedding'}">
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span>
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                						<span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
					                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
								                				<span>${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Retirement'}">
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span>
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
					                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
						                						<span class="txtDaysLeft counterValue">${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" />*</span>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span>
						                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
						                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
							                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span>
							                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></span>
						                					</c:when>
						                					<c:otherwise>
						                						<span class="txtDaysLeft counterValue"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter">Days Since Event</span>
						                					</c:otherwise>
					                					</c:choose>
				                					</c:otherwise>
			                					</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
									<dsp:oparam name="error">
										<dsp:valueof param="errorMsg"></dsp:valueof>
									</dsp:oparam>
								</dsp:droplet>

	                        </span>


	                        	<dsp:droplet name="DateCalculationDroplet">
									<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
									<dsp:param name="convertDateFlag" value="true" />
									<dsp:oparam name="output">
	                        			<dsp:droplet name="Switch">

	                            			<dsp:param name="value" param="check" />
	                            			<dsp:oparam name="true">
	                            				<span class="counterValue">
	                            					<dsp:valueof param="daysToGo"/>
	                            				</span>
											</dsp:oparam>
											<dsp:oparam name="false">

												<%--
												<c:choose>
				                					<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
				                						<c:choose>
						                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
					                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
																${daysToNextCeleb}
							                				</c:when>
							                				<c:otherwise>
							                					<dsp:valueof param="daysToGo"/>
							                				</c:otherwise>
					                					</c:choose>
				                					</c:when>
				                					<c:otherwise>
					                					<c:choose>
						                					<c:when test="${not empty eventType && eventType eq 'Wedding'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
					                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
																${daysToNextCeleb}
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Retirement'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
					                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
																${daysToNextCeleb}
						                					</c:when>
					                						<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
						                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
						                						<dsp:valueof param="daysToGo"/>
						                					</c:when>
						                					<c:otherwise>
							                					<dsp:valueof param="daysToGo"/>
						                					</c:otherwise>
					                					</c:choose>
				                					</c:otherwise>
			                					</c:choose>
			                					--%>
											</dsp:oparam>
										</dsp:droplet>
	                        		</dsp:oparam>
									<dsp:oparam name="error">
										<dsp:valueof param="errorMsg"></dsp:valueof>
									</dsp:oparam>
								</dsp:droplet>



	                    </div>

	                </div>
		        </div>
            </div>


            <div id="regToolbar" class="noprint grid_12 ">
            	<c:if test="${isTransient }">
					<div id="regHeaderSignInMSG">
						<span><bbbl:label key='lbl_mng_regitem_isthisreg' language="${pageContext.request.locale.language}" /></span>
						
						<dsp:a page="/account/Login"><bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" /></dsp:a>
						<%--
						<dsp:getvalueof var="validCheck" param="validCheck"/>
						<c:choose>
							<c:when test="${ not empty validCheck && validCheck && !isTransient}">
								<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
								<dsp:a href="${regUrl}" title="Sign in and manage">
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="eventType" value="${eventTypeVar}"/>
									<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
								</dsp:a>
							</c:when>
							<c:when test="${ not empty validCheck && !validCheck && !isTransient}">
								<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/my_registries.jsp"/>
								<dsp:a href="${regUrl}" title="Sign in and manage">
									<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
								</dsp:a>
							</c:when>
							<c:when test="${isTransient }">
								<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
								<dsp:a href="${regUrl}" title="Sign in and manage">
								<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="eventType" value="${eventTypeVar}"/>
									<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
								</dsp:a>
							</c:when>
						</c:choose>
						--%>
					</div>
				</c:if>
                <div class="regUtilityLinks topLevel noprint">
                	<ul>
                		<li>
							<a href="#" class="btnEmail" onclick="javascript:emailRegistry();" title='<bbbl:label key='lbl_mng_regitem_emailreg' language="${pageContext.request.locale.language}" />'>
								<span class="icon-fallback-text"><span class="icon-envelope" aria-hidden="true"></span><span class="icon-text"><bbbl:label key='lbl_mng_regitem_emailreg' language="${pageContext.request.locale.language}" /></span></span><bbbl:label key='lbl_mng_regitem_emailreg' language="${pageContext.request.locale.language}" />
							</a>


                			<dsp:form method="post" action="index.jsp" id="frmRegistryInfo">

							<%-- Email Specific --%>
							
		  					<dsp:getvalueof var="guest_registry_uri" param="registryURL"/>

                      <%--   <c:choose>
                             <c:when test="${ fromGiftGiver == 'true'}">
                                    <c:set var="registryURL" value="${guest_registry_uri}" />
			          		 </c:when>
			          		 <c:otherwise>
                                 <c:url var="registryURL"
									value="${scheme}://${serverName}:${serverPort}${contextPath}${guest_registry_uri}">
			            			<c:param name="registryId" value="${registryId}"/>
			            			<c:param name="eventType" value="${eventType}"/>
			            			<c:param name="pwsurl" value="${pwsurl}"/>
			          			</c:url>
			          		 </c:otherwise>
                        </c:choose>  --%>
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
							<dsp:input type="hidden" name="isInternationalCustomer"bean="SessionBean.internationalShippingContext" value="${isInternationalCustomer}" />

	                        </div>
							<input aria-hidden="true" type="submit" class="visuallyhidden" value=" " />

						</dsp:form>


                		</li>

                		<li id="">
                			<c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_regitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
                    		<a href="#" class="btnPrint" title="${btnPrintTitle}">
                    			<span class="icon-fallback-text"><span class="icon-print" aria-hidden="true"></span><span class="icon-text">print</span></span>${btnPrintTitle}
                    		</a>
                    	</li>
                    	<li>
							<c:set var="linkContent">
								<span class="icon-fallback-text">
									<span class="icon-bubbles" aria-hidden="true"></span>
									<span class="icon-text"></span>
								</span>
								<span class="toolLbl">Chat Live</span>
							</c:set>
							<div id="chatNowLink">
								<dsp:include page="/common/click2chatlink.jsp">
						        	<dsp:param name="pageId" value="2"/>
						        	<dsp:param name="linkContent" value="${linkContent}"/>
						        	<dsp:param name="divApplied" value=""/>
						        </dsp:include>
					        </div>
				        </li>
				        <%--
						<li>
							<c:set var="announcementCards"><bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="printAtHomeLink">
								<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
						    </c:set>									
							<a href="${contextPath}/${printAtHomeLink}?registryId=${registryId}" class="" title="${announcementCards}" >
								<span class="icon-fallback-text"><span class="icon-file-text-o" aria-hidden="true"></span><span class="icon-text">${announcementCards}</span></span>${announcementCards}
							</a>
                		</li>
						--%>
                	</ul>
                </div>
            </div>

			<%-- TODO - remove this whole section!! --%>

			<!--
        	<div class="grid_12">
				<div class="grid_6 alpha omega">
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

	        	<div class="grid_4 marTop_20">


							<c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_regitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
							<div class="button">
                                <a href="#" class="btnPrint" title="${btnPrintTitle}"><span class="btnPrint print">${btnPrintTitle}</span></a>
                            </div>


						<%-- Email Specific

						<dsp:form method="post" action="index.jsp" id="frmRegistryInfo">


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
						<div class="button">
							<a href="#" class="btnEmail" onclick="javascript:emailRegistry();" title='<bbbl:label key='lbl_mng_regitem_emailreg' language="${pageContext.request.locale.language}" />'><bbbl:label key='lbl_mng_regitem_emailreg' language="${pageContext.request.locale.language}" /></a>
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

					</dsp:form>	--%>

							<%-- CR9 - Don't want to allow people to share someone else's registry online.
										Remove Like button while viewing another person's registry --%>
								<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
								<dsp:contains var="isPresent" values="${sessionMapValues.userRegistriesList}" object="${registryId}"/>


					</div>


       			<dsp:include page="/common/click2chatlink.jsp">
					<dsp:param name="pageId" value="2"/>
	                <dsp:param name="divApplied" value="${true}"/>
					<dsp:param name="divClass" value="grid_2 omega marTop_20"/>
				</dsp:include>



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
								<span><bbbl:label key='lbl_mng_regitem_isthisreg' language="${pageContext.request.locale.language}" />
								<dsp:getvalueof var="validCheck" param="validCheck"/>
									<c:choose>
										<c:when test="${ not empty validCheck && validCheck && !isTransient}">
										<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
										<dsp:a href="${regUrl}" title="Sign in and manage">
											<dsp:param name="registryId" value="${registryId}"/>
											<dsp:param name="eventType" value="${eventTypeVar}"/>
											<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
										</dsp:a>
										</c:when>
										<c:when test="${ not empty validCheck && !validCheck && !isTransient}">
											<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/my_registries.jsp"/>
											<dsp:a href="${regUrl}" title="Sign in and manage">
												<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
											</dsp:a>
										</c:when>
										<c:when test="${isTransient }">
										<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
											<dsp:a href="${regUrl}" title="Sign in and manage">
											<dsp:param name="registryId" value="${registryId}"/>
												<dsp:param name="eventType" value="${eventTypeVar}"/>
												<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
											</dsp:a>
										</c:when>
									</c:choose>
								</span>


                          		<span class="icon icon-file-text-o"></span>
								<a href="${contextPath}/giftregistry/printCards/printCards.jsp?registryId=${registryId}" class="" title="<bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" />"><bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" /></a>

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
									<th class="width_2"><span>Registry Type</span></th>
									<c:choose>
									<c:when test="${eventTypeCode eq 'BA1' }">
										<th class="width_2"><span>Baby's Expected Arrival Date</span></th>
									</c:when>
									<c:otherwise>
										<th class="width_2"><span>Event Date</span></th>
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
										<th class="width_2"><span>Registry No.</span></th>
										<th class="width_6"><span>&nbsp;</span></th>
									</c:if>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td class="width_2"><span><dsp:valueof param="registrySummaryVO.eventType"/></span></td>
									<td class="width_2">
									<span>${eventDateStr}</span></td>

									<c:if test="${eventTypeCode eq 'BA1' }">
										<c:if test="${not empty babyShowerDate}">
											<td class="width_2">
											<span><dsp:valueof param="registrySummaryVO.eventVO.showerDateObject" converter="date" date="MM/dd/yyyy"/>
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
										<td class="width_6"><span class="txt"><bbbl:label key='lbl_mng_regitem_isthisreg' language="${pageContext.request.locale.language}" />
										<a href="#" title="Sign in and manage">
										<dsp:getvalueof var="validCheck" param="validCheck"/>
										<c:choose>
											<c:when test="${ not empty validCheck && validCheck && !isTransient}">
											<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
											<dsp:a href="${regUrl}" title="Sign in and manage">
												<dsp:param name="registryId" value="${registryId}"/>
												<dsp:param name="eventType" value="${eventTypeVar}"/>
												<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
											</dsp:a>
											</c:when>
											<c:when test="${ not empty validCheck && !validCheck && !isTransient}">
												<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/my_registries.jsp"/>
												<dsp:a href="${regUrl}" title="Sign in and manage">
													<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
												</dsp:a>
											</c:when>
											<c:when test="${isTransient }">
											<dsp:getvalueof var="regUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
												<dsp:a href="${regUrl}" title="Sign in and manage">
												<dsp:param name="registryId" value="${registryId}"/>
													<dsp:param name="eventType" value="${eventTypeVar}"/>
													<bbbl:label key='lbl_mng_regitem_signin_manage' language="${pageContext.request.locale.language}" />
												</dsp:a>
											</c:when>
										</c:choose>
										</a></span>

										<%-- need label? --%>
                            			<span class="icon icon-file-text-o"></span>
										<a href="${contextPath}/giftregistry/printCards/printCards.jsp?registryId=${registryId}" class="" title="<bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" />"><bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" /></a>

										</td>
									</c:if>
								</tr>
							</tbody>
						</table>
					</div>

				</div>

          	</div>
          	-->


          	<%--  Copy Registry changes start
	       	Copy registry has been moved to  registry_items_guest.jsp since new designs call for it to be under the sorting filters
			Copy Registry changes end --%>

<%--Including the Registry Items Page --%>
	<c:set var="maxBulkSize" scope="request">
		<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
 	</c:set>
 	<dsp:getvalueof var="totalToCopy" param="registrySummaryVO.giftRegistered"/>
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
 <c:set var="regAddress">
        <dsp:valueof value="${registryVO.shipping.shippingAddress.addressLine1}"  />
        <dsp:valueof value="${registryVO.shipping.shippingAddress.addressLine2}" />
 </c:set>
 
  <dsp:droplet name="POBoxValidateDroplet">
		 <dsp:param value="${regAddress}" name="address" />
		 <dsp:oparam name="output">
		 <dsp:getvalueof var="isPOBoxAddress" param="isValid"/>
		 </dsp:oparam>
	 </dsp:droplet>
	 
	 <dsp:setvalue bean="SessionBean.ownerRegAddPOBOX" value="${isPOBoxAddress}" />
	 
 <c:if test="${!regItemsWSCall && giftGiverItemsChecklistFlag && !isChecklistDisabled && sortSeq !=2}">
  <input type="hidden" id="giftGiverChecklistItems" value="true">
  </c:if>
  
  
   	<c:set var="myItemsFlag">
		<bbbc:config key="My_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	
	<c:choose>
		<c:when test="${isChecklistDisabled or !myItemsFlag or !giftGiverItemsChecklistFlag}">
			<c:set var="isMyItemsCheckList">false</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="isMyItemsCheckList">true</c:set>
		</c:otherwise>
	</c:choose>
  
  
   <input type="hidden" name="isMyItemsCheckListHiddenFlagGuest" id="isMyItemsCheckListHiddenFlagGuest" value="${isMyItemsCheckList}">
 <div id="regAjaxLoad" data-currentView="guest" data-registryId="${registryId}" data-regEventDate="${regEventDate}" data-startIdx="0" data-isGiftGiver="true" data-blkSize="${maxBulkSize}" data-isAvailForWebPurchaseFlag="false"  data-userToken="UT1021" data-sortSeq="${sortSeq}" data-eventTypeCode="${eventTypeCode}" data-eventType="${eventType}" data-pwsurl="${pwsurl}" data-view="${view}" data-totaltocopy="${totalToCopy}" data-isChecklistDisabled="${isChecklistDisabled}">
 
 <c:choose>
 <c:when test="${!regItemsWSCall && giftGiverItemsChecklistFlag && !isChecklistDisabled && sortSeq !=2}">
<dsp:include page="/giftregistry/frags/registry_items_guest_view.jsp">
                 		<dsp:param name="eventTypeCode" value="${eventTypeCode}" />
				</dsp:include>

 </c:when>
 <c:otherwise>
 	<div class="ajaxLoadWrapper">
			<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
		</div>
 </c:otherwise>
</c:choose>
</div>
<c:if test="${WarrantyOn}">
	<div  class="moreInfoWarrColor clearfix grid_12 alpha omega">
		<bbbt:textArea key="txt_warranty_link_registry" language ="${pageContext.request.locale.language}"/>
	</div>
</c:if>

<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
<%-- TODO make variable with item count --%>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="registryName"/>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="ownerRegEventType"/>
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
	<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
	<input type="hidden" value="${fromGiftGiver}" id="fromRecommendation">
	<input type="hidden" value="${param.createAccRecommenderFlow}" id="createAccRecommenderFlow">

	
    </jsp:body>
    <jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
        	    s.pageName = 'Registry View Page';
				s.channel = 'Registry';
				s.prop1 = 'Registry';
				s.prop2 = 'Registry';
				s.prop3 = 'Registry';
				if($("#fromRecommendation").val()=="true" && $("#createAccRecommenderFlow").val()=="true" ){
                 s.events="event28,event30";
				}
				else{
                 s.events="event28";
				}

			    s.eVar23 = '${fn:escapeXml(eventType)}';
			    s.eVar24 = '${fn:escapeXml(registryId)}';
				var s_code=s.t();if(s_code)document.write(s_code);
		   }
        </script>
    </jsp:attribute>
	</bbb:pageContainer>

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


	<%-- BBBSL-4343 DoubleClick Floodlight START  
	    <c:if test="${DoubleClickOn}">
		     <c:set var="rkgcollectionProd" value="${fn:trim(rkgProductList)}" />
			 <c:choose>
				 <c:when test="${not empty rkgcollectionProd }">
				 	<c:set var="rkgProductList" value="${rkgcollectionProd}"/>
				 </c:when>
				 <c:otherwise>
				 	<c:set var="rkgProductList" value="null"/>
				 </c:otherwise>
			 </c:choose>
			 <c:choose>
	   		 <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
	   		   <c:set var="cat"><bbbc:config key="cat_registry_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		 <c:when test="${(currentSiteId eq BedBathUSSite)}">
		    	<c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
		    	<c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    	<c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
	    	</c:when>
	    	 <c:when test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		 </c:choose>
	 		<dsp:include page="/_includes/double_click_tag.jsp">
	 			<dsp:param name="doubleClickParam"
	 			value="src=${src};type=${type};cat=${cat};u10=null,u11=null"/>
	 		</dsp:include>
 		</c:if>
		DoubleClick Floodlight END --%>

    
</dsp:page>