import {createFeature, createReducer, on} from '@ngrx/store'
import {CardGroup} from '../../model/group/card-group'
import {
  betsSaved,
  dashboardDataLoaded,
  getLeaderboard,
  getOverviewGroups,
  grantRemainingDoubleJoker,
  grantRemainingTripleJoker,
  groupLoaded,
  leaderboardLoaded,
  loadDashboardData,
  loadGroup,
  overviewGroupsLoaded,
  resetSaving,
  resultsSaved,
  revokeRemainingDoubleJoker,
  revokeRemainingTripleJoker,
  saveBets,
  saveResults
} from './tournament.actions'
import {Group} from '../../model/group/group'
import {JokerQuota} from '../../model/group/joker-quota'
import {Ranking} from '../../model/leaderboard/ranking'
import {DashboardData} from '../../model/dashboard/dashboard-data'

export interface TournamentState {
  isTournamentLoading: boolean
  isTournamentSaving: boolean
  dashboard: DashboardData | null
  groups: CardGroup[]
  percentage: number
  percentageResult: number
  activeGroup: Group | null
  jokerQuota: JokerQuota | null
  leaderboard: Ranking[]
}

export const initialState: TournamentState = {
  isTournamentLoading: false,
  isTournamentSaving: false,
  dashboard: null,
  groups: [],
  percentage: 0,
  percentageResult: 0,
  activeGroup: null,
  jokerQuota: null,
  leaderboard: []
}

export const tournamentFeature = createFeature({
  name: 'tournament',
  reducer: createReducer(
    initialState,
    on(
      getOverviewGroups,
      loadGroup,
      getLeaderboard,
      loadDashboardData,
      (state): TournamentState => {
        return {
          ...state,
          isTournamentLoading: true
        }
      }),
    on(saveBets, saveResults, (state): TournamentState => {
      return {
        ...state,
        isTournamentSaving: true
      }
    }),
    on(resetSaving, (state): TournamentState => {
      return {
        ...state,
        isTournamentSaving: false
      }
    }),
    on(betsSaved, resultsSaved, (state, action): TournamentState => {
      return {
        ...state,
        isTournamentSaving: false,
        activeGroup: action.group,
        jokerQuota: action.group.jokerQuota
      }
    }),
    on(getOverviewGroups, (state): TournamentState => {
      return {
        ...state,
        groups: []
      }
    }),
    on(getLeaderboard, (state): TournamentState => {
      return {
        ...state,
        leaderboard: []
      }
    }),
    on(overviewGroupsLoaded, (state, action): TournamentState => {
      return {
        ...state,
        isTournamentLoading: false,
        groups: action.overview.groups,
        percentage: action.overview.percentage,
        percentageResult: action.overview.percentageResult,
        activeGroup: null
      }
    }),
    on(groupLoaded, (state, action): TournamentState => {
      return {
        ...state,
        isTournamentLoading: false,
        activeGroup: action.group,
        jokerQuota: action.group.jokerQuota
      }
    }),
    on(grantRemainingDoubleJoker, (state): TournamentState => {
      const jokers = state.jokerQuota
      if (!jokers) return state
      return {
        ...state,
        jokerQuota: {
          ...jokers,
          remainingDoubleJokers: jokers.remainingDoubleJokers + 1,
        }
      }
    }),
    on(revokeRemainingDoubleJoker, (state): TournamentState => {
      const jokers = state.jokerQuota
      if (!jokers) return state
      return {
        ...state,
        jokerQuota: {
          ...jokers,
          remainingDoubleJokers: jokers.remainingDoubleJokers - 1
        }
      }
    }),
    on(grantRemainingTripleJoker, (state): TournamentState => {
      const jokers = state.jokerQuota
      if (!jokers) return state
      return {
        ...state,
        jokerQuota: {
          ...jokers,
          remainingTripleJokers: jokers.remainingTripleJokers + 1
        }
      }
    }),
    on(revokeRemainingTripleJoker, (state): TournamentState => {
      const jokers = state.jokerQuota
      if (!jokers) return state
      return {
        ...state,
        jokerQuota: {
          ...jokers,
          remainingTripleJokers: jokers.remainingTripleJokers - 1
        }
      }
    }),
    on(leaderboardLoaded, (state, action): TournamentState => {
      return {
        ...state,
        leaderboard: action.leaderboard,
        isTournamentLoading: false
      }
    }),
    on(dashboardDataLoaded, (state, action): TournamentState => {
      return {
        ...state,
        dashboard: action.data,
        isTournamentLoading: false
      }
    })
  )
})

export const {
  selectIsTournamentLoading,
  selectGroups,
  selectPercentage,
  selectPercentageResult,
  selectActiveGroup,
  selectIsTournamentSaving,
  selectJokerQuota,
  selectLeaderboard,
  selectDashboard
} = tournamentFeature
