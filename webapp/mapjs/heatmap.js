var heatmap = {
	generateMap : function (htmlMap, whichNetwork) {
		var heatmaploc;
		var pts = []
		$.getJSON('/data/heatmap?ssid='+whichNetwork, function(json){
			for(i in json){
					pts.push({location: new google.maps.LatLng(json[i][1], json[i][2]), weight: parseFloat(json[0]) });
			}
			console.log(pts);
			var pointArray = new google.maps.MVCArray(pts);
			heatmaploc = new google.maps.visualization.HeatmapLayer({ data: pointArray });
			heatmaploc.setMap(htmlMap);	
		})
 
	},

	clearMap: function (mapData) {
		mapData.setMap(null);
	}
}
