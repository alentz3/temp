<html>
<link rel="stylesheet" type="text/css" href="gallery.css">

<head>
<link rel="icon"type="image/png"href="camera.png">
	<title>Uploaded Images</title>
</head>
<body onload="iterateIDs()">

	<ul class = "nav">
		<li><a href="/image-location-toolkit">Home</a></li>
		<li><a href="/image-location-toolkit/search.jsp">Search</a></li>
		<li><a href="/image-location-toolkit/explore.jsp">Explore</a></li>
	</ul>

	<div class="wrapper">

		<h1>
			Uploaded Images
		</h1>

		<div id = "nonUploadedImages">
  		</div>

		<div id="img-wrapper">
		</div>
	</div>
</body>


<script>
	var IDs = JSON.parse(window.localStorage.getItem("ImageIdArray"));
	var nonUploadedImages = JSON.parse(window.localStorage.getItem("NonUploadedImageNames"));
	var IDarr = new Array();


	function iterateIDs() {
		var count = 0;
		var str = "";
		for(var x = 0; x < nonUploadedImages.length; x++) {
			str += nonUploadedImages[x] + ",";
		}
		if(str !== ""){
			str = str.substring(0, str.length - 1);
			document.getElementById("nonUploadedImages").innerHTML = ("Image(s): " + str + " were too large to be uploaded");
			}
		for (var i = 0; i < IDs.length; i++) {
			addImg(IDs[i], count);
			count++;
		}
	}

	function addImg(id, count) {
		var frag = document.createDocumentFragment();
		var div = document.createElement("DIV");
		div.id = "div" + count;
		div.setAttribute("class", "imgDiv");

		var img = document.createElement("IMG");
		img.id = "img" + count;
		var url = "/image-location-toolkit/rest/files/images/" + id;
		img.src = url;
		img.addEventListener("click", function () {
			window.location.href = "/image-location-toolkit/view_image.jsp?id=" + id;
		});

		div.appendChild(img);
		frag.appendChild(div);
		document.getElementById("img-wrapper").appendChild(frag);
	}

</script>
</html>