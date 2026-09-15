import { SectionType } from './section-type.enum';
import { OptionResponse } from './option-response.model';

export interface SectionResponse {
  originalSectionId: number;
  text: string;
  type: SectionType;
  options: OptionResponse[];
}