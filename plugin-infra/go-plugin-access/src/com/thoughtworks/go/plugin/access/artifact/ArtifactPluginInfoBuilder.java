/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package com.thoughtworks.go.plugin.access.artifact;

import com.thoughtworks.go.plugin.access.common.PluginInfoBuilder;
import com.thoughtworks.go.plugin.domain.artifact.ArtifactPluginInfo;
import com.thoughtworks.go.plugin.domain.common.*;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArtifactPluginInfoBuilder implements PluginInfoBuilder<ArtifactPluginInfo> {

    private ArtifactExtension artifactExtension;

    @Autowired
    public ArtifactPluginInfoBuilder(ArtifactExtension artifactExtension) {
        this.artifactExtension = artifactExtension;
    }

    @Override
    public ArtifactPluginInfo pluginInfoFor(GoPluginDescriptor descriptor) {
        PluggableInstanceSettings storeConfigSettings = storeConfigMetadata(descriptor.id());
        PluggableInstanceSettings publishArtifactConfigSettings = publishArtifactMetadata(descriptor.id());
        PluggableInstanceSettings fetchArtifactConfigSettings = fetchArtifactMetadata(descriptor.id());
        Image image = image(descriptor.id());
        return new ArtifactPluginInfo(descriptor, storeConfigSettings, publishArtifactConfigSettings, fetchArtifactConfigSettings, image);
    }

    private PluggableInstanceSettings storeConfigMetadata(String pluginId) {
        return new PluggableInstanceSettings(artifactExtension.getArtifactStoreMetadata(pluginId),
                new PluginView(artifactExtension.getArtifactStoreView(pluginId)));
    }

    private PluggableInstanceSettings publishArtifactMetadata(String pluginId) {
        return new PluggableInstanceSettings(artifactExtension.getPublishArtifactMetadata(pluginId),
                new PluginView(artifactExtension.getPublishArtifactView(pluginId)));
    }

    private PluggableInstanceSettings fetchArtifactMetadata(String pluginId) {
        // fetch does not require secure properties.
        final List<PluginConfiguration> fetchArtifactMetadata = artifactExtension.getFetchArtifactMetadata(pluginId);

        final List<PluginConfiguration> pluginConfigurations = new ArrayList<>();
        for (PluginConfiguration metadata : fetchArtifactMetadata) {
            pluginConfigurations.add(new PluginConfiguration(metadata.getKey(), new Metadata(metadata.getMetadata().isRequired(), false)));
        }

        return new PluggableInstanceSettings(pluginConfigurations,
                new PluginView(artifactExtension.getFetchArtifactView(pluginId)));
    }

    private Image image(String pluginId) {
        return artifactExtension.getIcon(pluginId);
    }
}


