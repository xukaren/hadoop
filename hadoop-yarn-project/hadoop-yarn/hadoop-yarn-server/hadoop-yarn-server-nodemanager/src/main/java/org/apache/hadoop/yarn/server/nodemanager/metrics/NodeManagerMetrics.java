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
package org.apache.hadoop.yarn.server.nodemanager.metrics;

import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.lib.MutableCounterInt;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import org.apache.hadoop.metrics2.lib.MutableGaugeLong;
import org.apache.hadoop.metrics2.lib.MutableGaugeFloat;
import org.apache.hadoop.metrics2.lib.MutableQuantiles;
import org.apache.hadoop.metrics2.lib.MutableRate;
import org.apache.hadoop.metrics2.source.JvmMetrics;
import org.apache.hadoop.yarn.api.records.Resource;

import org.apache.hadoop.classification.VisibleForTesting;

import static org.apache.hadoop.metrics2.lib.Interns.info;

@Metrics(about="Metrics for node manager", context="yarn")
public class NodeManagerMetrics {
  // CHECKSTYLE:OFF:VisibilityModifier
  @Metric MutableCounterInt containersLaunched;
  @Metric MutableCounterInt containersCompleted;
  @Metric MutableCounterInt containersFailed;
  @Metric MutableCounterInt containersKilled;
  @Metric MutableCounterInt containersRolledBackOnFailure;
  @Metric("# of reInitializing containers")
      MutableGaugeInt containersReIniting;
  @Metric("# of initializing containers")
      MutableGaugeInt containersIniting;
  @Metric MutableGaugeInt containersRunning;
  @Metric("# of paused containers") MutableGaugeInt containersPaused;
  @Metric("Current allocated memory in GB")
      MutableGaugeInt allocatedGB;
  @Metric("Current # of allocated containers")
      MutableGaugeInt allocatedContainers;
  @Metric MutableGaugeInt availableGB;
  @Metric("Current allocated Virtual Cores")
      MutableGaugeInt allocatedVCores;
  @Metric MutableGaugeInt availableVCores;
  @Metric("Container launch duration")
      MutableRate containerLaunchDuration;

  @Metric("Containers queued (Guaranteed)")
  MutableGaugeInt containersGuaranteedQueued;
  @Metric("Containers queued (Opportunistic)")
  MutableGaugeInt containersOpportunisticQueued;

  @Metric("# of bad local dirs")
      MutableGaugeInt badLocalDirs;
  @Metric("# of bad log dirs")
      MutableGaugeInt badLogDirs;
  @Metric("Disk utilization % on good local dirs")
      MutableGaugeInt goodLocalDirsDiskUtilizationPerc;
  @Metric("Disk utilization % on good log dirs")
      MutableGaugeInt goodLogDirsDiskUtilizationPerc;

  @Metric("Current allocated memory by opportunistic containers in GB")
      MutableGaugeLong allocatedOpportunisticGB;
  @Metric("Current allocated Virtual Cores by opportunistic containers")
      MutableGaugeInt allocatedOpportunisticVCores;
  @Metric("# of running opportunistic containers")
      MutableGaugeInt runningOpportunisticContainers;

  @Metric("Local cache size (public and private) before clean (Bytes)")
  MutableGaugeLong cacheSizeBeforeClean;
  @Metric("# of total bytes deleted from the public and private local cache")
  MutableGaugeLong totalBytesDeleted;
  @Metric("# of bytes deleted from the public local cache")
  MutableGaugeLong publicBytesDeleted;
  @Metric("# of bytes deleted from the private local cache")
  MutableGaugeLong privateBytesDeleted;
  @Metric("Current used physical memory by all containers in GB")
  MutableGaugeInt containerUsedMemGB;
  @Metric("Current used virtual memory by all containers in GB")
  MutableGaugeInt containerUsedVMemGB;
  @Metric("Aggregated CPU utilization of all containers")
  MutableGaugeFloat containerCpuUtilization;
  @Metric("Current used memory by this node in GB")
  MutableGaugeInt nodeUsedMemGB;
  @Metric("Current used virtual memory by this node in GB")
  MutableGaugeInt nodeUsedVMemGB;
  @Metric("Current CPU utilization")
  MutableGaugeFloat nodeCpuUtilization;
  @Metric("Current GPU utilization")
  MutableGaugeFloat nodeGpuUtilization;
  @Metric("Current running apps")
  MutableGaugeInt applicationsRunning;

