import React, { useState } from 'react';
import { 
  Sparkles, 
  AlertTriangle, 
  CheckCircle2, 
  Clock, 
  Brain, 
  RefreshCw, 
  ShieldCheck,
  ChevronRight
} from 'lucide-react';

export const AiInsights = ({ activePatient, insights, onGenerateInsight }) => {
  const [generating, setGenerating] = useState(false);
  const latest = insights && insights[0];

  const handleGenerate = async () => {
    setGenerating(true);
    try {
      await onGenerateInsight();
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      
      {/* Top Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2 mb-1">
            <span className="p-1.5 rounded-lg bg-teal-500/20 text-teal-400">
              <Brain size={16} />
            </span>
            <h1 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
              AI-Driven Clinical Lifestyle Insights Subsystem
            </h1>
          </div>
          <p className="text-xs sm:text-sm text-slate-400">
            Synthesizing health markers for <span className="text-teal-400 font-semibold">{activePatient?.fullName}</span> with zero PII exposure & built-in clinical safety guardrails.
          </p>
        </div>

        <button
          onClick={handleGenerate}
          disabled={generating}
          className="flex items-center space-x-2 px-5 py-3 rounded-xl bg-gradient-to-r from-teal-500 to-emerald-400 hover:from-teal-400 hover:to-emerald-300 text-slate-950 font-bold text-xs transition-all shadow-lg shadow-teal-500/20 shrink-0"
        >
          <Sparkles size={16} className={generating ? 'animate-spin' : ''} />
          <span>{generating ? 'Synthesizing Lifestyle Metrics...' : 'Generate AI Insights'}</span>
        </button>
      </div>

      {/* Mandatory Medical Safety Guardrail Alert */}
      <div className="p-4 rounded-2xl bg-amber-950/40 border border-amber-500/40 text-amber-200 text-xs flex items-start space-x-3 shadow-sm">
        <AlertTriangle size={18} className="text-amber-400 shrink-0 mt-0.5" />
        <div>
          <span className="font-bold uppercase tracking-wider text-[10px] text-amber-300 block mb-0.5">
            Non-Medical Disclaimer & Regulatory Safety Guardrail
          </span>
          <p className="text-slate-300 leading-relaxed text-[11px]">
            {latest?.disclaimer || "AI Wellness Insights are generated for motivational and lifestyle educational purposes only. They do not constitute medical diagnosis, clinical treatment, prescription, or healthcare professional advice."}
          </p>
        </div>
      </div>

      {/* Main Latest Insight Report Card */}
      {latest ? (
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
          
          <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-800 gap-4">
            <div>
              <div className="flex items-center space-x-2">
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-teal-500/20 text-teal-300 border border-teal-500/30">
                  Latest Synthesis Report
                </span>
                <span className="text-xs text-slate-500 flex items-center space-x-1">
                  <Clock size={12} />
                  <span>{new Date(latest.generatedAt).toLocaleString()}</span>
                </span>
              </div>
              <h2 className="text-lg font-bold text-white mt-1">
                Holistic Lifestyle Behavioral Synthesis
              </h2>
            </div>

            <div className="flex items-center space-x-3">
              <div className="text-right">
                <span className="text-2xl font-black text-teal-400">{latest.adherenceScore}/100</span>
                <span className="text-[10px] text-slate-400 block uppercase font-bold tracking-wider">Lifestyle Score</span>
              </div>
              <div className="w-12 h-12 rounded-2xl bg-teal-500/10 border border-teal-500/30 flex items-center justify-center text-teal-400 font-extrabold text-sm">
                <ShieldCheck size={24} />
              </div>
            </div>
          </div>

          {/* Inference Provider Badge */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-850 border border-slate-800 text-xs">
            <span className="text-slate-400">Inference Engine:</span>
            <span className="font-semibold text-teal-400 font-mono text-[11px]">
              {latest.provider || 'DETERMINISTIC_FALLBACK (Offline Heuristic Engine)'}
            </span>
          </div>

          {/* Observations */}
          <div className="space-y-3">
            <h3 className="text-xs uppercase tracking-wider text-slate-400 font-bold flex items-center space-x-2">
              <CheckCircle2 size={14} className="text-emerald-400" />
              <span>Positive Observations & Trends</span>
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
              {latest.observations && latest.observations.map((obs, idx) => (
                <div key={idx} className="bg-slate-850/60 border border-slate-800 p-4 rounded-2xl text-xs text-slate-200 leading-relaxed flex items-start space-x-2">
                  <span className="text-teal-400 font-extrabold">•</span>
                  <span>{obs}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Recommendations */}
          <div className="space-y-3 pt-2">
            <h3 className="text-xs uppercase tracking-wider text-slate-400 font-bold flex items-center space-x-2">
              <Sparkles size={14} className="text-teal-400" />
              <span>Actionable Lifestyle Recommendations</span>
            </h3>
            <div className="space-y-2.5">
              {latest.recommendations && latest.recommendations.map((rec, idx) => (
                <div key={idx} className="bg-slate-850 p-4 rounded-2xl border border-slate-800/80 text-xs text-slate-200 flex items-start space-x-3">
                  <span className="w-6 h-6 rounded-lg bg-teal-500/20 text-teal-300 font-bold flex items-center justify-center shrink-0 text-[11px]">
                    {idx + 1}
                  </span>
                  <p className="leading-relaxed mt-0.5">{rec}</p>
                </div>
              ))}
            </div>
          </div>

        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-12 text-center">
          <Sparkles size={36} className="text-teal-400 mx-auto mb-3" />
          <h3 className="text-base font-bold text-white mb-1">No AI Insights Generated Yet</h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto mb-5">
            Click the "Generate AI Insights" button above to synthesize lifestyle markers and generate habit recommendations.
          </p>
        </div>
      )}

      {/* Historical Insights Accordion / List */}
      {insights && insights.length > 1 && (
        <div className="space-y-3 pt-4">
          <h3 className="text-xs uppercase tracking-wider text-slate-400 font-bold">
            Previous AI Insight Reports
          </h3>
          <div className="space-y-2">
            {insights.slice(1).map((item) => (
              <div 
                key={item.id}
                className="bg-slate-900/60 border border-slate-800 p-4 rounded-2xl flex items-center justify-between text-xs"
              >
                <div>
                  <span className="font-bold text-white block">
                    Report #{item.id} — Score: {item.adherenceScore}/100
                  </span>
                  <span className="text-[11px] text-slate-500">
                    Generated: {new Date(item.generatedAt).toLocaleString()}
                  </span>
                </div>
                <span className="text-teal-400 font-semibold text-[11px] flex items-center space-x-1">
                  <span>Archived</span>
                  <ChevronRight size={14} />
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

    </div>
  );
};
