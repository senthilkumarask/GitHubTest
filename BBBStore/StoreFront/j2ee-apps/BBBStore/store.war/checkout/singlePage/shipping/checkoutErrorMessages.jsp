<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
    <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:oparam name="outputStart">
            <ul class="error">
        </dsp:oparam>
        <dsp:oparam name="output">
                  <li><dsp:valueof param="message" /></li>
        </dsp:oparam>
        <dsp:oparam name="outputEnd">
            </ul>            
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>