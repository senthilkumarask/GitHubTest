<dsp:page>
 <dsp:importbean
        bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
 		
	<dsp:getvalueof var="findRegistryFormCount" param="findRegistryFormCount" />
	<%-- <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" /> --%>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="findRegistryFormId" param="findRegistryFormId" />
	<dsp:getvalueof var="bridalException" param="bridalException" />
	<dsp:getvalueof var="isFlyout" param="flyout" />
	<dsp:getvalueof var="autoComplete" value="on"/>
	<c:if test ="${isFlyout eq 'true'}">
		<dsp:getvalueof var="autoComplete" value="off"/>
	</c:if>

<dsp:form action="bbb_search_registry" method="post" iclass="clearfix frmFindARegistry" id="${findRegistryFormId}">

	<c:choose>
		<c:when test="${siteId == 'BuyBuyBaby' || bridalException == 'true'}">
			<div class="input textBox grid_2">
				<div class="label">
					<label id="lblmxfirstNameReg_${findRegistryFormCount}" for="mxfirstNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_mxregflyout_first_name_placeholder" language="${pageContext.request.locale.language}" /></label>
				</div>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" id="mxfirstNameReg_${findRegistryFormCount}" value="" name="mxfirstNameReg" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required alphabasicpunc" maxlength="30" >
						<c:set var="lbl_mxregflyout_first_name_placeholder">
							<bbbl:label key="lbl_mxregflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_mxregflyout_first_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblmxfirstNameReg_${findRegistryFormCount} errormxfirstNameReg_${findRegistryFormCount}"/>
					</dsp:input>
					<label id="errormxfirstNameReg_${findRegistryFormCount}" for="mxfirstNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
				</div>				
			</div>
			<div class="input textBox grid_2 noMarBot">
				<div class="label">
					<label id="lblmxlastNameReg_${findRegistryFormCount}" for="mxlastNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_mxregflyout_last_name_placeholder" language="${pageContext.request.locale.language}" /></label>
				</div>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" id="mxlastNameReg_${findRegistryFormCount}"  value="" name="mxlastNameReg" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required alphabasicpunc" maxlength="30">
						<c:set var="lbl_mxregflyout_last_name_placeholder">
							<bbbl:label key="lbl_mxregflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_mxregflyout_last_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblmxlastNameReg_${findRegistryFormCount} errormxlastNameReg_${findRegistryFormCount}"/>
					</dsp:input>
					<label id="errormxlastNameReg_${findRegistryFormCount}" for="mxlastNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
				</div>				
			</div>
			
		</c:when>
		<c:otherwise>
			<div class="input textBox grid_2">
				<div class="label">
					<label id="lblmxfirstNameReg_${findRegistryFormCount}" for="mxfirstNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_mxregflyout_firstname" language ="${pageContext.request.locale.language}"/></label>
				</div>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" name="mxfirstNameReg" value="" id="mxfirstNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required mxalphabasicpunc" maxlength="30" style="margin:0px !important; " >
                        <c:set var="lbl_mxregflyout_first_name_placeholder">
							<bbbl:label key="lbl_mxregflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_mxregflyout_first_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblmxfirstNameReg_${findRegistryFormCount} errormxfirstNameReg_${findRegistryFormCount}"/>
                    </dsp:input>
				</div>
				<div class="error"></div>
			</div>
			<div class="input textBox grid_2 noMarBot">
				<div class="label">
					<label id="lblmxlastNameReg_${findRegistryFormCount}" for="mxlastNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_mxregflyout_lastname" language ="${pageContext.request.locale.language}"/></label>
				</div>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" name="mxlastNameReg" value="" id="mxlastNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required mxalphabasicpunc" maxlength="30" style="margin:0 !important; " >
                        <c:set var="lbl_mxregflyout_last_name_placeholder">
							<bbbl:label key="lbl_mxregflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_mxregflyout_last_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblmxlastNameReg_${findRegistryFormCount} errormxlastNameReg_${findRegistryFormCount}"/>
                    </dsp:input>
				</div>
				<div class="error"></div>	
			</div>
		</c:otherwise>
	</c:choose>
	
	<div class="mxformButtons">
	
		<dsp:getvalueof var="successURL" param="successURL" />
		<dsp:getvalueof var="errorURL" param="errorURL" />
		<dsp:getvalueof var="submitText" param="submitText" /> 
		
		
		<dsp:input bean="GiftRegistryFormHandler.registrySearchSuccessURL" type="hidden" value="${successURL}"></dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.registrySearchErrorURL" type="hidden" value="${errorURL}"></dsp:input>
		<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1"></dsp:input>
		
		<c:choose>
			<c:when test="${siteId == 'BuyBuyBaby'}">
				<div class="button button_primary button_active">
					<dsp:input type="submit" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.mxRegistrySearch" >
                       
                        <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry_${findRegistryFormCount}"/>
                       
                    </dsp:input>
				</div>
			</c:when>
			<c:otherwise>
				<div class="button button_secondary button_active">
					<dsp:input type="submit" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.mxRegistrySearch" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'find']);">
                       
                        <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry_${findRegistryFormCount}"/>
                       
                    </dsp:input>
				</div>
			</c:otherwise>
		</c:choose>
	
	</div>
	
</dsp:form>
</dsp:page>