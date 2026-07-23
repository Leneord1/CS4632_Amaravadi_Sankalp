# Service Department Operational Optimization Simulator

## Sankalp Amaravadi
CS 4632 W01 Summer 2026

**GitHub:** https://github.com/Leneord1/CS4632_Amaravadi_Sankalp

## Project Description
Discrete-event simulation (DES) of an automotive dealership service department. The model couples customer arrivals, advisor intake, technician and bay assignment, a shared FIFO job queue, and parts inventory. Arrivals use a non-stationary Cox process; service times use experience-scaled Gamma draws; parts use reorder-point inventory with stochastic lead time. Metrics support staffing and inventory decisions before changing live shop workflow.

## Project Status

### What's Implemented
- Core DES engine with priority-queue event scheduling (`SimulationEngine`)
- Domain model: customers, advisors, `ServiceTicket`, technicians, repair bays, job queue
- Parts department with reorder-point inventory, blocking, and replenishment
- Stochastic models: Cox / piecewise arrivals, Gamma service times, experience scaling
- Metrics: total wait components, technician/bay utilization, parts delay, throughput, M/M/c soft validation
- JavaFX UI: landing, configuration, and results views (`mvn javafx:run`)
- Data export: per-run time-series/event CSV, summary/config JSON, master index under `results/`
- JUnit 5 test suite with JaCoCo coverage enforcement
- Milestone documents through M5 final report under `documents/`

### Future Work
- Warm-up deletion for steady-state metrics
- Dealership-calibrated arrivals and lead times
- Priority / warranty queues and advisor experience effects
- Multi-supplier parts and financial cost layers

### Milestone Progress
- **M1 (Proposal / Design)**: Problem scope, parameters, UML — `documents/CS4632_M1_Amaravadi_Sankalp.pdf`, `documents/m1/`
- **M2 (Initial Implementation)**: Core components connected, console output — `documents/CS4632_M2_Amaravadi_Sankalp.pdf`
- **M3 (Complete Implementation and Testing)**: JavaFX UI, data collection, comprehensive tests — `documents/CS4632_M3_LastName_FirstName.pdf`
- **M4 (Analysis and Validation)**: Sensitivity / scenario experiments and validation discussion (covered in final report)
- **M5 (Final Report)**: Complete IEEE-style write-up of design, implementation, results, and validation — `documents/CS4632_M5_Amaravadi_Sankalp.pdf`

## Installation Instructions

### Prerequisites
1. Open a preferred Command-Line Interface (CLI)
2. Run `java -version` and verify JDK 17 or newer
3. If needed, download and install JDK 17 or newer
4. Run `mvn -version` and verify Maven 3.13 or newer
5. If needed, install Maven 3.13 or newer
6. Clone or download the project from GitHub
7. Open the project in the preferred IDE

### Dependencies and Versions
- Java 17
- JavaFX 17.0.13
- JUnit 5.11.4
- JaCoCo 0.8.12
- Maven (build / JavaFX plugin)

### Step-by-Step Setup Guide
1. Open a terminal in the project root
2. Build with `mvn compile`
3. Console run: `java -cp target/classes simulator.Main`
4. GUI run: `mvn javafx:run`
5. Enter technician, advisor, and customer counts when prompted, or press Enter for defaults
6. Review console metrics and exported files under `results/`

### Troubleshooting Common Issues
- If `java` or `mvn` is not recognized, confirm JDK 17 and Maven are installed and on PATH
- If the program will not start, run `mvn compile` from the project root first
- If prompts do not appear when running from an IDE, run from a terminal instead
- Run `java -cp target/classes simulator.Main --help` to view all command-line options
- GUI tests use Monocle headless mode in CI; local GUI needs a display for `mvn javafx:run`

## Usage

### How to Run the Simulation
1. Open a terminal in the project folder
2. Build with `mvn compile`
3. Console: `java -cp target/classes simulator.Main`
4. GUI: `mvn javafx:run`
5. Enter technicians, advisors, and customers when prompted, or press Enter for defaults
6. Review printed metrics and exported files under `results/`

### Command-Line Arguments or Configuration
- Defaults live in `src/main/resources/simulation.properties`
- Common options:
  - `--config=<path>` optional properties file override
  - `--horizon=<hours>` simulation run length
  - `--arrival-rate=<lambda>` customer arrival rate (vehicles/hour)
  - `--arrival-profile=<CONSTANT|DEALERSHIP_DAY>`
  - `--technicians=<count>` technician and bay count
  - `--service-rate=<mu>` service rate per technician
  - `--advisors=<count>` service advisor count
  - `--experience-alpha=<alpha>` experience coefficient
  - `--gamma-shape=<k>` Gamma shape parameter
  - `--service-time-model=<LEGACY|PDF>`
  - `--reorder-point=<r>` / `--reorder-quantity=<Q>` / `--parts-lead-time=<hours>`
  - `--replications=<count>` / `--seed=<value>`
  - `--output=<dir>` results output directory (default: `results`)
  - `--help` list all options
- Example: `java -cp target/classes simulator.Main --horizon=10 --arrival-rate=4 --technicians=3`

### Expected Output/Behavior
- Simulation runs for the configured horizon (default 10 hours) and prints configuration details
- Output includes customer wait times, technician and bay utilization, parts delays, jobs completed, and queue benchmarks
- Each run writes time-series CSV, event CSV, summary JSON, and config JSON under a dated session folder in `results/`
