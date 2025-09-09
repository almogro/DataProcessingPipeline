package com.functionalpipeline.functions

import com.functionalpipeline.models.MovieRecord

/**
 * Pure functions for parsing and validating data.
 * 
 * These functions demonstrate functional programming principles
 * including immutability, pure functions, and error handling.
 */
object DataParsingFunctions {
  
  /**
   * Parses a CSV line into a MovieRecord.
   * 
   * This is a pure function that takes a string and returns an Option[MovieRecord],
   * demonstrating functional error handling without exceptions.
   * 
   * @param line CSV line to parse
   * @return Some(MovieRecord) if parsing succeeds, None otherwise
   */
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
  
  /**
   * Validates a movie record for completeness and data quality.
   * 
   * @param movie The movie record to validate
   * @return true if the record is valid, false otherwise
   */
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
  
  /**
   * Parses and validates a movie record in one step.
   * 
   * This demonstrates function composition in functional programming.
   * 
   * @param line CSV line to parse
   * @return Some(MovieRecord) if parsing and validation succeed, None otherwise
   */
  def parseAndValidateMovieRecord(line: String): Option[MovieRecord] = {
    parseMovieRecord(line).filter(isValidMovieRecord)
  }
}
