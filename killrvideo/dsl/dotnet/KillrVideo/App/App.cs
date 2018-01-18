using System;
using System.Linq;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;
using Gremlin.Net.Driver.Remote;
using Gremlin.Net.Driver;
using Dse;
using Dse.Graph;

using KillrVideo.Dsl;

using static KillrVideo.Dsl.Kv;
using static KillrVideo.Dsl.Genre;
using static KillrVideo.Dsl.Enrichment;
using static KillrVideo.Dsl.Recommender;
using static KillrVideo.Dsl.__KillrVideo;

namespace KillrVideo.App
{
    public class App 
    {
        public static void Main() 
        {
            /*
            var graph = new Graph();
            var killr = graph.Traversal().WithRemote(new DriverRemoteConnection(
                new GremlinClient(new GremlinServer("localhost", 8182), mimeType:GremlinClient.GraphSON2MimeType), 
                "killrvideo.g"));
                */
            IDseCluster cluster = DseCluster.Builder()
                            .AddContactPoint("127.0.0.1")
                            .WithGraphOptions(new GraphOptions().SetName("killrvideo"))
                            .Build();
            IDseSession session = cluster.Connect();
            var killr = DseGraph.Traversal(session);   

            PrintHeader("Actors for Young Guns", "killr.Movies(\"Young Guns\").Actors().Values(\"name\")");
            var results = killr.Movies("Young Guns").Actors().Values<string>("name").ToList();
            results.ToList().ForEach(r => Console.WriteLine(r));
            
            PrintHeader("Ratings Distribution by Age for Young Guns", "killr.Movies(\"Young Guns\").Ratings().DistributionForAges(18, 40)");
            IDictionary<string,long> ratingsByAge = killr.Movies("Young Guns").Ratings().DistributionForAges(18, 40).Next();
            var pairs = String.Join(", ", ratingsByAge.Select(kvp => kvp.Key + ":" + kvp.Value.ToString()));
            Console.WriteLine($"[{pairs}]");

            PrintHeader("Failed Validation", "killr.Movies(\"Young Guns\").Ratings().DistributionForAges(17,40)");
            try {
                killr.Movies("Young Guns").Ratings().DistributionForAges(17,40).Next();
            } catch (ArgumentException ae) {
                Console.WriteLine($"Caught ArgumentException: {ae.Message}");
            }

            PrintHeader("Five Recommendations for u460", "killr.Users(\"u460\").Recommend(5, 7).Values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7).Values<string>(KeyTitle).ToList();
            results.ToList().ForEach(r => Console.WriteLine(r));

            /*
            PrintHeader("Five Recommendations for u460 that are comedies", "killr.Users(\"u460\").Recommend(5, 7, Genre(COMEDY)).Values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7, Genre(Comedy)).Values<string>(KeyTitle).ToList();
            results.ToList().ForEach(r => Console.WriteLine(r));

            PrintHeader("Five Recommendations for u460 that use larger actor sampling and are comedies", "killr.users(\"u460\").recommend(5, 7, genre(COMEDY)).values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7, LargeSample, Genre(Comedy)).Values<string>(KeyTitle).ToList();
            results.ToList().ForEach(r => Console.WriteLine(r));

            PrintHeader("Include some additional graph statistics about Young Guns", "killr.movies(\"Young Guns\").enrich(IN_DEGREE, OUT_DEGREE)");
            IDictionary<object,object> enriched = killr.Movies("Young Guns").enrich(InDegree, OutDegree).Next();
            Console.WriteLine(enriched);

            PrintHeader("Insert/update movie and a actors for that movie", "killr.movie(\"m100000\", \"Manos: The Hands of Fate\",...).actor(...)"); 
            results = killr.Movie("m100000", "Manos: The Hands of Fate", "USA", "Sun City Films", 1966, 70).
                    Ensure(Actor("p1000000", "Tom Neyman")).
                    Ensure(Actor("p1000001", "John Reynolds")).
                    Ensure(Actor("p1000002", "Diane Mahree")).
                    forEachRemaining(v -> Console.WriteLine("Added 3 actors to 'Manos: The Hands of Fate'"));
            results.ToList().ForEach(r => Console.WriteLine(r));

            PrintHeader("Get the actors for the newly added movie", "killr.movies(\"Manos: The Hands of Fate\").actors().values(\"name\")");
            results = killr.movies("Manos: The Hands of Fate").actors().values("name").toList();
            results.ToList().ForEach(r => Console.WriteLine(r));
            */
        }

        private static void PrintHeader(string title) 
        {
            PrintHeader(title, null);
        }

        private static void PrintHeader(string title, string subtitle) 
        {
            string st = "";
            string t = $"{Environment.NewLine}* {title}";
            Console.WriteLine(t);
            if (!string.IsNullOrEmpty(subtitle)) 
            {
                st = $"[{subtitle}]";
                Console.WriteLine(st);
            }

            string line = new string('-', string.IsNullOrEmpty(st) ? t.Length : st.Length);
            Console.WriteLine(line);
        }
    }
}