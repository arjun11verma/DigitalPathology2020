# Following example from https://digitalslidearchive.github.io/HistomicsTK/examples/color_deconvolution.html
import cv2
import matplotlib.pyplot as plt
import histomicstk as htk

import numpy as np
import scipy as sp

import skimage.io
import skimage.measure
import skimage.color

def supervisedDeconv(img):
    """Applies a supervised color deconvolution to digitally separate stains
    in a color histology image

    Args:
        img (np.array[] | string): A cancer slide image which has been stained
        to identify specific cellular structures (can be np array or url)

    Returns:
        stainImages (list): A list of tuples containing stain names and 
        deconvoluted images
    """
    selectAlpha = readImg(img)
    # prebuilt stain color map which describes the color characteristics of stains
    stain_color_map = htk.preprocessing.color_deconvolution.stain_color_map
    # guessing the stains in the input image
    stains = ['hematoxylin', 'eosin', 'null'] # nuclei stain, cytoplasm stain, null because only 2 stains

    # create stain matrix by making array of stain vectors and transposing
    W = np.array([stain_color_map[st] for st in stains]).T 

    # supervised color deconvolution
    deconvolved = htk.preprocessing.color_deconvolution.color_deconvolution(selectAlpha, W)
    # showing deconvolved images with different stains
    stainImages = [(stain, deconvolved.Stains[:, :, i]) for (i, stain) in enumerate(stains)]
    return stainImages

def smnfDeconv(img, sparsity_factor):
    selectAlpha = readImg(img)
    I_0 = 255
    im_sda = htk.preprocessing.color_conversion.rgb_to_sda(selectAlpha, I_0)
    # compute estimate for stain matrix using Sparse Non-negative Matrix Factorization
    W_init = initialStainMatrix()
    W_est = htk.preprocessing.color_deconvolution.separate_stains_xu_snmf(
        im_sda, W_init, sparsity_factor
    )
    deconvolvedGuess = htk.preprocessing.color_deconvolution.color_deconvolution(
        selectAlpha,
        # normalizes the stain matrix by filling in empty columns
        htk.preprocessing.color_deconvolution.complement_stain_matrix(W_est),
        I_0
    )
    stainImages = [(stain, deconvolved.Stains[:, :, i]) for (i, stain) in enumerate(stains)]
    return stainImages

def initialStainMatrix():
    stain_color_map = htk.preprocessing.color_deconvolution.stain_color_map
    # guessing the stains in the input image
    stains = ['hematoxylin', 'eosin', 'null'] # nuclei stain, cytoplasm stain, null because only 2 stains
    # create stain matrix by making array of stain vectors and transposing
    W = np.array([stain_color_map[st] for st in stains]).T 
    # create stain matrix which is the initial guess at the stain1111111111111111111111111111111111111111111111111111111111111111111111111232 vectors
    W_init = W[:, :2]
    return W_init


def pcaDeconv(img):
    W_init = initialStainMatrix(img)


def readImg(img):
    # Loading the image if it is a url
    if (isinstance(img, str)):
       selectAlpha = skimage.io.imread(img)
    selectAlpha = img[:, :, :3] # selecting all of the alpha channels from RGB image
    return selectAlpha