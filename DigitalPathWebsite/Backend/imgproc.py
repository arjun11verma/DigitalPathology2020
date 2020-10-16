import numpy as np
from matplotlib import pyplot

class imgproc:
    def __init__(self):
        pass
    
    @staticmethod
    def stichImages(img_data):
        return 0

    @staticmethod
    def linearFilter(img):
        return 0

    @staticmethod
    def toArray(img_data):
        array = np.frombuffer(img_data, dtype=np.uint8)

        clean_arr = []
        
        for i in array:
            if(i > 20 and i < 240):
                clean_arr.append(i)

        return np.expand_dims(clean_arr, axis=0)
