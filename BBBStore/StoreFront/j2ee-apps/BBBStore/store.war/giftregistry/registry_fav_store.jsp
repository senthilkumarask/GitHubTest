<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/PrefStoreInPilotStoresValidationDroplet" />

    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>

    <c:set var="BuyBuyBabySite">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    
    <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
    
	<c:set var="zipFromAjax" value="${param.zipCode}"></c:set>
	<c:set var="registryType" value="${param.registryType}"></c:set>
	
					

                        <c:if test="${!empty zipFromAjax}">
                         <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
                             <dsp:param name="searchString" value="${zipFromAjax}"/>
                                <dsp:param name="searchType" value="2"/>
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="storeDetails" param="StoreDetailsRegistry"/>
								<json:object>
									<json:property name="favStoreId"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeId"/></json:property>
									<json:property name="contactFlag"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].contactFlag"/></json:property>

							<dsp:droplet name="PrefStoreInPilotStoresValidationDroplet">
									<dsp:param name="prefredStoreId" param="StoreDetailsWrapper.storeDetails[0].storeId"/>
									<dsp:param name="registryTypeCode" value="${registryType}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="pilotFlag" param="isUserAllowedToScheduleAStoreAppointment"/>
									</dsp:oparam>
							</dsp:droplet>
									<json:property name="pilotFlag"><dsp:valueof value="${pilotFlag}"/></json:property>
									<json:object name="storeData">
										<json:property name="storeName"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeName"/></json:property>
										<json:property name="street"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].address"/></json:property>
										<json:property name="city"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].city"/></json:property>
										<json:property name="state"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].state"/></json:property>
										<json:property name="zip"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].postalCode"/></json:property>
										<json:property name="storeTimings" escapeXml="false"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].weekdaysStoreTimings"/><br><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].satStoreTimings" /><br>
										<dsp:valueof param="StoreDetailsWrapper.storeDetails[0].sunStoreTimings" /><br/>
										<dsp:valueof param="StoreDetailsWrapper.storeDetails[0].otherTimings1" /><br/>
										<dsp:valueof param="StoreDetailsWrapper.storeDetails[0].otherTimings2" /><br/>
										<dsp:valueof param="StoreDetails.storeDescription" /><br/>
										<br><strong><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storePhone"/></strong></json:property>
									</json:object>
								</json:object>
                            </dsp:oparam>
                            <dsp:oparam name="empty"></dsp:oparam>
                            <dsp:oparam name="error"></dsp:oparam>
                        </dsp:droplet>
                        
                        </c:if>

</dsp:page>