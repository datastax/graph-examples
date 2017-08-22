from kv import *
from gremlin_python.process.traversal import Bytecode, P
from gremlin_python.process.graph_traversal import (
    GraphTraversalSource, GraphTraversal)
from gremlin_python.process.graph_traversal import __ as AnonymousTraversal
from gremlin_python.structure.graph import Graph
from dse.cluster import Cluster, EXEC_PROFILE_GRAPH_DEFAULT
from dse_graph import DseGraph

class KillrVideoTraversal(GraphTraversal):

    def actors(self):
        return self.out(EDGE_ACTOR).hasLabel(VERTEX_PERSON)
        
    def ratings(self):
        return self.inE(EDGE_RATED)  
         
    def rated(self, min = 0, max = 0):
        if min < 0 or max > 10:
            raise ValueError('min must be a value between 0 and 10')
        if max < 0 or max > 10:
            raise ValueError('min must be a value between 0 and 10')
        if min != 0 and max != 0 and min > max:
            raise ValueError('min cannot be greater than max')
            
        if min == 0 and max == 0:
            return self.out(EDGE_RATED)
        elif min == 0:
            return self.outE(EDGE_RATED).has(KEY_RATING, gt(min)).inV()
        elif max == 0:
            return self.outE(EDGE_RATED).has(KEY_RATING, lt(min)).inV()
        else:
            return self.outE(EDGE_RATED).has(KEY_RATING, P.between(min, max)).inV()
            
    def byAges(self, start, end):
        if start < 18:
            raise ValueError('Age must be 18 or older')
        if start > end:
            raise ValueError('Start age must be greater than end age')
        if end > 120:
            raise ValueError('Now you are just being crazy') 
            
        return self.filter(__.outV().has(KEY_AGE, P.between(start,end))).group().by(KEY_RATING).by(__.count())  
        
class __(AnonymousTraversal):

    graph_traversal = KillrVideoTraversal
        
    @classmethod
    def actors(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).actors(*args)
    
    @classmethod
    def ratings(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).ratings(*args)
        
    @classmethod
    def rated(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).rated(*args)
        
    @classmethod
    def rated(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).byAges(*args)    
        
class KillrVideoTraversalSource(GraphTraversalSource):

    def __init__(self, *args, **kwargs):
        super(KillrVideoTraversalSource, self).__init__(*args, **kwargs)
        self.graph_traversal = KillrVideoTraversal
        
    def movies(self, *args):
        traversal = self.get_graph_traversal().V().hasLabel(VERTEX_MOVIE)

        if len(args) == 1:
            traversal = traversal.has(KEY_TITLE, args[0])
        elif len(args) > 1:
            traversal = traversal.has(KEY_TITLE, P.within(args)) 
            
        print traversal     
            
        return traversal                   
            
