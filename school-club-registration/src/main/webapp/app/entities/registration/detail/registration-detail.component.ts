import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IRegistration } from '../registration.model';

@Component({
  selector: 'jhi-registration-detail',
  templateUrl: './registration-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class RegistrationDetailComponent {
  registration = input<IRegistration | null>(null);

  previousState(): void {
    window.history.back();
  }
}
