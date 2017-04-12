 
<dsp:page>
<dsp:getvalueof var="promotionId" param="promotionId" />
 
    <dsp:droplet name="/atg/targeting/RepositoryLookup">
        <dsp:param name="id" value="${promotionId}"/>
        <dsp:param name="repository" bean="/atg/commerce/pricing/Promotions"/>
        <dsp:param name="itemDescriptor" value="promotion"/>
        <dsp:oparam name="output">
            <dsp:valueof param="element.tandc"  valueishtml="true"></dsp:valueof>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>