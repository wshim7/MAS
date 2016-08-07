var heatmapcustom = {
	generateMap : function(htmlMap, whichNetwork) {
		$.get( '/data/heatmap?ssid='+whichNetwork, function( data ) {
			console.log(data);
			var colorlow = 'RGB(251,219,92)';
			var colormed = 'RGB(255,179,60)';
			var colorhigh = 'RGB(222,0,0)';
			var curColor;
			
			for (i = 0; i < data.length; i++) {
				var curObj = data[i];
				var signalStr = parseFloat(curObj[0]);
				if (signalStr < 4)
					curColor = colorlow;
				else if (signalStr >= 4 && signalStr < 8)
					curColor = colormed;
				else if (signalStr >=8) 
					curColor = colorhigh;
				var wapOptions = {
					strokeWeight: 0,
					fillColor: curColor,
					fillOpacity: signalStr/10,
					map: htmlMap,
					center: { lat: parseFloat(curObj[1]), lng: parseFloat(curObj[2]) },
					radius: 2 /*0.5*parseFloat(curObj[0])*/
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
