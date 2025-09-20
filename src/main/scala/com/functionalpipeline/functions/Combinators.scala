package com.functionalpipeline.functions

/** Provides custom combinators for function composition as described.
 *
 *  This object provides higher-order functions that demonstrate advanced functional
 *  programming concepts including currying, partial application, and function composition.
 *  Combinators are essential building blocks for creating reusable, composable functions
 *  that follow functional programming principles.
 *
 *  @author Almog Roter and Yonathan Cohen
 *  @version 1.0.0
 *  @since September 2025
 */
object Combinators {
  
  /** Applies function to value if condition is met as described.
   *
   *  This combinator demonstrates currying and conditional function application.
   *  It takes a condition, a function, and a value, and applies the function
   *  only if the condition is true. This is useful for conditional transformations
   *  in data processing pipelines.
   *
   *  @param condition Boolean condition that determines whether to apply the function
   *  @param f Function to apply if condition is true
   *  @param value The value to potentially transform
   *  @tparam A Type of the value being processed
   *  @return The original value if condition is false, or f(value) if condition is true
   *
   *  @example
   *  {{{
   *  when(score > 0.9)(Math.ceil)(score)  // Rounds up only if score > 0.9
   *  }}}
   */
  def when[A](condition: Boolean)(f: A => A)(value: A): A = {
    if (condition) f(value) else value
  }
  
  /** Retries function with error handling using Either as described.
   *
   *  This combinator demonstrates functional error handling and tail recursion.
   *  It attempts to execute a function with automatic retry logic, returning
   *  either a successful result or the last error encountered. This is useful
   *  for handling transient failures in data processing operations.
   *
   *  @param f Function to retry on failure
   *  @param maxRetries Maximum number of retry attempts
   *  @param value Input value for the function
   *  @tparam A Input type
   *  @tparam B Output type
   *  @return Either[Throwable, B] containing either the result or the error
   *
   *  @example
   *  {{{
   *  retry(loadData, 3)(spark, path)  // Retry data loading up to 3 times
   *  }}}
   */
  def retry[A, B](f: A => B, maxRetries: Int)(value: A): Either[Throwable, B] = {
    /*
     * Tail recursive helper function for retry logic
     * Demonstrates tail recursion and functional error handling
     */
    def retryHelper(remainingRetries: Int): Either[Throwable, B] = {
      try {
        Right(f(value))  // Success case - wrap result in Right
      } catch {
        case e: Throwable if remainingRetries > 0 => 
          retryHelper(remainingRetries - 1)  // Retry with one less attempt
        case e: Throwable => 
          Left(e)  // Final failure - return error in Left
      }
    }
    retryHelper(maxRetries)
  }
  
  /** Applies function to Option value safely as described.
   *
   *  This combinator demonstrates safe function application over optional values.
   *  It applies a function to the value inside an Option if it exists, otherwise
   *  returns None. This prevents null pointer exceptions and promotes safe
   *  functional programming practices.
   *
   *  @param f Function to apply to the optional value
   *  @param value Optional value to process
   *  @tparam A Input type
   *  @tparam B Output type
   *  @return Option[B] containing the result if input was Some, None otherwise
   *
   *  @example
   *  {{{
   *  mapOption(_.toUpperCase)(Some("hello"))  // Some("HELLO")
   *  mapOption(_.toUpperCase)(None)           // None
   *  }}}
   */
  def mapOption[A, B](f: A => B)(value: Option[A]): Option[B] = {
    value.map(f)  // Safe function application over Option
  }
}
