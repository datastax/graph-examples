import datetime
from kv import *
from gremlin_python.process.traversal import (Bytecode, P, Scope, Order, Column)
from gremlin_python.process.graph_traversal import (GraphTraversalSource, GraphTraversal)
from gremlin_python.process.graph_traversal import __ as AnonymousTraversal

V = AnonymousTraversal.V
addV = AnonymousTraversal.addV
outE = AnonymousTraversal.outE
outV = AnonymousTraversal.outV
count = AnonymousTraversal.count
unfold = AnonymousTraversal.unfold

gt = P.gt
lt = P.lt
between = P.between
within = P.within
local = Scope.local
decr = Order.decr
values = Column.values
keys = Column.keys

class KillrVideoTraversal(GraphTraversal):
    """The KillrVideo Traversal class which exposes the available steps of the DSL."""

    def actors(self):
        """Traverses from a "movie" to an "person" over the "actor" edge."""

        return self.out(EDGE_ACTOR).hasLabel(VERTEX_PERSON)
        
    def ratings(self):
        """Traverses from a "movie" to a "rated" edge."""

        return self.inE(EDGE_RATED)  
         
    def rated(self, minimum=0, maximum=0):
        """Traverses from a "user" to a "movie" over the "rated" edge.

         Provides for filtering those edges when specified. If both arguments are zero then there is no rating filter.
        """
        if minimum < 0 or minimum > 10:
            raise ValueError('minimum rating must be a value between 0 and 10')
        if maximum < 0 or maximum > 10:
            raise ValueError('maximum rating must be a value between 0 and 10')
        if minimum != 0 and maximum != 0 and minimum > maximum:
            raise ValueError('minimum rating cannot be greater than maximum rating ')
            
        if minimum == 0 and maximum == 0:
            return self.out(EDGE_RATED)
        elif minimum == 0:
            return self.outE(EDGE_RATED).has(KEY_RATING, gt(minimum)).inV()
        elif maximum == 0:
            return self.outE(EDGE_RATED).has(KEY_RATING, lt(minimum)).inV()
        else:
            return self.outE(EDGE_RATED).has(KEY_RATING, between(minimum, maximum)).inV()
            
    def distributionForAges(self, start, end):
        """Assumes incoming "rated" edges and filters based on the age of the "user".

        Produces a map where the key is the rating and the value is the number of times that rating was given.
        This step validates that the start age should exclude minors (i.e. 18 and older).
        """
        if start < 18:
            raise ValueError('Age must be 18 or older')
        if start > end:
            raise ValueError('Start age must be greater than end age')
        if end > 120:
            raise ValueError('Now you are just being crazy') 
            
        return self.filter(outV().has(KEY_AGE, P.between(start, end))).group().by(KEY_RATING).by(count())

    def recommend(self, recommendations, minimum_rating):
        """A simple recommendation algorithm.

        Starts from a "user" and examines movies the user has seen filtered by the minimum_rating which removes
        movies that hold a rating lower than the value specified. It then samples the actors in the movies the
        user has seen and uses that to find other movies those actors have been in that the user has not yet
        seen. Those movies are grouped, counted and sorted based on that count to produce the recommendation.
        """

        if recommendations <= 0:
            raise ValueError('recommendations must be greater than zero')

        return (self.rated(minimum_rating, 0).
                aggregate("seen").
                local(outE(EDGE_ACTOR).sample(3).inV().fold()).
                unfold().in_(EDGE_ACTOR).where(P.without(["seen"])).
                groupCount().
                order(local).
                  by(values, decr).
                limit(local, recommendations).
                select(keys).
                unfold())

    def person(self, person_id, name):
        """Gets or creates a "person"

        This step first checks for existence of a person given their identifier. If it exists then the person is
        returned and their "name" property updated. It is not possible to change the person's identifier once it is
        assigned (at least as defined by this DSL). If the person does not exist then a new person vertex is added
        with the specified identifier and name.
        """

        if person_id in (None, ''):
            raise ValueError('The personId must not be null or empty')
        if name in (None, ''):
            raise ValueError('The name of the person must not be null or empty')

        return (self.coalesce(V().has(VERTEX_PERSON, KEY_PERSON_ID, person_id),
                              addV(VERTEX_PERSON).property(KEY_PERSON_ID, person_id)).
                     property(KEY_NAME, name))

    def actor(self, person_id, name):
        """Gets or creates an "actor".

        In this schema, an actor is a "person" vertex with an incoming "actor" edge from a "movie" vertex. This step
        therefore assumes that the incoming stream is a "movie" vertex and actors will be attached to that. This step
        checks for existence of the "actor" edge first before adding and if found will return the existing one. It
        further ensures the existence of the "person" vertex as provided by the person(String, String) step.
        """

        # as mentioned in the step documentation this step assumes an incoming "movie" vertex. it is immediately
        # labelled as "^movie". the addition of the caret prefix has no meaning except to provide for a unique
        # labelling space within the DSL itself.
        return (self.as_("^movie").
                     coalesce(__.actors().has(KEY_PERSON_ID, person_id),
                              __.person(person_id, name).addE(EDGE_ACTOR).from_("^movie").inV()))

    def ensure(self, mutation_traversal):
        """This step is an alias for the sideEffect() step.

        As an alias, it makes certain aspects of the DSL more readable.
        """

        return self.sideEffect(mutation_traversal)

