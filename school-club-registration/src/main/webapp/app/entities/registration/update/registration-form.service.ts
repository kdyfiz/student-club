import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRegistration, NewRegistration } from '../registration.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRegistration for edit and NewRegistrationFormGroupInput for create.
 */
type RegistrationFormGroupInput = IRegistration | PartialWithRequiredKeyOf<NewRegistration>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRegistration | NewRegistration> = Omit<T, 'registrationDate'> & {
  registrationDate?: string | null;
};

type RegistrationFormRawValue = FormValueOf<IRegistration>;

type NewRegistrationFormRawValue = FormValueOf<NewRegistration>;

type RegistrationFormDefaults = Pick<NewRegistration, 'id' | 'registrationDate'>;

type RegistrationFormGroupContent = {
  id: FormControl<RegistrationFormRawValue['id'] | NewRegistration['id']>;
  registrationDate: FormControl<RegistrationFormRawValue['registrationDate']>;
  status: FormControl<RegistrationFormRawValue['status']>;
  user: FormControl<RegistrationFormRawValue['user']>;
  club: FormControl<RegistrationFormRawValue['club']>;
};

export type RegistrationFormGroup = FormGroup<RegistrationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RegistrationFormService {
  createRegistrationFormGroup(registration: RegistrationFormGroupInput = { id: null }): RegistrationFormGroup {
    const registrationRawValue = this.convertRegistrationToRegistrationRawValue({
      ...this.getFormDefaults(),
      ...registration,
    });
    return new FormGroup<RegistrationFormGroupContent>({
      id: new FormControl(
        { value: registrationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      registrationDate: new FormControl(registrationRawValue.registrationDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(registrationRawValue.status, {
        validators: [Validators.required],
      }),
      user: new FormControl(registrationRawValue.user, {
        validators: [Validators.required],
      }),
      club: new FormControl(registrationRawValue.club, {
        validators: [Validators.required],
      }),
    });
  }

  getRegistration(form: RegistrationFormGroup): IRegistration | NewRegistration {
    return this.convertRegistrationRawValueToRegistration(form.getRawValue() as RegistrationFormRawValue | NewRegistrationFormRawValue);
  }

  resetForm(form: RegistrationFormGroup, registration: RegistrationFormGroupInput): void {
    const registrationRawValue = this.convertRegistrationToRegistrationRawValue({ ...this.getFormDefaults(), ...registration });
    form.reset(
      {
        ...registrationRawValue,
        id: { value: registrationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RegistrationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      registrationDate: currentTime,
    };
  }

  private convertRegistrationRawValueToRegistration(
    rawRegistration: RegistrationFormRawValue | NewRegistrationFormRawValue,
  ): IRegistration | NewRegistration {
    return {
      ...rawRegistration,
      registrationDate: dayjs(rawRegistration.registrationDate, DATE_TIME_FORMAT),
    };
  }

  private convertRegistrationToRegistrationRawValue(
    registration: IRegistration | (Partial<NewRegistration> & RegistrationFormDefaults),
  ): RegistrationFormRawValue | PartialWithRequiredKeyOf<NewRegistrationFormRawValue> {
    return {
      ...registration,
      registrationDate: registration.registrationDate ? registration.registrationDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
