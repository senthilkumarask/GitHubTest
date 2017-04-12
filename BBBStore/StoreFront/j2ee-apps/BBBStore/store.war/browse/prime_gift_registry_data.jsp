<%@ page import="java.lang.*,java.util.*" %>
<%@ page import="com.bbb.commerce.giftregistry.vo.*" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/PrimeGiftRegistryInfoDroplet" />
<dsp:droplet name="PrimeGiftRegistryInfoDroplet">
<dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id"/>
</dsp:droplet>
</dsp:page>