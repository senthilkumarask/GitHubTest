<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- <map name="recommendThings"><area alt="Recommend Things for your friends's registry" href="#" title="Recommend Things for your friends's registry" coords="1,0,998,579" shape="rect">
	</map> -->
	<div class="tab_wrapper">
	
	<div class="kickstarterSectionHeader grid_12 pending_recommendation_header registry_commendation_header">
         <div class="grid_5 alpha">
            <bbbt:textArea key="txt_registry_landing_recommendations"
							language="${pageContext.request.locale.language}"></bbbt:textArea>
         </div>
         <div class="grid_7 omega">
           <bbbl:label key="txt_registry_recomm_area" language="${pageContext.request.locale.language}"/>
         </div>
      </div>
	<div id="heroContent2" class="loggedOut grid_12 alpha">
		<img src="//s7d9.scene7.com/is/image/BedBathandBeyond/images/registry/loggedin_reglanding_20130531.jpg" />
		<div class="imageContent" class="grid_5">
			<h2> <bbbl:label key="lbl_Get_Recommendations_from_friends_family" language="${pageContext.request.locale.language}"/></h2>
			<p>Lorem ipsum dolor sit amet. Etham porta sem malesuadda magna mollis euismod. Etham porta sem malesucda magna mollis euismod. </p>
			<div  class="button button_active viewPicksButton"><a href="#"> <bbbl:label key="lbl_Invite_Your_Friends" language="${pageContext.request.locale.language}"/></a></div>
		</div>

	</div>
	<div id ="contentDiv1" class="grid_12 alpha">
		<div class="grid_4 alpha">
			<h4><bbbl:label key="lbl_Get_Their_Input" language="${pageContext.request.locale.language}"/></h4>
			<div>
				<a title="" href=""><img alt="" src="#"></a>
			</div>
			  <bbbl:label key="txt_registry_recomm_area" language="${pageContext.request.locale.language}"/>
		</div>
		<div class="grid_4 alpha omega">
			<h4><bbbl:label key="lbl_Add_Items_to_Your_Registry" language="${pageContext.request.locale.language}"/></h4>
			<div>
				<a title="" href=""><img alt="" src="#"></a>
			</div>
			<p>Lorem ipsum dolor sit amet. Duis mollis ,est non commoda luctus, nis erat porttitor ligula, eget lacinia odio sem nec elit. </p>
		</div>
		<div class="grid_4 alpha omega">
			<h4><bbbl:label key="lbl_Manage_Your_Recommenders" language="${pageContext.request.locale.language}"/> </h4>
			<div>
				<a title="" href=""><img alt="" src="#"></a>
			</div>
			<p>Nullam quis risus eget urna mollis ornare vel eu leo . Sociis natoque penatibus et magnis dis parturient montes , nascetur ridiculus mus. 	
			</p>
		</div>
	</div>
	</div>
	<div id="getStartedRecommending" class="grid_12 alpha">
		<div class="grid_9">
		<span id="getStarted"><bbbl:label key="lbl_Get_Started_dsk_recomm" language="${pageContext.request.locale.language}"/></span>
		<span id="recommend"> <bbbl:label key="lbl_recommending_dsk_recomm" language="${pageContext.request.locale.language}"/></span>
	</div>
		<div id="getButton" class="grid_2">
			<div  class="button button_active viewPicksButton"><a href="#">Invite Your Friends</a></div>
			<!-- <p id="accountExist">
			Already have an account <a href="#">Log In</a>
			</p> -->
		</div>
	</div>
	</dsp:page>