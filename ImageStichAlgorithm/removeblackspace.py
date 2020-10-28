import cv2
import numpy as np

def crop(img_arr, top, left, bottom, right):
    return img_arr[top:bottom, left:right]

class removeBlackSpace:
    shared_length = 0
    stitcher = cv2.Stitcher.create()

    def __init__(self):
        pass

    def displayImage(self, img_url):
        if(isinstance(img_url, str)):
            slide_image = cv2.imread(img_url)
        else:
            slide_image = img_url

        cv2.imshow("Slide Image", slide_image)
        cv2.waitKey(0)

    def removeBlackSpace(self, img_url):
        if(isinstance(img_url, str)):
            slide_image = cv2.imread(img_url)
        else:
            slide_image = img_url
        
        minimum_shade = 3.2 * len(slide_image)

        stopRowTop = 0
        stopColLeft = 0
        for a in range(len(slide_image)):
            if(sum([sum(i) for i in slide_image[a]]) > minimum_shade):
                stopRowTop = a
                break
        
        for a in range(len(slide_image[0])):
            if(sum([sum(slide_image[i][a]) for i in range(len(slide_image))]) > minimum_shade):
                stopColLeft = a
                break
        
        stopRowBottom = len(slide_image)
        stopColRight = len(slide_image[0])
        for a in reversed(range(len(slide_image))):
            if(sum([sum(i) for i in slide_image[a]]) > minimum_shade):
                stopRowBottom = a
                break
        
        for a in reversed(range(len(slide_image[0]))):
            if(sum([sum(slide_image[i][a]) for i in range(len(slide_image))]) > minimum_shade):
                stopColRight = a
                break

        new_image = crop(slide_image, stopRowTop, stopColLeft, stopRowBottom, stopColRight)

        return new_image
    
    def stitchImages(self, slides):
        (status, result) = removeBlackSpace.stitcher.stitch(slides)

        if(status == cv2.STITCHER_OK):
            return result
        else:
            return 0
    
    def twoDimConvolution(self, slide_image, kernel):
        new_img = cv2.filter2D(slide_image, -1, kernel)
        return new_img

    
    
