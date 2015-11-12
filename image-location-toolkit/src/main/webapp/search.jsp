<!DOCTYPE html>

<link rel="stylesheet" type="text/css" href="search.css">
<link rel="icon"type="image/png"href="camera.png">

<html>

<head lang="en">
    <script src="jquery-1.11.3.min.js"></script>
   <!-- <script type="text/javascript" src="jquery.tooltipster.min.js"></script>-->
    <meta charset="UTF-8">
    <title>ILT - Search</title>
</head>

<body>

<ul class = "nav">
    <li><a href="/image-location-toolkit">Home</a></li>
    <li><a href="/image-location-toolkit/search.jsp">Search</a></li>
    <li><a href="/image-location-toolkit/explore.jsp">Explore</a></li>
</ul>

<div class="wrapper">
    <div class="pageTitle">
        <h1>Search Images</h1>
    </div>
    <div id="searchFormDiv">
        <form id="searchForm">
            <div id="checkboxes">
                <div id="allAnyCbox" style="display:none;">
                    Require all fields:
                    <input type="checkbox" id="allAny" name="allAny">
                </div>
                <div id="ThesCbox" style="display:none;">
                    Use Thesaurus:
                    <input type="checkbox" id="Thesaurus" name="thesaurus" checked>
                </div>
            </div>
            <div id="boxes">
                <div id="searchline1" class = "searchline">
                    <select id="select1" name = "select1" onchange="checkThesaurusOption()">
                        <option value="date">Date & Time</option>
                        <option value="location">Location</option>
                        <option value="text">Extracted Text</option>
                        <option value="tag">Image Tags</option>
                    </select><input type="text" id="text1" name="text1"><br>
                </div>
            </div>
            <div id="buttons">
                <input type="button" class="button" onclick="addLine()" value="add new line" />
                <input type="submit" class="button" value="search" id= "submitter" />
                <button type="button" class="button" onclick="resetBoxes()">reset</button>
            </div>
        </form>
    </div>
    <div id="slideshow">
        <div id="thumbnail-wrapper">
            <div id="thumbnails">
            </div>
		</div>
		<div id="selected-div"></div>
    </div>
</div>
</body>
</html>

