import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IClub } from '../club.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../club.test-samples';

import { ClubService } from './club.service';

const requireRestSample: IClub = {
  ...sampleWithRequiredData,
};

describe('Club Service', () => {
  let service: ClubService;
  let httpMock: HttpTestingController;
  let expectedResult: IClub | IClub[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ClubService);
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

    it('should create a Club', () => {
      const club = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(club).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Club', () => {
      const club = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(club).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Club', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Club', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Club', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClubToCollectionIfMissing', () => {
      it('should add a Club to an empty array', () => {
        const club: IClub = sampleWithRequiredData;
        expectedResult = service.addClubToCollectionIfMissing([], club);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(club);
      });

      it('should not add a Club to an array that contains it', () => {
        const club: IClub = sampleWithRequiredData;
        const clubCollection: IClub[] = [
          {
            ...club,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, club);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Club to an array that doesn't contain it", () => {
        const club: IClub = sampleWithRequiredData;
        const clubCollection: IClub[] = [sampleWithPartialData];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, club);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(club);
      });

      it('should add only unique Club to an array', () => {
        const clubArray: IClub[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clubCollection: IClub[] = [sampleWithRequiredData];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, ...clubArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const club: IClub = sampleWithRequiredData;
        const club2: IClub = sampleWithPartialData;
        expectedResult = service.addClubToCollectionIfMissing([], club, club2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(club);
        expect(expectedResult).toContain(club2);
      });

      it('should accept null and undefined values', () => {
        const club: IClub = sampleWithRequiredData;
        expectedResult = service.addClubToCollectionIfMissing([], null, club, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(club);
      });

      it('should return initial array if no Club is added', () => {
        const clubCollection: IClub[] = [sampleWithRequiredData];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, undefined, null);
        expect(expectedResult).toEqual(clubCollection);
      });
    });

    describe('compareClub', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClub(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25440 };
        const entity2 = null;

        const compareResult1 = service.compareClub(entity1, entity2);
        const compareResult2 = service.compareClub(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25440 };
        const entity2 = { id: 10245 };

        const compareResult1 = service.compareClub(entity1, entity2);
        const compareResult2 = service.compareClub(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25440 };
        const entity2 = { id: 25440 };

        const compareResult1 = service.compareClub(entity1, entity2);
        const compareResult2 = service.compareClub(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
