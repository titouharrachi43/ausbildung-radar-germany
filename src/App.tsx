import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { StatsOverview } from './components/StatsOverview';
import { OpportunitiesView } from './components/OpportunitiesView';
import { CompaniesView } from './components/CompaniesView';
import { InvestigateTool } from './components/InvestigateTool';
import { GeminiTester } from './components/GeminiTester';
import { AutomationView } from './components/AutomationView';
import { IntegrationsAuditView } from './components/IntegrationsAuditView';
import { SEED_OPPORTUNITIES, SEED_COMPANIES } from './data/seedData';
import { Opportunity, Company, SystemHealth } from './types';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [opportunities, setOpportunities] = useState<Opportunity[]>(SEED_OPPORTUNITIES);
  const [companies, setCompanies] = useState<Company[]>(SEED_COMPANIES);
  const [health, setHealth] = useState<SystemHealth | null>(null);
  const [refreshingHealth, setRefreshingHealth] = useState(false);

  // Cross-screen state transfers
  const [investigatingCompany, setInvestigatingCompany] = useState<Company | null>(null);
  const [geminiPreload, setGeminiPreload] = useState<{
    text: string;
    url: string;
    company: string;
  } | null>(null);

  const fetchHealth = async () => {
    setRefreshingHealth(true);
    try {
      const res = await fetch('/api/health');
      if (res.ok) {
        const data = await res.json();
        setHealth(data);
      }
    } catch (e) {
      console.error('Failed to load system health:', e);
    } finally {
      setRefreshingHealth(false);
    }
  };

  useEffect(() => {
    fetchHealth();
  }, []);

  const handleInvestigateCompany = (company: Company) => {
    setInvestigatingCompany(company);
    setActiveTab('investigate');
  };

  const handleSendToGemini = (text: string, url: string, company: string) => {
    setGeminiPreload({ text, url, company });
    setActiveTab('gemini');
  };

  const handleAddOpportunity = (newOpp: Opportunity) => {
    setOpportunities((prev) => {
      if (prev.some((o) => o.id === newOpp.id)) return prev;
      return [newOpp, ...prev];
    });
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] text-gray-100 flex flex-col font-sans">
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {activeTab === 'overview' && (
          <StatsOverview
            opportunities={opportunities}
            companies={companies}
            health={health}
            onNavigate={setActiveTab}
          />
        )}

        {activeTab === 'opportunities' && (
          <OpportunitiesView opportunities={opportunities} />
        )}

        {activeTab === 'companies' && (
          <CompaniesView
            companies={companies}
            onInvestigateCompany={handleInvestigateCompany}
            onAddOpportunity={handleAddOpportunity}
          />
        )}

        {activeTab === 'investigate' && (
          <InvestigateTool
            initialCompany={investigatingCompany}
            onSendToGemini={handleSendToGemini}
          />
        )}

        {activeTab === 'gemini' && (
          <GeminiTester
            initialText={geminiPreload?.text}
            initialUrl={geminiPreload?.url}
            initialCompany={geminiPreload?.company}
          />
        )}

        {activeTab === 'automation' && <AutomationView />}

        {activeTab === 'audit' && (
          <IntegrationsAuditView
            health={health}
            onRefreshHealth={fetchHealth}
            refreshing={refreshingHealth}
          />
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-gray-900 bg-[#0A0D16] py-6 text-xs text-gray-500">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-2">
            <span className="font-semibold text-gray-400">Ausbildung Radar Germany</span>
            <span>•</span>
            <span>Autonomous Vocational Research Assistant</span>
          </div>
          <div className="flex items-center space-x-4">
            <span>Discovery: Gemini AI</span>
            <span>•</span>
            <span>Server: Node.js 22 + React 19</span>
            <span>•</span>
            <span>Host: 0.0.0.0:3000</span>
            <span>•</span>
            <span>Timezone: Europe/Berlin</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default App;
