<dsp:page>
<%--This JSP would load in cases of Dynamic Checklist. Story : BBBI-1071
Dynamic Checklist would be loaded on :

1. When the Static Checklist Flag for the particular Registry is turned off,and
2. When the page is loaded from ajax.

Input Params:

1.Registry Type
2. Registry IDs

--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
<dsp:setvalue param="checklistVO" beanvalue="SessionBean.checklistVO"/>
<dsp:getvalueof var="checklistVO" param="checklistVO"/>
<section id="C1-Slider" class="grid_12 clearfix interactive-flyout <c:if test="${empty checklistVO}">hidden</c:if>">
<c:set var="clickToView" scope="request"><bbbl:label key='lbl_click_tap_open' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="subCategories" scope="request"><bbbl:label key='lbl_subcategories' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="categoryTxt" scope="request"><bbbl:label key='lbl_category' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="navigateToPLP" scope="request"><bbbl:label key='lbl_navigate_to_plp' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="progressStatus" scope="request"><bbbl:label key='lbl_progress_Status_for' language="${pageContext.request.locale.language}"/> </c:set>
<c:set var="notStarted" scope="request"><bbbl:label key='lbl_not_started' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="itemsMarkedComplete" scope="request"><bbbl:label key='lbl_items_Marked_Complete' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="itemsRecommendedToAdd" scope="request"><bbbl:label key='lbl_items_Recommended_To_Add' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="checkToAdd" scope="request"><bbbl:label key='lbl_check_To_Add' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="checkToRemove" scope="request"><bbbl:label key='lbl_check_To_Remove' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="viewYourItems" scope="request"><bbbl:label key='lbl_view_Your_Items' language="${pageContext.request.locale.language}"/></c:set>

<c:set var="overallProgress"><fmt:formatNumber value="${checklistVO.averageC1Percentage}" maxFractionDigits="0" /></c:set>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry" />

   <c:choose>
<c:when test= "${fn:contains(sessionRegistry.registryType.registryTypeDesc,'College') || fn:containsIgnoreCase(registrySummaryVO.registryType.registryTypeDesc, 'University')}">
		 <c:set var="checkListKey" value="CollegeCheckListProgress" />
</c:when>
<c:otherwise>
  <c:set var="checkListKey" value="${sessionRegistry.registryType.registryTypeDesc}CheckListProgress" />
</c:otherwise>
</c:choose> 
  
  <c:set var="registryCheckListKey">
            <bbbc:config key="${checkListKey}" configName="ContentCatalogKeys" />
        </c:set>
<div class="overall-progress-bar ib fl">
<div class="left-container">
        <a id="openChecklistFullView" href="javascript:void(0);" aria-label="${clickToView} the <bbbl:label key="lbl_interactive_full_view" language="${pageContext.request.locale.language}"/> of ${registrySummaryVO.registryType.registryTypeDesc} <bbbl:label key="lbl_interactive_checklist" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_interactive_full_view" language="${pageContext.request.locale.language}"/></a>
    <div  class="registry-checklist-name">${registrySummaryVO.registryType.registryTypeDesc} <bbbl:label key="lbl_interactive_checklist" language="${pageContext.request.locale.language}"/></div>
        <div class="complete-status">
                <span class="visuallyhidden">${overallProgress}% <bbbl:label key="lbl_interactive_complete" language="${pageContext.request.locale.language}"/></span>
                <span aria-hidden='true'>${overallProgress}% </span><span aria-hidden='true'>&nbsp;<bbbl:label key="lbl_interactive_complete" language="${pageContext.request.locale.language}"/></span></div>
    <div class="progress-bar" aria-hidden="true">
        <div class="progressWrapper span12">
            <div class="inner-progress-bar_main <c:if test="${overallProgress eq 100}">complete</c:if>" data-progress-complete="${checklistVO.averageC1Percentage}" style="width: ${checklistVO.averageC1Percentage}%;">
            </div>
        </div>
    </div>
</div>
</div>
<c:set var="go_back"><bbbl:label key='lbl_go_back_to' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="emptyC3s" value="${true}"/>
<c:if test="${not empty checklistVO && not empty checklistVO.categoryListVO}">

  <c:forEach items="${checklistVO.categoryListVO}" var="c1Category">
     <c:forEach items="${c1Category.childCategoryVO}" var="c2Category">
          <c:forEach items="${c2Category.childCategoryVO}" var="c3Category">
             <c:set var="emptyC3s" value="${false}"/>
         </c:forEach>
       </c:forEach>
  </c:forEach>


