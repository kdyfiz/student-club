import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../registration.test-samples';

import { RegistrationFormService } from './registration-form.service';

describe('Registration Form Service', () => {
  let service: RegistrationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegistrationFormService);
  });

  describe('Service methods', () => {
    describe('createRegistrationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRegistrationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            registrationDate: expect.any(Object),
            status: expect.any(Object),
            user: expect.any(Object),
            club: expect.any(Object),
          }),
        );
      });

      it('passing IRegistration should create a new form with FormGroup', () => {
        const formGroup = service.createRegistrationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            registrationDate: expect.any(Object),
            status: expect.any(Object),
            user: expect.any(Object),
            club: expect.any(Object),
          }),
        );
      });
    });

    describe('getRegistration', () => {
      it('should return NewRegistration for default Registration initial value', () => {
        const formGroup = service.createRegistrationFormGroup(sampleWithNewData);

        const registration = service.getRegistration(formGroup) as any;

        expect(registration).toMatchObject(sampleWithNewData);
      });

      it('should return NewRegistration for empty Registration initial value', () => {
        const formGroup = service.createRegistrationFormGroup();

        const registration = service.getRegistration(formGroup) as any;

        expect(registration).toMatchObject({});
      });

      it('should return IRegistration', () => {
        const formGroup = service.createRegistrationFormGroup(sampleWithRequiredData);

        const registration = service.getRegistration(formGroup) as any;

        expect(registration).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRegistration should not enable id FormControl', () => {
        const formGroup = service.createRegistrationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRegistration should disable id FormControl', () => {
        const formGroup = service.createRegistrationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
