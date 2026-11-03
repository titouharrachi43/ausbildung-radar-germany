import { Router, Request, Response } from 'express';

export const healthRouter = Router();

healthRouter.get('/', (req: Request, res: Response) => {
  const googleMapsKey = process.env.GOOGLE_MAPS_API_KEY || process.env.VITE_GOOGLE_MAPS_API_KEY || '';
  const geminiKey = process.env.GEMINI_API_KEY || '';
  const firebaseProjectId = process.env.FIREBASE_PROJECT_ID || 'gen-lang-client-0556535763';

  res.json({
    status: 'ok',
    service: 'Ausbildung Radar Germany — Full-Stack Web Engine',
    timestamp: new Date().toISOString(),
    uptimeSeconds: Math.floor(process.uptime()),
    environment: process.env.NODE_ENV || 'development',
    integrations: {
      firebase: {
        configured: Boolean(firebaseProjectId),
        projectId: firebaseProjectId,
        authMethod: 'Google Sign-In (Firebase Auth)',
        firestoreDatabase: 'ai-studio-android-ausbildu-d760f243-40ee-46f2-8e1d-33ea02fbf2df',
      },
      googlePlacesApi: {
        configured: Boolean(googleMapsKey),
        keyMasked: googleMapsKey ? `${googleMapsKey.substring(0, 6)}...` : 'NOT_SET',
        endpoint: 'https://places.googleapis.com/v1/places:searchText (New)',
      },
      geminiApi: {
        configured: Boolean(geminiKey),
        keyMasked: geminiKey ? `${geminiKey.substring(0, 6)}...` : 'NOT_SET',
        models: ['gemini-1.5-flash', 'gemini-2.0-flash'],
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
    },
  });
});
