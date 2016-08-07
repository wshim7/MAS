# Crowd Source connectivity
CS 8803 Project

Endpoints
http://a.ashwinikhare.in:3050

Endpoint | Description
---------|-----------
`/add` | Add data from POST request
`/add/offline` | Add offline data from POST request
`/map` | Visualization of data
`/data/mapname` |  get relevant data output for specified map
`/data/mapjs` |  get relevant js file for specified map
`/points/<ssid>` | Gets Avg signal v/s lat long for ssid [GTwifi, GTother]
`/droppoints/<ssid>` | Displays location of drop points for ssid
`/predict` | GET parameters `bid, time`. Dummy placeholder for predictive function 