import {Component, input} from '@angular/core'
import {SnackbarMessage} from './model/snackbar-message'

@Component({
  selector: 'bet-snackbar',
  imports: [],
  templateUrl: './snackbar.component.html',
  styleUrl: './snackbar.component.scss'
})
export class SnackbarComponent {
  readonly message = input.required<SnackbarMessage, SnackbarMessage>({
    transform: v => ({
      ...v,
      type: v.type ?? 'success'
    })
  })
}
