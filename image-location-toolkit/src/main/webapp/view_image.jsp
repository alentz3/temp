<html>
<link rel="stylesheet" type="text/css" href="view_image_style.css">
<link rel="icon"type="image/png"href="camera.png">
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<meta charset="utf-8">
<title>ILT - Results</title>

<body onload="getMetadata('/image-location-toolkit/rest/files/images/' + id + '/metadata')">

	<ul class = "nav">
        <li><a href="/image-location-toolkit">Home</a></li>
        <li><a href="/image-location-toolkit/search.jsp">Search</a></li>
        <li><a id="exploreLink" href="/image-location-toolkit/explore.jsp">Explore</a></li>
    </ul>

<div id="wrapper">
	<div id="imagedata-mapcanvas-wrapper">
		<div id="imagedata">
			<h1>Image Metadata</h1>
			<table id="img-data-table">
				<tr>
					<td>Date & Time :</td>
					<td id="data-dateTime"></td>
				</tr>
				<tr>
					<td>Location :</td>
					<td id="data-location"></td>
				</tr>
				<tr>
					<td>Longitude & Latitude :</td>
					<td id="data-coords"></td>
				</tr>
				<tr>
					<td>Extracted Text :</td>

					<td id="data-text"></td>
				</tr>
				<tr>
					<td>Top Tags :</td>
					<td id="data-tags"></td>
				</tr>
				<tr>
				<td></td>
				<td id = "buttonage"></td>
				</tr>
			</table>
		</div>
		<div id="map-canvas">
		</div>
	</div>
	<div id="thumbnail-wrapper">
		<div id="thumbnail-text">
			Top similar images:
		</div>
		<div id="thumbnails">
		</div>
	</div>
</div>
</body>
</html>

