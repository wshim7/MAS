var bssidmap = {
	generateMap : function(htmlMap) {
		$.get( "/data/bssidmap", function( data ) {
			//console.log( "Data Loaded: " + data[0].bssid );
			var numColors = 100;
			var spectrumInterval = Math.floor(767/numColors);
			//make 100 colors to assign at random because there are over 500 bssids already
			points = data.points;
			var currColor = Math.floor(Math.random()*numColors);

			for (i = 0; i < points.length; i++) {
				var currObj = points[i];
				if (i != 0 && (points[i-1]).bssid != currObj.bssid)
					currColor = Math.floor(Math.random()*numColors);
				var wapOptions = {
					strokeWeight: 0,
					fillColor: 'RGB('+window.getRGB(spectrumInterval*currColor)+')',
					fillOpacity: 1,
					map: htmlMap,
					center: { lat: parseFloat(currObj.lat), lng: parseFloat(currObj.lng) },
					radius: 0.5*parseFloat(currObj.avg_signal)
				};
				// Add the circle for this city to the map.
				cityCircle = new google.maps.Circle(wapOptions);
			}
		}, "json");
	},

	clearMap: function (mapData) {
		for (i = 0; i < mapData.length; i++)
			(mapData[i]).setMap(null);
	}
}