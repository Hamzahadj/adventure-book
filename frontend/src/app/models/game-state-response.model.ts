import { OptionResponse } from './option-response.model';

export interface GameStateResponse {
  currentSectionId: number;
  healthPoints: number;
  isGameOver: boolean;
  isVictory: boolean;
  message: string;
  sectionText: string;
  optionResponses: OptionResponse[];
  }