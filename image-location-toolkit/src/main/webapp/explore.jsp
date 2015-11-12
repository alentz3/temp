<html>
<link rel="stylesheet" type="text/css" href="explore.css">
<link rel="icon"type="image/png"href="camera.png">
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<meta charset="utf-8">
<title>ILT - Explore</title>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true"></script>
<script>
	var map;
	var lat = 0;
	if (getParameterByName("lat") != null) { lat = getParameterByName("lat"); }
	var lng = 0;
	if (getParameterByName("lng") != null) { lng = getParameterByName("lng"); }
	var marker;
	var geoCircle;
	var coordinateList;
	var markerList = [];

	function initialize() {
		var latlng = new google.maps.LatLng(lat, lng);
		var mapOptions = {
			zoom: 3,
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
		moveMarker();		
	}

	// Adds the buttons to the map.
	function addControls() {

		// Distance Slider

        var sliderDiv = document.createElement("div");
		sliderDiv.style.marginBottom = "12px";
		sliderDiv.style.marginLeft = "5px";
		sliderDiv.style.marginRight = "5px";
		var slider = document.createElement("input");
		slider.setAttribute("type", "range");
		slider.value = 0;
		slider.id = "distanceSlider";
		slider.max = 250;

		// If there is no marker on the map...
		// Add listener to change circle radius if the slider moves
		addGeoCircle(slider);

		sliderDiv.appendChild(slider);
		map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(sliderDiv);

		/********************************************************/

		// Distance value display

		var distDiv = document.createElement("div");
		distDiv.id = "distDiv";
		distDiv.style.marginBottom = "16px";
		distDiv.innerHTML = "0 mi";

		map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(distDiv);

		/********************************************************/

		// Image Preview

		var imgDiv = document.createElement("div");
		imgDiv.style.marginTop = "12px";
		imgDiv.style.marginLeft = "12px";
		imgDiv.id = "imgDiv";

		map.controls[google.maps.ControlPosition.TOP_LEFT].push(imgDiv);
		
		/********************************************************/
		
		// "Show All Markers" Checkbox
		
		var showAllDiv = document.createElement("div");
		showAllDiv.innerHTML = "Show All Markers";
		showAllDiv.id = "showAllDiv";
		var showAllCheck = document.createElement("input");
		showAllCheck.setAttribute("type", "checkbox");
		showAllCheck.id = "showAllMarkers";
		addCheckboxListener(showAllCheck);		
		showAllDiv.insertBefore(showAllCheck, showAllDiv.firstChild);
		
		map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(showAllDiv);
	}
	
	// Adds a listener for the "Show All Markers" checkbox.
	function addCheckboxListener(checkbox) {
		google.maps.event.addDomListener(checkbox, "change", function() {
			if (checkbox.checked) {		
				for(var i = 0; i < markerList.length; i++){
					markerList[i].setMap(map);
				}
			}
			else { updateMarkers();	}
		});
	}

	// Adds listener to map that changes marker position on user click.
	function moveMarker() {		
		google.maps.event.addListener(map, "click", function (event) {
			lat = event.latLng.lat();
			lng = event.latLng.lng();
			if (marker) { marker.setMap(null); }
			if (geoCircle) { geoCircle.setMap(null); }
			marker = new google.maps.Marker({
				position: new google.maps.LatLng(lat, lng),
				map: map,
				cursor: 'default'
			});
			if (document.getElementById("distanceSlider").value != 0 && !document.getElementById("showAllMarkers").checked) {
				updateMarkers();
			}

			addGeoCircle(document.getElementById("distanceSlider"));

			document.getElementById("distanceSlider").disabled = false;
		});		
	}

	// Adds the circle to look for other images within a certain distance
	function addGeoCircle(slider) {
		geoCircle = new google.maps.Circle({ // Make a circle around the marker
			strokeColor: "#2D2D2D",
			strokeOpacity: 0.8,
			strokeWeight: 2,
			fillColor: "#2D2D2D",
			fillOpacity: 0.1,
			map: map,
			cursor: 'default',
			center: new google.maps.LatLng(lat, lng),
			radius: slider.value * 1609.344,
			clickable: false
		});

		google.maps.event.addDomListener(slider, "input", function() {
			geoCircle.setRadius(slider.value * 1609.344);
			if (document.getElementById("distanceSlider").value != 0 && !document.getElementById("showAllMarkers").checked) {
				updateMarkers();
			}
			document.getElementById("distDiv").innerHTML = slider.value + " mi";
		});
	}

	function codeLatLng() {
		findAllCoordinates();
		initialize();
		var latlng = new google.maps.LatLng(lat, lng);
        map.setZoom(3);
        marker = new google.maps.Marker({
            position: latlng,
            map: map,
            cursor: 'default'
        });
    }

	// Displays all markers that are within the correct distance
	// coordinateList is an array with each element styled as: [encryptedID,latitude,longitude]
    function updateMarkers() {    	
		for (var i = 0; i < markerList.length; i++) {
			var tempDistance = distance(lat, lng, markerList[i].position.lat(), markerList[i].position.lng(), 'M')
			if (markerList[i].getMap() != map && tempDistance <= document.getElementById("distanceSlider").value) {
				markerList[i].setMap(map); // Display if within distance
			}
			else if (markerList[i].getMap() != null && tempDistance > document.getElementById("distanceSlider").value) {
				markerList[i].setMap(null); // Don't display if not within distance
			}
		}
    }

	function findAllCoordinates() {
		var xml = new XMLHttpRequest();
		xml.onreadystatechange = function () {
			if (xml.readyState == 4 && xml.status == 200) {
				coordinateList = xml.responseText.split(";");
				loadMarkerList();
			}
		}
		xml.open("GET", "/image-location-toolkit/rest/files/search_coordinates", true);
		xml.send();
	}

	// Loads a list with all the markers needed for the page.
	function loadMarkerList() {
		for (var i = 0; i < coordinateList.length; i++) {
			var newLat = coordinateList[i].split(",")[1]; // Extract the coordinates from the string
			var newLng = coordinateList[i].split(",")[2];
			var newMarker =	// Store all markers and display them to the map initially
				new google.maps.Marker({
					position: new google.maps.LatLng(newLat, newLng),
					map: null,
					icon: "http://maps.google.com/mapfiles/ms/icons/purple-dot.png",
					clickURL: "/image-location-toolkit/view_image.jsp?id=" + coordinateList[i].split(",")[0],
					hoverURL: "/image-location-toolkit/rest/files/images/" + coordinateList[i].split(",")[0]
				});
				// onclick event listener
				google.maps.event.addDomListener(newMarker, "click", function() {
					window.location.href = this.clickURL;
				});
				// onmouseover event listener
				google.maps.event.addDomListener(newMarker, "mouseover", function(event) {
					var imgDiv = document.getElementById("imgDiv");
					var img = document.createElement("img");
					img.id = "thumbnail";
					img.style.maxHeight = "250px";
					img.style.maxWidth = "250px";
					img.src = this.hoverURL;
					imgDiv.appendChild(img);
				});
				// onmouseout event listener
				google.maps.event.addDomListener(newMarker, "mouseout", function(event) {
					document.getElementById("imgDiv").innerHTML = "";
				});
			markerList.push(newMarker);
		}
	}

	// Code taken from http://www.geodatasource.com/developers/javascript.
	// Finds the distance between 2 sets of coordinates
    function distance(lat1, lon1, lat2, lon2, unit) {
    	var radlat1 = Math.PI * lat1/180
    	var radlat2 = Math.PI * lat2/180
    	var radlon1 = Math.PI * lon1/180
    	var radlon2 = Math.PI * lon2/180
    	var theta = lon1-lon2
    	var radtheta = Math.PI * theta/180
    	var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    	dist = Math.acos(dist)
    	dist = dist * 180/Math.PI
    	dist = dist * 60 * 1.1515
    	if (unit=="K") { dist = dist * 1.609344 }
    	if (unit=="N") { dist = dist * 0.8684 }
    	return dist
    }

	function getParameterByName(name) {
		name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
		var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
			results = regex.exec(location.search);
		return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}

</script>
</head>
<body onload="codeLatLng()">
	<ul class = "nav">
        <li><a href="/image-location-toolkit">Home</a></li>
        <li><a href="/image-location-toolkit/search.jsp">Search</a></li>
        <li><a href="/image-location-toolkit/explore.jsp">Explore</a></li>
    </ul>
	<br>
	<div id="map-canvas"></div>

</body>
</html>
<!-- Currently, this code is only guaranteed to work in the newer versions of Chrome. -->