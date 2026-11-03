import React, { useState } from 'react';
import { Sparkles, Play, CheckCircle2, AlertTriangle, Code, ArrowRight, BookOpen } from 'lucide-react';

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

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <span>Gemini AI Ausbildung Verification & Extraction</span>
          <span className="text-xs px-2.5 py-0.5 rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800">
            Gemini 1.5 Flash
          </span>
        </h2>
        <p className="text-xs text-gray-400">
          Server-side AI pipeline that verifies genuine German vocational training (BBiG/PiA) and excludes university degrees.
        </p>
      </div>

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
            className="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-bold shadow-md shadow-cyan-950 transition disabled:opacity-50"
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
        <div className="p-4 rounded-xl bg-rose-950/40 border border-rose-500/30 text-rose-300 text-xs space-y-2">
          <div className="flex items-center gap-2 font-bold">
            <AlertTriangle className="w-4 h-4 shrink-0" />
            <span>Gemini Server Endpoint Notice</span>
          </div>
          <p className="font-mono">{error}</p>
          <p className="text-gray-400">
            Tip: If the API key is not yet configured or is blocked in Google Cloud Console, enable the <strong>Generative Language API</strong> in project <code>gen-lang-client-0556535763</code> and provide <code>GEMINI_API_KEY</code>.
          </p>
        </div>
      )}

      {/* Success Output */}
      {result && (
        <div className="rounded-xl bg-[#111827] border border-cyan-500/30 p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-2">
            <h3 className="font-bold text-white text-sm flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              <span>Extraction Result ({result.modelUsed})</span>
            </h3>
            <span className="text-[11px] font-mono text-cyan-400">
              Verified at {new Date(result.verifiedAt).toLocaleTimeString()} CET
            </span>
          </div>

          <pre className="p-4 rounded-xl bg-gray-950 text-cyan-300 font-mono text-xs overflow-x-auto border border-gray-800">
            {JSON.stringify(result.extracted, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};
