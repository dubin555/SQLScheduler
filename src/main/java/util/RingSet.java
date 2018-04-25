package util;

import config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RingSet data structure.
 * The RingSet has many slots, every slot contains a set of Tasks, such slots make a Ring,
 * every tick, the current slot move one step, and fire this set of Tasks.
 * Not thread-safe, no need to be, only one RingSet available.
 */
public abstract class RingSet<T> {

    private int slotsCount;
    private List<Set<T>> ringSets;
    private int currentSlot = 0;

    private Logger logger = LoggerFactory.getLogger(RingSet.class.getName());

    /**
     * Get the slot of the task should be put into, should be implemented.
     */
    public abstract int getSelfSlot(T t);

    /**
     * Get jobId of the task, should be implemented.
     */
    public abstract String getJobId(T t);

    /**
     * Get category of the task, should be implemented.
     */
    public abstract String getCategory(T t);

    /**
     * Default size of MINUTES_IN_DAY.
     */
    public RingSet() {
        this(Constants.MINUTES_IN_DAY);
    }

    public RingSet(int slotsCount) {
        this.fillInSlots(slotsCount);
    }

    /**
     * Get the slots count of the ringSet.
     */
    public int getSlotsCount() {
        return this.slotsCount;
    }

    /**
     * Get the current moving slot.
     */
    public int getCurrentSlot() {
        return this.currentSlot;
    }

    /**
     * Add the task to the specified slot.
     */
    public void add(int slot, T t) {
        this.getSet(slot).add(t);
    }

    /**
     * Remove the task in the specified slot.
     */
    public void remove(int slot, T t) {
        this.getSet(slot).remove(t);
    }

    /**
     * Add task into the RingSet.
     */
    public void add(T t) {
        logger.info(String.format("slot of t -> %d", getSelfSlot(t)));
       this.add(getSelfSlot(t), t);
    }

    /**
     * Remove the task from the RingSet.
     * @// TODO: 25/04/2018 Should store the jobId to slot mapping.
     */
    public void remove(T t) {
        for (Set<T> ringSet: ringSets) {
            for (T o: ringSet) {
                if (this.getJobId(o) == this.getJobId(t)) {
                    ringSet.remove(o);
                }
            }
        }
    }

    /**
     * Remove the task base on jobId from the RingSet.
     */
    public void remove(String jobId) {
        for (Set<T> ringSet: ringSets) {
            for (T t: ringSet) {
                if (this.getJobId(t).equals(jobId)) {
                    ringSet.remove(t);
                }
            }
        }
    }

    /**
     * Remove the task base on category from the RingSet.
     */
    public void removeCategory(String category) {
        for (Set<T> ringSet: ringSets) {
            for (T t: ringSet) {
                if (this.getCategory(t).equals(category)) {
                    ringSet.remove(t);
                }
            }
        }
    }

    /**
     * Move one step of the slot, and fire all the tasks of the current slot.
     */
    public Set<T> tick() {
        logger.info("RingSet tick...");
        logger.info(String.format("Current slot %d", this.getCurrentSlot()));
        Set<T> res = new HashSet<>();
        Set<T> currentSet = this.getSet(this.currentSlot);
        for (T t: currentSet) {

            // add to the return result
            res.add(t);

            // add back to the RingSet for a proper position
            this.add(t);
        }
        cleanCurrentSet();
        forwordCurrentSlot();
        return res;
    }

    /**
     * Move steps of the slot, but not fire the tasks!
     */
    public void forward(int step) {
        for (int i = 0; i < step; i++) {
            this.forwordCurrentSlot();
        }
    }

    /**
     * Clean the set of the slot.
     */
    public void cleanSet(int slot) {
        this.ringSets.set(slot, new HashSet<>());
    }

    /**
     * Clean the set of the current slot.
     */
    public void cleanCurrentSet() {
        this.cleanSet(this.getCurrentSlot());
    }

    /**
     * Return the set of the specified slot.
     */
    private Set<T> getSet(int slot) {
        return this.ringSets.get(slot);
    }

    /**
     * Fill in the empty slots.
     */
    private void fillInSlots(int slotsCount) {
        this.slotsCount = slotsCount;
        this.ringSets = new ArrayList<>();
        for (int i = 0; i < this.slotsCount; i++) {
            ringSets.add(new HashSet<>());
        }
    }

    /**
     * Get the last slot index.
     */
    private int getLastSlot() {
        return this.slotsCount - 1;
    }

    /**
     * Current slot forward one step, like a ring.
     */
    private void forwordCurrentSlot() {
        this.currentSlot += 1;
        if (this.currentSlot == this.getLastSlot()) {
            this.currentSlot = 0;
        }
    }

    @Override
    public String toString() {
        return "RingHashSet{" +
                "slots=" + slotsCount +
                ", ringSets=" + ringSets +
                ", currentPointer=" + currentSlot +
                '}';
    }
}
