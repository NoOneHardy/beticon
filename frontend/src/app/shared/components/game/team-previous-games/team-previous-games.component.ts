import {Component, input} from '@angular/core'
import {NgOptimizedImage} from '@angular/common'
import {Team} from '../../../../model/team/team'

@Component({
  selector: 'bet-team-previous-games',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './team-previous-games.component.html',
  styleUrl: './team-previous-games.component.scss'
})
export class TeamPreviousGamesComponent {
  readonly teamSignal = input.required<Team>({alias: 'team'})
}
