# uses API from https://github.com/IBM/MAX-Nucleus-Segmenter
# to set up the nuclei segmentation server on localhost:6000, run the following docker command:
# docker run -it -p 6000:6000 quay.io/codait/max-nucleus-segmenter
import os
import urllib3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
from typing import List
import requests as req
import numpy as np
import cv2

port = 6000
currDir = os.getcwd()
# The nuclei segmentation API takes 64x64 .png images
predictImgHeight = 256
predictImgWidth = 256
analyisPath = 'AnalysisImages'


def croppedNucleiMask(croppedFilePath: str, xPos: int, yPos: int):
    masks = []
    files = {'image': open(croppedFilePath, 'rb')}
    postUrl = "http://localhost:{port}/model/predict".format(port=port)
    predictResponse = req.post(postUrl, files=files, verify=False)
    predictions = predictResponse.json()['predictions']
    for p in predictions:
        masks.append(p)


def nucleiMask(stitchedFilePath: str) -> List[List[float]]:
    stitchedImg = cv2.imread(stitchedFilePath)
    height, width, channel = stitchedImg.shape
    numCols = width // predictImgWidth
    numRows = height // predictImgHeight
    for x in range(numCols - 1):
        for y in range(numRows - 1):
            croppedImg = stitchedImg[y * predictImgHeight:(y + 1) * predictImgHeight,
                         x * predictImgWidth:(x + 1) * predictImgWidth]
            croppedFileName = "nucleiCroppedImgx{x}y{y}.png".format(x=x, y=y)
            croppedPath = os.path.join(analyisPath, croppedFileName)
            # writes the cropped image into the analysis folder
            cv2.imwrite(croppedPath, croppedImg)

            try:
                os.remove(croppedPath)
            except:
                pass

