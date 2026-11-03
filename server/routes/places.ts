import { Router, Request, Response } from 'express';

export const placesRouter = Router();

// German cities seed list for discovery
export const GERMAN_CITIES = [
  { name: 'Hannover', bundesland: 'Niedersachsen', populationTier: 'Large' },
  { name: 'Göttingen', bundesland: 'Niedersachsen', populationTier: 'Medium (<150k)' },
  { name: 'Hildesheim', bundesland: 'Niedersachsen', populationTier: 'Small (<110k)' },
  { name: 'Celle', bundesland: 'Niedersachsen', populationTier: 'Small (<70k)' },
  { name: 'Hameln', bundesland: 'Niedersachsen', populationTier: 'Small (<60k)' },
  { name: 'Bamberg', bundesland: 'Bayern', populationTier: 'Small (<80k)' },
  { name: 'Passau', bundesland: 'Bayern', populationTier: 'Small (<55k)' },
  { name: 'Trier', bundesland: 'Rheinland-Pfalz', populationTier: 'Medium (<120k)' },
  { name: 'Flensburg', bundesland: 'Schleswig-Holstein', populationTier: 'Small (<95k)' },
  { name: 'Fulda', bundesland: 'Hessen', populationTier: 'Small (<70k)' },
  { name: 'Konstanz', bundesland: 'Baden-Württemberg', populationTier: 'Small (<85k)' },
  { name: 'Potsdam', bundesland: 'Brandenburg', populationTier: 'Medium (<190k)' },
  { name: 'Weimar', bundesland: 'Thüringen', populationTier: 'Small (<65k)' },
  { name: 'Schwerin', bundesland: 'Mecklenburg-Vorpommern', populationTier: 'Small (<96k)' },
  { name: 'Wismar', bundesland: 'Mecklenburg-Vorpommern', populationTier: 'Small (<45k)' },
  { name: 'Dessau-Roßlau', bundesland: 'Sachsen-Anhalt', populationTier: 'Small (<80k)' },
];

placesRouter.post('/search', async (req: Request, res: Response) => {
  const { city = 'Hannover', category = 'Marketingagentur' } = req.body;
  const apiKey = process.env.GOOGLE_MAPS_API_KEY || process.env.VITE_GOOGLE_MAPS_API_KEY || '';

  if (!apiKey) {
    return res.status(400).json({
      success: false,
      error: 'GOOGLE_MAPS_API_KEY is not configured in server environment secrets.',
      status: 'API_KEY_MISSING',
      city,
      category,
      places: [],
    });
  }

  try {
    const textQuery = `${category} ${city} Germany`;
    const response = await fetch('https://places.googleapis.com/v1/places:searchText', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Goog-Api-Key': apiKey,
        'X-Goog-FieldMask': 'places.id,places.displayName,places.formattedAddress,places.websiteUri,places.rating,places.types',
      },
      body: JSON.stringify({
        textQuery,
        languageCode: 'de',
        maxResultCount: 10,
      }),
    });

    const data = await response.json();

    if (!response.ok) {
      return res.status(response.status).json({
        success: false,
        error: data.error?.message || 'Google Places API returned an error',
        status: data.error?.status || 'API_ERROR',
        details: data.error?.details || [],
        city,
        category,
      });
    }

    const places = (data.places || []).map((p: any) => ({
      placeId: p.id,
      name: p.displayName?.text || 'Unknown Business',
      address: p.formattedAddress || `${city}, Germany`,
      websiteUrl: p.websiteUri || null,
      city,
      category,
      source: 'Google Places API (New)',
      discoveredAt: new Date().toISOString(),
    }));

    res.json({
      success: true,
      query: textQuery,
      count: places.length,
      places,
    });
  } catch (err: any) {
    res.status(500).json({
      success: false,
      error: err.message || 'Internal server error during Places search',
    });
  }
});

placesRouter.get('/cities', (req: Request, res: Response) => {
  res.json({
    totalCities: GERMAN_CITIES.length,
    cities: GERMAN_CITIES,
  });
});
