/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.json.types;

import java.util.List;
import java.util.Map;

public class GlusterVolumeClaim {

    private String apiVersion = null;

    private String kind = null;

    private Map<String, String> metadata = null;

    private PVCSpec spec = null;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public PVCSpec getSpec() {
        return spec;
    }

    public void setSpec(PVCSpec spec) {
        this.spec = spec;
    }

    public static class PVCSpec {
        public PVCSpec() {

        }
        private Map<String, String> capacity;
        private List<String> accessModes;
        private GlusterFS glusterfs;

        public Map<String, String> getCapacity() {
            return capacity;
        }

        public void setCapacity(Map<String, String> capacity) {
            this.capacity = capacity;
        }

        public List<String> getAccessModes() {
            return accessModes;
        }

        public void setAccessModes(List<String> accessModes) {
            this.accessModes = accessModes;
        }

        public GlusterFS getGlusterfs() {
            return glusterfs;
        }

        public void setGlusterfs(GlusterFS glusterfs) {
            this.glusterfs = glusterfs;
        }
    };

    public static class GlusterFS {

        public GlusterFS() {

        }
        private String endpoints;
        private String path;

        public String getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(String endpoint) {
            this.endpoints = endpoint;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
