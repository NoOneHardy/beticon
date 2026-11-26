import {Component, HostBinding, input} from '@angular/core'
import {PointsBreakdown} from '../../../../model/game/points-breakdown'

@Component({
  selector: 'bet-point-table',
  standalone: true,
  templateUrl: './point-table.component.html',
  styleUrl: './point-table.component.css'
})
export class PointTableComponent {
  readonly points = input.required<PointsBreakdown>()
  readonly summary = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })
  readonly large = input<boolean, boolean | ''>(false, {
    transform: (v) => v === '' || v
  })

  @HostBinding('class.-large')
  get largeClass(): boolean {
    return this.large()
  }
}