</c:if>

<input type="hidden" id="isEmptyC3s" value="${emptyC3s}"/>

<div class="separator-bar fl ib"></div>
<c:choose>
<c:when test="${!emptyC3s}">
<div class="category-one-list ib">
    <div class="back-icon ib" aria-hidden="true"></div>
    <div class="C1-Scrollable-list ib">
        <ul class="C1-list-items">
        <c:forEach items="${checklistVO.categoryListVO}" var="categoryListVO"  varStatus="countC1">
          <li id="${categoryListVO.categoryId}_c1">
            <c:set var="cat1Url">javascript:void(0);</c:set>
          <!--   <c:if test="${not empty categoryListVO.categoryURL}">
               <c:set var="cat1Url">${contextPath}${categoryListVO.categoryURL}</c:set>
            </c:if> -->

            <c:choose>
                <c:when test="${empty categoryListVO.labelMessage}">
                    <c:set var="cat1labelmsg">${notStarted}</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="cat1labelmsg">${categoryListVO.labelMessage}</c:set>
                </c:otherwise>
            </c:choose>
              <a id="${categoryListVO.categoryId}_c1" href="${cat1Url}" aria-hidden="false" aria-labelledby='c1Name_${countC1.count} progressStatusPercent_${countC1.count} viewSubCategories_${countC1.count}' data-count='${countC1.count}' data-target="#${categoryListVO.categoryId}" >
              <div class="category-img" aria-hidden='true'><img class="c1Img" src="${categoryListVO.imageURL}&wid=38"/></div>
              <div class="C1-name cb"  aria-hidden='true'>${categoryListVO.displayName}</div>
              <div class="progress-bar" aria-hidden="true">
                    <div class="progressWrapper span12">
                        <div class="inner-progress-bar <c:if test="${categoryListVO.c1PercentageComplete eq 100}">complete</c:if>" data-progress-complete="${categoryListVO.c1PercentageComplete}" style="width: ${categoryListVO.c1PercentageComplete}%;">
                        </div>
                    </div>
              </div>
              <c:choose>
              <c:when test="${not empty categoryListVO.labelMessage}">
              <div class="status-message" aria-hidden="true" data-progress-msg="${categoryListVO.labelMessage}"> ${categoryListVO.labelMessage}</div>
              </c:when>
              <c:otherwise>
              <div class="status-message visibilityHidden" data-progress-msg="${categoryListVO.labelMessage}"> ${categoryListVO.categoryId}</div>
              </c:otherwise>
              </c:choose>
            </a>
            <span style='display:none' id="c1Name_${countC1.count}"> ${progressStatus} ${categoryListVO.displayName} ${categoryTxt} is</span>
            <span style='display:none' id="progressStatusMsg_${countC1.count}">${cat1labelmsg}</span>
            <span style='display:none' id="progressStatusPercent_${countC1.count}"><c:choose><c:when test="${categoryListVO.c1PercentageComplete eq 0.0}">0</c:when><c:otherwise>${categoryListVO.c1PercentageComplete}</c:otherwise></c:choose> % .</span>
            <span style='display:none' id="viewSubCategories_${countC1.count}">${clickToView} ${subCategories} ${categoryListVO.displayName} ${categoryTxt}</span>
          </li>
        </c:forEach>
        </ul>
    </div>
    <div class="forward-icon ib" aria-hidden="true"></div>
