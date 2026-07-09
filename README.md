# Service Department Operational Optimization Simulator

## Sankalp Amaravadi 
CS 4632 W01 Summer 2026

## Project Description
This project is a discrete event simulation (DES) of an automotive dealership service department. It is designed to identify bottlenecks and operational inefficiencies within the department to assist managers in optimizing their workforce scheduling and resource allocation. The simulator models technician experience-based service times, customer arrival patterns, and queue management to provide actionable insights for department optimization.

## Project Status

### What's Implemented So Far
- Core Discrete Event Simulation (DES) engine
- Basic service department model and queue management
- Console output for simulation results
- **UI Wireframes**: Design sketches for user interface components
- **UI Implementation**: Complete graphical user interface for simulation visualization
- **Experience-Based Service Times**: Enhanced modeling of technician skill progression affecting service duration
- **Data Collection**: Per-run time-series/event CSV and summary/config JSON, plus a master index
- **Comprehensive Testing**: JUnit suite covering engine, models, stochastic, inventory, metrics, config, data, and UI

### What's Still to Come
- **Real-World Validation**: Testing with actual automotive service department metrics and performance data (Milestone 4)

### Milestone Progress
- **Milestone 2 (Initial Implementation)**: Core components created and connected, console output functional, basic documentation complete
- **Milestone 3 (Complete Implementation and Testing)**: UI, data collection, and comprehensive testing complete
- **Milestone 4 (Analysis and Validation)**: N/A

## Installation Instructions
- 1. Open preferred Command-Line Interface (CLI)
1. Enter "java -version" and verify that the version installed is newer then JDK 17
2. If JDK is older then 17 is installed, download and install JDK 17 or newer
3. Enter "mvn -version" and verify that 3.13 or newer is installed
4. If not, then install a version newer then 3.13
5. Download the project from GitHub
6. Open the project in the preferred IDE
7. Build the project with the command "mvn compile" in the IDE's terminal
8. Run the simulation by running the file named main

### Dependencies and Versions
- Java 17
- JUnit 5.11.01
- JaCoCo 0.8.12
- Additional dependencies will be documented as they are used

### Step-by-Step Setup Guide
- 1. Open preferred Command-Line Interface (CLI)
- 2. Enter "java -version" and verify that the version installed is newer then JDK 17
-  3. If JDK is older then 17 is installed, download and install JDK 17 or newer
   4. Enter "mvn -version" and verify that 3.13 or newer is installed
   5. If not, then install a version newer then 3.13
   6. Download the project from GitHub
   7. Open the project in the preferred IDE
   8. Compile the project with the command "mvn compile" in the IDE's terminal
   9. Run the simulation by using the command "mvn javafx:run"
   10. Once the simulation is running, enter how many technicians, advisors and customers you have for the day
   11. Press enter and get the relavent information printed back

### Troubleshooting Common Issues
- If "java" or "mvn" is not recognized, confirm JDK 17 and Maven are installed and added to your system PATH
- If the program will not start, run "mvn compile" from the project root before running main
- If prompts do not appear when running from an IDE, run the simulation from a terminal instead
- Run "java -cp target/classes simulator.Main --help" to view all available command-line options

## Usage

### How to Run the Simulation
- 1. Open a terminal in the project folder
1. Build the project with "mvn compile"
2. Run "java -cp target/classes simulator.Main"
3. Enter the number of technicians, advisors, and customers when prompted, or press enter to keep the default values
4. Review the simulation results printed to the console

A graphical user interface is still in development. Until the UI is complete, the simulation is run from the command line or IDE as described above.

### Command-Line Arguments or Configuration
- Default settings are stored in "src/main/resources/simulation.properties"
- Common command-line options include:
  - "--horizon=<hours>" for simulation run length
  - "--arrival-rate=<lambda>" for customer arrival rate
  - "--technicians=<count>" for technician and bay count
  - "--advisors=<count>" for service advisor count
  - "--seed=<value>" for a repeatable random seed
  - "--output=<dir>" to set the results output directory (default: results)
  - "--help" to list all available options
- Example: "java -cp target/classes simulator.Main --horizon=10 --arrival-rate=4 --technicians=3"

### Expected Output/Behavior
- The simulation runs for the configured time period (default 10 hours) and prints configuration details to the console
- Output includes customer wait times, technician and bay utilization, parts delays, jobs completed, and queue benchmarks
- These results are designed to help identify where the department is slowing down so staffing and inventory decisions can be evaluated

## Architecture Overview

### Main Components
- **Discrete Event Simulation Engine**: Core simulation logic and event processing
- **Service Department Model**: Represents technicians, customers, and service queues
- **User Interface**: GUI for simulation control and visualization (in development)
- **Analytics Module**: Performance metrics and bottleneck identification

### Architecture to UML Design
- The class diagram in "documents/Classdiagram.puml" shows the planned structure for SimulationEngine, customers, advisors, tickets, technicians, bays, parts, the job queue, and metrics collection
- The Java classes follow this layout
- The activity diagram in "documents/ActivityUML.puml" shows the flow from customer arrival through ticket creation, technician assignment, parts fulfillment, repair, and job completion
- The simulation engine follows this same flow through its event handlers

### Architectural Changes
- SimKit was replaced with a built-in event scheduler to keep the project self-contained and easier to maintain
- JavaFX visualization was deferred so the simulation core and metrics could be completed first
- Service advisor intake time is kept constant rather than experience-based, matching the original project assumptions
