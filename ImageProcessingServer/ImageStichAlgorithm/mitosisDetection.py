# uses API from https://github.com/IBM/MAX-Breast-Cancer-Mitosis-Detector
# to set up the mitosis detection server on localhost:5000, run the following docker command:
# docker run -it -p 5000:5000 quay.io/codait/max-breast-cancer-mitosis-detector
import os
import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
from typing import List
import requests as req
import numpy as np
import cv2

port = 5000
currDir = os.getcwd()
# The mitosis prediction API takes 64x64 .png images
predictImgHeight = 64
predictImgWidth = 64
analyisPath = 'AnalysisImages'

def croppedMitosisProb(croppedFilePath: str) -> float:
    files = {'image': open(croppedFilePath, 'rb')}
    postUrl = "http://localhost:{port}/model/predict".format(port=port)
    predictResponse = req.post(postUrl, files=files, verify=False)
    predictionProb = predictResponse.json()['predictions'][0]['probability']
    return predictionProb

def mitosisProb(stitchedFilePath: str) -> List[List[float]]:
    stitchedImg = cv2.imread(stitchedFilePath)
    height, width, channel = stitchedImg.shape
    numCols = width // predictImgWidth
    numRows = height // predictImgHeight
    mitosisProbGrid: np.ndarray = np.zeros([numCols, numRows], dtype=np.float64)
    for x in range(numCols-1):
        for y in range(numRows-1):
            croppedImg = stitchedImg[y*predictImgHeight:(y+1)*predictImgHeight, x*predictImgWidth:(x+1)*predictImgWidth]
            croppedFileName = "croppedImgx{x}y{y}.png".format(x=x, y=y)
            croppedPath = os.path.join(analyisPath, croppedFileName)
            # writes the cropped image into the analysis folder
            cv2.imwrite(croppedPath, croppedImg)
            prob = croppedMitosisProb(croppedPath)
            mitosisProbGrid[x][y] = prob
            try:
                os.remove(croppedPath)
            except:
                pass

    return mitosisProbGrid