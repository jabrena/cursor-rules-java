---
name: 162-java-profiling-analyze
description: Use when you need to analyze Java profiling data collected during the detection phase — including interpreting flamegraphs, memory allocation patterns, CPU hotspots, threading issues, systematic problem categorization, evidence documentation, or prioritizing fixes using Impact/Effort scoring.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Profiling Workflow / Step 2 / Analyze profiling data

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

This cursor rule provides a systematic approach to analyzing Java profiling data collected during the detection phase. It serves as the second step in the structured profiling workflow, focusing on interpreting profiling results, identifying root causes, and documenting findings with actionable solutions.

The rule establishes a comprehensive analysis framework that transforms raw profiling data into meaningful insights. It guides users through systematically examining flamegraphs, memory allocation patterns, CPU hotspots, and threading issues to identify performance bottlenecks and their underlying causes.

Key capabilities include:
- **Systematic Analysis Framework**: Structured approach to examining different types of profiling data (CPU, memory, threading, GC)
- **Problem Categorization**: Clear methodology for classifying issues by severity, impact, and type (memory leaks, CPU hotspots, threading problems)
- **Evidence Documentation**: Standardized templates for documenting findings with specific references to profiling files and quantitative metrics
- **Solution Development**: Framework for creating prioritized, actionable recommendations with implementation effort estimates
- **Cross-Correlation Analysis**: Techniques for correlating multiple profiling results and identifying patterns across different time periods
- **Impact Assessment**: Scoring system for prioritizing fixes based on performance impact and implementation effort

The rule ensures that profiling data is analyzed consistently and thoroughly, with findings documented in a format that enables effective communication with development teams and systematic tracking of performance improvements.

## Project Organization

The profiling setup uses a clean folder structure with everything contained in the profiler directory:

```
your-project/
└── profiler/               # ← All profiling-related files
├── scripts/            # ← Profiling scripts and tools
│   └── java-profile.sh # ← Main profiling script
├── results/            # Generated profiling output
│   ├── *.html          # Flamegraph files
│   ├── *.jfr           # JFR recording files
│   ├── *.log           # Garbage Collection Log files
│   └── *.txt           # Thread Dump files
├── current/            # ← Symlink to current profiler version
└── async-profiler-*/   # ← Downloaded profiler binaries
```

## Profiling Results Analysis Workflow

When analyzing profiling results, follow this systematic approach:

This comprehensive approach ensures thorough analysis of profiling data and actionable solutions for performance optimization.

## Steps

### Step 1: Inventory Available Results

Scan the following directories for profiling results:
- `*/profiler/results/` - Example-specific profiling results

Look for these file types:
- `allocation-flamegraph-*.html` - Memory allocation patterns
- `heatmap-cpu-*.html` - CPU hotspot visualization
- `inverted-flamegraph-*.html` - Bottom-up call analysis
- `memory-leak-*.html` - Memory leak detection reports
- `*.jfr` - Java Flight Recorder files
- `*.txt` - Thread dump files
- `*.log` - GC log files

### Step 2: Problem Identification Process

For each profiling result file, analyze and document:

**Memory-Related Issues:**
- **Memory Leaks**: Look for continuously growing memory usage patterns
- **Excessive Allocations**: Identify high-frequency object creation
- **Memory Fragmentation**: Check for inefficient memory usage patterns
- **GC Pressure**: Analyze garbage collection frequency and duration

**Performance Issues:**
- **CPU Hotspots**: Identify methods consuming excessive CPU time
- **Blocking Operations**: Look for synchronization bottlenecks
- **Inefficient Algorithms**: Spot methods with unexpected complexity
- **Resource Contention**: Identify thread contention issues

**Threading Issues:**
- **Deadlocks**: Look for thread synchronization problems
- **Context Switching**: Excessive thread switching overhead
- **Thread Pool Saturation**: Insufficient thread pool sizing

### Step 3: Problem Identification Process

Generate the following documents in the `*/profiler/docs/` (According with Project organization) folder:

**Problem Analysis Document:**
Create: `docs/profiling-problem-analysis-YYYYMMDD.md`

Structure:

