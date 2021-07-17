import numpy as np 

class Edge:
    def __init__(self, starting_point, length):
        self.starting_point = starting_point
        self.length = length

class Sector:
    def __init__(self, starting_point, width, height):
        self.starting_point = starting_point
        self.width = width
        self.height = height
    
    def sector_contains(self, input_sector):
        edges_starting_point = np.zeros(2)
        point_difference = self.starting_point - input_sector.starting_point
        if(point_difference[0] < self.width or point_difference[1] < self.height):
            if (point_difference[0] < 0):
                edges_starting_point[0] = self.starting_point[0] + self.width
            else:
                edges_starting_point[0] = self.starting_point[0]
            if (point_difference[1] < 0):
                edges_starting_point[1] = self.starting_point[1] + self.height
            else:
                edges_starting_point[1] = self.starting_point[1]
            return {'Horizontal': Edge(edges_starting_point, self.width - abs(point_difference[0])), 'Vertical': Edge(edges_starting_point, self.height - abs(point_difference[1]))}
        else: return None 