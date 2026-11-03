import express from 'express';
import cors from 'cors';
import path from 'path';
import { fileURLToPath } from 'url';
import dotenv from 'dotenv';

import { healthRouter } from './routes/health.js';
import { placesRouter } from './routes/places.js';
import { geminiRouter } from './routes/gemini.js';
import { crawlerRouter } from './routes/crawler.js';
import { schedulerRouter } from './routes/scheduler.js';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const root = path.resolve(__dirname, '..');

const app = express();
const port = 3000;
const host = '0.0.0.0';

app.use(cors());
app.use(express.json({ limit: '10mb' }));

// Health and API routes
app.use('/api/health', healthRouter);
app.use('/api/places', placesRouter);
app.use('/api/gemini', geminiRouter);
app.use('/api/crawler', crawlerRouter);
app.use('/api/scheduler', schedulerRouter);

// Production vs Development frontend serving
async function setupFrontend() {
  if (process.env.NODE_ENV === 'production') {
    const clientDist = path.resolve(root, 'dist/client');
    app.use(express.static(clientDist));
    app.get('*', (req, res) => {
      res.sendFile(path.resolve(clientDist, 'index.html'));
    });
  } else {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true, host: '0.0.0.0' },
      appType: 'spa',
      root,
    });
    app.use(vite.middlewares);
  }
}

setupFrontend().then(() => {
  app.listen(port, host, () => {
    console.log(`[Ausbildung Radar Germany] Server active on http://${host}:${port}`);
  });
}).catch(err => {
  console.error('[Ausbildung Radar Germany] Failed to start server:', err);
  process.exit(1);
});
