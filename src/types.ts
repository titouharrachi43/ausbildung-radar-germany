export type OpportunityType =
  | 'DUAL_AUSBILDUNG'
  | 'SCHULISCHE_AUSBILDUNG'
  | 'DUALES_STUDIUM'
  | 'OTHER';

export type Sector = 'MARKETING' | 'EDUCATION';

export type OpportunityStatus =
  | 'VERIFIED_ACTIVE'
  | 'UNDER_INVESTIGATION'
  | 'EXPIRED'
  | 'EXCLUDED';

export interface Opportunity {
  id: string;
  titleGerman: string;
  companyName: string;
  companyId: string;
  city: string;
  bundesland: string;
  opportunityType: OpportunityType;
  sector: Sector;
  targetYear: 2027 | 2028 | 2026;
  sourceUrl: string;
  verifiedAt: string;
  status: OpportunityStatus;
  confidenceScore: number;
  descriptionSnippet: string;
  requirements: string[];
  benefits: string[];
  isDemoData: boolean;
}

export interface Company {
  id: string;
  name: string;
  city: string;
  bundesland: string;
  category: string;
  websiteUrl: string;
  careerUrl: string;
  placeId: string;
  discoveredAt: string;
  verifiedOpportunitiesCount: number;
  isDemoData: boolean;
}

export interface SystemHealth {
  status: string;
  service: string;
  timestamp: string;
  uptimeSeconds: number;
  environment: string;
  integrations: {
    firebase: {
      configured: boolean;
      projectId: string;
      authMethod: string;
      firestoreDatabase: string;
    };
    googlePlacesApi: {
      configured: boolean;
      keyMasked: string;
      endpoint: string;
    };
    geminiApi: {
      configured: boolean;
      keyMasked: string;
      models: string[];
    };
    cloudScheduler: {
      configured: boolean;
      schedule: string;
      webhookEndpoint: string;
      targetRuntime: string;
    };
  };
}
