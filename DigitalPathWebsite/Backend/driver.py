from ImageStichAlgorithm.removeblackspace import removeBlackSpace
import cv2 
import numpy as np

imgproc = removeBlackSpace()

images = []

base_file_name = './ImageStichAlgorithm/DigPathSlideImages/'

for i in range(2, 18):
    images.append(imgproc.removeBlackSpace(base_file_name + 'download (' + str(i) + ').jpg', 'name', False))
    imgproc.displayImage(images[i - 2])

stitch_one = (imgproc.stitchImages(images))

imgproc.displayImage(stitch_one)






