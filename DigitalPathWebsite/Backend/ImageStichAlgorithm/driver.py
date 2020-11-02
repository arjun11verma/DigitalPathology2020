from removeblackspace import removeBlackSpace as rbs
import cv2 
import numpy as np

imgproc = rbs()

images = []

base_file_name = './DigPathSlideImages/'

for i in range(2, 18):
    images.append(imgproc.removeBlackSpace(base_file_name + 'download (' + str(i) + ').jpg'))

stitch_one = (imgproc.stitchImages(images))

kernel_data = []

dim = 3
factor = 10
for i in range(dim*dim):
    kernel_data.append(-1/factor)

kernel_data[int(dim*dim/2)] = (factor-1)/(factor)

sharpening_kernel = np.array(kernel_data).reshape((dim, dim))

print(sharpening_kernel)

imgproc.displayImage(stitch_one + imgproc.twoDimConvolution(stitch_one, sharpening_kernel))






