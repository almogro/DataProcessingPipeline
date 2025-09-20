package com.functionalpipeline.functions

import com.functionalpipeline.models.MovieRecord

/** Data filtering functions using higher-order functions. */
object DataFilteringFunctions {

  /** Checks if movie meets high quality criteria. */
  def isHighQualityMovie(movie: MovieRecord): Boolean = {
    movie.rating >= 7.0 && movie.votes >= 1000
  }

  /** Returns function to check if movie is from specific decade (currying). */
  def isFromDecade(decade: String): MovieRecord => Boolean = {
    val decadeStart = decade.take(4).toInt
    val decadeEnd = decadeStart + 9

    (movie: MovieRecord) => movie.year >= decadeStart && movie.year <= decadeEnd
  }

  /** Returns function to check minimum rating (currying). */
  def hasMinimumRating(minRating: Double): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.rating >= minRating
  }

  /** Returns function to check minimum votes. */
  def hasMinimumVotes(minVotes: Long): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.votes >= minVotes
  }

  /** Combines multiple filter predicates with AND logic. */
  def combineFilters(predicates: List[MovieRecord => Boolean]): MovieRecord => Boolean = {
    (movie: MovieRecord) => predicates.forall(_(movie))
  }

  /** Returns function to check specific genre. */
  def isGenre(genre: String): MovieRecord => Boolean = {
    (movie: MovieRecord) => movie.genre.equalsIgnoreCase(genre)
  }
}
