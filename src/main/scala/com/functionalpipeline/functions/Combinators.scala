package com.functionalpipeline.functions

/**
 * Custom combinators demonstrating functional programming principles.
 * 
 * This object contains custom combinator implementations that show
 * how to compose functions in functional programming.
 */
object Combinators {
  
  /**
   * Custom combinator that applies a function to a value if a condition is met.
   * 
   * This demonstrates the combinator pattern in functional programming.
   * 
   * @param condition The condition to check
   * @param f The function to apply if condition is true
   * @param value The value to potentially transform
   * @return The transformed value if condition is true, original value otherwise
   */
  def when[A](condition: Boolean)(f: A => A)(value: A): A = {
    if (condition) f(value) else value
  }
  
  /**
   * Custom combinator that chains multiple transformations.
   * 
   * This demonstrates function composition and the pipeline pattern.
   * 
   * @param transformations List of transformation functions
   * @param value The initial value
   * @return The value after applying all transformations
   */
  def pipe[A](transformations: List[A => A])(value: A): A = {
    transformations.foldLeft(value)((acc, f) => f(acc))
  }
  
  /**
   * Custom combinator that applies a function and then applies another function to the result.
   * 
   * This is similar to Function1.andThen but demonstrates custom implementation.
   * 
   * @param f First function to apply
   * @param g Second function to apply to the result of f
   * @param value The input value
   * @return The result of applying g(f(value))
   */
  def andThen[A, B, C](f: A => B, g: B => C)(value: A): C = {
    g(f(value))
  }
  
  /**
   * Custom combinator that applies a function and then applies another function to the original value.
   * 
   * This is similar to Function1.compose but demonstrates custom implementation.
   * 
   * @param f First function to apply
   * @param g Second function to apply to the original value
   * @param value The input value
   * @return The result of applying f(g(value))
   */
  def compose[A, B, C](f: B => C, g: A => B)(value: A): C = {
    f(g(value))
  }
  
  /**
   * Custom combinator that retries a function a specified number of times.
   * 
   * This demonstrates error handling and retry logic in functional programming.
   * 
   * @param f The function to retry
   * @param maxRetries Maximum number of retries
   * @param value The input value
   * @return The result of the function or the last error
   */
  def retry[A, B](f: A => B, maxRetries: Int)(value: A): Either[Throwable, B] = {
    def retryHelper(remainingRetries: Int): Either[Throwable, B] = {
      try {
        Right(f(value))
      } catch {
        case e: Throwable if remainingRetries > 0 => retryHelper(remainingRetries - 1)
        case e: Throwable => Left(e)
      }
    }
    retryHelper(maxRetries)
  }
  
  /**
   * Custom combinator that applies a function only if the value is not null/None.
   * 
   * This demonstrates safe function application in functional programming.
   * 
   * @param f The function to apply
   * @param value The optional value
   * @return The result wrapped in Option
   */
  def mapOption[A, B](f: A => B)(value: Option[A]): Option[B] = {
    value.map(f)
  }
  
  /**
   * Custom combinator that applies a function to each element of a list and flattens the result.
   * 
   * This is similar to List.flatMap but demonstrates custom implementation.
   * 
   * @param f The function that returns a list for each element
   * @param list The input list
   * @return The flattened result
   */
  def flatMap[A, B](f: A => List[B])(list: List[A]): List[B] = {
    list.flatMap(f)
  }
}
