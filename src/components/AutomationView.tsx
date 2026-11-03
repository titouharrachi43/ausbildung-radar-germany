import React, { useState, useEffect } from 'react';
import { Radio, Clock, Play, CheckCircle2, AlertTriangle, Terminal, RefreshCw, Server, Shield } from 'lucide-react';

export const AutomationView: React.FC = () => {
  const [schedulerStatus, setSchedulerStatus] = useState<any | null>(null);
  const [running, setRunning] = useState(false);
  const [runResult, setRunResult] = useState<any | null>(null);

  const fetchStatus = async () => {
    try {
      const res = await fetch('/api/scheduler/status');
      const data = await res.json();
      setSchedulerStatus(data);
    } catch (e) {
      console.error('Failed to fetch scheduler status:', e);
    }
  };

  useEffect(() => {
    fetchStatus();
  }, []);

  const handleTriggerRun = async () => {
    setRunning(true);
    setRunResult(null);
    try {
      const res = await fetch('/api/scheduler/run', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          cities: ['Hannover', 'Göttingen', 'Hildesheim', 'Celle'],
          sectors: ['Marketing', 'Education'],
        }),
      });
      const data = await res.json();
      setRunResult(data);
      fetchStatus();
    } catch (e: any) {
      setRunResult({ success: false, error: e.message });
    } finally {
      setRunning(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <span>Autonomous 5-Hour Search Cycle & Cloud Scheduler</span>
          <span className="text-xs px-2.5 py-0.5 rounded-full bg-purple-950 text-purple-300 border border-purple-800">
            Cron: 0 */5 * * *
          </span>
        </h2>
        <p className="text-xs text-gray-400">
          Independent background execution engine operating on Google Cloud Run without requiring an open browser session.
        </p>
      </div>

      {/* Cloud Architecture Details */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2">
          <div className="flex items-center space-x-2 text-purple-400">
            <Clock className="w-4 h-4" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-gray-300">Schedule & Frequency</h3>
          </div>
          <p className="text-lg font-bold text-white">Every ~5 Hours</p>
          <p className="text-xs text-gray-400">
            Standard cron: <code className="text-cyan-300 font-mono">0 */5 * * *</code> in time zone <span className="text-gray-200">Europe/Berlin</span>.
          </p>
        </div>

        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2">
          <div className="flex items-center space-x-2 text-cyan-400">
            <Server className="w-4 h-4" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-gray-300">Target Runtime</h3>
          </div>
          <p className="text-lg font-bold text-white">Google Cloud Run</p>
          <p className="text-xs text-gray-400">
            Serverless container hosting this Node.js web server and crawler API.
          </p>
        </div>

        <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-2">
          <div className="flex items-center space-x-2 text-emerald-400">
            <Shield className="w-4 h-4" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-gray-300">Target Webhook</h3>
          </div>
          <p className="text-sm font-mono text-cyan-300 font-bold truncate">/api/scheduler/run</p>
          <p className="text-xs text-gray-400">
            Protected endpoint authenticated via secret bearer token or Google OIDC.
          </p>
        </div>
      </div>

      {/* Manual Trigger & Configuration Status */}
      <div className="rounded-xl bg-[#111827] border border-gray-800 p-5 space-y-4">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold uppercase tracking-wider text-gray-400">Cloud Scheduler Job:</span>
              <span className="text-xs font-bold px-2 py-0.5 rounded bg-rose-500/10 text-rose-400 border border-rose-500/20">
                NOT CONFIGURED
              </span>
            </div>
            <p className="text-xs text-gray-400">
              A real Cloud Scheduler job has not yet been deployed on GCP. You can trigger an on-demand cycle right now to test the crawler & intelligence workflow.
            </p>
          </div>

          <button
            onClick={handleTriggerRun}
            disabled={running}
            className="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-bold shadow-md shadow-purple-950 transition disabled:opacity-50"
          >
            {running ? (
              <>
                <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                <span>Running Scan Cycle...</span>
              </>
            ) : (
              <>
                <Play className="w-3.5 h-3.5" />
                <span>Trigger Manual Search Cycle</span>
              </>
            )}
          </button>
        </div>

        {runResult && (
          <div className="p-4 rounded-xl bg-gray-900 border border-purple-500/30 text-xs font-mono space-y-1 text-gray-200">
            <p className="text-purple-300 font-bold">Execution Result:</p>
            <pre className="overflow-x-auto text-[11px] text-cyan-300">
              {JSON.stringify(runResult, null, 2)}
            </pre>
          </div>
        )}
      </div>

      {/* Execution History Table */}
      <div className="rounded-xl bg-[#111827] border border-gray-800 overflow-hidden">
        <div className="p-4 border-b border-gray-800 flex items-center justify-between">
          <h3 className="font-bold text-white text-sm flex items-center gap-2">
            <Terminal className="w-4 h-4 text-cyan-400" />
            <span>Search Cycle Execution History</span>
          </h3>
          <span className="text-xs text-gray-400">
            {schedulerStatus?.history?.length || 0} Cycles Logged
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-gray-900/60 text-gray-400 uppercase tracking-wider text-[10px]">
              <tr>
                <th className="p-3">Execution ID</th>
                <th className="p-3">Timestamp (CET)</th>
                <th className="p-3">Trigger</th>
                <th className="p-3">Cities Scanned</th>
                <th className="p-3">Duration</th>
                <th className="p-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800 text-gray-300 font-mono">
              {schedulerStatus?.history?.map((item: any) => (
                <tr key={item.id} className="hover:bg-gray-900/40">
                  <td className="p-3 text-cyan-400">{item.id}</td>
                  <td className="p-3 text-gray-400">{new Date(item.triggeredAt).toLocaleString('de-DE')}</td>
                  <td className="p-3">
                    <span className="px-1.5 py-0.5 rounded bg-gray-800 text-gray-300 text-[10px]">
                      {item.triggerType}
                    </span>
                  </td>
                  <td className="p-3 text-gray-300 font-sans">{item.citiesScanned.join(', ')}</td>
                  <td className="p-3 text-gray-400">{item.durationMs}ms</td>
                  <td className="p-3">
                    <span className="px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px] font-bold">
                      {item.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
