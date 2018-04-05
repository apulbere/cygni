package com.apulbere.cygni.util

class ParameterResolver(private val args: Array[String]) {
  private val params = resolve(args)

  private def resolve(args: Array[String]) = args.filter(_.contains("=")).map(createParam).toMap

  private def createParam(arg: String) = arg.split("=") match { case param => param(0) -> param(1) }

  def get(param: String): Option[String] = params.get(s"--$param")
}

object ParameterResolver {
  def apply(args: Array[String]): ParameterResolver = new ParameterResolver(args)
}