  @Metric("Missed localization requests in bytes")
      MutableCounterLong localizedCacheMissBytes;
  @Metric("Cached localization requests in bytes")
      MutableCounterLong localizedCacheHitBytes;
  @Metric("Localization cache hit ratio (bytes)")
      MutableGaugeInt localizedCacheHitBytesRatio;
  @Metric("Missed localization requests (files)")
      MutableCounterLong localizedCacheMissFiles;
  @Metric("Cached localization requests (files)")
      MutableCounterLong localizedCacheHitFiles;
  @Metric("Localization cache hit ratio (files)")
      MutableGaugeInt localizedCacheHitFilesRatio;
  @Metric("Container localization time in milliseconds")
      MutableRate localizationDurationMillis;

  // CHECKSTYLE:ON:VisibilityModifier

  // Log aggregation metrics
  @Metric("# of log aggregation threads currently in use")
  MutableGaugeInt logAggrThreadsUsed;
  @Metric("# of applications currently waiting for aggregation threads")
  MutableGaugeInt appsWaitingForLogAggregation;
  @Metric("Number of applications with successful log aggregation")
  MutableCounterLong numSuccessfulAppLogAggregations;
  @Metric("Number of applications with failed log aggregation")
  MutableCounterLong numFailedAppLogAggregations;
  @Metric("Number of containers whose log were uploaded successfully")
  MutableCounterLong numSuccessfulContainerLogUploads;
  @Metric("Number of containers whose log were uploaded unsuccessfully")
  MutableCounterLong numFailedContainerLogUploads;
  @Metric("Time spent waiting for a log aggregation thread after application finished (ms)")
  MutableRate logAggregationThreadWaitTimeMs;
  @Metric("Time spent doing log aggregation i.e. time holding log aggr thread (ms)")
  MutableRate logAggregationThreadHoldTimeMs;
  @Metric("Time taken uploading logs to HDFS during log aggregation (ms)")
  MutableRate HDFSUploadDurationMs;

  //Provide quantile counters for all latencies
  private MutableQuantiles waitTimeLatency;
  private MutableQuantiles logAggrLatency;
  private MutableQuantiles HDFSUploadLatency;

  private JvmMetrics jvmMetrics = null;

  private long allocatedMB;
  private long availableMB;
  private long allocatedOpportunisticMB;

  private static MetricsRegistry registry;

  private static final String METRIC_NAME = "LogAggregationMetrics";
  private static final MetricsInfo RECORD_INFO =
      info(METRIC_NAME, "Log Aggregation");

  private NodeManagerMetrics(JvmMetrics jvmMetrics) {
    this.jvmMetrics = jvmMetrics;
    registry = new MetricsRegistry(RECORD_INFO);
    registry.tag(RECORD_INFO, "LogAggregation");
    waitTimeLatency = registry.newQuantiles("waitTimeLatency",
        "latency of waiting for log aggr thread", "ops", "latency", 10);
    logAggrLatency = registry.newQuantiles("logAggrLatency",
        "latency of log aggregation", "ops", "latency", 10);
    HDFSUploadLatency =
        registry.newQuantiles("HDFSUploadLatency",
            "latency of log upload to HDFS", "ops", "latency", 10);
  }

  public static NodeManagerMetrics create() {
    return create(DefaultMetricsSystem.instance());
  }

  private static NodeManagerMetrics create(MetricsSystem ms) {
    JvmMetrics jm = JvmMetrics.initSingleton("NodeManager", null);
    return ms.register(new NodeManagerMetrics(jm));
  }

  public JvmMetrics getJvmMetrics() {
    return jvmMetrics;
  }

  // Potential instrumentation interface methods

  public void launchedContainer() {
    containersLaunched.incr();
  }

  public void completedContainer() {
    containersCompleted.incr();
  }

  public void rollbackContainerOnFailure() {
    containersRolledBackOnFailure.incr();
  }

  public void failedContainer() {
    containersFailed.incr();
  }

  public void killedContainer() {
    containersKilled.incr();
  }

