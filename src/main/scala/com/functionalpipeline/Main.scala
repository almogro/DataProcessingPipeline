package com.functionalpipeline

import com.functionalpipeline.pipeline.DataPipeline
import com.functionalpipeline.io.DataLoader
import com.functionalpipeline.io.DataSaver
import com.functionalpipeline.functions.{DataTransformationFunctions, DataParsingFunctions}
import org.apache.spark.sql.SparkSession

/**
 * Main entry point for the Functional Data Processing Pipeline.
 * 
 * This application demonstrates functional programming principles
 * applied to Apache Spark data processing.
 */
object Main {
  
  def main(args: Array[String]): Unit = {
    // Create Spark session
    val spark = SparkSession.builder()
      .appName("Functional Data Processing Pipeline")
      .master("local[*]")
      .getOrCreate()
    
    try {
      // Initialize the data pipeline
      val pipeline = new DataPipeline(spark)
      
      // Load data from external source
      val inputPath = args.headOption.getOrElse("data/input")
      val outputPath = args.lift(1).getOrElse("data/output")
      
      val rawData = DataLoader.loadData(spark, inputPath)
      
      // Process high-quality data (filtered)
      val highQualityData = pipeline.processData(rawData)
      DataSaver.saveData(highQualityData, s"$outputPath/high_quality_movies")
      
      // Process all data (unfiltered)
      val allData = pipeline.processAllData(rawData)
      DataSaver.saveData(allData, s"$outputPath/all_movies")
      
      // Generate summarization reports
      println("Generating summarization reports...")
      
      // Genre statistics for high-quality movies
      val genreStats = pipeline.computeGenreStatistics(highQualityData)
      DataSaver.saveGenreStatistics(genreStats, s"$outputPath/genre_statistics")
      
      // Top movies by decade for high-quality movies
      val topMoviesByDecade = pipeline.findTopMoviesByDecade(highQualityData)
      DataSaver.saveTopMoviesByDecade(topMoviesByDecade, s"$outputPath/top_movies_by_decade")
      
      // Display summary statistics
      val genreStatsList = genreStats.collect()
      val topMoviesList = topMoviesByDecade.collect()
      
      println("\nSUMMARY STATISTICS")
      println("=" * 50)
      
      println("\nGENRE STATISTICS (High-Quality Movies):")
      println("-" * 40)
      genreStatsList.foreach { stats =>
        println(f"${stats.genre}%15s: ${stats.count}%3d movies, avg rating: ${stats.averageRating}%.2f, top movie: ${stats.topMovie}")
      }
      
      println("\nTOP MOVIES BY DECADE:")
      println("-" * 40)
      topMoviesList.foreach { case (decade, movies) =>
        println(s"\n$decade:")
        movies.take(3).foreach { movie =>
          println(f"  • ${movie.movie.title}%30s (${movie.movie.rating}%.1f/10, ${movie.popularityScore}%.3f)")
        }
      }
      
      // Demonstrate combinator usage
      println("\nCOMBINATOR DEMONSTRATIONS:")
      println("=" * 50)
      
      // Demonstrate when combinator
      val sampleMovie = allData.take(1).head.movie
      println(s"\n1. 'when' combinator - Popularity boost for high-vote movies:")
      println(s"   Original: ${sampleMovie.title} - Votes: ${sampleMovie.votes}")
      
      // Demonstrate mapOption combinator
      println(s"\n2. 'mapOption' combinator - Safe parsing:")
      val sampleLine = s"${sampleMovie.id},${sampleMovie.title},${sampleMovie.year},${sampleMovie.genre},${sampleMovie.rating},${sampleMovie.votes}"
      val enriched = DataParsingFunctions.parseAndEnrichMovieRecord(sampleLine)
      println(s"   Enriched: ${enriched.getOrElse("Failed to parse")}")
      
      // Demonstrate retry combinator
      println(s"\n3. 'retry' combinator - Robust data loading:")
      val retryResult = DataLoader.loadDataWithRetry(spark, inputPath, 2)
      retryResult match {
        case Right(_) => println("   Data loaded successfully with retry logic")
        case Left(error) => println(s"   Retry failed: $error")
      }
      
      println("\nData processing pipeline completed successfully!")
      println(s" High-quality movies: $outputPath/high_quality_movies/")
      println(s" All movies: $outputPath/all_movies/")
      println(s" Genre statistics: $outputPath/genre_statistics/")
      println(s" Top movies by decade: $outputPath/top_movies_by_decade/")
      
    } finally {
      spark.stop()
    }
  }
}