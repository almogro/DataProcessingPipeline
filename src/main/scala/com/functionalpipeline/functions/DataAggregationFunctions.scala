package com.functionalpipeline.functions

import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics, MovieRecord}

/** Data aggregation and analysis functions. */
object DataAggregationFunctions {
  
  /** Computes genre statistics from movie list. */
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
  
  /** Finds top N movies by popularity score. */
  def findTopMovies(movies: List[ProcessedMovieRecord], n: Int): List[ProcessedMovieRecord] = {
    movies
      .sortBy(-_.popularityScore)
      .take(n)
  }
  
  /** Finds highest rated movie title. */
  private def findTopMovieByRating(movies: List[ProcessedMovieRecord]): String = {
    movies
      .sortBy(-_.movie.rating)
      .headOption
      .map(_.movie.title)
      .getOrElse("N/A")
  }
  
  /** Computes average rating using tail recursion. */
  def computeAverageRating(movies: List[MovieRecord]): Double = {
    computeAverageRatingTail(movies, 0.0, 0)
  }
  
  /** Tail recursive helper for average calculation. */
  private def computeAverageRatingTail(movies: List[MovieRecord], sum: Double, count: Int): Double = {
    movies match {
      case Nil => if (count == 0) 0.0 else sum / count
      case head :: tail => computeAverageRatingTail(tail, sum + head.rating, count + 1)
    }
  }
  
  /** Groups movies by decade. */
  def groupByDecade(movies: List[ProcessedMovieRecord]): Map[String, List[ProcessedMovieRecord]] = {
    movies.groupBy(_.decade)
  }
  
  /** Computes total votes using fold. */
  def computeTotalVotes(movies: List[MovieRecord]): Long = {
    movies.foldLeft(0L)(_ + _.votes)
  }
}
