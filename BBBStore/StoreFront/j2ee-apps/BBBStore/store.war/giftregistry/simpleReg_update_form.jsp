<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/SimpleRegFieldsDroplet" />

	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="cssPath" param="cssPath" />
	<dsp:getvalueof var="eventTypeForUpdate" param="eventTypeForUpdate" />
	<dsp:getvalueof var="appid" param="appid" />
	<dsp:getvalueof var="isTransient" param="isTransient" />
	<dsp:getvalueof var="registryEventType" param="registryEventType" />
	<dsp:getvalueof var="regPublic" param="regPublic" />
	<dsp:getvalueof var="coRegOwner" param="coRegOwner" />
<c:set var="hideRegistryFlag">
            <bbbc:config key="public_private_registry_hide_Flag" configName="FlagDrivenFunctions" />
         </c:set>
         
	<c:choose>
        <c:when test="${appid == 'BuyBuyBaby'}">
		            <c:set var="pageVariation" value="bb" scope="request" />
		            <c:set var="leftgrid" value="grid_5" scope="request" />
                    <c:set var="rightgrid" value="grid_5" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
                    <c:set var="lastnamegrid" value="grid_2" scope="request" />
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${registryEventType == 'BA1' }">
                    <c:set var="pageVariation" value="by" scope="request" />
                    <c:set var="leftgrid" value="grid_5" scope="request" />
                    <c:set var="rightgrid" value="grid_5" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
                    <c:set var="lastnamegrid" value="grid_2" scope="request" />
                     <c:set var="gridClass1">grid_8</c:set>
                    <c:set var="gridClass2">grid_2</c:set>

                </c:when>
                <c:otherwise>
                    <c:set var="pageVariation" value="br" scope="request" />
                    <c:set var="leftgrid" value="grid_3" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
                    <c:set var="lastnamegrid" value="grid_2" scope="request" />
                    <c:set var="rightgrid" value="grid_6" scope="request" />
                    <c:set var="gridClass1">grid_10</c:set>
                    <c:set var="gridClass2">grid_1</c:set>

                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
	
	<div id="backupUpdateForm" class="hidden"></div>
	
	<div id="updateForm" class="hidden">
	
	<dsp:droplet name="GetRegistryTypeNameDroplet">
    <dsp:param name="siteId" value="${appid}"/>
	<dsp:param name="registryTypeCode" value="${registryEventType}"/>
    <dsp:oparam name="output">
		<dsp:getvalueof var="eventType" param="registryTypeName"/>
	</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${( not empty registryId ) && !isTransient }">
	
	<div class="container_12 clearfix noMarTop">
	<div class="grid_1 omega" style="height:200px;"></div>
   	<div class="grid_10 center flatform">
   	<div class="grid_10 alpha omega">
	<div class="updateFormErrors"></div>

   		<div class="alert alert-alert hidden clearfix shippingAlert">
			<bbbl:label key='lbl_registry_update_modal_shipping_alert' language="${pageContext.request.locale.language}" />
		</div>


	<c:if test="${!regPublic}">
	<div id="regPrivateIndicator">
		<span>
			<p class="regPrivateIndicatorIcon"></p>
			<h2><bbbl:label key='lbl_update_modal_registry_status_alert' language="${pageContext.request.locale.language}" /></h2>
			<p class="regPrivateIndicatorText"><bbbl:label key='lbl_update_modal_complete_registry_text' language="${pageContext.request.locale.language}" /></p>
		</span>
	</div>
	</c:if>
	
	<div id="editRegIndicator" class="marBottom_20">
	<span class="editYourProf">
		<bbbl:label key='lbl_update_modal_edit_profile' language="${pageContext.request.locale.language}" />
	</span>
	<span class="modifyProfNotice">
		<bbbl:label key='lbl_update_modal_modify_profile_alert' language="${pageContext.request.locale.language}" />
	</span>
	</div>
	
	<div id="coRegistrantDialog2" title="Co-registrant Access" class="clear">
		<bbbt:textArea key="txt_reg_coreg_emailsent" language ="${pageContext.request.locale.language}"/>
		<p id="newCoRegistrantEmail"></p>
		<bbbt:textArea key="txt_reg_coreg_manage_msg" language ="${pageContext.request.locale.language}"/>
	</div>
	            
	<div id="coRegistrantDialog3" title="Co-registrant Access" class="clear">
		<bbbt:textArea key="txt_coregistrant_dialog3" language ="${pageContext.request.locale.language}"/>
	</div>
	            
	<div id="coRegistrantDialog1" title="Co-registrant Access" class="clear">
		<bbbt:textArea key="txt_reg_coreg_account_found" language ="${pageContext.request.locale.language}"/>
		<p id="newCoRegistrantEmail1"></p>
		<bbbt:textArea key="txt_reg_coreg_manage_msg2" language ="${pageContext.request.locale.language}"/>
	</div>
	            
	<div id="coRegistrantErrorDialog" class="clear">
		<h3><bbbt:textArea key="txt_reg_coreg_entered_email" language ="${pageContext.request.locale.language}"/></h3>
		<p><bbbt:textArea key="txt_reg_coreg_enter_coreg_email" language ="${pageContext.request.locale.language}"/></p>
	</div>
	
	<dsp:droplet name="SimpleRegFieldsDroplet">
		<dsp:param name="eventType" value="${eventType}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="inputListMap" param="inputListMap" />
	  	</dsp:oparam>
	</dsp:droplet>
	
	<dsp:form id="registryFormPost" iclass="pushDown clearfix form post noMarTop" method="post" formid="registryFormPost">
		<dsp:input id="registryEventType" bean="GiftRegistryFormHandler.registryEventType" type="hidden" value="${registryEventType}" />
	    <dsp:input id="coRegEmailFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailFoundPopupStatus" type="hidden" name="coRegEmailFoundPopupStatus"/>
	    <dsp:input id="coRegEmailNotFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailNotFoundPopupStatus" type="hidden" name="coRegEmailNotFoundPopupStatus"/>
		<dsp:input id="regIdHidden" type="hidden" name="regIdHidden" value="${regVO.registryId}" bean="GiftRegistryFormHandler.registryVO.registryId" />
		<dsp:input id="regTypeHidden" type="hidden" name="regTypeHidden" value="${regVO.registryType.registryTypeName}" bean="GiftRegistryFormHandler.registryVO.registryType.registryTypeName" />
		
		<dsp:include page="simpleReg_updatePrimaryRegistrant.jsp">
			<dsp:param name="event" value="${registryEventType}"/>
			<dsp:param name="inputListMap" value="${inputListMap}"/>
			<dsp:param name="coRegOwner" value="${coRegOwner}" />
		</dsp:include>
		
		<c:if test="${registryEventType != 'COL'}">
		<div class="clear padTop_15"></div>
		<dsp:include page="simpleReg_updateRegistrant_form.jsp">
			<dsp:param name="inputListMap" value="${inputListMap}"/>
		</dsp:include>
		</c:if>
		
		<div class="clear padTop_15"></div>
		<div class="grid_10 alpha omega clearfix eventtt marBottom_25">
		<dsp:include page="simpleReg_updateEvent_form.jsp">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="inputListMap" value="${inputListMap}"/>
		</dsp:include>
		<dsp:include page="simpleReg_updateShipping_form.jsp">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="inputListMap" value="${inputListMap}"/>
		</dsp:include>
		<dsp:include page="simpleReg_updateShowerDate_form.jsp">
			<dsp:param name="event" value="${registryEventType}"/>
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="inputListMap" value="${inputListMap}"/>
		</dsp:include>
		</div>
		
		<dsp:include page="simpleReg_updateStore_form.jsp">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="inputListMap" value="${inputListMap}"/>
		</dsp:include>
		
		<c:if test="${!regPublic}">
		<c:choose>
		<c:when test="${registryEventType == 'BA1' }">
			<div class="grid_6 alpha omega marLeft_20 fr">
		</c:when>
		<c:otherwise>
			<div class="grid_6 alpha omega marLeft_20">
		</c:otherwise>
		</c:choose>
		<bbbt:textArea key="txt_update_modal_make_public_benefit_msg" language ="${pageContext.request.locale.language}"/>
		</div>
		</c:if>
		
		<c:if test="${registryEventType != 'BA1' }">
			<div class="clear"></div>                
		</c:if>
		<c:if test="${regVO != null}">
			
			<dsp:getvalueof var="netAff" value="${regVO.networkAffiliation}" />
			<c:choose>
			<c:when test="${!regPublic}">
	      <c:if test="${!hideRegistryFlag}">
			<div class="grid_10 marTop_25">
				<input id="makeRegistryPublic" name="makeRegistryPublic" type="checkbox"  title="Make Registry Public" style="opacity: 0;"/>
				<div class="grid_8 fl"><label id="lblmakeRegistryPublic" for="makeRegistryPublic">
				<span class="chngRegSt"><bbbl:label key='lbl_update_modal_make_public_msg' language="${pageContext.request.locale.language}" /></span></br>
				<span class="dunWryText"><bbbl:label key='lbl_update_modal_change_private_no_worries_msg' language="${pageContext.request.locale.language}" /></span>
				</label></div>
			</div>
			</c:if>
			<dsp:input id="makeRegPublic" bean="GiftRegistryFormHandler.makeRegistryPublic" type="hidden" value="false" />
			<dsp:input id="makeRegPrivate" bean="GiftRegistryFormHandler.deactivateRegistry" type="hidden" value="true" />
			
			<div class="grid_10 marBottom_15 marTop_10">
				<input id="networkAff" name="optInOutCheck" type="checkbox"  title="Make Registry Public" style="opacity: 0;" disabled/>
				<div class="grid_8 fl"><label id="lblnetworkAff" for="networkAff">
				<span class="chngRegSt"><bbbl:label key='lbl_update_modal_opt_in_and_out_msg' language="${pageContext.request.locale.language}" /></span>
				</label></div>
				<dsp:input id="networkAffiliation" bean="GiftRegistryFormHandler.registryVO.networkAffiliation" type="hidden" value="N"/>
			</div>
			</c:when>
			<c:otherwise>
          <c:if test="${!hideRegistryFlag}">
			<div class="grid_10 marTop_25">
				<input id="makeRegistryPrivate" name="makeRegistryPrivate" type="checkbox" title="Make Registry Private" style="opacity: 0;"/>
				<div class="grid_8 fl"><label id="lblmakeRegistryPrivate" for="makeRegistryPrivate">
				<span class="chngRegSt"><bbbl:label key='lbl_update_modal_make_private_msg' language="${pageContext.request.locale.language}" /></span></br>
				<span class="dunWryText"><bbbl:label key='lbl_update_modal_change_public_no_worries_msg' language="${pageContext.request.locale.language}" /></span>
				</label></div>
			</div>
			</c:if>
			<dsp:input id="makeRegPrivate" bean="GiftRegistryFormHandler.deactivateRegistry" type="hidden" value="false" />
			<dsp:input id="makeRegPublic" bean="GiftRegistryFormHandler.makeRegistryPublic" type="hidden" value="true" />
			<div class="grid_10 marBottom_15 marTop_10">
				<c:choose>
				<c:when test="${netAff eq 'Y'}">
					<input id="networkAff" name="optInOutCheck" type="checkbox"  title="Make Registry Public" style="opacity: 0;" checked/>
					<div class="grid_8 fl"><label id="lblnetworkAff" for="networkAff">
					<span class="chngRegSt"><bbbl:label key='lbl_update_modal_opt_in_and_out_msg' language="${pageContext.request.locale.language}" /></span>
					</label></div>
					<dsp:input id="networkAffiliation" bean="GiftRegistryFormHandler.registryVO.networkAffiliation" type="hidden" value="Y"/>
				</c:when>
				<c:otherwise>
					<input id="networkAff" name="optInOutCheck" type="checkbox"  title="Make Registry Public" style="opacity: 0;"/>
					<div class="grid_8 fl"><label id="lblnetworkAff" for="networkAff">
					<span class="chngRegSt"><bbbl:label key='lbl_update_modal_opt_in_and_out_msg' language="${pageContext.request.locale.language}" /></span>
					</label></div>
					<dsp:input id="networkAffiliation" bean="GiftRegistryFormHandler.registryVO.networkAffiliation" type="hidden" value="N"/>
				</c:otherwise>
				</c:choose>
			</div>
			</c:otherwise>
			</c:choose>
			<div class="grid_10 marBottom_25 clearfix">
				<dsp:input iclass="button-Med btnRegistryPrimary marTop_20" id="submitUpdateAjaxForm" bean="GiftRegistryFormHandler.updateRegistry" type="submit" value="Save"/>
			</div>
			
			<dsp:input id="updateSimplified" bean="GiftRegistryFormHandler.updateSimplified" type="hidden" value="true" />
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.prefStoreNum"  id="favStoreIDForUI" value="${regVO.prefStoreNum}"/>
		</c:if>
	</dsp:form>
	</div>
	</div>
	</div>
	
	<c:import url="/selfservice/simpleReg_find_in_store.jsp" >
	    <c:param name="enableStoreSelection" value="true"/>
	    <c:param name="errMessageShown" value="true"/>
	</c:import>
	<div class="clear"></div>
	<c:import url="/_includes/modules/simpleReg_change_store_form.jsp" >
	</c:import>
	<div class="clear"></div> 
	<!--  New version of view map/get directions -->
	<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
	
	<dsp:include page="frags/simpleRegAddressChange.jsp"/>
	
	</c:if>
	</div>

</dsp:page>