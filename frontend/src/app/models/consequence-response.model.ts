import { ConsequenceType } from './consequence-type.enum';

export interface ConsequenceResponse {
  type: ConsequenceType;
  value: string;
}