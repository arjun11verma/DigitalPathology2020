import json
from ast import literal_eval
from bson import ObjectId

from flask import Flask
from flask import request
from flask_cors import CORS, cross_origin
from flask_pymongo import PyMongo
from flask_ngrok import run_with_ngrok

from removeblackspace import ImageProcessing

# Use Heroku to deploy this, or maybe Google Cloud or Google Cloud App Engine, or maybe Amazon EC2

app = Flask(__name__)

mongoUri = "mongodb+srv://averma332:Timeline123#@digpath2020.mujb1.mongodb.net/DigitalPathDB?retryWrites=true&w=majority"

cors = CORS(app)

mongo = PyMongo(app, uri = mongoUri)

images = mongo.db.ImageSet

imgproc = ImageProcessing()

@app.route('/returnImages', methods = ['POST'])
def returnImages():
   post_data = (literal_eval(request.data.decode('utf8')))
   index = int(post_data['index'])

   data = images.find_one_or_404({"name": "testuserone"})
   base_data = data["imageObjects"]

   img = base_data[index]
   rendered_image = img['image']

   return rendered_image

@app.route('/acceptImages', methods = ['POST'])
def acceptImages():
   post_data = (literal_eval(request.data.decode('utf8')))
   img_list = []
   
   for i in range(len(post_data) - 3):
      img_list.append(imgproc.base64ToArray(post_data[str(i)]))
   
   for img in img_list:
      img = imgproc.removeBlackSpace(img)
   
   slide_image = imgproc.stitchImages(img_list)

   imgproc.displayImage(slide_image)
   
   return "Data posted successfully!"

run_with_ngrok(app)
app.run()

   
   