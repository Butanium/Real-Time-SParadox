import scala.scalanative.build.*

scalaVersion := "3.2.2"
enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  // not sure what it does: _.withIncrementalCompilation(true)
  // use this if you want a more explicit stack trace:
  // _.withLTO(LTO.full).withMode(Mode.debug)
  // Use this for faster compilation:
  _.withLTO(LTO.thin).withMode(Mode.debug)
}

githubSuppressPublicationWarning := true
githubTokenSource := TokenSource.GitConfig("github.token")

resolvers += Resolver.githubPackages("lafeychine")
libraryDependencies += "io.github.lafeychine" %%% "scala-native-sfml" % "0.4.1"
// libraryDependencies += "com.lihaoyi" %% "upickle" % "3.0.0-M1" // for json
