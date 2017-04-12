
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<dsp:page>

<dsp:getvalueof var="bopusOrderNum" param="bopusOrderNum"/>
<dsp:importbean bean="/com/bbb/selfservice/SearchStoreDroplet"/>

	<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
       	<dsp:param name="storeId" param="shippingGroup.storeId" />
       	<dsp:oparam name="output">
				<tbody>
					<tr>
						<td>${bopusOrderNum}</td>
						<td><dsp:valueof param="StoreDetails.storeName"/>
							<dsp:valueof param="StoreDetails.address"/></span>
							<dsp:valueof param="StoreDetails.city"/></span>
							<dsp:valueof param="StoreDetails.state"/></span>&nbsp;<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/>
							<dsp:valueof param="StoreDetails.storePhone"/>
						</td>
					</tr>
				</tbody>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
