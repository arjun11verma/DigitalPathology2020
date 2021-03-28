from ImageStichAlgorithm.ImageProcessor import ImageProcessor
from ImageStichAlgorithm.mitosisDetection import mitosisProb
from ImageStichAlgorithm.watershedSeg import watershedSegment
import numpy as np
import cv2
import os

# This is a class for testing the image processing algorithms and verifying sample slide images. It is merely for testing purposes

imgproc = ImageProcessor() 

images = []

base_file_name = './RecordedImages/'
analyisPath = './AnalysisImages'
# for i in range(1, 20):
#     images.append(imgproc.removeBlackSpace(f'{base_file_name}a{i}.jpg', 'slide_image', False))
#
# stitch_one = (imgproc.stitchImages(images))
#
# sharpening_factor = 9
#
# increase = 1.3
#
# stitched = cv2.imread('stitchedImage2.png')
# print(type(stitched))
# cropped = stitched[300:364, 300:364]
# cv2.imwrite('croppedImgsmall.png', cropped)

# imgproc.displayImage(stitch_one)

# Code which applies canny edge detection
# edges = imgproc.imageToEdgeMap(stitch_one)
# imgproc.displayImage(edges)

# Plots the color graph
# imgproc.colorGraph(stitch_one)

# imgproc.displayImage(imgproc.sharpenImage(stitch_one, sharpening_factor, increase))
# stitched = cv2.imread('stitchedImage2.jpg')
# supDeconv = supervisedDeconv(stitched)
# # 0.5 is a standard sparsity factor
# unSupDeconv = smnfDeconv(stitched, 0.5)
# segmented = watershedSegment(stitched)
# imgproc.displayImage(segmented)

# print(mitosisProb(os.path.join(base_file_name, 'croppedImgsmall.png')))
print(mitosisProb('stitchedImage2.png'))




