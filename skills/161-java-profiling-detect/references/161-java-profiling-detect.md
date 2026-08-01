---
name: 161-java-profiling-detect
description: Use when you need to set up Java application profiling to detect and measure performance issues — including trusted preinstalled async-profiler v4.x setup, problem-driven profiling (CPU, memory, threading, GC, I/O), interactive profiling scripts, or collecting profiling data with flamegraphs and JFR recordings.
license: Apache-2.0
metadata:
  author: Juan Antonio Breña Moral
  version: 0.18.0
---
# Java Profiling Workflow / Step 1 / Collect data to measure potential issues

## Role

You are a Senior software engineer with extensive experience in Java software development

## Goal

This cursor rule provides a comprehensive Java application profiling framework designed to detect and measure performance issues systematically.
It serves as the first step in a structured profiling workflow, focusing on data collection and problem identification using a trusted preinstalled async-profiler v4.x distribution.

The rule automates the profiling setup process from detecting running Java processes to validating a trusted preinstalled profiler for your system.
It provides interactive scripts that guide you through identifying specific performance problems (CPU hotspots, memory leaks, concurrency issues, GC problems, or I/O bottlenecks) and then executes targeted profiling commands to collect relevant performance data.

Key capabilities include:
- **Automated Environment Setup**: Detects OS/architecture and validates `ASYNC_PROFILER_HOME` or `profiler/current` as a trusted preinstalled async-profiler distribution
- **Problem-Driven Profiling**: Guides users through identifying specific performance issues before profiling
- **Interactive Workflow**: Provides menu-driven interface for selecting appropriate profiling strategies
- **Comprehensive Data Collection**: Supports CPU profiling, memory allocation tracking, lock contention analysis, GC monitoring, and I/O bottleneck detection
- **Modern Tooling**: Leverages async-profiler v4.0 features including interactive heatmaps, native memory leak detection, and enhanced JFR conversion
- **Enhanced JFR Integration (Java 25)**: Utilizes JEP 518 (JFR Cooperative Sampling) and JEP 520 (JFR Method Timing & Tracing) for improved profiling accuracy and reduced overhead
- **Advanced Sampling**: Benefits from cooperative sampling techniques that minimize profiling impact while maintaining measurement precision
- **Organized Results**: Maintains clean directory structure with timestamped results for easy analysis and comparison

The rule ensures consistent, repeatable profiling procedures while providing the flexibility to target specific performance concerns based on your application's behavior and suspected issues.

The profiling setup uses a clean folder structure with everything contained in the profiler directory:

```text
your-project/
├── run-java-process-for-profiling.sh    # ← Step 1: Run main application with profiling JVM flags
└── profiler/                            # ← All profiling-related files
├── scripts/                         # ← Profiling scripts and tools
│   └── profile-java-process.sh      # ← Step 2: Interactive profiling script
├── results/                         # ← Generated profiling output
│   ├── *.html                       # ← Flamegraph files
│   └── *.jfr                        # ← JFR recording files
├── current/                         # ← Symlink to current profiler version
└── async-profiler-*/                # ← Downloaded profiler binaries
```

## Steps

### Step 1: Setup Application Runner Script

**IMPORTANT**: Use the exact bash script from the template without any modification or interpretation.

Use the packaged application runner:
[run-java-process-for-profiling.sh](../scripts/run-java-process-for-profiling.sh).

**Script Location:**
```
your-project/
└── run-with-profiler.sh    # ← Run main application with the right JVM flags for profiling
```

**Setup Instructions:**
1. Copy the **EXACT** bash script content from `run-java-process-for-profiling.sh`
2. Save it as `run-java-process-for-profiling.sh` in your project root
3. Make it executable: `chmod +x run-java-process-for-profiling.sh`
4. **NO MODIFICATIONS** to the script content are needed or allowed

