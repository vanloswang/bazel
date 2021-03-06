// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.analysis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionRegistry;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.MiddlemanFactory;
import com.google.devtools.build.lib.actions.Root;
import com.google.devtools.build.lib.analysis.buildinfo.BuildInfoFactory.BuildInfoKey;
import com.google.devtools.build.lib.events.EventHandler;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.skyframe.SkyFunction;

/**
 * The set of services that are provided to {@link ConfiguredTarget} objects
 * during initialization.
 */
public interface AnalysisEnvironment extends ActionRegistry {
  /**
   * Returns a callback to be used in this build for reporting analysis errors.
   */
  EventHandler getEventHandler();

  /**
   * Returns whether any errors were reported to this instance.
   */
  boolean hasErrors();

  /**
   * Returns the artifact for the derived file {@code rootRelativePath}.
   *
   * <p>Creates the artifact if necessary and sets the root of that artifact to {@code root}.
   */
  Artifact getDerivedArtifact(PathFragment rootRelativePath, Root root);

  /**
   * Returns an artifact for the derived file {@code rootRelativePath} whose changes do not cause
   * a rebuild.
   *
   * <p>Creates the artifact if necessary and sets the root of that artifact to {@code root}.
   *
   * <p>This is useful for files that store data that changes very frequently (e.g. current time)
   * but does not substantially affect the result of the build.
   */
  Artifact getConstantMetadataArtifact(PathFragment rootRelativePath,
      Root root);

  /**
   * Returns the artifact for the derived file {@code rootRelativePath},
   * creating it if necessary, and setting the root of that artifact to
   * {@code root}. The artifact will represent the output directory of a {@code Fileset}.
   */
  Artifact getFilesetArtifact(PathFragment rootRelativePath, Root root);

  /**
   * Returns the artifact for the specified tool.
   */
  Artifact getEmbeddedToolArtifact(String embeddedPath);

  /**
   * Returns the middleman factory associated with the build.
   */
  // TODO(bazel-team): remove this method and replace it with delegate methods.
  MiddlemanFactory getMiddlemanFactory();

  /**
   * Returns the generating action for the given local artifact.
   *
   * If the artifact was created in another analysis environment (e.g. by a different configured
   * target instance) or the artifact is a source artifact, it returns null.
   */
  Action getLocalGeneratingAction(Artifact artifact);

  /**
   * Returns the actions that were registered so far with this analysis environment, that is, all
   * the actions that were created by the current target being analyzed.
   */
  Iterable<Action> getRegisteredActions();

  /**
   * Returns the Skyframe SkyFunction.Environment if available. Otherwise, null.
   *
   * <p>If you need to use this for something other than genquery, please think long and hard
   * about that.
   */
  SkyFunction.Environment getSkyframeEnv();

  /**
   * Returns the Artifact that is used to hold the non-volatile workspace status for the current
   * build request.
   */
  Artifact getStableWorkspaceStatusArtifact();

  /**
   * Returns the Artifact that is used to hold the volatile workspace status (e.g. build
   * changelist) for the current build request.
   */
  Artifact getVolatileWorkspaceStatusArtifact();

  /**
   * Returns the Artifacts that contain the workspace status for the current build request.
   *
   * @param ruleContext the rule to use for error reporting and to determine the
   *        configuration
   */
  ImmutableList<Artifact> getBuildInfo(RuleContext ruleContext, BuildInfoKey key);

  /**
   * Returns the set of orphan Artifacts (i.e. Artifacts without generating action). Should only be
   * called after the ConfiguredTarget is created.
   */
  ImmutableSet<Artifact> getOrphanArtifacts();
}
