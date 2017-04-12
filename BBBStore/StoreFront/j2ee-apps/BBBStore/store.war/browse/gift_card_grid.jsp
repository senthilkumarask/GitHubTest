<%@ page import="com.bbb.commerce.browse.BBBSearchBrowseConstants" %>
<c:set var="section" value="browse" scope="request" />
<c:set var="pageWrapper" value="giftCardGrid" scope="request" />  

<dsp:page>

<dsp:importbean bean="/com/bbb/commerce/browse/droplet/GiftCardListDroplet" />
<bbb:pageContainer>
<jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
	<jsp:body>
		<div id="content" class="container_12 clearfix" role="main">
			
			
				<div class="grid_12 clearfix textCenter pageTitle">
					<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
					<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}"/>
					<bbbt:textArea key="txt_gcgrid_promoheaderimage" placeHolderMap="${placeHolderMap}" language="<c:out param='${pageContext.request.locale.language}'/>" />
				</div>
			

			<div id="prodGridContainer" class="grid_12 clearfix">	
				<div id="prodGrid">
					<dsp:droplet name="GiftCardListDroplet">
						<dsp:oparam name="output">
							<dsp:include page="/_includes/modules/gift_card_grid.jsp">
								<dsp:param name="<%= BBBSearchBrowseConstants.PRODUCT_VO_LIST %>" param="<%= BBBSearchBrowseConstants.PRODUCT_VO_LIST %>" />
							</dsp:include>
						</dsp:oparam>
						<dsp:oparam name="error">
							<dsp:valueof param="errorMsg" />
						</dsp:oparam>
					</dsp:droplet>
				</div>				
			</div>
			
				<div class="grid_12 clearfix textCenter pageTitle">
					<bbbt:textArea key="txt_gcgrid_promofooterimage" placeHolderMap="${placeHolderMap}" language="<c:out param='${pageContext.request.locale.language}'/>" />
				</div>
		
		</div>
		</jsp:body>
</bbb:pageContainer>
</dsp:page>