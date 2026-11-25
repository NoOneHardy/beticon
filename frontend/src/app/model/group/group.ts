import {BetGame} from '../game/bet-game'
import {JokerQuota} from './joker-quota'

export interface Group {
  id: string
  name: string
  percentage: number
  percentageResult: number
  lastSavedAt: Date
  lastSavedAtResult: Date
  games: BetGame[]
  isKnockout: boolean
  jokerQuota: JokerQuota
}
