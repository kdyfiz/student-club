import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { RegistrationStatus } from 'app/entities/enumerations/registration-status.model';
import { RegistrationService } from '../service/registration.service';
import { IRegistration } from '../registration.model';
import { RegistrationFormGroup, RegistrationFormService } from './registration-form.service';

@Component({
  selector: 'jhi-registration-update',
  templateUrl: './registration-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RegistrationUpdateComponent implements OnInit {
  isSaving = false;
  registration: IRegistration | null = null;
  registrationStatusValues = Object.keys(RegistrationStatus);

  usersSharedCollection: IUser[] = [];
  clubsSharedCollection: IClub[] = [];

  protected registrationService = inject(RegistrationService);
  protected registrationFormService = inject(RegistrationFormService);
  protected userService = inject(UserService);
  protected clubService = inject(ClubService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RegistrationFormGroup = this.registrationFormService.createRegistrationFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareClub = (o1: IClub | null, o2: IClub | null): boolean => this.clubService.compareClub(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ registration }) => {
      this.registration = registration;
      if (registration) {
        this.updateForm(registration);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const registration = this.registrationFormService.getRegistration(this.editForm);
    if (registration.id !== null) {
      this.subscribeToSaveResponse(this.registrationService.update(registration));
    } else {
      this.subscribeToSaveResponse(this.registrationService.create(registration));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRegistration>>): void {
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

  protected updateForm(registration: IRegistration): void {
    this.registration = registration;
    this.registrationFormService.resetForm(this.editForm, registration);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, registration.user);
    this.clubsSharedCollection = this.clubService.addClubToCollectionIfMissing<IClub>(this.clubsSharedCollection, registration.club);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.registration?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.clubService
      .query()
      .pipe(map((res: HttpResponse<IClub[]>) => res.body ?? []))
      .pipe(map((clubs: IClub[]) => this.clubService.addClubToCollectionIfMissing<IClub>(clubs, this.registration?.club)))
      .subscribe((clubs: IClub[]) => (this.clubsSharedCollection = clubs));
  }
}
