using System;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.Kv;

namespace KillrVideo.Dsl
{
    public static class KillrVideoGraphTraversalSourceExtensions
    {
        /// <summary>
        /// Gets movies by their title.
        /// </summary>
        public static GraphTraversal<Vertex,Vertex> Actors(this GraphTraversalSource g, string title, params string[] additionalTitles) 
        {
            var titles = new List<string>();
            titles.Add(title);
            titles.AddRange(additionalTitles);
            GraphTraversal<Vertex,Vertex> t = g.V().HasLabel(VertexMovie);
            if (titles.Count == 1) 
            {
                t = t.Has(KeyTitle, titles[0]);
            } 
            else if (titles.Count > 1) 
            {
                t = t.Has(KeyTitle, P.Within(titles.ToArray()));
            }
            return t;
        }
    }
}
