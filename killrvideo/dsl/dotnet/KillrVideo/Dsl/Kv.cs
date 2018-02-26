using System;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// String tokens for graph element lables and property keys.
    /// </summary>
    public static class Kv 
    {
        public const String VertexMovie = "movie";
        public const String VertexPerson = "person";
        public const String VertexUser = "user";
        public const String VertexGenre = "genre";

        public const String EdgeActor = "actor";
        public const String EdgeRated = "rated";
        public const String EdgeBelongsTo = "belongsTo";

        public const String KeyAge = "age";
        public const String KeyCountry = "country";
        public const String KeyDegree = "_degree";
        public const String KeyDuration = "duration";
        public const String KeyInDegree = "_inDegree";
        public const String KeyMovieId = "movieId";
        public const String KeyName = "name";
        public const String KeyOutDegree = "_outDegree";
        public const String KeyDistribution = "_distribution";
        public const String KeyPersonId = "personId";
        public const String KeyProduction = "production";
        public const String KeyRating = "rating";
        public const String KeyTitle = "title";
        public const String KeyUserId = "userId";
        public const String KeyVertex = "_vertex";
        public const String KeyYear = "year";
    }
}