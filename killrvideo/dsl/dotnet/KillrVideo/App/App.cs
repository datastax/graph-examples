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
using static KillrVideo.Dsl.Recommender;
using static KillrVideo.Dsl.__KillrVideo;
using static KillrVideo.Dsl.Enrichment;

using Vertex = Gremlin.Net.Structure.Vertex;

namespace KillrVideo.App
{
    public class App 
    {
        public static void Main() 
        {
            IDseCluster cluster = DseCluster.Builder().
                                             AddContactPoint("127.0.0.1").
                                             WithGraphOptions(new GraphOptions().SetName("killrvideo")).
                                             Build();
            IDseSession session = cluster.Connect();
            var killr = DseGraph.Traversal(session);   

            PrintHeader("Actors for Young Guns", "killr.Movies(\"Young Guns\").Actors().Values(\"name\")");
            var results = killr.Movies("Young Guns").Actors().Values<string>("name").ToList();
            PrintItems(results);
            
            PrintHeader("Ratings Distribution by Age for Young Guns", "killr.Movies(\"Young Guns\").Ratings().DistributionForAges(18, 40)");
            IDictionary<string,long> ratingsByAge = killr.Movies("Young Guns").Ratings().DistributionForAges(18, 40).Next();
            var pairs = String.Join(", ", ratingsByAge.Select(kvp => kvp.Key + "=" + kvp.Value.ToString()));
            Console.WriteLine($"[{pairs}]");

            PrintHeader("Failed Validation", "killr.Movies(\"Young Guns\").Ratings().DistributionForAges(17,40)");
            try 
            {
                killr.Movies("Young Guns").Ratings().DistributionForAges(17,40).Next();
            } 
            catch (ArgumentException ae) 
            {
                Console.WriteLine($"Caught ArgumentException: {ae.Message}");
            }

            PrintHeader("Five Recommendations for u460", "killr.Users(\"u460\").Recommend(5, 7).Values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7).Values<string>(KeyTitle).ToList();
            PrintItems(results);

            PrintHeader("Five Recommendations for u460 that are comedies", "killr.Users(\"u460\").Recommend(5, 7, Genre(COMEDY)).Values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7, Genre(Comedy)).Values<string>(KeyTitle).ToList();
            PrintItems(results);

            PrintHeader("Five Recommendations for u460 that use larger actor sampling and are comedies", "killr.users(\"u460\").recommend(5, 7, genre(COMEDY)).values(KEY_TITLE)");
            results = killr.Users("u460").Recommend(5, 7, LargeSample, Genre(Comedy)).Values<string>(KeyTitle).ToList();
            PrintItems(results);

            PrintHeader("Include some additional graph statistics about Young Guns", "killr.movies(\"Young Guns\").enrich(Only(true, \"title\", \"year\"), InDegree(), OutDegree())");
            IDictionary<object,object> enriched = killr.Movies("Young Guns").enrich<object>(Only(true, "title", "age"), InDegree(), OutDegree()).Next();
            pairs = String.Join(", ", enriched.Select(kvp => kvp.Key + "=" + kvp.Value.ToString()));
            Console.WriteLine($"[{pairs}]");

            PrintHeader("Insert/update movie and a actors for that movie", "killr.movie(\"m100000\", \"Manos: The Hands of Fate\",...).actor(...)"); 
            killr.Movie("m100000", "Manos: The Hands of Fate", 1966, 70, "USA", "Sun City Films").
                  Ensure<Vertex,object,Vertex>(__KillrVideo.Actor("p1000000", "Tom Neyman")).
                  Ensure<Vertex,object,Vertex>(__KillrVideo.Actor("p1000001", "John Reynolds")).
                  Ensure<Vertex,object,Vertex>(__KillrVideo.Actor("p1000002", "Diane Mahree")).
                  Iterate();
            Console.WriteLine("Updated 'Manos: The Hands of Fate'");

            PrintHeader("Get the actors for the newly added movie", "killr.movies(\"Manos: The Hands of Fate\").actors().values(\"name\")");
            results = killr.Movies("Manos: The Hands of Fate").Actors().Values<String>("name").ToList();
            PrintItems(results);
        }

        private static void PrintItems(IEnumerable<object> results)
        {
            foreach (var r in results)
            {
                Console.WriteLine(r);
            }
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