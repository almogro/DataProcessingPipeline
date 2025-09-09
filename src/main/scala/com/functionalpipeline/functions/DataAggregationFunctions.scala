package com.functionalpipeline.functions

import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics, MovieRecord}

/**
 * Pure functions for aggregating and analyzing data.
 * 
 * These functions demonstrate functional programming concepts
 * including tail recursion, higher-order functions, and immutability.
 */
object DataAggregationFunctions {
  
  /**
   * Computes statistics for a genre.
   * 
   * @param genre The genre name
   * @param movies List of processed movie records for this genre
   * @return GenreStatistics object with computed values
   */
  def computeGenreStats(genre: String, movies: List[ProcessedMovieRecord]): GenreStatistics = {
    if (movies.isEmpty) {
      GenreStatistics(genre, 0, 0.0, 0, "N/A")
    } else {
      val ratings = movies.map(_.movie.rating)
      val votes = movies.map(_.movie.votes)
      
      GenreStatistics(
        genre = genre,
        count = movies.length,
        averageRating = ratings.sum / ratings.length,
        totalVotes = votes.sum,
        topMovie = findTopMovieByRating(movies)
      )
    }
  }
  
  /**
   * Finds the top N movies by popularity score.
   * 
   * This demonstrates tail recursion and functional sorting.
   * 
   * @param movies List of processed movie records
   * @param n Number of top movies to return
   * @return List of top N movies sorted by popularity score
   */
  def findTopMovies(movies: List[ProcessedMovieRecord], n: Int): List[ProcessedMovieRecord] = {
    movies
      .sortBy(-_.popularityScore)
      .take(n)
  }
  
  /**
   * Finds the movie with the highest rating in a list.
   * 
   * This demonstrates functional programming with fold operations.
   * 
   * @param movies List of processed movie records
   * @return Title of the highest rated movie
   */
  private def findTopMovieByRating(movies: List[ProcessedMovieRecord]): String = {
    movies
      .sortBy(-_.movie.rating)
      .headOption
      .map(_.movie.title)
      .getOrElse("N/A")
  }
  
  /**
   * Computes average rating for a list of movies using tail recursion.
   * 
   * This demonstrates tail recursion in functional programming.
   * 
   * @param movies List of movie records
   * @return Average rating
   */
  def computeAverageRating(movies: List[MovieRecord]): Double = {
    computeAverageRatingTail(movies, 0.0, 0)
  }
  
  /**
   * Tail recursive helper for computing average rating.
   * 
   * @param movies Remaining movies to process
   * @param sum Current sum of ratings
   * @param count Current count of movies
   * @return Average rating
   */
  private def computeAverageRatingTail(movies: List[MovieRecord], sum: Double, count: Int): Double = {
    movies match {
      case Nil => if (count == 0) 0.0 else sum / count
      case head :: tail => computeAverageRatingTail(tail, sum + head.rating, count + 1)
    }
  }
  
  /**
   * Groups movies by decade using functional operations.
   * 
   * @param movies List of processed movie records
   * @return Map of decade to list of movies
   */
  def groupByDecade(movies: List[ProcessedMovieRecord]): Map[String, List[ProcessedMovieRecord]] = {
    movies.groupBy(_.decade)
  }
  
  /**
   * Computes the total votes across all movies.
   * 
   * This demonstrates functional aggregation using fold.
   * 
   * @param movies List of movie records
   * @return Total votes
   */
  def computeTotalVotes(movies: List[MovieRecord]): Long = {
    movies.foldLeft(0L)(_ + _.votes)
  }
}
