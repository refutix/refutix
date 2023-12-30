/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.refutix.refutix.server.data.vo;

/** VO of metadata manifest. */
public class ManifestsVO {
    private final String fileName;
    private final Long fileSize;
    private final Long numAddedFiles;

    public ManifestsVO(String fileName, Long fileSize, Long numAddedFiles) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.numAddedFiles = numAddedFiles;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Long getNumAddedFiles() {
        return numAddedFiles;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** ManifestsInfoVo Builder. */
    public static class Builder {
        private String fileName;
        private Long fileSize;
        private Long numAddedFiles;

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setFileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder setNumAddedFiles(Long numAddedFiles) {
            this.numAddedFiles = numAddedFiles;
            return this;
        }

        public ManifestsVO build() {
            return new ManifestsVO(fileName, fileSize, numAddedFiles);
        }
    }
}
