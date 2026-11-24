import {ComponentFixture, TestBed} from '@angular/core/testing'
import {PointTableComponent} from './point-table.component'
import {DetailedPoints} from '../../../../model/game/detailed-points'

describe('PointTableComponent', () => {
  let fixture: ComponentFixture<PointTableComponent>

  const render = (points: DetailedPoints, summary = false): string[] => {
    fixture.componentRef.setInput('points', points)
    fixture.componentRef.setInput('summary', summary)
    fixture.detectChanges()
    const el: HTMLElement = fixture.nativeElement
    return Array.from(el.querySelectorAll('span')).map(s => (s.textContent ?? '').replace(/\s/g, ' ').trim())
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
    expect(fixture.nativeElement.querySelector('.separator')).not.toBeNull()
  })

  it('should skip items that are null', () => {
    const texts = render({
      correctWinner: 5,
      correctGoalsHome: null,
      correctGoalsGuest: 2,
      correctGoalsTotal: null,
      joker: null,
      total: 7
    })

    expect(texts).toEqual([
      'Richtiger Sieger', '+ 5',
      'Tore Gast', '+ 2',
      'Total', '+ 7'
    ])
  })

  it('should render only the total in summary mode', () => {
    const texts = render({correctWinner: 5, total: 5}, true)

    expect(texts).toEqual(['Total', '+ 5'])
    expect(fixture.nativeElement.querySelector('.separator')).toBeNull()
  })
})
