<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry" />
<dsp:getvalueof bean="SessionBean" var="bean"/>
                    <c:if test="${not empty sessionRegistry}">
                        <c:set var="regType" value="${sessionRegistry.registryType.registryTypeDesc}" />
                      
                        
                    </c:if>
                    
                    
                    
<section id="C1-Slider" class="grid_12 clearfix interactive-flyout">
<div class="overall-progress-bar ib fl">
<div class="left-container">
    <a href="#">Full View</a>
    <dsp:droplet name="CheckListDroplet">
			<dsp:param name="registryType" value="${regType}" />	
			<dsp:param name="sessionBean" value="${bean}" />	
			<dsp:param name="staticChecklist" value="true" />
							
				<dsp:oparam name="output">
						<dsp:getvalueof var="isDisabled" param="isDisabled" />
						<dsp:getvalueof var="checkListVO" param="checkListVO" />
						
    <div  class="registry-checklist-name">${checkListVO.displayName}</div>
  <div class="complete-status"><span>25%</span><span>&nbsp;Complete</span></div> 
    <div class="progress-bar" aria-hidden="true">
        <div class="progressWrapper span12">
            <div class="inner-progress-bar" data-progress-complete="50" style="width: 50%;"> 
            </div>
        </div>
    </div>
    </dsp:oparam>
    </dsp:droplet>
</div>
</div>
<div class="separator-bar fl ib"></div>
<div class="category-one-list ib">
    <div class="back-icon ib"></div>
    <div class="C1-Scrollable-list ib">
        <ul class="C1-list-items">
         <dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param value="${checkListVO.categoryListVO}" name="array" />
	<dsp:param name="elementName" value="category"/>
	<dsp:oparam name="output">
	<li>
	<dsp:getvalueof param="category.displayName" var = "categoryDisplayname"/>
              <div aria-hidden="true" class="category-img ${categoryDisplayname }"></div>
	<div class="C1-name cb">
	
	${categoryDisplayname}
	</div>
	 <div class="progress-bar" aria-hidden="true">
                    <div class="progressWrapper span12">
                    <div class="inner-progress-bar" data-progress-complete="50" style="width: 50%;"> 
            </div>
        </div>
    </div>
          </li>
        
     </dsp:oparam>
          </dsp:droplet>
             </ul>
        
    </div>
    
    
    <div class="forward-icon ib"></div>
</div>
<div class="c2-category-list clearfix">
        <div class="active-category-arrow"></div>
        <ul>
        
           
       <dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param value="${checkListVO.categoryListVO}" name="array" />
	<dsp:param name="elementName" value="category"/>
	<dsp:oparam name="output">
	<dsp:getvalueof param="category.childCategoryVO" var="c2childcategories"/>
			<c:forEach var="c2childcategory" items="${c2childcategories}">
            <li class="c2-category checklist-complete">
                <a href="javascript:void(0);" class="c2-category-link" data-menu-content="#dinnerware-categories-container"> 
                 ${c2childcategory.value.displayName}</a>
				
				<dsp:getvalueof value="${c2childcategory.value}" var="c2Category"/>
					
				<div id="dinnerware-categories-container" class="hidden"><section class="c3-categories-container scrollable vert">
				
				<div class="viewport clearfix registry-checklist-container">
				
				<table class="overview registry-checklist-items">
				<thead>
                                    <tr class="checklist-header">
                                       <th class="checklist-status"></th>
                                       <th class="checklist-name">Browse</th>
                                       <th class="checklist-quantity">Your Items</th>
                                    </tr>
                                </thead>
								<tbody>
								
				
		 <c:forEach var="c3CategoryMap" items="${c2Category.childCategoryVO}">
        <c:set var="c3Category" value="${c3CategoryMap.value}"/>
							
							
						<tr class="checklist-items">
								  <td class="checklist-status">
                                          <input id="c3-checkbox-1" type="checkbox" name="c3-checkbox" class="status-input">
                                          <label for="c3-checkbox-1" class="status-label"><a href="#"></a></label>
                                       </td>
                                       <td class="checklist-name"><a href="#">${c3Category.displayName}
									   </a></td>
                                  </tr>	

	</c:forEach>
							
								</tbody>
				</table>
				</div>
				</section>
				</div>
				 
				 
				
				 
            </li>
            </c:forEach>
              
     </dsp:oparam>
          </dsp:droplet>
            

</ul>

    </div>
    
</section>

</dsp:page>