from urllib2 import urlopen
import json
import statsmodels.api as sm
import pandas
from datetime import datetime
import time
from sqlalchemy import create_engine    #SQLAlchemy might need to be installed and PyMYSQL
import calendar


def create_connection():
    engine = create_engine('mysql+pymysql://aashu:aashu@localhost/apidata', connect_args= dict(host='127.0.0.1'),echo=False)
    cnxn = engine.raw_connection()
    cursor = cnxn.cursor()
    return cursor, cnxn


cursor, cnxn = create_connection()


def predict ():
    curr_timestamp = time.time()
    proper_curr_format =  datetime.fromtimestamp(curr_timestamp)
    sum_occupancy = 0;
    for x in range(0,7):
        d = datetime(year=2015, month=10, day=(9+x), hour=proper_curr_format.hour, minute=proper_curr_format.minute)
        prev_timestamp = calendar.timegm(d.utctimetuple())
        cursor.execute("""select maxoccupancy from bid_data where timestamp = (%s)""",(prev_timestamp))
        for (maxoccupancy) in cursor:
            sum_occupancy += maxoccupancy[0]
            
    return int(sum_occupancy / 7)
