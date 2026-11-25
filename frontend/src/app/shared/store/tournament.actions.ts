import {createAction, props} from '@ngrx/store'
import {Group} from '../../model/group/group'
import {BetForm} from '../../model/game/bet-form'
import {ScoreForm} from '../../model/game/score-form'
import {Ranking} from '../../model/leaderboard/ranking'
import {DashboardData} from '../../model/dashboard/dashboard-data'
import {Overview} from '../../model/group/overview'

export const getOverviewGroups = createAction('[Tournament] Get Overview Groups')
export const overviewGroupsLoaded = createAction('[Tournament] Overview Groups Loaded', props<{
  overview: Overview
}>())
export const loadGroup = createAction('[Tournament] Select Group', props<{ groupId: string }>())
export const groupLoaded = createAction('[Tournament] Group Selected', props<{ group: Group }>())
export const saveBets = createAction('[Tournament] Save Bets', props<{ groupId: string, bets: BetForm[] }>())
export const betsSaved = createAction('[Tournament] Bets Saved', props<{ group: Group }>())
export const resetSaving = createAction('[Tournament] Reset Saving')

export const grantRemainingDoubleJoker = createAction('[Joker] Grant double joker')
export const revokeRemainingDoubleJoker = createAction('[Joker] Revoke double joker')
export const grantRemainingTripleJoker = createAction('[Joker] Grant triple joker')
export const revokeRemainingTripleJoker = createAction('[Joker] Revoke triple joker')

export const saveResults = createAction('[Tournament Admin] Save results', props<{
  groupId: string,
  results: ScoreForm[]
}>())
export const resultsSaved = createAction('[Tournament Admin] Results saved', props<{ group: Group }>())

export const getLeaderboard = createAction('[Tournament] Get Leaderboard')
export const leaderboardLoaded = createAction('[Tournament] Leaderboard Loaded', props<{
  leaderboard: Ranking[]
}>())

export const loadDashboardData = createAction('[Tournament] Get Dashboard Data')
export const dashboardDataLoaded = createAction('[Tournament] Dashboard Data Loaded', props<{
  data: DashboardData
}>())