<script>
    var i = 1;
    var selVals = new Array();
    var textVals = new Array();
    var opts = ["Date & Time", "Location", "Extracted Text", "Image Tags"];
    var names = ["date", "location", "text", "tag"];
    var currentMetadata = new Array();
    var div_clone = $("#boxes").clone();
    var IDarr = new Array();
    var client;
    document.getElementById("selected-div").innerHTML = "";
    
    document.onkeydown = function(event) {
    	if (document.getElementById("selected-div").hasChildNodes()) {
	        switch (event.keyCode) {
	            case 37:            	
	                    slideshowPrev();
	                break;
	            case 39:            	
	                	slideshowNext();
	                break;
	        }
    	}
    };

    // Adds a new line to the search form consisting of another selection box and another text box
    function addLine() {
        // Saves user-defined values
        keepSel();
        keepText();
        i++;
        var boxes = document.getElementById("boxes");

        // Creates new Document Fragment to append to the current box div
        var newFrag = document.createDocumentFragment();

        // Appends new select box and text box to the fragment
        var newDiv = document.createElement("DIV");
        newDiv.setAttribute("class", "searchline");
        newDiv.id="searchline" + i;
        newDiv.appendChild(newSelect());
        newDiv.appendChild(newText());
        newDiv.appendChild(document.createElement("br"));
        newFrag.appendChild(newDiv);

        // Appends fragment to div
        boxes.appendChild(newFrag);

        // Resets previously set values in selection boxes and text boxes
        for (n = i - 1; n >= 1; n--) {
            document.getElementById("select" + n).value = selVals.pop();            
            document.getElementById("text" + n).value = textVals.pop();
        }
        document.getElementById("allAnyCbox").style.display = "block";
    }

    // Stores user-selected select box choices in global array
    function keepSel() {
        for (n = 1; n <= i; n++) {
            selVals.push(document.getElementById("select" + n).value);
        }
    }

    // Stores user-defined text queries in global array
    function keepText() {
        for (n = 1; n <= i; n++) {
            textVals.push(document.getElementById("text" + n).value);
        }
    }

    // Creates a new selection box, matching the already present one
    function newSelect() {
        // Create new Select box and set ID/name
        var newSel = document.createElement("SELECT");
        newSel.id = "select" + i;
        newSel.name = "select" + i;
        newSel.addEventListener("change", checkThesaurusOption);

        // Creates and adds Select box Options
        for (var j = 0; j < opts.length; j++) {
            var opt = document.createElement("OPTION");
            opt.text = opts[j];
            opt.value = names[j];
            newSel.add(opt);
        }
        return newSel;
    }

    // Creates a new text box
    function newText() {
        var newText = document.createElement("INPUT");
        newText.setAttribute("type", "text");
        newText.setAttribute("id", "text" + i);
        newText.setAttribute("name", "text" + i);
        return newText;
    }

    // Resets search boxes, calls resetImgs()
    function resetBoxes() {
        $("#boxes").replaceWith(div_clone.clone());
        i=1;
        
        document.getElementById("ThesCbox").style.display = "none";
        document.getElementById("allAnyCbox").style.display = "none";
        resetImgs();
    }

    // Clears images
    function resetImgs() {
        document.getElementById("selected-div").innerHTML = "";
        var thumbnails = document.getElementById("thumbnails");
        while (thumbnails.firstChild) {
            thumbnails.removeChild(thumbnails.firstChild);
        }
    }

    $('#searchForm').submit(function () {
		thisbutton = document.getElementById("submitter");
		thisbutton.setAttribute('disabled',true);
		thisbutton.value = "searching";
	    search();
    	return false;
    });

    function search(){
        // Reset slideshow on new search
        resetImgs();
        var searchingFrag = document.createDocumentFragment();
        var searchingPara = document.createElement("P");
        searchingPara.id = "searchingPara";
        var searchingNode = document.createElement("img");
        searchingNode.src = "http://i.stack.imgur.com/qtj7C.gif";
        searchingPara.appendChild(document.createElement("BR"));
        searchingPara.appendChild(searchingNode);
        searchingFrag.appendChild(searchingPara);
        document.getElementById("searchFormDiv").appendChild(searchingFrag);
        slideshowIndex = 0;
        var formData = $('#searchForm').serialize();
        console.log('Searching database for relevant images.');
        $.ajax({
            type: "POST",
            url: "/image-location-toolkit/rest/files/images/query",
            data:  formData,
            dataType: "text",
            contentType : "application/json",
            success:  function(data){
                displaySearchImages(data);
            },
            error: function(jqXHR, textStatus, errorThrown){
                // remove "Searching..." text on fail
                var searchingPara = document.getElementById("searchingPara");
                searchingPara.parentNode.removeChild(searchingPara);

                alert("Error: Something went wrong.");
            }
        });
    }

    function displaySearchImages(Ids) {
        var numberOfImages;
        if (Ids === "[]") {
            // remove "Searching..." text after no results found
            var searchingPara = document.getElementById("searchingPara");
            searchingPara.parentNode.removeChild(searchingPara);

            alert("No matches")
			var thisbutton1 = document.getElementById("submitter");
			thisbutton1.value = "search";
			thisbutton1.removeAttribute('disabled');
            return;
        }

        Ids = Ids.substring(1, Ids.length - 1);
        IDarr = Ids.split(",");
        for (var x = 0; x < IDarr.length; x++) {
            IDarr[x] = IDarr[x].substring(1, IDarr[x].length - 1);
            // Create new div for each thumbnail
            var newThumbFrag = document.createDocumentFragment();
            var newThumbDiv = document.createElement("DIV");
            newThumbDiv.id = "thumbDiv-" + x;
            newThumbFrag.appendChild(newThumbDiv);
            document.getElementById("thumbnails").appendChild(newThumbFrag);
            getMetadata('/image-location-toolkit/rest/files/images/' + IDarr[x] + '/metadata', x, IDarr[x]);
        }
    }

    function getMetadata(url,imgNum, Id) {
        var xml = new XMLHttpRequest();
        xml.onreadystatechange = function () {
            if (xml.readyState == 4 && xml.status == 200) {
               parseData(xml.responseText,imgNum);
               getTags("/image-location-toolkit/rest/files/images/" + Id + "/tags", imgNum, Id);
            }
        }

        xml.open("GET", url, true);
        xml.send();
    }

    function getTags(url, imgNum, Id) {
        	var xml = new XMLHttpRequest();
        	xml.onreadystatechange = function () {
        		if (xml.readyState == 4 && xml.status == 200) {
        			currentMetadata[imgNum] += "\nTags: " + xml.responseText;
        			setImage("/image-location-toolkit/rest/files/images/" + Id, imgNum, Id);
        		}
        	}
        	xml.open("GET",url, true);
        	xml.send();
    }

    // Parses and displays image metadata
    function parseData(xmlResponse,imgNum) {
        var data = JSON.parse(xmlResponse);
        arr = []
        for (var event in data) {
            var dataCopy = data[event]
            for (key in dataCopy) {
                if (key == "start" || key == "end") {
                    dataCopy[key] = new Date(dataCopy[key])
                }
            }
            arr.push(dataCopy)
        }

        var output = "Date: " + arr[3] + "\n";
        output += "Coordinates: " + arr[4] + ", " + arr[5] + "\n";
        output += "Location: " + arr[6] +"\n";
        output += "Text: " + arr[7];
        //document.getElementById("metadataStr").innerHTML = output;
        currentMetadata[imgNum] = output;
    }

    // Displays images on results page
    function setImage(url, imgNum, Id) {
        var xml = new XMLHttpRequest();
        xml.onreadystatechange = function() {
            if (xml.readyState == 4 && xml.status == 200) {
                // Creates image thumbnails
                var url = window.URL || window.webkitURL;
                var newImg = document.createElement("IMG");
                newImg.src = url.createObjectURL(this.response);
                newImg.id = "thumbnail-" + imgNum;
                newImg.title = currentMetadata[imgNum];
                newImg.imageID = Id;

                // Gives all thumbnails transparency so that selected image stands out
                newImg.style.opacity = "0.4";

                // Sets non-selected images to have full opacity when moused over, and then revert to 0.4 on mouseout
                newImg.onmouseover = function() {
                    // only change opacity to 1.0 for non-selected image (does this check add efficiency?)
                    if (this.id.substring(10) != slideshowIndex) {
                        this.style.opacity = "1.0";
                    }
                }

                newImg.onmouseout = function() {
                    // only revert opacity to 0.4 for non-selected image
                    if (this.id.substring(10) != slideshowIndex) {
                        this.style.opacity = "0.4";
                    }
                }

                // Clicked thumbnails should change selected-img (large) to themselves
                newImg.onclick = function() {
                    var oldImg = document.getElementById("thumbnail-" + slideshowIndex);
                    oldImg.style.opacity = "0.4";
                    slideshowIndex = this.id.substring(10);
                    this.style.opacity = "1.0";
                    var selectedImg = document.getElementById("selected-img");
                    selectedImg.src = this.src;
                    selectedImg.title = this.title;
                    selectedImg.addEventListener("click", function () {
                        window.location.href = "/image-location-toolkit/view_image.jsp?id=" + newImg.imageID;
                    });
                }

                // Appends img to corresponding div in slideshow
                document.getElementById("thumbDiv-" + imgNum).appendChild(newImg);

                // Sets selected image (large, on display) to the first image in the returned set
                if (imgNum == 0) {
                    newImg.style.opacity = "1.0";
                    var selectedImgFrag = document.createDocumentFragment();
                    var selectedImg = document.createElement("IMG");
                    selectedImg.id = "selected-img";
                    selectedImg.title = currentMetadata[imgNum];
                    selectedImg.src = newImg.src;
                    selectedImg.addEventListener("click", function () {
                        window.location.href = "/image-location-toolkit/view_image.jsp?id=" + newImg.imageID;
                    });
                    selectedImgFrag.appendChild(selectedImg);
                    document.getElementById("selected-div").appendChild(selectedImgFrag);  
					var thisbutton1 = document.getElementById("submitter");
					thisbutton1.value = "search";
					thisbutton1.removeAttribute('disabled');
                    // add bottom bar if at least one image displayed
                }

                if (imgNum == IDarr.length - 1) {
                    // remove "Searching..." text once images begin loading
                    var searchingPara = document.getElementById("searchingPara");
                    searchingPara.parentNode.removeChild(searchingPara);
                }
            }
        }

        xml.open("GET", url, true);
        xml.responseType = "blob";
        xml.send();
    }

    // Advances the slideshow one image
    function slideshowNext() {
        var selectedImg = document.getElementById("selected-img");
        var oldImg = document.getElementById("thumbnail-" + slideshowIndex);
        oldImg.style.opacity = "0.4";

        if (slideshowIndex < IDarr.length - 1) { slideshowIndex++; }
        else { slideshowIndex = 0; }

        var newImg = document.getElementById("thumbnail-" + slideshowIndex);
        newImg.style.opacity = "1.0";
        selectedImg.src = newImg.src;
        selectedImg.title = newImg.title;
        
        selectedImg.addEventListener("click", function () {
            window.location.href = "/image-location-toolkit/view_image.jsp?id=" + newImg.imageID;
    	});
    }

    // Sets current slideshow image to the previous image
    function slideshowPrev() {
        var selectedImg = document.getElementById("selected-img");
        var oldImg = document.getElementById("thumbnail-" + slideshowIndex);
        oldImg.style.opacity = "0.4";

        if (slideshowIndex > 0) { slideshowIndex--; } 
        else { slideshowIndex = IDarr.length - 1; }

        var newImg = document.getElementById("thumbnail-" + slideshowIndex);
        newImg.style.opacity = "1.0";
        selectedImg.src = newImg.src;
        selectedImg.title = newImg.title;
        
        selectedImg.addEventListener("click", function () {
            window.location.href = "/image-location-toolkit/view_image.jsp?id=" + newImg.imageID;
    	});
    }
    
    // Checks if the thesaurus checkbox should be visible/hidden and changes it if it is incorrect
    function checkThesaurusOption() {
    	for (var j = 1; j <= i; j++) {
	    	var selectBox = document.getElementById("select" + j);
	    	var checkBox = document.getElementById("ThesCbox");
	    	if (selectBox.value == "tag") { 
	    		checkBox.style.display = "block"; 
	    		return; 
	    	}
    	}
    	checkBox.style.display = "none"; // If none of the selects had tags selected
    }

</script>
