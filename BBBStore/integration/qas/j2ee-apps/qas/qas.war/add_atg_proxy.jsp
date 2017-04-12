<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>
<%@ page contentType="text/xml" %>
<dsp:importbean bean="/atg/qas/AddAddressVerificationSearch" />
<dsp:importbean bean="/atg/qas/AddAddressVerificationFormat" />

<dsp:page>
<proweb>
    <dsp:getvalueof var="action" param="action" />
    <c:choose>
        <c:when test="${action == 'search'}">
            <dsp:droplet name="AddAddressVerificationSearch">
                <dsp:param name="searchstring" param="searchstring" />
                <dsp:param name="country" param="country" />
                <dsp:param name="addlayout" param="addlayout" />
                <dsp:oparam name="output">
                    <verifylevel><dsp:valueof param="verificationlevel" /></verifylevel>
                    <dsp:getvalueof var="verificationlevel" param="verificationlevel" />
                    <dsp:getvalueof var="isverified" param="isverified" />
                    <c:choose>
                        <c:when test="${isverified}">
                            <dpvstatus><dsp:valueof param="dpvstatus" /></dpvstatus>
                            <address>
                                <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param param="addresslines" name="array"/>
                                    <dsp:oparam name="output">
                                        <line><c:out value="${fe.element}"/></line>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </address>
                        </c:when>
                        <c:otherwise>
                            <picklist>
                                <fullmoniker><dsp:valueof param="fullmoniker" /></fullmoniker>
                                <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param param="picklistitems" name="array"/>
                                    <dsp:oparam name="output">
                                        <picklistitem>
                                            <partialtext>${fe.element["partialtext"]}</partialtext>
                                            <addresstext>${fe.element["addresstext"]}</addresstext>
                                            <postcode>${fe.element["postcode"]}</postcode>
                                            <moniker>${fe.element["moniker"]}</moniker>
                                            <fulladdress>${fe.element["fulladdress"]}</fulladdress>
                                        </picklistitem>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </picklist>
                        </c:otherwise>
                    </c:choose>
                </dsp:oparam>
            </dsp:droplet>
        </c:when>
        <c:when test="${action == 'GetFormattedAddress'}">
            <dsp:droplet name="QasAddressVerificationFormat">
                <dsp:param name="moniker" param="moniker" />
                <dsp:param name="addlayout" param="addlayout" />
                <dsp:oparam name="output">
                    <verifylevel><dsp:valueof param="verificationlevel" /></verifylevel>
                    <dsp:getvalueof var="verificationlevel" param="verificationlevel" />
                    <c:choose>
                        <c:when test="${verificationlevel == 'Verified' || verificationlevel == 'InteractionRequired'}">
                            <dpvstatus><dsp:valueof param="dpvstatus" /></dpvstatus>
                            <address>
                                <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param param="addresslines" name="array"/>
                                    <dsp:oparam name="output">
                                        <line><c:out value="${fe.element}"/></line>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </address>
                        </c:when>
                        <c:otherwise>
                            <picklist>
                                <fullmoniker><dsp:valueof param="fullmoniker" /></fullmoniker>
                                <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
                                    <dsp:param param="picklistitems" name="array"/>
                                    <dsp:oparam name="output">
                                        <picklistitem>
                                            <partialtext>${fe.element["partialtext"]}</partialtext>
                                            <addresstext>${fe.element["addresstext"]}</addresstext>
                                            <postcode>${fe.element["postcode"]}</postcode>
                                            <moniker>${fe.element["moniker"]}</moniker>
                                            <fulladdress>${fe.element["fulladdress"]}</fulladdress>
                                        </picklistitem>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </picklist>
                        </c:otherwise>
                    </c:choose>
                </dsp:oparam>
            </dsp:droplet>
        </c:when>
    </c:choose>
</proweb>
</dsp:page>
