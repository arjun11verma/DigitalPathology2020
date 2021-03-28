# example from https://docs.opencv.org/master/d3/db4/tutorial_py_watershed.html
import numpy as np
import cv2 as cv
from matplotlib import pyplot as plt

def watershedSegment(img):
    gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    # using otsu thresholding to separate regions in the black & white image
    ret, thresh = cv.threshold(gray, 0, 255, cv.THRESH_BINARY_INV+cv.THRESH_OTSU)

    # removing noise using a kernel of ones
    kernel = np.ones((3, 3), np.uint8)
    # use morphological opening to remove small holes or white noise in the object
    opening = cv.morphologyEx(thresh, cv.MORPH_OPEN, kernel, iterations = 2)

    # finding the area of the image which is definitely background
    definite_bg = cv.dilate(opening, kernel, iterations = 3)

    # finding the area which is definitely foreground
    dist_transform = cv.distanceTransform(opening, cv.DIST_L2, 5)
    ret, definite_fg = cv.threshold(dist_transform, 0.7 * dist_transform.max(), 255, 0)
    definite_fg = np.uint8(definite_fg)
    unknown = cv.subtract(definite_bg, definite_fg)

    ret, markers = cv.connectedComponents(definite_fg)
    # add 1 to all labels to make the background 1
    markers = markers + 1
    markers[unknown==255] = 0

    markers = cv.watershed(img, markers)
    img[markers == -1] = [255, 0 , 0]

    # making foreground white
    markers[markers == 1] = 0
    markers[markers > 1] = 255

    kernel = np.ones((3, 3), np.uint8)
    dilation = cv.dilate(markers.astype(np.float32), kernel, iterations=1)

    final_img = cv.bitwise_and(img, img, mask=dilation.astype(np.uint8))
    # convert from bgr to rgb
    b, g, r = cv.split(final_img)
    final_img = cv.merge([r, g, b])

    plt.imshow(markers)
    plt.show()

    # f, axarr = plt.subplots(1,2)
    # axarr[0].imshow(markers)
    # axarr[1].imshow(img)
    # plt.show()
    #
    # result = opening
    return dist_transform