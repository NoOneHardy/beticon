import {Component, ChangeDetectionStrategy} from '@angular/core'

@Component({
  changeDetection: ChangeDetectionStrategy.Eager,
  selector: 'bet-stadium',
  templateUrl: './stadium.component.html',
  styleUrl: './stadium.component.css',
  standalone: true
})
export class StadiumComponent {
}
