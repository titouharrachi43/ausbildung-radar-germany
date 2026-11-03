import React, { useState } from 'react';
import { Building2, MapPin, ExternalLink, Globe, Search, Radar, ArrowRight } from 'lucide-react';
import { Company } from '../types';

interface CompaniesViewProps {
  companies: Company[];
  onInvestigateCompany: (company: Company) => void;
}

export const CompaniesView: React.FC<CompaniesViewProps> = ({ companies, onInvestigateCompany }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const filtered = companies.filter((c) => {
    return (
      searchQuery.trim() === '' ||
      c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.city.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.category.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <span>Discovered Businesses & Potential Employers</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-amber-950 text-amber-300 border border-amber-800">
              {filtered.length} Discovered
            </span>
          </h2>
          <p className="text-xs text-gray-400">
            German companies and educational institutions discovered in target small and medium cities.
          </p>
        </div>

        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search company, city, or sector..."
            className="w-full pl-9 pr-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500 transition"
          />
        </div>
      </div>

      <div className="p-3 bg-gray-900/60 border border-gray-800 rounded-xl text-xs text-gray-400 flex items-center justify-between">
        <span>Status: <strong>Potential Employers (Demo Seed Registry • Places API: NOT CONFIGURED)</strong></span>
        <span className="text-gray-500">Google Places API storage rules respected</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filtered.map((company) => (
          <div
            key={company.id}
            className="rounded-xl bg-[#111827] border border-gray-800 hover:border-amber-500/30 p-5 space-y-4 transition flex flex-col justify-between"
          >
            <div className="space-y-3">
              <div className="flex items-center justify-between gap-2">
                <span className="text-[11px] font-bold px-2 py-0.5 rounded bg-gray-800 text-gray-300 border border-gray-700">
                  {company.category}
                </span>
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20">
                  Potential Employer
                </span>
              </div>

              <h3 className="text-base font-bold text-white leading-snug">
                {company.name}
              </h3>

              <div className="flex items-center gap-2 text-xs text-gray-400">
                <MapPin className="w-3.5 h-3.5 text-gray-500 shrink-0" />
                <span>{company.city}, {company.bundesland}</span>
              </div>

              <p className="text-[11px] font-mono text-gray-500 truncate">
                Seed Place ID: {company.placeId}
              </p>
            </div>

            <div className="pt-3 border-t border-gray-800/80 flex items-center justify-between gap-2 flex-wrap">
              <div className="flex items-center gap-2">
                <a
                  href={company.websiteUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-xs text-gray-300 transition"
                >
                  <Globe className="w-3 h-3" />
                  <span>Website</span>
                </a>
                <a
                  href={company.careerUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-xs text-gray-300 transition"
                >
                  <ExternalLink className="w-3 h-3" />
                  <span>Career Page</span>
                </a>
              </div>

              <button
                onClick={() => onInvestigateCompany(company)}
                className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-sm transition"
              >
                <Radar className="w-3.5 h-3.5" />
                <span>Investigate</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