  public void initingContainer() {
    containersIniting.incr();
  }

  public void endInitingContainer() {
    containersIniting.decr();
  }

  public void runningContainer() {
    containersRunning.incr();
  }

  public void endRunningContainer() {
    containersRunning.decr();
  }

  public void reInitingContainer() {
    containersReIniting.incr();
  }

  public void endReInitingContainer() {
    containersReIniting.decr();
  }

  public void runningApplication() {
    applicationsRunning.incr();
  }

  public void endRunningApplication() {
    applicationsRunning.decr();
  }

  public void pausedContainer() {
    containersPaused.incr();
  }

  public void endPausedContainer() {
    containersPaused.decr();
  }

  public void allocateContainer(Resource res) {
    allocatedContainers.incr();
    allocatedMB = allocatedMB + res.getMemorySize();
    allocatedGB.set((int)Math.ceil(allocatedMB/1024d));
    availableMB = availableMB - res.getMemorySize();
    availableGB.set((int)Math.floor(availableMB/1024d));
    allocatedVCores.incr(res.getVirtualCores());
    availableVCores.decr(res.getVirtualCores());
  }

  public void releaseContainer(Resource res) {
    allocatedContainers.decr();
    allocatedMB = allocatedMB - res.getMemorySize();
    allocatedGB.set((int)Math.ceil(allocatedMB/1024d));
    availableMB = availableMB + res.getMemorySize();
    availableGB.set((int)Math.floor(availableMB/1024d));
    allocatedVCores.decr(res.getVirtualCores());
    availableVCores.incr(res.getVirtualCores());
  }

  public void changeContainer(Resource before, Resource now) {
    long deltaMB = now.getMemorySize() - before.getMemorySize();
    int deltaVCores = now.getVirtualCores() - before.getVirtualCores();
    allocatedMB = allocatedMB + deltaMB;
    allocatedGB.set((int)Math.ceil(allocatedMB/1024d));
    availableMB = availableMB - deltaMB;
    availableGB.set((int)Math.floor(availableMB/1024d));
    allocatedVCores.incr(deltaVCores);
    availableVCores.decr(deltaVCores);
  }

  public void startOpportunisticContainer(Resource res) {
    runningOpportunisticContainers.incr();
    allocatedOpportunisticMB = allocatedOpportunisticMB + res.getMemorySize();
    allocatedOpportunisticGB
        .set((int) Math.ceil(allocatedOpportunisticMB / 1024d));
    allocatedOpportunisticVCores.incr(res.getVirtualCores());
  }

  public void completeOpportunisticContainer(Resource res) {
    runningOpportunisticContainers.decr();
    allocatedOpportunisticMB = allocatedOpportunisticMB - res.getMemorySize();
    allocatedOpportunisticGB
        .set((int) Math.ceil(allocatedOpportunisticMB / 1024d));
    allocatedOpportunisticVCores.decr(res.getVirtualCores());
  }

  public void setQueuedContainers(int opportunisticCount, int guaranteedCount) {
    containersOpportunisticQueued.set(opportunisticCount);
    containersGuaranteedQueued.set(guaranteedCount);
  }

  public void addResource(Resource res) {
    availableMB = availableMB + res.getMemorySize();
    availableGB.set((int)Math.floor(availableMB/1024d));
    availableVCores.incr(res.getVirtualCores());
  }

  public void addContainerLaunchDuration(long value) {
    containerLaunchDuration.add(value);
  }

  public void setBadLocalDirs(int badLocalDirs) {
    this.badLocalDirs.set(badLocalDirs);
  }

  public void setBadLogDirs(int badLogDirs) {
    this.badLogDirs.set(badLogDirs);
  }

  public void setGoodLocalDirsDiskUtilizationPerc(
      int goodLocalDirsDiskUtilizationPerc) {
    this.goodLocalDirsDiskUtilizationPerc.set(goodLocalDirsDiskUtilizationPerc);
  }

  public void setGoodLogDirsDiskUtilizationPerc(
      int goodLogDirsDiskUtilizationPerc) {
    this.goodLogDirsDiskUtilizationPerc.set(goodLogDirsDiskUtilizationPerc);
  }

