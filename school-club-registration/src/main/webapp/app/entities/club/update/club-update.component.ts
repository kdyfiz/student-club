import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClub } from '../club.model';
import { ClubService } from '../service/club.service';
import { ClubFormGroup, ClubFormService } from './club-form.service';

@Component({
  selector: 'jhi-club-update',
  templateUrl: './club-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClubUpdateComponent implements OnInit {
  isSaving = false;
  club: IClub | null = null;

  protected clubService = inject(ClubService);
  protected clubFormService = inject(ClubFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClubFormGroup = this.clubFormService.createClubFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ club }) => {
      this.club = club;
      if (club) {
        this.updateForm(club);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const club = this.clubFormService.getClub(this.editForm);
    if (club.id !== null) {
      this.subscribeToSaveResponse(this.clubService.update(club));
    } else {
      this.subscribeToSaveResponse(this.clubService.create(club));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClub>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(club: IClub): void {
    this.club = club;
    this.clubFormService.resetForm(this.editForm, club);
  }
}
