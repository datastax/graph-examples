using System;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.KV;

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
            GraphTraversal<Vertex,Vertex> t = g.V().HasLabel(VERTEX_MOVIE);
            if (titles.Count == 1) 
            {
                t = t.Has(KEY_TITLE, titles[0]);
            } 
            else if (titles.Count > 1) 
            {
                t = t.Has(KEY_TITLE, P.Within(titles.ToArray()));
            }
            return t;
        }
    }
}
