<html>
	<link rel="stylesheet" type="text/css" href="style.css">
	<head>
	<script src="jquery-1.11.3.min.js"></script>
		<title>Image Location Toolkit - Home</title>
		<link rel="icon"
              type="image/png"
              href="camera.png">
	</head>

	<body>
		<ul class = "nav">
            <li><a href="/image-location-toolkit">Home</a></li>
            <li><a href="/image-location-toolkit/search.jsp">Search</a></li>
            <li><a href="/image-location-toolkit/explore.jsp">Explore</a></li>
        </ul>

        <img id="bgImage">
        <img id= "Logo">

		<!-- Form is used for either input choice (input by URL or input by choosing a file) -->
		<div id="formDiv" class="1">
			<form id="urlForm">
            		<input type="text" name="url" id="txt"/>
            		<input type="submit" class="button" value="Upload URL">
            </form>

			<br> <br>

			<form id="localFileForm">
				<input type="file" name="file" id="fileChooser" multiple accept="image/*"/>
				<input type="submit" class="button" value="Upload File(s)">
			</form>
			<div id="loadingDiv" z-index:100; position:relative; left:42%; style="display:none;">
				<table id="loadingTable">
					<tr>
						<td height=40px;><a id="l1" style="color: white;text-shadow: 2px 2px black;font-weight: bold;">Extracting Metadata...</a></td>
						<td height=40px;><img id="l1image" style="max-height:40px;"></td>
					</tr>
					<tr>
						<td height=40px;><a id="l2" style="color: white;text-shadow: 2px 2px black;font-weight: bold;">Reverse Geocoding...</a></td>
						<td height=40px;><img id="l2image" style="max-height:40px;"></td>
					</tr>
					<tr>
						<td height=40px;><a id="l3" style="color: white;text-shadow: 2px 2px black;font-weight: bold;">Performing Optical Character Recognition...</a></td>
						<td height=40px;><img id="l3image" style="max-height:40px;"></td>
					</tr>
					<tr>
						<td height=40px;><a id="l4" style="color: white;text-shadow: 2px 2px black;font-weight: bold;">Searching for Tags...</a></td>
						<td height=40px;><img id="l4image" style="max-height:40px;"></td>
					</tr>
				</table>
			</div>
		</div>

	<object width="1000px" height="800px" id="heli" style="display:none; position: absolute; left: 50%;	top: 50%; overflow:hidden; transform: translate(-50%, -50%);">
		<param name="movie" value="http://www.santa.net/free-santa-claus-games/arcade/460.swf">
		<embed src="http://www.santa.net/free-santa-claus-games/arcade/460.swf" width="100%" height="50%">
		</embed>
	</object>
	</body>

	<script>
		var count;
		var isMultiple;
		var ImageIDs = new Array();
		var nonUploadedImages = new Array();

		// Sets the background image
		document.getElementById("bgImage").src = "bg_images/bg" + Math.floor((Math.random() * 5) + 1) + ".jpg";

		// Sets the title logo
		document.getElementById("Logo").src = "Title.png";

		// Adds on submit listeners for the buttons
		$('#localFileForm').submit(function () {
				if (document.getElementById("fileChooser").value != "" && document.getElementById("fileChooser").value != null) {
	        	    uploadFiles();
				}
				return false;
            })

        $('#urlForm').submit(function () {
        	var url = document.getElementById("txt").value;
        		if (url != "") {
					if (url == "helicopter game") {
						document.getElementById("heli").style.display = "block";
						var i = 0;
						while (i < document.getElementsByClassName("1").length) {
							document.getElementsByClassName("1")[i].style.display = "none";
							i++;
						}
						return false;
					}
					if (url == "credits") {
						var header = document.createElement("H1");
						header.innerHTML = "<marquee>Creators:&emsp;Jonathan Downs&emsp;" +
												"Ryan Fischbach&emsp;Brian Garvey&emsp;Alexander Jack&emsp;Ben Summers </marquee>";
						document.body.appendChild(header);
						url = "";
						return false;
               		}
					imageExists(url, function(exists){
						if(!exists){
							alert("Error: Invalid URL");
						}else{
							document.getElementById("loadingDiv").style.display = "block";
                        	document.getElementById("l1image").src = "http://i.stack.imgur.com/qtj7C.gif";
                        	waitForMetadata();
							uploadURL();
						}
					});
        		}
        		return false;
            })

		// Checks if URL contains valid image
        function imageExists(url, callback) {
                          var img = new Image();
                          img.onload = function() { callback(true); };
                          img.onerror = function() { callback(false); };
                          img.src = url;
        }

		// Checks the file size before allowing the image to submit
		function checkFileSize(i, mult) {
			var file = document.getElementById("fileChooser").files[i];
			var MAX_SIZE = 10; // In megabytes

			if (file === undefined || file === null) { return false; }
			var size = file.size / 1024; // The size of the image in kilobytes
			if (size > MAX_SIZE * 1024) // If the size is more than 10MB, don't allow the image to submit
			{
				var megabyteSize = size / 1024; // Convert from kilobytes to megabytes
				// if single file then do alert and reset fileForm, if not then just console.log and keep going
				if(!mult){
					alert("Size is too large! Image is " + (Math.round( megabyteSize * 100 ) / 100) + " MB but must be under 10 MB.");
					document.getElementById("loadingDiv").style.display = "none";
					document.getElementById("localFileForm").reset();
				}
				return false;
			}
			return true;
		}

		// Uploads local files
        function uploadFiles(){
        	document.getElementById("loadingDiv").style.display = "block";
			document.getElementById("l1image").src = "http://i.stack.imgur.com/qtj7C.gif";
        	waitForMetadata();
        	count = 0;
        	ImageIDs = new Array();
        	var size = 0;
        	var LargeImageCounter = 0;
			var fileInput = document.getElementById("fileChooser");
			if (fileInput.files.length > 1) {
				isMultiple = true;
				for (var i = 0, len = fileInput.files.length; i < len; i++) {
					var file = fileInput.files.item(i);
					// find real size of file list that arent too large
					if(checkFileSize(i, isMultiple)){
						size++;
					}
				}
				for (var i = 0, len = fileInput.files.length; i < len; i++) {
                	var file = fileInput.files.item(i);
                	if(!checkFileSize(i, isMultiple)){
                		nonUploadedImages[LargeImageCounter++] = file.name;
                	}else{
                	    uploadFileMultiple(file, size)
                	}
                }
			// Only one file being uploaded
			}
			else {
				isMultiple = false;
				var file = fileInput.files.item(0);
				uploadFileSingle(file);
			}
        }

		// Rest calls for uploading Images
		function uploadFileMultiple(file, size){
        	var xml = new XMLHttpRequest();
        	var formData = new FormData();
        	formData.append("file", file);
        	xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					ImageIDs[count] = xml.responseText;
					count++;
					if(count === size){
						window.localStorage.setItem("NonUploadedImageNames", JSON.stringify(nonUploadedImages));
						window.localStorage.setItem("ImageIdArray", JSON.stringify(ImageIDs));
						window.location.href = "/image-location-toolkit/gallery.jsp";
					}
				}
        	}
        	xml.open("POST","/image-location-toolkit/rest/files/uploadfolder", true);
        	xml.send(formData);
        }

        function uploadFileSingle(file){
        	if (!checkFileSize(0, isMultiple)) { return false; }
            var xml = new XMLHttpRequest();
            var formData = new FormData();
            formData.append("file", file);
            xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					window.location.href = "/image-location-toolkit/view_image.jsp?id=" + xml.responseText;
				}
			}
			xml.open("POST","/image-location-toolkit/rest/files/upload", true);
			xml.send(formData);
        }

        function uploadURL(){
        var xml = new XMLHttpRequest();
        var url = document.getElementById("txt").value;
             xml.onreadystatechange = function () {
        			if (xml.readyState == 4 && xml.status == 200) {
        				window.location.href = "/image-location-toolkit/view_image.jsp?id=" + xml.responseText;
        			}else if(xml.readyState == 4){
        			document.getElementById("loadingDiv").style.display = "none";
        			document.getElementById("urlForm").reset();
                    alert("URL Forbidden Error");
                    }
        		}
        	xml.open("POST","/image-location-toolkit/rest/files/url", true);
        	xml.send(url);
        }

		function waitForMetadata() {
			var xml = new XMLHttpRequest();
			xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					document.getElementById("l1image").src =
						"https://upload.wikimedia.org/wikipedia/commons/b/bd/Checkmark_green.svg";
					document.getElementById("l2image").src = "http://i.stack.imgur.com/qtj7C.gif";
					waitForReverseGeocoding();
				}
			}
			xml.open("GET", "/image-location-toolkit/rest/files/loading/metadata", true);
			xml.send();
		}

		function waitForReverseGeocoding() {
			var xml = new XMLHttpRequest();
			xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					document.getElementById("l2image").src =
						"https://upload.wikimedia.org/wikipedia/commons/b/bd/Checkmark_green.svg";
					document.getElementById("l3image").src = "http://i.stack.imgur.com/qtj7C.gif";
					waitForOCR();
				}
			}
			xml.open("GET", "/image-location-toolkit/rest/files/loading/reverse_geo", true);
			xml.send();
		}

		function waitForOCR() {
			var xml = new XMLHttpRequest();
			xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					document.getElementById("l3image").src =
						"https://upload.wikimedia.org/wikipedia/commons/b/bd/Checkmark_green.svg";
					document.getElementById("l4image").src = "http://i.stack.imgur.com/qtj7C.gif";
				}
			}
			xml.open("GET", "/image-location-toolkit/rest/files/loading/ocr", true);
			xml.send();
		}
	</script>
</html>
<!-- Currently, this code is only guaranteed to work in the newer versions of Chrome. -->