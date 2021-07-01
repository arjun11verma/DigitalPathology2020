import json
from ast import literal_eval
from bson import ObjectId

from datetime import datetime
from time import time

from flask import Flask
from flask import request
from flask_cors import CORS, cross_origin
from flask_pymongo import PyMongo
from flask_ngrok import run_with_ngrok

import boto3  

from ImageStichAlgorithm.ImageProcessor import ImageProcessor
from APIKEYS import MONGODB_KEY, S3_ACCESS_KEY, S3_SECRET_KEY

# Deliverables for CS side

# Improve post processing (sharpening, brightness, various other kernels and whatnot)

# Finalize slide image acquisition process (lots of potential to make it more efficient as well)

app = Flask(__name__)
cors = CORS(app) # Sets my Cross Origin Access Header to Allow Access so my phone can send data to my server
mongo = PyMongo(app, uri = MONGODB_KEY) # Connects my Flask server to my MongoDB database
images = mongo.db.ImageSet
imgproc = ImageProcessor()
held_images = {}

s3 = boto3.client(
   's3',
   aws_access_key_id=S3_ACCESS_KEY,
   aws_secret_access_key=S3_SECRET_KEY
)

def mongo_upload(mongo_document):
   return images.insert_one(mongo_document)

def s3_upload(bucket_name, username, slide_id, mongo_id, image_data):
   s3_path = username + "/" + slide_id + "/" + mongo_id
   s3.Bucket(bucket_name).put_object(Key=s3_path, Body=image_data)

@app.route('/uploadImage', methods = ['POST'])
def uploadImage():
   post_data = (literal_eval(request.data.decode('utf8')))
   slide_id = post_data['slide_id']
   time_marker = post_data['time_marker']
   response = "N"

   if (post_data['status'] == "Y"):
      image_documents = held_images.get(slide_id, None)
      if image_documents: image_documents = image_documents.get(time_marker, None)

      mongo_id = mongo_upload(image_documents[0])
      s3_upload("whole-slide-images", image_documents[0]['username'], image_documents[0]['slide_id'], mongo_id, image_documents[1])

      if True: {'response': "Y"} 

   if (slide_id in held_images): del held_images[slide_id]
   return {'response': response}

@app.route('/acceptImages', methods = ['POST'])
def acceptImages():
   """Accepts Images in JSON format, stitches them together using OpenCV and uploads the stitched image to MongoDB. Returns the status of the processing/upload."""
   post_data = (literal_eval(request.data.decode('utf8')))

   if post_data['name'] in held_images: held_images.pop(post_data['name'])

   img_list = [imgproc.base64ToArray(post_data[str(i)]) for i in range(len(post_data) - 4)]

   whole_slide_image = imgproc.stitchImages(img_list)

   whole_slide_image = imgproc.sharpenImage(whole_slide_image, 0.5, 4.7)
   
   username = post_data['username']
   slide_id = post_data['slide_id']
   slide_type = post_data['slide']
   cancer_type = post_data['cancer']
   time_stamp = (datetime.now()).strftime("%m/%d/%Y %H:%M:%S")

   upload_document = {'username': username, 'slide_id': slide_id, 'slide': slide_type, 'cancer': cancer_type, 'timestamp': time_stamp, 'diagnosis': "N"}

   if not held_images[slide_id]: held_images[slide_id] = {time_stamp: [upload_document, whole_slide_image]}
   else: held_images[slide_id][time_stamp] = [upload_document, whole_slide_image]

   return {'response': "Y", 'imageData': whole_slide_image, "timeStamp": time_stamp}

def main():
   app.run(port=4000) # Runs the server


   # https://aws.amazon.com/premiumsupport/knowledge-center/decrypt-kms-encrypted-objects-s3/
   # https://boto3.amazonaws.com/v1/documentation/api/latest/index.html look at credentials section 

   # bucket_name = "digital-pathology-slide-images"
   # s3_path = "/"
   # image_data = {"test": "test"}
   # image_data = json.dumps(image_data)
   # s3.Bucket(bucket_name).put_object(Key=s3_path, Body=image_data)

if __name__ == "__main__":
   main()

   
   