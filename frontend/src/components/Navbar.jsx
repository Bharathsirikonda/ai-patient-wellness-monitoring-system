import React from 'react';
import { 
  Activity, 
  Users, 
  Calendar, 
  Target, 
  Sparkles, 
  FileCode, 
  Settings, 
  ShieldCheck, 
  LogIn, 
  LogOut,
  Wifi,
  WifiOff
} from 'lucide-react';

export const Navbar = ({ 
  activeTab, 
  setActiveTab, 
  activePatient, 
  patients, 
  setActivePatient, 
  backendOnline, 
  onOpenAuth, 
  onOpenSettings, 
  onOpenSwagger, 
  user, 
  onLogout 
}) => {
  const tabs = [
    { id: 'dashboard', label: 'Dashboard', icon: Activity },
    { id: 'patients', label: 'Patients', icon: Users },
    { id: 'logger', label: 'Daily Log', icon: Calendar },
    { id: 'plans', label: 'Wellness Goals', icon: Target },
    { id: 'insights', label: 'AI Insights', icon: Sparkles, badge: 'AI' },
  ];

  return (
    <header className="sticky top-0 z-40 bg-slate-900/90 backdrop-blur-md border-b border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Logo & Brand */}
          <div className="flex items-center space-x-3 cursor-pointer" onClick={() => setActiveTab('dashboard')}>
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-teal-600 to-emerald-400 flex items-center justify-center text-slate-950 font-bold shadow-lg shadow-teal-500/20">
              <Activity size={22} className="text-slate-950 stroke-[2.5]" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-extrabold text-lg tracking-tight text-white">
                  Wellness<span className="text-teal-400">AI</span>
                </span>
                <span className="text-[10px] px-1.5 py-0.5 rounded-full font-bold bg-teal-500/20 text-teal-300 border border-teal-500/30">
                  v1.0
                </span>
              </div>
              <p className="text-[11px] text-slate-400 font-medium hidden sm:block">
                Patient Wellness Monitoring Platform
              </p>
            </div>
          </div>

          {/* Nav Tabs */}
          <nav className="hidden md:flex items-center space-x-1">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center space-x-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all ${
                    isActive 
                      ? 'bg-teal-500/15 text-teal-400 border border-teal-500/30 shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`}
                >
                  <Icon size={15} />
                  <span>{tab.label}</span>
                  {tab.badge && (
                    <span className="px-1.5 py-0.2 rounded text-[9px] font-bold bg-gradient-to-r from-teal-400 to-emerald-400 text-slate-950">
                      {tab.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>

          {/* Right Utilities */}
          <div className="flex items-center space-x-3">
            
            {/* Active Patient Selector (compact) */}
            {patients && patients.length > 0 && (
              <div className="hidden lg:flex items-center space-x-2 bg-slate-850 px-2.5 py-1.5 rounded-xl border border-slate-700/60 text-xs">
                <span className="text-slate-400 text-[11px]">Patient:</span>
                <select
                  value={activePatient?.id || ''}
                  onChange={(e) => {
                    const sel = patients.find(p => p.id === Number(e.target.value));
                    if (sel) setActivePatient(sel);
                  }}
                  className="bg-transparent text-slate-200 font-semibold focus:outline-none cursor-pointer"
                >
                  {patients.map(p => (
                    <option key={p.id} value={p.id} className="bg-slate-900 text-slate-200">
                      {p.fullName}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Backend Connectivity Badge */}
            <div 
              onClick={onOpenSettings}
              className={`cursor-pointer flex items-center space-x-1.5 px-2.5 py-1.5 rounded-xl text-[11px] font-medium border transition-colors ${
                backendOnline 
                  ? 'bg-emerald-950/60 text-emerald-400 border-emerald-500/30 hover:bg-emerald-950'
                  : 'bg-amber-950/60 text-amber-400 border-amber-500/30 hover:bg-amber-950'
              }`}
              title="Click to configure API Base URL"
            >
              {backendOnline ? (
                <>
                  <Wifi size={13} className="text-emerald-400 animate-pulse" />
                  <span className="hidden sm:inline">Backend Live</span>
                </>
              ) : (
                <>
                  <WifiOff size={13} className="text-amber-400" />
                  <span className="hidden sm:inline">Demo / Cloud</span>
                </>
              )}
            </div>

            {/* Swagger Docs Launcher */}
            <button
              onClick={onOpenSwagger}
              className="flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition-colors"
              title="Open Swagger OpenAPI Documentation"
            >
              <FileCode size={14} className="text-teal-400" />
              <span className="hidden sm:inline">Swagger API</span>
            </button>

            {/* Settings */}
            <button
              onClick={onOpenSettings}
              className="p-2 rounded-xl text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
              title="API & Connection Settings"
            >
              <Settings size={16} />
            </button>

            {/* User Auth */}
            {user ? (
              <div className="flex items-center space-x-2">
                <div className="w-8 h-8 rounded-xl bg-teal-500/20 text-teal-300 font-bold text-xs flex items-center justify-center border border-teal-500/30">
                  {user.fullName ? user.fullName[0] : 'U'}
                </div>
                <button
                  onClick={onLogout}
                  className="p-1.5 rounded-lg text-slate-400 hover:text-rose-400 transition-colors"
                  title="Sign Out"
                >
                  <LogOut size={15} />
                </button>
              </div>
            ) : (
              <button
                onClick={onOpenAuth}
                className="flex items-center space-x-1 px-3 py-1.5 rounded-xl text-xs font-bold bg-teal-500 hover:bg-teal-400 text-slate-950 transition-all shadow-md shadow-teal-500/20"
              >
                <LogIn size={13} />
                <span>Sign In</span>
              </button>
            )}

          </div>

        </div>

        {/* Mobile Navigation Row */}
        <div className="flex md:hidden items-center justify-between py-2 border-t border-slate-800/80 overflow-x-auto space-x-1">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap ${
                  isActive 
                    ? 'bg-teal-500/20 text-teal-400 border border-teal-500/40' 
                    : 'text-slate-400'
                }`}
              >
                <Icon size={14} />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

      </div>
    </header>
  );
};
