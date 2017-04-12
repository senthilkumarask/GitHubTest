<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
		<dsp:getvalueof var="jasonObj" param="addItemResults" />
		<dsp:getvalueof var="regId" param="registryId" />
		<dsp:getvalueof var="regName" param="registryName" />
		<dsp:getvalueof var="totQty" param="totQuantity" />
		<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
		
		<%--<dsp:setvalue bean="GiftRegistryFormHandler.addItemToGiftRegistry" /> --%>
		<dsp:getvalueof
			bean="GiftRegistryFormHandler.errorFlagAddItemToRegistry" var="flag" />
			<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
			<dsp:getvalueof id="omnitureRegistryProd"  param="giftRegistryViewBean.omniProductList"/>
			<dsp:getvalueof id="registryId"  param="giftRegistryViewBean.targetRegistry"/>
			<dsp:getvalueof id="registryName"  param="giftRegistryViewBean.registryName"/>
			<dsp:getvalueof id="totQtySrcReg"  param="giftRegistryViewBean.totQtySrcReg"/>
			<dsp:getvalueof id="copyRegErr"  param="giftRegistryViewBean.copyRegErr"/>
			<c:if test="${empty addAll}">
			<dsp:getvalueof id="consultantName"  param="giftRegistryViewBean.consultantName"/>
			</c:if>

			<c:choose>
				<c:when test="${flag==true || copyRegErr==true}">
					<div title='<bbbe:error key="err_add_item_registry" language ="${pageContext.request.locale.language}"/>'>
					<div class="clearfix marBottom_20">
						<dsp:droplet name="ErrorMessageForEach">
							<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
								<dsp:oparam name="output">
									<p class="error"><dsp:valueof param="message"/></p>
								</dsp:oparam>
						</dsp:droplet>
					</div>
					<div class="clear"></div>
					<div class="fl noMarBot clearfix">
						<div class="fl button button_active button_active_orange">				
								<input type="submit" value="Ok" name="Ok" class="close-any-dialog" role="button" aria-pressed="false">
						</div>
			        </div>
					</div>
				</c:when>
				<c:otherwise> 		
				<div title='${totQtySrcReg} <bbbl:label key="lbl_item_added_registry" language ="${pageContext.request.locale.language}"/> ${registryName} <bbbl:label key="lbl_item_registry_name" language ="${pageContext.request.locale.language}"/>'>
				<div class="fl noMarBot clearfix">
					<div class="fl button button_active">				
					<input type="submit" value="CLOSE" name="CLOSE" class="close-any-dialog" role="button" aria-pressed="false">
					</div>
					<div class="fl bold marTop_5 marLeft_10">		
							<dsp:a href="/tbs/giftregistry/view_registry_owner.jsp"><bbbt:textArea key="txt_pdp_reg_viewandmanageregistry" language ="${pageContext.request.locale.language}"/>
								<dsp:param name="registryId" value="${registryId}" />
                                <dsp:param name="eventType" value="${registryName}" />
							</dsp:a>
						<div class="clear"></div>
					</div>
				</div>
					 </div>
					
		           
				</c:otherwise>
			</c:choose>	
</dsp:page>