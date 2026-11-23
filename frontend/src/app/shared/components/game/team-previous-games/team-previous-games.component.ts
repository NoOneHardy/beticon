import {Component, input, ChangeDetectionStrategy} from '@angular/core'
import {NgOptimizedImage} from '@angular/common'
import {Team} from '../../../../model/team/team'

@Component({
  changeDetection: ChangeDetectionStrategy.Eager,
  selector: 'bet-team-previous-games',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './team-previous-games.component.html',
  styleUrl: './team-previous-games.component.css'
})
export class TeamPreviousGamesComponent {
  teamSignal = input.required<Team>({alias: 'team'})
}
