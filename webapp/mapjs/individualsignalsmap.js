var individualsignalsmap = {
	generateMap : function(htmlMap) {
		$.get( "/data/individualsignalsmap", function( data ) {
			var numColors = data.number;
			var spectrumInterval = Math.floor(767/numColors);
			points = data.points;
			var currColor = 0;

			for (i = 0; i < points.length; i++) {
				var currObj = points[i];
				if (i != 0 && (points[i-1]).imei != currObj.imei) {
					currColor++;
					console.log('Curr Color: '+getRGB(currColor*spectrumInterval)+', imei: ' + currObj.imei);
				}
				var wapOptions = {
					strokeWeight: 0,
					fillColor: 'RGB('+window.getRGB(spectrumInterval*currColor)+')',
					fillOpacity: .75,
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