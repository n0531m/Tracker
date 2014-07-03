var map;
var marker_current;
var infowindow = new google.maps.InfoWindow();
google.maps.event.addDomListener(window, "load", initializeMap);
function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex
			.exec(location.search);
	return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g,
			" "));
}
/*
 * function zoom(map) { var bounds = new google.maps.LatLngBounds();
 * map.data.forEach(function(feature) { processPoints(feature.getGeometry(),
 * bounds.extend, bounds); }); map.fitBounds(bounds); } function
 * processPoints(geometry, callback, thisArg) { if (geometry instanceof
 * google.maps.LatLng) { callback.call(thisArg, geometry); } else if (geometry
 * instanceof google.maps.Data.Point) { callback.call(thisArg, geometry.get()); }
 * else { geometry.getArray().forEach(function(g) { processPoints(g, callback,
 * thisArg); }); } }
 */
function handleNoGeolocation(errorFlag) {
	var message = "";
	if (errorFlag) {
		message = 'Error: The Geolocation service failed.';
	} else {
		message = 'Error: Your browser doesn\'t support geolocation.';
	}
	console.log(message);
}
function setCurrentPositionMarker(map, latlng) {
	console.log("setMarker : " + latlng.toString());
	if (!marker_current) {
		marker_current = new google.maps.Marker({
			position : latlng,

			icon : {
				path : google.maps.SymbolPath.CIRCLE,
				scale : 8,
				fillColor : 'blue',
				fillOpacity : 0.8,
				strokeColor : 'lightblue',
				strokeWeight : 4
			},

			// icon :
			// "http://icons.iconarchive.com/icons/icons-land/vista-map-markers/32/Map-Marker-Marker-Outside-Azure-icon.png",
			draggable : false,
			map : map
		});

	} else {
		marker_current.setPosition(latlng);
	}
}
function initializeMap() {
	console.log("#initializeMap");

	var center = new google.maps.LatLng(35.676148, 139.74479);

	var mapOptions = {
		// zoom : 14,
		zoom : 18,
		center : center,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		disableDefaultUI : false,
		// streetViewControl : true,
		mapTypeIds : [ google.maps.MapTypeId.HYBRID,
				google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.TERRAIN,
				google.maps.MapTypeId.SATELLITE, "mapstyle_grayscale", //
		]
	// ,
	// styles : mapstyle_grayscale
	};
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

	styledMap_gray = new google.maps.StyledMapType(feature_opts_grayscale, {
		name : "Gray"
	});
	map.mapTypes.set("mapstyle_grayscale", styledMap_gray);
	map.setMapTypeId("mapstyle_grayscale");
	/*
	 * map.mapTypes.set('map-style-grayscale', new google.maps.StyledMapType(
	 * mapstyle_grayscale, { map : map, name : 'Grayscale' }));
	 */

	var lat = getParameterByName("latitude");
	var lng = getParameterByName("longitude");

	if ((lat != "") && (lng != "")) {
		map.setCenter(new google.maps.LatLng(lat, lng))
	} else {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				var pos = new google.maps.LatLng(position.coords.latitude,
						position.coords.longitude);
				/*
				 * infowindow = new google.maps.InfoWindow({ map : map, position :
				 * pos, content : '莉翫さ繧ｳ' });
				 */
				map.panTo(pos);
				setCurrentPositionMarker(map, pos);

			}, function() {
				handleNoGeolocation(true);
			});
		} else {
			handleNoGeolocation(false);
		}
	}

	if (getParameterByName("reporter") != "") {
		// http://2-dot-third-shade-621.appspot.com/crumbs?reporter=19619217
		map.data.loadGeoJson("./crumbs?reporter="
				+ getParameterByName("reporter"));
		// map.data.loadGeoJson("//third-shade-621.appspot.com/crumbs?reporter="
		// + getParameterByName("reporter"));
		// zoom(map);
		map.data.setStyle(function(feature) {
			var accuracy = feature.getProperty('accuracy');
			// var color = ascii > 91 ? 'red' : 'blue';
			var color = "#f00";
			/*
			 * if (feature.getProperty("providername") == "gps") { color =
			 * "#0f0"; }
			 */
			return {
				icon : {
					path : google.maps.SymbolPath.CIRCLE,
					// path : "M 100, 100 m -75, 0 a 75,75 0 1,0 150,0 a 75,75 0
					// 1,0 -150,0",
					scale : accuracy,
					fillColor : color,
					// fillOpacity : 0.35,
					fillOpacity : 1 / accuracy,
					strokeWeight : 0
				},
				title : feature.getProperty("reportDate")
			};
		});
		map.data.addListener('click', function(event) {
			map.data.overrideStyle(event.feature, {
				fillColor : 'red'
			});
		});
	}

}
$(document).ready(function() {
});