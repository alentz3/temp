<html>
<link rel="stylesheet" type="text/css" href="search_by_image.css">
<title>ILT - Search By Image</title>

<body onload="getImageURL()">
<div id="loadingDiv" align="center" top="50%" z-index:100">
    <img id="loadingIcon" src="http://i.stack.imgur.com/qtj7C.gif">
</div>
</body>

<script>
var id = getParameterByName("id");

// Gets a URL that can be used to perform a Google Search by Image
function getImageURL() {
    var xml = new XMLHttpRequest();
    xml.onreadystatechange = function () {
        if (xml.readyState == 4 && xml.status == 200) {
            deleteImageURL(xml.responseText);
        }
    }
    xml.open("GET", "/image-location-toolkit/rest/files/"
        + id + "/public_url", true);
    xml.send();
}

// Deletes the URL for the image
function deleteImageURL(imageURL) {
    var xml = new XMLHttpRequest();
    xml.open("GET", "/image-location-toolkit/rest/files/image_url/delete", true);
    xml.send();
    window.location.href = "https://www.google.com/searchbyimage?site=search&sa=X&image_url=" + imageURL;
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

</script>
</html>