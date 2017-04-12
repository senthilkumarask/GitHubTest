<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<div class="findARegistryForm clearfix">
	<div class="findARegistryFormTitle">
		<h2>
		<span><bbbl:label key="lbl_looking_for_a" language="${pageContext.request.locale.language}"/></span>
		<br>
		<bbbl:label key="lbl_cart_registry_text" language="${pageContext.request.locale.language}"/>
		</h2>
		<div class="grid_6">
                <p><bbbl:label key="lbl_enter_registrant's_information" language="${pageContext.request.locale.language}"/></p>
            </div>
		</div>
		
		<div class="findARegistryFormForm">
			<dsp:form id="frmRegInfo">
						
			<div class="grid_2">
			 <div class="inputField">
                    <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/>
                    <dsp:input type="text" bean="GiftRegistryFormHandler.registrySearchVO.firstName" id="txtCoRegistrantFirstNameAlt" name="txtCoRegistrantFirstNameAltName">
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="txtCoRegistrantFirstNameAlt errortxtCoRegistrantFirstNameAlt"/>
                    </dsp:input>
                </div>
            </div>
			
			<div class="grid_2">
                <div class="inputField">
                    <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>
                    <dsp:input type="text"	bean="GiftRegistryFormHandler.registrySearchVO.lastName" id="lastNameReg" name="lastNameReg">
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
                    </dsp:input>
                </div>
            </div>
			
			<div class="grid_1">
                
                <div class="input submit small">
                    <div class="button">
                        <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1" />
						<%-- Client DOM XSRF
						<dsp:input bean="GiftRegistryFormHandler.registrySearchSuccessURL" type="hidden" value="/store/giftregistry/registry_search_guest.jsp" /> --%>
						<%-- R2.2 Story - SEO Friendly URL changes --%>
						<dsp:input bean="GiftRegistryFormHandler.registrySearchErrorURL" type="hidden" value="s/${searchTerm}" />
						<dsp:input id="searchRegistry" bean="GiftRegistryFormHandler.registrySearch" type="submit" value="FIND REGSITRY">
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="searchRegistry"/>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
                    </div>
                </div>
            </div>
			</dsp:form>	
			<div class="clear"></div>
		</div>
</div>
</dsp:page>