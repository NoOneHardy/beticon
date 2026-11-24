import {Component, HostBinding, input} from '@angular/core'
import {DetailedPoints} from '../../../../model/game/detailed-points'

@Component({
  selector: 'bet-point-table',
  standalone: true,
  templateUrl: './point-table.component.html',
  styleUrl: './point-table.component.css'
})
export class PointTableComponent {
  points = input.required<DetailedPoints>()
  summary = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })
  large = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })

  @HostBinding('class.-large')
  get largeClass(): boolean {
    return this.large()
  }

  get hasDetails(): boolean {
    const points = this.points()
    return !this.summary() && !!(points.correctWinner || points.correctGoalsHome || points.correctGoalsGuest
      || points.correctGoalsTotal || points.joker)
  }
}
