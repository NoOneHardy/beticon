import {ComponentFixture, TestBed} from '@angular/core/testing'
import {of, Subject} from 'rxjs'
import {RescoreManagementComponent} from './rescore-management.component'
import {AdminService} from '../admin.service'
import {PointsChange} from '../model/points-change'

describe('RescoreManagementComponent', () => {
  let fixture: ComponentFixture<RescoreManagementComponent>
  let element: HTMLElement
  let adminService: {rescoreEverything: ReturnType<typeof vi.fn>}

  const changes: PointsChange[] = [
    {userId: 'u1', username: 'hardy', oldPoints: 12, newPoints: 9},
    {userId: 'u2', username: 'lena', oldPoints: 0, newPoints: 5}
  ]

  const buttons = () => Array.from(element.querySelectorAll('button'))
  const buttonByText = (text: string) => buttons().find(b => b.textContent?.includes(text))

  async function click(text: string): Promise<void> {
    buttonByText(text)?.click()
    fixture.detectChanges()
    await fixture.whenStable()
    fixture.detectChanges()
  }

  beforeEach(async () => {
    adminService = {rescoreEverything: vi.fn().mockReturnValue(of(changes))}

    await TestBed.configureTestingModule({
      imports: [RescoreManagementComponent],
      providers: [{provide: AdminService, useValue: adminService}]
    }).compileComponents()

    fixture = TestBed.createComponent(RescoreManagementComponent)
    element = fixture.nativeElement
    fixture.detectChanges()
  })

  it('should show the re-score button without calling the API', () => {
    expect(buttonByText('Alles neu berechnen')).toBeTruthy()
    expect(adminService.rescoreEverything).not.toHaveBeenCalled()
  })

  it('should ask for confirmation before calling the API', async () => {
    await click('Alles neu berechnen')

    expect(element.textContent).toContain('Wirklich')
    expect(buttonByText('Bestätigen')).toBeTruthy()
    expect(adminService.rescoreEverything).not.toHaveBeenCalled()
  })

  it('should do nothing when the confirmation is cancelled', async () => {
    await click('Alles neu berechnen')
    await click('Abbrechen')

    expect(adminService.rescoreEverything).not.toHaveBeenCalled()
    expect(buttonByText('Bestätigen')).toBeFalsy()
    expect(buttonByText('Alles neu berechnen')).toBeTruthy()
  })

  it('should call the API once when confirmed', async () => {
    await click('Alles neu berechnen')
    await click('Bestätigen')

    expect(adminService.rescoreEverything).toHaveBeenCalledTimes(1)
  })

  it('should list each changed total with old and new value', async () => {
    await click('Alles neu berechnen')
    await click('Bestätigen')

    const rows = Array.from(element.querySelectorAll('tbody tr')).map(r => r.textContent?.replace(/\s+/g, ' ').trim())
    expect(rows).toHaveLength(2)
    expect(rows[0]).toContain('hardy')
    expect(rows[0]).toContain('12')
    expect(rows[0]).toContain('9')
    expect(rows[1]).toContain('lena')
    expect(element.textContent).not.toContain('Keine Änderungen')
  })

  it('should say that nothing changed when no total changed', async () => {
    adminService.rescoreEverything.mockReturnValue(of([]))

    await click('Alles neu berechnen')
    await click('Bestätigen')

    expect(element.textContent).toContain('Keine Änderungen')
    expect(element.querySelectorAll('tbody tr')).toHaveLength(0)
  })

  it('should not allow a second run while the first is in progress', async () => {
    adminService.rescoreEverything.mockReturnValue(new Subject<PointsChange[]>())

    await click('Alles neu berechnen')
    await click('Bestätigen')

    expect(buttonByText('Alles neu berechnen')?.disabled).toBe(true)
  })
})
