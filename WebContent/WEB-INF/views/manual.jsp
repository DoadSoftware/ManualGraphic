<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>

  <meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1">
  <title>Manual</title>
	
  <script type="text/javascript" src="<c:url value="/webjars/jquery/3.6.0/jquery.min.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/webjars/bootstrap/5.1.3/js/bootstrap.min.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/resources/javascript/index.js"/>"></script>
  
  <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/5.1.3/css/bootstrap.min.css"/>"/>  
  <link href="<c:url value="/webjars/font-awesome/6.0.0/css/all.css"/>" rel="stylesheet">
  
  <!--<script type="text/javascript">
	setInterval(() => {
		processManualProcedures('READ-MATCH-AND-POPULATE');
	}, 1000);
</script>  -->
</head>
<body onload="reloadPage('MANUAL')">
<div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">
  <div class="container">
	<div class="row">
	 <div class="col-md-8 offset-md-2">
       <span class="anchor"></span>
         <div class="card card-outline-secondary">
           <div class="card-header">
             <h3 class="mb-0">Manual</h3>   
           </div>
           <div class="card-body">
           	<div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="select_cricket_scenes" class="col-sm-4 col-form-label text-left">Select Scenes </label>
			    <div class="col-sm-6 col-md-6">
			      <select id="selectedScene" name="selectedScene" 
			      		class="browser-default custom-select custom-select-sm">
						<c:forEach items = "${session_viz_scenes}" var = "scenes">
				          	<option value="${scenes.name}">${scenes.name}</option>
						</c:forEach>
			      </select>
			    </div>
			  </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="previous_xml_data" class="col-sm-4 col-form-label text-left">Previous XML Data </label>
			    <div class="col-sm-6 col-md-6">
			      <select id="previous_xml_data" name="previous_xml_data" 
			      		class="browser-default custom-select custom-select-sm">
						<c:forEach items = "${scene_files}" var = "files">
				          	<option value="${files.name}">${files.name}</option>
						</c:forEach>
			      </select>
			    </div>
			  </div>
			  <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
		  		name="load_scene_btn" id="load_scene_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-film"></i> Load Scene</button>
		  	  <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
		  		name="get_container_btn" id="get_container_btn"   onclick="processUserSelection(this)" >
		  		<i class="fas fa-film"></i> Get Container </button>
		  	  <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
		  		name="load_previous_data_btn" id="load_previous_data_btn"   onclick="processUserSelection(this)" >
		  		<i class="fas fa-film"></i> Load Previous XML Data </button>
		  	  <button style="background-color:#f44336;color:#FEFEFE;;" class="btn btn-sm" type="button"
			  		name="animateout_graphic_btn" id="animateout_graphic_btn" onclick="processUserSelection(this)"> AnimateOut </button>
			  <button style="background-color:#f44336;color:#FEFEFE;;" class="btn btn-sm" type="button"
			  		name="clear_all_btn" id="clear_all_btn" onclick="processUserSelection(this)"> Clear All </button>
		  	  
			  <div id="logging_stats_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			  
			  </div>
	       </div>
	    </div>
       </div>
    </div>
  </div>
</div>
<input type="hidden" name="select_sports" id="select_sports" value="${session_selected_sports}"/>
<input type="hidden" id="manual_file_timestamp" name="manual_file_timestamp" value="${session_Data.manual_file_timestamp}"></input>
<input type="hidden" name="scene_path" id="scene_path" value = ""></input>
</body>
</html>