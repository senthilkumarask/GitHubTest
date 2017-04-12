<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*" %>

<dsp:importbean bean="/com/bbb/idm/TBSIDMFormHandler"/>
<c:set var="emailPlaceholder">
    <bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="passPlaceholder">
    <bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" />
</c:set>

<dsp:page>
<head><title>TBS IDM login</title></head>
<body>

<div id="associateLoginModal" class="reveal-modal small" data-reveal data-options="close_on_background_click:false;close_on_esc:false;">
	<c:set var="url">${pageContext.request.requestURL}</c:set>
	<c:set var="uri">${pageContext.request.requestURI}</c:set>
    <div class="small-12 columns">
        <h2>Associate Login</h2>
    </div>
    
    <div id="authfailed" class="small-12 columns error hidden">
        <p class="error">Authorization Failed</p>
    </div>
    
    <dsp:form id="login" action="idm_login.jsp" method="post">
        <div class="row">
            <!-- <div class="small-12 columns">
                <label id="lblemail" for="email">Username</label>
            </div> -->
            <div class="small-12 columns">
                <dsp:input id="associateEmail" bean="TBSIDMFormHandler.username" type="text">
                    <dsp:tagAttribute name="placeholder" value="BBBUID"/>
                </dsp:input>
                <small for="associateEmail" class="error hidden" id="errorAssociateEmail">Please enter a valid username.</small>
            </div>
            <!-- <div class="small-12 columns">
                <label id="lblpassword" for="password">Password</label>
            </div> -->
            <div class="small-12 columns">
                <dsp:input bean="TBSIDMFormHandler.password" id="associatePassword" type="password" autocomplete="off">
                    <dsp:tagAttribute name="placeholder" value="${passPlaceholder}"/>
                </dsp:input>
                <small for="associatePassword" class="error hidden" id="errorAssociatePassword">Please enter valid current password.</small>
            </div>
            <div class="small-6 columns">
                <a class="small button secondary column" href="/tbs">Cancel</a>
            </div>
            <div class="small-6 columns">
                <dsp:input bean="TBSIDMFormHandler.assocAuth" type="submit" iclass="small button service expand" value="Log In" />
            </div>
            <dsp:input bean="TBSIDMFormHandler.fromPage" type="hidden" value="idmLogin" />
            	<%-- <dsp:input bean="TBSIDMFormHandler.successURL" value="/tbs/account/idm/login_success.jsp" type="hidden"/>
		<dsp:input bean="TBSIDMFormHandler.errorURL" value="/tbs/account/idm/login_fail.jsp" type="hidden"/> --%>
        </div>
    </dsp:form>
</div>
<div id="result" class="reveal-modal small" data-reveal>
<a class="close-reveal-modal">&#215;</a>
</div>
</body>
<script type="text/javascript">
$(document).ready(function(){
    // js functions to handle associate login
    var associate1 = "${sessionScope.associate1}";
    if (associate1 == null || associate1 == ""){
        $('#associateLoginModal').foundation('reveal', 'open');
    }
    $('body').on('focus', '#associateEmail', function(event){
        $('#associateEmail').removeClass('error');
        $('#errorAssociateEmail').addClass('hidden');
    });
    $('body').on('focus', '#associatePassword', function(event){
        $('#associatePassword').removeClass('error');
        $('#errorAssociatePassword').addClass('hidden');
    });
    $("#login input.service").click(function( event ) {
        if($('#associateEmail').val() === '') {
            $('#associateEmail').addClass('error');
            $('#errorAssociateEmail').removeClass('hidden');
            event.preventDefault();
            event.stopPropagation();
        }
        else if($('#associatePassword').val() === '') {
            $('#associatePassword').addClass('error');
            $('#errorAssociatePassword').removeClass('hidden');
            event.preventDefault();
            event.stopPropagation();
        }
        else {
            $('#login').ajaxForm({
                url: '/tbs/account/idm/idm_login.jsp',
                type: 'POST',
                success:  function(data) {
                    if(data.indexOf("Unsuccessful") > -1){
                        /*$('#result').html(data);
                        $('#result').foundation('reveal', 'open');
                        $(document).foundation('reflow');*/
                        $('#authfailed').removeClass('hidden');
                    }
                    if(data.indexOf("Failed") > -1){
                        //$('#associateLoginModal').html(data).foundation('reveal', 'open');
                    	$('#authfailed').removeClass('hidden');
                    }
                    else {
                        $('#associateLoginModal').foundation('reveal', 'close');
                        $(".associateLogout,.associateLogoutMobile").removeClass("hidden");
                        $(".search-container").addClass("search-container-asso");
                        var assoEmail = $('#associateEmail').val();
                        assoEmail = assoEmail.substr(0,7);
                        assoEmail = " "+assoEmail;
                        $(".associateLogout p,.associateLogoutMobile p").text($(".associateLogout p").text().replace(" ",assoEmail));
                        //$(".login-forms h1").addClass("marTop_35");
                    }
                }
            });
        }
    });
});
</script>
</dsp:page>