</div>
<c:set var="noOfC1" value="${fn:length(checklistVO.categoryListVO)}"/>
<div class="c2-category-list clearfix hidden">
        <div class="active-category-arrow"></div>
        <a href="#" class="prev-c2-category" aria-hidden="true">
            <span class="icon-leftarrow"></span>
        </a>
        <div class="c2-category-wrapper">
        <c:forEach items="${checklistVO.categoryListVO}" var="c1Category">
        <ul id="${c1Category.categoryId}">
        <li class="noHeight"><a class="goToC1 visuallyhidden" href="javascript:void(0);" data-ref-id="${c1Category.categoryId}_c1" aria-label="${go_back} ${c1Category.displayName}"></a></li>
         <c:forEach items="${c1Category.childCategoryVO}" var="c2Category" varStatus="countC2">
         <c:set var="noOfC3s" value="${fn:length(c2Category.childCategoryVO)}"/>
            <li class="c2-category">
            <span aria-hidden="true" class="tick">H</span>
            <c:set var="cat2Url">javascript:void(0);</c:set>
          <!--   <c:if test="${not empty c2Category.categoryURL}">
               <c:set var="cat2Url">${contextPath}${c2category.categoryURL}</c:set>
            </c:if> -->
                <a href="${cat2Url}" aria-label="${c2Category.displayName}. ${clickToView} ${subCategories} ${c2Category.displayName} ${categoryTxt}" <c:if test="${not empty c2Category.childCategoryVO}">class="c2-category-link"</c:if> data-menu-content="#${c2Category.categoryId}" data-aria-label="${c2Category.displayName}. ${clickToView} ${subCategories} ${c2Category.displayName} ${categoryTxt}">${c2Category.displayName}</a>
              
                <div id="${c2Category.categoryId}" class="hidden">
                    <section class="c3-categories-container" data-c2Id = "${c2Category.categoryId}">
                        <div class="viewport clearfix registry-checklist-container">
                            <table class="overview registry-checklist-items">
                                <thead>
                                    <tr class="checklist-header">
                                       <th class="checklist-status showCheckListStatus"></th>
                                       <th class="checklist-name"><bbbl:label key="lbl_interactive_browse" language="${pageContext.request.locale.language}"/></th>
                                       <th class="checklist-quantity"><bbbl:label key="lbl_interactive_your_items" language="${pageContext.request.locale.language}"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                     <c:forEach items="${c2Category.childCategoryVO}" var="c3Category" varStatus="countC3">
                                        <c:set var="cat3Url">javascript:void(0);</c:set>
                                        <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c3Url" value="${c3Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value= "${c3Category.usOverriddenURL }"/>													
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c3Url" value="${c3Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value= "${c3Category.babyOverriddenURL }"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c3Url" value="${c3Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC3" value= "${c3Category.caOverriddenURL }"/>
											</c:otherwise>
										</c:choose>
                                        <c:choose>
                                            <c:when  test="${not empty c3Url}">
                                            	 
           											 <c:set var="cat3Url"><c:if test="${!isOverriddenURLC3}">${contextPath}</c:if>${c3Url}</c:set>                  
                                                <c:set var="cat3Element"><a data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}" data-c3name="${c3Category.displayName}" href="${cat3Url}" aria-label="${c3Category.displayName}. ${navigateToPLP}">${c3Category.displayName}</a></c:set>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="cat3Element"><span class="textOnly">${c3Category.displayName}</span></c:set>
                                            </c:otherwise>
                                        </c:choose>
                                    <tr class="checklist-items <c:if test="${c3Category.c3ManualComplete}">c3-active</c:if>">
                                       <td class="checklist-status showCheckListStatus">
                                       <c:set var="addedQuantityForPercentage" value="${c3Category.addedQuantity}"/>
                                       <c:if test="${c3Category.addedQuantity >c3Category.suggestedQuantity}">
                                        <c:set var="addedQuantityForPercentage" value="${c3Category.suggestedQuantity}"/>
                                       </c:if>
                                       
                                        <input aria-hidden='true' <c:if test="${c3Category.c3AutoComplete}">disabled</c:if> id="c3-checkbox-5" type="checkbox" name="c3-checkbox" class="status-input" data-c1name="${c1Category.categoryId}" data-c2name="${c2Category.categoryId}" 
                                        data-c3AddedQuantity="${addedQuantityForPercentage}" data-c3name="${c3Category.categoryId}" data-addedQuantity="${c2Category.addedQuantity}" data-suggestedQuantity="${c1Category.totalSuggestedQuantityForPercentageCalc}" data-averagePercentage="${checklistVO.averageC1Percentage}" data-numberofc1="${noOfC1}"
                                        data-registryId="${registrySummaryVO.registryId}" data-registryType="${registrySummaryVO.registryType.registryTypeName}" <c:if test="${c3Category.c3ManualComplete || c3Category.c3AutoComplete}">checked="checked"</c:if>>
                                          <label for="c3-checkbox-5" class="status-label <c:if test="${c3Category.c3ManualComplete}">manualCheckedc3</c:if>" ><a <c:if test="${c3Category.c3AutoComplete}">aria-disabled="true"</c:if>role='checkbox' 
                                          aria-checked='<c:choose><c:when test="${c3Category.c3ManualComplete || c3Category.c3AutoComplete}">true</c:when><c:otherwise>false</c:otherwise></c:choose>' href="javascript:void(0);" 
                                          aria-label="${c3Category.displayName} <c:choose><c:when test="${c3Category.c3ManualComplete}">${checkToRemove}</c:when><c:when test="${c3Category.c3AutoComplete}"></c:when><c:otherwise>${checkToAdd}</c:otherwise></c:choose>" class="<c:if test="${c3Category.c3AutoComplete}">autoCheckedc3 </c:if><c:if test="${c3Category.c3ManualComplete || c3Category.c3AutoComplete}">checklist-checked</c:if>" data-label-name="${c3Category.displayName}"></a></label>
                                       </td>
                                       <td class="checklist-name">${cat3Element}</td>
                                       <td class="checklist-quantity text-right"><a id="c3Count_${c3Category.categoryId}" href="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registrySummaryVO.registryId}&eventType=${registrySummaryVO.eventType}&c1name=${c1Category.displayName}&c2name=${c2Category.displayName}&c3name=${c3Category.displayName}&qty=${c3Category.addedQuantity} of ${c3Category.suggestedQuantity}&c1id=${c1Category.categoryId}&c2id=${c2Category.categoryId}&c3id=${c3Category.categoryId}#t=myItems" data-c3addedQuantity="${c3Category.addedQuantity}" data-c3suggestedQuantity="${c3Category.suggestedQuantity}" aria-label='${c3Category.addedQuantity} of ${c3Category.suggestedQuantity}
                                        <c:choose><c:when test="${c3Category.c3ManualComplete || c3Category.c3AutoComplete}"> ${itemsMarkedComplete} ${viewYourItems}</c:when><c:otherwise> ${itemsRecommendedToAdd} ${viewYourItems}</c:otherwise></c:choose>'>${c3Category.addedQuantity} of ${c3Category.suggestedQuantity}</a></td>
                                    </tr>
                                    </c:forEach>
                                 <tr>
                                  <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.usOverriddenURL }"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.babyOverriddenURL }"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" value="${c2Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.caOverriddenURL }"/>
											</c:otherwise>
										</c:choose>
    							<td colspan="3">
    								<a data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}" href="<c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}" role="button" class="button-Med btnSecondary checklistShowAll" >		    							
                           			<span>SHOP ALL ${c2Category.displayName} </span></a></td>
  								</tr>   
                                </tbody>
                             </table>
                         </div>
                    </section>
                </div>

            </li>
            </c:forEach>
            <li class="noHeight"><a class="goToC1 visuallyhidden" href="javascript:void(0);" data-ref-id="${c1Category.categoryId}_c1" aria-label="${go_back} ${c1Category.displayName}"></a></li>
        </ul>
    </c:forEach>
    </div>
    <a href="#" class="next-c2-category" aria-hidden="true">
        <span class="icon-rightarrow"></span>
    </a>
    </div>
