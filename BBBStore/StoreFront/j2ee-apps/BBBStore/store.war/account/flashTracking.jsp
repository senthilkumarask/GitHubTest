<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<div id="trackingContainer">
    <h3><strong><bbbl:label key="lbl_upgrade_flash" language="${pageContext.request.locale.language}" /></strong></h3>

    <a href="https://www.adobe.com/go/getflashplayer" target="getflashplayer">
        <img src=https://www.adobe.com/images/shared/download_buttons/get_flash_player.gif
         alt="Get Adobe Flash Player" width="88" height="31" border="0" />

        <bbbl:label key="lbl_download_flash" language="${pageContext.request.locale.language}" />! </a>
    <p> <bbbl:label key="lbl_enable_js" language="${pageContext.request.locale.language}" /></p>
</div>
<script type="text/JavaScript" src="https://sandbox.borderfree.com/tracking/swfobject.js"></script>
<script type="text/javascript">

    var ePath = "https://sandbox.borderfree.com/flex/tracking/tracking.swf";
    var eID = "flashOrderGeneralId";
    var eWidth = "986px";
    var eHeight = "750px";
    var fpMinVer = "9";
    var eBackground = "#FFFFFF";
    var so = new SWFObject(ePath, eID, eWidth, eHeight, fpMinVer, eBackground);
    so.setAttribute("AllowScriptAccess","always");
    so.useExpressInstall("https://sandbox.fiftyone.com/utils/expressinstall.swf");
    so.setAttribute("xiRedirectUrl",window.location);
    so.addParam("wmode", "transparent");
    so.write("trackingContainer");
</script>