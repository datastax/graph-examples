from dsl import KillrVideoTraversalSource, __
from kv import *
from genre import *
from gremlin_python.structure.graph import Graph
from dse.cluster import Cluster
from dse_graph import DseGraph, DSESessionRemoteGraphConnection


def print_header(title, subtitle=''):
    st = ''
    t = '\n* ' + title
    print t
    if len(subtitle) > 0:
        st = '[' + subtitle + ']'
        print st

    line = '-' * ((len(st) if (len(st) > 0) else len(t)) - 1)
    print line

c = Cluster()
session = c.connect()

# initialize the TraversalSource for the DSL using the DSE Java Driver
# https://github.com/datastax/java-dse-driver
killr = Graph().traversal(KillrVideoTraversalSource).withRemote(DSESessionRemoteGraphConnection(session, 'killrvideo'))

print_header('Actors for Young Guns', 'killr.movies(\'Young Guns\').actors().values(\'name\')')
for n in killr.movies("Young Guns").actors().values("name").toList():
    print n

print_header('Ratings Distribution by Age for Young Guns', 'killr.movies(\'Young Guns\').ratings().distributionForAges(18, 40)')
ratingsByAge = killr.movies('Young Guns').ratings().distributionForAges(18, 40).next()
print ratingsByAge

print_header('Failed Validation', 'killr.movies(\'Young Guns\').ratings().distributionForAges(17,40)')
try:
    killr.movies('Young Guns').ratings().distributionForAges(17, 40).next()
except ValueError as ve:
    print ve.args

print_header('Five Recommendations for u460', 'killr.users(\'u460\').recommend(5, 7).values(KEY_TITLE)');
for r in killr.users('u460').recommend(5, 7).values(KEY_TITLE).toList():
    print r

print_header('Five Recommendations for u460 that are commedies', 'killr.users(\'u460\').recommend(5, 7, genre(COMEDY)).values(KEY_TITLE)');
for r in killr.users('u460').recommend(5, 7, __.genre(COMEDY)).values(KEY_TITLE).toList():
    print r

print_header('Insert/update movie and a actors for that movie', 'killr.movie(\'m100000\', \'Manos: The Hands of Fate\',...).actor(...)')
(killr.movie('m100000', 'Manos: The Hands of Fate', 1966, 70, 'USA', 'Sun City Films').
       ensure(__.actor('p1000000', 'Tom Neyman')).
       ensure(__.actor('p1000001', 'John Reynolds')).
       ensure(__.actor('p1000002', 'Diane Mahree')).iterate())
print 'Added 3 actors to \'Manos: The Hands of Fate\''

print_header('Get the actors for the newly added movie', 'killr.movies(\'Manos: The Hands of Fate\').actors().values(\'name\')');
for n in killr.movies('Manos: The Hands of Fate').actors().values('name').toList():
    print n
