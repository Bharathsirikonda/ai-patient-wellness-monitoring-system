import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Dashboard } from './components/Dashboard';
import { PatientManager } from './components/PatientManager';
import { WellnessLogger } from './components/WellnessLogger';
import { PlansManager } from './components/PlansManager';
import { AiInsights } from './components/AiInsights';
import { AuthModal } from './components/AuthModal';
import { SettingsModal } from './components/SettingsModal';
import { SwaggerDocsModal } from './components/SwaggerDocsModal';
import { 
  patientService, 
  recordService, 
  summaryService, 
  planService, 
  insightService, 
  authService, 
  checkBackendHealth 
} from './services/api';
import { Github, ExternalLink, Activity, ShieldCheck, Heart, Sparkles } from 'lucide-react';

export function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [patients, setPatients] = useState([]);
  const [activePatient, setActivePatient] = useState(null);
  const [records, setRecords] = useState([]);
  const [summary, setSummary] = useState(null);
  const [plans, setPlans] = useState([]);
  const [insights, setInsights] = useState([]);
  const [backendOnline, setBackendOnline] = useState(false);
  const [user, setUser] = useState(null);

  // Modals
  const [authOpen, setAuthOpen] = useState(false);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [swaggerOpen, setSwaggerOpen] = useState(false);

  // Initialize
  useEffect(() => {
    setUser(authService.getCurrentUser());
    refreshHealth();
    loadPatients();
  }, []);

  // When active patient changes, load records, summary, plans, and insights
  useEffect(() => {
    if (activePatient) {
      loadPatientData(activePatient.id);
    }
  }, [activePatient]);

  const refreshHealth = async () => {
    const health = await checkBackendHealth();
    setBackendOnline(health.online);
  };

  const loadPatients = async () => {
    const pts = await patientService.getPatients();
    setPatients(pts);
    if (pts.length > 0 && !activePatient) {
      setActivePatient(pts[0]);
    }
  };

  const loadPatientData = async (patientId) => {
    try {
      const [recs, summ, plns, ins] = await Promise.all([
        recordService.getRecentRecords(patientId),
        summaryService.getWeeklySummary(patientId),
        planService.getPlans(patientId),
        insightService.getInsights(patientId)
      ]);
      setRecords(recs);
      setSummary(summ);
      setPlans(plns);
      setInsights(ins);
    } catch (err) {
      console.error('Error fetching patient data', err);
    }
  };

  const handleAddPatient = async (patientData) => {
    const created = await patientService.createPatient(patientData);
    setPatients(prev => [created, ...prev]);
    setActivePatient(created);
  };

  const handleLogRecord = async (recordData) => {
    if (!activePatient) return;
    const created = await recordService.logRecord(activePatient.id, recordData);
    setRecords(prev => [created, ...prev]);
    const updatedSummary = await summaryService.getWeeklySummary(activePatient.id);
    setSummary(updatedSummary);
  };

  const handleCreatePlan = async (planData) => {
    if (!activePatient) return;
    const created = await planService.createPlan(activePatient.id, planData);
    setPlans(prev => [created, ...prev]);
  };

  const handleGenerateInsight = async () => {
    if (!activePatient) return;
    const newInsight = await insightService.generateInsight(activePatient.id);
    setInsights(prev => [newInsight, ...prev]);
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#0B0F19] text-slate-100">
      
      {/* Navigation */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        activePatient={activePatient}
        patients={patients}
        setActivePatient={setActivePatient}
        backendOnline={backendOnline}
        onOpenAuth={() => setAuthOpen(true)}
        onOpenSettings={() => setSettingsOpen(true)}
        onOpenSwagger={() => setSwaggerOpen(true)}
        user={user}
        onLogout={() => {
          authService.logout();
          setUser(null);
        }}
      />

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        {activeTab === 'dashboard' && (
          <Dashboard
            activePatient={activePatient}
            summary={summary}
            records={records}
            plans={plans}
            insights={insights}
            onNavigateTab={setActiveTab}
            onOpenSwagger={() => setSwaggerOpen(true)}
          />
        )}

        {activeTab === 'patients' && (
          <PatientManager
            patients={patients}
            activePatient={activePatient}
            onSelectPatient={setActivePatient}
            onAddPatient={handleAddPatient}
          />
        )}

        {activeTab === 'logger' && (
          <WellnessLogger
            activePatient={activePatient}
            onLogRecord={handleLogRecord}
          />
        )}

        {activeTab === 'plans' && (
          <PlansManager
            activePatient={activePatient}
            plans={plans}
            onCreatePlan={handleCreatePlan}
          />
        )}

        {activeTab === 'insights' && (
          <AiInsights
            activePatient={activePatient}
            insights={insights}
            onGenerateInsight={handleGenerateInsight}
          />
        )}
      </main>

      {/* Modals */}
      <AuthModal
        isOpen={authOpen}
        onClose={() => setAuthOpen(false)}
        onAuthSuccess={(userData) => setUser(userData)}
      />

      <SettingsModal
        isOpen={settingsOpen}
        onClose={() => setSettingsOpen(false)}
        backendOnline={backendOnline}
        onRefreshHealth={refreshHealth}
      />

      <SwaggerDocsModal
        isOpen={swaggerOpen}
        onClose={() => setSwaggerOpen(false)}
      />

      {/* Footer */}
      <footer className="mt-12 bg-slate-900 border-t border-slate-800/80 py-8 text-xs text-slate-400">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-4">
          
          <div className="flex items-center space-x-3 text-center md:text-left">
            <div className="w-8 h-8 rounded-lg bg-teal-500/20 text-teal-400 flex items-center justify-center">
              <Activity size={16} />
            </div>
            <div>
              <span className="font-bold text-white block">
                AI-Enabled Patient Wellness Monitoring Platform
              </span>
              <span className="text-[11px] text-slate-500">
                Engineered with Spring Boot 3, Java 21, React 18, Tailwind CSS & Docker
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            <a
              href="https://github.com/Bharathsirikonda/ai-patient-wellness-monitoring-system"
              target="_blank"
              rel="noreferrer"
              className="flex items-center space-x-1.5 text-slate-400 hover:text-white transition-colors"
            >
              <Github size={15} />
              <span>GitHub Repo</span>
            </a>
            <span>•</span>
            <button
              onClick={() => setSwaggerOpen(true)}
              className="text-teal-400 hover:text-teal-300 font-semibold"
            >
              Swagger OpenAPI
            </button>
            <span>•</span>
            <span className="text-slate-500">
              © {new Date().getFullYear()} Bharath Sirikonda
            </span>
          </div>

        </div>
      </footer>

    </div>
  );
}

export default App;
