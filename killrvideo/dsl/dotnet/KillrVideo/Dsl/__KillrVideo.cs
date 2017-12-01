using System;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.Kv;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// Spawns anonymous traversal instances for the DSL.
    /// </summary>
    public static class __KillrVideo 
    {
        /// <summary>
        /// Traverses from a "movie" to an "person" over the "actor" edge.
        /// </summary>
        public static GraphTraversal<object,Vertex> Actors() 
        {
            return __.Out(EdgeActor).HasLabel(VertexPerson);
        }

        /// <summary>
        /// Gets or creates a "person".
        ///
        /// This step first checks for existence of a person given their identifier. If it exists then the person is
        /// returned and their "name" property updated. It is not possible to change the person's identifier once it is
        /// assigned (at least as defined by this DSL). If the person does not exist then a new person vertex is added
        /// with the specified identifier and name.
        /// </summary>
        public static GraphTraversal<object,Vertex> Person(string personId, string name) 
        {
            if (string.IsNullOrEmpty(personId)) throw new ArgumentException("The personId must not be null or empty");
            if (string.IsNullOrEmpty(name)) throw new ArgumentException("The name of the person must not be null or empty");

            return __.Coalesce<Vertex>(__.V().Has(VertexPerson, KeyPersonId, personId),
                                      __.AddV(VertexPerson).Property(KeyPersonId, personId)).
                   Property(KeyName, name);
        }
    }
}