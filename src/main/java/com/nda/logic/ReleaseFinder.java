package com.nda.logic;

import static com.nda.common.Constants.FINISH_INDEX;
import static com.nda.common.Constants.START_DAY_INDEX;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReleaseFinder {
    private static final Logger LOGGER = LogManager.getLogger(ReleaseFinder.class);

    private static final List<int[]> EMPTY_RELEASES = Collections.emptyList();
    private static final int FULL_DAY_CORRECTION = 1;


    public static List<int[]> findMaxReleasesWithoutShiftPerSprint(List<int[]> releases, int sprintLength) {
        LOGGER.info("Finding maximum number of releases per sprint without ability to postpone testing...");

        List<int[]> filteredReleases = prepareReleases(releases, sprintLength, true);

        if (filteredReleases == EMPTY_RELEASES) {
            return EMPTY_RELEASES;
        }

        sortReleasesByFinishDay(filteredReleases);
        return selectMaxReleasesWithoutShift(filteredReleases);
    }

    public static List<int[]> findMaxReleasesWithShiftPerSprint(List<int[]> releases, int sprintLength) {
        LOGGER.info("Finding maximum number of releases per sprint with ability to postpone testing...");

        List<int[]> filteredReleases = prepareReleases(releases, sprintLength, false);

        if (filteredReleases == EMPTY_RELEASES) {
            return EMPTY_RELEASES;
        }

        sortReleasesByEstimationAndAvailability(filteredReleases);
        return selectMaxReleasesWithShift(filteredReleases, sprintLength);
    }

    private static List<int[]> prepareReleases(List<int[]> releases, int sprintLength, boolean calculateFinishDay) {
        if (isListEmpty(releases)) {
            LOGGER.warn("Releases list is empty.");
            return EMPTY_RELEASES;
        }

        List<int[]> filteredReleases = filterReleases(releases, sprintLength);
        if (isListEmpty(filteredReleases)) {
            LOGGER.warn("No releases can fit the sprint length.");
            return EMPTY_RELEASES;
        }
        if (calculateFinishDay) {
            calculateReleasesFinishDay(filteredReleases);
        }
        return filteredReleases;
    }

    private static List<int[]> selectMaxReleasesWithoutShift(List<int[]> releases) {
        List<int[]> selectedReleases = new ArrayList<>();
        int lastFinishDay = 0;

        LOGGER.info("Selecting maximum number of non-overlapping releases...");

        for (int[] release : releases) {
            if (release[START_DAY_INDEX] > lastFinishDay) {
                selectedReleases.add(release);
                lastFinishDay = release[FINISH_INDEX];
            }
        }

        LOGGER.info("There are maximum {} non-overlapping releases.", selectedReleases.size());
        logReleases(selectedReleases);
        return selectedReleases;
    }

    private static List<int[]> selectMaxReleasesWithShift(List<int[]> releases, int sprintLength) {
        List<int[]> scheduledReleases = new ArrayList<>();
        boolean[] daysOccupied = new boolean[sprintLength + FULL_DAY_CORRECTION];

        for (int[] release : releases) {
            int availableDay = release[START_DAY_INDEX];
            int duration = release[FINISH_INDEX];
            boolean releaseTestingScheduled = false;

            for (int startDay = availableDay; startDay <= sprintLength; startDay++) {
                if (canScheduleReleaseTesting(startDay, duration, sprintLength, daysOccupied)) {
                    for (int d = startDay; d < startDay + duration; d++) {
                        daysOccupied[d] = true;
                    }
                    scheduledReleases.add(new int[]{startDay, duration});
                    releaseTestingScheduled = true;
                    break;
                }
            }
            if (!releaseTestingScheduled) {
                LOGGER.debug("Release {} testing could not be scheduled.", Arrays.toString(release));
            }
        }
        calculateReleasesFinishDay(scheduledReleases);
        sortReleasesByFinishDay(scheduledReleases);
        LOGGER.info("There are maximum {} releases with ability to postpone testing.", scheduledReleases.size());
        return scheduledReleases;
    }

    private static List<int[]> filterReleases(List<int[]> releases, int sprintLength) {
        List<int[]> validReleases = new ArrayList<>();
        LOGGER.info("Filtering releases based on sprint length and release finish dates...");
        for (int[] release : releases) {
            int startDay = release[START_DAY_INDEX];
            int finishDay = startDay + release[FINISH_INDEX] - FULL_DAY_CORRECTION;
            if (finishDay <= sprintLength) {
                validReleases.add(new int[]{startDay, release[FINISH_INDEX]});
            }
        }
        LOGGER.info("Filtering complete.");
        logReleases(validReleases);
        return validReleases;
    }

    private static void calculateReleasesFinishDay(List<int[]> releases) {
        LOGGER.info("Calculating finish day for each release...");
        for (int[] release : releases) {
            int startDay = release[START_DAY_INDEX];
            int finishDay = startDay + release[FINISH_INDEX] - FULL_DAY_CORRECTION;
            release[FINISH_INDEX] = finishDay;
        }
        LOGGER.info("Calculation complete.");
        logReleases(releases);
    }

    private static boolean canScheduleReleaseTesting(int startDay, int duration, int sprintLength, boolean[] daysOccupied) {
        if (startDay + duration - FULL_DAY_CORRECTION > sprintLength) {
            return false;
        }
        for (int d = startDay; d < startDay + duration; d++) {
            if (daysOccupied[d]) {
                return false;
            }
        }
        return true;
    }

    private static void sortReleasesByFinishDay(List<int[]> releases) {
        releases.sort(Comparator.comparingInt(release -> release[FINISH_INDEX]));
        LOGGER.debug("Sorted releases by finish time: ");
        logReleases(releases);
    }

    private static void sortReleasesByEstimationAndAvailability(List<int[]> releases) {
        releases.sort(Comparator.comparingInt((int[] release) -> release[FINISH_INDEX])
                .thenComparingInt(release -> release[START_DAY_INDEX]));
        LOGGER.debug("Sorted releases by estimated duration, then by availability day: ");
        logReleases(releases);
    }

    private static void logReleases(List<int[]> releases) {
        releases.forEach(release -> LOGGER.debug(Arrays.toString(release)));
    }

    private static boolean isListEmpty(List<int[]> releases) {
        return releases == null || releases.isEmpty();
    }

}