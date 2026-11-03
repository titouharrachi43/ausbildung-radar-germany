import React, { useState, useEffect } from 'react';
import {
  Sparkles,
  Building2,
  MapPin,
  ExternalLink,
  Globe,
  Search,
  Radar,
  Award,
  Calendar,
  CheckCircle2,
  Euro,
  Filter,
  RefreshCw,
  PlusCircle,
  HelpCircle
} from 'lucide-react';
import { Company, GeminiBestOpportunity } from '../types';

interface CompaniesViewProps {
  companies: Company[];
  onInvestigateCompany: (company: Company) => void;
  onAddOpportunity?: (opp: any) => void;
}

const BUNDESLAENDER = [
  'ALL',
  'Baden-Württemberg',
  'Bayern',
  'Berlin',
  'Brandenburg',
  'Bremen',
  'Hamburg',
  'Hessen',
  'Mecklenburg-Vorpommern',
  'Niedersachsen',
  'Nordrhein-Westfalen',
  'Rheinland-Pfalz',
  'Saarland',
  'Sachsen',
  'Sachsen-Anhalt',
  'Schleswig-Holstein',
  'Thüringen',
];

export const CompaniesView: React.FC<CompaniesViewProps> = ({
  companies,
  onInvestigateCompany,
  onAddOpportunity,
}) => {
  const [selectedCategory, setSelectedCategory] = useState<'ALL' | 'MARKETING' | 'EDUCATION'>('ALL');
  const [selectedYear, setSelectedYear] = useState<'BOTH' | '2027' | '2028'>('BOTH');
  const [selectedBundesland, setSelectedBundesland] = useState('ALL');
  const [cityFilter, setCityFilter] = useState('');
  const [smallCityPriority, setSmallCityPriority] = useState(true);

  const [loading, setLoading] = useState(false);
  const [geminiResults, setGeminiResults] = useState<GeminiBestOpportunity[]>([]);
  const [sourceNotice, setSourceNotice] = useState<string | null>(null);
  const [savedIds, setSavedIds] = useState<Set<string>>(new Set());

  // Search filter for results
  const [searchQuery, setSearchQuery] = useState('');

  const fetchBestWithGemini = async () => {
    setLoading(true);
    setSourceNotice(null);
    try {
      const res = await fetch('/api/gemini/find-best', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          category: selectedCategory,
          targetYear: selectedYear,
          bundesland: selectedBundesland,
          city: cityFilter,
          cityTier: smallCityPriority ? 'SMALL_MEDIUM' : 'ALL',
        }),
      });

      const data = await res.json();
      if (data.opportunities) {
        setGeminiResults(data.opportunities);
      }
      if (data.geminiNotice) {
        setSourceNotice(data.geminiNotice);
      }
    } catch (err: any) {
      console.error('Failed to find best with Gemini:', err);
    } finally {
      setLoading(false);
    }
  };

  // Initial load
  useEffect(() => {
    fetchBestWithGemini();
  }, [selectedCategory, selectedYear, selectedBundesland, smallCityPriority]);

  const filtered = geminiResults.filter((c) => {
    return (
      searchQuery.trim() === '' ||
      c.companyName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.city.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.titleGerman.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.bundesland.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const handleSaveToPipeline = (item: GeminiBestOpportunity) => {
    setSavedIds((prev) => new Set(prev).add(item.id));
    if (onAddOpportunity) {
      onAddOpportunity({
        id: item.id,
        titleGerman: item.titleGerman,
        companyName: item.companyName,
        companyId: `comp-${item.id}`,
        city: item.city,
        bundesland: item.bundesland,
        opportunityType: item.opportunityType,
        sector: item.category,
        targetYear: item.targetYear,
        sourceUrl: item.careerUrl || item.websiteUrl,
        verifiedAt: new Date().toISOString(),
        status: 'VERIFIED_ACTIVE',
        confidenceScore: item.qualityScore / 100,
        descriptionSnippet: item.whyRatedBest,
        requirements: item.requirements,
        benefits: item.benefits,
        stipendMonthly: item.stipendMonthly,
        whyRatedBest: item.whyRatedBest,
        isDemoData: false,
      });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header and Hero Badge */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <span>Find Best Ausbildungen with Gemini AI</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800">
              Gemini Intelligent Discovery
            </span>
          </h2>
          <p className="text-xs text-gray-400">
            Powered exclusively by Gemini AI to identify, evaluate, and rank Germany's highest-quality training employers and pedagogical institutions.
          </p>
        </div>

        <button
          onClick={fetchBestWithGemini}
          disabled={loading}
          className="inline-flex items-center space-x-2 px-4 py-2 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold shadow-md shadow-cyan-950 transition disabled:opacity-50 cursor-pointer"
        >
          <Sparkles className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>{loading ? 'Consulting Gemini...' : 'Discover Best Opportunities'}</span>
        </button>
      </div>

      {/* Control Panel: Sector, Bundesland, Target Year, Small-City Prioritization */}
      <div className="rounded-xl bg-[#111827] border border-gray-800 p-4 space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {/* Sector selection */}
          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider">Sector</label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value as any)}
              className="w-full px-3 py-2 rounded-lg bg-gray-900 border border-gray-800 text-xs text-gray-200 focus:outline-none focus:border-cyan-500"
            >
              <option value="ALL">All Sectors (Marketing & Education)</option>
              <option value="MARKETING">Marketing & Digital Communication</option>
              <option value="EDUCATION">Education & Childcare (Kita/PiA)</option>
            </select>
          </div>

          {/* Target Year */}
          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider">Ausbildung Year</label>
            <select
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value as any)}
              className="w-full px-3 py-2 rounded-lg bg-gray-900 border border-gray-800 text-xs text-gray-200 focus:outline-none focus:border-cyan-500"
            >
              <option value="BOTH">Both 2027 & 2028</option>
              <option value="2027">Start 2027 (Recommended)</option>
              <option value="2028">Start 2028 (Long-term)</option>
            </select>
          </div>

          {/* Bundesland */}
          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider">Bundesland (Region)</label>
            <select
              value={selectedBundesland}
              onChange={(e) => setSelectedBundesland(e.target.value)}
              className="w-full px-3 py-2 rounded-lg bg-gray-900 border border-gray-800 text-xs text-gray-200 focus:outline-none focus:border-cyan-500"
            >
              {BUNDESLAENDER.map((b) => (
                <option key={b} value={b}>
                  {b === 'ALL' ? 'All 16 Bundesländer' : b}
                </option>
              ))}
            </select>
          </div>

          {/* City search input */}
          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider">City Search</label>
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="e.g. Hildesheim, Göttingen, Bamberg..."
              className="w-full px-3 py-2 rounded-lg bg-gray-900 border border-gray-800 text-xs text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500"
            />
          </div>
        </div>

        {/* Small city toggle & status banner */}
        <div className="pt-2 border-t border-gray-800/80 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
          <label className="flex items-center space-x-2 cursor-pointer text-gray-300">
            <input
              type="checkbox"
              checked={smallCityPriority}
              onChange={(e) => setSmallCityPriority(e.target.checked)}
              className="rounded bg-gray-900 border-gray-700 text-cyan-500 focus:ring-0 focus:ring-offset-0"
            />
            <span className="font-medium">
              Prioritize small & medium cities (&lt;100,000 residents) where placement rates are highest
            </span>
          </label>

          <span className="text-gray-400 text-[11px]">
            Showing <strong className="text-cyan-300">{filtered.length}</strong> evaluated opportunities
          </span>
        </div>
      </div>

      {/* Notice / Diagnostic banner if any */}
      {sourceNotice && (
        <div className="p-3 bg-cyan-950/40 border border-cyan-500/20 rounded-xl text-xs text-cyan-200/90 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-cyan-400 shrink-0" />
            <span>{sourceNotice}</span>
          </div>
        </div>
      )}

      {/* Grid of Gemini Best Opportunities */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filtered.map((item) => {
          const isMarketing = item.category === 'MARKETING';
          const isSaved = savedIds.has(item.id);

          return (
            <div
              key={item.id}
              className="rounded-xl bg-[#111827] border border-gray-800 hover:border-cyan-500/40 p-5 space-y-4 transition flex flex-col justify-between"
            >
              <div className="space-y-3">
                {/* Badges row */}
                <div className="flex items-center justify-between gap-2 flex-wrap">
                  <div className="flex items-center gap-2">
                    <span
                      className={`text-[11px] font-bold px-2 py-0.5 rounded border ${
                        isMarketing
                          ? 'bg-amber-950/40 text-amber-300 border-amber-500/30'
                          : 'bg-emerald-950/40 text-emerald-300 border-emerald-500/30'
                      }`}
                    >
                      {isMarketing ? 'Marketing & E-Commerce' : 'Education & Childcare'}
                    </span>
                    <span className="text-[11px] font-semibold px-2 py-0.5 rounded bg-cyan-950/60 text-cyan-300 border border-cyan-800">
                      Start {item.targetYear}
                    </span>
                  </div>

                  <div className="flex items-center gap-1.5 px-2 py-0.5 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-300 text-[11px] font-bold">
                    <Award className="w-3.5 h-3.5 text-cyan-400" />
                    <span>Gemini Score: {item.qualityScore}/100</span>
                  </div>
                </div>

                {/* Opportunity Title in German */}
                <div>
                  <h3 className="text-base font-bold text-white leading-snug">
                    {item.titleGerman}
                  </h3>
                  <div className="text-xs text-gray-300 font-semibold mt-1 flex items-center gap-1.5">
                    <Building2 className="w-3.5 h-3.5 text-cyan-400 shrink-0" />
                    <span>{item.companyName}</span>
                  </div>
                </div>

                {/* Location and Stipend */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                  <div className="flex items-center gap-1.5 text-gray-400">
                    <MapPin className="w-3.5 h-3.5 text-gray-500 shrink-0" />
                    <span>{item.city}, {item.bundesland} <span className="text-[10px] text-gray-500">({item.cityTier})</span></span>
                  </div>
                  <div className="flex items-center gap-1.5 text-emerald-400 font-medium">
                    <Euro className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                    <span className="truncate">{item.stipendMonthly}</span>
                  </div>
                </div>

                {/* Gemini Quality Evaluation */}
                <div className="p-3 rounded-lg bg-gray-900/70 border border-gray-800/80 space-y-1">
                  <div className="text-[10px] font-bold uppercase tracking-wider text-cyan-400 flex items-center gap-1">
                    <Sparkles className="w-3 h-3" />
                    <span>Why Gemini Ranks This Among The Best</span>
                  </div>
                  <p className="text-xs text-gray-300 leading-relaxed">
                    {item.whyRatedBest}
                  </p>
                </div>

                {/* Benefits / Requirements pills */}
                <div className="space-y-1.5 text-xs">
                  <div className="flex flex-wrap gap-1">
                    {item.benefits.map((b, i) => (
                      <span key={i} className="text-[10px] px-2 py-0.5 rounded bg-emerald-950/40 text-emerald-300 border border-emerald-800/50">
                        ✓ {b}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              {/* Action buttons */}
              <div className="pt-3 border-t border-gray-800/80 flex items-center justify-between gap-2 flex-wrap text-xs">
                <div className="flex items-center gap-2">
                  <a
                    href={item.careerUrl || item.websiteUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-xs text-gray-300 transition"
                  >
                    <ExternalLink className="w-3 h-3" />
                    <span>Career Page</span>
                  </a>

                  <button
                    onClick={() =>
                      onInvestigateCompany({
                        id: `comp-${item.id}`,
                        name: item.companyName,
                        city: item.city,
                        bundesland: item.bundesland,
                        category: item.category,
                        websiteUrl: item.websiteUrl,
                        careerUrl: item.careerUrl,
                        discoveredAt: new Date().toISOString(),
                        verifiedOpportunitiesCount: 1,
                        isDemoData: false,
                        whyRatedBest: item.whyRatedBest,
                        stipendMonthly: item.stipendMonthly,
                      })
                    }
                    className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-cyan-300 text-xs font-semibold transition"
                  >
                    <Radar className="w-3 h-3" />
                    <span>Crawler Deep-Dive</span>
                  </button>
                </div>

                <button
                  onClick={() => handleSaveToPipeline(item)}
                  disabled={isSaved}
                  className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                    isSaved
                      ? 'bg-emerald-950 text-emerald-400 border border-emerald-800 cursor-default'
                      : 'bg-cyan-600 hover:bg-cyan-500 text-white shadow-sm cursor-pointer'
                  }`}
                >
                  {isSaved ? (
                    <>
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      <span>Saved to Pipeline</span>
                    </>
                  ) : (
                    <>
                      <PlusCircle className="w-3.5 h-3.5" />
                      <span>Save Opportunity</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
