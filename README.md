# Service Department Operational Optimization Simulator

## Sankalp Amaravadi
CS 4632 W01 Summer 2026

## Project Description
This project is a discrete event simulation (DES) of an automotive dealership service department. It is designed to identify bottlenecks and operational inefficiencies within the department to assist managers in optimizing their workforce scheduling and resource allocation. The simulator models technician experience-based service times, customer arrival patterns, and queue management to provide actionable insights for department optimization.

## Project Status

### What's Implemented So Far
- Core Discrete Event Simulation (DES) engine
- Service department model and queue management
- Console output for simulation results
- **UI Wireframes**: Design sketches for user interface components
- **UI Implementation**: JavaFX GUI for configuration, run control, and results visualization
- **Experience-Based Service Times**: Technician skill progression affecting service duration
- **Data Collection**: Per-run time-series/event CSV and summary/config JSON, plus a master index
- **Comprehensive Testing**: JUnit suite covering engine, models, stochastic, inventory, metrics, config, data, and UI

### What's Still to Come
- **Real-World Validation**: Testing with actual automotive service department metrics and performance data (Milestone 4)

### Milestone Progress
- **Milestone 2 (Initial Implementation)**: Core components created and connected, console output functional, basic documentation complete
- **Milestone 3 (Complete Implementation and Testing)**: UI, data collection, and comprehensive testing complete
- **Milestone 4 (Analysis and Validation)**: Analyzing results
## Installation Instructions

### Prerequisites
1. Open a preferred Command-Line Interface (CLI)
2. Run `java -version` and verify JDK 17 or newer
3. If needed, download and install JDK 17 or newer
4. Run `mvn -version` and verify Maven 3.13 or newer
5. If needed, install Maven 3.13 or newer
6. Download the project from GitHub
7. Open the project in the preferred IDE

### Dependencies and Versions
- Java 17
- JavaFX 17.0.13
- JUnit 5.11.4
- JaCoCo 0.8.12

### Step-by-Step Setup Guide
1. Open a terminal in the project root
2. Build with `mvn compile`
3. Console run: `java -cp target/classes simulator.Main`
4. GUI run: `mvn javafx:run`
5. Enter technician, advisor, and customer counts when prompted, or press Enter for defaults
6. Review console output and/or results in the GUI

### Troubleshooting Common Issues
- If `java` or `mvn` is not recognized, confirm JDK 17 and Maven are installed and on PATH
- If the program will not start, run `mvn compile` from the project root first
- If prompts do not appear when running from an IDE, run from a terminal instead
- Run `java -cp target/classes simulator.Main --help` to view all command-line options

## Usage

### How to Run the Simulation
1. Open a terminal in the project folder
2. Build with `mvn compile`
3. Console: `java -cp target/classes simulator.Main`
4. GUI: `mvn javafx:run`
5. Enter technicians, advisors, and customers when prompted, or press Enter for defaults
6. Review printed metrics and exported files under `results/`

### Command-Line Arguments or Configuration
- Default settings are stored in `src/main/resources/simulation.properties`
- Common options:
  - `--horizon=<hours>` simulation run length
  - `--arrival-rate=<lambda>` customer arrival rate
  - `--technicians=<count>` technician and bay count
  - `--advisors=<count>` service advisor count
  - `--seed=<value>` repeatable random seed
  - `--output=<dir>` results output directory (default: `results`)
  - `--no-prompt` skip interactive prompts
  - `--help` list all options
- Example: `java -cp target/classes simulator.Main --horizon=10 --arrival-rate=4 --technicians=3`

### Expected Output/Behavior
- Simulation runs for the configured horizon (default 10 hours) and prints configuration details
- Output includes customer wait times, technician and bay utilization, parts delays, jobs completed, and queue benchmarks
- Results support staffing and inventory decisions by highlighting department slowdowns

## Architecture Overview

### Main Components
- **Discrete Event Simulation Engine**: Core simulation logic and event processing
- **Service Department Model**: Technicians, customers, advisors, bays, parts, and queues
- **User Interface**: JavaFX GUI for simulation control and visualization
- **Analytics Module**: Performance metrics and bottleneck identification
- **Data Export**: CSV/JSON run artifacts under the configured output directory

### Architecture to UML Design
- Class diagram in `documents/Classdiagram.puml` shows SimulationEngine, customers, advisors, tickets, technicians, bays, parts, job queue, and metrics
- Java classes follow this layout
- Activity diagram in `documents/ActivityUML.puml` shows flow from arrival through ticket creation, technician assignment, parts fulfillment, repair, and completion
- Simulation engine follows the same event-handler flow

### Architectural Changes
- SimKit was replaced with a built-in event scheduler to keep the project self-contained
- JavaFX UI is available via `mvn javafx:run` (`simulator.ui.SimulatorApp`)
- Service advisor intake time is constant rather than experience-based, matching original project assumptions
