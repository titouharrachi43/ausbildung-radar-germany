import React, { useState } from 'react';
import { Search, Filter, ExternalLink, Calendar, MapPin, CheckCircle, Clock, AlertTriangle, Sparkles, Building2, BookOpen } from 'lucide-react';
import { Opportunity, OpportunityType, Sector } from '../types';

interface OpportunitiesViewProps {
  opportunities: Opportunity[];
}

export const OpportunitiesView: React.FC<OpportunitiesViewProps> = ({ opportunities }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedSector, setSelectedSector] = useState<Sector | 'ALL'>('ALL');
  const [selectedYear, setSelectedYear] = useState<number | 'ALL'>('ALL');
  const [selectedType, setSelectedType] = useState<OpportunityType | 'ALL'>('ALL');
  const [selectedOpportunity, setSelectedOpportunity] = useState<Opportunity | null>(null);

  const filteredOpportunities = opportunities.filter((opp) => {
    const matchesSearch =
      searchQuery.trim() === '' ||
      opp.titleGerman.toLowerCase().includes(searchQuery.toLowerCase()) ||
      opp.companyName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      opp.city.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesSector = selectedSector === 'ALL' || opp.sector === selectedSector;
    const matchesYear = selectedYear === 'ALL' || opp.targetYear === selectedYear;
    const matchesType = selectedType === 'ALL' || opp.opportunityType === selectedType;

    return matchesSearch && matchesSector && matchesYear && matchesType;
  });

  return (
    <div className="space-y-6">
      {/* Header and Filter Controls */}
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h2 className="text-xl font-bold text-white flex items-center gap-2">
              <span>Ausbildung Vacancies & Intelligence</span>
              <span className="text-xs px-2.5 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800">
                {filteredOpportunities.length} Available
              </span>
            </h2>
            <p className="text-xs text-gray-400">
              Verified German vocational training positions categorized by BBiG standards.
            </p>
          </div>

          {/* Search Input */}
          <div className="relative w-full md:w-80">
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search title, company, or city..."
              className="w-full pl-9 pr-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500 transition"
            />
          </div>
        </div>

        {/* Filter Pills */}
        <div className="flex flex-wrap items-center gap-2 pt-1 text-xs">
          <div className="flex items-center space-x-1 bg-gray-900 p-1 rounded-lg border border-gray-800">
            <button
              onClick={() => setSelectedSector('ALL')}
              className={`px-3 py-1 rounded-md font-medium transition ${
                selectedSector === 'ALL' ? 'bg-cyan-500/20 text-cyan-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              All Sectors
            </button>
            <button
              onClick={() => setSelectedSector('MARKETING')}
              className={`px-3 py-1 rounded-md font-medium transition ${
                selectedSector === 'MARKETING' ? 'bg-amber-500/20 text-amber-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              Marketing
            </button>
            <button
              onClick={() => setSelectedSector('EDUCATION')}
              className={`px-3 py-1 rounded-md font-medium transition ${
                selectedSector === 'EDUCATION' ? 'bg-emerald-500/20 text-emerald-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              Education & Kita
            </button>
          </div>

          <div className="flex items-center space-x-1 bg-gray-900 p-1 rounded-lg border border-gray-800">
            <button
              onClick={() => setSelectedYear('ALL')}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedYear === 'ALL' ? 'bg-cyan-500/20 text-cyan-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              All Years
            </button>
            <button
              onClick={() => setSelectedYear(2027)}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedYear === 2027 ? 'bg-cyan-500/20 text-cyan-300 font-bold' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              2027 ★
            </button>
            <button
              onClick={() => setSelectedYear(2028)}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedYear === 2028 ? 'bg-cyan-500/20 text-cyan-300 font-bold' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              2028 ★
            </button>
          </div>

          <div className="flex items-center space-x-1 bg-gray-900 p-1 rounded-lg border border-gray-800">
            <button
              onClick={() => setSelectedType('ALL')}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedType === 'ALL' ? 'bg-cyan-500/20 text-cyan-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              All Types
            </button>
            <button
              onClick={() => setSelectedType('DUAL_AUSBILDUNG')}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedType === 'DUAL_AUSBILDUNG' ? 'bg-cyan-500/20 text-cyan-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              Duale Ausbildung
            </button>
            <button
              onClick={() => setSelectedType('SCHULISCHE_AUSBILDUNG')}
              className={`px-2.5 py-1 rounded-md font-medium transition ${
                selectedType === 'SCHULISCHE_AUSBILDUNG' ? 'bg-cyan-500/20 text-cyan-300' : 'text-gray-400 hover:text-gray-200'
              }`}
            >
              Schulische Ausbildung
            </button>
          </div>
        </div>
      </div>

      {/* Opportunities Card Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredOpportunities.map((opp) => {
          const isMarketing = opp.sector === 'MARKETING';
          const isExpired = opp.status === 'EXPIRED';

          return (
            <div
              key={opp.id}
              className={`group rounded-xl bg-[#111827] border ${
                isExpired ? 'border-gray-800/60 opacity-70' : 'border-gray-800 hover:border-cyan-500/40'
              } p-5 space-y-4 transition flex flex-col justify-between`}
            >
              <div className="space-y-3">
                {/* Header row: Sector, Target Year, and Demo badge */}
                <div className="flex items-center justify-between gap-2 flex-wrap">
                  <div className="flex items-center gap-2">
                    <span
                      className={`text-[11px] font-bold px-2.5 py-0.5 rounded-md border ${
                        isMarketing
                          ? 'bg-amber-950/40 text-amber-300 border-amber-500/30'
                          : 'bg-emerald-950/40 text-emerald-300 border-emerald-500/30'
                      }`}
                    >
                      {opp.sector === 'MARKETING' ? 'Marketing & Media' : 'Education & Childcare'}
                    </span>
                    <span className="text-[11px] font-semibold px-2 py-0.5 rounded-md bg-cyan-950/60 text-cyan-300 border border-cyan-800/60">
                      Start: {opp.targetYear}
                    </span>
                  </div>

                  {opp.isDemoData ? (
                    <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20">
                      Demo Data
                    </span>
                  ) : (
                    <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-cyan-500/10 text-cyan-300 border border-cyan-500/20">
                      Live Verified
                    </span>
                  )}
                </div>

                {/* German Title */}
                <h3 className="text-base font-bold text-white group-hover:text-cyan-300 transition leading-snug">
                  {opp.titleGerman}
                </h3>

                {/* Company & Location */}
                <div className="flex items-center gap-3 text-xs text-gray-400 flex-wrap">
                  <span className="flex items-center gap-1 font-medium text-gray-300">
                    <Building2 className="w-3.5 h-3.5 text-gray-500" />
                    {opp.companyName}
                  </span>
                  <span className="flex items-center gap-1">
                    <MapPin className="w-3.5 h-3.5 text-gray-500" />
                    {opp.city}, {opp.bundesland}
                  </span>
                </div>

                {/* Description Snippet or Gemini Rating */}
                <p className="text-xs text-gray-300 line-clamp-2 leading-relaxed">
                  {opp.whyRatedBest || opp.descriptionSnippet}
                </p>

                {opp.stipendMonthly && (
                  <div className="text-xs text-emerald-400 font-medium">
                    Tarifvergütung: {opp.stipendMonthly}
                  </div>
                )}

                {/* Requirements / Tags */}
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {opp.requirements.slice(0, 2).map((req, i) => (
                    <span key={i} className="text-[10px] px-2 py-0.5 rounded bg-gray-900 text-gray-400 border border-gray-800">
                      {req}
                    </span>
                  ))}
                  {opp.requirements.length > 2 && (
                    <span className="text-[10px] px-2 py-0.5 rounded bg-gray-900 text-gray-400 border border-gray-800">
                      +{opp.requirements.length - 2} more
                    </span>
                  )}
                </div>
              </div>

              {/* Card Footer: Confidence Score & Action Buttons */}
              <div className="pt-3 border-t border-gray-800/80 flex items-center justify-between text-xs">
                <div className="flex items-center gap-1.5 text-cyan-400 font-semibold">
                  <Sparkles className="w-3.5 h-3.5" />
                  <span>{(opp.confidenceScore * 100).toFixed(0)}% Score</span>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setSelectedOpportunity(opp)}
                    className="px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-200 font-medium transition"
                  >
                    View Details
                  </button>
                  <a
                    href={opp.sourceUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="p-1.5 rounded-lg bg-cyan-950/60 hover:bg-cyan-900 text-cyan-300 border border-cyan-800/60 transition"
                    title="Open Source Career Page"
                  >
                    <ExternalLink className="w-3.5 h-3.5" />
                  </a>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Opportunity Detail Modal */}
      {selectedOpportunity && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
          <div className="w-full max-w-2xl rounded-2xl bg-[#0E1526] border border-cyan-500/30 p-6 space-y-5 shadow-2xl text-left max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-gray-800">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold px-2 py-0.5 rounded bg-cyan-500/10 text-cyan-300 border border-cyan-500/30">
                  {selectedOpportunity.opportunityType.replace('_', ' ')}
                </span>
                {selectedOpportunity.isDemoData && (
                  <span className="text-xs font-bold px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 border border-amber-500/30">
                    DEMO RECORD
                  </span>
                )}
              </div>
              <button
                onClick={() => setSelectedOpportunity(null)}
                className="text-gray-400 hover:text-white text-sm font-bold px-2 py-1"
              >
                ✕ Close
              </button>
            </div>

            <div className="space-y-2">
              <h2 className="text-lg font-bold text-white leading-tight">
                {selectedOpportunity.titleGerman}
              </h2>
              <div className="text-xs text-gray-400 flex items-center gap-4 flex-wrap">
                <span>Employer: <strong className="text-gray-200">{selectedOpportunity.companyName}</strong></span>
                <span>Location: <strong className="text-gray-200">{selectedOpportunity.city}, {selectedOpportunity.bundesland}</strong></span>
                <span>Ausbildungsjahr: <strong className="text-cyan-300">{selectedOpportunity.targetYear}</strong></span>
              </div>
            </div>

            {selectedOpportunity.whyRatedBest && (
              <div className="p-3.5 rounded-lg bg-cyan-950/40 border border-cyan-500/30 text-xs text-cyan-200">
                <span className="font-bold text-cyan-300 block mb-1">Gemini AI Evaluation:</span>
                {selectedOpportunity.whyRatedBest}
              </div>
            )}

            {selectedOpportunity.stipendMonthly && (
              <div className="text-xs text-emerald-400 font-semibold bg-emerald-950/30 border border-emerald-800/40 p-2.5 rounded-lg">
                Monatliche Ausbildungsvergütung: {selectedOpportunity.stipendMonthly}
              </div>
            )}

            <div className="space-y-1">
              <h4 className="text-xs font-bold uppercase tracking-wider text-gray-400">Description & Role</h4>
              <p className="text-xs text-gray-300 leading-relaxed bg-gray-900/60 p-3 rounded-lg border border-gray-800">
                {selectedOpportunity.descriptionSnippet}
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-2">
                <h4 className="text-xs font-bold uppercase tracking-wider text-gray-400">Requirements</h4>
                <ul className="text-xs text-gray-300 space-y-1.5 bg-gray-900/40 p-3 rounded-lg border border-gray-800">
                  {selectedOpportunity.requirements.map((r, i) => (
                    <li key={i} className="flex items-center gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-cyan-400" />
                      <span>{r}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="space-y-2">
                <h4 className="text-xs font-bold uppercase tracking-wider text-gray-400">Benefits & Offering</h4>
                <ul className="text-xs text-gray-300 space-y-1.5 bg-gray-900/40 p-3 rounded-lg border border-gray-800">
                  {selectedOpportunity.benefits.map((b, i) => (
                    <li key={i} className="flex items-center gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                      <span>{b}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            <div className="pt-3 border-t border-gray-800 flex items-center justify-between text-xs">
              <span className="text-gray-400 font-mono text-[11px]">
                Verified: {new Date(selectedOpportunity.verifiedAt).toLocaleDateString('de-DE')}
              </span>
              <a
                href={selectedOpportunity.sourceUrl}
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-semibold shadow-md transition"
              >
                <span>Open Public Career Page</span>
                <ExternalLink className="w-3.5 h-3.5" />
              </a>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
