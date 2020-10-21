import cv2
import numpy as np
import matplotlib as plot
from matplotlib import image
from matplotlib import pyplot

def crop(img_arr, top, left, bottom, right):
    return img_arr[top:bottom, left:right]

class removeBlackSpace:
    shared_length = 0

    def __init__(self):
        pass

    def displayImage(self, img_url):
        if(isinstance(img_url, str)):
            slide_image = image.imread(img_url)
        else:
            slide_image = img_url
        pyplot.imshow(slide_image)
        pyplot.show()

    def removeBlackSpace(self, img_url):
        minimum_shade = 20
        slide_image = image.imread(img_url)

        stopRowTop = 0
        stopColLeft = 0
        topFlag = False
        leftFlag = False
        for a in range(len(slide_image)):
            for i in range(len(slide_image[a])):
                if(not(topFlag) and a != 0 and sum(((slide_image[a])[i])[np.nonzero((slide_image[a])[i])]) > minimum_shade):
                    stopRowTop = a
                    topFlag = True
                if(not(leftFlag) and a != 0 and sum(((slide_image[i])[a])[np.nonzero((slide_image[i])[a])]) > minimum_shade):
                    stopColLeft = a
                    leftFlag = True
            if(topFlag and leftFlag):
                break
        
        stopRowBottom = len(slide_image)
        stopColRight = len(slide_image[0])
        topFlag = False
        leftFlag = False
        for a in reversed(range(len(slide_image))):
            for i in range(len(slide_image[a])):
                if(not(topFlag) and a != len(slide_image) and sum(((slide_image[a])[i])[np.nonzero((slide_image[a])[i])]) > minimum_shade):
                    stopRowBottom = a
                    topFlag = True
                if(not(leftFlag) and a != len(slide_image[0]) and sum(((slide_image[i])[a])[np.nonzero((slide_image[i])[a])]) > minimum_shade):
                    stopColRight = a
                    leftFlag = True
            if(topFlag and leftFlag):
                break
        
        width_radius = stopColRight - stopColLeft
        height_radius = stopRowBottom - stopRowTop

        if(removeBlackSpace.shared_length == 0):
            radius = height_radius if width_radius > height_radius else width_radius
            radius = radius/2
            length = (radius/np.sqrt(2))
            removeBlackSpace.shared_length = length

        start_left = stopColLeft + int(width_radius/2) - int(removeBlackSpace.shared_length)
        start_top = stopRowTop + int(height_radius/2) - int(removeBlackSpace.shared_length)

        new_image = crop(slide_image, start_top, start_left, start_top + int(removeBlackSpace.shared_length*2), start_left + int(removeBlackSpace.shared_length*2))

        print(new_image.shape)
        print(int(removeBlackSpace.shared_length * 2))

        return new_image

    
    
