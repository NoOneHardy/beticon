import {FeatureSlice} from '@ngrx/store'
import * as feature from './tournament.feature'
import {TournamentState} from './tournament.feature'
import {
  grantRemainingDoubleJoker,
  grantRemainingTripleJoker,
  groupLoaded,
  loadGroup,
  overviewGroupsLoaded,
  resultsSaved,
  revokeRemainingDoubleJoker,
  revokeRemainingTripleJoker,
  saveResults
} from './tournament.actions'
import {Group} from '../../model/group/group'

const mockGroup: Group = {
  id: 'group-1',
  name: 'Gruppe A',
  games: [],
  isKnockout: false,
  lastSavedAt: new Date(),
  lastSavedAtResult: new Date(),
  percentage: 100,
  percentageResult: 100,
  jokerQuota: {
    remainingDoubleJokers: 2,
    remainingTripleJokers: 3,
    doubleJokerLimit: 10,
    tripleJokerLimit: 6
  }
}

describe('TournamentFeature', () => {
  let store: FeatureSlice<TournamentState>
  let initialState: TournamentState

  beforeEach(() => {
    store = feature.tournamentFeature
    initialState = feature.initialState
  })

  it('should initialize', () => {
    expect(store).toBeTruthy()
  })

  it('should set joker quota when setting active group', () => {
    let jokerQuota = initialState.jokerQuota
    expect(jokerQuota).toBeFalsy()
    jokerQuota = store.reducer(initialState, groupLoaded({group: mockGroup})).jokerQuota
    expect(jokerQuota).toBeTruthy()
  })

  it('should add a double joker', () => {
    let state = store.reducer(initialState, groupLoaded({group: mockGroup}))
    let jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingDoubleJokers).toBe(2)
    state = store.reducer(state, grantRemainingDoubleJoker())
    jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingDoubleJokers).toBe(3)
  })

  it('should remove a double joker', () => {
    let state = store.reducer(initialState, groupLoaded({group: mockGroup}))
    let jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingDoubleJokers).toBe(2)
    state = store.reducer(state, revokeRemainingDoubleJoker())
    jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingDoubleJokers).toBe(1)
  })

  it('should add a triple joker', () => {
    let state = store.reducer(initialState, groupLoaded({group: mockGroup}))
    let jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingTripleJokers).toBe(3)
    state = store.reducer(state, grantRemainingTripleJoker())
    jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingTripleJokers).toBe(4)
  })

  it('should remove a triple joker', () => {
    let state = store.reducer(initialState, groupLoaded({group: mockGroup}))
    let jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingTripleJokers).toBe(3)
    state = store.reducer(state, revokeRemainingTripleJoker())
    jokerQuota = state.jokerQuota
    expect(jokerQuota?.remainingTripleJokers).toBe(2)
  })

  it('should start loading when selecting a group', () => {
    let state = initialState
    expect(state.isTournamentLoading).toBe(false)
    state = store.reducer(state, loadGroup({groupId: 'group-1'}))
    expect(state.isTournamentLoading).toBe(true)
  })

  it('should stop loading when group has been loaded', () => {
    let state = initialState
    state = store.reducer(state, loadGroup({groupId: 'group-1'}))
    expect(state.isTournamentLoading).toBe(true)
    state = store.reducer(state, groupLoaded({group: mockGroup}))
    expect(state.isTournamentLoading).toBe(false)
  })

  it('should reset active group when loading overview groups', () => {
    let state = initialState
    state = store.reducer(state, groupLoaded({group: mockGroup}))
    expect(state.activeGroup).toBeTruthy()
    state = store.reducer(state, overviewGroupsLoaded({
      overview: {
        groups: [],
        percentage: 0,
        percentageResult: 0
      }
    }))
    expect(state.activeGroup).toBeNull()
  })

  it('should start loading when saving results', () => {
    let state = initialState
    expect(state.isTournamentSaving).toBe(false)
    state = store.reducer(state, saveResults({groupId: 'group-1', results: []}))
    expect(state.isTournamentSaving).toBe(true)
  })

  it('should stop loading when results have been saved', () => {
    let state = initialState
    state = store.reducer(state, saveResults({groupId: 'group-1', results: []}))
    expect(state.isTournamentSaving).toBe(true)
    state = store.reducer(state, resultsSaved({group: mockGroup}))
    expect(state.isTournamentSaving).toBe(false)
  })

  it('should update group and joker quota when results have been saved', () => {
    let state = initialState
    state = store.reducer(state, saveResults({groupId: 'group-1', results: []}))
    expect(state.isTournamentSaving).toBe(true)
    state = store.reducer(state, resultsSaved({group: mockGroup}))
    expect(state.activeGroup).toEqual(mockGroup)
    expect(state.jokerQuota).toEqual(mockGroup.jokerQuota)
  })
})
