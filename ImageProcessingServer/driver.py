from ImageStichAlgorithm.ImageProcessor import ImageProcessor
import numpy as np

# This is a class for testing the image processing algorithms and verifying sample slide images. It is merely for testing purposes

imgproc = ImageProcessor() 

images = []

base_file_name = './RecordedImages/'

for i in range(1, 11):
    images.append(imgproc.removeBlackSpace(f'{base_file_name}Bill{i}.jpg', 'slide_image', False))

stitch_one = (imgproc.stitchImages(images))

sharpening_factor = 9

increase = 1.3

imgproc.displayImage(stitch_one)

imgproc.displayImage(imgproc.sharpenImage(stitch_one, sharpening_factor, increase))






