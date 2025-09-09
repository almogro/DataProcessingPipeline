package com.functionalpipeline.functions

import com.functionalpipeline.models.MovieRecord

/**
 * Pure functions for filtering data based on various criteria.
 * 
 * These functions demonstrate higher-order functions and
 * function composition in functional programming.
 */
object DataFilteringFunctions {
  
  /**
   * Checks if a movie meets high quality criteria.
   * 
   * @param movie The movie record to check
   * @return true if the movie meets quality criteria
   */
  def isHighQualityMovie(movie: MovieRecord): Boolean = {
    movie.rating >= 7.0 && movie.votes >= 1000
  }
  
  /**
   * Checks if a movie is from a specific decade.
   * 
   * This demonstrates currying - a function that returns another function.
   * 
   * @param decade The decade to filter by (e.g., "1990s")
   * @return A function that checks if a movie is from that decade
   */
  def isFromDecade(decade: String): MovieRecord => Boolean = {
    val decadeStart = decade.take(4).toInt
    val decadeEnd = decadeStart + 9
    
    (movie: MovieRecord) => movie.year >= decadeStart && movie.year <= decadeEnd
  }
  
  /**
   * Checks if a movie has a minimum rating.
   * 
   * Another example of currying.
   * 
   * @param minRating Minimum rating threshold
   * @return A function that checks if a movie meets the rating threshold
   */
  def hasMinimumRating(minRating: Double): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.rating >= minRating
  }
  
  /**
   * Checks if a movie has a minimum number of votes.
   * 
   * @param minVotes Minimum votes threshold
   * @return A function that checks if a movie meets the votes threshold
   */
  def hasMinimumVotes(minVotes: Long): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.votes >= minVotes
  }
  
  /**
   * Combines multiple filter predicates using logical AND.
   * 
   * This demonstrates function composition and higher-order functions.
   * 
   * @param predicates List of filter predicates
   * @return A function that applies all predicates with AND logic
   */
  def combineFilters(predicates: List[MovieRecord => Boolean]): MovieRecord => Boolean = {
    (movie: MovieRecord) => predicates.forall(_(movie))
  }
  
  /**
   * Creates a filter for movies from a specific genre.
   * 
   * @param genre The genre to filter by
   * @return A function that checks if a movie belongs to the specified genre
   */
  def isGenre(genre: String): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.genre.equalsIgnoreCase(genre)
  }
}
