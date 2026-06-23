package simulator.inventory;

import java.util.Random;
import simulator.stochastic.PoissonDistribution;

public final class PartsInventoryEquations {
    private PartsInventoryEquations() {
    }

    public static boolean shouldReorder(int quantityOnHand, int reorderPoint) {
        return quantityOnHand < reorderPoint;
    }

    public static int inventoryAfterReorder(int quantityOnHand, int reorderQuantity) {
        if (quantityOnHand < 0 || reorderQuantity < 0) {
            throw new IllegalArgumentException("inventory values must be non-negative");
        }
        return quantityOnHand + reorderQuantity;
    }

    public static boolean isPartAvailable(int quantityOnHand, int requiredQuantity) {
        if (requiredQuantity < 0) {
            throw new IllegalArgumentException("requiredQuantity must be non-negative");
        }
        return quantityOnHand >= requiredQuantity;
    }

    public static int inventoryAfterFulfillment(int quantityOnHand, int requiredQuantity) {
        if (quantityOnHand < requiredQuantity || requiredQuantity < 0) {
            throw new IllegalArgumentException("insufficient inventory for fulfillment");
        }
        return quantityOnHand - requiredQuantity;
    }

    public static double averageLeadTimeHours(double[] observedLeadTimesHours) {
        if (observedLeadTimesHours == null || observedLeadTimesHours.length == 0) {
            return 0.0;
        }

        double total = 0.0;
        for (double leadTime : observedLeadTimesHours) {
            total += leadTime;
        }
        return total / observedLeadTimesHours.length;
    }

    public static double sampleLeadTimeHours(Random random, double meanLeadTimeHours) {
        if (meanLeadTimeHours <= 0.0) {
            return 0.0;
        }
        return PoissonDistribution.sampleExponentialInterArrival(random, 1.0 / meanLeadTimeHours);
    }
}
