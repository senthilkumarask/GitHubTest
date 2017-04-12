<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>	
<dsp:getvalueof var="appid" bean="Site.id" />

<dsp:getvalueof var="showOnlySearchFields" param="showOnlySearchFields" />
<dsp:getvalueof var="formId" param="formId" />


<dsp:form 	id="registryFilterForm"
			iclass="grid_12 alpha omega"			
			method="post"
			action="bbb_search_registry"
			formid="registryFilterForm${formId}"
			>
		<input type="hidden" id="showOnlySearch" value="${showOnlySearchFields}"/>			
							
	<fieldset class="grid_9 alpha" id="registrySearchFilterFields">	            	
		<legend class="hidden">registrySearchFilterFields</legend>
		<div class="formField">
        	<label><bbbl:label key="lbl_reg_search_results_show_me" language ="${pageContext.request.locale.language}"/></label>
            <div class="select">
             	<label id="lblregistrySearchFilterEventType" class="txtOffScreen" for="registrySearchFilterEventType"><bbbl:label key="lbl_reg_search_results_show_me" language ="${pageContext.request.locale.language}"/></label>
		     	<dsp:select bean="GiftRegistryFormHandler.registryEventType"  
                			iclass="uniform" 
                         	id="registrySearchFilterEventType">	
					<dsp:droplet name="GiftRegistryTypesDroplet">
						<dsp:param name="siteId" value="${appid}"/>					
						<dsp:oparam name="output">
							<dsp:option value="" ><bbbl:label key="lbl_reg_search_results_event_type_default" language ="${pageContext.request.locale.language}"/></dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registryTypes" />
								<dsp:oparam name="output">
									<dsp:param name="regTypes" param="element" />	
									<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
										<dsp:option value="${registryCode}" selected="${sessEventType ==  registryCode}">
										<dsp:valueof param="element.registryName"></dsp:valueof>
									</dsp:option>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</div>
		</div>
        <div class="formField">
        	<label><bbbl:label key="lbl_reg_search_results_registries_in" language ="${pageContext.request.locale.language}"/></label>
           	<div class="select">
			 	<label id="lblregistrySearchFilterState" class="txtOffScreen" for="registrySearchFilterState"><bbbl:label key="lbl_reg_search_results_registries_in" language ="${pageContext.request.locale.language}"/></label>
							
            	<dsp:select	bean="GiftRegistryFormHandler.registryEventType"
							iclass="uniform" 
							id="registrySearchFilterState"
							name="stateFilter">
					<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
						<dsp:oparam name="output">
							<dsp:option>
								<c:choose>
									<c:when test="${currentSiteId eq BedBathCanadaSite}">
									  <bbbl:label key="lbl_reg_search_results_all_states_canada"
									  language="${pageContext.request.locale.language}" />
									</c:when>
									 <c:otherwise>
										<bbbl:label key="lbl_reg_search_results_all_states_usa"
										language="${pageContext.request.locale.language}" />
									</c:otherwise>
							   </c:choose>
							</dsp:option>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							   <dsp:param name="array" param="location" />
							   	<dsp:oparam name="output">
							   	<dsp:getvalueof param="element.stateName" id="stateName" />
							   	<dsp:getvalueof param="element.stateCode" id="stateCode" />
							   	<dsp:option value="${stateCode}" selected="${stateCode == sessState}">
									${stateName}
							   	</dsp:option>														
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</div>
		</div>

		<div class="formField">
        	<label><bbbl:label key="lbl_reg_search_results_sorted_by"
									language="${pageContext.request.locale.language}" /></label>
            <div class="select">
             	<label id="lblregistrySearchFilterSort" class="txtOffScreen" for="registrySearchFilterSort"><bbbl:label key="lbl_reg_search_results_sorted_by" language ="${pageContext.request.locale.language}"/></label>
				<select class="uniform" id="registrySearchFilterSort">
                   	<option value="NAMEASCE" <c:if test="${sessSortOrder eq 'NAMEASCE'}">selected="selected"</c:if> >
                   		<bbbl:label key="lbl_reg_search_results_sort_name_asc" language="${pageContext.request.locale.language}" />
                   	</option>
					<option value="NAMEDESC" <c:if test="${sessSortOrder eq 'NAMEDESC'}">selected="selected"</c:if> >
						<bbbl:label key="lbl_reg_search_results_sort_name_desc" language="${pageContext.request.locale.language}" />
					</option>                                      
                   	<option value="DATEASCE" <c:if test="${sessSortOrder eq 'DATEASCE'}">selected="selected"</c:if> >
                   		<bbbl:label key="lbl_reg_search_results_sort_date_asc" language="${pageContext.request.locale.language}" />
                   	</option>
                   	<option value="DATEDESC" <c:if test="${sessSortOrder eq 'DATEDESC'}">selected="selected"</c:if> >
                   		<bbbl:label key="lbl_reg_search_results_sort_date_desc" language="${pageContext.request.locale.language}" />
                   	</option>                   											                            
                </select>
			</div>
		</div>	                
	</fieldset>	    
    <fieldset class="grid_9 alpha"
			  id="registrySearchAgainFields" 
			<c:if test="${showOnlySearchFields != 'true'}">style="display: none"</c:if> >
		<legend class="hidden">registrySearchAgainFields</legend>
		<div class="formField">
			<label id="lblbbRegistryFirstName" class="txtOffScreen" for="bbRegistryFirstName"><bbbl:label key="lbl_enter_first_name" language ="${pageContext.request.locale.language}"/></label>
        	<dsp:input 	type="text" title="First Name"
						bean="GiftRegistryFormHandler.registrySearchVO.firstName"
						id="bbRegistryFirstName" 
						name="bbRegistryFirstName"
						maxlength="30" 
						value="">
				<dsp:tagAttribute name="placeholder" value="First Name" />
				<dsp:tagAttribute name="aria-required" value="false" />
				<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryFirstName errorbbRegistryFirstName" />
			</dsp:input>
		</div>
        <div class="formField">
			<label id="lblbbRegistryLastName" class="txtOffScreen" for="bbRegistryLastName"><bbbl:label key="lbl_enter_last_name" language ="${pageContext.request.locale.language}"/></label>
        	<dsp:input type="text"
						bean="GiftRegistryFormHandler.registrySearchVO.lastName"
						id="bbRegistryLastName" title="Last Name" 
						name="bbRegistryLastName"
						maxlength="30" value="">
						<dsp:tagAttribute name="placeholder" value="Last Name" />
                                    <dsp:tagAttribute
									name="aria-required" value="false" />
                                    <dsp:tagAttribute
									name="aria-labelledby"
									value="lblbbRegistryLastName errorbbRegistryLastName" />
                                </dsp:input>
		</div>
        <div class="formField">
			<div class="select <c:if test="${showOnlySearchFields == 'true'}">grey</c:if>" >
			
        	<c:choose>
				<c:when test="${currentSiteId eq BedBathCanadaSite}">
				  <label id="lblregistrySearchOnState" class="txtOffScreen" for="registrySearchOnState"><bbbl:label key="lbl_bridalbook_select_province" language ="${pageContext.request.locale.language}"/></label>
				</c:when>
				 <c:otherwise>
					<label id="lblregistrySearchOnState" class="txtOffScreen" for="registrySearchOnState"><bbbl:label key="lbl_bridalbook_select_state" language ="${pageContext.request.locale.language}"/></label>
				</c:otherwise>
		    </c:choose>
            	<dsp:select	bean="GiftRegistryFormHandler.registrySearchVO.state"
                     		name="bbRegistryState"
							id="registrySearchOnState"
							iclass="uniform"
							nodefault="true">
					<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
						<dsp:oparam name="output">
							<dsp:option>
								<c:choose>
								<c:when test="${currentSiteId eq BedBathCanadaSite}">
								  <bbbl:label key="lbl_bridalbook_select_province"
								  language="${pageContext.request.locale.language}" />
								</c:when>
								 <c:otherwise>
									<bbbl:label key="lbl_bridalbook_select_state"
									language="${pageContext.request.locale.language}" />
								</c:otherwise>
							   </c:choose>
							</dsp:option>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							   <dsp:param name="array" param="location" />
							   	<dsp:oparam name="output">
							   	<dsp:getvalueof param="element.stateName" id="stateName" />
							   	<dsp:getvalueof param="element.stateCode" id="stateCode" />
							   	<dsp:option value="${stateCode}">
									${stateName}
							   	</dsp:option>														
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
				   </dsp:droplet>
				    <dsp:tagAttribute name="aria-hidden" value="false"/>
				</dsp:select>
			</div>
		</div>        
		<div class="formField">
                   	<label>or</label>	
                   	<label id="lblbbRegistryFirstNamebbRegistryNumber" class="txtOffScreen" for="bbRegistryNumber"><bbbl:label key="lbl_regsearch_registry_num" language ="${pageContext.request.locale.language}"/></label>
       														                        
                       <dsp:input type="text"
								bean="GiftRegistryFormHandler.registrySearchVO.registryId"
								id="bbRegistryNumber" name="bbRegistryNumber" title="Registry Number"
								value="">
                                   	<dsp:tagAttribute
									name="placeholder" value="Registry Number" />
                                       <dsp:tagAttribute
									name="aria-required" value="false" />
                                       <dsp:tagAttribute
									name="aria-labelledby"
									value="lblbbRegistryNumber errorbbRegistryNumber" />
                                   </dsp:input>
        </div>        
		<div class="formField" id="registrySearchTipsCancel">
			<c:if test="${showOnlySearchFields != 'true'}">
				<a href="#" id="regSearchResultsCancelNewSearch"><bbbl:label key="lbl_reg_search_results_cancel"
								  											language="${pageContext.request.locale.language}" /></a>
			</c:if>
			<a href="javascript:void(0)" class="info <c:if test="${showOnlySearchFields == 'true'}">onlyTips</c:if>" id="regSearchResultsSearchTips">
				<bbbl:label key="lbl_reg_search_results_search_tips" language="${pageContext.request.locale.language}" />

				<div>
					<ul>
						<li><bbbl:label key="lbl_regsrchguest_search_tips1"
										language="${pageContext.request.locale.language}" />
									</li>
						<li><bbbl:label key="lbl_regsrchguest_search_tips2"
										language="${pageContext.request.locale.language}" />
									</li>
						<li><bbbl:label key="lbl_regsrchguest_search_tips3"
										language="${pageContext.request.locale.language}" />
									</li>
						<li><bbbl:label key="lbl_regsrchguest_search_tips4"
										language="${pageContext.request.locale.language}" />
									</li>
					</ul> 
				</div>																	
			</a>			
		</div>
				<%--Client DOM XSRF | Part -1
				  <dsp:input	bean="GiftRegistryFormHandler.registrySearchSuccessURL"
							type="hidden" value="${contextPath}/giftregistry/registry_search_guest.jsp" />
				<dsp:input	bean="GiftRegistryFormHandler.registrySearchErrorURL"
							type="hidden" value="${contextPath}/giftregistry/registry_search_guest.jsp" />--%>
				<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registrySearch" />		
	</fieldset>	
	<fieldset class="grid_3 omega" style="float:right">
		<legend class="hidden">registryFilterFormSubmitFields</legend>
    	<div class="formField grid_2 omega" id="registryFilterFormSubmitWrap">
	        <c:if test="${showOnlySearchFields != 'true'}">		
	        	<div class="button button_active">
					<input	id="registryFilterFormSearchAgain" type="button"
							value="Search Again" />
				</div>
			</c:if>
	        <div class="button button_active" <c:if test="${showOnlySearchFields != 'true'}"> style="display: none;"</c:if> >
				<c:set var="findButton">
					<bbbl:label key='lbl_reg_search_results_find_registry' language="${pageContext.request.locale.language}"></bbbl:label>
				</c:set>
				<dsp:input	id="registryFilterFormSubmit"
							bean="GiftRegistryFormHandler.registrySearch"
							type="submit" value="${findButton}">
					<dsp:tagAttribute name="aria-labelledby" value="searchRegistry" />
					<dsp:tagAttribute name="role" value="button" />
				</dsp:input>
			</div>
		</div>
	</fieldset>			                
</dsp:form>
<script>
  $(function(){
	 
  	var isShowSearchOnly = document.getElementById('showOnlySearch').value;
  	if(isShowSearchOnly == 'true'){
  		$("#registrySearchFilterFields").css('display','none');
  		$("#registrySearchAgainFields").css('display','block');
  	} else{
		$("#registrySearchFilterFields").css('display','block');
  		$("#registrySearchAgainFields").css('display','none');
		$('#registryFilterFormSubmit').parent().css('display','none');
		$('#registryFilterFormSearchAgain').parent().css('display','block');
  	}
  	
  });
  
</script>
</dsp:page>
