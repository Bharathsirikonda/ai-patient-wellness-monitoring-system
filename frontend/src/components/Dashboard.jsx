import React, { useState } from 'react';
import { 
  Activity, 
  Droplet, 
  Moon, 
  Dumbbell, 
  Smile, 
  Zap, 
  AlertTriangle, 
  Sparkles, 
  ArrowUpRight, 
  Calendar,
  CheckCircle2,
  TrendingUp,
  FileCode,
  Target
} from 'lucide-react';

export const Dashboard = ({ 
  activePatient, 
  summary, 
  records, 
  plans, 
  insights, 
  onNavigateTab, 
  onOpenSwagger 
}) => {
  const [period, setPeriod] = useState('weekly');

  const activePlan = plans && plans.find(p => p.status === 'ACTIVE');
  const latestInsight = insights && insights[0];

  return (
    <div className="space-y-8 animate-fadeIn">
      
      {/* Top Welcome / Patient Header */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl pointer-events-none -mr-20 -mt-20"></div>
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          
          <div>
            <div className="flex items-center space-x-2.5 mb-2">
              <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-teal-500/20 text-teal-300 border border-teal-500/30">
                Active Monitoring Profile
              </span>
              <span className="text-xs text-slate-400">
                ID #{activePatient?.id || '1'}
              </span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              {activePatient?.fullName || 'Bharath Sirikonda'}
            </h1>
            <p className="text-xs sm:text-sm text-slate-400 mt-1 flex flex-wrap items-center gap-x-4 gap-y-1">
              <span>DOB: {activePatient?.dateOfBirth || '1998-05-15'}</span>
              <span>•</span>
              <span>Gender: {activePatient?.gender || 'MALE'}</span>
              <span>•</span>
              <span>Contact: {activePatient?.contactEmail || 'bharathsirikonda2003s@gmail.com'}</span>
            </p>
          </div>

          {/* Quick Actions */}
          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => onNavigateTab('logger')}
              className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-teal-500/20"
            >
              <Calendar size={15} />
              <span>Log Daily Vitals</span>
            </button>
            <button
              onClick={() => onNavigateTab('insights')}
              className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-750 text-teal-300 border border-teal-500/30 font-semibold text-xs transition-all"
            >
              <Sparkles size={15} className="text-teal-400" />
              <span>AI Insights</span>
            </button>
            <button
              onClick={onOpenSwagger}
              className="p-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition-colors"
              title="Open Swagger API Documentation"
            >
              <FileCode size={16} />
            </button>
          </div>

        </div>
      </div>

      {/* 4 Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
        
        {/* Sleep Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition-all shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-semibold text-slate-400">Sleep Average</span>
            <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-400">
              <Moon size={18} />
            </div>
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl sm:text-3xl font-extrabold text-white">
              {summary?.averageSleepHours || '7.4'}
            </span>
            <span className="text-xs text-slate-400 font-medium">hrs / night</span>
          </div>
          <p className="text-[11px] text-emerald-400 mt-2 flex items-center space-x-1">
            <TrendingUp size={12} />
            <span>Optimal restorative range (7-9h)</span>
          </p>
        </div>

        {/* Water Intake Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition-all shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-semibold text-slate-400">Hydration Volume</span>
            <div className="p-2 rounded-xl bg-cyan-500/10 text-cyan-400">
              <Droplet size={18} />
            </div>
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl sm:text-3xl font-extrabold text-white">
              {summary?.averageWaterIntakeMl || '2,660'}
            </span>
            <span className="text-xs text-slate-400 font-medium">mL / day</span>
          </div>
          <p className="text-[11px] text-cyan-400 mt-2 flex items-center space-x-1">
            <CheckCircle2 size={12} />
            <span>89% of 3000mL target met</span>
          </p>
        </div>

        {/* Exercise Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition-all shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-semibold text-slate-400">Physical Activity</span>
            <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400">
              <Dumbbell size={18} />
            </div>
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl sm:text-3xl font-extrabold text-white">
              {summary?.averageExerciseMinutes || '39'}
            </span>
            <span className="text-xs text-slate-400 font-medium">mins / day</span>
          </div>
          <p className="text-[11px] text-amber-400 mt-2 flex items-center space-x-1">
            <span>273 mins recorded this week</span>
          </p>
        </div>

        {/* Plan Adherence Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition-all shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs font-semibold text-slate-400">Goal Adherence</span>
            <div className="p-2 rounded-xl bg-emerald-500/10 text-emerald-400">
              <Target size={18} />
            </div>
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl sm:text-3xl font-extrabold text-emerald-400">
              {activePlan?.adherenceProgress?.overallPercentage || '88'}%
            </span>
            <span className="text-xs text-slate-400 font-medium">On Track</span>
          </div>
          <div className="w-full bg-slate-800 h-2 rounded-full mt-3 overflow-hidden">
            <div 
              className="bg-emerald-500 h-full rounded-full transition-all"
              style={{ width: `${activePlan?.adherenceProgress?.overallPercentage || 88}%` }}
            ></div>
          </div>
        </div>

      </div>

      {/* Main Grid: AI Spotlight & Recent Logs */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 sm:gap-8">
        
        {/* Left 2 Cols: Recent Logs Table */}
        <div className="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h2 className="text-base font-bold text-white tracking-tight">
                Recent Daily Wellness Records
              </h2>
              <p className="text-xs text-slate-400">
                Enforcing strict temporal validation & one-entry-per-day integrity
              </p>
            </div>
            <button
              onClick={() => onNavigateTab('logger')}
              className="text-xs font-bold text-teal-400 hover:text-teal-300 flex items-center space-x-1"
            >
              <span>+ Log Today</span>
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800 bg-slate-850/50">
                <tr>
                  <th className="py-3 px-3">Date</th>
                  <th className="py-3 px-3">Sleep</th>
                  <th className="py-3 px-3">Water</th>
                  <th className="py-3 px-3">Exercise</th>
                  <th className="py-3 px-3">Mood</th>
                  <th className="py-3 px-3">Energy</th>
                  <th className="py-3 px-3">Stress</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 font-medium text-slate-300">
                {records && records.length > 0 ? (
                  records.slice(0, 5).map((rec) => (
                    <tr key={rec.id} className="hover:bg-slate-850/40 transition-colors">
                      <td className="py-3 px-3 font-semibold text-white whitespace-nowrap">
                        {rec.recordDate}
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {rec.sleepHours}h
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {rec.waterIntakeMl} mL
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {rec.exerciseMinutes}m
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                          rec.mood === 'EXCELLENT' || rec.mood === 'HAPPY' 
                            ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                            : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                        }`}>
                          {rec.mood}
                        </span>
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="text-amber-400 font-bold">{rec.energyLevel}</span> / 10
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="text-indigo-400 font-bold">{rec.stressLevel}</span> / 10
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" className="py-6 text-center text-slate-500">
                      No wellness records logged yet. Click "+ Log Today" to add your first vitals.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Right 1 Col: AI Wellness Spotlight */}
        <div className="bg-gradient-to-b from-slate-900 to-slate-900 border border-teal-500/30 rounded-3xl p-6 shadow-xl relative overflow-hidden flex flex-col justify-between">
          <div className="absolute top-0 right-0 w-48 h-48 bg-teal-500/10 rounded-full blur-2xl pointer-events-none"></div>

          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-2">
                <div className="p-2 rounded-xl bg-teal-500/20 text-teal-400 border border-teal-500/30">
                  <Sparkles size={16} />
                </div>
                <div>
                  <h3 className="font-bold text-sm text-white">AI Health Synthesis</h3>
                  <span className="text-[10px] text-teal-400 font-medium">Privacy-First Offline / OpenAI</span>
                </div>
              </div>
              <span className="text-[11px] px-2 py-0.5 rounded-full font-bold bg-teal-500/20 text-teal-300">
                Score: {latestInsight?.adherenceScore || 91}/100
              </span>
            </div>

            <div className="space-y-3 my-4">
              <h4 className="text-xs uppercase tracking-wider text-slate-400 font-semibold">
                Clinical Lifestyle Recommendations
              </h4>
              <ul className="space-y-2 text-xs text-slate-300">
                {latestInsight?.recommendations ? (
                  latestInsight.recommendations.slice(0, 3).map((rec, i) => (
                    <li key={i} className="flex items-start space-x-2">
                      <span className="text-teal-400 font-bold">•</span>
                      <span className="leading-relaxed">{rec}</span>
                    </li>
                  ))
                ) : (
                  <li className="text-slate-400">Click below to generate comprehensive AI wellness insights.</li>
                )}
              </ul>
            </div>

            {/* Non-medical Disclaimer */}
            <div className="mt-4 p-3 rounded-xl bg-slate-850/80 border border-slate-700/60 text-[10px] text-slate-400 leading-normal flex items-start space-x-2">
              <AlertTriangle size={14} className="text-amber-400 shrink-0 mt-0.5" />
              <span>
                Generated for lifestyle educational purposes only. Non-diagnostic.
              </span>
            </div>
          </div>

          <button
            onClick={() => onNavigateTab('insights')}
            className="w-full mt-5 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs flex items-center justify-center space-x-2 transition-all shadow-md shadow-teal-500/20"
          >
            <span>Explore Full AI Report</span>
            <ArrowUpRight size={14} />
          </button>

        </div>

      </div>

    </div>
  );
};
