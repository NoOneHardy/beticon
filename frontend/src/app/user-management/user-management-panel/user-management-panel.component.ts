import {Component, input} from '@angular/core'

@Component({
  selector: 'bet-user-management-panel',
  imports: [],
  templateUrl: './user-management-panel.component.html',
  styleUrl: './user-management-panel.component.css'
})
export class UserManagementPanelComponent {
  readonly heading = input<string | null>(null)
}
