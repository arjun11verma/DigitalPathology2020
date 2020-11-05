import cv2
import numpy as np
import base64

def crop(img_arr, top, left, bottom, right):
    return img_arr[top:bottom, left:right]


class removeBlackSpace:
    num_images = 0
    stopRowTop = 0
    stopColLeft = 0
    stopRowBottom = 0
    stopColRight = 0
    processed = True
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

    def removeBlackSpace(self, img_url, img_name):
        if(isinstance(img_url, str)):
            slide_image = cv2.imread(img_url)
        else:
            slide_image = img_url
        
        limit = 30

        bin_img = cv2.cvtColor(slide_image, cv2.COLOR_BGR2GRAY)
        threshold, bin_img = cv2.threshold(bin_img, limit, 255, cv2.THRESH_BINARY)

        if(removeBlackSpace.processed):
            removeBlackSpace.stopRowTop = 0
            topFlag = True
            removeBlackSpace.stopColLeft = 0
            leftFlag = True
            removeBlackSpace.stopRowBottom = len(slide_image)
            bottomFlag = False
            removeBlackSpace.stopColRight = len(slide_image[0])
            rightFlag = False

            for row in range(len(bin_img)):
                if(topFlag and sum(bin_img[row]) > 10):
                    removeBlackSpace.stopRowTop = row
                    topFlag = False
                    bottomFlag = True
                elif(bottomFlag and sum(bin_img[row]) < 10):
                    removeBlackSpace.stopRowBottom = row
                    bottomFlag = False

                if(leftFlag and sum(bin_img[:, row]) > 10):
                    removeBlackSpace.stopColLeft = row
                    leftFlag = False
                    rightFlag = True
                elif(rightFlag and sum(bin_img[:, row]) < 10):
                    print(sum(bin_img[:, row]))
                    removeBlackSpace.stopColRight = row
                    rightFlag = False

                if(not(leftFlag or rightFlag or topFlag or bottomFlag)):
                    break
            
            removeBlackSpace.processed = False

        new_image = crop(slide_image, removeBlackSpace.stopRowTop, removeBlackSpace.stopColLeft,
                        removeBlackSpace.stopRowBottom, removeBlackSpace.stopColRight)
        
        removeBlackSpace.num_images += 1
        img_name = img_name + str(removeBlackSpace.num_images) + ".jpg"
        cv2.imwrite(img_name, new_image)

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

    def base64ToArray(self, img_data):
        im_bytes = base64.b64decode(img_data)
        im_arr = np.frombuffer(im_bytes, dtype=np.uint8)
        img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

        return img
