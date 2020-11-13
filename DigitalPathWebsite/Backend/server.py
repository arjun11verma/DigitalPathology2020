import json
from ast import literal_eval
from bson import ObjectId

from datetime import datetime

from flask import Flask
from flask import request
from flask_cors import CORS, cross_origin
from flask_pymongo import PyMongo
from flask_ngrok import run_with_ngrok

from ImageStichAlgorithm.removeblackspace import removeBlackSpace
import imutils
import cv2
import numpy as np

# Use Heroku to deploy this, or maybe Google Cloud or Google Cloud App Engine, or maybe Amazon EC2

app = Flask(__name__)

mongoUri = "mongodb+srv://averma332:Timeline123#@digpath2020.mujb1.mongodb.net/digitalpath2020?retryWrites=true&w=majority"

cors = CORS(app)

mongo = PyMongo(app, uri = mongoUri)

images = mongo.db.ImageSet

@app.route('/acceptImages', methods = ['POST'])
def acceptImages():
   imgproc = removeBlackSpace()

   post_data = (literal_eval(request.data.decode('utf8')))

   img_list = []
   
   for i in range(len(post_data) - 4):
      img_list.append(imgproc.removeBlackSpace(imgproc.base64ToArray(post_data[str(i)]), post_data['name'], True))
   
   slide_image = imgproc.stitchImages(img_list)

   slide_image = slide_image + imgproc.sharpenImage(slide_image)

   imgproc.displayImage(slide_image)

   username = post_data['username']
   name = post_data['name']
   slide_type = post_data['slide']
   cancer_type = post_data['cancer']
   time_stamp = (datetime.now()).strftime("%d/%m/%Y %H:%M:%S")
   stitched_image = imgproc.arrayToBase64(slide_image)

   if(len(slide_image) < 10000):
      rand = 100
      #mongo_document = {'username': username, 'name': name, 'slide': slide_type, 'cancer': cancer_type, 'timestamp': time_stamp, 'image': stitched_image}
      #print(images.insert_one(mongo_document).inserted_id)
   
   return "Data posted successfully!"

@app.route('/displayImages', methods = ['POST'])
def displayImages():
   post_data = (literal_eval(request.data.decode('utf8')))
   username = post_data['username']

   data = images.find_one_or_404({"username": username})
   img_data = data["image"]

   data = list(images.find({"username": username}))
   img_data_new = []

   for img in data:
      img_data_new.append(str(img["image"], 'utf-8'))

   return {"image_list": img_data_new}

run_with_ngrok(app)
app.run()

   
   