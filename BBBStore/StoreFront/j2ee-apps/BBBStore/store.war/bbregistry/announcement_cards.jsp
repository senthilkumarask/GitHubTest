<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>    
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryAnnouncementCardDroplet"/>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />     
<dsp:importbean bean="/atg/multisite/Site"/>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="appid" bean="Site.id" />

<dsp:droplet name="GiftRegistryAnnouncementCardDroplet">
	<dsp:param name="profile" bean="Profile"/>
	<dsp:param name="siteId" value="${appid}"/>
    <dsp:oparam name="output">
		<div title="Registry Announcement Cards" class="container_6">
			<bbbt:textArea key="txt_forget_spread_world" language ="${pageContext.request.locale.language}"/>
				<div class="grid_2 alpha">
					<bbbt:textArea key="txt_hooray_registry" language ="${pageContext.request.locale.language}"/>
				</div>
			<dsp:form id="announcementCards" action="${contextPath}/bbregistry/registry_confirmation.jsp">
				<dsp:setvalue bean="GiftRegSessionBean.clear" value="true"/>			
				<div class="grid_4 omega">
				<bbbt:textArea key="txt_announce_confirm" language ="${pageContext.request.locale.language}"/>
					
				<p class="bold noMarBot"><bbbl:label key="lbl_announce_send" language ="${pageContext.request.locale.language}"/></p>
			
				<dsp:getvalueof param="count" idtype="java.lang.Integer" id="count">
				
					<c:if test="${count>1}">
						<%-- multiple registries --%>
						<div class="input width_3 omega">
							<div class="label">
								<label id="lblwedding" for="wedding"></label>
							</div>												
							<div class="select">
								<dsp:select bean="GiftRegSessionBean.registryVO.registryId" name="wedding" id="wedding" 
										iclass="selector_primary">		
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="registries"/>
										<dsp:param name="elementName" value="registry" />
										<dsp:oparam name="output">
											<dsp:option paramvalue="registry.registryId">
												<dsp:valueof param="registry.eventType" /> - <dsp:valueof param="registry.eventDate" />
											</dsp:option>
										</dsp:oparam>	
									</dsp:droplet>
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblwedding errorwedding"/>
								</dsp:select>
							</div>
						</div>
					</c:if>
					<c:if test="${count==1}">
						<%-- one registry --%>
						<dsp:input type="hidden" bean="GiftRegSessionBean.registryVO.registryId" paramvalue="registries[0].registryId" />
					</c:if>					
				</dsp:getvalueof>

				
			<p class="marTop_10">

				<dsp:input name="registryAnnouncement" type="radio" 
					bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg25"  value="25" checked="true">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblreg25 lblregistryAnnouncement"/>
                </dsp:input>
				<label id="lblreg25" for="reg25" class="marRight_10"><bbbl:label key="lbl_announce_send_25" language ="${pageContext.request.locale.language}"/></label>
				<dsp:input name="registryAnnouncement" type="radio"			
					bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg50"  value="50">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblreg50 lblregistryAnnouncement"/>
                </dsp:input>
				<label id="lblreg50" for="reg50" class="marRight_10"><bbbl:label key="lbl_announce_send_50" language ="${pageContext.request.locale.language}"/></label>
				<dsp:input name="registryAnnouncement" type="radio" 
					bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg75"  value="75">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblreg75 lblregistryAnnouncement"/>
                </dsp:input>
				<label id="lblreg75" for="reg75" class="marRight_10"><bbbl:label key="lbl_announce_send_75" language ="${pageContext.request.locale.language}"/></label>
				<dsp:input name="registryAnnouncement" type="radio" 
					bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg100"  value="100">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblreg100 lblregistryAnnouncement"/>
                </dsp:input>
				<label id="lblreg100" for="reg100" class="marRight_10"><bbbl:label key="lbl_announce_send_100" language ="${pageContext.request.locale.language}"/></label>
				<label id="lblregistryAnnouncement" for="registryAnnouncement" generated="true" class="error"></label>
				<label for="reg25" class="offScreen"><bbbl:label key="lbl_announce_send_25" language ="${pageContext.request.locale.language}"/></label>
				<label for="reg50" class="offScreen"><bbbl:label key="lbl_announce_send_50" language ="${pageContext.request.locale.language}"/></label>
				<label for="reg75" class="offScreen"><bbbl:label key="lbl_announce_send_75" language ="${pageContext.request.locale.language}"/></label>
				<label for="reg100" class="offScreen"><bbbl:label key="lbl_announce_send_100" language ="${pageContext.request.locale.language}"/></label>
			</p>
			<div class="marTop_20 buttonpane clearfix">
                <div class="ui-dialog-buttonset">
                    <div class="button button_active">
                        <%-- Client DOM XSRF | Part -1
                        dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" 
					value="${contextPath}/bbregistry/registry_confirmation.jsp"/>
				<dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" 
					value="${contextPath}/bbregistry/announcement_cards.jsp"/> --%>
			<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" 
					value="announcementCard"/>
				<dsp:input type="submit" value="Submit" 
					bean="GiftRegistryFormHandler.announcementCardCount" id="registryAnnouncementSubmit" >
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="registryAnnouncementSubmit"/>
                    <dsp:tagAttribute name="role" value="button"/>
                </dsp:input>
                    </div>	

                    <div class="button button_active">
                       <input type="button" id="registryAnnouncementCancel" value="cancel" class="close-any-dialog" />
                    </div>
                </div>
            </div>
		</div>
		</dsp:form>
		</div>
	</dsp:oparam>
    <dsp:oparam name="error">
	    <div title="Registry Announcement Cards" class="container_6">
	    	<div  id="announcementCards">
				<dsp:valueof param="errorMsg"/>
			</div>
			<div class="marTop_20 buttonpane clearfix">
			 	<div class="button button_active">
                       <input type="button" id="registryAnnouncementCancel" value="ok" class="close-any-dialog" role="button" aria-pressed="false" aria-labelledby="registryAnnouncementCancel" />
              	</div>
            </div>
			
		</div>
	</dsp:oparam>
	<dsp:oparam name="empty">
	   <div title="Registry Announcement Cards" class="container_6">
	    	<div  id="announcementCards">
				<dsp:valueof param="errorMsg"/>
			</div>
			<div class="marTop_20 buttonpane clearfix">
			 	<div class="button button_active">
                       <input type="button" id="registryAnnouncementCancel" value="ok" class="close-any-dialog" role="button" aria-pressed="false" aria-labelledby="registryAnnouncementCancel" />
              	</div>
            </div>			
		</div>
	</dsp:oparam>
  </dsp:droplet>

</dsp:page>


