<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		<dsp:setvalue param="giftRegistryViewBean" beanvalue="SessionBean.giftRegistryViewBean" />
		<dsp:getvalueof id="omnitureRegistryProd"  param="giftRegistryViewBean.omniProductList"/>
		<dsp:getvalueof id="omnitureRegistryId"  param="giftRegistryViewBean.registryId"/>
		<dsp:getvalueof id="omnitureRegistryName"  param="giftRegistryViewBean.registryName"/>
		<dsp:getvalueof var="additemflagerror" param="additemflagerror" />
		<dsp:getvalueof var="showpopup" param="showpopup" />
		<c:if test="${additemflagerror=='false' && showpopup == 'true'}">
		<json:object>
		<json:property name="registryPagename">registryAdd</json:property>
		<json:property name="var24">${omnitureRegistryId}</json:property>
		<json:property name="product">${omnitureRegistryProd}</json:property>
		<json:property name="var23">${omnitureRegistryName}</json:property>
		</json:object>
		</c:if>
	<dsp:setvalue bean="GiftRegistryFormHandler.clearSessionBeanData" />
</dsp:page>