using System;
using System.Linq;
using System.Collections.Generic;
using Gremlin.Net.Process.Traversal;
using Gremlin.Net.Structure;
using Gremlin.Net.Driver.Remote;
using Gremlin.Net.Driver;

using KillrVideo.Dsl;

namespace KillrVideo.App
{
    public class App {
        public static void Main() {
            var graph = new Graph();
            var killr = graph.Traversal().WithRemote(new DriverRemoteConnection(
                new GremlinClient(new GremlinServer("localhost", 8182), mimeType:GremlinClient.GraphSON2MimeType), 
                "killrvideo.g"));

            PrintHeader("Actors for Young Guns", "killr.Movies(\"Young Guns\").Actors().Values(\"name\")");
            var names = killr.Movies("Young Guns").Actors().Values<String>("name").ToList();
            foreach (object item in names)
            {
                Console.WriteLine(item);
            }
        }

        private static void PrintHeader(string title) {
            PrintHeader(title, null);
        }

        private static void PrintHeader(string title, string subtitle) {
            string st = "";
            string t = "\n* " + title;
            Console.WriteLine(t);
            if (!string.IsNullOrEmpty(subtitle)) {
                st = "[" + subtitle + "]";
                Console.WriteLine(st);
            }

            string line = new string('-', string.IsNullOrEmpty(st) ? t.Length : st.Length);
            Console.WriteLine(line);
        }
    }
}