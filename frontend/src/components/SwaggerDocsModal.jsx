import React from 'react';
import { FileCode, ExternalLink, X, Shield, CheckCircle, Database } from 'lucide-react';
import { getApiBaseUrl } from '../services/api';

export const SwaggerDocsModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;
  const baseUrl = getApiBaseUrl();
  const swaggerUrl = `${baseUrl}/swagger-ui/index.html`;
  const openApiUrl = `${baseUrl}/v3/api-docs`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fadeIn">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-2xl w-full shadow-2xl relative max-h-[90vh] overflow-y-auto">
        
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800"
        >
          <X size={18} />
        </button>

        <div className="flex items-center space-x-2.5 mb-2">
          <div className="p-2 rounded-xl bg-teal-500/20 text-teal-400">
            <FileCode size={20} />
          </div>
          <span className="text-xs font-bold text-teal-400 uppercase tracking-wider">
            OpenAPI 3.0 / Springdoc
          </span>
        </div>

        <h2 className="text-xl font-extrabold text-white">
          Swagger Interactive API Documentation
        </h2>
        <p className="text-xs text-slate-400 mb-6 mt-1">
          Explore and interact directly with all secure RESTful endpoints exposed by the Spring Boot backend service.
        </p>

        {/* Live Buttons */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-6">
          <a
            href={swaggerUrl}
            target="_blank"
            rel="noreferrer"
            className="flex items-center justify-between p-4 rounded-2xl bg-teal-500/10 border border-teal-500/30 text-teal-300 hover:bg-teal-500/20 transition-all text-xs font-bold"
          >
            <div className="flex items-center space-x-2">
              <FileCode size={16} className="text-teal-400" />
              <span>Open Swagger UI Console</span>
            </div>
            <ExternalLink size={14} />
          </a>

          <a
            href={openApiUrl}
            target="_blank"
            rel="noreferrer"
            className="flex items-center justify-between p-4 rounded-2xl bg-slate-850 border border-slate-700 text-slate-300 hover:bg-slate-800 transition-all text-xs font-bold"
          >
            <div className="flex items-center space-x-2">
              <Database size={16} className="text-indigo-400" />
              <span>Raw OpenAPI JSON Spec</span>
            </div>
            <ExternalLink size={14} />
          </a>
        </div>

        {/* Instructions */}
        <div className="p-4 rounded-2xl bg-slate-850/80 border border-slate-800 space-y-2 mb-6 text-xs text-slate-300">
          <h4 className="font-bold text-white flex items-center space-x-1.5">
            <Shield size={14} className="text-teal-400" />
            <span>How to Test Protected Endpoints in Swagger:</span>
          </h4>
          <ol className="list-decimal list-inside space-y-1 text-slate-400 text-[11px] leading-relaxed">
            <li>Call <code className="text-teal-300 bg-slate-800 px-1 py-0.5 rounded">POST /api/v1/auth/login</code> in Swagger UI with your credentials.</li>
            <li>Copy the returned <code className="text-teal-300 bg-slate-800 px-1 py-0.5 rounded">accessToken</code> value.</li>
            <li>Click the green <strong className="text-white">Authorize</strong> button at the top of Swagger UI.</li>
            <li>Enter <code className="text-teal-300 bg-slate-800 px-1 py-0.5 rounded">Bearer YOUR_TOKEN</code> and click Authorize.</li>
          </ol>
        </div>

        {/* Endpoints Table Summary */}
        <div className="space-y-3">
          <h3 className="text-xs uppercase tracking-wider text-slate-400 font-bold">
            Available Controller Endpoints
          </h3>
          <div className="divide-y divide-slate-800 text-xs text-slate-300">
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-emerald-500/20 text-emerald-400 font-bold">POST</span>
                <span className="font-mono text-slate-200">/api/v1/auth/register</span>
              </div>
              <span className="text-slate-500 text-[11px]">User Registration</span>
            </div>
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-emerald-500/20 text-emerald-400 font-bold">POST</span>
                <span className="font-mono text-slate-200">/api/v1/auth/login</span>
              </div>
              <span className="text-slate-500 text-[11px]">JWT Issuance</span>
            </div>
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-blue-500/20 text-blue-400 font-bold">GET</span>
                <span className="font-mono text-slate-200">/api/v1/patients</span>
              </div>
              <span className="text-slate-500 text-[11px]">List Tenant Patients</span>
            </div>
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-emerald-500/20 text-emerald-400 font-bold">POST</span>
                <span className="font-mono text-slate-200">/api/v1/patients/{`{id}`}/wellness-records</span>
              </div>
              <span className="text-slate-500 text-[11px]">Daily Vitals Entry</span>
            </div>
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-blue-500/20 text-blue-400 font-bold">GET</span>
                <span className="font-mono text-slate-200">/api/v1/patients/{`{id}`}/wellness-summary/weekly</span>
              </div>
              <span className="text-slate-500 text-[11px]">Weekly Statistical Rollup</span>
            </div>
            <div className="py-2 flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <span className="px-1.5 py-0.5 rounded font-mono text-[10px] bg-emerald-500/20 text-emerald-400 font-bold">POST</span>
                <span className="font-mono text-slate-200">/api/v1/patients/{`{id}`}/insights/generate</span>
              </div>
              <span className="text-slate-500 text-[11px]">Synthesize AI Health Insights</span>
            </div>
          </div>
        </div>

        <div className="mt-6 pt-4 border-t border-slate-800 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-semibold text-xs"
          >
            Close
          </button>
        </div>

      </div>
    </div>
  );
};