  public void setCacheSizeBeforeClean(long cacheSizeBeforeClean) {
    this.cacheSizeBeforeClean.set(cacheSizeBeforeClean);
  }

  public void setTotalBytesDeleted(long totalBytesDeleted) {
    this.totalBytesDeleted.set(totalBytesDeleted);
  }

  public void setPublicBytesDeleted(long publicBytesDeleted) {
    this.publicBytesDeleted.set(publicBytesDeleted);
  }

  public void setPrivateBytesDeleted(long privateBytesDeleted) {
    this.privateBytesDeleted.set(privateBytesDeleted);
  }

  public void appReadyForLogAggregation(){
    appsWaitingForLogAggregation.incr();
  }

  public void addLogAggregationThreadWaitTime(long waitTime) {
    this.logAggregationThreadWaitTimeMs.add(waitTime);
    this.waitTimeLatency.add(waitTime);
  }

  public void appStartedLogAggr(){
    appsWaitingForLogAggregation.decr();
    logAggrThreadsUsed.incr();
  }

  public void successfulContainerLogUpload() {
    this.numSuccessfulContainerLogUploads.incr();
  }

  public void failedContainerLogUpload() {
    this.numFailedContainerLogUploads.incr();
  }

  public void addLogUploadDuration(long duration) {
    this.HDFSUploadDurationMs.add(duration);
    this.HDFSUploadLatency.add(duration);
  }

  public void reportLogAggregationStatus(boolean succeeded){
    if(succeeded) {
      this.numSuccessfulAppLogAggregations.incr();
    } else {
      this.numFailedAppLogAggregations.incr();
    }
  }

  public void appLogAggregationFinished(){
    logAggrThreadsUsed.decr();
  }

  public void addLogAggregationThreadHoldTime(long holdTime) {
    this.logAggregationThreadHoldTimeMs.add(holdTime);
    this.logAggrLatency.add(holdTime);
  }

  public int getRunningContainers() {
    return containersRunning.value();
  }

  public int getPausedContainers() {
    return containersPaused.value();
  }

  @VisibleForTesting
  public int getKilledContainers() {
    return containersKilled.value();
  }

  @VisibleForTesting
  public int getFailedContainers() {
    return containersFailed.value();
  }

  @VisibleForTesting
  public int getCompletedContainers() {
    return containersCompleted.value();
  }

  @VisibleForTesting
  public int getBadLogDirs() {
    return badLogDirs.value();
  }

  @VisibleForTesting
  public int getBadLocalDirs() {
    return badLocalDirs.value();
  }

  @VisibleForTesting
  public int getGoodLogDirsDiskUtilizationPerc() {
    return goodLogDirsDiskUtilizationPerc.value();
  }

  @VisibleForTesting
  public int getGoodLocalDirsDiskUtilizationPerc() {
    return goodLocalDirsDiskUtilizationPerc.value();
  }

  @VisibleForTesting
  public int getReInitializingContainer() {
    return containersReIniting.value();
  }

  @VisibleForTesting
  public int getContainersRolledbackOnFailure() {
    return containersRolledBackOnFailure.value();
  }

  public long getAllocatedOpportunisticGB() {
    return allocatedOpportunisticGB.value();
  }

  public int getAllocatedOpportunisticVCores() {
    return allocatedOpportunisticVCores.value();
  }

  public int getRunningOpportunisticContainers() {
    return runningOpportunisticContainers.value();
  }

  @VisibleForTesting
  public int getQueuedOpportunisticContainers() {
    return containersOpportunisticQueued.value();
  }

  @VisibleForTesting
  public int getQueuedGuaranteedContainers() {
    return containersGuaranteedQueued.value();
  }

  public long getCacheSizeBeforeClean() {
    return this.cacheSizeBeforeClean.value();
  }

  public long getTotalBytesDeleted() {
    return this.totalBytesDeleted.value();
  }

  public long getPublicBytesDeleted() {
    return this.publicBytesDeleted.value();
  }

  public long getPrivateBytesDeleted() {
    return this.privateBytesDeleted.value();
  }

  public void setContainerUsedMemGB(long usedMem) {
    this.containerUsedMemGB.set((int)Math.floor(usedMem/1024d));
  }

