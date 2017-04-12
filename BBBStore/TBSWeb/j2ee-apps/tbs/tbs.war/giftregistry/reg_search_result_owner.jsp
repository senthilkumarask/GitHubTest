<dsp:page>
<bbb:pageContainer>
<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <div id="content" class="container_12 clearfix manageRegistry" role="main">
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
   <%-- Commenting Click to Chat as part of 34473 
   <div class="grid_12 noMar">
					<div id="chatModal">
                    	<div id="chatModalDialogs"></div>
                        	<dsp:include page="/common/click2chatlink.jsp">
                            	<dsp:param name="pageId" value="2"/>
                            </dsp:include>
							<br>
                 	</div>
				</div>
	--%>
        <div class="grid_12">
      <div class="error">
         <dsp:droplet name="ErrorMessageForEach">
									      <dsp:param bean="GiftRegistryFormHandler.formExceptions"
							name="exceptions" />
									      <dsp:oparam name="output">
									      <dsp:valueof param="message" />
							<br>
									      </dsp:oparam>
									    </dsp:droplet></div>
            <h1><bbbl:label key='lbl_event_myregistry' language ="${pageContext.request.locale.language}"/></h1>
            
            
            <div class="grid_9 suffix_2 prefix_1 alpha omega">
                <div class="grid_4 container alpha">
                    <bbbt:textArea key="txt_another_registry" language="${pageContext.request.locale.language}"/>
                    <div class="input width_2">
                    <dsp:include page="my_registry_type_select.jsp"></dsp:include>
                    </div>
                </div>
                <div class="grid_4 container whyRegister omega">
                 <bbbt:textArea key="txt_why_registry" language="${pageContext.request.locale.language}"/>
                </div>
            </div>
            <div class="clear"></div>
            <div class="grid_12 alpha omega" id="findForm">
                <div class="highlight">
                    <p class="bold"><bbbl:label key='lbl_regsearch_helptxt' language ="${pageContext.request.locale.language}"/></p>
                    <dsp:form  id="findOldRegistry" method="post" iclass="marTop_20">
                        <h3><bbbl:label key='lbl_regsearch_lnktxt' language ="${pageContext.request.locale.language}"/></h3>
                        <div class="grid_3 alpha">
                            <fieldset>
                                <div class="input grid_3">
                                    <div class="label">
                                        <label id="lblregistryFirstName" for="registryFirstName"> <bbbl:label key='lbl_reg_firstname' language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                                    </div>
                                    <div class="text">
                                        <dsp:input type="text" name="registryFirstName" id="registryFirstName" bean="GiftRegistryFormHandler.registrySearchVO.firstName">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryFirstName errorregistryFirstName"/>
                                        </dsp:input>
                                    </div>
                                </div>
                                
                                
                                <div class="input grid_3">
                                    <div class="label">
                                        <label id="lblregistryLastName" for="registryLastName">  <bbbl:label key='lbl_reg_lastname' language ="${pageContext.request.locale.language}"/><span class="required">*</span> </label>
                                    </div>
                                    <div class="text">
                                        <dsp:input type="text" name="registryLastName"  id="registryLastName" bean="GiftRegistryFormHandler.registrySearchVO.lastName">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryLastName errorregistryLastName"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                        <div class="grid_1 textCenter"><bbbl:label key='lbl_regsearch_or' language ="${pageContext.request.locale.language}"/></div>
                        <div class="grid_3 alpha bdr_left">
                            <fieldset>
                                <div class="input grid_3">
                                    <div class="label">
                                        <label id="lblregistryEmail" for="registryEmail"> <bbbl:label key='lbl_regsearch_email' language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                                    </div>
                                    <div class="text">
                                        <dsp:input type="text" name="registryEmail" id="registryEmail" bean="GiftRegistryFormHandler.registrySearchVO.email">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryEmail errorregistryEmail"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                        <div class="grid_1 textCenter"><bbbl:label key='lbl_regsearch_or' language ="${pageContext.request.locale.language}"/></div>
                        <div class="grid_3 omega bdr_left">
                            <fieldset>
                                <div class="input grid_3">
                                    <div class="label">
                                        <label id="lblregistryNumber" for="registryNumber"> <bbbl:label key='lbl_regsearch_registry_num' language ="${pageContext.request.locale.language}"/> <span class="required">*</span> </label>
                                    </div>
                                    <div class="text">
                                        <dsp:input type="text" name="registryNumber"  id="registryNumber" bean="GiftRegistryFormHandler.registrySearchVO.registryId" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryNumber errorregistryNumber"/>
                                        </dsp:input>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                        <div class="clear"></div>
                        <div class="button">
                         <c:set var="findRegistryBtn"><bbbl:label key='lbl_regsearch_find_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                       
                          <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="2"></dsp:input>
                            <dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromImportRegistryPage" value="${findRegistryBtn}" id="findRegistryBtn" iclass="small button primary">
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="findRegistryBtn"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
                        </div>
                    </dsp:form>
                </div>
            </div>
          
        </div>
    </div>
</bbb:pageContainer>
</dsp:page>
