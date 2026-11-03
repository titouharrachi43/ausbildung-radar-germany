import { Router, Request, Response } from 'express';

export const geminiRouter = Router();

const SYSTEM_INSTRUCTION = `
You are an expert German vocational training (Ausbildung) analyst for the system "Ausbildung Radar Germany".
Your job is to analyze German career text, detect whether it contains a GENUINE Ausbildung vacancy, and extract structured data.

CRITICAL RULES:
1. TARGET SECTORS:
   - Marketing (Kaufmann/-frau für Marketingkommunikation, Kaufmann/-frau im E-Commerce, Mediengestalter/in Digital und Print, etc.)
   - Education & Childcare (Erzieher/in, Sozialpädagogische Assistenz, Kinderpfleger/in, etc.)
2. TARGET YEARS: Prioritize start year 2027 or 2028. If 2025/2026, still record but mark accordingly.
3. RIGOROUS CLASSIFICATION:
   - Distinguish strictly between:
     * "DUAL_AUSBILDUNG" (Company + Berufsschule according to BBiG/HwO)
     * "SCHULISCHE_AUSBILDUNG" (School-based vocational training, e.g. Fachschule für Sozialpädagogik)
     * "DUALES_STUDIUM" (University degree with corporate partner - NOT a pure Ausbildung)
     * "PRAKTIKUM" (Internship - NOT an Ausbildung)
     * "TRAINEE" (Post-graduate program - NOT an Ausbildung)
   - Do NOT classify university degrees or student internships as Ausbildung vacancies!
4. Return strict JSON format.
`;

geminiRouter.post('/extract', async (req: Request, res: Response) => {
  const { text, sourceUrl, companyName } = req.body;
  const apiKey = process.env.GEMINI_API_KEY || '';

  if (!text || typeof text !== 'string') {
    return res.status(400).json({
      success: false,
      error: 'Please provide job posting text in the request body.',
    });
  }

  if (!apiKey) {
    return res.status(400).json({
      success: false,
      error: 'GEMINI_API_KEY is not configured in server environment secrets.',
      status: 'API_KEY_MISSING',
    });
  }

  try {
    const prompt = `
Analyze the following career advertisement for company "${companyName || 'Unknown Company'}" (URL: ${sourceUrl || 'N/A'}).
Determine if there is a verified Ausbildung vacancy in Marketing or Education.

Content to analyze:
"""
${text.substring(0, 8000)}
"""

Respond with a JSON object strictly matching this schema:
{
  "isAusbildung": boolean,
  "confidenceScore": number (0.0 to 1.0),
  "opportunityType": "DUAL_AUSBILDUNG" | "SCHULISCHE_AUSBILDUNG" | "DUALES_STUDIUM" | "OTHER",
  "category": "MARKETING" | "EDUCATION" | "OTHER",
  "titleGerman": string,
  "startYear": 2027 | 2028 | 2026 | 2025 | null,
  "startDate": string | null,
  "locationCity": string | null,
  "locationBundesland": string | null,
  "requirements": string[],
  "benefits": string[],
  "applicationDeadline": string | null,
  "reasoning": string
}
`;

    const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`;
    const geminiRes = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: `${SYSTEM_INSTRUCTION}\n\n${prompt}` }] }],
        generationConfig: {
          responseMimeType: 'application/json',
          temperature: 0.2,
        },
      }),
    });

    const data = await geminiRes.json();

    if (!geminiRes.ok) {
      return res.status(geminiRes.status).json({
        success: false,
        error: data.error?.message || 'Gemini API call failed',
        status: data.error?.status || 'GEMINI_API_ERROR',
        details: data.error?.details || [],
      });
    }

    const candidateText = data.candidates?.[0]?.content?.parts?.[0]?.text;
    let extracted;
    try {
      extracted = JSON.parse(candidateText);
    } catch (parseErr) {
      extracted = { raw: candidateText };
    }

    res.json({
      success: true,
      modelUsed: 'gemini-1.5-flash',
      extracted,
      verifiedAt: new Date().toISOString(),
    });
  } catch (err: any) {
    res.status(500).json({
      success: false,
      error: err.message || 'Internal server error while calling Gemini API',
    });
  }
});
