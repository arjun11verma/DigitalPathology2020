from removeblackspace import removeBlackSpace as rbs

imgproc = rbs()

base_file_name = './DigPathSlideImages/'

clean_img = imgproc.removeBlackSpace(base_file_name + 'download (18).jpg')

clean_img = imgproc.removeBlackSpace(base_file_name + 'download (2).jpg')

imgproc.displayImage(clean_img)

#rbs.displayImage(base_file_name + 'download (13).jpg')



