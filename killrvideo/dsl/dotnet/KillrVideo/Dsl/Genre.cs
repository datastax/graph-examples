using System;
using System.Collections.Generic;

namespace KillrVideo.Dsl
{
    /// <summary>
    /// The available "genre" vertex types in the KillrVideo dataset.
    /// </summary>
    public enum Genre 
    {
        Action,
        Adventure,
        Animation,
        Comedy,
        Documentary,
        Drama,
        Fantasy,
        FilmNoir,
        Horror,
        Kids,
        Musical,
        Mystery,
        Romance,
        SciFi,
        TvSeries,
        Thriller,
        War,
        Western
    }

    public static class GenreLookup 
    {
        public static readonly Dictionary<Genre,string> Names = new Dictionary<Genre, string>
        {
            {Genre.Action, "Action"},
            {Genre.Adventure, "Adventure"},
            {Genre.Animation, "Animation"},
            {Genre.Comedy, "Comedy"},
            {Genre.Documentary, "Documentary"},
            {Genre.Drama, "Drama"},
            {Genre.Fantasy, "Fantasy"},
            {Genre.FilmNoir, "Film-Noir"},
            {Genre.Horror, "Horror"},
            {Genre.Kids, "Kids"},
            {Genre.Musical, "Musical"},
            {Genre.Mystery, "Mystery"},
            {Genre.Romance, "Romance"},
            {Genre.SciFi, "Sci-Fi"},
            {Genre.TvSeries, "TV Series"},
            {Genre.Thriller, "Thriller"},
            {Genre.War, "War"},
            {Genre.Western, "Western"}
        };
    }
}