import {Component, DestroyRef, inject, signal} from '@angular/core'
import {takeUntilDestroyed} from '@angular/core/rxjs-interop'
import {finalize} from 'rxjs'
import {ButtonComponent} from '../../shared/components/button/button.component'
import {AdminService} from '../admin.service'
import {PointsChange} from '../model/points-change'

@Component({
  selector: 'bet-rescore-management',
  imports: [ButtonComponent],
  templateUrl: './rescore-management.component.html',
  styleUrl: './rescore-management.component.css'
})
export class RescoreManagementComponent {
  private adminService = inject(AdminService)
  private destroyRef = inject(DestroyRef)

  isConfirming = signal(false)
  isRunning = signal(false)
  changes = signal<PointsChange[] | null>(null)

  askConfirmation(): void {
    this.isConfirming.set(true)
  }

  cancel(): void {
    this.isConfirming.set(false)
  }

  confirm(): void {
    this.isConfirming.set(false)
    this.isRunning.set(true)
    this.adminService.rescoreEverything().pipe(
      finalize(() => this.isRunning.set(false)),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(changes => this.changes.set(changes))
  }
}
