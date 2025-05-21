import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IClub } from '../club.model';

@Component({
  selector: 'jhi-club-detail',
  templateUrl: './club-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ClubDetailComponent {
  club = input<IClub | null>(null);

  previousState(): void {
    window.history.back();
  }
}
