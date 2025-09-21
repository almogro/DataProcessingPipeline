package com.functionalpipeline.functions

import com.functionalpipeline.functions.Combinators._
import com.functionalpipeline.models.MovieRecord

/** Pure functions for parsing and validating data. */
object DataParsingFunctions {

  /** Parses CSV line to MovieRecord with error handling. */
  def parseMovieRecord(line: String): Option[MovieRecord] = {
    val fields = line.split(",")

    if (fields.length >= 6) {
      try {
        Some(MovieRecord(
          id = fields(0).trim,
          title = fields(1).trim,
          year = fields(2).trim.toInt,
          genre = fields(3).trim,
          rating = fields(4).trim.toDouble,
          votes = fields(5).trim.toLong
        ))
      } catch {
        case _: NumberFormatException => None
        case _: IndexOutOfBoundsException => None
      }
    } else {
      None
    }
  }

  /** Validates movie record for data quality. */
  def isValidMovieRecord(movie: MovieRecord): Boolean = {
    movie.id.nonEmpty &&
      movie.title.nonEmpty &&
      movie.year > 1900 &&
      movie.year <= 2024 &&
      movie.genre.nonEmpty &&
      movie.rating >= 0.0 &&
      movie.rating <= 10.0 &&
      movie.votes >= 0
  }

  /** Parses and validates movie record in one step. */
  def parseAndValidateMovieRecord(line: String): Option[MovieRecord] = {
    parseMovieRecord(line).filter(isValidMovieRecord)
  }

  /** Parses and enriches movie record using mapOption combinator. */
  def parseAndEnrichMovieRecord(line: String): Option[String] = {
    val parseResult = parseMovieRecord(line)
    val enrichFunction = (movie: MovieRecord) => s"${movie.title} (${movie.year}) - ${movie.genre} - Rating: ${movie.rating}/10"

    mapOption(enrichFunction)(parseResult)
  }

  /** Parses and conditionally cleans movie title using when combinator. */
  def parseAndCleanMovieRecord(line: String): Option[MovieRecord] = {
    val parseResult = parseMovieRecord(line)
    val cleanTitle = (movie: MovieRecord) =>
      movie.copy(title = when(movie.title.contains("  "))(removeExtraSpaces)(movie.title))

    mapOption(cleanTitle)(parseResult)
  }

  /** Removes extra spaces from movie titles. */
  private def removeExtraSpaces(title: String): String = title.replaceAll("\\s+", " ").trim
}
