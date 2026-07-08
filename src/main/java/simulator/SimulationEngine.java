package simulator;

import simulator.config.ServiceTimeModel;
import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.data.EventRecord;
import simulator.data.TimeSeriesSample;
import simulator.inventory.PartsDepartment;
import simulator.inventory.PartsFulfillmentResult;
import simulator.inventory.PendingPartOrder;
import simulator.metrics.MetricsCollector;
import simulator.model.Customer;
import simulator.model.JobQueue;
import simulator.model.Part;
import simulator.model.RepairBay;
import simulator.model.ServiceAdvisor;
import simulator.model.ServiceTicket;
import simulator.model.Technician;
import simulator.model.TicketStatus;
import simulator.stochastic.CoxProcess;
import simulator.stochastic.GammaDistribution;
import simulator.stochastic.PiecewiseConstantArrivalRate;
import simulator.stochastic.ServiceTimeEquations;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class SimulationEngine {
    //  Constants
    private static final double ADVISOR_INTAKE_HOURS = 0.1;
    private static final double PARTS_REQUIREMENT_PROBABILITY = 0.4;
    private static final double SNAPSHOT_INTERVAL_HOURS = 0.25;
    private static final String[] JOB_TYPES = {
        "Oil Change", "Brake Service", "Diagnostics", "Transmission"
    };

    private enum EventType {
        ARRIVAL,
        INTAKE_COMPLETE,
        SERVICE_COMPLETE,
        PARTS_ARRIVAL,
        SNAPSHOT
    }

    private record Event(
            double time, EventType type, long sequence, Customer customer, ServiceTicket ticket) {}

    private final SimulationConfig config;
    private final Random random;
    private final CoxProcess coxProcess;
    private final JobQueue jobQueue = new JobQueue();
    private final PartsDepartment partsDepartment;
    private final MetricsCollector metrics = new MetricsCollector();
    private final DataRecorder recorder;

    private final Deque<ServiceAdvisor> freeAdvisors = new ArrayDeque<>();
    private final Deque<Technician> freeTechnicians = new ArrayDeque<>();
    private final Deque<Customer> intakeWaitQueue = new ArrayDeque<>();
    private final List<Technician> allTechnicians = new ArrayList<>();

    private final PriorityQueue<Event> events =
            new PriorityQueue<>(
                    Comparator.comparingDouble((Event e) -> e.time)
                            .thenComparingLong(e -> e.sequence));
    private final Map<ServiceTicket, Customer> customerByTicket = new HashMap<>();
    private final Map<Customer, Double> advisorWaitByCustomer = new HashMap<>();
    private final Map<ServiceTicket, Double> readyTimeByTicket = new HashMap<>();

    private long eventSequence;
    private int nextTicketId = 1;
    private double scheduledPartsArrivalTime = Double.POSITIVE_INFINITY;
    private double clockHours;

    public SimulationEngine(SimulationConfig config) {
        this(config, null);
    }

    public SimulationEngine(SimulationConfig config, DataRecorder recorder) {
        this.config = config;
        this.recorder = recorder;
        this.random = new Random(config.getRandomSeed());
        this.coxProcess = new CoxProcess(buildArrivalRate(config), random, 1.0);
        this.partsDepartment = PartsDepartment.fromConfig(config);
        buildResources();
        metrics.configureSimulation(config);
    }

    private static PiecewiseConstantArrivalRate buildArrivalRate(SimulationConfig config) {
        /*
        Build the arrival rate function based on the configuration's arrival profile.
        If the profile is CONSTANT, create a PiecewiseConstantArrivalRate with a single
        constant rate.  If the profile is DEALERSHIP_DAY, use the default dealership day
        profile and scale it to match the mean arrival rate over the simulation horizon.
        */
        return switch (config.getArrivalProfile()) {
            case CONSTANT ->
                    new PiecewiseConstantArrivalRate(
                            new double[] {0.0}, new double[] {config.getArrivalRate()});
            case DEALERSHIP_DAY ->
                    PiecewiseConstantArrivalRate.defaultDealershipDay()
                            .scaledToMeanRate(
                                    config.getArrivalRate(), config.getSimulationHorizonHours());
        };
    }

    public void run() {
        /*
           Run the simulation until all events are processed.
           The simulation clock is advanced
        */
        config.printConfig("run start");
        scheduleArrivals();
        scheduleSnapshots();

        while (!events.isEmpty()) {
            Event event = events.poll();
            clockHours = event.time;
            switch (event.type) {
                case ARRIVAL -> handleArrival(event);
                case INTAKE_COMPLETE -> handleIntakeComplete(event);
                case SERVICE_COMPLETE -> handleServiceComplete(event);
                case PARTS_ARRIVAL -> handlePartsArrival(event);
                case SNAPSHOT -> handleSnapshot(event);
                default -> throw new IllegalStateException("Unknown event type");
            }
        }

        metrics.printSnapshot("run end");
        metrics.report();
    }

    public MetricsCollector getMetrics() {
        // Metrics collected during the last run; used by callers that need a report.
        return metrics;
    }

    private void buildResources() {
        // Build the service advisors and technicians based on the configuration.
        for (int i = 1; i <= config.getAdvisorCount(); i++) {
            freeAdvisors.add(new ServiceAdvisor(i));
        }
        for (int i = 1; i <= config.getTechnicianCount(); i++) {
            int experienceLevel = 1 + random.nextInt(config.getMaxExperienceLevel());
            Technician technician = new Technician(i, experienceLevel, config);
            RepairBay bay = new RepairBay(i);
            technician.setAssignedBay(bay);
            bay.setAssignedTechnician(technician);
            freeTechnicians.add(technician);
            allTechnicians.add(technician);
        }
    }

    private void scheduleSnapshots() {
        //  Schedule snapshot events at regular intervals throughout the simulation horizon.
        if (recorder == null) {
            return;
        }
        double horizon = config.getSimulationHorizonHours();
        for (double t = 0.0; t <= horizon + 1.0e-9; t += SNAPSHOT_INTERVAL_HOURS) {
            push(t, EventType.SNAPSHOT, null, null);
        }
    }

    private void scheduleArrivals() {
        //  Schedule customer arrival events based on the Cox process and the configuration.
        int maxArrivals =
                config.getCustomerCount() > 0
                        ? config.getCustomerCount()
                        : (int)
                                        Math.ceil(
                                                config.getArrivalRate()
                                                        * config.getSimulationHorizonHours()
                                                        * 3)
                                + 10;
        List<Customer> arrivals =
                coxProcess.generateArrivals(0.0, config.getSimulationHorizonHours(), maxArrivals);
        for (Customer customer : arrivals) {
            push(customer.getArrivalTime(), EventType.ARRIVAL, customer, null);
        }
    }

    private void handleArrival(Event event) {
        coxProcess.printRate(event.time);
        event.customer.printArrival(event.time);
        intakeWaitQueue.add(event.customer);
        recordEvent(event.time, "ARRIVAL", null, "customer arrival");
        tryStartIntakes(event.time);
    }

    private void tryStartIntakes(double now) {
        // Start intakes for customers in the intake wait queue if there are free advisors
        // available.
        while (!intakeWaitQueue.isEmpty() && !freeAdvisors.isEmpty()) {
            Customer customer = intakeWaitQueue.poll();
            ServiceAdvisor advisor = freeAdvisors.poll();

            double advisorWait = now - customer.getArrivalTime();
            advisorWaitByCustomer.put(customer, advisorWait);

            ServiceTicket ticket = null;
            if (advisor != null) {
                ticket =
                        advisor.intakeCustomer(
                                customer,
                                nextTicketId++,
                                JOB_TYPES[random.nextInt(JOB_TYPES.length)],
                                config.getMeanServiceTimeHours(),
                                config.getMeanServiceTimeHours());
            }
            maybeAddPartsRequirement(ticket);
            if (advisor != null) {
                advisor.printIntake(ticket);
            }

            if (ticket != null) {
                customerByTicket.put(ticket, customer);
            }
            push(now + ADVISOR_INTAKE_HOURS, EventType.INTAKE_COMPLETE, customer, ticket);
        }
    }

    private void handleIntakeComplete(Event event) {
        ServiceAdvisor advisor = event.customer.getAssignedAdvisor();
        advisor.setAvailable(true);
        freeAdvisors.add(advisor);

        PartsFulfillmentResult result =
                partsDepartment.requestPartsForTicket(event.ticket, event.time);
        partsDepartment.printStatus(event.time);
        if (result.isFulfilled()) {
            enqueueReadyTicket(event.ticket, event.time);
            recordEvent(event.time, "INTAKE_COMPLETE", event.ticket, "ready for service");
        } else {
            event.ticket.printStatus("blocked on parts");
            recordEvent(event.time, "INTAKE_COMPLETE", event.ticket, "blocked on parts");
        }
        schedulePartsArrival();

        tryStartIntakes(event.time);
        tryStartService(event.time);
    }

    private void enqueueReadyTicket(ServiceTicket ticket, double now) {
        readyTimeByTicket.put(ticket, now);
        jobQueue.enqueue(ticket);
        jobQueue.printQueueDepth("ticket #" + ticket.getTicketId() + " ready");
    }

    private void tryStartService(double now) {
        while (!jobQueue.isEmpty() && !freeTechnicians.isEmpty()) {
            ServiceTicket ticket = jobQueue.dequeue();
            Technician technician = freeTechnicians.poll();
            RepairBay bay = null;
            if (technician != null) {
                bay = technician.getAssignedBay();
            }

            if (technician != null) {
                technician.setAvailable(false);
            }
            if (technician != null) {
                technician.setCurrentTicket(ticket);
            }
            ticket.setAssignedTechnician(technician);
            if (bay != null) {
                bay.setOccupied(true);
            }

            double readyTime = readyTimeByTicket.getOrDefault(ticket, now);
            ticket.setQueueDelay(ticket.getQueueDelay() + (now - readyTime));
            ticket.setStatus(TicketStatus.IN_PROGRESS);

            if (technician != null) {
                ticket.setActualLaborTime(sampleServiceTime(technician));
                technician.printAssignment(ticket);
            }
            if (bay != null) {
                bay.printOccupancy(now);
            }

            push(
                    now + ticket.getActualLaborTime(),
                    EventType.SERVICE_COMPLETE,
                    customerByTicket.get(ticket),
                    ticket);
        }
    }

    private void handleServiceComplete(Event event) {
        ServiceTicket ticket = event.ticket;
        Technician technician = ticket.getAssignedTechnician();
        RepairBay bay = technician.getAssignedBay();

        ticket.setStatus(TicketStatus.COMPLETE);
        bay.setOccupied(false);
        technician.setCurrentTicket(null);
        technician.setAvailable(true);
        freeTechnicians.add(technician);

        ticket.printStatus("complete");
        metrics.recordTicket(ticket);
        recordEvent(event.time, "SERVICE_COMPLETE", ticket, "job complete");

        Customer customer = customerByTicket.get(ticket);
        double advisorWait = advisorWaitByCustomer.getOrDefault(customer, 0.0);
        metrics.recordCustomerCompletion(customer, advisorWait, ticket.getActualLaborTime());

        tryStartService(event.time);
    }

    private void handlePartsArrival(Event event) {
        scheduledPartsArrivalTime = Double.POSITIVE_INFINITY;
        List<ServiceTicket> released = partsDepartment.processPendingOrders(event.time);
        partsDepartment.printStatus(event.time);
        for (ServiceTicket ticket : released) {
            ticket.printStatus("parts received");
            enqueueReadyTicket(ticket, event.time);
        }
        recordEvent(event.time, "PARTS_ARRIVAL", null, "released=" + released.size());
        schedulePartsArrival();
        tryStartService(event.time);
    }

    private void schedulePartsArrival() {
        double earliest = Double.POSITIVE_INFINITY;
        for (PendingPartOrder order : partsDepartment.getPendingOrders()) {
            earliest = Math.min(earliest, order.arrivalTimeHours());
        }
        if (earliest >= scheduledPartsArrivalTime) {
            return;
        }
        scheduledPartsArrivalTime = earliest;
        push(Math.max(earliest, clockHours), EventType.PARTS_ARRIVAL, null, null);
    }

    private void maybeAddPartsRequirement(ServiceTicket ticket) {
        if (random.nextDouble() < PARTS_REQUIREMENT_PROBABILITY) {
            ticket.addRequiredPart(1, 1 + random.nextInt(3));
        }
    }

    private double sampleServiceTime(Technician technician) {
        double meanServiceTime = config.getMeanServiceTimeHours();
        double shapeParameter = config.getGammaShapeParameter();
        if (config.getServiceTimeModel() == ServiceTimeModel.PDF) {
            double normalizedExperience =
                    ServiceTimeEquations.normalizeExperienceLevel(
                            technician.getExperienceLevel(), config.getMaxExperienceLevel());
            return GammaDistribution.sampleForTechnician(
                    random,
                    meanServiceTime,
                    normalizedExperience,
                    config.getExperienceAlpha(),
                    shapeParameter);
        }
        double scale = ServiceTimeEquations.gammaScaleParameter(meanServiceTime, shapeParameter);
        return GammaDistribution.sample(random, shapeParameter, scale);
    }

    private void push(double time, EventType type, Customer customer, ServiceTicket ticket) {
        events.add(new Event(time, type, eventSequence++, customer, ticket));
    }

    private void handleSnapshot(Event event) {
        if (recorder == null) {
            return;
        }
        int busyTechnicians = 0;
        int busyBays = 0;
        for (Technician technician : allTechnicians) {
            if (!technician.isAvailable()) {
                busyTechnicians++;
            }
            RepairBay bay = technician.getAssignedBay();
            if (bay != null && bay.isOccupied()) {
                busyBays++;
            }
        }
        recorder.recordSample(
                new TimeSeriesSample(
                        System.currentTimeMillis(),
                        event.time,
                        intakeWaitQueue.size(),
                        jobQueue.size(),
                        busyTechnicians,
                        busyBays,
                        partsOnHand(),
                        metrics.getThroughputMetrics().getRecordedJobs()));
    }

    private void recordEvent(double time, String type, ServiceTicket ticket, String detail) {
        if (recorder == null) {
            return;
        }
        int ticketId = ticket == null ? -1 : ticket.getTicketId();
        recorder.recordEvent(new EventRecord(time, type, ticketId, detail));
    }

    private int partsOnHand() {
        int total = 0;
        for (Part part : partsDepartment.getInventory().getParts()) {
            total += part.getQuantityOnHand();
        }
        return total;
    }
}