```markdown
# Profiling Problem Analysis - [Date]

## Executive Summary
- Brief overview of identified issues
- Severity classification
- Impact assessment

## Detailed Findings

### [Problem Category 1]
- **Description**: What was found
- **Evidence**: Reference to specific profiling files
- **Impact**: Performance/resource impact
- **Root Cause**: Technical explanation

### [Problem Category 2]
[Repeat structure]

## Methodology
- Profiling tools used
- Data collection approach
- Analysis techniques applied

## Recommendations Priority
1. Critical issues requiring immediate attention
2. High-impact optimizations
3. Long-term improvements
```

#### Solutions Document
Create: `docs/profiling-solutions-YYYYMMDD.md`

Structure:

```markdown
# Profiling Solutions and Recommendations - [Date]

## Quick Wins (Low effort, High impact)
### Solution 1
- **Problem**: Reference to specific issue
- **Solution**: Concrete implementation steps
- **Expected Impact**: Quantified improvement
- **Implementation Effort**: Time/complexity estimate
- **Code Changes**: Specific files/methods to modify

## Medium-term Improvements
### Solution 2
[Repeat structure]

## Long-term Optimizations
### Solution 3
[Repeat structure]

## Implementation Plan
1. **Phase 1**: Critical fixes (Week 1-2)
2. **Phase 2**: Performance optimizations (Week 3-4)
3. **Phase 3**: Architecture improvements (Month 2+)

## Monitoring and Validation
- Key metrics to track
- Testing approach
- Success criteria
```

### Step 4: Problem Identification Process

**File Naming Convention:**
- Problem analysis: `profiling-problem-analysis-YYYYMMDD.md`
- Solutions: `profiling-solutions-YYYYMMDD.md`
- Summary reports: `profiling-summary-YYYYMMDD.md`

**Data Correlation:**
- Cross-reference multiple profiling files
- Compare different time periods
- Correlate with application logs
- Consider load testing scenarios

**Evidence Documentation:**
- Include specific flamegraph screenshots
- Reference exact file names and timestamps
- Provide quantitative metrics
- Document reproduction steps

### Step 5: Solution Prioritization Framework

Rate each identified problem using:

**Impact Score (1-5):**
- 5: Critical performance degradation
- 4: Significant resource waste
- 3: Moderate performance impact
- 2: Minor inefficiency
- 1: Cosmetic optimization

**Effort Score (1-5):**
- 1: Configuration change
- 2: Single method optimization
- 3: Class-level refactoring
- 4: Module restructuring
- 5: Architecture change

**Priority = Impact / Effort**
Focus on high-priority items first.

**Integration with Development Workflow**

**Before Analysis:**
1. Ensure all profiling results are recent and relevant
2. Verify results represent realistic load scenarios
3. Document test conditions and environment

**During Analysis:**
1. Use systematic approach for consistency
2. Document assumptions and limitations
3. Validate findings with additional data points

**After Analysis:**
1. Share findings with development team
2. Create implementation tickets/issues
3. Plan validation and monitoring approach
4. Schedule follow-up profiling sessions

**Approved Analysis Scope:**

Select only the user-approved, already installed analyzer needed for the available evidence:
- **async-profiler**: Existing flamegraph or CPU/memory recording analysis
- **JFR (Java Flight Recorder)**: Existing JFR recording analysis
- **JProfiler/YourKit**: Existing export analysis when the project already uses the selected commercial profiler
- **GCViewer**: Existing garbage collection log analysis

Do not install, launch, or access any profiler outside the selected evidence source.

**Visualization Techniques:**
- **Flamegraphs**: Call stack visualization
- **Heatmaps**: Temporal analysis of hotspots
- **Allocation tracking**: Memory usage patterns
- **Thread dumps**: Concurrency analysis


## Output Format

- Systematically analyze Java profiling data to identify performance bottlenecks and optimization opportunities
- Create structured documentation following the problem analysis and solutions templates
- Prioritize findings using the Impact/Effort scoring framework
- Generate actionable recommendations with clear implementation steps and expected outcomes
- Correlate multiple profiling results to identify patterns and validate findings


## Safeguards

- Always validate profiling results represent realistic load scenarios before analysis
- Document all assumptions and limitations in analysis reports
- Cross-reference multiple profiling files to validate findings
- Include quantitative metrics and specific evidence in all recommendations