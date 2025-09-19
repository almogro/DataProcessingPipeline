package com.functionalpipeline.functions

/** Custom combinators for function composition. */
object Combinators {
  
  /** Applies function to value if condition is met. */
  def when[A](condition: Boolean)(f: A => A)(value: A): A = {
    if (condition) f(value) else value
  }
  
  /** Retries function with error handling using Either. */
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
  
  /** Applies function to Option value safely. */
  def mapOption[A, B](f: A => B)(value: Option[A]): Option[B] = {
    value.map(f)
  }
}
