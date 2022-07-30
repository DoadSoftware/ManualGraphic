function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function reloadPage(whichPage)
{
	switch(whichPage){
	/*case 'INITIALISE':
		//processUserSelection(document.getElementById('select_broadcaster'));
		//processManualProcedures('LOAD_MATCHES');
		break;*/
	case 'MANUAL':
		//processUserSelection(document.getElementById('select_broadcaster'));
		//processManualProcedures('LOAD_MATCHES');
		//document.initialise_form.submit();
		break;
	}
}
function initialisePage(whichPage)
{
	switch(whichPage){
	case 'INITIALISE':
		processUserSelection($('#select_sports'));
		break;
	
	}
}
function initialiseForm(whatToProcess, dataToProcess)
{
	/*switch (whatToProcess){
	case '':

		break;
	}*/
}
function processUserSelection(whichInput)
{	
	//alert($(whichInput).attr('id'));
	switch ($(whichInput).attr('id')) {
	case 'finish_btn':
		if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}
		
      	document.initialise_form.submit();
		break;
	case 'load_scene_btn':
		processManualProcedures('LOAD_SCENE');
		break;
	case 'get_container_btn':
		processManualProcedures('LOAD_DATA');
		break;
	case 'save_button':
		alert("File Saved \r\nFile Location = C:/Sports/Cricket/Manual/Data");
		$('#logging_stats_div').hide();
		uploadFormDataToSessionObjects('SAVE_DATA');
		
		break;
	case 'load_previous_data_btn':
		processManualProcedures('LOAD_PREVIOUS_SCENE');
		break;
	case 'animateout_graphic_btn':
		if(confirm('Are You Sure To Animate Out? ') == true){
			processManualProcedures('ANIMATE-OUT');	
		}
		break;
	case 'clear_all_btn':
		if(confirm('Are You Sure To Clear All Scenes? ') == true){
			$('#logging_stats_div').hide();
			processManualProcedures('CLEAR-ALL');	
		}
		break;
	case 'select_sports':
		//alert($('#select_sports :selected').val());
		switch ($('#select_sports :selected').val()) {
			
			
		/*case 'BADMINTON':
			$('#vizPortNumber').attr('value','1980');
			//processManualProcedures('BADMINTON-OPTIONS')
			break;*/
		/*case 'CRICKET':
			processManualProcedures('CRICKET-OPTIONS')
			break;*/
		}
		break;
	}
}	
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){ 
			formData.append($(this).attr('id'),document.getElementById($(this).attr('id')).value);
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'SAVE_DATA':
		url_path = 'save_data';
		formData.append('file_name',document.getElementById('file_name').value);
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(response) {

        	switch(whatToProcess.toUpperCase()) {
        	case 'SAVE_DATA':
        	 	window.location.reload();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processManualProcedures(whatToProcess)
{
	var valueToProcess;
	switch(whatToProcess) {
	case 'LOAD_SCENE': case 'LOAD_PREVIOUS_SCENE': case 'LOAD_DATA': case 'BADMINTON-OPTIONS': case 'CRICKET-OPTIONS':
    	switch(whatToProcess) {
		case 'LOAD_SCENE':
			valueToProcess = $('#selectedScene option:selected').val();
			//alert("SCENE LOAD");
			break;
		case 'LOAD_PREVIOUS_SCENE':
			valueToProcess =$('#previous_xml_data option:selected').val();
			//alert(valueToProcess);
			break;
		case 'LOAD_DATA':
			valueToProcess = $('#selectedScene option:selected').val();
			break;
		case 'BADMINTON-OPTIONS':
			valueToProcess = $('#select_sports option:selected').val();
			break;
		case 'CRICKET-OPTIONS':
			valueToProcess = $('#select_sports option:selected').val();
			break;
    	}
		break;
	}
	//alert(whatToProcess);
	$.ajax({    
        type : 'Get',     
        url : 'processManualProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + valueToProcess, 
        dataType : 'json',
        success : function(data) {
			
			switch(whatToProcess) {
			case 'LOAD_DATA':
				addItemsToList('LOAD_DATA-OPTIONS',data);
				break;
			case 'LOAD_PREVIOUS_SCENE':
			if(confirm('Animate In?') == true){
				processManualProcedures('ANIMATE-IN');
				addItemsToList('LOAD_PREVIOUS_SCENE-OPTIONS',data);
			}else{
				document.initialise_form.submit();
			}
			
			break;
			}
			processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess){

	var option,i,div;
	
	switch (whatToProcess) {
	
	case 'LOAD_DATA-OPTIONS':
		if(dataToProcess) {
			
			$('#logging_stats_div').empty();

			div = document.createElement('div');
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			
			tbody = document.createElement('tbody');

			for(var i = 0; i < dataToProcess.length ; i++) {
				row = tbody.insertRow(tbody.rows.length);
				
					if(i == 0) {
						document.getElementById('scene_path').value = dataToProcess[i];
					} else if(i == 1) {
					}else {
						if(dataToProcess[i].split(':')[1] == 'TAG_IMAGE1'){
							select = document.createElement('input')
							select.type = 'text';
							select.id = (i - 1) + '_' + dataToProcess[i].split(':')[0];
							select.value = 'D:/DOAD_In_House_Everest/Everest_Cricket/EVEREST_APL2022/Logos/Delhi.png'
							label = document.createElement('label');
							label.type = 'label';
							label.innerHTML = dataToProcess[i].split(':')[0];
							label.for = select.id;
							div.appendChild(label).appendChild(select);
							//row.insertCell(j);
							row.insertCell(0).appendChild(label).appendChild(select);
							}
							
						else{
							select = document.createElement('input')
							select.type = 'text';
							select.id = (i - 1) + '_' + dataToProcess[i].split(':')[0];
							label = document.createElement('label');
							label.type = 'label';
							label.innerHTML = dataToProcess[i].split(':')[0];
							label.for = select.id;
							div.appendChild(label).appendChild(select);
							row.insertCell(0).appendChild(label).appendChild(select);
						}
				}
			}
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('input')
			select.type = 'text';
			select.id = 'file_name';
			select.value = (document.getElementById('scene_path').value).split('/')[
				(document.getElementById('scene_path').value).split('/').length - 1].replace('.sum','');
			label = document.createElement('label');
			label.type = 'label';
			label.innerHTML = 'Save XML File As';
			label.for = select.id;
			row.insertCell(0).appendChild(label).appendChild(select);
			
			select = document.createElement('button');
			select.innerHTML = 'Save XML';
			select.id = 'save_button';
			select.setAttribute('onclick','processUserSelection(this);');

			row.insertCell(0).appendChild(select);
			
			table.appendChild(tbody);
			
			document.getElementById('logging_stats_div').appendChild(table);
			document.getElementById('logging_stats_div').style.display = '';
		}
		break;
		
		case 'LOAD_PREVIOUS_SCENE-OPTIONS':
		
			if(dataToProcess) {
			$('#logging_stats_div').empty();

			div = document.createElement('div');
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			
			tbody = document.createElement('tbody');
			
			dataToProcess.containers.forEach(function(cont,index,array){
				row = tbody.insertRow(tbody.rows.length);
				
				select = document.createElement('input');
				select.type = 'text';
				select.id = cont.container_key;
				select.value = cont.container_value;
				//row.insertCell(0).appendChild(select);
				
				label = document.createElement('label');
				label.type = 'label';
				label.innerHTML = cont.container_key;
				label.for = select.id;
				row.insertCell(0).appendChild(label).appendChild(select);
			});
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('input')
			select.type = 'text';
			select.id = 'file_name';
			select.value = (document.getElementById('scene_path').value).split('/')[
				(document.getElementById('scene_path').value).split('/').length - 1].replace('.sum','');
			label = document.createElement('label');
			label.type = 'label';
			label.innerHTML = 'Save XML File As';
			label.for = select.id;
			row.insertCell(0).appendChild(label).appendChild(select);
			
			select = document.createElement('button');
			select.innerHTML = 'Save As';
			select.id = 'save_button';
			select.setAttribute('onclick','processUserSelection(this);');

			row.insertCell(0).appendChild(select);
			table.appendChild(tbody);
			
			document.getElementById('logging_stats_div').appendChild(table);
			document.getElementById('logging_stats_div').style.display = '';
		}
			
			break;
		
	}
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}