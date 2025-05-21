import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClub } from '../club.model';
import { ClubService } from '../service/club.service';

@Component({
  templateUrl: './club-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClubDeleteDialogComponent {
  club?: IClub;

  protected clubService = inject(ClubService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clubService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
