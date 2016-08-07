var bssidareamap = {
  generateMap : function(htmlMap) {
    $.get( "/data/bssidareamap", function( data ) {
      if (!google.maps.Polygon.prototype.getBounds) {
        google.maps.Polygon.prototype.getBounds=function(){
            var bounds = new google.maps.LatLngBounds()
            this.getPath().forEach(function(element,index){bounds.extend(element)})
            return bounds
        }
      }
      var points = null
          polygonCoords = null,
          mapPolygons = [],
          mapPolygon = null,
          color = null
          infoWindow = null
          bounds = new google.maps.LatLngBounds();
      for (var i = 0; i < data.length; i++) {
        polygonCoords = [];
        points = data[i].points;
        for (var j = 0; j < points.length; j++) {
          polygonCoords.push(new google.maps.LatLng(points[j][0], points[j][1]))
        }
        color = getRandomColor();
        mapPolygon = new google.maps.Polygon({
          paths: polygonCoords,
          strokeColor: color,
          strokeOpacity: 0.8,
          strokeWeight: 2,
          fillColor: color,
          fillOpacity: 0.35
        });
        mapPolygon.setMap(htmlMap)
        mapPolygon.set('info', data[i].bssid)
        mapPolygons.push(mapPolygon)
      }
      $.each(mapPolygons, function(i, mapPolygon) {
        google.maps.event.addListener(mapPolygon, 'mouseover', function (event) {
         $.each(mapPolygons, function(idx, val) {
            val.setOptions({visible: false})
         })
         mapPolygon.setOptions({visible: true, fillColor: '#FF0000'})
          infoWindow = new google.maps.InfoWindow();
          infoWindow.setContent("BSSID : " + mapPolygon.get("info"));
          infoWindow.setPosition(mapPolygon.getBounds().getCenter());
          infoWindow.open(htmlMap);
       });
        google.maps.event.addListener(mapPolygon, 'mouseout', function (event) {
           $.each(mapPolygons, function(idx, val) {
             color = getRandomColor()
             val.setOptions({visible: true, fillColor: color, strokeColor: color})
           })
           infoWindow.close();
         });
      });
    }, "json");
  },

  clearMap: function (mapData) {
    mapData.clearMarkers();
  }
}
