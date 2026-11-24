import {DetailedPoints} from './detailed-points'

export interface Bet {
  id: string
  gameId: string
  scoreTeamHome: number
  scoreTeamGuest: number
  joker: 1 | 2 | 3
  points: DetailedPoints | null
}