  public int getContainerUsedMemGB() {
    return this.containerUsedMemGB.value();
  }

  public void setContainerUsedVMemGB(long usedVMem) {
    this.containerUsedVMemGB.set((int)Math.floor(usedVMem/1024d));
  }

  public int getContainerUsedVMemGB() {
    return this.containerUsedVMemGB.value();
  }

  public void setContainerCpuUtilization(float cpuUtilization) {
    this.containerCpuUtilization.set(cpuUtilization);
  }

  public float getContainerCpuUtilization() {
    return this.containerCpuUtilization.value();
  }

  public void setNodeUsedMemGB(long totalUsedMemGB) {
    this.nodeUsedMemGB.set((int)Math.floor(totalUsedMemGB/1024d));
  }

  public int getNodeUsedMemGB() {
    return nodeUsedMemGB.value();
  }

  public void setNodeUsedVMemGB(long totalUsedVMemGB) {
    this.nodeUsedVMemGB.set((int)Math.floor(totalUsedVMemGB/1024d));
  }

  public int getNodeUsedVMemGB() {
    return nodeUsedVMemGB.value();
  }

  public float getNodeCpuUtilization() {
    return nodeCpuUtilization.value();
  }

  public void setNodeCpuUtilization(float cpuUtilization) {
    this.nodeCpuUtilization.set(cpuUtilization);
  }

  public void setNodeGpuUtilization(float nodeGpuUtilization) {
    this.nodeGpuUtilization.set(nodeGpuUtilization);
  }

  public float getNodeGpuUtilization() {
    return nodeGpuUtilization.value();
  }

  private void updateLocalizationHitRatios() {
    updateLocalizationHitRatio(localizedCacheHitBytes, localizedCacheMissBytes,
        localizedCacheHitBytesRatio);
    updateLocalizationHitRatio(localizedCacheHitFiles, localizedCacheMissFiles,
        localizedCacheHitFilesRatio);
  }

  private static void updateLocalizationHitRatio(MutableCounterLong hitCounter,
      MutableCounterLong missedCounter, MutableGaugeInt ratioGauge) {
    final long hits = hitCounter.value();
    final long misses = missedCounter.value();
    final long total = hits + misses;
    if (total > 0) {
      ratioGauge.set((int)(100 * hits / total));
    }
  }

  public void localizationCacheHitMiss(long size) {
    if (size > 0) {
      localizedCacheMissBytes.incr(size);
      localizedCacheMissFiles.incr();
      updateLocalizationHitRatios();
    } else if (size < 0) {
      // cached: recorded negative, restore the sign
      localizedCacheHitBytes.incr(-size);
      localizedCacheHitFiles.incr();
      updateLocalizationHitRatios();
    }
  }

  public void localizationComplete(long downloadMillis) {
    localizationDurationMillis.add(downloadMillis);
  }

  public long getLogAggrThreadsUsed(){
    return this.logAggrThreadsUsed.value();
  }

  public long getAppsWaitingForLogAggregation(){
    return this.appsWaitingForLogAggregation.value();
  }

  public long getNumSuccessfulAppLogAggregations(){
    return this.numSuccessfulAppLogAggregations.value();
  }

  public long getNumFailedAppLogAggregations(){
    return this.numFailedAppLogAggregations.value();
  }

  public long getNumSuccessfulContainerLogUploads(){
    return this.numSuccessfulContainerLogUploads.value();
  }

  public long getNumFailedContainerLogUploads(){
    return this.numFailedContainerLogUploads.value();
  }

  public long getNumLogAggregationThreadWaitApps() {
    return this.logAggregationThreadWaitTimeMs.lastStat().numSamples();
  }

  public long getNumLogAggregatingApps() {
    return this.logAggregationThreadHoldTimeMs.lastStat().numSamples();
  }

  public double getLogAggregationThreadWaitTimeMs() {
    return this.logAggregationThreadWaitTimeMs.lastStat().mean();
  }

  public double getLogAggregationThreadHoldTimeMs() {
    return this.logAggregationThreadHoldTimeMs.lastStat().mean();
  }

  @VisibleForTesting
  public double getHDFSTotalUploadDurationMs() {
    return this.HDFSUploadDurationMs.lastStat().total();
  }
}
