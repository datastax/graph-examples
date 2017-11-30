using System;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// String tokens for graph element lables and property keys.
    /// </summary>
    public static class KV 
    {
        public const String VERTEX_MOVIE = "movie";
        public const String VERTEX_PERSON = "person";
        public const String VERTEX_USER = "user";
        public const String VERTEX_GENRE = "genre";

        public const String EDGE_ACTOR = "actor";
        public const String EDGE_RATED = "rated";
        public const String EDGE_BELONGS_TO = "belongsTo";

        public const String KEY_AGE = "age";
        public const String KEY_COUNTRY = "country";
        public const String KEY_DURATION = "duration";
        public const String KEY_MOVIE_ID = "movieId";
        public const String KEY_NAME = "name";
        public const String KEY_PERSON_ID = "personId";
        public const String KEY_PRODUCTION = "production";
        public const String KEY_RATING = "rating";
        public const String KEY_TITLE = "title";
        public const String KEY_USER_ID = "userId";
        public const String KEY_YEAR = "year";
    }
}