import React, { useState } from 'react';
import { 
  Calendar, 
  Moon, 
  Droplet, 
  Dumbbell, 
  Smile, 
  Zap, 
  AlertCircle, 
  CheckCircle,
  Plus
} from 'lucide-react';

export const WellnessLogger = ({ activePatient, onLogRecord }) => {
  const [formData, setFormData] = useState({
    recordDate: new Date().toISOString().split('T')[0],
    sleepHours: 7.5,
    waterIntakeMl: 2500,
    exerciseMinutes: 45,
    mood: 'HAPPY',
    energyLevel: 8,
    stressLevel: 3,
    notes: 'Feeling energetic after morning workout and optimal hydration.'
  });

  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState(null);

  const moods = [
    { value: 'EXCELLENT', label: 'Excellent', icon: '🌟', color: 'border-emerald-500 text-emerald-400 bg-emerald-500/10' },
    { value: 'HAPPY', label: 'Happy', icon: '😊', color: 'border-teal-500 text-teal-400 bg-teal-500/10' },
    { value: 'NEUTRAL', label: 'Neutral', icon: '😐', color: 'border-slate-500 text-slate-300 bg-slate-500/10' },
    { value: 'TIRED', label: 'Tired', icon: '🥱', color: 'border-amber-500 text-amber-400 bg-amber-500/10' },
    { value: 'STRESSED', label: 'Stressed', icon: '😫', color: 'border-rose-500 text-rose-400 bg-rose-500/10' },
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSuccessMsg(null);
    try {
      await onLogRecord(formData);
      setSuccessMsg('Daily wellness vitals successfully recorded into clinical audit log.');
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err) {
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  const addWater = (amount) => {
    setFormData(prev => ({
      ...prev,
      waterIntakeMl: Math.min(6000, Number(prev.waterIntakeMl) + amount)
    }));
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 animate-fadeIn">
      
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
          Daily Wellness Record Logger
        </h1>
        <p className="text-xs sm:text-sm text-slate-400 mt-1">
          Logging markers for <span className="text-teal-400 font-semibold">{activePatient?.fullName || 'Active Patient'}</span>. Backend validates physiological ranges & unique date constraints.
        </p>
      </div>

      {successMsg && (
        <div className="p-4 rounded-2xl bg-emerald-950/80 border border-emerald-500/50 text-emerald-300 text-xs flex items-center space-x-2">
          <CheckCircle size={16} className="text-emerald-400 shrink-0" />
          <span>{successMsg}</span>
        </div>
      )}

      {/* Main Form Card */}
      <form onSubmit={handleSubmit} className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
        
        {/* Date Selector */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-800 gap-4">
          <div>
            <label className="block text-xs uppercase tracking-wider text-slate-400 font-bold mb-1">
              Log Date
            </label>
            <p className="text-[11px] text-slate-500">Only one daily entry is permitted per patient per calendar day.</p>
          </div>
          <input
            type="date"
            required
            value={formData.recordDate}
            onChange={(e) => setFormData({ ...formData, recordDate: e.target.value })}
            className="bg-slate-850 border border-slate-700 rounded-xl px-4 py-2 text-xs text-white focus:outline-none focus:border-teal-500"
          />
        </div>

        {/* 3 Metric Inputs (Sleep, Water, Exercise) */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          
          {/* Sleep */}
          <div className="bg-slate-850/60 border border-slate-800 rounded-2xl p-4">
            <div className="flex items-center space-x-2 text-indigo-400 mb-2">
              <Moon size={16} />
              <span className="font-bold text-xs">Sleep Duration</span>
            </div>
            <div className="flex items-center space-x-2">
              <input
                type="number"
                step="0.1"
                min="0"
                max="24"
                required
                value={formData.sleepHours}
                onChange={(e) => setFormData({ ...formData, sleepHours: parseFloat(e.target.value) || 0 })}
                className="w-24 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-base font-extrabold text-white text-center focus:outline-none focus:border-teal-500"
              />
              <span className="text-xs text-slate-400">hours / day</span>
            </div>
          </div>

          {/* Hydration */}
          <div className="bg-slate-850/60 border border-slate-800 rounded-2xl p-4">
            <div className="flex items-center space-x-2 text-cyan-400 mb-2">
              <Droplet size={16} />
              <span className="font-bold text-xs">Hydration Volume</span>
            </div>
            <div className="flex items-center space-x-2 mb-2">
              <input
                type="number"
                step="50"
                min="0"
                max="8000"
                required
                value={formData.waterIntakeMl}
                onChange={(e) => setFormData({ ...formData, waterIntakeMl: parseInt(e.target.value) || 0 })}
                className="w-28 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-base font-extrabold text-white text-center focus:outline-none focus:border-teal-500"
              />
              <span className="text-xs text-slate-400">mL</span>
            </div>
            {/* Quick Increment Buttons */}
            <div className="flex items-center space-x-1">
              <button
                type="button"
                onClick={() => addWater(250)}
                className="px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-750 text-[10px] text-cyan-300 font-semibold border border-slate-700"
              >
                +250ml
              </button>
              <button
                type="button"
                onClick={() => addWater(500)}
                className="px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-750 text-[10px] text-cyan-300 font-semibold border border-slate-700"
              >
                +500ml
              </button>
              <button
                type="button"
                onClick={() => addWater(1000)}
                className="px-2 py-1 rounded-lg bg-slate-800 hover:bg-slate-750 text-[10px] text-cyan-300 font-semibold border border-slate-700"
              >
                +1L
              </button>
            </div>
          </div>

          {/* Exercise */}
          <div className="bg-slate-850/60 border border-slate-800 rounded-2xl p-4">
            <div className="flex items-center space-x-2 text-amber-400 mb-2">
              <Dumbbell size={16} />
              <span className="font-bold text-xs">Physical Activity</span>
            </div>
            <div className="flex items-center space-x-2">
              <input
                type="number"
                step="5"
                min="0"
                max="720"
                required
                value={formData.exerciseMinutes}
                onChange={(e) => setFormData({ ...formData, exerciseMinutes: parseInt(e.target.value) || 0 })}
                className="w-24 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-base font-extrabold text-white text-center focus:outline-none focus:border-teal-500"
              />
              <span className="text-xs text-slate-400">minutes</span>
            </div>
          </div>

        </div>

        {/* Mood Category */}
        <div className="space-y-2">
          <label className="block text-xs uppercase tracking-wider text-slate-400 font-bold">
            Emotional State / Mood
          </label>
          <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
            {moods.map((m) => {
              const selected = formData.mood === m.value;
              return (
                <button
                  type="button"
                  key={m.value}
                  onClick={() => setFormData({ ...formData, mood: m.value })}
                  className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-bold transition-all ${
                    selected ? m.color + ' ring-2 ring-teal-500/40 shadow-md' : 'border-slate-800 bg-slate-850/50 text-slate-400 hover:border-slate-700'
                  }`}
                >
                  <span className="text-2xl mb-1">{m.icon}</span>
                  <span>{m.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Sliders for Energy & Stress */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 pt-2">
          
          {/* Energy */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-bold">
              <span className="text-amber-400 flex items-center space-x-1">
                <Zap size={14} />
                <span>Energy Level (1-10)</span>
              </span>
              <span className="text-white px-2 py-0.5 rounded bg-slate-800 border border-slate-700 font-extrabold">
                {formData.energyLevel}
              </span>
            </div>
            <input
              type="range"
              min="1"
              max="10"
              value={formData.energyLevel}
              onChange={(e) => setFormData({ ...formData, energyLevel: parseInt(e.target.value) })}
              className="w-full accent-amber-500 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] text-slate-500">
              <span>Depleted (1)</span>
              <span>Optimal Vitality (10)</span>
            </div>
          </div>

          {/* Stress */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-bold">
              <span className="text-rose-400 flex items-center space-x-1">
                <AlertCircle size={14} />
                <span>Perceived Stress Level (1-10)</span>
              </span>
              <span className="text-white px-2 py-0.5 rounded bg-slate-800 border border-slate-700 font-extrabold">
                {formData.stressLevel}
              </span>
            </div>
            <input
              type="range"
              min="1"
              max="10"
              value={formData.stressLevel}
              onChange={(e) => setFormData({ ...formData, stressLevel: parseInt(e.target.value) })}
              className="w-full accent-rose-500 cursor-pointer"
            />
            <div className="flex justify-between text-[10px] text-slate-500">
              <span>Calm / Relaxed (1)</span>
              <span>Acute Tension (10)</span>
            </div>
          </div>

        </div>

        {/* Subjective Notes */}
        <div className="space-y-2 pt-2">
          <label className="block text-xs uppercase tracking-wider text-slate-400 font-bold">
            Subjective Notes & Observations
          </label>
          <textarea
            rows="3"
            value={formData.notes}
            onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
            placeholder="Document nutrition notes, symptoms, workout details, or daytime lifestyle factors..."
            className="w-full bg-slate-850 border border-slate-700 rounded-2xl p-3.5 text-xs text-slate-200 focus:outline-none focus:border-teal-500"
          ></textarea>
        </div>

        {/* Submit Button */}
        <div className="pt-4 border-t border-slate-800 flex items-center justify-end">
          <button
            type="submit"
            disabled={saving}
            className="flex items-center space-x-2 px-6 py-3 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition-all shadow-lg shadow-teal-500/20"
          >
            <CheckCircle size={15} />
            <span>{saving ? 'Validating & Persisting...' : 'Save Daily Wellness Record'}</span>
          </button>
        </div>

      </form>

    </div>
  );
};
