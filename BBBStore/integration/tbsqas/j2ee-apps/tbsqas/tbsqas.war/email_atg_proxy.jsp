<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ page import="java.net.URLDecoder"%>
<dsp:importbean bean="/atg/tbsqas/EmailPhoneVerificationSearch" />

<dsp:page>
    <dsp:droplet name="EmailPhoneVerificationSearch">
        <dsp:param name="qasemail" param="qasemail" />
        <dsp:param name="qasphone" param="qasphone" />
        <dsp:oparam name="output">
            <dsp:valueof param="tdresponse" valueishtml="true" />
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>
