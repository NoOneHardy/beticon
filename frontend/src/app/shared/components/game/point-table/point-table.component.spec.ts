import {ComponentFixture, TestBed} from '@angular/core/testing'
import {PointTableComponent} from './point-table.component'
import {PointsBreakdown} from '../../../../model/game/points-breakdown'
import {findElement} from '../../../helper/test.helper'

describe('PointTableComponent', () => {
  let fixture: ComponentFixture<PointTableComponent>

  const render = (points: PointsBreakdown, summary = false): string[] => {
    fixture.componentRef.setInput('points', points)
    fixture.componentRef.setInput('summary', summary)
    fixture.detectChanges()
    const el = fixture.nativeElement as HTMLElement
    return Array.from(el.querySelectorAll('span')).map(s => s.textContent.replace(/\s/g, ' ').trim())
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PointTableComponent]
    }).compileComponents()
    fixture = TestBed.createComponent(PointTableComponent)
  })

  it('should render every awarded item, the joker and the total from the payload', () => {
    const texts = render({
      correctWinner: 5,
      correctGoalsHome: 2,
      correctGoalsGuest: 2,
      correctGoalsTotal: 1,
      joker: 2,
      total: 20
    })

    expect(texts).toEqual([
      'Richtiger Sieger', '+ 5',
      'Tore Heim', '+ 2',
      'Tore Gast', '+ 2',
      'Tore Gesamt', '+ 1',
      'Joker', 'x2',
      'Total', '+ 20'
    ])
    expect(findElement(fixture, '.separator')).not.toBeNull()
  })

  it('should skip items that are null', () => {
    const texts = render({
      correctWinner: 5,
      correctGoalsHome: null,
      correctGoalsGuest: 2,
      correctGoalsTotal: null,
      joker: 1,
      total: 7
    })

    expect(texts).toEqual([
      'Richtiger Sieger', '+ 5',
      'Tore Gast', '+ 2',
      'Total', '+ 7'
    ])
  })

  it('should render the point values from the payload', () => {
    const texts = render({
      correctWinner: 7,
      correctGoalsHome: 3,
      correctGoalsGuest: 4,
      correctGoalsTotal: 6,
      joker: 3,
      total: 60
    })

    expect(texts).toEqual([
      'Richtiger Sieger', '+ 7',
      'Tore Heim', '+ 3',
      'Tore Gast', '+ 4',
      'Tore Gesamt', '+ 6',
      'Joker', 'x3',
      'Total', '+ 60'
    ])
  })

  it('should not render a joker of 1', () => {
    const texts = render({
      correctWinner: 5, correctGoalsHome: null, correctGoalsGuest: null, correctGoalsTotal: null, joker: 1, total: 5
    })

    expect(texts).toEqual(['Richtiger Sieger', '+ 5', 'Total', '+ 5'])
  })

  it('should render only the total in summary mode', () => {
    const texts = render({
      correctWinner: 5, correctGoalsHome: null, correctGoalsGuest: null, correctGoalsTotal: null, joker: 1, total: 5
    }, true)

    expect(texts).toEqual(['Total', '+ 5'])
    expect(findElement(fixture, '.separator')).toBeNull()
  })
})