class __(AnonymousTraversal):
    """Spawns anonymous KillrVideoTraversal instances for the DSL.

    Note that this class is a bit boilerplate in its approach. For purposes of this DSL, all methods available to the
    KillrVideoTraversal are also made available from here so that each can spawn an anonymous traversal.
    """

    graph_traversal = KillrVideoTraversal
        
    @classmethod
    def actors(cls):
        return cls.graph_traversal(None, None, Bytecode()).actors()
    
    @classmethod
    def ratings(cls):
        return cls.graph_traversal(None, None, Bytecode()).ratings()
        
    @classmethod
    def rated(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).rated(*args)
        
    @classmethod
    def byAges(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).byAges(*args)

    @classmethod
    def recommend(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).recommend(*args)

    @classmethod
    def person(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).person(*args)

    @classmethod
    def actor(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).actor(*args)

    @classmethod
    def ensure(cls, *args):
        return cls.graph_traversal(None, None, Bytecode()).ensure(*args)


class KillrVideoTraversalSource(GraphTraversalSource):
    """The KillrVideo DSL TraversalSource which will provide the start steps for DSL-based traversals.

    This TraversalSource spawns KillrVideoTraversal instances.
    """

    def __init__(self, *args, **kwargs):
        super(KillrVideoTraversalSource, self).__init__(*args, **kwargs)
        self.graph_traversal = KillrVideoTraversal   # tells the "source" the type of Traversal to spawn
        
    def movies(self, *args):
        """Gets movies by their title."""

        traversal = self.get_graph_traversal().V().hasLabel(VERTEX_MOVIE)

        if len(args) == 1:
            traversal = traversal.has(KEY_TITLE, args[0])
        elif len(args) > 1:
            traversal = traversal.has(KEY_TITLE, within(args))
            
        return traversal

    def users(self, *args):
        """Gets users by their identifier."""

        traversal = self.get_graph_traversal().V().hasLabel(VERTEX_USER)

        if len(args) == 1:
            traversal = traversal.has(KEY_USER_ID, args[0])
        elif len(args) > 1:
            traversal = traversal.has(KEY_USER_ID, within(args))

        return traversal
            
    def movie(self, movie_id, title, year, duration, country="", production=""):
        """Ensures that a "movie" exists.

        This step performs a number of validations on the various parameters passed to it and then checks for existence
        of the movie based on the identifier for the movie. If it exists then the movie is returned with its mutable
        properties updated (all are mutable except for the "movieId" as defined by this DSL). If it does not exist then
        a new "movie" vertex is added.
        """

        if year < 1895:
            raise ValueError('The year of the movie cannot be before 1895')
        if year > datetime.datetime.today().year:
            raise ValueError('The year of the movie can not be in the future')
        if duration <= 0:
            raise ValueError('The duration of the movie must be greater than zero')
        if movie_id in (None, ''):
            raise ValueError('The movie_id must not be null or empty')
        if title in (None, ''):
            raise ValueError('The title of the movie must not be null or empty')

        traversal = self.get_graph_traversal().V()

        # performs a "get or create/update" for the movie vertex. if it is present then it simply returns the existing
        # movie and updates the mutable property keys. if it is not, then it is created with the specified movieId
        # and properties.
        return (traversal.
                has(VERTEX_MOVIE, KEY_MOVIE_ID, movie_id).
                fold().
                coalesce(unfold(),
                         addV(VERTEX_MOVIE).property(KEY_MOVIE_ID, movie_id)).
                property(KEY_TITLE, title).
                property(KEY_COUNTRY, country).
                property(KEY_PRODUCTION, production).
                property(KEY_YEAR, year).
                property(KEY_DURATION, duration))
