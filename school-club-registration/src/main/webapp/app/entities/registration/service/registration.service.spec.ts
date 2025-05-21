import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRegistration } from '../registration.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../registration.test-samples';

import { RegistrationService, RestRegistration } from './registration.service';

const requireRestSample: RestRegistration = {
  ...sampleWithRequiredData,
  registrationDate: sampleWithRequiredData.registrationDate?.toJSON(),
};

describe('Registration Service', () => {
  let service: RegistrationService;
  let httpMock: HttpTestingController;
  let expectedResult: IRegistration | IRegistration[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RegistrationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Registration', () => {
      const registration = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(registration).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Registration', () => {
      const registration = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(registration).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Registration', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Registration', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Registration', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRegistrationToCollectionIfMissing', () => {
      it('should add a Registration to an empty array', () => {
        const registration: IRegistration = sampleWithRequiredData;
        expectedResult = service.addRegistrationToCollectionIfMissing([], registration);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(registration);
      });

      it('should not add a Registration to an array that contains it', () => {
        const registration: IRegistration = sampleWithRequiredData;
        const registrationCollection: IRegistration[] = [
          {
            ...registration,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRegistrationToCollectionIfMissing(registrationCollection, registration);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Registration to an array that doesn't contain it", () => {
        const registration: IRegistration = sampleWithRequiredData;
        const registrationCollection: IRegistration[] = [sampleWithPartialData];
        expectedResult = service.addRegistrationToCollectionIfMissing(registrationCollection, registration);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(registration);
      });

      it('should add only unique Registration to an array', () => {
        const registrationArray: IRegistration[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const registrationCollection: IRegistration[] = [sampleWithRequiredData];
        expectedResult = service.addRegistrationToCollectionIfMissing(registrationCollection, ...registrationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const registration: IRegistration = sampleWithRequiredData;
        const registration2: IRegistration = sampleWithPartialData;
        expectedResult = service.addRegistrationToCollectionIfMissing([], registration, registration2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(registration);
        expect(expectedResult).toContain(registration2);
      });

      it('should accept null and undefined values', () => {
        const registration: IRegistration = sampleWithRequiredData;
        expectedResult = service.addRegistrationToCollectionIfMissing([], null, registration, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(registration);
      });

      it('should return initial array if no Registration is added', () => {
        const registrationCollection: IRegistration[] = [sampleWithRequiredData];
        expectedResult = service.addRegistrationToCollectionIfMissing(registrationCollection, undefined, null);
        expect(expectedResult).toEqual(registrationCollection);
      });
    });

    describe('compareRegistration', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRegistration(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10394 };
        const entity2 = null;

        const compareResult1 = service.compareRegistration(entity1, entity2);
        const compareResult2 = service.compareRegistration(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10394 };
        const entity2 = { id: 14380 };

        const compareResult1 = service.compareRegistration(entity1, entity2);
        const compareResult2 = service.compareRegistration(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10394 };
        const entity2 = { id: 10394 };

        const compareResult1 = service.compareRegistration(entity1, entity2);
        const compareResult2 = service.compareRegistration(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
