import { Router, Request, Response } from 'express';

export const healthRouter = Router();

healthRouter.get('/', (req: Request, res: Response) => {
  const geminiKey = process.env.GEMINI_API_KEY || 'AIzaSyAEBfLGwD0u5iyG6ovJBA22IkIt5lFzI0A';
  const firebaseProjectId = process.env.FIREBASE_PROJECT_ID || 'gen-lang-client-0556535763';

  res.json({
    status: 'ok',
    service: 'Ausbildung Radar Germany — Full-Stack Web Engine',
    timestamp: new Date().toISOString(),
    uptimeSeconds: Math.floor(process.uptime()),
    environment: process.env.NODE_ENV || 'development',
    discoveryEngine: 'Gemini AI Intelligent Ranking Engine (No Google Maps needed)',
    integrations: {
      firebase: {
        configured: Boolean(firebaseProjectId),
        projectId: firebaseProjectId,
        authMethod: 'Google Sign-In (Firebase Auth)',
        firestoreDatabase: 'ai-studio-android-ausbildu-d760f243-40ee-46f2-8e1d-33ea02fbf2df',
      },
      geminiApi: {
        configured: Boolean(geminiKey),
        keyMasked: geminiKey ? `${geminiKey.substring(0, 6)}...${geminiKey.slice(-4)}` : 'NOT_SET',
        models: ['gemini-1.5-flash', 'gemini-2.0-flash'],
        role: 'Primary intelligence engine for business discovery, ranking best positions, and content extraction',
      },
      googlePlacesApi: {
        configured: false,
        required: false,
        status: 'DEPRECATED_PER_USER_REQUEST',
        message: 'Google Places API is disabled. Gemini AI is used exclusively to find the best opportunities.',
      },
      cloudScheduler: {
        configured: false,
        schedule: 'Every 5 hours (0 */5 * * *)',
        webhookEndpoint: '/api/scheduler/run',
        targetRuntime: 'Google Cloud Run',
      },
    },
    specs: {
      targetCountry: 'Germany',
      bundeslaender: 16,
      targetYears: [2027, 2028],
      targetSectors: ['Marketing & Digital Communication', 'Education & Childcare'],
      cityFocus: 'Prioritizing small and medium cities (<100,000 residents)',
    },
  });
});
