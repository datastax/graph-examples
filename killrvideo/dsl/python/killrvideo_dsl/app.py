from dsl import KillrVideoTraversalSource
from kv import *
from gremlin_python.structure.graph import Graph
from dse.cluster import Cluster
from dse_graph import DseGraph, DSESessionRemoteGraphConnection

c = Cluster()
session = c.connect()

killr = Graph().traversal(KillrVideoTraversalSource).withRemote(DSESessionRemoteGraphConnection(session, 'killrvideo'))
print killr.movies('Young Guns').next()

                   
            
