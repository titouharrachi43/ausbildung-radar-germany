import React from 'react';
import { Target, Building2, Calendar, Radio, CheckCircle2, AlertCircle, ArrowUpRight, Sparkles, Globe, Cpu, Award } from 'lucide-react';
import { Opportunity, Company, SystemHealth } from '../types';

interface StatsOverviewProps {
  opportunities: Opportunity[];
  companies: Company[];
  health: SystemHealth | null;
  onNavigate: (tab: string) => void;
}

export const StatsOverview: React.FC<StatsOverviewProps> = ({
  opportunities,
  companies,
  health,
  onNavigate,
}) => {
  const activeOpportunities = opportunities.filter((o) => o.status === 'VERIFIED_ACTIVE');
  const demoActive = activeOpportunities.filter((o) => o.isDemoData).length;
  const liveActive = activeOpportunities.filter((o) => !o.isDemoData).length;

  const marketingCount = opportunities.filter((o) => o.sector === 'MARKETING').length;
  const educationCount = opportunities.filter((o) => o.sector === 'EDUCATION').length;

  const geminiStatus = health?.integrations.geminiApi.configured ? 'Active' : 'NOT CONFIGURED';

  return (
    <div className="space-y-6">
      {/* Hero Banner with Radar Status */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-[#0E1526] via-[#111C38] to-[#0E1526] border border-cyan-500/20 p-6 sm:p-8">
        <div className="absolute top-0 right-0 w-96 h-96 bg-cyan-500/5 rounded-full blur-3xl pointer-events-none" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-3 max-w-2xl">
            <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-300 text-xs font-semibold">
              <span className="w-2 h-2 rounded-full bg-cyan-400 animate-ping" />
              <span>Gemini AI Intelligence Radar</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              Ausbildung Research Radar Germany
            </h1>
            <p className="text-sm text-gray-300 leading-relaxed">
              Automated intelligence assistant utilizing Gemini AI to discover, evaluate, and rank the best genuine vocational training opportunities for 2027 and 2028 across all 16 German Bundesländer.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3">
            <button
              onClick={() => onNavigate('companies')}
              className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold shadow-lg shadow-cyan-950 transition cursor-pointer"
            >
              <Sparkles className="w-4 h-4" />
              <span>Find Best with Gemini</span>
            </button>
            <button
              onClick={() => onNavigate('investigate')}
              className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-gray-200 border border-gray-700 text-xs font-semibold transition cursor-pointer"
            >
              <Globe className="w-4 h-4" />
              <span>Investigate Career URL</span>
            </button>
          </div>
        </div>
      </div>

      {/* Primary Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Metric 1: Verified Opportunities */}
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2 hover:border-cyan-500/30 transition">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Active Pipeline</span>
            <Target className="w-4 h-4 text-cyan-400" />
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-white">{activeOpportunities.length}</span>
            <span className="text-xs text-cyan-400 font-medium">Positions</span>
          </div>
          <div className="pt-2 border-t border-gray-800/80 flex items-center justify-between text-[11px]">
            <span className="text-amber-400/90 font-medium">Seed: {demoActive}</span>
            <span className="text-gray-500">|</span>
            <span className="text-cyan-400 font-medium">Live/Added: {liveActive}</span>
          </div>
        </div>

        {/* Metric 2: Gemini Discovery Engine */}
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2 hover:border-cyan-500/30 transition">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Discovery Engine</span>
            <Award className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-white">Gemini AI</span>
            <span className="text-xs text-emerald-400 font-semibold">{geminiStatus}</span>
          </div>
          <div className="pt-2 border-t border-gray-800/80 flex items-center justify-between text-[11px] text-gray-400">
            <span>Method:</span>
            <span className="text-cyan-400 font-medium">Direct Best-in-Class Ranking</span>
          </div>
        </div>

        {/* Metric 3: Target Sectors */}
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2 hover:border-cyan-500/30 transition">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Target Sectors</span>
            <Sparkles className="w-4 h-4 text-amber-400" />
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl font-bold text-white">2 Focus Sectors</span>
          </div>
          <div className="pt-2 border-t border-gray-800/80 flex items-center justify-between text-[11px]">
            <span className="text-amber-400 font-medium">Marketing ({marketingCount})</span>
            <span className="text-gray-500">•</span>
            <span className="text-emerald-400 font-medium">Education ({educationCount})</span>
          </div>
        </div>

        {/* Metric 4: 5-Hour Automation */}
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2 hover:border-cyan-500/30 transition">
          <div className="flex items-center justify-between text-gray-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Cloud Automation</span>
            <Radio className="w-4 h-4 text-purple-400" />
          </div>
          <div className="flex items-baseline space-x-2">
            <span className="text-sm font-bold text-purple-400">Scheduled Endpoint</span>
          </div>
          <div className="pt-2 border-t border-gray-800/80 flex items-center justify-between text-[11px] text-gray-400">
            <span>Cadence: ~5h</span>
            <span className="text-purple-400 font-mono text-[10px]">/api/scheduler/run</span>
          </div>
        </div>
      </div>

      {/* Target Criteria & Geography Information Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-3">
          <div className="flex items-center space-x-2 text-cyan-400">
            <Calendar className="w-4 h-4" />
            <h3 className="font-bold text-sm text-white">Target Criteria & BBiG Standards</h3>
          </div>
          <ul className="text-xs text-gray-300 space-y-2 leading-relaxed">
            <li className="flex items-start gap-2">
              <span className="text-cyan-400 font-bold">•</span>
              <span><strong>Target Start Years:</strong> Prioritizing 2027 and 2028 openings for long-term placement strategy.</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="text-cyan-400 font-bold">•</span>
              <span><strong>Marketing Communication & Media:</strong> Kaufmann/-frau für Marketingkommunikation, E-Commerce, Mediengestalter/in Digital und Print.</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="text-cyan-400 font-bold">•</span>
              <span><strong>Education & Childcare:</strong> Staatlich anerkannte/r Erzieher/in, Sozialpädagogische Assistenz (PiA/Vollzeit).</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="text-emerald-400 font-bold">✓</span>
              <span className="text-emerald-300/90">Pure Gemini evaluation: Automatically analyzes mentorship, training stipend, and BBiG standards.</span>
            </li>
          </ul>
        </div>

        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-3">
          <div className="flex items-center space-x-2 text-emerald-400">
            <Building2 className="w-4 h-4" />
            <h3 className="font-bold text-sm text-white">Target Regions & City Prioritization</h3>
          </div>
          <p className="text-xs text-gray-300 leading-relaxed">
            The radar searches all 16 German Bundesländer with strict prioritization of <strong>small and medium-sized cities</strong> (e.g. Hildesheim, Göttingen, Celle, Hameln, Bamberg, Passau, Flensburg, Trier, Fulda, Konstanz, Potsdam, Weimar) where competition is lower and genuine vocational training opportunities are abundant.
          </p>
          <div className="flex flex-wrap gap-1.5 pt-2">
            {['Niedersachsen', 'Bayern', 'Baden-Württemberg', 'NRW', 'Hessen', 'Schleswig-Holstein', 'Sachsen'].map((state) => (
              <span key={state} className="px-2 py-0.5 rounded-md bg-gray-800 text-[11px] text-gray-300 border border-gray-700">
                {state}
              </span>
            ))}
            <span className="px-2 py-0.5 rounded-md bg-cyan-950/60 text-[11px] text-cyan-300 border border-cyan-800">
              + 9 more Bundesländer
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
