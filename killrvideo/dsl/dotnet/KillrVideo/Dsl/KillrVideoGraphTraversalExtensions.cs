using System;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.Kv;

namespace KillrVideo.Dsl
{
    public static class KillrVideoGraphTraversalExtensions
    {
        /// <summary>
        /// Traverses from a "movie" to an "person" over the "actor" edge.
        /// </summary>
        public static GraphTraversal<Vertex,Vertex> Actors(this GraphTraversal<Vertex,Vertex> t) 
        {
            return t.Out(EdgeActor).HasLabel(VertexPerson);
        }
    }
}
