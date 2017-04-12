<dsp:page>
	<!-- Third Party Tag ON/OFF Include start  6/18/2013-->
	<c:set var="pageSecured" value="${pageContext.request.secure}" scope="request" />

	<c:set var="bExternalJSCSS" value="${true}" scope="request" />
	<c:set var="bAnalytics" value="${true}" scope="request" />
	<c:set var="bPlugins" value="${true}" scope="request" />
	<c:set var="bAsync" value="${true}" scope="request" />

	<c:set var="MapQuestOnAsync" value="${true}" scope="request" />

	<%-- document.write will not work with async load, so disabling it for the following --%>
	<c:set var="OmnitureOnAsync" value="${false}" scope="request" />
	<c:set var="TBSQASOnAsync" value="${false}" scope="request" />
	<c:set var="ForeseeOnAsync" value="${false}" scope="request" />
	<c:set var="TeaLeafOnAsync" value="${false}" scope="request" />
	<c:set var="bAdobeActiveContent" value="${true}" scope="request" />

	<%-- disabling scene7 async load in an attempt to prevent delay in initialization --%>
	<c:set var="SceneSevenOnAsync" value="${false}" scope="request" />

	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="TBS_BedBathUSSite" scope="request">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite" scope="request">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite" scope="request">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite or currentSiteId eq 'BedBathUS'}">
			<c:set var="mPulseON" scope="request"><tpsw:switch tagName="mPulse_us"/></c:set>
			<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_us"/></c:set>
			<c:set var="eDialogON" scope="request"><tpsw:switch tagName="eDialog_us"/></c:set>
			<c:set var="YourAmigoON" scope="request"><tpsw:switch tagName="YourAmigo_us"/></c:set>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
			<c:set var="BopusOn" scope="request"><tpsw:switch tagName="BopusTag_us"/></c:set>
			<c:set var="BridalBookOn" scope="request"><tpsw:switch tagName="BridalBookTag_us"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_us"/></c:set>
			<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_us"/></c:set>
			<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_us"/></c:set>
			<c:set var="EmailOn" scope="request"><tpsw:switch tagName="EmailTag_us"/></c:set>
			<c:set var="FBOn" scope="request"><tpsw:switch tagName="FBTag_us"/></c:set>
			<c:set var="GoogleAnalyticsOn" scope="request"><tpsw:switch tagName="GoogleAnalyticsTag_us"/></c:set>
			<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_us"/></c:set>
			<%-- Commenting these 3rd party services as part of 34473
			<c:set var="ClicktochatOn" scope="request"><tpsw:switch tagName="ClicktochatTag_us"/></c:set>
			<c:set var="IcrossingOn" scope="request"><tpsw:switch tagName="IcrossingTag_us"/></c:set>
			<c:set var="ForeseeOn" scope="request"><tpsw:switch tagName="ForeseeTag_us"/></c:set>
			<c:set var="SmartSEOOn" scope="request"><tpsw:switch tagName="Smart_SEO_US"/></c:set>
			<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_us" /></c:set>
			<c:set var="CommisionJunctionOn" scope="request"><tpsw:switch tagName="CommisionJunctionTag_us"/></c:set>
			--%>
			<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_us"/></c:set>
			<c:set var="LiveClickerOn" scope="request"><tpsw:switch tagName="LiveClickerTag_us"/></c:set>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_us"/></c:set>
			<c:set var="LoadMQALibOn" scope="request"><tpsw:switch tagName="LoadMQALib_us"/></c:set>
			<c:set var="MomOn" scope="request"><tpsw:switch tagName="MomTag_us"/></c:set>
			<c:set var="OmnitureOn" scope="request"><tpsw:switch tagName="OmnitureTag_us"/></c:set>
			<c:set var="OrderHistoryOn" scope="request"><tpsw:switch tagName="OrderHistoryTag_us"/></c:set>
			<c:set var="OrderSubmissionOn" scope="request"><tpsw:switch tagName="OrderSubmissionTag_us"/></c:set>
			<c:set var="OutOfStockOn" scope="request"><tpsw:switch tagName="OutOfStockTag_us"/></c:set>
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="QASTag_us"/></c:set>
			<c:set var="RKGOn" scope="request"><tpsw:switch tagName="RKGTag_us"/></c:set>
			<c:set var="RichFXOn" scope="request"><tpsw:switch tagName="RichFXTag_us"/></c:set>
			<c:set var="SceneSevenOn" scope="request"><tpsw:switch tagName="SceneSevenTag_us"/></c:set>
			<c:set var="SurveyOn" scope="request"><tpsw:switch tagName="SurveyTag_us"/></c:set>
			<c:set var="TellAFriendOn" scope="request"><tpsw:switch tagName="TellAFriendTag_us"/></c:set>
			<c:set var="TellApartOn" scope="request"><tpsw:switch tagName="TellApartTag_us"/></c:set>
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_us"/></c:set>
			<c:set var="WeddingchannelOn" scope="request"><tpsw:switch tagName="WeddingchannelTag_us"/></c:set>
			<c:set var="ValueClickOn" scope="request"><tpsw:switch tagName="ValueClickTag_us"/></c:set>
			<c:set var="TheBumpsOn" scope="request"><bbbc:config key="BumpOptIn" configName="FlagDrivenFunctions" /></c:set>
			<%-- <c:set var="TeaLeafOn" scope="request"><tpsw:switch tagName="TeaLeafTag_us"/></c:set> --%>
			<c:set var="OutdatedBrowserMsgOn" scope="request"><tpsw:switch tagName="OutdatedBrowserMsgTag_us"/></c:set>
			<c:set var="TagManOn" scope="request"><tpsw:switch tagName="TagMan_US"/></c:set>
			<c:set var="HolidayMessagingOn" scope="request"><bbbc:config key="isHolidayMessaging" configName="FlagDrivenFunctions" /></c:set>
			<c:set var="upcGiftRegistryEnabled" scope="request"><bbbc:config key="upcGiftRegistryEnabled_us" configName="FlagDrivenFunctions" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite or currentSiteId eq 'BuyBuyBaby'}">
			<c:set var="mPulseON" scope="request"><tpsw:switch tagName="mPulse_baby"/></c:set>
			<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_baby"/></c:set>
			<c:set var="eDialogON" scope="request"><tpsw:switch tagName="eDialog_baby"/></c:set>
			<c:set var="YourAmigoON" scope="request"><tpsw:switch tagName="YourAmigo_baby"/></c:set>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
			<c:set var="BopusOn" scope="request"><tpsw:switch tagName="BopusTag_baby"/></c:set>
			<c:set var="BabyBookOn" scope="request"><tpsw:switch tagName="BridalBookTag_baby"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_baby"/></c:set>
			<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_baby"/></c:set>
			<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_baby"/></c:set>
			<c:set var="EmailOn" scope="request"><tpsw:switch tagName="EmailTag_baby"/></c:set>
			<c:set var="FBOn" scope="request"><tpsw:switch tagName="FBTag_baby"/></c:set>
			<c:set var="GoogleAnalyticsOn" scope="request"><tpsw:switch tagName="GoogleAnalyticsTag_baby"/></c:set>
			<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_baby"/></c:set>
			<%-- Commenting out these 3rd party services as part of 34473
			<c:set var="ClicktochatOn" scope="request"><tpsw:switch tagName="ClicktochatTag_baby"/></c:set>
			<c:set var="IcrossingOn" scope="request"><tpsw:switch tagName="IcrossingTag_baby"/></c:set>
			<c:set var="ForeseeOn" scope="request"><tpsw:switch tagName="ForeseeTag_baby"/></c:set>
			<c:set var="SmartSEOOn" scope="request"><tpsw:switch tagName="Smart_SEO_Baby"/></c:set>
			<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_baby" /></c:set>
			<c:set var="CommisionJunctionOn" scope="request"><tpsw:switch tagName="CommisionJunctionTag_baby"/></c:set>
			--%>
			<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_baby"/></c:set>
			<c:set var="LiveClickerOn" scope="request"><tpsw:switch tagName="LiveClickerTag_baby"/></c:set>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_baby"/></c:set>
			<c:set var="LoadMQALibOn" scope="request"><tpsw:switch tagName="LoadMQALib_baby"/></c:set>
			<c:set var="MomOn" scope="request"><tpsw:switch tagName="MomTag_baby"/></c:set>
			<c:set var="OmnitureOn" scope="request"><tpsw:switch tagName="OmnitureTag_baby"/></c:set>
			<c:set var="OrderHistoryOn" scope="request"><tpsw:switch tagName="OrderHistoryTag_baby"/></c:set>
			<c:set var="OrderSubmissionOn" scope="request"><tpsw:switch tagName="OrderSubmissionTag_baby"/></c:set>
			<c:set var="OutOfStockOn" scope="request"><tpsw:switch tagName="OutOfStockTag_baby"/></c:set>
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="QASTag_baby"/></c:set>
			<c:set var="RKGOn" scope="request"><tpsw:switch tagName="RKGTag_baby"/></c:set>
			<c:set var="RichFXOn" scope="request"><tpsw:switch tagName="RichFXTag_baby"/></c:set>
			<c:set var="SceneSevenOn" scope="request"><tpsw:switch tagName="SceneSevenTag_baby"/></c:set>
			<c:set var="SurveyOn" scope="request"><tpsw:switch tagName="SurveyTag_baby"/></c:set>
			<c:set var="TellAFriendOn" scope="request"><tpsw:switch tagName="TellAFriendTag_baby"/></c:set>
			<c:set var="TellApartOn" scope="request"><tpsw:switch tagName="TellApartTag_baby"/></c:set>
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_baby"/></c:set>
			<c:set var="WeddingchannelOn" scope="request"><tpsw:switch tagName="WeddingchannelTag_baby"/></c:set>
			<c:set var="ValueClickOn" scope="request"><tpsw:switch tagName="ValueClickTag_baby"/></c:set>
			<c:set var="TheBumpsOn" scope="request"><bbbc:config key="BumpOptIn" configName="FlagDrivenFunctions" /></c:set>
			<%-- <c:set var="TeaLeafOn" scope="request"><tpsw:switch tagName="TeaLeafTag_baby"/></c:set> --%>
			<c:set var="TagManOn" scope="request"><tpsw:switch tagName="TagMan_Baby"/></c:set>
			<c:set var="OutdatedBrowserMsgOn" scope="request"><tpsw:switch tagName="OutdatedBrowserMsgTag_baby"/></c:set>
			<c:set var="HolidayMessagingOn" scope="request"><bbbc:config key="isHolidayMessaging" configName="FlagDrivenFunctions" /></c:set>
			<c:set var="upcGiftRegistryEnabled" scope="request"><bbbc:config key="upcGiftRegistryEnabled_baby" configName="FlagDrivenFunctions" /></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="mPulseON" scope="request"><tpsw:switch tagName="mPulse_ca"/></c:set>
			<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_ca"/></c:set>
			<c:set var="eDialogON" scope="request"><tpsw:switch tagName="eDialog_ca"/></c:set>
			<c:set var="YourAmigoON" scope="request"><tpsw:switch tagName="YourAmigo_ca"/></c:set>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
			<c:set var="BopusTagOn" scope="request"><tpsw:switch tagName="BopusTag_ca"/></c:set>
			<c:set var="BridalBookOn" scope="request"><tpsw:switch tagName="BridalBookTag_ca"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_ca"/></c:set>
			<c:set var="ContactUsOn" scope="request"><tpsw:switch tagName="ContactUsTag_ca"/></c:set>
			<c:set var="CouponOn" scope="request"><tpsw:switch tagName="CouponTag_ca"/></c:set>
			<c:set var="EmailOn" scope="request"><tpsw:switch tagName="EmailTag_ca"/></c:set>
			<c:set var="FBOn" scope="request"><tpsw:switch tagName="FBTag_ca"/></c:set>
			<c:set var="GoogleAnalyticsOn" scope="request"><tpsw:switch tagName="GoogleAnalyticsTag_ca"/></c:set>
			<c:set var="HarteHanksOn" scope="request"><tpsw:switch tagName="HarteHanksTag_ca"/></c:set>
			<%-- Commenting out these 3rd party services as part of 34473
			<c:set var="ClicktochatOn" scope="request"><tpsw:switch tagName="ClicktochatTag_ca"/></c:set>
			<c:set var="IcrossingOn" scope="request"><tpsw:switch tagName="IcrossingTag_ca"/></c:set>
			<c:set var="ForeseeOn" scope="request"><tpsw:switch tagName="ForeseeTag_ca"/></c:set>
			<c:set var="SmartSEOOn" scope="request"><tpsw:switch tagName="Smart_SEO_CA"/></c:set>
			<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_ca" /></c:set>
			<c:set var="CommisionJunctionOn" scope="request"><tpsw:switch tagName="CommisionJunctionTag_ca"/></c:set>
			--%>
			<c:set var="KirschOn" scope="request"><tpsw:switch tagName="KirschTag_ca"/></c:set>
			<c:set var="LiveClickerOn" scope="request"><tpsw:switch tagName="LiveClickerTag_ca"/></c:set>
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_ca"/></c:set>
			<c:set var="LoadMQALibOn" scope="request"><tpsw:switch tagName="LoadMQALib_ca"/></c:set>
			<c:set var="MomOn" scope="request"><tpsw:switch tagName="MomTag_ca"/></c:set>
			<c:set var="OmnitureOn" scope="request"><tpsw:switch tagName="OmnitureTag_ca"/></c:set>
			<c:set var="OrderHistoryOn" scope="request"><tpsw:switch tagName="OrderHistoryTag_ca"/></c:set>
			<c:set var="OrderSubmissionOn" scope="request"><tpsw:switch tagName="OrderSubmissionTag_ca"/></c:set>
			<c:set var="OutOfStockOn" scope="request"><tpsw:switch tagName="OutOfStockTag_ca"/></c:set>
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="QASTag_ca"/></c:set>
			<c:set var="RKGOn" scope="request"><tpsw:switch tagName="RKGTag_ca"/></c:set>
			<c:set var="RichFXOn" scope="request"><tpsw:switch tagName="RichFXTag_ca"/></c:set>
			<c:set var="SceneSevenOn" scope="request"><tpsw:switch tagName="SceneSevenTag_ca"/></c:set>
			<c:set var="SurveyOn" scope="request"><tpsw:switch tagName="SurveyTag_ca"/></c:set>
			<c:set var="TellAFriendOn" scope="request"><tpsw:switch tagName="TellAFriendTag_ca"/></c:set>
			<c:set var="TellApartOn" scope="request"><tpsw:switch tagName="TellApartTag_ca"/></c:set>
			<c:set var="ValueLinkOn" scope="request"><tpsw:switch tagName="ValueLinkTag_ca"/></c:set>
			<c:set var="WeddingchannelOn" scope="request"><tpsw:switch tagName="WeddingchannelTag_ca"/></c:set>
			<c:set var="ValueClickOn" scope="request"><tpsw:switch tagName="ValueClickTag_ca"/></c:set>
			<c:set var="TheBumpsOn" scope="request"><bbbc:config key="BumpOptIn" configName="FlagDrivenFunctions" /></c:set>
		<%-- 	<c:set var="TeaLeafOn" scope="request"><tpsw:switch tagName="TeaLeafTag_ca"/></c:set> --%>
			<c:set var="TagManOn" scope="request"><tpsw:switch tagName="TagMan_CA"/></c:set>
			<c:set var="OutdatedBrowserMsgOn" scope="request"><tpsw:switch tagName="OutdatedBrowserMsgTag_ca"/></c:set>
			<c:set var="HolidayMessagingOn" scope="request"><bbbc:config key="isHolidayMessaging" configName="FlagDrivenFunctions" /></c:set>
			<c:set var="upcGiftRegistryEnabled" scope="request"><bbbc:config key="upcGiftRegistryEnabled_ca" configName="FlagDrivenFunctions" /></c:set>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite or currentSiteId eq 'BedBathUS'}">
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="TBS_QASTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite or currentSiteId eq 'BuyBuyBaby'}">
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="TBS_QASTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="TBSQASOn" scope="request"><tpsw:switch tagName="TBS_QASTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>
	<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="browser">${fn:toLowerCase(header['User-Agent'])}</c:set>
	<c:if test="${TeaLeafOn && fn:containsIgnoreCase(browser,'ipad')}">
		<c:set var="TeaLeafOn" scope="request">${false}</c:set>
	</c:if>

	<c:set var="shareProfileOn" scope="request">
		<bbbc:config key="ShareProfile" configName="ContentCatalogKeys" />
	</c:set>
	<!-- adding this flag for R2.2 scope 83a  -->
	<c:set var="paypalOn" scope="request"><bbbc:config key="paypalFlag" configName="FlagDrivenFunctions" /></c:set>
	<!-- adding this flag for R2.2 scope 153a  -->
	<c:set var="everLivingOn" scope="request"><bbbc:config key="EverLivingPDPOn" configName="FlagDrivenFunctions" /></c:set>

	<!-- adding this flag for International Shipping switch R2.2.1-->
	<c:set var="internationalShippingOn" scope="request"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>

	<!-- adding this flag for International Merchant Id R2.2.1-->
	<c:set var="internationalMerchantId" scope="request"><bbbc:config key="international_merchant_id" configName="International_Shipping"/></c:set>

	<!-- adding this flag for International Welcome Mat Url R2.2.1-->
	<c:set var="welcomeMatUrl" scope="request"><bbbc:config key="welcome_mat_url" configName="International_Shipping"/></c:set>

	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	<!-- adding this flag for R2.2 scope 158  -->
	<c:set var="deviceFingerprintOn" scope="request"><bbbc:config key="deviceFingerprintON" configName="FlagDrivenFunctions" /></c:set>
	<!-- adding this flag for R2.2 scope 83-3  -->
	<c:set var="paypalButtonEnable" scope="request"><bbbc:config key="paypalButtonFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="paypalCartButtonEnable" scope="request"><bbbc:config key="paypalButtonCartFlag" configName="FlagDrivenFunctions" /></c:set>

	<!-- adding these 2 flags for R2.2 scope 258  -->
	<c:set var="visaLogoOn" scope="request"><bbbc:config key="visaLogoFlag" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="masterCardLogoOn" scope="request"><bbbc:config key="masterCardLogoFlag" configName="FlagDrivenFunctions" /></c:set>

	<!-- Third Party Tag ON/OFF Include End -->
</dsp:page>
