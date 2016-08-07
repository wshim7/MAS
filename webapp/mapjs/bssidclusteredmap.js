var bssidclusteredmap = {
  generateMap : function(htmlMap) {
    $.get( "/data/bssidmap", function( data ) {
      points = data.points;
      markers = []
      for (var i = 0; i < points.length; i++) {
        var latLng = new google.maps.LatLng(parseFloat(points[i].lat), parseFloat(points[i].lng));
        var marker = new google.maps.Marker({'position': latLng});
        markers.push(marker);
      }
      var markerCluster = new MarkerClusterer(htmlMap, markers);
      console.log(markerCluster)
    }, "json");
  },


  clearMap: function (mapData) {
    mapData.clearMarkers();
  }
}
