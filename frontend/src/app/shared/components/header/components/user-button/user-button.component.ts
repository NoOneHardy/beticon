import {Component, input, output} from '@angular/core'
import {RouterLink} from '@angular/router'
import {MatRipple} from '@angular/material/core'

@Component({
  selector: 'bet-user-button',
  imports: [
    RouterLink,
    MatRipple
  ],
  templateUrl: './user-button.component.html',
  styleUrl: './user-button.component.css'
})
export class UserButtonComponent {
  readonly secondary = input<boolean, boolean | ''>(false, {
    transform: (value) => value === '' || value
  })
  readonly route = input<string | null>(null)
  readonly content = input<string>('')
  clickButton = output<void>()
}
