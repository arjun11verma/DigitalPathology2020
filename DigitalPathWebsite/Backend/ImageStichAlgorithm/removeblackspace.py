import cv2
import numpy as np
import base64
import os

def crop(img_arr, top, left, bottom, right):
    return img_arr[top:bottom, left:right]

class removeBlackSpace:
    """Image processing class for converting image formats, stitching images together, applying filters and removing black space from microscope images"""
    num_images = 0
    stopRowTop = 0
    stopColLeft = 0
    stopRowBottom = 0
    stopColRight = 0
    processed = False
    divider = 0
    inner_len = 0
    stitcher = cv2.Stitcher.create()

    def __init__(self):
        """Intializer for the class"""
        pass

    def displayImage(self, img_url):
        """Displays an image from either a filepath or an Numpy array"""
        if(isinstance(img_url, str)):
            slide_image = cv2.imread(img_url)
        else:
            slide_image = img_url

        cv2.imshow("Slide Image", slide_image)
        cv2.waitKey(0)

    def removeBlackSpace(self, img_url, img_name, save_image):
        """Converts an image to Numpy and saves it if the option is selected. Removes black space around microscope image if neccessary"""
        if(isinstance(img_url, str)):
            slide_image = cv2.imread(img_url)
        else:
            slide_image = img_url

        removeBlackSpace.num_images += 1
        img_name = img_name + str(removeBlackSpace.num_images) + ".jpg"

        path = 'C:\VSCode Projects\DigitalPathology2020\DigitalPathWebsite\Backend\RecordedImages'
        if(save_image): cv2.imwrite(os.path.join(path, img_name), slide_image)

        return slide_image
    
    def twoDimConvolution(self, slide_image, kernel):
        """Performs a 2D Convlution on the image"""
        new_img = cv2.filter2D(slide_image, -1, kernel)
        return new_img
    
    def sharpenImage(self, img_data):
        """Applies a typical medical image processing sharpening kernel to the image"""
        kernel_data = []

        dim = 3
        factor = 10
        for i in range(dim*dim):
            kernel_data.append(-1/factor)

        kernel_data[int(dim*dim/2)] = (factor-1)/(factor)

        sharpening_kernel = np.array(kernel_data).reshape((dim, dim))
        
        return cv2.filter2D(img_data, -1, sharpening_kernel)

    def stitchImages(self, slides):
        """Stitches together an array of images using the OpenCV Panorama stitcher"""
        (status, result) = removeBlackSpace.stitcher.stitch(slides)

        if(status == cv2.STITCHER_OK):
            return result
        else:
            return np.array([])

    def base64ToArray(self, img_data):
        """Converts an image from Base64 to a Numpy array"""
        im_bytes = base64.b64decode(img_data)
        im_arr = np.frombuffer(im_bytes, dtype=np.uint8)
        img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

        return img
    
    def arrayToBase64(self, img_data):
        """Converts an image from a Numpy array to Base64"""
        success, img_data = cv2.imencode(".jpg", img_data)

        byte_list = []
        for i in range(len(img_data)):
            byte_list.append((img_data[i])[0])
        
        byte_list = np.array(byte_list)
        byte_list = byte_list.tobytes()
        byte_list = base64.b64encode(byte_list)

        return byte_list
    
    def pythonRemoveBlackSpace(self, slide_image): 
        """Removes the black space from a microscope slide image"""
        limit = 10

        bin_img = cv2.cvtColor(slide_image, cv2.COLOR_BGR2GRAY)

        threshold, bin_img = cv2.threshold(bin_img, limit, 1, cv2.THRESH_BINARY)

        if(not removeBlackSpace.processed):
            removeBlackSpace.stopRowTop = 0
            topFlag = True
            removeBlackSpace.stopColLeft = 0
            leftFlag = True
            removeBlackSpace.stopRowBottom = len(slide_image)
            bottomFlag = False
            removeBlackSpace.stopColRight = len(slide_image[0])
            rightFlag = False

            for row in range(len(bin_img)):
                if(topFlag and sum(bin_img[row]) > 50):
                    removeBlackSpace.stopRowTop = row
                    topFlag = False
                    bottomFlag = True
                elif(bottomFlag and sum(bin_img[row]) < 10):
                    removeBlackSpace.stopRowBottom = row
                    bottomFlag = False

                if(leftFlag and sum(bin_img[:, row]) > 50):
                    removeBlackSpace.stopColLeft = row
                    leftFlag = False
                    rightFlag = True
                elif(rightFlag and sum(bin_img[:, row]) < 10):
                    removeBlackSpace.stopColRight = row
                    rightFlag = False

                if(not(leftFlag or rightFlag or topFlag or bottomFlag)):
                    break
            
            removeBlackSpace.inner_len = ((removeBlackSpace.stopColRight - removeBlackSpace.stopColLeft)*(1.4142))/2
            divider = ((removeBlackSpace.stopRowBottom - removeBlackSpace.stopRowTop) - removeBlackSpace.inner_len)/2
            removeBlackSpace.divider = int(divider)

            removeBlackSpace.processed = True

        new_image = crop(slide_image, removeBlackSpace.stopRowTop, removeBlackSpace.stopColLeft,
                            removeBlackSpace.stopRowBottom, removeBlackSpace.stopColRight)

        new_image = crop(new_image, removeBlackSpace.divider, removeBlackSpace.divider, removeBlackSpace.divider + int(removeBlackSpace.inner_len), removeBlackSpace.divider + int(removeBlackSpace.inner_len))

        return new_image
