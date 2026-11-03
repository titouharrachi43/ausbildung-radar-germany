import React, { useState, useEffect } from 'react';
import { Radar, Clock, ShieldCheck, User, LogIn, LogOut, CheckCircle2, AlertTriangle } from 'lucide-react';
import { auth, googleProvider } from '../firebase';
import { signInWithPopup, signOut, onAuthStateChanged, User as FirebaseUser } from 'firebase/auth';

interface HeaderProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
}

export const Header: React.FC<HeaderProps> = ({ activeTab, setActiveTab }) => {
  const [currentUser, setCurrentUser] = useState<FirebaseUser | null>(null);
  const [berlinTime, setBerlinTime] = useState<string>('');
  const [authLoading, setAuthLoading] = useState(false);
  const [authError, setAuthError] = useState<string | null>(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);
    });

    const updateTime = () => {
      try {
        const timeStr = new Intl.DateTimeFormat('de-DE', {
          timeZone: 'Europe/Berlin',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
        }).format(new Date());
        setBerlinTime(timeStr);
      } catch (e) {
        setBerlinTime(new Date().toLocaleTimeString());
      }
    };

    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => {
      unsubscribe();
      clearInterval(interval);
    };
  }, []);

  const handleGoogleSignIn = async () => {
    setAuthLoading(true);
    setAuthError(null);
    try {
      await signInWithPopup(auth, googleProvider);
    } catch (err: any) {
      console.error('Google Sign-in failed:', err);
      setAuthError(err.message || 'Sign in failed');
    } finally {
      setAuthLoading(false);
    }
  };

  const handleSignOut = async () => {
    try {
      await signOut(auth);
    } catch (err: any) {
      console.error('Sign-out failed:', err);
    }
  };

  const navItems = [
    { id: 'overview', label: 'Overview' },
    { id: 'opportunities', label: 'Opportunities' },
    { id: 'companies', label: 'Discovered Businesses' },
    { id: 'investigate', label: 'Website Crawler' },
    { id: 'gemini', label: 'AI Extractor' },
    { id: 'automation', label: 'Automation & Scheduler' },
    { id: 'audit', label: 'System Health' },
  ];

  return (
    <header className="border-b border-gray-800 bg-[#0E1526]/80 backdrop-blur sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Top bar with branding, live Berlin clock and auth */}
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-3">
            <div className="relative flex items-center justify-center w-10 h-10 rounded-xl bg-cyan-950/60 border border-cyan-500/30 text-cyan-400 shadow-lg shadow-cyan-500/10">
              <Radar className="w-5 h-5 animate-pulse" />
              <span className="absolute -top-1 -right-1 flex h-2.5 w-2.5">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-cyan-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-cyan-500"></span>
              </span>
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-bold text-lg text-white tracking-tight">Ausbildung Radar</span>
                <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                  Germany
                </span>
              </div>
              <p className="text-xs text-gray-400 hidden sm:block">Automated Research Assistant • Marketing & Education 2027/2028</p>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            {/* Live Berlin Time Indicator */}
            <div className="hidden md:flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-gray-900/80 border border-gray-800 text-xs">
              <Clock className="w-3.5 h-3.5 text-cyan-400" />
              <span className="text-gray-400">Berlin Time:</span>
              <span className="font-mono text-cyan-300 font-semibold">{berlinTime || 'Loading...'}</span>
            </div>

            {/* Google Authentication */}
            {currentUser ? (
              <div className="flex items-center space-x-3 pl-2 border-l border-gray-800">
                <div className="flex items-center space-x-2">
                  {currentUser.photoURL ? (
                    <img src={currentUser.photoURL} alt={currentUser.displayName || ''} className="w-8 h-8 rounded-full border border-cyan-500/30" />
                  ) : (
                    <div className="w-8 h-8 rounded-full bg-cyan-900 text-cyan-300 flex items-center justify-center font-bold text-xs">
                      {currentUser.email?.[0].toUpperCase() || 'U'}
                    </div>
                  )}
                  <div className="hidden lg:block text-left text-xs">
                    <p className="font-medium text-gray-200 truncate max-w-[120px]">{currentUser.displayName || currentUser.email}</p>
                    <p className="text-[10px] text-emerald-400 flex items-center gap-1">
                      <ShieldCheck className="w-2.5 h-2.5" /> Admin Authorized
                    </p>
                  </div>
                </div>
                <button
                  onClick={handleSignOut}
                  title="Sign Out"
                  className="p-1.5 rounded-lg hover:bg-gray-800 text-gray-400 hover:text-rose-400 transition"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <button
                onClick={handleGoogleSignIn}
                disabled={authLoading}
                className="flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-md shadow-cyan-950 transition disabled:opacity-50"
              >
                <LogIn className="w-3.5 h-3.5" />
                <span>{authLoading ? 'Signing In...' : 'Google Sign-In'}</span>
              </button>
            )}
          </div>
        </div>

        {authError && (
          <div className="p-2 mb-2 bg-rose-950/40 border border-rose-500/30 text-rose-300 text-xs rounded-lg flex items-center gap-2">
            <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
            <span>Auth Notice: {authError}</span>
          </div>
        )}

        {/* Navigation Tabs */}
        <nav className="flex space-x-1 sm:space-x-2 overflow-x-auto py-2 scrollbar-none border-t border-gray-800/60">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-colors ${
                activeTab === item.id
                  ? 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/30 shadow-sm shadow-cyan-950'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50'
              }`}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </div>
    </header>
  );
};
