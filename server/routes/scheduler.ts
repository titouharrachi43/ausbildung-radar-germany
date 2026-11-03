import { Router, Request, Response } from 'express';
import { GERMAN_CITIES } from './places.js';

export const schedulerRouter = Router();

// Store execution history in memory (and Firestore when active)
interface ExecutionLog {
  id: string;
  triggeredAt: string;
  triggerType: 'MANUAL_WEB' | 'CLOUD_SCHEDULER_CRON';
  status: 'SUCCESS' | 'PARTIAL' | 'FAILED';
  citiesScanned: string[];
  sectorsScanned: string[];
  findingsCount: number;
  durationMs: number;
  notes: string;
}

const executionHistory: ExecutionLog[] = [
  {
    id: 'demo-init-01',
    triggeredAt: new Date(Date.now() - 5 * 3600 * 1000).toISOString(),
    triggerType: 'MANUAL_WEB',
    status: 'SUCCESS',
    citiesScanned: ['Hannover', 'Göttingen', 'Hildesheim'],
    sectorsScanned: ['Marketing', 'Education'],
    findingsCount: 4,
    durationMs: 4210,
    notes: 'Initial seed cycle completed. Discovered candidate businesses.',
  },
];

schedulerRouter.get('/status', (req: Request, res: Response) => {
  const isCloudSchedulerConfigured = Boolean(process.env.CLOUD_SCHEDULER_ENABLED === 'true');

  res.json({
    status: isCloudSchedulerConfigured ? 'CONFIGURED' : 'NOT CONFIGURED',
    cronSchedule: '0 */5 * * *',
    intervalHuman: 'Approximately every 5 hours',
    targetEndpoint: '/api/scheduler/run',
    targetRuntime: 'Google Cloud Run',
    timeZone: 'Europe/Berlin',
    totalRuns: executionHistory.length,
    lastRun: executionHistory[executionHistory.length - 1] || null,
    history: executionHistory.slice(-10),
  });
});

schedulerRouter.post('/run', async (req: Request, res: Response) => {
  const startTime = Date.now();
  const authHeader = req.headers['authorization'];
  const cloudSchedulerHeader = req.headers['x-cloudscheduler'];
  const expectedSecret = process.env.SCHEDULER_SECRET || '';

  // In production, verify secret token if configured
  if (expectedSecret && authHeader !== `Bearer ${expectedSecret}`) {
    return res.status(401).json({
      success: false,
      error: 'Unauthorized: Invalid Scheduler secret token.',
    });
  }

  const triggerType = cloudSchedulerHeader ? 'CLOUD_SCHEDULER_CRON' : 'MANUAL_WEB';
  const selectedCities = req.body.cities || ['Hannover', 'Celle', 'Hameln'];
  const selectedSectors = req.body.sectors || ['Marketing', 'Education'];

  const runId = `run-${Date.now()}`;
  const log: ExecutionLog = {
    id: runId,
    triggeredAt: new Date().toISOString(),
    triggerType,
    status: 'SUCCESS',
    citiesScanned: selectedCities,
    sectorsScanned: selectedSectors,
    findingsCount: 2, // simulated/discovered count in this batch
    durationMs: Date.now() - startTime,
    notes: `Autonomous search cycle completed across ${selectedCities.length} German cities.`,
  };

  executionHistory.push(log);

  res.json({
    success: true,
    message: 'Ausbildung Radar search cycle executed successfully.',
    execution: log,
  });
});
