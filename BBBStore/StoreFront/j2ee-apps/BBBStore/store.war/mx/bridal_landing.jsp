<dsp:page>
	 <c:set var="pageWrapper" value="bridalLanding useCertonaJs useFB useAdobeActiveContent" scope="request" />
     <c:set var="mxHostName">
     <bbbc:config key="mxHostName" configName="ThirdPartyURLs" />
     </c:set>

 <dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
  <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
  <c:choose>
    <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
      <c:set var="pageVariation" value="" scope="request" />
    </c:when>
    <c:when test="${not empty eventType && eventType eq 'Baby'}">
      <c:set var="pageVariation" value="" scope="request" />
    </c:when>
    <c:otherwise>
      <c:set var="pageVariation" value="br" scope="request" />
    </c:otherwise>
  </c:choose>
  <bbb:mxPageContainer>
    <jsp:attribute name="section">registry</jsp:attribute>
    <jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
    <jsp:body>
      <div id="heroContent" class="loggedOut" style="background-image:url(${mxHostName}images/loggedout_reglanding.jpg) !important; ">
        <div id="content" class="container_12 clearfix"> <a id="top"></a>
          <div class="clear"></div>
          <div class="grid_6 logIn" style="left:515px; ">
            <div class="guests">
              <h3>
                <bbbl:label key="lbl_find_registry_registry_header" language ="${pageContext.request.locale.language}"/>
              </h3>
            </div>
            <c:set var="findButton">
            <bbbl:label key='lbl_find_mxreg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label>
            </c:set>
            <c:set var="findRegistryFormCount" value="1" scope="request"/>
            <dsp:include page="find_mxregistry_widget.jsp">
              <dsp:param name="findRegistryFormId" value="frmFindRegistry" />
              <dsp:param name="submitText" value="${findButton}" />
              <dsp:param name="successURL" value="${contextPath}/mx/registry_search_guest.jsp" />
              <dsp:param name="errorURL" value="${findErrorURL }" />
              <dsp:param name="bridalException" value="false" />
              <dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
            </dsp:include>
          </div>
        </div>
      </div>
      </div>
      
      <!-- NAVIGATION RIBBON START-->
      <div class="clearfix cb">
      <div class="catTabsData prodIconsCarousal">
 <div class="carousel clearfix">
						<div class="carouselBody ">
  <div class="bridalCarousalCA clearfix">     
           <ul class=" container_12" >
                <li class="mxnoCarousel" ><a title="Beneficios" href="${mxHostName}RegistryFeatures.html" ><img src="${mxHostName}images/bbregistry/icon_20140225_beneficios.png" alt="Beneficios"> 
<p class="grid_1 prodIcons padTop_10">Beneficios </p> </a> </li>
                <li class="mxnoCarousel" ><a title="Preguntas frecuentes" href="${mxHostName}RegistryFAQ.html" > <img src="${mxHostName}images/bbregistry/icon_20140225_preguntas.png" alt="Preguntas frecuentes"> 
<p class="grid_1 prodIcons">Preguntas<br> frecuentes</p> </a> </li>
                <li class="mxnoCarousel" ><a title="Lista de regalos" href="${mxHostName}RegistryChecklist.html"> <img src="${mxHostName}images/bbregistry/icon_20121220_bridaltoolkit.jpg" alt="Lista de regalos"> 
<p class="grid_1 prodIcons">Lista de <br>regalos</p> </a> </li>
                <li class="mxnoCarousel" ><a title="Gu&iacute;as Y Consejos" href="${mxHostName}RegistryGuides.html"> <img src="${mxHostName}images/bbregistry/icon_20121220_bridalbook.jpg" alt="Gu&iacute;as Y Consejos" > 
<p class="grid_1 prodIcons">Gu&iacute;as Y <br>Consejos</p> </a> </li>
                <li class="mxnoCarousel" ><a title="Libro de ideas" href="${mxHostName}RegistryBook.html"> <img src="${mxHostName}images/bbregistry/icon_20121220_bridalshows.jpg" alt="Libro de ideas" > 
<p class="grid_1 prodIcons">Libro de <br>ideas</p> </a> </li>
              </ul>
            </div>
          </div>
          <!--end  NAVIGATION RIBBON --> 
      
        </div>
        </div>
        </div>
        
        <!--*****************Bottom Page Images Linking to Circular, Gift Cards, and Bridal Page*****************-->
        <div class="container_12 clearfix">
          <div class="categoryContent grid_12 marTop_20" >
            <div class="contentImages clearfix marBottom_10" >
              <div class="grid_3 alpha noOverflow"><a href="${mxHostName}FindStoreHome.html" title=""><img src="${mxHostName}images/findAStore.jpg"></img></a> </div>
              <div class="grid_3 noOverflow"> <a href="${mxHostName}GiftCardsHome.html" title=""><img height="293" alt="" width="229" src="${mxHostName}images/promoWeddingGiftcard.jpg"></img></a> </div>
              <!--<div class="grid_6 omega"> <a href="GiftRegistryHome.html" title=""><img height="293" alt="" width="478" src="images/find_a_registry_background_2.png"></img></a> </div>-->
              <div class="grid_3 noOverflow"> <img height="293" alt="" width="229" src="${mxHostName}images/promoPriceGuar.jpg"></img></div>
              <div class="grid_3 omega"> <a href="${mxHostName}RegistryBook.html" title=""><img height="293" alt="" width="229" src="${mxHostName}images/Ideabook.jpg"></img></a> </div>
            </div>
          </div>
        </div>
      
        <!--*****************END Bottom Page Images Linking to Circular, Gift Cards, and Bridal Page*****************--> 
        
    </jsp:body>
  </bbb:mxPageContainer>
</dsp:page>
