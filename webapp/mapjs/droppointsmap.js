var droppointsmap = {
	generateMap : function(htmlMap) {
		 var campusCoords = [
			new google.maps.LatLng(33.781520, -84.394408),
			new google.maps.LatLng(33.783014, -84.394483),
			new google.maps.LatLng(33.783081, -84.396972),
			new google.maps.LatLng(33.781552, -84.397197),
			new google.maps.LatLng(33.781605, -84.407516),
			new google.maps.LatLng(33.776193, -84.407077),
			new google.maps.LatLng(33.774084, -84.405661),
			new google.maps.LatLng(33.772376, -84.401659),
			new google.maps.LatLng(33.771426, -84.397571),
			new google.maps.LatLng(33.771368, -84.396133),
			new google.maps.LatLng(33.768911, -84.396101),
			new google.maps.LatLng(33.768532, -84.390683),
			new google.maps.LatLng(33.775154, -84.390651),
			new google.maps.LatLng(33.775105, -84.387325),
			new google.maps.LatLng(33.777553, -84.387304),
			new google.maps.LatLng(33.777896, -84.391209),
			new google.maps.LatLng(33.781512, -84.391316)
  		];
		
		// Construct the polygon.
		campusBoundaries = new google.maps.Polygon({
			paths: campusCoords,
			strokeColor: '#fff600',
			strokeOpacity: 0.8,
			strokeWeight: 1,
			fillColor: '#FF0000',
			fillOpacity: 0
		});
		
		//campusBoundaries.setMap(map);
		
		/*$.get( "/data/droppointsmap?dtype=offline", function( data ) {
			console.log(data);
			var dropsCount = 0;
			for (i = 0; i < data.length; i++) {
				var currObj = data[i];
				var wapOptions = {
					strokeWeight: 0,
					fillColor: 'RGB(0,191,243)',
					fillOpacity: 1,
					map: htmlMap,
					center: { lat: parseFloat(currObj[0]), lng: parseFloat(currObj[1]) },
					radius: 5
				};
				// Add the circle for this city to the map.
				cityCircle = new google.maps.Circle(wapOptions);
				dropsCount++;
			}
			console.log('offline: '+dropsCount);
		}, "json");*/
		
		$.get( "/data/droppointsmap?dtype=undetected", function( data ) {
			console.log(data);
			var dropsCount = 0;
			for (i = 0; i < data.length; i++) {
				var currObj = data[i];
				if (google.maps.geometry.poly.containsLocation(new google.maps.LatLng(currObj[0], currObj[1]), campusBoundaries)) {
					var wapOptions = {
						strokeWeight: 0,
						fillColor: 'RGB(13,0,76)',
						fillOpacity: 1,
						map: htmlMap,
						center: { lat: parseFloat(currObj[0]), lng: parseFloat(currObj[1]) },
						radius: 5
					};
					// Add the circle for this city to the map.
					cityCircle = new google.maps.Circle(wapOptions);
					dropsCount++;
				}
			}
			console.log('undetected: '+dropsCount);
		}, "json");
		
		$.get( "/data/droppointsmap?dtype=time", function( data ) {
			console.log(data);
			var dropsCount = 0;
			for (i = 0; i < data.length; i++) {
				var currObj = data[i];
				var wapOptions = {
					strokeWeight: 0,
					fillColor: 'RGB(102,45,145)',
					fillOpacity: 1,
					map: htmlMap,
					center: { lat: parseFloat(currObj[0]), lng: parseFloat(currObj[1]) },
					radius: 5
				};
				// Add the circle for this city to the map.
				cityCircle = new google.maps.Circle(wapOptions);
				dropsCount++;
			}
			console.log('time: '+dropsCount);
		}, "json")
	},
	
	clearMap: function (mapData) {
		for (i = 0; i < mapData.length; i++)
			(mapData[i]).setMap(null);
	}
}