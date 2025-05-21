import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClubDetailComponent } from './club-detail.component';

describe('Club Management Detail Component', () => {
  let comp: ClubDetailComponent;
  let fixture: ComponentFixture<ClubDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClubDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./club-detail.component').then(m => m.ClubDetailComponent),
              resolve: { club: () => of({ id: 25440 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClubDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClubDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load club on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClubDetailComponent);

      // THEN
      expect(instance.club()).toEqual(expect.objectContaining({ id: 25440 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
