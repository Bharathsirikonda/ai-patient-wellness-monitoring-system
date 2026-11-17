import React, { useState } from 'react';
import { Lock, Mail, User, X, CheckCircle, Shield } from 'lucide-react';
import { authService } from '../services/api';

export const AuthModal = ({ isOpen, onClose, onAuthSuccess }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('bharathsirikonda2003s@gmail.com');
  const [password, setPassword] = useState('Password123!');
  const [fullName, setFullName] = useState('Bharath Sirikonda');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (isRegister) {
        await authService.register(fullName, email, password);
        await authService.login(email, password);
      } else {
        await authService.login(email, password);
      }
      onAuthSuccess({ email, fullName });
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed. Please check credentials or switch to Demo Mode.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fadeIn">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl relative">
        
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800"
        >
          <X size={18} />
        </button>

        <div className="flex items-center space-x-2.5 mb-2">
          <div className="p-2 rounded-xl bg-teal-500/20 text-teal-400">
            <Shield size={18} />
          </div>
          <span className="text-xs font-bold text-teal-400 uppercase tracking-wider">
            Spring Security & JWT
          </span>
        </div>

        <h2 className="text-xl font-extrabold text-white">
          {isRegister ? 'Register Platform User' : 'Sign In to Wellness AI'}
        </h2>
        <p className="text-xs text-slate-400 mb-6 mt-1">
          {isRegister 
            ? 'Create a secure practitioner / patient account with BCrypt password hashing.' 
            : 'Access multi-tenant patient wellness records and AI analytics.'}
        </p>

        {error && (
          <div className="p-3 mb-4 rounded-xl bg-rose-950/60 border border-rose-500/40 text-rose-300 text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          {isRegister && (
            <div>
              <label className="block text-slate-300 font-semibold mb-1">Full Name</label>
              <div className="relative">
                <User size={15} className="absolute left-3.5 top-3 text-slate-500" />
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="Bharath Sirikonda"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl pl-10 pr-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block text-slate-300 font-semibold mb-1">Email Address</label>
            <div className="relative">
              <Mail size={15} className="absolute left-3.5 top-3 text-slate-500" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="practitioner@example.com"
                className="w-full bg-slate-800 border border-slate-700 rounded-xl pl-10 pr-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-slate-300 font-semibold mb-1">Password</label>
            <div className="relative">
              <Lock size={15} className="absolute left-3.5 top-3 text-slate-500" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full bg-slate-800 border border-slate-700 rounded-xl pl-10 pr-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-2 py-3 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-teal-500/20"
          >
            {loading ? 'Authenticating...' : isRegister ? 'Create Account' : 'Authenticate & Issue JWT'}
          </button>
        </form>

        <div className="mt-5 text-center text-xs text-slate-400">
          {isRegister ? (
            <span>
              Already have an account?{' '}
              <button 
                onClick={() => setIsRegister(false)} 
                className="text-teal-400 font-bold hover:underline"
              >
                Sign In
              </button>
            </span>
          ) : (
            <span>
              Need a new practitioner profile?{' '}
              <button 
                onClick={() => setIsRegister(true)} 
                className="text-teal-400 font-bold hover:underline"
              >
                Register Here
              </button>
            </span>
          )}
        </div>

      </div>
    </div>
  );
};
