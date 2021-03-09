from ImageStichAlgorithm.ImageProcessor import ImageProcessor
import numpy as np
from ImageStichAlgorithm.colorDeconvolution import supervisedDeconv, smnfDeconv
import cv2

# This is a class for testing the image processing algorithms and verifying sample slide images. It is merely for testing purposes

imgproc = ImageProcessor() 

images = []

base_file_name = './RecordedImages/'

# for i in range(1, 20):
#     images.append(imgproc.removeBlackSpace(f'{base_file_name}a{i}.jpg', 'slide_image', False))

# stitch_one = (imgproc.stitchImages(images))

sharpening_factor = 9

increase = 1.3

# cv2.imwrite('stitchedImage2.jpg', stitch_one)

# imgproc.displayImage(stitch_one)

# Code which applies canny edge detection
# edges = imgproc.imageToEdgeMap(stitch_one)
# imgproc.displayImage(edges)

# Plots the color graph
# imgproc.colorGraph(stitch_one)

# imgproc.displayImage(imgproc.sharpenImage(stitch_one, sharpening_factor, increase))
stitched = cv2.imread('stitchedImage2.jpg')
supDeconv = supervisedDeconv(stitched)
# 0.5 is a standard sparsity factor
unSupDeconv = smnfDeconv(stitched, 0.5)
# imgproc.displayImage(supervisedDeconv(cv2.imread('stitchedImage2.jpg')))




