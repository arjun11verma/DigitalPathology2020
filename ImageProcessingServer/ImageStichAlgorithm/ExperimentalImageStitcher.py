import cv2
import numpy as np
from time import time 
import sys

brief = cv2.xfeatures2d.BriefDescriptorExtractor_create()

bf_matcher = cv2.BFMatcher_create(cv2.NORM_HAMMING, crossCheck=True)

def generate_edges(image, lower_threshold, upper_threshold, view_image=False):
    image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    canny_edges = cv2.Canny(image, lower_threshold, upper_threshold)
    
    if view_image: display_image(canny_edges)

    return canny_edges

def generate_harris_corners(image, threshold, view_image=False):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    gray = np.float32(gray)
    harris_corners = cv2.cornerHarris(gray, 2, 5, 0.04)
    harris_corner_max_threshold = threshold * harris_corners.max()
    harris_corners = np.argwhere(harris_corners > harris_corner_max_threshold)

    return harris_corners

def generate_keypoints_from_features(features):
    return [cv2.KeyPoint(float(point[1]), float(point[0]), 1) for point in features]

def generate_SIFT_descriptor(image, view_image=False):
    sift = cv2.SIFT_create()
    keypoints = sift.detect(image, None)
    image = cv2.drawKeypoints(image, keypoints, image)
    if view_image:
        display_image(image)

    return keypoints

def gaussian_smooth(image, sigma_x, sigma_y=None):
    if not sigma_y: sigma_y = sigma_x
    return cv2.GaussianBlur(image, (5, 5), sigmaX=sigma_x, sigmaY=sigma_y)

def generate_brief_descriptor(image, keypoints):
    image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    image = gaussian_smooth(image, 1.73)
    return brief.compute(image, keypoints)

def display_image(image):
    cv2.imshow('dst', image)
    cv2.waitKey(0)

def draw_keypoints(image, keypoints):
    cv2.drawKeypoints(image, keypoints, image)
    return image 

def get_hamming_distance_from_BRIEF(descriptor_one, descriptor_two):
    hamming_distance = 0
    for i in range(len(descriptor_one)):
        for j in range(8):
            if (descriptor_one[i] & (1 << j) != descriptor_two[i] & (1 << j)): hamming_distance += 1
    return hamming_distance

def get_matching_points(keypoints_one, keypoints_two, descriptor_one, descriptor_two):
    matches = bf_matcher.match(descriptor_one, descriptor_two)
    matches = sorted(matches, key = lambda match : match.distance)
    matches = matches[:10] if len(matches) > 10 else matches
    matches = [{"First": keypoints_one[match.queryIdx].pt, "Second": keypoints_two[match.trainIdx].pt} for match in matches]
    
    return matches

def overlay_images(image_one, image_two, point_one, point_two):
    x_offset = int(abs(point_one[0] - point_two[0]))
    y_offset = int(abs(point_one[1] - point_two[1]))

    image_one_height, image_two_height = len(image_one), len(image_two)
    image_one_width, image_two_width = len(image_one[0]), len(image_two[0])
    return_image_buffer = np.zeros((image_one_height + image_two_height, image_one_width + image_two_width, 3), dtype=np.uint8)

    return_image_buffer[y_offset:(image_two_height + y_offset), -(image_two_width + x_offset):-x_offset] = image_two
    return_image_buffer[:image_one_height, -image_one_width:] = image_one

    return return_image_buffer

def main():
    image = cv2.imread('../RecordedImages/a1.jpg')
    image_features = generate_harris_corners(image, 0.02)
    keypoints = generate_keypoints_from_features(image_features)
    keypoints, descriptor = generate_brief_descriptor(image, keypoints)

    image_two = cv2.imread('../RecordedImages/a2.jpg')
    image_features = generate_harris_corners(image_two, 0.02)
    keypoints_two = generate_keypoints_from_features(image_features)
    keypoints_two, descriptor_two = generate_brief_descriptor(image_two, keypoints_two)

    matches = get_matching_points(keypoints, keypoints_two, descriptor, descriptor_two)
    display_image(overlay_images(image, image_two, matches[0]['First'], matches[0]['Second']))

    # this is a 32 length array of BYTES - that is why it is showing up as numbers instead of 1's and 0's

    # evidence supporting use of harris corner: https://cs.nyu.edu/~fergus/teaching/vision_2012/3_Corners_Blobs_Descriptors.pdf

    # BRIEF image descriptor: https://medium.com/data-breach/introduction-to-brief-binary-robust-independent-elementary-features-436f4a31a0e6 


if __name__ == "__main__":
    main()
