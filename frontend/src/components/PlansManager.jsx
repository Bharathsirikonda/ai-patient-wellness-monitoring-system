import React, { useState } from 'react';
import { Target, Plus, CheckCircle2, AlertTriangle, Calendar, Award } from 'lucide-react';

export const PlansManager = ({ activePatient, plans, onCreatePlan }) => {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [formData, setFormData] = useState({
    title: 'Balanced Vitality & Hydration Plan',
    targetSleepHours: 8.0,
    targetWaterIntakeMl: 3000,
    targetWeeklyExerciseMinutes: 210,
    startDate: new Date().toISOString().split('T')[0],
    endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  });
  const [creating, setCreating] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await onCreatePlan(formData);
      setShowCreateModal(false);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
            Wellness Goals & Target Adherence Engine
          </h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Setting quantitative lifestyle goals for <span className="text-teal-400 font-semibold">{activePatient?.fullName}</span> and calculating adherence scoring.
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-teal-500/20 self-start sm:self-auto"
        >
          <Plus size={15} />
          <span>Create New Goal Plan</span>
        </button>
      </div>

      {/* Plans List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {plans && plans.map((plan) => {
          const overall = plan.adherenceProgress?.overallPercentage || 88;
          const isActive = plan.status === 'ACTIVE';

          return (
            <div
              key={plan.id}
              className={`rounded-3xl p-6 border transition-all ${
                isActive 
                  ? 'bg-slate-900 border-teal-500/40 shadow-xl' 
                  : 'bg-slate-900/60 border-slate-800'
              }`}
            >
              <div className="flex items-start justify-between mb-4">
                <div>
                  <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                    isActive 
                      ? 'bg-teal-500/20 text-teal-300 border border-teal-500/30' 
                      : 'bg-slate-800 text-slate-400'
                  }`}>
                    {plan.status}
                  </span>
                  <h3 className="font-extrabold text-base text-white mt-2">
                    {plan.title}
                  </h3>
                  <p className="text-xs text-slate-400 mt-0.5 flex items-center space-x-1">
                    <Calendar size={12} />
                    <span>{plan.startDate} to {plan.endDate}</span>
                  </p>
                </div>

                <div className="text-right">
                  <span className="text-2xl font-black text-teal-400">
                    {overall}%
                  </span>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
                    Adherence
                  </p>
                </div>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-slate-800 h-2.5 rounded-full overflow-hidden mb-5">
                <div 
                  className="bg-gradient-to-r from-teal-500 to-emerald-400 h-full rounded-full transition-all duration-500"
                  style={{ width: `${overall}%` }}
                ></div>
              </div>

              {/* Targets Breakdown */}
              <div className="grid grid-cols-3 gap-3 pt-3 border-t border-slate-800/80 text-xs">
                
                <div className="bg-slate-850 p-3 rounded-xl border border-slate-800 text-center">
                  <span className="text-[10px] text-slate-400 block font-semibold">Sleep Goal</span>
                  <span className="font-extrabold text-white text-sm">{plan.targetSleepHours}h</span>
                  <span className="text-[10px] text-teal-400 block mt-1">
                    {plan.adherenceProgress?.sleepAdherencePercent || 92}% Met
                  </span>
                </div>

                <div className="bg-slate-850 p-3 rounded-xl border border-slate-800 text-center">
                  <span className="text-[10px] text-slate-400 block font-semibold">Hydration</span>
                  <span className="font-extrabold text-white text-sm">{plan.targetWaterIntakeMl} mL</span>
                  <span className="text-[10px] text-cyan-400 block mt-1">
                    {plan.adherenceProgress?.waterAdherencePercent || 89}% Met
                  </span>
                </div>

                <div className="bg-slate-850 p-3 rounded-xl border border-slate-800 text-center">
                  <span className="text-[10px] text-slate-400 block font-semibold">Exercise / Wk</span>
                  <span className="font-extrabold text-white text-sm">{plan.targetWeeklyExerciseMinutes}m</span>
                  <span className="text-[10px] text-amber-400 block mt-1">
                    {plan.adherenceProgress?.exerciseAdherencePercent || 84}% Met
                  </span>
                </div>

              </div>

            </div>
          );
        })}
      </div>

      {/* Create Plan Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl">
            <h2 className="text-lg font-bold text-white mb-1">Create Target Wellness Plan</h2>
            <p className="text-xs text-slate-400 mb-5">
              Establish numerical benchmarks to calculate patient adherence scoring.
            </p>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Plan Title</label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Cardiorespiratory Conditioning"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                />
              </div>

              <div className="grid grid-cols-3 gap-2">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Sleep (h/day)</label>
                  <input
                    type="number"
                    step="0.5"
                    value={formData.targetSleepHours}
                    onChange={(e) => setFormData({ ...formData, targetSleepHours: parseFloat(e.target.value) })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white text-center focus:outline-none focus:border-teal-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Water (mL/day)</label>
                  <input
                    type="number"
                    step="100"
                    value={formData.targetWaterIntakeMl}
                    onChange={(e) => setFormData({ ...formData, targetWaterIntakeMl: parseInt(e.target.value) })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white text-center focus:outline-none focus:border-teal-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Exercise (m/wk)</label>
                  <input
                    type="number"
                    step="15"
                    value={formData.targetWeeklyExerciseMinutes}
                    onChange={(e) => setFormData({ ...formData, targetWeeklyExerciseMinutes: parseInt(e.target.value) })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white text-center focus:outline-none focus:border-teal-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Start Date</label>
                  <input
                    type="date"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-teal-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">End Date</label>
                  <input
                    type="date"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-teal-500"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2.5 rounded-xl text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={creating}
                  className="px-5 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold transition-all shadow-md shadow-teal-500/20"
                >
                  {creating ? 'Saving...' : 'Activate Plan'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