</c:when>
<c:otherwise>
         <div class="category-one-list ib">
    <div class="back-icon ib" aria-hidden="true"></div>
    <div class="C1-Scrollable-list ib">
        <ul class="C1-list-items">
         <c:forEach items="${checklistVO.categoryListVO}" var="categoryListVO" varStatus="countC1">
          <li id="${categoryListVO.categoryId}_c1">
            <c:set var="cat1Url">javascript:void(0);</c:set>
            <!-- <c:if test="${not empty categoryListVO.categoryURL}">
               <c:set var="cat1Url">${contextPath}${categoryListVO.categoryURL}</c:set>
            </c:if> -->

            <c:choose>
                <c:when test="${empty categoryListVO.labelMessage}">
                    <c:set var="cat1labelmsg">${notStarted}</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="cat1labelmsg">${categoryListVO.labelMessage}</c:set>
                </c:otherwise>
            </c:choose>

              <a href="${cat1Url}" aria-hidden="false" aria-labelledby='c1Name_${countC1.count} progressStatusPercent_${countC1.count} viewSubCategories_${countC1.count}' data-count='${countC1.count}' data-target="#${categoryListVO.categoryId}" data-menu-content="#${categoryListVO.categoryId}">
              <div class="category-img" aria-hidden='true'><img class="c1Img" src="${categoryListVO.imageURL}&wid=38"/></div>
              <div class="C1-name cb"  aria-hidden='true'>${categoryListVO.displayName}</div>
              <div class="progress-bar" aria-hidden="true">
                    <div class="progressWrapper span12">
                        <div class="inner-progress-bar <c:if test="${categoryListVO.c1PercentageComplete eq 100}">complete</c:if>" data-progress-complete="${categoryListVO.c1PercentageComplete}" style="width:${categoryListVO.c1PercentageComplete}%;">
                        </div>
                    </div>
              </div>
               <c:choose>
              <c:when test="${not empty categoryListVO.labelMessage}">
              <div class="status-message" aria-hidden="true" data-progress-msg="${categoryListVO.labelMessage}"> ${categoryListVO.labelMessage}</div>
              </c:when>
              <c:otherwise>
              <div class="status-message visibilityHidden" data-progress-msg="${categoryListVO.labelMessage}"> ${categoryListVO.categoryId}</div>
              </c:otherwise>
              </c:choose>
            </a>
            <span style='display:none' id="c1Name_${countC1.count}">${progressStatus} ${categoryListVO.displayName} ${categoryTxt} is</span>
            <span style='display:none' id="progressStatusMsg_${countC1.count}">${cat1labelmsg}.</span>
            <span style='display:none' id="progressStatusPercent_${countC1.count}"><c:choose><c:when test="${categoryListVO.c1PercentageComplete eq 0.0}">0</c:when><c:otherwise>${categoryListVO.c1PercentageComplete}</c:otherwise></c:choose> % .</span>
            <span style='display:none' id="viewSubCategories_${countC1.count}">${clickToView} ${subCategories} ${categoryListVO.displayName} ${categoryTxt}</span>
          </li>
              </c:forEach>
        </ul>
    </div>
    <div class="forward-icon ib" aria-hidden="true"></div>
