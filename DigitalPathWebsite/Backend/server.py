import matplotlib
matplotlib.use('Agg')
from matplotlib import image
from matplotlib import pyplot

import json
import base64
from bson import ObjectId

from imgproc import imgproc as imgpr
from PIL import Image

from flask import Flask
from flask import request
from flask_cors import CORS, cross_origin
from flask_pymongo import PyMongo

from ast import literal_eval

app = Flask(__name__)

mongoUri = "mongodb+srv://averma332:Timeline123#@digpath2020.mujb1.mongodb.net/DigitalPathDB?retryWrites=true&w=majority"

cors = CORS(app)

mongo = PyMongo(app, uri = mongoUri)

images = mongo.db.ImageSet

@app.route('/processImages', methods = ['POST'])
def processImages():
   post_data = (literal_eval(request.data.decode('utf8')))
   name = str(post_data['name'])

   data = images.find_one_or_404({"name": name})
   base_data = data["imageObjects"]

   img_data = []

   for img in base_data:
      img_data.append(imgpr.toArray(img['image']))
   
   pyplot.imshow(img_data[0]) #not working for some reason, I might have to do some stuff
   
   return "Okay, you made a POST request. You're cool now."

@app.route('/returnImages', methods = ['POST'])
def returnImages():
   post_data = (literal_eval(request.data.decode('utf8')))
   index = int(post_data['index'])

   data = images.find_one_or_404({"name": "testuserone"})
   base_data = data["imageObjects"]

   img = base_data[index]
   rendered_image = img['image']

   return base64.b64encode(rendered_image)

   

   
   