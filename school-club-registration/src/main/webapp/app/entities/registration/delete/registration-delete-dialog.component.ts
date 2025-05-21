import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRegistration } from '../registration.model';
import { RegistrationService } from '../service/registration.service';

@Component({
  templateUrl: './registration-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RegistrationDeleteDialogComponent {
  registration?: IRegistration;

  protected registrationService = inject(RegistrationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.registrationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
