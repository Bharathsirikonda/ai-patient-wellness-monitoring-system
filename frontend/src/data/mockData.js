export const mockPatients = [
  {
    id: 1,
    fullName: "Bharath Sirikonda",
    dateOfBirth: "1998-05-15",
    gender: "MALE",
    contactEmail: "bharathsirikonda2003s@gmail.com",
    contactPhone: "+91 7981714097",
    active: true,
    totalRecords: 14,
    createdAt: "2026-11-01T08:00:00Z"
  },
  {
    id: 2,
    fullName: "Aarav Sharma",
    dateOfBirth: "1995-10-22",
    gender: "MALE",
    contactEmail: "aarav.sharma@example.com",
    contactPhone: "+91 9876543210",
    active: true,
    totalRecords: 8,
    createdAt: "2026-11-05T09:30:00Z"
  },
  {
    id: 3,
    fullName: "Priya Patel",
    dateOfBirth: "2001-03-11",
    gender: "FEMALE",
    contactEmail: "priya.patel@example.com",
    contactPhone: "+91 9811223344",
    active: true,
    totalRecords: 12,
    createdAt: "2026-11-08T11:15:00Z"
  }
];

export const mockRecords = [
  {
    id: 101,
    recordDate: "2026-11-17",
    sleepHours: 7.8,
    waterIntakeMl: 2800,
    exerciseMinutes: 45,
    mood: "HAPPY",
    energyLevel: 8,
    stressLevel: 3,
    notes: "Completed 45m moderate cardio and met hydration goals early in the afternoon."
  },
  {
    id: 102,
    recordDate: "2026-11-16",
    sleepHours: 7.2,
    waterIntakeMl: 2600,
    exerciseMinutes: 30,
    mood: "HAPPY",
    energyLevel: 7,
    stressLevel: 4,
    notes: "Consistent workday pacing. Hydrated regularly throughout the afternoon."
  },
  {
    id: 103,
    recordDate: "2026-11-15",
    sleepHours: 6.5,
    waterIntakeMl: 2200,
    exerciseMinutes: 20,
    mood: "NEUTRAL",
    energyLevel: 6,
    stressLevel: 5,
    notes: "Late meeting delayed evening wind-down routine; hydration slightly below target."
  },
  {
    id: 104,
    recordDate: "2026-11-14",
    sleepHours: 8.0,
    waterIntakeMl: 3000,
    exerciseMinutes: 60,
    mood: "EXCELLENT",
    energyLevel: 9,
    stressLevel: 2,
    notes: "Great morning jog and recovery. Optimal sleep continuity recorded."
  },
  {
    id: 105,
    recordDate: "2026-11-13",
    sleepHours: 7.5,
    waterIntakeMl: 2700,
    exerciseMinutes: 40,
    mood: "GOOD",
    energyLevel: 8,
    stressLevel: 3,
    notes: "Well-balanced routine with good focus and physical recovery."
  }
];

export const mockSummary = {
  patientId: 1,
  period: "Trailing 7 Days",
  totalDaysLogged: 7,
  completionRate: 100,
  averageSleepHours: 7.4,
  averageWaterIntakeMl: 2660,
  averageExerciseMinutes: 39,
  averageEnergyLevel: 7.6,
  averageStressLevel: 3.4,
  primaryMood: "HAPPY",
  moodBreakdown: {
    HAPPY: 4,
    EXCELLENT: 2,
    NEUTRAL: 1
  }
};

export const mockPlans = [
  {
    id: 201,
    title: "Cardiovascular Stamina & Hydration Focus",
    targetSleepHours: 8.0,
    targetWaterIntakeMl: 3000,
    targetWeeklyExerciseMinutes: 210,
    status: "ACTIVE",
    startDate: "2026-11-01",
    endDate: "2026-12-01",
    adherenceProgress: {
      overallPercentage: 88,
      sleepAdherencePercent: 92,
      waterAdherencePercent: 89,
      exerciseAdherencePercent: 84,
      status: "ON_TRACK"
    }
  },
  {
    id: 202,
    title: "Stress Reduction & Circadian Rhythm Alignment",
    targetSleepHours: 7.5,
    targetWaterIntakeMl: 2500,
    targetWeeklyExerciseMinutes: 150,
    status: "COMPLETED",
    startDate: "2026-10-01",
    endDate: "2026-10-31",
    adherenceProgress: {
      overallPercentage: 94,
      sleepAdherencePercent: 96,
      waterAdherencePercent: 93,
      exerciseAdherencePercent: 92,
      status: "COMPLETED"
    }
  }
];

export const mockInsights = [
  {
    id: 301,
    patientId: 1,
    generatedAt: "2026-11-17T09:00:00Z",
    provider: "DETERMINISTIC_FALLBACK (Offline Heuristic Engine)",
    observations: [
      "Consistent sleep duration averaging 7.4 hours supports cognitive alertness and muscular recovery.",
      "Hydration compliance reached 89% of target threshold with optimal mid-day distribution.",
      "Cardiovascular training consistency correlated directly with lower self-reported evening stress scores."
    ],
    recommendations: [
      "Maintain a 15-minute digital-screen curfew prior to sleep to elevate deep sleep phase efficiency.",
      "Incorporate pre-hydration (300-500mL water) 30 minutes ahead of scheduled physical workouts.",
      "Preserve morning sunlight exposure within 30 minutes of waking to stabilize circadian melatonin suppression."
    ],
    adherenceScore: 91,
    disclaimer: "AI Wellness Insights are generated for motivational and lifestyle educational purposes only. They do not constitute medical diagnosis, clinical treatment, prescription, or healthcare professional advice."
  }
];
