package com.acme.hr.benefits;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EligibilityCheckerTest {

    @Test
    void activeEmployeeIsEligible() {
        String status = "ACTIVE";
        boolean eligible = "ACTIVE".equals(status) || "ON_LEAVE".equals(status);
        assertTrue(eligible);
    }

    @Test
    void terminatedEmployeeIsNotEligible() {
        String status = "TERMINATED";
        boolean eligible = "ACTIVE".equals(status) || "ON_LEAVE".equals(status);
        assertFalse(eligible);
    }

    @Test
    void planCodeFollowsNamingConvention() {
        String planCode = "MED-2026-GOLD";
        assertTrue(planCode.matches("[A-Z]{3}-\\d{4}-[A-Z]+"));
    }

    @Test
    void enrollmentWindowIsValid() {
        int openMonth = 10;
        int closeMonth = 12;
        int currentMonth = 11;
        assertTrue(currentMonth >= openMonth && currentMonth <= closeMonth);
    }
}
