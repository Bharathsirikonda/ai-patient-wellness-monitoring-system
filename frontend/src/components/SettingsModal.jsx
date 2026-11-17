import React, { useState } from 'react';
import { Settings, X, Wifi, Server, CheckCircle2, RefreshCw, AlertCircle } from 'lucide-react';
import { getApiBaseUrl, setApiBaseUrl, isDemoModeActive, setDemoMode, checkBackendHealth } from '../services/api';

export const SettingsModal = ({ isOpen, onClose, backendOnline, onRefreshHealth }) => {
  const [url, setUrl] = useState(getApiBaseUrl());
  const [demo, setDemo] = useState(isDemoModeActive());
  const [testing, setTesting] = useState(false);
  const [testResult, setTestResult] = useState(null);

  if (!isOpen) return null;

  const handleSave = () => {
    setApiBaseUrl(url.trim());
    setDemoMode(demo);
    onRefreshHealth();
    onClose();
  };

  const handleTestConnection = async () => {
    setTesting(true);
    setTestResult(null);
    setApiBaseUrl(url.trim());
    try {
      const res = await checkBackendHealth();
      setTestResult(res);
    } finally {
      setTesting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fadeIn">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-2xl relative">
        
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800"
        >
          <X size={18} />
        </button>

        <div className="flex items-center space-x-2.5 mb-2">
          <div className="p-2 rounded-xl bg-teal-500/20 text-teal-400">
            <Server size={18} />
          </div>
          <span className="text-xs font-bold text-teal-400 uppercase tracking-wider">
            Backend Connectivity
          </span>
        </div>

        <h2 className="text-xl font-extrabold text-white">
          API & Service Settings
        </h2>
        <p className="text-xs text-slate-400 mb-6 mt-1">
          Configure whether this frontend connects to your local Spring Boot instance or your deployed Render cloud backend.
        </p>

        <div className="space-y-5 text-xs">
          
          {/* Preset Buttons */}
          <div>
            <label className="block text-slate-300 font-semibold mb-2">
              Preset Configurations
            </label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => setUrl('http://localhost:8080')}
                className={`p-3 rounded-xl border text-left transition-all ${
                  url === 'http://localhost:8080'
                    ? 'border-teal-500 bg-teal-500/10 text-white'
                    : 'border-slate-800 bg-slate-850 text-slate-400 hover:border-slate-700'
                }`}
              >
                <span className="font-bold block text-xs">Localhost Backend</span>
                <span className="text-[10px] text-slate-500 font-mono">http://localhost:8080</span>
              </button>

              <button
                type="button"
                onClick={() => setUrl('https://ai-patient-wellness-backend.onrender.com')}
                className={`p-3 rounded-xl border text-left transition-all ${
                  url.includes('onrender.com')
                    ? 'border-teal-500 bg-teal-500/10 text-white'
                    : 'border-slate-800 bg-slate-850 text-slate-400 hover:border-slate-700'
                }`}
              >
                <span className="font-bold block text-xs">Render Cloud Backend</span>
                <span className="text-[10px] text-slate-500 font-mono truncate block">ai-...onrender.com</span>
              </button>
            </div>
          </div>

          {/* Custom URL Input */}
          <div>
            <label className="block text-slate-300 font-semibold mb-1">
              Active Backend Base URL
            </label>
            <div className="flex space-x-2">
              <input
                type="text"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="http://localhost:8080"
                className="flex-1 bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white font-mono text-xs focus:outline-none focus:border-teal-500"
              />
              <button
                type="button"
                onClick={handleTestConnection}
                disabled={testing}
                className="px-3.5 py-2 rounded-xl bg-slate-800 hover:bg-slate-750 text-slate-200 border border-slate-700 font-semibold text-xs flex items-center space-x-1"
              >
                <RefreshCw size={13} className={testing ? 'animate-spin' : ''} />
                <span>Test</span>
              </button>
            </div>
          </div>

          {/* Connection Test Result Indicator */}
          {testResult && (
            <div className={`p-3 rounded-xl border text-xs flex items-center space-x-2 ${
              testResult.online
                ? 'bg-emerald-950/60 border-emerald-500/40 text-emerald-300'
                : 'bg-rose-950/60 border-rose-500/40 text-rose-300'
            }`}>
              {testResult.online ? (
                <>
                  <CheckCircle2 size={15} className="text-emerald-400 shrink-0" />
                  <span>Connection successful! Spring Boot Actuator responded with status {testResult.status}.</span>
                </>
              ) : (
                <>
                  <AlertCircle size={15} className="text-rose-400 shrink-0" />
                  <span>Cannot reach backend at {url}. (If on Render, it may be waking up from sleep; you can enable Standalone Demo Mode below).</span>
                </>
              )}
            </div>
          )}

          {/* Standalone Demo Mode Toggle */}
          <div className="pt-3 border-t border-slate-800 flex items-center justify-between">
            <div>
              <span className="font-bold text-slate-200 block">Interactive Standalone Demo Mode</span>
              <span className="text-[11px] text-slate-400">
                Allows recruiters to test all features instantly even if the backend is waking up or offline.
              </span>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={demo}
                onChange={(e) => setDemo(e.target.checked)}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-slate-800 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-teal-500"></div>
            </label>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center justify-end space-x-3 pt-4 border-t border-slate-800">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2.5 rounded-xl text-slate-400 hover:text-white"
            >
              Cancel
            </button>
            <button
              type="button"
              onClick={handleSave}
              className="px-5 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold transition-all shadow-md shadow-teal-500/20"
            >
              Apply & Save Settings
            </button>
          </div>

        </div>

      </div>
    </div>
  );
};
