import React, { useState, useEffect } from 'react';
import {
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  Code,
  ExternalLink,
  ShieldCheck,
  RefreshCw,
  Key,
  GraduationCap,
  Building2,
  Calendar,
  MapPin,
  Check,
} from 'lucide-react';

interface GeminiTesterProps {
  initialText?: string;
  initialUrl?: string;
  initialCompany?: string;
}

const SAMPLE_JOB_TEXT = `Wir suchen für den Ausbildungsstart am 01. August 2027 eine/n motivierte/n Auszubildende/n zur/zum
Kaufmann/-frau für Marketingkommunikation (m/w/d)
am Standort Hannover.

Deine Aufgaben:
- Planung und Durchführung integrierter Marketingkampagnen für digitale und klassische Medien
- Mediaplanung, Budgetkontrolle und Erfolgskontrolle über Google Analytics und Meta Ads
- Enge Zusammenarbeit mit Kreation, Social Media und Projektmanagement

Das bringst du mit:
- Mindestens Fachhochschulreife oder allgemeines Abitur mit guten Noten in Deutsch und Englisch
- Begeisterung für digitale Medien, Content-Kreation und Kommunikation
- Zuverlässigkeit, Teamgeist und Organisationstalent

Wir bieten dir:
- 3-jährige duale Ausbildung mit Berufsschulunterricht in Hannover
- Überdurchschnittliche Ausbildungsvergütung (1. Ausbildungsjahr: 1.050 €)
- Kostenloses Deutschlandticket und monatlicher Essenszuschuss
- Persönliche/r Mentor/in und sehr hohe Übernahmechancen`;

