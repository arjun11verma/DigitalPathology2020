import cv2
import numpy as np

brief = cv2.xfeatures2d.BriefDescriptorExtractor_create()
bf_matcher = cv2.BFMatcher_create(cv2.NORM_HAMMING, crossCheck=True)

def display_image(image):
    cv2.imshow('dst', image)
    cv2.waitKey(0)

def draw_keypoints(image, keypoints):
    cv2.drawKeypoints(image, keypoints, image)
    return image 

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

def generate_keypoints_and_descriptor(image):
    keypoints = generate_keypoints_from_features(generate_harris_corners(image, 0.02))
    return generate_brief_descriptor(image, keypoints)

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

def get_best_matching_point(keypoints_one, keypoints_two, descriptor_one, descriptor_two):
    matches = bf_matcher.match(descriptor_one, descriptor_two)
    best_match = min(matches, key= lambda match : match.distance)
    first, second = keypoints_one[best_match.queryIdx].pt, keypoints_two[best_match.trainIdx].pt
    first, second = (int(first[0]), int(first[1])), (int(second[0]), int(second[1]))
    best_match = {"First": first, "Second": second}

    return best_match

def calculate_image_offset(matching_point):
    x_offset = (matching_point['Second'][0] - matching_point['First'][0])
    y_offset = (matching_point['Second'][1] - matching_point['First'][1])
    return np.array([x_offset, y_offset])

def get_homography(matching_offsets):
    return np.array([
        [1, 0, matching_offsets[0]],
        [0, 1, matching_offsets[1]]
    ], dtype=np.float)

def generate_homographies(images):
    homographies = []
    prev_keypoints, prev_descriptor = generate_keypoints_and_descriptor(images[0])
    image_idx = 0

    for image in images[1:]:
        next_keypoints, next_descriptor = generate_keypoints_and_descriptor(image)
        best_matching_point = get_best_matching_point(prev_keypoints, next_keypoints, prev_descriptor, next_descriptor)
        homographies.append(get_homography(calculate_image_offset(best_matching_point)))
        prev_keypoints, prev_descriptor = next_keypoints, next_descriptor
        image_idx += 1
    
    return homographies

def generate_ending_point_from_homographies(homographies):
    ending_point = np.array([0, 0, 1], dtype=np.float)
    opposing_shift = np.array([500, 500]) # Buffer for up an down behavior - TODO Handle how to generate this correctly programmatically 
    for homography in homographies:
        ending_point = homography @ ending_point
        ending_point = np.append(ending_point, 1.0)
    return ending_point.astype('int'), opposing_shift.astype('int')

def erase_overlap_from_image(base_image, overlapping_image):
    base_image = cv2.cvtColor(base_image, cv2.COLOR_BGR2GRAY)
    threshold, base_image = cv2.threshold(base_image, 1, 255, cv2.THRESH_BINARY)
    erase_mask = cv2.bitwise_not(base_image)
    return cv2.bitwise_and(overlapping_image, overlapping_image, mask=erase_mask)

def add_gradient_along_edge(image, length, depth, starting_postition, horizontal_edge=False, facing_negative=False):
    image = image.astype('float')

    image_height, image_width = len(image), len(image[0])
    alpha_gradient = np.power(np.linspace(0.0, 1.0, num=abs(depth)), 2)
    facing_negative = 0 if facing_negative else 1

    for i in range(starting_postition, starting_postition + depth):
        #y_slice, x_slice = length 
        image[:length, i] *= abs(facing_negative - alpha_gradient[i - starting_postition])
    
    for i in range(starting_postition + depth, image_width):
        image[:length, i] = [0, 0, 0]
    
    return image.astype('uint8')

    
# POTENTIAL - Construct homography matrix to determine if there are any rotations/transformations (other than translation) needed
# Determine where the new image will be placed - Allocate sectors for the present image and for the new image
# Blending - alpha or minimum error boundary to handle harsh edges then poisson to distribute lighting. 


# You can add to a buffer with slicing - it'll proabably make things a LOT easier 
# Plus you can do alpha blending a lot easier now! Yay! Also you could potentially utilize minimum error boundary cuts
# Also use poisson blending to handle variance in lighting 
# evidence supporting use of harris corner: https://cs.nyu.edu/~fergus/teaching/vision_2012/3_Corners_Blobs_Descriptors.pdf
# BRIEF image descriptor: https://medium.com/data-breach/introduction-to-brief-binary-robust-independent-elementary-features-436f4a31a0e6 