<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true"></script>
<script>
	var geocoder;
	var map;
	var lat = 0;
	var lng = 0;
	var id = getParameterByName("id");
	var infowindow = new google.maps.InfoWindow();
	var marker;
	var address;

	// Adds Search By Image button to the results page
	function createSearchByImageButton() {
		var searchLink = document.createElement("button");
		searchLink.innerHTML = "Search By Image";
		searchLink.id = "searchByImageBtn";
		searchLink.onclick = function()
			{ window.location = "/image-location-toolkit/search_by_image.jsp?id=" + id; }
		var container = document.getElementById("imagedata");
        container.insertBefore(searchLink, container.firstChild);
	}

	// Initializes the map on the results page
	function initialize() {
		geocoder = new google.maps.Geocoder();
		var latlng = new google.maps.LatLng(lat, lng);
		var mapOptions = {
			zoom: 6,
			center: latlng,
			mapTypeControl: true,
			mapTypeId: 'roadmap'
		}
		map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
		google.maps.event.addListenerOnce(map, 'idle', function(){
			if (document.getElementById("map-canvas").children.length > 0) {
				if (map) { google.maps.event.trigger(map, 'resize'); }
			}
			map.setCenter(new google.maps.LatLng(lat, lng));
		});
		addControls();
		geocodeWithMarker();
	}

	// Adds buttons to the map
	function addControls() {
		// Geo-tag button
		var mapDiv = document.createElement("div");
		mapDiv.style.marginTop = "5px";
		mapDiv.style.marginLeft = "5px";
		var geoBtn = document.createElement("button");
		var checkEle = document.getElementById("gbtn");
		if(checkEle === null) {
		geoBtn.disabled = true;
		}
		geoBtn.id = "geoBtn";
		geoBtn.innerHTML = "Geotag!";
		google.maps.event.addDomListener(geoBtn, 'click', function() {
        	geotagImage();
        });
		mapDiv.appendChild(geoBtn);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(mapDiv);
	}

	// Adds listener to map, which changes marker position on user click
	function geocodeWithMarker() {
		google.maps.event.addListener(map, "click", function (event) {
			lat = event.latLng.lat();
			lng = event.latLng.lng();
			updateExploreLink();
			if (marker) { marker.setMap(null); }
			marker = new google.maps.Marker({
				position: new google.maps.LatLng(lat, lng),
				map: map
			});
			document.getElementById("geoBtn").disabled = false;
		});
	}

	// Gets image tags of the main image on the results page
	function getTags() {
			var tagUrl = "/image-location-toolkit/rest/files/images/" + id + "/tags";
			var xml = new XMLHttpRequest();
			xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					document.getElementById("data-tags").innerHTML = xml.responseText;
					getSimilarImages(xml.responseText)
				}
			}
			xml.open("GET",tagUrl, true);
			xml.send();
    	}

	// Gets images that are similar (by tags) to the main image on the results page
    function getSimilarImages(tags){
            var xml = new XMLHttpRequest();
            xml.onreadystatechange = function () {
            	if (xml.readyState == 4 && xml.status == 200) {
            		DisplaySimilarImages(xml.responseText);
            	}
            }
            xml.open("POST","/image-location-toolkit/rest/files/images/searchtags", true);
            xml.send(tags);
    }

	// Displays similar images on results page
	function DisplaySimilarImages(ID_string){
		 var IDarr = ID_string.split(",");
		 for (var x = 0; x < IDarr.length; x++) {
		 	if (IDarr[x] != id) {
				DisplayOneSimilarImage("/image-location-toolkit/rest/files/images/" + IDarr[x],x,IDarr[x]);
			}
         }
	}

	// Displays each similar image along bottom edge of results page
	function DisplayOneSimilarImage(url, imgNum, imageID){
	 		var xml = new XMLHttpRequest();
			xml.onreadystatechange = function () {
				if (xml.readyState == 4 && xml.status == 200) {
					var container = document.getElementById("canvasContainer");
					var img = document.createElement("img");
					var url = window.URL || window.webkitURL;
					img.src = url.createObjectURL(this.response);
					img.addEventListener("click", function () {
						window.location.href = "/image-location-toolkit/view_image.jsp?id=" + imageID;
					})
					var frag = document.createDocumentFragment();
					var div = document.createElement("DIV")
					div.appendChild(img);
					frag.appendChild(div);
					document.getElementById("thumbnails").appendChild(frag);
				}
			}
			 xml.open("GET", url, true);
             xml.responseType = "blob";
             xml.send();
      }

    // Prepares the map on the results page
	function codeLatLng() {
		if(!!lat) {
			var mapblock = document.getElementById("map-canvas");
			mapblock.style.display = "flex";
            initialize();
            var latlng = new google.maps.LatLng(lat, lng);
            geocoder.geocode({'latLng': latlng}, function(results, status) {
            	if (status == google.maps.GeocoderStatus.OK) {
           			map.setZoom(6);
           			marker = new google.maps.Marker({
           				position: latlng,
           				map: map
           			});
           			if (address != null && address != "") {
           				infowindow.setContent('<div id="infoWin">'+ address +'</div>');
           				infowindow.open(map, marker);
           			}
            	}
            });
          }
		  else {
			  var gbtn = document.createElement("BUTTON");
			  gbtn.innerHTML = "Click to add geotag!";
				gbtn.onclick = function() {
					lat = 1;
					lng = 1;
					codeLatLng();
				}
				gbtn.id = "gbtn";
			  document.getElementById("buttonage").appendChild(gbtn);
		  }
	}

	// Sets the submitted image to the results page
	function setImage(url)
	{
		var xml = new XMLHttpRequest();
		xml.onreadystatechange = function () {
			if (xml.readyState == 4 && xml.status == 200) {
				var img = document.createElement("img");
				var url = window.URL || window.webkitURL;
				img.src = url.createObjectURL(this.response);
				var container = document.getElementById("imagedata");
				container.insertBefore(img, container.firstChild);
				var millisecondsToWait = 250;
				setTimeout(function() {
					codeLatLng();
				}, millisecondsToWait);
			}
		}
		xml.open("GET", url, true);
		xml.responseType = "blob";
		xml.send();
		createSearchByImageButton();
	}

	// Grabs the image metadata from the backend
	function getMetadata(url) {
		var xml = new XMLHttpRequest();
		xml.onreadystatechange = function () {
			if (xml.readyState == 4 && xml.status == 200) {
				parseData(xml.responseText);
				setImage("/image-location-toolkit/rest/files/images/" + id);
				getTags();
			}
		}
		xml.open("GET", url, true);
		xml.send();
	}

	// Parses the JSON object containing the image metadata
	function parseData(xmlResponse)
	{
		var data = JSON.parse(xmlResponse);
		arr = []
		for(var event in data){
			var dataCopy = data[event]
			for(key in dataCopy){
				if(key == "start" || key == "end"){
					dataCopy[key] = new Date(dataCopy[key])
				}
			}
			arr.push(dataCopy)
		}
		lat = arr[5];
		lng = arr[4];
		address = arr[6];
		if (lat != null && lng != null) { updateExploreLink(); }
		if (arr[3] == null) {
			document.getElementById("data-dateTime").innerHTML = "Unknown";
		}
		else { document.getElementById("data-dateTime").innerHTML = arr[3]; }
		if (arr[6] == null) {
        		document.getElementById("data-location").innerHTML = "Unknown";
        }
        else { document.getElementById("data-location").innerHTML = arr[6]; }
        if (arr[5] == null || arr[4] == null) {
        		document.getElementById("data-coords").innerHTML = "Unknown";
        }
        else { document.getElementById("data-coords").innerHTML = arr[5] + ", " + arr[4]; }
        if (arr[7] == "" || arr[7] == null) {
        		document.getElementById("data-text").innerHTML = "No text found.";
       	}
       	else { document.getElementById("data-text").innerHTML = arr[7]; }
	}

	// Retrieves the encrypted image id from the query string
	// Function taken from http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript
	function getParameterByName(name) {
		name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
		var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
				results = regex.exec(location.search);
		return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}

	// Geo-tags the image with user-specified coordinates
	function geotagImage() {
		var xml = new XMLHttpRequest();
		xml.onreadystatechange = function () {
			updateMetadata();
		}
		xml.open("POST", "/image-location-toolkit/rest/files/images/"
			+ id + "/edit_coord", true);
		xml.send(lat + "," + lng); // Sends the coordinates to the backend
	}

	// Grabs the updated location data and prints it to the screen
	function updateMetadata() {
		var xml = new XMLHttpRequest();
		xml.onreadystatechange = function () {
			if (xml.readyState == 4 && xml.status == 200) {
				parseData(xml.responseText);
			}
		}
		xml.open("GET", "/image-location-toolkit/rest/files/images/"
			+ id + "/metadata", true);
		xml.send();
	}

	// Updates the link to the explore page, so that the map is generated correctly
	function updateExploreLink() {
		document.getElementById("exploreLink").href = "/image-location-toolkit/explore.jsp?lat="
			+ lat + "&lng=" + lng;
	}
</script>