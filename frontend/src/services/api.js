import axios from 'axios';
import { mockPatients, mockRecords, mockSummary, mockPlans, mockInsights } from '../data/mockData';

// Priority: LocalStorage override -> env var -> localhost default
export const getApiBaseUrl = () => {
  return localStorage.getItem('wellness_api_base_url') || 
         import.meta.env.VITE_API_BASE_URL || 
         'http://localhost:8080';
};

export const setApiBaseUrl = (url) => {
  localStorage.setItem('wellness_api_base_url', url);
};

const apiClient = axios.create({
  timeout: 8000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Interceptor to inject JWT token and dynamically resolve current base URL
apiClient.interceptors.request.use((config) => {
  config.baseURL = getApiBaseUrl();
  const token = localStorage.getItem('wellness_auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Storage for in-memory simulated updates when in offline/demo mode
let localPatients = [...mockPatients];
let localRecords = [...mockRecords];
let localPlans = [...mockPlans];
let localInsights = [...mockInsights];

export const isDemoModeActive = () => {
  return localStorage.getItem('wellness_force_demo') === 'true';
};

export const setDemoMode = (enabled) => {
  localStorage.setItem('wellness_force_demo', enabled ? 'true' : 'false');
};

// API Services
export const authService = {
  login: async (email, password) => {
    if (isDemoModeActive()) {
      const mockToken = "mock_jwt_token_" + Date.now();
      localStorage.setItem('wellness_auth_token', mockToken);
      localStorage.setItem('wellness_user_email', email);
      return { accessToken: mockToken, tokenType: "Bearer", user: { email, fullName: "Demo Practitioner" } };
    }
    try {
      const res = await apiClient.post('/api/v1/auth/login', { email, password });
      if (res.data.accessToken) {
        localStorage.setItem('wellness_auth_token', res.data.accessToken);
        localStorage.setItem('wellness_user_email', email);
      }
      return res.data;
    } catch (err) {
      if (isDemoModeActive() || !err.response) {
        // Fallback for seamless UX
        const mockToken = "mock_jwt_token_demo";
        localStorage.setItem('wellness_auth_token', mockToken);
        localStorage.setItem('wellness_user_email', email);
        return { accessToken: mockToken, tokenType: "Bearer", user: { email, fullName: "Demo Practitioner" } };
      }
      throw err;
    }
  },

  register: async (fullName, email, password) => {
    if (isDemoModeActive()) {
      return { message: "User registered successfully (Demo Mode)", email };
    }
    try {
      const res = await apiClient.post('/api/v1/auth/register', { fullName, email, password });
      return res.data;
    } catch (err) {
      if (isDemoModeActive() || !err.response) {
        return { message: "Simulated registration successful", email };
      }
      throw err;
    }
  },

  logout: () => {
    localStorage.removeItem('wellness_auth_token');
    localStorage.removeItem('wellness_user_email');
  },

  getCurrentUser: () => {
    const token = localStorage.getItem('wellness_auth_token');
    const email = localStorage.getItem('wellness_user_email') || 'bharathsirikonda2003s@gmail.com';
    return token ? { email, fullName: "Bharath Sirikonda", role: "ROLE_USER" } : null;
  }
};

export const patientService = {
  getPatients: async () => {
    if (isDemoModeActive()) return localPatients;
    try {
      const res = await apiClient.get('/api/v1/patients');
      return res.data && res.data.length ? res.data : localPatients;
    } catch (err) {
      return localPatients;
    }
  },

  createPatient: async (patientData) => {
    if (isDemoModeActive()) {
      const newPatient = {
        id: Date.now(),
        ...patientData,
        active: true,
        totalRecords: 0,
        createdAt: new Date().toISOString()
      };
      localPatients.unshift(newPatient);
      return newPatient;
    }
    try {
      const res = await apiClient.post('/api/v1/patients', patientData);
      return res.data;
    } catch (err) {
      const newPatient = {
        id: Date.now(),
        ...patientData,
        active: true,
        totalRecords: 0,
        createdAt: new Date().toISOString()
      };
      localPatients.unshift(newPatient);
      return newPatient;
    }
  }
};

export const recordService = {
  getRecentRecords: async (patientId, count = 10) => {
    if (isDemoModeActive()) return localRecords.slice(0, count);
    try {
      const res = await apiClient.get(`/api/v1/patients/${patientId}/wellness-records/recent?count=${count}`);
      return res.data && res.data.length ? res.data : localRecords.slice(0, count);
    } catch (err) {
      return localRecords.slice(0, count);
    }
  },

  logRecord: async (patientId, recordData) => {
    if (isDemoModeActive()) {
      const newRecord = {
        id: Date.now(),
        ...recordData
      };
      localRecords.unshift(newRecord);
      return newRecord;
    }
    try {
      const res = await apiClient.post(`/api/v1/patients/${patientId}/wellness-records`, recordData);
      return res.data;
    } catch (err) {
      const newRecord = {
        id: Date.now(),
        ...recordData
      };
      localRecords.unshift(newRecord);
      return newRecord;
    }
  }
};

export const summaryService = {
  getWeeklySummary: async (patientId) => {
    if (isDemoModeActive()) return mockSummary;
    try {
      const res = await apiClient.get(`/api/v1/patients/${patientId}/wellness-summary/weekly`);
      return res.data || mockSummary;
    } catch (err) {
      return mockSummary;
    }
  },

  getMonthlySummary: async (patientId) => {
    if (isDemoModeActive()) return { ...mockSummary, period: "Trailing 30 Days", totalDaysLogged: 28 };
    try {
      const res = await apiClient.get(`/api/v1/patients/${patientId}/wellness-summary/monthly`);
      return res.data || { ...mockSummary, period: "Trailing 30 Days" };
    } catch (err) {
      return { ...mockSummary, period: "Trailing 30 Days" };
    }
  }
};

export const planService = {
  getPlans: async (patientId) => {
    if (isDemoModeActive()) return localPlans;
    try {
      const res = await apiClient.get(`/api/v1/patients/${patientId}/wellness-plans`);
      return res.data && res.data.length ? res.data : localPlans;
    } catch (err) {
      return localPlans;
    }
  },

  createPlan: async (patientId, planData) => {
    if (isDemoModeActive()) {
      const newPlan = {
        id: Date.now(),
        ...planData,
        status: "ACTIVE",
        adherenceProgress: {
          overallPercentage: 100,
          sleepAdherencePercent: 100,
          waterAdherencePercent: 100,
          exerciseAdherencePercent: 100,
          status: "ON_TRACK"
        }
      };
      localPlans.unshift(newPlan);
      return newPlan;
    }
    try {
      const res = await apiClient.post(`/api/v1/patients/${patientId}/wellness-plans`, planData);
      return res.data;
    } catch (err) {
      const newPlan = {
        id: Date.now(),
        ...planData,
        status: "ACTIVE",
        adherenceProgress: {
          overallPercentage: 100,
          status: "ON_TRACK"
        }
      };
      localPlans.unshift(newPlan);
      return newPlan;
    }
  }
};

export const insightService = {
  getInsights: async (patientId) => {
    if (isDemoModeActive()) return localInsights;
    try {
      const res = await apiClient.get(`/api/v1/patients/${patientId}/insights`);
      return res.data && res.data.length ? res.data : localInsights;
    } catch (err) {
      return localInsights;
    }
  },

  generateInsight: async (patientId) => {
    if (isDemoModeActive()) {
      const newInsight = {
        id: Date.now(),
        patientId,
        generatedAt: new Date().toISOString(),
        provider: "DETERMINISTIC_FALLBACK (Offline Heuristic Engine)",
        observations: [
          "Recent physical activity frequency reached 4.2 sessions per week, elevating cardiovascular recovery scores.",
          "Average sleep consistency remained stable at 7.6 hours with reduced wake-frequency indicators.",
          "Hydration markers aligned within 92% of target threshold with balanced daytime distribution."
        ],
        recommendations: [
          "Maintain hydration pacing by consuming at least 500mL within the first 60 minutes of waking.",
          "Incorporate 10-15 minutes of dynamic mobility stretching post-exercise to mitigate nocturnal muscle fatigue.",
          "Preserve consistent evening sleep timing (within ±30 minutes) across both workdays and weekends."
        ],
        adherenceScore: 93,
        disclaimer: "AI Wellness Insights are generated for motivational and lifestyle educational purposes only. They do not constitute medical diagnosis, clinical treatment, prescription, or healthcare professional advice."
      };
      localInsights.unshift(newInsight);
      return newInsight;
    }
    try {
      const res = await apiClient.post(`/api/v1/patients/${patientId}/insights/generate`);
      return res.data;
    } catch (err) {
      // Fallback response for interactive experience
      const newInsight = {
        id: Date.now(),
        patientId,
        generatedAt: new Date().toISOString(),
        provider: "DETERMINISTIC_FALLBACK (Simulated Resilience Mode)",
        observations: [
          "Hydration intake consistently achieved across trailing 7 days.",
          "Restorative sleep patterns observed with low subjective stress variance."
        ],
        recommendations: [
          "Preserve morning sunlight exposure to reinforce circadian rhythm.",
          "Schedule evening wind-down routine 45 minutes prior to sleep."
        ],
        adherenceScore: 89,
        disclaimer: "AI Wellness Insights are generated for motivational and lifestyle educational purposes only. They do not constitute medical diagnosis, clinical treatment, prescription, or healthcare professional advice."
      };
      localInsights.unshift(newInsight);
      return newInsight;
    }
  }
};

export const checkBackendHealth = async () => {
  try {
    const res = await apiClient.get('/actuator/health', { timeout: 3000 });
    return { online: true, status: res.data?.status || 'UP' };
  } catch (err) {
    return { online: false, status: 'UNAVAILABLE' };
  }
};
