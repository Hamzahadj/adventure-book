import { ConsequenceResponse } from './consequence-response.model';

export interface OptionResponse {
  id: number;
  description: string;
  gotoId: number;
  consequence?: ConsequenceResponse;
}