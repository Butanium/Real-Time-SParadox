import scala.scalanative.build.*

scalaVersion := "3.2.2"
enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  _.withIncrementalCompilation(true)
    .withLTO(LTO.none)
    .withMode(Mode.debug)
}

githubSuppressPublicationWarning := true
githubTokenSource := TokenSource.GitConfig("github.token")

resolvers += Resolver.githubPackages("lafeychine")
libraryDependencies += "io.github.lafeychine" %%% "scala-native-sfml" % "0.3.1"