**Purpose:**
- Configures JVM with profiling-friendly flags
- Ensures proper async-profiler compatibility
- Starts your application ready for profiling

**Usage:**
```bash
# Start your application with profiling-ready JVM settings
./run-java-process-for-profiling.sh
```

#### Step Constraints

- **CRITICAL INSTRUCTION FOR AI ASSISTANTS:**
- **COPY THE BASH SCRIPT EXACTLY** from the template file
- **DO NOT MODIFY, INTERPRET, OR ENHANCE** the script content
- **DO NOT ADD NEW FEATURES** or change the logic
- **USE THE SCRIPT VERBATIM** - every line, comment, and function exactly as provided
- The script is already complete and tested - no improvements needed

### Step 2: Setup Interactive Profiling Script

**IMPORTANT**: Use the exact bash script from the template without any modification or interpretation.

Use the packaged profiler:
[profile-java-process.sh](../scripts/profile-java-process.sh).

**Script Location:**
```
└── profiler/               # ← All profiling-related files
├── scripts/            # ← Profiling scripts and tools
│   └── profile-java-process.sh # ← Copy exact script from template
```

**Setup Instructions:**
1. Copy the **EXACT** bash script from the template
2. Save it as `profiler/scripts/profile-java-process.sh` in your project root
3. Make it executable: `chmod +x profiler/scripts/profile-java-process.sh`
4. **NO MODIFICATIONS** to the script content are needed or allowed

**Purpose:**
- Detects running Java processes automatically
- Validates and configures a trusted preinstalled async-profiler v4.x distribution
- Provides interactive menu for different profiling scenarios
- Generates flamegraphs and analysis reports

**Usage:**
```bash
# Execute the interactive profiling script
./profiler/scripts/profile-java-process.sh
```

#### Step Constraints

- **CRITICAL INSTRUCTION FOR AI ASSISTANTS:**
- **COPY THE BASH SCRIPT EXACTLY** from the template file
- **DO NOT MODIFY, INTERPRET, OR ENHANCE** the script content
- **DO NOT ADD NEW FEATURES** or change the logic
- **USE THE SCRIPT VERBATIM** - every line, comment, and function exactly as provided
- The script is already complete and tested - no improvements needed
- Create the profiler directory structure: `mkdir -p profiler/scripts profiler/results`
- Copy the **EXACT** bash script content from `java-profiling-script-template.md`
- Save it as `profiler/scripts/profile-java-process.sh`
- Make it executable: `chmod +x profiler/scripts/profile-java-process.sh`
- **NO MODIFICATIONS** to the script content are needed or allowed

### Step 3: Explain how to use the scripts

- Run the script to start the application with profiling support
- Run the script to start the interactive profiling script


## Output Format

- Set up automated Java profiling environment with async-profiler v4.0 and enhanced JFR capabilities
- Create interactive profiling scripts for problem-driven data collection with Java 25 JFR improvements
- Generate targeted profiling data based on specific performance issues (CPU, memory, threading, GC, I/O) with cooperative sampling
- Leverage JEP 518 (JFR Cooperative Sampling) for reduced overhead and JEP 520 (Method Timing & Tracing) for enhanced accuracy
- Organize profiling results in structured directory hierarchy with timestamped files and improved JFR recordings
- Provide flamegraph and heatmap visualizations for performance analysis with enhanced method-level tracing
- Enable systematic measurement and detection of Java application performance bottlenecks with minimal profiling impact


## Safeguards

- Always use the exact bash script templates without modification or interpretation
- Do not download profiler binaries at runtime; require `ASYNC_PROFILER_HOME` or `profiler/current` to point to a trusted, preinstalled async-profiler distribution
- Ensure proper JVM flags are applied for profiling compatibility before data collection
- Verify Java processes are running and accessible before attempting to attach profiler
- Create organized directory structure for profiling results with timestamped files
- Validate async-profiler v4.x installation and compatibility with target Java version before executing profiler tools