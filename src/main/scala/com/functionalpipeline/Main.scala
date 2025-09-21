package com.functionalpipeline

import com.functionalpipeline.functions.{DataParsingFunctions, PatternMatching}
import com.functionalpipeline.io.{DataLoader, DataSaver}
import com.functionalpipeline.pipeline.DataPipeline
import org.apache.spark.sql.SparkSession

/** Provides the main entry point for the Functional Data Processing Pipeline as described.
 *
 * This application demonstrates functional programming principles
 * applied to Apache Spark data processing. It serves as the orchestrator
 * for the entire data processing workflow, maintaining clear separation
 * between I/O operations and pure business logic.
 *
 * The application processes movie data through a functional pipeline
 * that includes data loading, transformation, filtering, enrichment,
 * and analysis operations, all implemented using pure functions and
 * immutable data structures.
 *
 * @author Almog Roter and Yonathan Cohen
 * @version 1.0.0
 * @since September 2025
 */
object Main {

  /** Main entry point for the application as described.
   *
   * This method orchestrates the entire data processing pipeline,
   * demonstrating functional programming principles in a real-world
   * data processing scenario. It maintains clear separation between
   * I/O operations and pure business logic.
   *
   * @param args Command line arguments: [inputPath] [outputPath]
   *              - inputPath: Path to input data directory (default: "data/input")
   *              - outputPath: Path to output data directory (default: "data/output")
   */
  def main(args: Array[String]): Unit = {
    /*
     * Step 1: Initialize Spark session for distributed data processing
     * Uses local mode with all available cores for development
     */
    val spark = SparkSession.builder()
      .appName("Functional Data Processing Pipeline")
      .master("local[*]")
      .getOrCreate()

    try {
      /*
       * Step 2: Initialize the functional data pipeline
       * Dependency injection of Spark session into pipeline
       */
      val dataPipeline = new DataPipeline(spark)

      /*
       * Step 3: Parse command line arguments with defaults
       * Demonstrates functional programming with Option handling
       */
      val inputPath = args.headOption.getOrElse("data/input")
      val outputPath = args.lift(1).getOrElse("data/output")

      /*
       * Step 4: Load raw data from external source
       * I/O operation separated from business logic
       */
      val rawData = DataLoader.loadData(spark, inputPath)

      /*
       * Step 5: Process high-quality data (filtered)
       * Demonstrates pure function composition and data transformation
       */
      val highQualityData = dataPipeline.processData(rawData)
      DataSaver.saveData(highQualityData, s"$outputPath/high_quality_movies")

      /*
       * Step 6: Process all data (unfiltered)
       * Provides comprehensive dataset for analysis
       */
      val allData = dataPipeline.processAllData(rawData)
      DataSaver.saveData(allData, s"$outputPath/all_movies")

      /*
       * Step 7: Generate summarization reports
       * Demonstrates data aggregation and analysis capabilities
       */
      println("Generating summarization reports...")

      // Genre statistics for high-quality movies
      val genreStats = dataPipeline.computeGenreStatistics(highQualityData)
      DataSaver.saveGenreStatistics(genreStats, s"$outputPath/genre_statistics")

      // Top movies by decade for high-quality movies
      val topMoviesByDecade = dataPipeline.findTopMoviesByDecade(highQualityData)
      DataSaver.saveTopMoviesByDecade(topMoviesByDecade, s"$outputPath/top_movies_by_decade")

      /*
       * Step 8: Display summary statistics
       * Demonstrates data collection and presentation
       */
      val genreStatsList = genreStats.collect()
      val topMoviesList = topMoviesByDecade.collect()

      /*
       * Step 10: Display comprehensive summary statistics
       * Demonstrates data presentation and analysis results
       */
      println("\nSUMMARY STATISTICS")
      println("=" * 50)

      // Display genre statistics with formatted output
      println("\nGENRE STATISTICS (High-Quality Movies):")
      println("-" * 40)
      genreStatsList.foreach { stats =>
        println(f"${stats.genre}%15s: ${stats.count}%3d movies, avg rating: ${stats.averageRating}%.2f, top movie: ${stats.topMovie}")
      }

      // Display top movies by decade with formatted output
      println("\nTOP MOVIES BY DECADE:")
      println("-" * 40)
      topMoviesList.foreach { case (decade, movies) =>
        println(s"\n$decade:")
        movies.take(3).foreach { movie =>
          println(f"  • ${movie.movie.title}%30s (${movie.movie.rating}%.1f/10, ${movie.popularityScore}%.3f)")
        }
      }

      /*
       * Step 11: Demonstrate custom combinator usage
       * Shows practical application of functional programming concepts
       */
      println("\nCOMBINATOR DEMONSTRATIONS:")
      println("=" * 50)

      // Demonstrate when combinator with sample data
      val sampleMovie = allData.take(1).head.movie
      println(s"\n1. 'when' combinator - Popularity boost for high-vote movies:")
      println(s"   Original: ${sampleMovie.title} - Votes: ${sampleMovie.votes}")

      // Demonstrate mapOption combinator with safe parsing
      println(s"\n2. 'mapOption' combinator - Safe parsing:")
      val sampleLine = s"${sampleMovie.id},${sampleMovie.title},${sampleMovie.year},${sampleMovie.genre},${sampleMovie.rating},${sampleMovie.votes}"
      val enriched = DataParsingFunctions.parseAndEnrichMovieRecord(sampleLine)
      println(s"   Enriched: ${enriched.getOrElse("Failed to parse")}")

      // Demonstrate retry combinator with robust data loading
      println(s"\n3. 'retry' combinator - Robust data loading:")
      val retryResult = DataLoader.loadDataWithRetry(spark, inputPath, 2)
      retryResult match {
        case Right(_) => println("   Data loaded successfully with retry logic")
        case Left(error) => println(s"   Retry failed: $error")
      }

      /*
       * Step 12: Demonstrate advanced functional pattern matching
       * Shows sophisticated pattern matching with custom extractors, 
       * higher-order functions, and functional combinators
       */
      println("\nADVANCED PATTERN MATCHING DEMONSTRATIONS:")
      println("=" * 50)

      // Demonstrate custom extractors and pattern matching
      println(s"\n1. Custom Extractors and Pattern Matching:")
      val sampleMovieForPatternMatching = allData.take(1).head.movie
      val extractedResult = PatternMatching.processMovieWithExtractors(sampleMovieForPatternMatching)
      println(s"   Extracted: $extractedResult")

      // Demonstrate higher-order functions with pattern matching
      println(s"\n2. Higher-Order Functions with Pattern Matching:")
      val premiumProcessor = PatternMatching.createMovieProcessor("premium")
      val premiumResult = premiumProcessor(sampleMovieForPatternMatching)
      println(s"   Premium Processor: $premiumResult")

      val popularProcessor = PatternMatching.createMovieProcessor("popular")
      val popularResult = popularProcessor(sampleMovieForPatternMatching)
      println(s"   Popular Processor: $popularResult")

      // Demonstrate monadic operations with pattern matching
      println(s"\n3. Monadic Operations with Pattern Matching:")
      val safeResult = PatternMatching.processMovieSafely(Some(sampleMovieForPatternMatching))
      safeResult match {
        case Right(result) => println(s"   Safe Processing: $result")
        case Left(error) => println(s"   Error: $error")
      }

      // Demonstrate functional combinators with pattern matching
      println(s"\n4. Functional Combinators with Pattern Matching:")
      val combinatorResult = PatternMatching.processMovieWithCombinators(sampleMovieForPatternMatching)
      println(s"   Combinator Result: $combinatorResult")

      // Demonstrate functional composition with pattern matching
      println(s"\n5. Functional Composition with Pattern Matching:")
      val analyzerResults = PatternMatching.analyzeMovieWithMultipleAnalyzers(sampleMovieForPatternMatching)
      analyzerResults.foreach { result =>
        println(s"   Analysis: $result")
      }

      // Demonstrate functional pipeline with pattern matching
      println(s"\n6. Functional Pipeline with Pattern Matching:")
      val patternMatchingPipeline = PatternMatching.createMovieProcessorPipeline(List("premium", "popular", "decade"))
      val pipelineResults = patternMatchingPipeline(sampleMovieForPatternMatching)
      pipelineResults.foreach { result =>
        println(s"   Pipeline: $result")
      }

      /*
       * Step 13: Display completion status and output locations
       * Provides user feedback on successful processing
       */
      println("\nData processing pipeline completed successfully!")
      println(s" High-quality movies: $outputPath/high_quality_movies/")
      println(s" All movies: $outputPath/all_movies/")
      println(s" Genre statistics: $outputPath/genre_statistics/")
      println(s" Top movies by decade: $outputPath/top_movies_by_decade/")

    } finally {
      /*
       * Step 13: Clean up resources
       * Ensures proper resource management and cleanup
       */
      spark.stop()
    }
  }
}