</div>
        <c:set var="noOfC1" value="${fn:length(checklistVO.categoryListVO)}"/>
        <c:forEach items="${checklistVO.categoryListVO}" var="c1Category">
                 <div id="${c1Category.categoryId}" class=" hidden hiddenInteractive">
                  <section class="c3-categories-container">
                        <div class="viewport clearfix registry-checklist-container">
                            <table class="overview registry-checklist-items">
                                <thead>
                                    <tr class="checklist-header">
                                       <th class="checklist-status showCheckListStatus"></th>
                                       <th class="checklist-name"><bbbl:label key="lbl_interactive_browse" language="${pageContext.request.locale.language}"/></th>
                                       <th class="checklist-quantity">
                                   <c:choose>
       						 											<c:when test="${registryCheckListKey eq 'on'}"><bbbl:label key='lbl_interactive_your_items' language="${pageContext.request.locale.language}" /></c:when>
            																<c:when test="${registryCheckListKey eq 'off'}"><bbbl:label key='lbl_checklist_full_view_recommended' language="${pageContext.request.locale.language}" /></c:when>
        															</c:choose>
        															</th>
                                    </tr>
                                </thead>
                                <tbody>
                                     <c:forEach items="${c1Category.childCategoryVO}" var="c2Category">
                                    <c:set var="cat2Url">javascript:void(0);</c:set>
                                    <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.usOverriddenURL }"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c2Url" value="${c2Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.babyOverriddenURL }"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c2Url" value="${c2Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC2" value= "${c2Category.caOverriddenURL }"/>
											</c:otherwise>
										</c:choose>
                                    <c:choose>
                                        <c:when test="${not empty c2Url}">
                                               
           											<c:set var="cat2Url"><c:if test="${!isOverriddenURLC2}">${contextPath}</c:if>${c2Url}</c:set>                                
                                            <c:set var="cat2Element"><a data-c1name="${c1Category.displayName}" data-c2name="${c2Category.displayName}"  href="${cat2Url}" aria-label="${c2Category.displayName}. ${navigateToPLP}">${c2Category.displayName}</a></c:set>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="cat2Element">
                                                <span class="textOnly">${c2Category.displayName}</span>
                                            </c:set>
                                        </c:otherwise>
                                    </c:choose>
                                    <tr class="checklist-items <c:if test="${c2Category.c3ManualComplete}">c3-active</c:if>">
                                       <td class="checklist-status showCheckListStatus">
                                       <c:set var="addedQuantityForPercentage" value="${c2Category.addedQuantity}"/>
                                       <c:if test="${c2Category.addedQuantity >c2Category.suggestedQuantity}">
                                        <c:set var="addedQuantityForPercentage" value="${c2Category.suggestedQuantity}"/>
                                       </c:if>
                                        <input aria-hidden='true' <c:if test="${c2Category.c3AutoComplete}">disabled</c:if> id="c3-checkbox-5" type="checkbox" name="c3-checkbox" class="status-input" data-c1name="${c1Category.categoryId}" data-c2name="${c2Category.categoryId}" 
                                        data-c3AddedQuantity="${addedQuantityForPercentage}" data-c3name="" data-addedQuantity="${c2Category.addedQuantity}" data-suggestedQuantity="${c1Category.totalSuggestedQuantityForPercentageCalc}" data-averagePercentage="${checklistVO.averageC1Percentage}" data-numberofc1="${noOfC1}"
                                        data-registryId="${registrySummaryVO.registryId}" data-registryType="${registrySummaryVO.registryType.registryTypeName}" <c:if test="${c2Category.c3ManualComplete || c2Category.c3AutoComplete}">checked="checked"</c:if>>
                                          <label for="c3-checkbox-5" class="status-label <c:if test="${c2Category.c3ManualComplete}">manualCheckedc3</c:if>" ><a <c:if test="${c2Category.c3AutoComplete}">aria-disabled="true"</c:if>role='checkbox' 
                                          aria-checked='<c:choose><c:when test="${c2Category.c3ManualComplete || c2Category.c3AutoComplete}">true</c:when><c:otherwise>false</c:otherwise></c:choose>' href="javascript:void(0);" aria-label="${c2Category.displayName} <c:choose><c:when test="${c2Category.c3ManualComplete}">${checkToRemove}</c:when><c:when test="${c2Category.c3AutoComplete}"></c:when><c:otherwise>${checkToAdd}</c:otherwise></c:choose>" class="<c:if test="${c2Category.c3AutoComplete}">autoCheckedc3</c:if> <c:if test="${c2Category.c3ManualComplete || c2Category.c3AutoComplete}">checklist-checked</c:if>" data-label-name="${c2Category.displayName}"></a></label>
                                       </td>
                                       <td class="checklist-name">${cat2Element}</td>
                                       <td class="checklist-quantity text-right"><a id="c3Count_${c2Category.categoryId}" href="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registrySummaryVO.registryId}&eventType=${registrySummaryVO.eventType}&c1name=${c1Category.displayName}&c2name=${c2Category.displayName}&c3name=${c3Category.displayName}&qty=${c2Category.addedQuantity} of ${c2Category.suggestedQuantity}&c1id=${c1Category.categoryId}&c2id=${c2Category.categoryId}&c3id=${c3Category.categoryId}#t=myItems" aria-label='${c2Category.addedQuantity} of ${c2Category.suggestedQuantity} <c:choose><c:when test="${c2Category.c3ManualComplete || c2Category.c3AutoComplete}"> ${itemsMarkedComplete} ${viewYourItems}</c:when><c:otherwise> ${itemsRecommendedToAdd} ${viewYourItems}</c:otherwise></c:choose>'>${c2Category.addedQuantity} of ${c2Category.suggestedQuantity}</a></td>
                                    </tr>
                                    </c:forEach>
                                    <c:choose>
            								<c:when test="${currentSiteId == 'BedBathUS'}">
													<dsp:getvalueof var="c1Url" value="${c1Category.uscategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC1" value= "${c1Category.usOverriddenURL }"/>
											</c:when>
											<c:when test="${currentSiteId == 'BuyBuyBaby'}">
													<dsp:getvalueof var="c1Url" value="${c1Category.babycategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC1" value= "${c1Category.babyOverriddenURL }"/>
											</c:when>
											<c:otherwise>
													<dsp:getvalueof var="c1Url" value="${c1Category.cacategoryURL}" />
													<dsp:getvalueof var="isOverriddenURLC1" value= "${c1Category.caOverriddenURL }"/>
											</c:otherwise>
										</c:choose>
                                     <tr>
    							<td colspan="3"><a data-c1name="${c1Category.displayName}" href="<c:if test="${!isOverriddenURLC1}">${contextPath}</c:if>${c1Url}" role="button" class="button-Med btnSecondary checklistShowAll" >
                   				<span> SHOP ALL ${c1Category.displayName} </span></a></td>
  								</tr> 
                                </tbody>
                             </table>
                         </div>
                    </section>
				</div>
            </c:forEach>
</c:otherwise>
</c:choose>
</section>
</dsp:page>
