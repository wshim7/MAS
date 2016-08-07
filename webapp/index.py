import time
import json
import datetime
import sys
from bottle import route, run, template, request, static_file
import MySQLdb as mdb

@route('/')
def index():
    return 'Hello MAS Project'

@route('/map')
def map():
    return static_file('visualization.html', root='./')

@route('/mapjs')
def mapjs():
  return static_file(request.query['file']+'.js', root='./mapjs')

# RESTful andpoints
@route('/droppoints/<ssid>')
def get_drop_points_ssid(ssid):
  # Assuming ssid is passed
  if ssid not in ['GTwifi', 'GTother', 'GTvisitor', 'GTRI']:
    return 'not valid SSID'
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT lat, lng FROM locationdata WHERE lat != '0.0' AND ssid = '"+ ssid +"' AND offline = '1' GROUP BY lat,lng"
    cur.execute(query)
    rows = cur.fetchall()
    output = list()
    for row in rows:
      output.append(row)
    return json.dumps(output)
  except:
    return json.dumps([])

@route('/points/<ssid>')
def get_points(ssid):
  print ssid
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT AVG(signal_level), lat, lng, imei FROM locationdata WHERE ssid = '"+ ssid +"' GROUP BY lat, lng, imei ORDER BY imei"
    querySize = "SELECT COUNT(DISTINCT imei) FROM locationdata WHERE lat != '0.0' AND ssid = '" + ssid + "'"
    print query
    cur.execute(query)
    rows = cur.fetchall()
    cur.execute(querySize)
    rowsSize = cur.fetchall()
  except:
    return 'No query'
  iSignals = {}
  iSignals['number'] = rowsSize[0][0]
  points = []
  for row in rows:
    currISignal = {}
    currISignal['avg_signal'] = str(row[0])
    currISignal['lat'] = row[1]
    currISignal['lng'] = row[2]
    currISignal['imei'] = row[3]
    points.append(currISignal)
  iSignals['points'] = points
  return json.dumps(iSignals)

@route('/predict')
def predict():
  if 'bid' not in request.query.keys() or 'time' not in request.query.keys():
    return 'Incomple information. bid + time required'
  bid =request.query['bid']
  time = request.query['bid']
  prediction = {
    'occupancy' : 'magic'
  }
  return json.dumps(prediction)

@route('/data/heatmap')
def getDataHeatMap():
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT AVG(signal_level) as avg_signal, lat, lng FROM `locationdata` WHERE lat != '0.0' \
            AND ssid = '"+ request.query['ssid']+"' GROUP BY lat,lng,bssid ORDER BY avg_signal"
    cur.execute(query)
    rows = cur.fetchall()
  except:
    return 'No query'
  dump = []
  for row in rows:
    newrow = []
    newrow.append(str(row[0]))
    newrow.append(row[1])
    newrow.append(row[2])
    dump.append(newrow)
  return json.dumps(dump)

@route('/data/bssidmap')
def getDataBssidMap():
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT AVG(signal_level) as avg_signal, lat, lng, bssid FROM `locationdata` WHERE lat != '0.0' \
             AND ssid = 'GTwifi' GROUP BY lat,lng,bssid ORDER BY bssid"
    querySize = "SELECT COUNT(DISTINCT bssid) FROM locationdata WHERE lat != '0.0' AND ssid = 'GTwifi'"
    cur.execute(query)
    rows = cur.fetchall()
    cur.execute(querySize)
    rowsSize = cur.fetchall()
  except:
    return 'No query'
  bssids = {}
  bssids['number'] = rowsSize[0][0]
  points = []
  for row in rows:
    currBssid = {}
    currBssid['avg_signal'] = str(row[0])
    currBssid['lat'] = row[1]
    currBssid['lng'] = row[2]
    currBssid['bssid'] = row[3]
    points.append(currBssid)
  bssids['points'] = points
  return json.dumps(bssids)

