import ExperimentalImageStitcher as expim
import numpy as np
import cv2 
import ImageProcessor as ImgProc
from time import time 

def lay_image_on_buffer(buffer, image, starting_offset):
    buffer[starting_offset[1]:(starting_offset[1] + len(image)), starting_offset[0]:(starting_offset[0] + len(image[0]))] = image
    return buffer

def extract_image_from_buffer(buffer, image, starting_offset):
    return buffer[starting_offset[1]:(starting_offset[1] + len(image)), starting_offset[0]:(starting_offset[0] + len(image[0]))]

def test_stitching(images):
    homographies = expim.generate_homographies(images)
    ending_point, opposing_shift = expim.generate_ending_point_from_homographies(homographies) # TODO - Handle opposing shift
    
    starting_offset = np.array([abs(ending_point[0]) if ending_point[0] < 0 else 0, abs(ending_point[1]) if ending_point[1] < 0 else 0]) + opposing_shift
    output_size = np.flip(np.abs(ending_point[:-1])) + images[0].shape[:-1] + (opposing_shift * 2)

    rolling_image = np.zeros((output_size[0], output_size[1], 3), dtype=np.uint8)
    input_image = np.zeros((output_size[0], output_size[1], 3), dtype=np.uint8)

    rolling_image = lay_image_on_buffer(rolling_image, images[0], starting_offset)
    for homography, input in zip(homographies, images[1:]):
        rolling_image = cv2.warpAffine(rolling_image, homography, np.flip(output_size))
        input = expim.erase_overlap_from_image(extract_image_from_buffer(rolling_image, input, starting_offset), input)
        input_image = lay_image_on_buffer(input_image, input, starting_offset)
        rolling_image = cv2.addWeighted(rolling_image, 1, input_image, 1, 0)
    
    cv2.imwrite('test_img.jpg', rolling_image)

def main():
    images = [cv2.imread(f'./DigPathSlideImages/a{i}.jpg') for i in range(23, 47)]

    start = time()
    test_stitching(images)
    print(f'Time taken to complete operation: {time() - start}')
    start = time()
    processor = ImgProc.ImageProcessor()
    cv2.imwrite('cvtest.jpg', processor.stitchImages(images))
    print(f'Time taken to complete operation: {time() - start}')

if __name__ == "__main__":
    main()