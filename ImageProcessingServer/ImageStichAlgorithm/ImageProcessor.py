import cv2
import numpy as np
import base64
import os

def crop(img_arr, top, left, bottom, right):
    return img_arr[top:bottom, left:right]

class ImageProcessor:
    """Image processing class for converting image formats, stitching images together, applying filters and removing black space from microscope images"""
    stitcher = cv2.Stitcher.create()

    def __init__(self):
        self.num_images = 0
        self.stopRowTop = 0
        self.stopColLeft = 0
        self.stopRowBottom = 0
        self.stopColRight = 0
        self.processed = False
        self.divider = 0
        self.inner_len = 0
        """Intializer for the class"""

    def combineImages(self, image_list):
        return np.concatenate(image_list, axis=0)

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

        self.num_images += 1
        img_name = img_name + str(self.num_images) + ".jpg"

        path = 'C:\VSCode Projects\DigitalPathology\ImageProcessingServer\RecordedImages'
        if(save_image): cv2.imwrite(os.path.join(path, img_name), slide_image)

        return slide_image
    
    def twoDimConvolution(self, slide_image, kernel):
        """Performs a 2D Convlution on the image"""
        new_img = cv2.filter2D(slide_image, -1, kernel)
        return new_img
    
    def removeNoise(self, img_data):
        pass
    
    def sharpenImage(self, img_data, factor, increase):
        """Applies a typical medical image processing sharpening kernel to the image"""
        kernel_data = []
        factor = 1/factor

        dim = 3
        for i in range(dim*dim):
            kernel_data.append(-1/factor)

        kernel_data[int(dim*dim/2)] = (factor-1)/(factor) + increase

        sharpening_kernel = np.array(kernel_data).reshape((dim, dim))
    
        print(sharpening_kernel)
        
        return self.twoDimConvolution(img_data, sharpening_kernel)

    def stitchImages(self, slides):
        """Stitches together an array of images using the OpenCV Panorama stitcher"""
        (status, result) = ImageProcessor.stitcher.stitch(slides)

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

        return str(byte_list, 'utf-8')
    
    def pythonRemoveBlackSpace(self, slide_image): 
        """Removes the black space from a microscope slide image"""
        limit = 10

        bin_img = cv2.cvtColor(slide_image, cv2.COLOR_BGR2GRAY)

        threshold, bin_img = cv2.threshold(bin_img, limit, 1, cv2.THRESH_BINARY)

        if(not self.processed):
            self.stopRowTop = 0
            topFlag = True
            self.stopColLeft = 0
            leftFlag = True
            self.stopRowBottom = len(slide_image)
            bottomFlag = False
            self.stopColRight = len(slide_image[0])
            rightFlag = False

            for row in range(len(bin_img)):
                if(topFlag and sum(bin_img[row]) > 50):
                    self.stopRowTop = row
                    topFlag = False
                    bottomFlag = True
                elif(bottomFlag and sum(bin_img[row]) < 10):
                    self.stopRowBottom = row
                    bottomFlag = False

                if(leftFlag and sum(bin_img[:, row]) > 50):
                    self.stopColLeft = row
                    leftFlag = False
                    rightFlag = True
                elif(rightFlag and sum(bin_img[:, row]) < 10):
                    self.stopColRight = row
                    rightFlag = False

                if(not(leftFlag or rightFlag or topFlag or bottomFlag)):
                    break
            
            self.inner_len = ((self.stopColRight - self.stopColLeft)*(1.4142))/2
            divider = ((self.stopRowBottom - self.stopRowTop) - self.inner_len)/2
            self.divider = int(divider)

            self.processed = True

        new_image = crop(slide_image, self.stopRowTop, self.stopColLeft,
                            self.stopRowBottom, self.stopColRight)

        new_image = crop(new_image, self.divider, self.divider, self.divider + int(self.inner_len), self.divider + int(self.inner_len))

        return new_image
