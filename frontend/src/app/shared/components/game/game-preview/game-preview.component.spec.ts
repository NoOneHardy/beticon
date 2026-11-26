import { ComponentFixture, TestBed } from '@angular/core/testing'

import { GamePreviewComponent } from './game-preview.component'
import {BetGame} from '../../../../model/game/bet-game'
import {Bet} from '../../../../model/game/bet'
import {findElement} from '../../../helper/test.helper'

describe('GamePreviewComponent', () => {
  let component: GamePreviewComponent
  let fixture: ComponentFixture<GamePreviewComponent>
  let mockBet: Bet
  let mockGame: BetGame

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamePreviewComponent]
    }).compileComponents()

    mockBet = {
      id: 'asdf-1',
      scoreTeamHome: 3,
      scoreTeamGuest: 2,
      joker: 3,
      gameId: 'bet-1',
      points: null
    }

    mockGame = {
      groupId: 'group-1',
      groupName: 'Gruppe A',
      id: 'game-1',
      timestamp: new Date(
        new Date().getFullYear(),
        new Date().getMonth(),
        new Date().getDate() + 1
      ),
      teamHome: {
        id: 'team-1',
        name: 'Deutschland',
        flag: 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Flag_of_Germany.svg/180px-Flag_of_Germany.svg.png',
        previousGames: [],
        shortName: ''
      },
      teamGuest: {
        id: 'team-2',
        name: 'Schottland',
        flag: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Flag_of_Scotland.svg/250px-Flag_of_Scotland.svg.png',
        previousGames: [],
        shortName: ''
      },
      result: null,
      bet: mockBet
    }

    fixture = TestBed.createComponent(GamePreviewComponent)
    component = fixture.componentInstance
    fixture.componentRef.setInput('game', {...mockGame})
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should render the total of the bet points payload', () => {
    fixture.componentRef.setInput('game', {
      ...mockGame,
      result: {id: 'r', gameId: 'game-1', scoreTeamHome: 3, scoreTeamGuest: 2},
      bet: {
        ...mockBet,
        points: {correctWinner: 5, correctGoalsHome: 2, correctGoalsGuest: 2, correctGoalsTotal: 1, joker: 3, total: 30}
      }
    })
    fixture.detectChanges()

    const points = findElement(fixture, '.bet-points')
    expect(points?.textContent.trim()).toBe('+30')
  })

  it('should render +0 when the bet has no points payload', () => {
    fixture.componentRef.setInput('game', {
      ...mockGame,
      result: {id: 'r', gameId: 'game-1', scoreTeamHome: 3, scoreTeamGuest: 2}
    })
    fixture.detectChanges()

    const points = findElement(fixture, '.bet-points')
    expect(points?.textContent.trim()).toBe('+0')
  })
})
