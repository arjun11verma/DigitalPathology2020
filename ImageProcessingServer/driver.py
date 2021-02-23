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
import numpy as np
import cv2
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

data = images.find({'username': 'arjun@gmail.com'})

data = [(imgproc.base64ToArray(image['image'])) for image in data]

scale_factor = 0.20

dimesions = (int(len(data[0][0]) * scale_factor), int(len(data[0]) * scale_factor))

data = [cv2.resize(image, dimesions, interpolation=cv2.INTER_AREA) for image in data]

first_height = len(data[0])

final_image = np.concatenate((data[0], data[1]), axis=0)

height = len(final_image)
width = len(final_image[0])
black_pixel = np.zeros(3)
lower_bound = 0

print(f'Height: {height}, Width: {width}')

image_shift = 150

for image_row in range(first_height, height):
    for pixel in range(width):
        if (sum(final_image[image_row][pixel]) > 30):
            if (image_row - image_shift > lower_bound): lower_bound = image_row - image_shift
            final_image[image_row - image_shift][pixel] = final_image[image_row][pixel]
            final_image[image_row][pixel] = black_pixel

print(lower_bound)

print("Processed!")

final_image = final_image[:lower_bound]

imgproc.displayImage(final_image)


"""
images = []

name = 'William'

base_file_name = './RecordedImages/'

for i in range(1, 31):
    images.append(imgproc.removeBlackSpace(f'{base_file_name}{name}{i}.jpg', 'slide_image', False))

stitch_one = (imgproc.stitchImages(images))

imgproc.displayImage(stitch_one)

stitch_two = imgproc.removeBlackSpace(stitch_one, 'H&E Partial Stitch', True)
"""







