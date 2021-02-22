import json
from ast import literal_eval
from bson import ObjectId

from datetime import datetime

from flask import Flask
from flask import request
from flask_cors import CORS, cross_origin
from flask_pymongo import PyMongo
from flask_ngrok import run_with_ngrok

from ImageStichAlgorithm.ImageProcessor import ImageProcessor
from APIKEYS import MONGODB_KEY

# Deliverables for CS side

# Improve post processing (sharpening, brightness, various other kernels and whatnot)

# Finalize slide image acquisition process (lots of potential to make it more efficient as well)

app = Flask(__name__)

mongoUri = MONGODB_KEY

cors = CORS(app) # Sets my Cross Origin Access Header to Allow Access so my phone can send data to my server

mongo = PyMongo(app, uri = mongoUri) # Connects my Flask server to my MongoDB database

images = mongo.db.ImageSet

imgproc = ImageProcessor()

@app.route('/acceptImages', methods = ['POST'])
def acceptImages():
   """Accepts Images in JSON format, stitches them together using OpenCV and uploads the stitched image to MongoDB. Returns the status of the processing/upload."""
   post_data = (literal_eval(request.data.decode('utf8')))

   img_list = [imgproc.removeBlackSpace(imgproc.base64ToArray(post_data[str(i)]), post_data['name'], False) for i in range(len(post_data) - 4)]

   slide_image = imgproc.stitchImages(img_list)

   slide_image = imgproc.sharpenImage(slide_image, 0.5, 4.7)
   
   username = post_data['username']
   name = post_data['name']
   slide_type = post_data['slide']
   cancer_type = post_data['cancer']
   time_stamp = (datetime.now()).strftime("%m/%d/%Y %H:%M:%S")
   stitched_image = imgproc.arrayToBase64(slide_image)

   #previous_image_set = images.find_one_or_404({'username': username, 'cancer': cancer_type, 'slide': slide_type})

   #if (previous_image_set):
   #   pass
   #else:
   imgproc.removeBlackSpace(slide_image, 'slide_image_data', False)
   mongo_document = {'username': username, 'name': name, 'slide': slide_type, 'cancer': cancer_type, 'timestamp': time_stamp, 'image': stitched_image, 'diagnosis': "N"}
   print(images.insert_one(mongo_document).inserted_id)

   return {'response': "Data posted successfully!", 'imageData': stitched_image}

@app.route('/displayImages', methods = ['POST'])
def displayImages():
   """Checks MongoDB for images based on specifications given in the POST call and returns those images as Base64 Strings in JSON"""
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
   """Stitches together images in chunks then stiches the chunks together. This algorithm is typically less effective than regular stitching."""
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

run_with_ngrok(app) # Configures the server with Ngrok, a localhost tunneling service that allows my phone to send data to an instance of the server running on a computer
app.run() # Runs the server

   
   