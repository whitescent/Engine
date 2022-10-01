package com.github.nthily.engine

import com.github.nthily.engine.destinations.AppScaffoldDestination
import com.github.nthily.engine.destinations.PresetsEditorDestination
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
    override val startRoute = PresetsEditorDestination
    override val destinationsByRoute = listOf(
      PresetsEditorDestination,
    ).associateBy { it.route }
  }

  val root = object: NavGraphSpec {
    override val route = "root"
    override val startRoute = main
    override val destinationsByRoute = emptyMap<String, DestinationSpec<*>>()
    override val nestedNavGraphs = listOf(
      main,
      presets
    )
  }

}
