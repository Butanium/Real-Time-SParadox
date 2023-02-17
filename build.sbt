import scala.scalanative.build.*

scalaVersion := "3.2.2"
enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  _.withIncrementalCompilation(true)
    .withLTO(LTO.thin)
    .withMode(Mode.debug)
}
scalacOptions ++= Seq("-unchecked", "-deprecation")
githubSuppressPublicationWarning := true
githubTokenSource := TokenSource.GitConfig("github.token")

resolvers += Resolver.githubPackages("lafeychine")
libraryDependencies += "io.github.lafeychine" %%% "scala-native-sfml" % "0.3.6"
// libraryDependencies += "com.lihaoyi" %% "upickle" % "3.0.0-M1" // for json