@route('/data/bssidareamap')
def getDataBssidMap():
  # Local import
  # Removing bssid map for now
  """
  from scipy.spatial import ConvexHull
  from copy import deepcopy

  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SET SESSION group_concat_max_len = 1000000000000"
    cur.execute(query)
    q2 = "SELECT GROUP_CONCAT(CONCAT( lat,  ':', lng)), bssid FROM `locationdata` WHERE lat != '0.0' AND lng != '0.0' AND ssid = 'GTwifi' GROUP BY bssid, ssid ORDER BY bssid"
    cur.execute(q2)
    rows = cur.fetchall()
  except:
    return 'No query'

  polygons = []
  for row in rows:
    points = row[0].split(',')
    array_pts = deepcopy([])
    for pt in points:
      p = pt.split(':')
      array_pts.append([float(p[0]), float(p[1])])
    if len(array_pts) < 3:
      continue
    try:
      hull = ConvexHull(array_pts)
    except:
      continue
    vertices = deepcopy([])
    length_pts = len(array_pts)
    for idx in hull.vertices:
      if (idx >= length_pts):
        break
      vertices.append([array_pts[idx][0], array_pts[idx][1]])
    polygons.append({'bssid': row[1], 'points': vertices})

  return json.dumps(polygons)
  """
  pass

@route('/data/droppointsmap')
def getDataDropPointsMap():

  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    if request.query['dtype'] == 'offline':
      query = "SELECT lat, lng FROM locationdata WHERE lat != '0.0' AND ssid = 'GTwifi' AND offline = '1' GROUP BY lat,lng"
    elif request.query['dtype'] == 'time':
      query = "SELECT distinct loc2.lat, loc2.lng FROM `locationdata` loc1 JOIN locationdata loc2 ON loc2.timestamp - loc1.timestamp = 7 AND loc1.imei = loc2.imei WHERE loc1.ssid = 'GTwifi' AND loc1.offline = '0' AND loc2.offline = '1' AND loc1.activeWAP = 1 AND loc1.lat != '0.0' AND loc2.lat != '0.0'"
    elif request.query['dtype'] == 'undetected':
      query = "SELECT lat, lng FROM (SELECT l.lat as lat, l.lng AS lng, GROUP_CONCAT(l.ssid) AS grouped_ssid FROM locationdata AS l GROUP BY l.lat, l.lng HAVING grouped_ssid NOT LIKE '%GTwifi%') as T"
    else:
      return 'No query type'
    cur.execute(query)
    rows = cur.fetchall()
  except:
    return 'No query'
  output = []
  for row in rows:
    output.append(row)
  return json.dumps(output)

@route('/data/pathsmap')
def getDataPathsMap():
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT DISTINCT lat, lng FROM locationdata"
    cur.execute(query)
    rows = cur.fetchall()
  except:
    return 'No query'
  output = []
  for row in rows:
    output.append(row)
  return json.dumps(output)

@route('/data/individualsignalsmap')
def getIndividualSignalsMap():
  try:
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    query = "SELECT AVG(signal_level), lat, lng, imei FROM locationdata WHERE ssid = 'GTwifi' GROUP BY lat, lng, imei ORDER BY imei"
    querySize = "SELECT COUNT(DISTINCT imei) FROM locationdata WHERE lat != '0.0' AND ssid = 'GTwifi'"
    cur.execute(query)
    rows = cur.fetchall()
    cur.execute(querySize)
    rowsSize = cur.fetchall()
  except:
    return 'No query'
  iSignals = {}
  iSignals['number'] = rowsSize[0][0]
  points = []
  for row in rows:
    currISignal = {}
    currISignal['avg_signal'] = str(row[0])
    currISignal['lat'] = row[1]
    currISignal['lng'] = row[2]
    currISignal['imei'] = row[3]
    points.append(currISignal)
  iSignals['points'] = points
  return json.dumps(iSignals)

@route('/add')
def get_data():
    return 'No data passed'

