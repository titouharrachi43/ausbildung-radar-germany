import { Router, Request, Response } from 'express';
import * as cheerio from 'cheerio';

export const crawlerRouter = Router();

const CAREER_KEYWORDS = [
  'karriere',
  'ausbildung',
  'jobs',
  'stellenangebote',
  'nachwuchs',
  'praktikum',
  'bewerbung',
  'career',
  'work-with-us',
];

crawlerRouter.post('/investigate', async (req: Request, res: Response) => {
  const { websiteUrl, companyName } = req.body;

  if (!websiteUrl) {
    return res.status(400).json({
      success: false,
      error: 'Please provide a valid company websiteUrl to investigate.',
    });
  }

  let normalizedUrl = websiteUrl.trim();
  if (!normalizedUrl.startsWith('http://') && !normalizedUrl.startsWith('https://')) {
    normalizedUrl = `https://${normalizedUrl}`;
  }

  const investigationLogs: string[] = [];
  investigationLogs.push(`Starting investigation of ${normalizedUrl} for company "${companyName || 'Unknown'}"`);

  try {
    // 1. Fetch homepage
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 8000);

    const homeResponse = await fetch(normalizedUrl, {
      signal: controller.signal,
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 (AusbildungRadarBot/1.0)',
        'Accept': 'text/html,application/xhtml+xml',
        'Accept-Language': 'de-DE,de;q=0.9,en;q=0.8',
      },
    });
    clearTimeout(timeout);

    if (!homeResponse.ok) {
      investigationLogs.push(`Homepage returned HTTP ${homeResponse.status}`);
      return res.status(400).json({
        success: false,
        error: `Could not fetch homepage (HTTP ${homeResponse.status})`,
        logs: investigationLogs,
      });
    }

    const homeHtml = await homeResponse.text();
    const $ = cheerio.load(homeHtml);

    investigationLogs.push(`Homepage loaded successfully (${homeHtml.length} bytes)`);

    // 2. Discover Career Links
    const candidateCareerLinks: { text: string; href: string }[] = [];
    $('a').each((_, el) => {
      const href = $(el).attr('href');
      const text = $(el).text().trim().toLowerCase();
      if (!href) return;

      const hrefLower = href.toLowerCase();
      const isMatch = CAREER_KEYWORDS.some(kw => hrefLower.includes(kw) || text.includes(kw));
      if (isMatch) {
        let fullUrl = href;
        if (href.startsWith('/')) {
          const origin = new URL(normalizedUrl).origin;
          fullUrl = `${origin}${href}`;
        } else if (!href.startsWith('http')) {
          fullUrl = `${normalizedUrl.replace(/\/$/, '')}/${href}`;
        }
        if (!candidateCareerLinks.some(l => l.href === fullUrl)) {
          candidateCareerLinks.push({ text: $(el).text().trim(), href: fullUrl });
        }
      }
    });

    investigationLogs.push(`Discovered ${candidateCareerLinks.length} career-related link(s) on homepage`);

    // Pick top career link or fallback to homepage
    const targetCareerUrl = candidateCareerLinks[0]?.href || normalizedUrl;
    investigationLogs.push(`Inspecting target URL: ${targetCareerUrl}`);

    let careerText = '';
    if (candidateCareerLinks.length > 0 && candidateCareerLinks[0]?.href !== normalizedUrl) {
      try {
        const c2 = new AbortController();
        const t2 = setTimeout(() => c2.abort(), 8000);
        const careerRes = await fetch(targetCareerUrl, {
          signal: c2.signal,
          headers: {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (AusbildungRadarBot/1.0)',
            'Accept-Language': 'de-DE,de;q=0.9,en;q=0.8',
          },
        });
        clearTimeout(t2);
        if (careerRes.ok) {
          const careerHtml = await careerRes.text();
          const $career = cheerio.load(careerHtml);
          // remove scripts, styles, navs
          $career('script, style, nav, footer, noscript').remove();
          careerText = $career('body').text().replace(/\s+/g, ' ').trim().substring(0, 10000);
          investigationLogs.push(`Fetched career page content (${careerText.length} characters)`);
        }
      } catch (subErr: any) {
        investigationLogs.push(`Failed to fetch career page sub-link: ${subErr.message}`);
      }
    }

    if (!careerText) {
      $('script, style, nav, footer, noscript').remove();
      careerText = $('body').text().replace(/\s+/g, ' ').trim().substring(0, 10000);
      investigationLogs.push(`Extracted homepage content (${careerText.length} characters)`);
    }

    // Check for Ausbildung keyword hits
    const lowerText = careerText.toLowerCase();
    const hasAusbildung = lowerText.includes('ausbildung') || lowerText.includes('azubi');
    const hasMarketing = lowerText.includes('marketing') || lowerText.includes('e-commerce') || lowerText.includes('mediengestalter');
    const hasEducation = lowerText.includes('erzieher') || lowerText.includes('sozialpädagog') || lowerText.includes('kinderpfleg');

    res.json({
      success: true,
      companyName,
      websiteUrl: normalizedUrl,
      careerPageUrl: targetCareerUrl,
      careerLinksFound: candidateCareerLinks,
      keywordHits: {
        hasAusbildung,
        hasMarketing,
        hasEducation,
      },
      contentSnippet: careerText.substring(0, 500),
      rawTextLength: careerText.length,
      investigatedAt: new Date().toISOString(),
      logs: investigationLogs,
    });
  } catch (err: any) {
    investigationLogs.push(`Investigation failed with error: ${err.message}`);
    res.status(500).json({
      success: false,
      error: err.message || 'Investigation failed',
      logs: investigationLogs,
    });
  }
});