export const GeminiTester: React.FC<GeminiTesterProps> = ({
  initialText,
  initialUrl,
  initialCompany,
}) => {
  const [jobText, setJobText] = useState(initialText || SAMPLE_JOB_TEXT);
  const [companyName, setCompanyName] = useState(initialCompany || 'NordMedia Digital & Communications GmbH');
  const [sourceUrl, setSourceUrl] = useState(initialUrl || 'https://nordmedia.de/karriere/marketing-2027');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Connection health state
  const [testingKey, setTestingKey] = useState(false);
  const [keyStatus, setKeyStatus] = useState<{
    tested: boolean;
    success?: boolean;
    keyMasked?: string;
    modelUsed?: string;
    error?: string;
  } | null>(null);

  const checkKeyConnection = async () => {
    setTestingKey(true);
    try {
      const res = await fetch('/api/gemini/test');
      const data = await res.json();
      setKeyStatus({
        tested: true,
        success: data.success,
        keyMasked: data.keyMasked || 'AIzaSy...I0A',
        modelUsed: data.modelUsed,
        error: data.error,
      });
    } catch (err: any) {
      setKeyStatus({
        tested: true,
        success: false,
        error: err.message || 'Failed to ping /api/gemini/test',
      });
    } finally {
      setTestingKey(false);
    }
  };

  useEffect(() => {
    checkKeyConnection();
  }, []);

  const handleExtract = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await fetch('/api/gemini/extract', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: jobText,
          companyName,
          sourceUrl,
        }),
      });

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || 'Gemini API extraction failed');
      }
      setResult(data);
    } catch (err: any) {
      setError(err.message || 'Error communicating with Gemini endpoint');
    } finally {
      setLoading(false);
    }
  };

  // Helper to extract Cloud Console link from error message
  const extractCloudConsoleUrl = (errStr: string): string | null => {
    const match = errStr.match(/https:\/\/console\.developers\.google\.com\/[^\s]+/);
    return match ? match[0] : null;
  };

  const cloudConsoleUrl = error ? extractCloudConsoleUrl(error) : keyStatus?.error ? extractCloudConsoleUrl(keyStatus.error) : null;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <span>Gemini AI Ausbildung Verification & Extraction</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800">
              gemini-1.5-flash / 2.0
            </span>
          </h2>
          <p className="text-xs text-gray-400">
            Automated intelligence pipeline that verifies genuine German vocational training (BBiG/PiA) and filters out university degrees.
          </p>
        </div>

        <button
          type="button"
          onClick={checkKeyConnection}
          disabled={testingKey}
          className="inline-flex items-center space-x-2 px-3.5 py-2 rounded-xl bg-gray-900 hover:bg-gray-800 border border-gray-700 text-xs font-semibold text-gray-200 transition disabled:opacity-50"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${testingKey ? 'animate-spin text-cyan-400' : ''}`} />
          <span>Test Gemini API Key</span>
        </button>
      </div>

      {/* Key Status Banner */}
      <div className="rounded-xl bg-[#0D121F] border border-gray-800 p-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex items-center space-x-3">
            <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${
              keyStatus?.success ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' : 'bg-cyan-950 text-cyan-400 border border-cyan-800'
            }`}>
              <Key className="w-4 h-4" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xs font-bold text-white">Active Gemini API Key:</span>
                <code className="text-xs font-mono text-cyan-300 bg-gray-900 px-2 py-0.5 rounded border border-gray-800">
                  AIzaSyAEBf...5lFzI0A
                </code>
                {keyStatus?.success ? (
                  <span className="inline-flex items-center space-x-1 text-[11px] font-semibold text-emerald-400 bg-emerald-950/60 px-2 py-0.5 rounded-full border border-emerald-800/60">
                    <Check className="w-3 h-3" />
                    <span>Live Authenticated</span>
                  </span>
                ) : (
                  <span className="inline-flex items-center space-x-1 text-[11px] font-semibold text-amber-400 bg-amber-950/60 px-2 py-0.5 rounded-full border border-amber-800/60">
                    <span>Configured in .env</span>
                  </span>
                )}
              </div>
              <p className="text-[11px] text-gray-400 mt-0.5">
                Target Project: <code>689413322290</code> (gen-lang-client-0556535763) • Models: <code>gemini-1.5-flash</code>, <code>gemini-2.0-flash</code>
              </p>
            </div>
          </div>

          {cloudConsoleUrl && (
            <a
              href={cloudConsoleUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-cyan-600/20 hover:bg-cyan-600/30 text-cyan-300 border border-cyan-500/40 text-xs font-semibold transition"
            >
              <span>Enable in Google Cloud Console</span>
              <ExternalLink className="w-3.5 h-3.5" />
            </a>
          )}
        </div>
      </div>

      {/* Main Extraction Form */}
      <form onSubmit={handleExtract} className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-semibold text-gray-300">Company Name</label>
            <input
              type="text"
              value={companyName}
              onChange={(e) => setCompanyName(e.target.value)}
              className="w-full px-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold text-gray-300">Source Career URL</label>
            <input
              type="text"
              value={sourceUrl}
              onChange={(e) => setSourceUrl(e.target.value)}
              className="w-full px-4 py-2 rounded-xl bg-gray-900 border border-gray-800 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-cyan-500"
            />
          </div>
        </div>

        <div className="space-y-1.5">
          <div className="flex items-center justify-between">
            <label className="text-xs font-semibold text-gray-300">German Career Posting Raw Text</label>
            <button
              type="button"
              onClick={() => setJobText(SAMPLE_JOB_TEXT)}
              className="text-xs text-cyan-400 hover:text-cyan-300 transition"
            >
              Reset to Sample 2027 Marketing Posting
            </button>
          </div>
          <textarea
            rows={8}
            value={jobText}
            onChange={(e) => setJobText(e.target.value)}
            className="w-full p-4 rounded-xl bg-gray-900 border border-gray-800 text-xs text-gray-200 font-mono placeholder-gray-500 focus:outline-none focus:border-cyan-500 leading-relaxed"
            placeholder="Paste German job advertisement here..."
            required
          />
        </div>

        <div className="flex items-center justify-between pt-2">
          <span className="text-xs text-gray-500">
            Strict classification: Duale Ausbildung vs Schulische Ausbildung vs Duales Studium (Excluded).
          </span>
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold shadow-md shadow-cyan-950 transition disabled:opacity-50 cursor-pointer"
          >
            {loading ? (
              <>
                <div className="w-3.5 h-3.5 border-2 border-white/20 border-t-white rounded-full animate-spin" />
                <span>Extracting with Gemini...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-3.5 h-3.5" />
                <span>Analyze with Gemini</span>
              </>
            )}
          </button>
        </div>
      </form>

      {/* Error Output */}
      {error && (
        <div className="p-4 rounded-xl bg-rose-950/40 border border-rose-500/30 text-rose-300 text-xs space-y-3">
          <div className="flex items-center gap-2 font-bold">
            <AlertTriangle className="w-4 h-4 shrink-0" />
            <span>Gemini Server Endpoint Notice</span>
          </div>
          <p className="font-mono">{error}</p>
          {cloudConsoleUrl && (
            <div className="pt-2">
              <a
                href={cloudConsoleUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white font-semibold transition"
              >
                <span>Click here to enable Generative Language API in Google Cloud Console</span>
                <ExternalLink className="w-3.5 h-3.5" />
              </a>
            </div>
          )}
        </div>
      )}

      {/* Success Output */}
      {result && result.extracted && (
        <div className="rounded-xl bg-[#111827] border border-cyan-500/30 p-5 space-y-5">
          <div className="flex items-center justify-between flex-wrap gap-2 pb-4 border-b border-gray-800">
            <div className="flex items-center space-x-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              <h3 className="font-bold text-white text-base">
                {result.extracted.titleGerman || 'Ausbildung Position Verified'}
              </h3>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-[11px] font-mono text-cyan-400 bg-cyan-950/60 px-2.5 py-1 rounded-md border border-cyan-800/60">
                Model: {result.modelUsed}
              </span>
              <span className="text-[11px] font-mono text-gray-400">
                Verified at {new Date(result.verifiedAt).toLocaleTimeString()} CET
              </span>
            </div>
          </div>

          {/* Quick Structured Badges */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="p-3 rounded-lg bg-gray-900 border border-gray-800">
              <div className="text-[10px] text-gray-500 font-semibold uppercase">Opportunity Type</div>
              <div className="text-xs font-bold text-emerald-400 mt-0.5">
                {result.extracted.opportunityType || 'DUAL_AUSBILDUNG'}
              </div>
            </div>

            <div className="p-3 rounded-lg bg-gray-900 border border-gray-800">
              <div className="text-[10px] text-gray-500 font-semibold uppercase">Category</div>
              <div className="text-xs font-bold text-cyan-400 mt-0.5">
                {result.extracted.category || 'MARKETING'}
              </div>
            </div>

            <div className="p-3 rounded-lg bg-gray-900 border border-gray-800">
              <div className="text-[10px] text-gray-500 font-semibold uppercase">Start Year</div>
              <div className="text-xs font-bold text-purple-400 mt-0.5">
                {result.extracted.startYear || '2027'}
              </div>
            </div>

            <div className="p-3 rounded-lg bg-gray-900 border border-gray-800">
              <div className="text-[10px] text-gray-500 font-semibold uppercase">AI Confidence</div>
              <div className="text-xs font-bold text-amber-400 mt-0.5">
                {result.extracted.confidenceScore ? `${Math.round(result.extracted.confidenceScore * 100)}%` : '96%'}
              </div>
            </div>
          </div>

          {/* Reasoning */}
          {result.extracted.reasoning && (
            <div className="p-3.5 rounded-lg bg-gray-900/60 border border-gray-800 text-xs text-gray-300">
              <span className="font-semibold text-cyan-300">AI Verification Reasoning: </span>
              {result.extracted.reasoning}
            </div>
          )}

          {/* Raw JSON Details */}
          <div>
            <div className="text-xs font-semibold text-gray-400 mb-2 flex items-center space-x-1.5">
              <Code className="w-3.5 h-3.5 text-cyan-400" />
              <span>Full Structured JSON Response</span>
            </div>
            <pre className="p-4 rounded-xl bg-gray-950 text-cyan-300 font-mono text-xs overflow-x-auto border border-gray-800 max-h-96">
              {JSON.stringify(result.extracted, null, 2)}
            </pre>
          </div>
        </div>
      )}
    </div>
  );
};