@route('/add/offline', method = 'POST')
def post_data_offline():
    postdata = request.body.read()
    d = json.loads(postdata)
    print 'Request Received'
    for obj in d:
      wap_data = obj['wap_data']
      location_data = obj['location_data']
      user_data = obj['user_data']
    offline = 1
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    epoch = time.time()
    q = "INSERT INTO locationdata(timestamp, dblevel, lat, lng, location_comment, server_timestamp, imei, bssid, \
         ssid, signal_level, location_speed, activeWAP, uploadsize, timediff_sec, offline) VALUES(FROM_UNIXTIME("\
         + str(location_data['timestamp']/1000)+ ")," + str(location_data['db_level']) + ",'" + str(location_data['gps_lat'])\
         + "','" + str(location_data['gps_long']) + "','" + str(location_data['location_comment']) + "',FROM_UNIXTIME(" +str(epoch)+"),'" \
         +str(user_data['device_id'])+ "','" + str(wap_data['bssid'])+"','"+str(wap_data['ssid'])+"',"+str(location_data['signal_level'])+","+\
         str(location_data['location_speed'])+","+str(wap_data['is_active_connection'])+","+str(sys.getsizeof(postdata))+","+str(int(round(epoch*10)) - int(round(location_data['timestamp']/100)))+", " +str(offline) + ")"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    q = "INSERT INTO users(imei, phonedata) VALUES('"+ user_data['device_id']+ "','"+user_data['android_version']+" \
        "+user_data['phone_model']+"')"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    q = "INSERT INTO wap(ssid, bssid, capabilities, frequency) VALUES('"+str(wap_data['ssid']) + "','"+ str(wap_data['bssid']) \
      +"','"+ str(wap_data['capabilities']) +"',"+ str(wap_data['frequency'])+")"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    if con.open:
      pass

@route('/add', method = 'POST')
def post_data():
    postdata = request.body.read()
    d = json.loads(postdata)
    print 'Request Received'
    for obj in d:
      wap_data = obj['wap_data']
      location_data = obj['location_data']
      user_data = obj['user_data']
    offline = 0
    con = mdb.connect(host='localhost', user='indoor', passwd='indoor_location', db='mas')
    cur = con.cursor()
    epoch = time.time()
    q = "INSERT INTO locationdata(timestamp, dblevel, lat, lng, location_comment, server_timestamp, imei,\
         bssid, ssid, signal_level, location_speed, activeWAP, uploadsize, timediff_sec, offline) \
         VALUES(FROM_UNIXTIME(" + str(location_data['timestamp']/1000)+ ")," + str(location_data['db_level']) + ",'" + \
         str(location_data['gps_lat']) + "','" + str(location_data['gps_long']) + "','" + \
         str(location_data['location_comment']) + "',FROM_UNIXTIME(" +str(epoch)+"),'" +str(user_data['device_id'])+ "','" + \
         str(wap_data['bssid'])+"','"+str(wap_data['ssid'])+"',"+str(location_data['signal_level']) + "," + \
         str(location_data['location_speed'])+","+str(wap_data['is_active_connection'])+","+\
         str(sys.getsizeof(postdata))+","+str(int(round(epoch*10)) - int(round(location_data['timestamp']/100)))+", " +\
         str(offline) + ")"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    q = "INSERT INTO users(imei, phonedata) VALUES('"+ user_data['device_id']+ "','"+user_data['android_version']+" "+user_data['phone_model']+"')"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    q = "INSERT INTO wap(ssid, bssid, capabilities, frequency) VALUES('"+str(wap_data['ssid']) + "','"+ \
         str(wap_data['bssid']) +"','"+ str(wap_data['capabilities']) +"',"+ str(wap_data['frequency'])+")"
    try:
      cur.execute(q)
    except:
      pass
    con.commit()
    if con.open:
      pass

run(host='0.0.0.0',port=3050, reloader=True, debug=True, server='cherrypy')
