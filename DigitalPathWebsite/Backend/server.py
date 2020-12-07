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
# Definitely use one of the latter, this project has funding, so use it!

app = Flask(__name__)

mongoUri = "mongodb+srv://averma332:Timeline123#@digpath2020.mujb1.mongodb.net/digitalpath2020?retryWrites=true&w=majority"

cors = CORS(app)

mongo = PyMongo(app, uri = mongoUri)

images = mongo.db.ImageSet

@app.route('/login', methods = ['POST'])
def login():
   login_status = False
   return {'status': login_status}

@app.route('/acceptImages', methods = ['POST'])
def acceptImages():
   imgproc = removeBlackSpace()

   post_data = (literal_eval(request.data.decode('utf8')))

   img_list = []
   inner_list = []
   
   for i in range(len(post_data) - 4):
      img_list.append(imgproc.removeBlackSpace(imgproc.base64ToArray(post_data[str(i)]), post_data['name'], True))

   slide_image = imgproc.stitchImages(img_list)

   slide_image = slide_image + imgproc.sharpenImage(slide_image)

   imgproc.displayImage(slide_image)

   print("Regular stitching over!")

   partial_stitch = partialStitching(imgproc, img_list)

   print("Partial stitching over!") 

   username = post_data['username']
   name = post_data['name']
   slide_type = post_data['slide']
   cancer_type = post_data['cancer']
   time_stamp = (datetime.now()).strftime("%d/%m/%Y %H:%M:%S")
   stitched_image = imgproc.arrayToBase64(slide_image)

   #slide_image = partial_stitch if len(partial_stitch) > len(slide_image) else slide_image
   print(len(slide_image))

   if(len(slide_image) >= 1000):
      mongo_document = {'username': username, 'name': name, 'slide': slide_type, 'cancer': cancer_type, 'timestamp': time_stamp, 'image': stitched_image}
      print(images.insert_one(mongo_document).inserted_id)
      return {'response': "Data posted successfully!"}
   
   return {'response': "Data not posted right. You're gonna have to try again!"}

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

def partialStitching(imgproc, img_list):
   inner_list = []

   inner_step = int(len(img_list)/4)
   if(inner_step == 0): inner_step = 1
   slice_end = 0

   for i in range(0, len(img_list), inner_step):
      slice_end = i + inner_step + 1 if (i + inner_step + 1 < len(img_list)) else len(img_list)
      if(len(img_list[i:slice_end]) > 0):
         inner_list.append(img_list[i:slice_end])
   
   partial_stiches = []

   for img_data in inner_list:
      print("Partially stitched!")
      temp_slide_image = imgproc.stitchImages(img_data)
      if(temp_slide_image.size != 0): 
         imgproc.displayImage(temp_slide_image)
         partial_stiches.append(temp_slide_image)
   
   slide_image = imgproc.stitchImages(partial_stiches)

   slide_image = slide_image + imgproc.sharpenImage(slide_image)

   imgproc.displayImage(slide_image)

   print("Partial stitching over!")

   return slide_image

run_with_ngrok(app)
app.run()

   
   