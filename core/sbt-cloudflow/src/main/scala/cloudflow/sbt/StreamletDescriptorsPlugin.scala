/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloudflow.sbt

import com.typesafe.config._
import sbt._
import sbt.Keys._
import spray.json._
import cloudflow.sbt.CloudflowKeys._
import cloudflow.blueprint.StreamletDescriptorFormat._
import cloudflow.blueprint.StreamletDescriptor

/**
 * Plugin that generates a json containing a Map of class name and `StreamletDescriptor`
 * for all streamlets found in this sub-project. These files are used later to insert the
 * proper image names into the `StreamletDescriptor` and `StreamletDeployment` s present
 * in the `ApplicationDescriptor` generated by `verifyBlueprint` task.
 *
 * Note that `verifyBlueprint` being executed from the top level project sets all image names
 * to the top level project name.
 */
object StreamletDescriptorsPlugin extends AutoPlugin {

  override def requires =
    CommonSettingsAndTasksPlugin && StreamletScannerPlugin

  override def projectSettings = Seq(
    cloudflowDockerImageName := Def.task {
          Some(DockerImageName((ThisProject / name).value.toLowerCase, (ThisProject / cloudflowBuildNumber).value.buildNumber))
        }.value,
    streamletDescriptorsInProject := Def.taskDyn {
          val detectedStreamlets = cloudflowStreamletDescriptors.value
          // TODO add a streamlet descriptors file to jar META-INF dir, could be useful for discovery.
          buildStreamletDescriptors(detectedStreamlets)
        }.value
  )

  private[sbt] def buildStreamletDescriptors(
      detectedStreamlets: Map[String, Config]
  ): Def.Initialize[Task[Map[String, StreamletDescriptor]]] =
    Def.task {
      val detectedStreamletDescriptors = detectedStreamlets.mapValues { configDescriptor =>
        val jsonString = configDescriptor.root().render(ConfigRenderOptions.concise())
        jsonString.parseJson
          .convertTo[cloudflow.blueprint.StreamletDescriptor]
      }
      detectedStreamletDescriptors
    }
}
