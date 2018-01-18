using System;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.Kv;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// Provides for pre-built data enrichment options for the <code>enrich(Enrichment...)</code> step. These options will
    ///  include extra information about the <code>Vertex</code>> when output from that step. Note that the enrichment 
    /// examples presented here are examples to demonstrate this concept. The primary lesson here is to show how one might 
    /// merge map results as part of a DSL. These enrichment options may not be suitable for traversals in production systems 
    /// as counting all edges might add an unreasonable amount of time to an otherwise fast traversal.
    /// </summary>
    public enum Enrichment 
    {
        Vertex,
        InDegree,
        OutDegree,
        Degree,
        Distribution
    }

    public static class EnrichmentLookup 
    {
        public static readonly Dictionary<Enrichment,GraphTraversal<object,IDictionary<String,object>>> Traversals = new Dictionary<Enrichment, GraphTraversal<object,IDictionary<String,object>>>
        {
            {Enrichment.Vertex, __.Project<object>("_vertex").By()},
            {Enrichment.InDegree, __.Project<object>("_inDegree").By(__.InE().Count())},
            {Enrichment.OutDegree, __.Project<object>("_outDegree").By(__.OutE().Count())},
            {Enrichment.Degree, __.Project<object>("_degree").By(__.BothE().Count())},
            {Enrichment.Distribution, __.Project<object>("_distribution").By(__.BothE().GroupCount<string>().By(T.Label))},
        };
    }
}