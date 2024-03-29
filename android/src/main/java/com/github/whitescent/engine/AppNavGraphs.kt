package com.github.whitescent.engine

import com.github.whitescent.engine.destinations.AboutLibraryDestination
import com.github.whitescent.engine.destinations.AppScaffoldDestination
import com.github.whitescent.engine.destinations.ConsoleDestination
import com.github.whitescent.engine.destinations.EditorDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object AppNavGraphs {

  val main = object: NavGraphSpec {
    override val route = "main"
    override val startRoute = AppScaffoldDestination
    override val destinationsByRoute = listOf(
      AppScaffoldDestination,
    ).associateBy { it.route }
  }

  val presets = object : NavGraphSpec {
    override val route = "presets"
    override val startRoute = EditorDestination
    override val destinationsByRoute = listOf(
      EditorDestination,
    ).associateBy { it.route }
  }

  val console = object : NavGraphSpec {
    override val route = "console"
    override val startRoute = ConsoleDestination
    override val destinationsByRoute = listOf(
      ConsoleDestination,
    ).associateBy { it.route }
  }

  val settings = object : NavGraphSpec {
    override val route = "settings"
    override val startRoute = AboutLibraryDestination
    override val destinationsByRoute = listOf(
      AboutLibraryDestination,
    ).associateBy { it.route }
  }


  val root = object: NavGraphSpec {
    override val route = "root"
    override val startRoute = main
    override val destinationsByRoute = emptyMap<String, DestinationSpec<*>>()
    override val nestedNavGraphs = listOf(
      main,
      presets,
      console,
      settings
    )
  }

}
