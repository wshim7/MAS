var pathsmap = {
	generateMap : function(htmlMap) {
		$.get( "/data/pathsmap", function( data ) {
			console.log(data);
			for (i = 0; i < data.length; i++) {
				var currObj = data[i];
				var wapOptions = {
					strokeWeight: 0,
					fillColor: 'RGB(0,0,0)',
					fillOpacity: 1,
					map: htmlMap,
					center: { lat: parseFloat(currObj[0]), lng: parseFloat(currObj[1]) },
					radius: 2
				};
				// Add the circle for this city to the map.
				cityCircle = new google.maps.Circle(wapOptions);
				//console.log(cityCircle);

			}
		}, "json")
	},
	
	clearMap: function (mapData) {
		for (i = 0; i < mapData.length; i++)
			(mapData[i]).setMap(null);
	}
}