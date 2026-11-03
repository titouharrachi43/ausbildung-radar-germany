import React, { useState } from 'react';
import { Globe, Search, Play, CheckCircle2, AlertTriangle, ArrowRight, Sparkles, Terminal, FileText } from 'lucide-react';
import { Company } from '../types';

interface InvestigateToolProps {
  initialCompany?: Company | null;
  onSendToGemini: (text: string, url: string, company: string) => void;
}

export const InvestigateTool: React.FC<InvestigateToolProps> = ({ initialCompany, onSendToGemini }) => {
  const [url, setUrl] = useState(initialCompany?.websiteUrl || 'https://nordmedia.de');
  const [companyName, setCompanyName] = useState(initialCompany?.name || 'NordMedia Digital & Communications GmbH');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleInvestigate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await fetch('/api/crawler/investigate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ websiteUrl: url, companyName }),
      });

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || 'Investigation failed');
      }
      setResult(data);
    } catch (err: any) {
      setError(err.message || 'Network error during investigation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <span>Company Website Crawler & Investigation Engine</span>
          <span className="text-xs px-2.5 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800">
            Node.js Cheerio Engine
          </span>
        </h2>
        <p className="text-xs text-gray-400">
          Discovers career sub-pages (/karriere, /ausbildung, /jobs), extracts structured content, and validates German apprenticeship text.
        </p>
      </div>

      {/* Investigation Form */}
      <form onSubmit={handleInvestigate} className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-semibold text-gray-300">Target Website URL</label>
            <div className="relative">
              <Globe className="w-4 h-4 text-gray-500 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="https://example-agency.de"
                required
                className="w-full pl-9 pr-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold text-gray-300">Company Name</label>
            <input
              type="text"
              value={companyName}
              onChange={(e) => setCompanyName(e.target.value)}
              placeholder="e.g. Media Agency GmbH"
              className="w-full px-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500"
            />
          </div>
        </div>

        <div className="flex items-center justify-between pt-2">
          <span className="text-xs text-gray-500">
            Searches German career keywords: karriere, ausbildung, jobs, stellenangebote, nachwuchs.
          </span>
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold shadow-md shadow-cyan-950 transition disabled:opacity-50"
          >
            {loading ? (
              <>
                <div className="w-3.5 h-3.5 border-2 border-white/20 border-t-white rounded-full animate-spin" />
                <span>Crawling Website...</span>
              </>
            ) : (
              <>
                <Play className="w-3.5 h-3.5" />
                <span>Run Investigation</span>
              </>
            )}
          </button>
        </div>
      </form>

      {/* Error Banner */}
      {error && (
        <div className="p-4 rounded-xl bg-rose-950/40 border border-rose-500/30 text-rose-300 text-xs space-y-2">
          <div className="flex items-center gap-2 font-bold">
            <AlertTriangle className="w-4 h-4 shrink-0" />
            <span>Investigation Encountered An Issue</span>
          </div>
          <p>{error}</p>
        </div>
      )}

      {/* Investigation Results */}
      {result && (
        <div className="space-y-4">
          <div className="rounded-xl bg-[#111827] border border-cyan-500/30 p-5 space-y-4">
            <div className="flex items-center justify-between flex-wrap gap-2">
              <h3 className="font-bold text-white text-sm flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                <span>Investigation Completed: {result.companyName}</span>
              </h3>
              <span className="text-[11px] font-mono text-gray-400">
                {new Date(result.investigatedAt).toLocaleTimeString()} CET
              </span>
            </div>

            {/* Keyword Hits Summary */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div className="p-3 rounded-lg bg-gray-900 border border-gray-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-gray-400">Ausbildung Hit</span>
                <p className={`text-sm font-bold ${result.keywordHits.hasAusbildung ? 'text-emerald-400' : 'text-gray-400'}`}>
                  {result.keywordHits.hasAusbildung ? 'DETECTED' : 'Not Found'}
                </p>
              </div>
              <div className="p-3 rounded-lg bg-gray-900 border border-gray-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-gray-400">Marketing Sector</span>
                <p className={`text-sm font-bold ${result.keywordHits.hasMarketing ? 'text-amber-400' : 'text-gray-400'}`}>
                  {result.keywordHits.hasMarketing ? 'DETECTED' : 'Not Found'}
                </p>
              </div>
              <div className="p-3 rounded-lg bg-gray-900 border border-gray-800 space-y-1">
                <span className="text-[10px] uppercase font-bold text-gray-400">Education Sector</span>
                <p className={`text-sm font-bold ${result.keywordHits.hasEducation ? 'text-emerald-400' : 'text-gray-400'}`}>
                  {result.keywordHits.hasEducation ? 'DETECTED' : 'Not Found'}
                </p>
              </div>
            </div>

            {/* Career links discovered */}
            <div className="space-y-2">
              <span className="text-xs font-semibold text-gray-300">
                Discovered Career Pages ({result.careerLinksFound.length})
              </span>
              <div className="flex flex-wrap gap-2">
                {result.careerLinksFound.map((link: any, i: number) => (
                  <a
                    key={i}
                    href={link.href}
                    target="_blank"
                    rel="noreferrer"
                    className="px-2.5 py-1 rounded bg-gray-900 hover:bg-gray-800 text-cyan-300 text-xs border border-gray-800 transition"
                  >
                    {link.text || link.href}
                  </a>
                ))}
              </div>
            </div>

            {/* Content snippet */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-gray-300 flex items-center gap-1.5">
                  <FileText className="w-3.5 h-3.5" />
                  <span>Scraped Text Sample ({result.rawTextLength} characters total)</span>
                </span>
                <button
                  onClick={() => onSendToGemini(result.contentSnippet, result.careerPageUrl, result.companyName)}
                  className="inline-flex items-center gap-1 text-xs font-bold text-cyan-300 hover:text-cyan-200 transition"
                >
                  <Sparkles className="w-3.5 h-3.5" />
                  <span>Send to Gemini AI for Extraction →</span>
                </button>
              </div>
              <pre className="p-3 rounded-lg bg-gray-950 text-gray-300 text-xs font-mono overflow-x-auto whitespace-pre-wrap max-h-48 border border-gray-800">
                {result.contentSnippet}
              </pre>
            </div>

            {/* Server crawler logs */}
            <div className="space-y-2">
              <span className="text-xs font-semibold text-gray-400 flex items-center gap-1.5">
                <Terminal className="w-3.5 h-3.5" />
                <span>Backend Crawler Execution Trace</span>
              </span>
              <div className="p-3 rounded-lg bg-gray-950/80 border border-gray-800 space-y-1 font-mono text-[11px] text-gray-400">
                {result.logs.map((log: string, i: number) => (
                  <div key={i} className="flex items-start gap-2">
                    <span className="text-cyan-500">›</span>
                    <span>{log}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
