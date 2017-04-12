<c:choose>
	<c:when test="${param.carouselType == 'custom'}">
		<div class="carousel">
			
			<div class="carouselBody">
				
				<c:if test="${param.carouselArrow != 'custom'}">
					<div class="carouselArrow carouselArrowPrevious">
						<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
						<a href="#" title="Previous" class="carouselScrollPrevious"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</c:if>
				
				<div class="carouselContent container_12">
					<div class="${param.carouselCustomClass}">
						<c:choose>
							<c:when test="${param.carouselContentURL != ''}">
								<c:import url="${param.carouselContentURL}" />
							</c:when>
							<c:otherwise>
								<c:import url="/_includes/modules/carousel/carousel_inner.jsp" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				
				<c:if test="${param.carouselArrow != 'custom'}">
					<div class="carouselArrow carouselArrowNext">
						<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
						<a href="#" title="Next" class="carouselScrollNext"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</c:if>
			</div>

			<c:if test="${param.showCarouselFooter != 'N'}">
				<div class="carouselPages">
					<c:if test="${param.carouselArrow == 'custom'}">
						<div class="carouselArrowContainerCenterAlignFix">
							<div class="carouselArrowContainer">
								<div class="carouselArrow carouselArrowPrevious">
									<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
									<a href="#" title="Previous" class="carouselScrollPrevious"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
								</div>
					</c:if>
					
					<span class="carouselPageLinks">
						<a href="#" class="selected" title="Page 1">1</a>
						<a href="#" title="Page 2">2</a>
						<a href="#" title="Page 3">3</a>
						<a href="#" title="Page 4">4</a>
					</span>
					
					<c:if test="${param.carouselArrow == 'custom'}">
								<div class="carouselArrow carouselArrowNext">
									<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
									<a href="#" title="Next" class="carouselScrollNext"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>
		</div>
	</c:when>
	<c:otherwise>
		<div class="carousel grid_12 clearfix">
			
			<div class="carouselBody grid_12 clearfix">
				
				<div class="grid_1 carouselArrow carouselArrowPrevious clearfix">
					<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
					<a href="#" title="Previous" class="carouselScrollPrevious"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
				</div>
				
				<div class="carouselContent grid_10 clearfix">
					<ul class="prodListRow">
						<c:choose>
							<c:when test="${param.carouselContentURL != ''}">
								<c:import url="${param.carouselContentURL}" />
							</c:when>
							<c:otherwise>
								<c:import url="/_includes/modules/carousel/carousel_inner.jsp" />
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
				
				<div class="grid_1 carouselArrow carouselArrowNext clearfix">
					<%-- this hardcoded space below is a fix to prevent the carousel container from floating all the way to the left when there are less than 5 items --%>&nbsp;
					<a href="#" title="Next" class="carouselScrollNext"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
				</div>
			</div>

			<c:if test="${param.showCarouselFooter != 'N'}">
				<div class="carouselPages grid_12 clearfix">
					<span class="carouselPageLinks">
						<a href="#" class="selected" title="Page 1">1</a>
						<a href="#" title="Page 2">2</a>
						<a href="#" title="Page 3">3</a>
						<a href="#" title="Page 4">4</a>
					</span>
				</div>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>
