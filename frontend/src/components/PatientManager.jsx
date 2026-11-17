import React, { useState } from 'react';
import { Users, UserPlus, Calendar, Mail, Phone, CheckCircle, Shield } from 'lucide-react';

export const PatientManager = ({ patients, activePatient, onSelectPatient, onAddPatient }) => {
  const [showAddModal, setShowAddModal] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    dateOfBirth: '1998-05-15',
    gender: 'MALE',
    contactEmail: '',
    contactPhone: '+91 '
  });
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.fullName || !formData.dateOfBirth) return;
    setSubmitting(true);
    try {
      await onAddPatient(formData);
      setShowAddModal(false);
      setFormData({
        fullName: '',
        dateOfBirth: '1998-05-15',
        gender: 'MALE',
        contactEmail: '',
        contactPhone: '+91 '
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-white tracking-tight">
            Patient Identity & Multi-Tenant Registry
          </h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Tenant isolation ensures standard users only access patients registered under their authenticated user account.
          </p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold text-xs transition-all shadow-md shadow-teal-500/20 self-start sm:self-auto"
        >
          <UserPlus size={15} />
          <span>Register New Patient</span>
        </button>
      </div>

      {/* Patient Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {patients && patients.map((patient) => {
          const isSelected = activePatient?.id === patient.id;
          return (
            <div
              key={patient.id}
              onClick={() => onSelectPatient(patient)}
              className={`cursor-pointer rounded-2xl p-5 border transition-all ${
                isSelected 
                  ? 'bg-slate-900 border-teal-500/60 shadow-lg shadow-teal-500/10 ring-1 ring-teal-500/30'
                  : 'bg-slate-900/60 border-slate-800 hover:border-slate-700 hover:bg-slate-900'
              }`}
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className={`w-11 h-11 rounded-xl flex items-center justify-center font-extrabold text-sm ${
                    isSelected ? 'bg-teal-500 text-slate-950' : 'bg-slate-800 text-slate-300'
                  }`}>
                    {patient.fullName ? patient.fullName.split(' ').map(n => n[0]).join('') : 'PT'}
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-white">{patient.fullName}</h3>
                    <span className="text-[11px] text-slate-400">ID #{patient.id}</span>
                  </div>
                </div>

                {isSelected && (
                  <span className="flex items-center space-x-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-teal-500/20 text-teal-300 border border-teal-500/40">
                    <CheckCircle size={11} />
                    <span>Active</span>
                  </span>
                )}
              </div>

              <div className="space-y-2 text-xs text-slate-400 border-t border-slate-800/80 pt-3">
                <div className="flex items-center justify-between">
                  <span>Date of Birth:</span>
                  <span className="font-semibold text-slate-200">{patient.dateOfBirth}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>Gender:</span>
                  <span className="font-semibold text-slate-200">{patient.gender}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>Email:</span>
                  <span className="font-semibold text-slate-200 truncate max-w-[170px]" title={patient.contactEmail}>
                    {patient.contactEmail || 'N/A'}
                  </span>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-800/80 flex items-center justify-between text-[11px]">
                <span className="text-slate-400">Total Logs:</span>
                <span className="font-bold text-teal-400">{patient.totalRecords || 14} Daily Records</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Patient Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-2xl relative">
            <h2 className="text-lg font-bold text-white mb-1">Register Patient Profile</h2>
            <p className="text-xs text-slate-400 mb-5">
              Enter clinical profile details. Record will be stored in your tenant boundary.
            </p>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Full Legal Name</label>
                <input
                  type="text"
                  required
                  value={formData.fullName}
                  onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                  placeholder="e.g. John Doe"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Date of Birth</label>
                  <input
                    type="date"
                    required
                    value={formData.dateOfBirth}
                    onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                  />
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Gender</label>
                  <select
                    value={formData.gender}
                    onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                  >
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Contact Email</label>
                <input
                  type="email"
                  value={formData.contactEmail}
                  onChange={(e) => setFormData({ ...formData, contactEmail: e.target.value })}
                  placeholder="patient@example.com"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Contact Phone</label>
                <input
                  type="tel"
                  value={formData.contactPhone}
                  onChange={(e) => setFormData({ ...formData, contactPhone: e.target.value })}
                  placeholder="+91 9876543210"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white focus:outline-none focus:border-teal-500"
                />
              </div>

              <div className="flex items-center justify-end space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2.5 rounded-xl text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2.5 rounded-xl bg-teal-500 hover:bg-teal-400 text-slate-950 font-bold transition-all shadow-md shadow-teal-500/20"
                >
                  {submitting ? 'Registering...' : 'Save Patient Profile'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
