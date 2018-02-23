using System;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;

using static KillrVideo.Dsl.Kv;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// Provides for pre-built data enrichment options for the <code>enrich(Enrichment...)</code> step. These options will
    /// include extra information about the <code>Vertex</code>> when output from that step. Note that the enrichment 
    /// examples presented here are examples to demonstrate this concept. The primary lesson here is to show how one might 
    /// merge map results as part of a DSL. These enrichment options may not be suitable for traversals in production systems 
    /// as counting all edges might add an unreasonable amount of time to an otherwise fast traversal.
    /// </summary>
    public class Enrichment 
    {
        private GraphTraversal<object,IDictionary<object,object>> traversal;

        private Enrichment(GraphTraversal<object,IDictionary<object,object>> traversal) 
        {
            this.traversal = traversal;
        }

        public GraphTraversal<object, IDictionary<object,object>> getTraversal() 
        {
            return traversal;
        }

        /**
        * Include the {@code Vertex} itself as a value in the enriched output which might be helpful if additional
        * traversing on that element is required.
        */
        public static Enrichment Vertex() 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Project<object>("_vertex").By()));
        }

        /**
        * The number of incoming edges on the {@code Vertex}.
        */
        public static Enrichment InDegree() 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Project<object>("_inDegree").By(__.InE().Count())));
        }

        /**
        * The number of outgoing edges on the {@code Vertex}.
        */
        public static Enrichment OutDegree() 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Project<object>("_outDegree").By(__.OutE().Count())));
        }

        /**
        * The total number of in and out edges on the {@code Vertex}.
        */
        public static Enrichment Degree() 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Project<object>("_degree").By(__.BothE().Count())));
        }

        /**
        * Calculates the edge label distribution for the {@code Vertex}.
        */
        public static Enrichment Distribution() 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Project<object>("_distribution").By(__.BothE().GroupCount<string>().By(T.Label))));
        }

        /**
        * Chooses the keys to include in the output and assumes that id and label should not be included.
        */
        public static Enrichment Only(params String[] keys) 
        {
            return Only(false, keys);
        }

        /**
        * Chooses the keys to include in the output and determines if id and label are included with them.
        */
        public static Enrichment Only(bool includeIdLabel, params String[] propertyKeys) 
        {
            return new Enrichment(__.Map<IDictionary<object,object>>(__.Map<IDictionary<object,object>>(__.ValueMap<object>(true, propertyKeys))));
        }
    }
}